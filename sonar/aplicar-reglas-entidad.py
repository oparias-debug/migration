"""Aplica sonar/reglas-entidad.json al SonarQube local y verifica que quedó aplicado.

Lo corre el servicio sonarqube-init de docker-compose.yml (python:alpine, solo stdlib).
Es idempotente: se puede correr cada vez que se levanta Sonar o se edita el JSON.

  1. Por cada perfil (el principal y los de 'perfiles_adicionales', uno por lenguaje): crea el perfil (si no existe), lo hace heredar de 'hereda_de', activa cada regla con
     sus parámetros, desactiva las reglas propias del perfil que ya no están en el JSON y
     lo deja como perfil por defecto del lenguaje.
  2. Crea el quality gate (si no existe), reemplaza sus condiciones por las del JSON y lo
     deja como gate por defecto.
  3. Verifica regla por regla (y condición por condición) contra lo que Sonar devuelve;
     termina con código 1 si algo no coincide.
"""
import base64
import json
import os
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

URL = os.environ.get("SONAR_URL", "http://sonarqube:9000").rstrip("/")
CONFIG = os.environ.get("REGLAS_ENTIDAD", os.path.join(os.path.dirname(__file__), "reglas-entidad.json"))

if os.environ.get("SONAR_ADMIN_TOKEN"):
    CREDENCIALES = os.environ["SONAR_ADMIN_TOKEN"] + ":"
elif os.environ.get("SONAR_ADMIN_PASSWORD"):
    CREDENCIALES = os.environ.get("SONAR_ADMIN_USER", "admin") + ":" + os.environ["SONAR_ADMIN_PASSWORD"]
else:
    sys.exit("Falta SONAR_ADMIN_TOKEN o SONAR_ADMIN_PASSWORD (ver .env).")
AUTH = "Basic " + base64.b64encode(CREDENCIALES.encode()).decode()


def api(ruta, datos=None, get=False):
    cuerpo = urllib.parse.urlencode(datos or {}, doseq=True)
    if get:
        req = urllib.request.Request(f"{URL}{ruta}?{cuerpo}")
    else:
        req = urllib.request.Request(f"{URL}{ruta}", data=cuerpo.encode(), method="POST")
    req.add_header("Authorization", AUTH)
    try:
        with urllib.request.urlopen(req, timeout=60) as resp:
            texto = resp.read().decode()
            return json.loads(texto) if texto else {}
    except urllib.error.HTTPError as e:
        raise SystemExit(f"{ruta} -> HTTP {e.code}: {e.read().decode()[:500]}")


def esperar_sonar(max_segundos=600):
    inicio = time.time()
    while time.time() - inicio < max_segundos:
        try:
            with urllib.request.urlopen(f"{URL}/api/system/status", timeout=10) as resp:
                estado = json.loads(resp.read().decode()).get("status")
                if estado == "UP":
                    return
                print(f"Sonar en estado {estado}, esperando...", flush=True)
        except (urllib.error.URLError, OSError):
            print("Sonar todavía no responde, esperando...", flush=True)
        time.sleep(10)
    sys.exit("Sonar no llegó a estado UP a tiempo.")


def buscar_perfil(nombre, lenguaje):
    perfiles = api("/api/qualityprofiles/search", {"language": lenguaje}, get=True)["profiles"]
    return next((p for p in perfiles if p["name"] == nombre), None)


def reglas_propias(clave_perfil):
    """Reglas activadas directamente en el perfil (no heredadas del padre)."""
    reglas, pagina = [], 1
    while True:
        r = api("/api/rules/search", {"qprofile": clave_perfil, "activation": "true",
                                      "inheritance": "NONE", "ps": 500, "p": pagina, "f": "name"}, get=True)
        reglas += [x["key"] for x in r["rules"]]
        if pagina * 500 >= r["total"]:
            return reglas
        pagina += 1


def aplicar_perfil(cfg):
    nombre, lenguaje = cfg["nombre"], cfg["lenguaje"]
    perfil = buscar_perfil(nombre, lenguaje)
    if not perfil:
        print(f"Creando perfil '{nombre}'")
        api("/api/qualityprofiles/create", {"name": nombre, "language": lenguaje})
        perfil = buscar_perfil(nombre, lenguaje)
    if cfg.get("hereda_de"):
        api("/api/qualityprofiles/change_parent", {"language": lenguaje, "qualityProfile": nombre,
                                                   "parentQualityProfile": cfg["hereda_de"]})
    deseadas = {r["regla"] for r in cfg["reglas"]}
    for regla in cfg["reglas"]:
        params = ";".join(f"{k}={v}" for k, v in regla.get("params", {}).items())
        datos = {"key": perfil["key"], "rule": regla["regla"]}
        if params:
            datos["params"] = params
        api("/api/qualityprofiles/activate_rule", datos)
    for sobrante in set(reglas_propias(perfil["key"])) - deseadas:
        print(f"Desactivando {sobrante} (ya no está en el JSON)")
        api("/api/qualityprofiles/deactivate_rule", {"key": perfil["key"], "rule": sobrante})
    api("/api/qualityprofiles/set_default", {"language": lenguaje, "qualityProfile": nombre})
    return perfil["key"]


def aplicar_gate(cfg):
    nombre = cfg["nombre"]
    existentes = [g["name"] for g in api("/api/qualitygates/list", get=True)["qualitygates"]]
    if nombre not in existentes:
        print(f"Creando quality gate '{nombre}'")
        api("/api/qualitygates/create", {"name": nombre})
    for c in api("/api/qualitygates/show", {"name": nombre}, get=True).get("conditions", []):
        api("/api/qualitygates/delete_condition", {"id": c["id"]})
    for c in cfg["condiciones"]:
        api("/api/qualitygates/create_condition", {"gateName": nombre, "metric": c["metric"],
                                                   "op": c["op"], "error": c["error"]})
    api("/api/qualitygates/set_as_default", {"name": nombre})


def verificar_perfil(perfil_cfg, clave_perfil):
    errores = []
    print(f"\nVerificación del perfil '{perfil_cfg['nombre']}' ({perfil_cfg['lenguaje']}):")
    for regla in perfil_cfg["reglas"]:
        r = api("/api/rules/show", {"key": regla["regla"], "actives": "true"}, get=True)
        activa = next((a for a in r.get("actives", []) if a["qProfile"] == clave_perfil), None)
        if not activa:
            errores.append(f"{regla['regla']} no quedó activa")
            print(f"  FALTA  {regla['regla']}")
            continue
        actuales = {p["key"]: p["value"] for p in activa.get("params", [])}
        distintos = {k: (v, actuales.get(k)) for k, v in regla.get("params", {}).items() if actuales.get(k) != v}
        if distintos:
            errores.append(f"{regla['regla']} con parámetros distintos: {distintos}")
            print(f"  PARAM  {regla['regla']} {distintos}")
        else:
            print(f"  OK     {regla['regla']} {actuales or ''}")
    perfil = buscar_perfil(perfil_cfg["nombre"], perfil_cfg["lenguaje"])
    if not perfil or not perfil["isDefault"]:
        errores.append(f"el perfil {perfil_cfg['lenguaje']} no quedó como default")
    else:
        print(f"  Perfil '{perfil['name']}' ({perfil_cfg['lenguaje']}): "
              f"{perfil['activeRuleCount']} reglas activas (heredadas + propias).")
    return errores


def verificar_gate(gate_cfg):
    errores = []
    gate = api("/api/qualitygates/show", {"name": gate_cfg["nombre"]}, get=True)
    actuales = {(c["metric"], c["op"], c["error"]) for c in gate.get("conditions", [])}
    print("\nVerificación del quality gate:")
    for c in gate_cfg["condiciones"]:
        ok = (c["metric"], c["op"], c["error"]) in actuales
        print(f"  {'OK   ' if ok else 'FALTA'}  {c['metric']} {c['op']} {c['error']}")
        if not ok:
            errores.append(f"condición {c['metric']} {c['op']} {c['error']} no quedó en el gate")
    if not next((g for g in api("/api/qualitygates/list", get=True)["qualitygates"]
                 if g["name"] == gate_cfg["nombre"]), {}).get("isDefault"):
        errores.append("el quality gate no quedó como default")
    return errores


def main():
    with open(CONFIG, encoding="utf-8") as fh:
        cfg = json.load(fh)
    esperar_sonar()
    errores = []
    for perfil_cfg in [cfg["perfil"]] + cfg.get("perfiles_adicionales", []):
        clave = aplicar_perfil(perfil_cfg)
        errores += verificar_perfil(perfil_cfg, clave)
    aplicar_gate(cfg["quality_gate"])
    errores += verificar_gate(cfg["quality_gate"])
    pendientes = cfg.get("no_disponibles_en_community", {}).get("reglas", [])
    if pendientes:
        print(f"\n{len(pendientes)} reglas de la entidad no se pueden aplicar en Sonar Community "
              "(ver 'no_disponibles_en_community' en el JSON):")
        for r in pendientes:
            print(f"  - [{r['lenguaje']}] {r['nota']}")
    if errores:
        print("\nNO coincide con reglas-entidad.json:")
        for e in errores:
            print("  - " + e)
        sys.exit(1)
    print("Perfil y quality gate de la entidad aplicados y verificados.")


if __name__ == "__main__":
    main()

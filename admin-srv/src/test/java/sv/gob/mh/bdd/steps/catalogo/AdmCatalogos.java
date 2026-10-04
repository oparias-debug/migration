package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CatalogoFixtures;
import sv.gob.mh.bdd.support.CatalogoFixtures.Campo;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;
import sv.gob.mh.bdd.support.TipoBdd;
import sv.gob.mh.domain.model.catalogo.Vigencia;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Steps de los catálogos de CU-ADM-01: crear (HU-ADM-01-01), definir campos (02), consultar (03),
 * buscar y listar (04), actualizar descriptores (05), jerarquía (06), inactivar (07) y
 * reactivar (08).
 */
public class AdmCatalogos {

    private static final String CATALOGOS = "/catalogos";
    private static final String CATALOGO = "/catalogos/{codigo}";
    private static final String CAMPOS = "/catalogos/{codigo}/campos";
    private static final String ESTADO_CATALOGO = "/catalogos/{codigo}/estado";
    private static final String REGISTROS = "/catalogos/{codigo}/registros";
    private static final String CODIGO = "codigo";
    private static final String NOMBRE = "nombre";
    private static final String PADRE = "padre";
    private static final String ESTADO = "estado";
    private static final String VIGENCIA = "vigencia";
    private static final String HASTA = "hasta";
    private static final String VALORES = "valores";
    private static final String CONFIRMAR_CAMBIO_PADRE = "confirmarCambioPadre";
    private static final Pattern DESCRIPTOR_NOMBRE = Pattern.compile("nombre \"([^\"]*)\"");
    private static final Pattern DESCRIPTOR_PADRE = Pattern.compile("padre \"([^\"]*)\"");
    private static final Pattern DESCRIPTOR_ESTADO = Pattern.compile("estado \"([^\"]*)\"");
    private static final Pattern DESCRIPTOR_VIGENCIA = Pattern.compile("vigencia \"([^\"]*)\"");
    private static final Pattern CODIGO_ENTRE_COMILLAS = Pattern.compile("\"([^\"]*)\"");

    private final CatalogoFixtures fixtures;
    private final ContextoCatalogoBdd contexto;

    public AdmCatalogos(CatalogoFixtures fixtures, ContextoCatalogoBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    private static List<String> codigos(String texto) {
        List<String> codigos = new ArrayList<>();
        Matcher codigo = CODIGO_ENTRE_COMILLAS.matcher(texto);
        while (codigo.find()) {
            codigos.add(codigo.group(1));
        }
        return codigos;
    }

    private static LocalDate fecha(String descripcion) {
        return switch (descripcion) {
            case "la fecha actual" -> Vigencia.hoy();
            case "una fecha pasada" -> Vigencia.hoy().minusDays(10);
            case "una fecha futura" -> Vigencia.hoy().plusDays(30);
            default -> throw new IllegalArgumentException(descripcion);
        };
    }

    // ---------- HU-ADM-01-01: crear ----------

    /** {@code CatalogoCreacion} con los descriptores que indique el texto del paso; el estado es obligatorio. */
    private Map<String, Object> solicitudCreacion(String codigo, String descriptores, List<Map<String, Object>> campos) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put(CODIGO, codigo);
        String nombre = AdmComun.texto(DESCRIPTOR_NOMBRE, descriptores);
        cuerpo.put(NOMBRE, nombre != null ? nombre : "Catálogo " + codigo);
        String padre = AdmComun.texto(DESCRIPTOR_PADRE, descriptores);
        if (padre != null) {
            cuerpo.put(PADRE, padre);
        }
        String estado = AdmComun.texto(DESCRIPTOR_ESTADO, descriptores);
        cuerpo.put(ESTADO, estado != null ? estado : EstadoVigencia.ACTIVE.name());
        String vigencia = AdmComun.texto(DESCRIPTOR_VIGENCIA, descriptores);
        if (vigencia != null) {
            LocalDate[] rango = TablasCatalogoBdd.vigencia(vigencia);
            cuerpo.put(VIGENCIA, TablasCatalogoBdd.vigenciaSolicitud(rango[0], rango[1]));
        }
        cuerpo.put("campos", campos);
        return cuerpo;
    }

    private void crear(String codigo, String descriptores, List<Map<String, Object>> campos) {
        contexto.post(CATALOGOS, solicitudCreacion(codigo, descriptores, campos));
    }

    @Cuando("^(?:creo|intento crear) el catálogo \"([^\"]*)\"(.*) y campos válidos$")
    public void creo_el_catalogo_con_campos_validos(String codigo, String descriptores) {
        crear(codigo, descriptores, TablasCatalogoBdd.camposSolicitud(CatalogoFixtures.camposPorDefecto(codigo)));
    }

    @Cuando("^intento crear el catálogo \"([^\"]*)\" con campos válidos$")
    public void intento_crear_el_catalogo_con_campos_validos(String codigo) {
        creo_el_catalogo_con_campos_validos(codigo, "");
    }

    @Cuando("^(?:creo|intento crear) el catálogo \"([^\"]*)\"(.*) y los campos:$")
    public void creo_el_catalogo_con_los_campos(String codigo, String descriptores, DataTable campos) {
        crear(codigo, descriptores, TablasCatalogoBdd.camposSolicitud(campos));
    }

    @Cuando("^intento crear el catálogo \"([^\"]*)\" con los campos:$")
    public void intento_crear_el_catalogo_con_los_campos(String codigo, DataTable campos) {
        crear(codigo, "", TablasCatalogoBdd.camposSolicitud(campos));
    }

    @Cuando("^intento crear el catálogo \"([^\"]*)\"(.*) sin indicar campos$")
    public void intento_crear_el_catalogo_sin_campos(String codigo, String descriptores) {
        crear(codigo, descriptores, List.of());
    }

    @Cuando("^intento crear otro catálogo con código \"([^\"]*)\"$")
    public void intento_crear_otro_catalogo(String codigo) {
        creo_el_catalogo_con_campos_validos(codigo, "");
    }

    @Entonces("^el catálogo \"([^\"]*)\" queda registrado en el catalogMaster$")
    public void el_catalogo_queda_registrado(String codigo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(201);
        assertThat(contexto.getUltimoCuerpo().path(CODIGO).asText()).isEqualTo(codigo);
        assertThat(fixtures.existeCatalogo(codigo)).isTrue();
    }

    @Entonces("^el catálogo \"([^\"]*)\" no tiene catálogo padre$")
    public void el_catalogo_no_tiene_padre(String codigo) {
        assertThat(fixtures.codigoPadre(codigo)).isNull();
    }

    @Entonces("^el campo \"([^\"]*)\" del catálogo \"([^\"]*)\" queda definido con tipo \"(.*)\"$")
    public void el_campo_queda_definido_con_tipo(String nombreCampo, String codigo, String tipo) {
        Campo campo = fixtures.campos(codigo).stream().filter(c -> c.nombre().equals(nombreCampo)).findFirst()
                .orElseThrow();
        assertThat(campo.tipoBdd()).isEqualTo(TipoBdd.de(tipo));
    }

    @Entonces("^(?:el catálogo )?\"([^\"]*)\" (?:referencia|sigue referenciando) a \"([^\"]*)\" como padre$")
    public void referencia_como_padre(String hijo, String padre) {
        assertThat(fixtures.codigoPadre(hijo)).isEqualTo(padre);
    }

    /** RN-05: el catálogo padre conoce a su hijo; se verifica en la definición que expone la API. */
    @Entonces("^(?:el catálogo )?\"([^\"]*)\" registra a \"([^\"]*)\" como su hijo$")
    public void registra_como_su_hijo(String padre, String hijo) {
        contexto.get(CATALOGO, padre);
        assertThat(contexto.getUltimoCuerpo().path("hijo").asText()).isEqualTo(hijo);
    }

    @Entonces("^los registros que se creen en \"([^\"]*)\" deberán indicar el id de un registro padre de \"([^\"]*)\"$")
    public void los_registros_deberan_indicar_registro_padre(String hijo, String padre) {
        contexto.post(REGISTROS, Map.of(VALORES, fixtures.valoresValidos(hijo, "R01", Map.of())), hijo);
        assertThat(contexto.getUltimoCuerpo().path(CODIGO).asText()).isEqualTo("E-18");
        assertThat(contexto.getUltimoCuerpo().path("mensaje").asText()).contains("catálogo " + padre);
    }

    // ---------- HU-ADM-01-02: campos ----------

    private void tomarInstantaneaDeCampos(String codigo) {
        contexto.setInstantanea(fixtures.existeCatalogo(codigo) ? fixtures.campos(codigo) : List.of());
    }

    /** El cuerpo de SF-04 paso 5 es la lista de {@code CampoDefinicion}. */
    private void reemplazarCampos(String codigo, List<Map<String, Object>> campos) {
        tomarInstantaneaDeCampos(codigo);
        contexto.put(CAMPOS, campos, codigo);
    }

    @Cuando("^(?:actualizo|intento actualizar) los campos del catálogo \"([^\"]*)\" a:$")
    public void actualizo_los_campos(String codigo, DataTable campos) {
        reemplazarCampos(codigo, TablasCatalogoBdd.camposSolicitud(campos));
    }

    @Cuando("^intento actualizar los campos del catálogo \"([^\"]*)\" a una lista vacía$")
    public void intento_actualizar_los_campos_a_una_lista_vacia(String codigo) {
        reemplazarCampos(codigo, List.of());
    }

    @Cuando("^intento actualizar los campos del catálogo \"([^\"]*)\"$")
    public void intento_actualizar_los_campos(String codigo) {
        reemplazarCampos(codigo, TablasCatalogoBdd.camposSolicitud(CatalogoFixtures.camposPorDefecto(codigo)));
    }

    @Cuando("^cambio el campo \"([^\"]*)\" del catálogo \"([^\"]*)\" a \"(.*)\"$")
    public void cambio_el_tipo_del_campo(String nombreCampo, String codigo, String tipo) {
        List<Campo> campos = fixtures.campos(codigo).stream()
                .map(campo -> campo.nombre().equals(nombreCampo)
                        ? new Campo(campo.nombre(), campo.esKey(), tipo, campo.posicion())
                        : campo)
                .toList();
        reemplazarCampos(codigo, TablasCatalogoBdd.camposSolicitud(campos));
    }

    @Cuando("^intento definir en el catálogo \"([^\"]*)\" el campo FIELD \"([^\"]*)\" con tipo \"(.*)\"$")
    public void intento_definir_un_campo(String codigo, String nombreCampo, String tipo) {
        List<Map<String, Object>> campos = new ArrayList<>(TablasCatalogoBdd.camposSolicitud(fixtures.campos(codigo)));
        campos.add(TipoBdd.solicitud(nombreCampo, "FIELD", campos.size() + 1, tipo));
        reemplazarCampos(codigo, campos);
    }

    @Cuando("^intento (agregar un campo|modificar un campo existente|quitar un campo) en el catálogo \"([^\"]*)\"$")
    public void intento_cambiar_los_campos(String operacion, String codigo) {
        List<Campo> campos = new ArrayList<>(fixtures.campos(codigo));
        switch (operacion) {
            case "agregar un campo" -> campos.add(new Campo("nuevo", false, "STRING{20}", campos.size() + 1));
            case "modificar un campo existente" -> {
                Campo ultimo = campos.remove(campos.size() - 1);
                campos.add(new Campo(ultimo.nombre(), ultimo.esKey(), "STRING{99}", ultimo.posicion()));
            }
            default -> campos.remove(campos.size() - 1);
        }
        reemplazarCampos(codigo, TablasCatalogoBdd.camposSolicitud(campos));
    }

    @Entonces("^la definición del campo \"([^\"]*)\" del catálogo \"([^\"]*)\" queda como \"(.*)\"$")
    public void la_definicion_del_campo_queda_como(String nombreCampo, String codigo, String tipo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        el_campo_queda_definido_con_tipo(nombreCampo, codigo, tipo);
    }

    @Entonces("^al consultar el catálogo \"([^\"]*)\" el campo \"([^\"]*)\" se muestra con tipo \"(.*)\"$")
    public void al_consultar_el_campo_se_muestra_con_tipo(String codigo, String nombreCampo, String tipo) {
        contexto.get(CATALOGO, codigo);
        JsonNode campo = null;
        for (JsonNode candidato : contexto.getUltimoCuerpo().path("campos")) {
            if (candidato.path(NOMBRE).asText().equals(nombreCampo)) {
                campo = candidato;
            }
        }
        assertThat(campo).as("campo %s", nombreCampo).isNotNull();
        assertThat(TipoBdd.de(campo)).isEqualTo(TipoBdd.de(tipo));
    }

    @Entonces("^el catálogo \"([^\"]*)\" queda con los campos ordenados por posición:$")
    public void queda_con_los_campos_ordenados(String codigo, DataTable nombres) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(fixtures.campos(codigo)).extracting(Campo::nombre).containsExactlyElementsOf(nombres.asList()
                .subList(1, nombres.asList().size()));
    }

    @Entonces("^los campos del catálogo \"([^\"]*)\" no cambian$")
    public void los_campos_no_cambian(String codigo) {
        assertThat(fixtures.campos(codigo)).isEqualTo(contexto.getInstantanea());
    }

    // ---------- HU-ADM-01-03: consultar ----------

    @Cuando("^consulto el catálogo con código \"([^\"]*)\"$")
    public void consulto_el_catalogo(String codigo) {
        contexto.get(CATALOGO, codigo);
    }

    @Entonces("^obtengo los descriptores del catálogo:$")
    public void obtengo_los_descriptores(DataTable descriptores) {
        Map<String, String> esperado = descriptores.asMaps().get(0);
        JsonNode catalogo = contexto.getUltimoCuerpo();
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(catalogo.path(CODIGO).asText()).isEqualTo(esperado.get(CODIGO));
        assertThat(catalogo.path(NOMBRE).asText()).isEqualTo(esperado.get(NOMBRE));
        assertThat(textoONulo(catalogo, PADRE)).isEqualTo(TablasCatalogoBdd.celda(esperado, PADRE));
        assertThat(textoONulo(catalogo, "hijo")).isEqualTo(TablasCatalogoBdd.celda(esperado, "hijo"));
        assertThat(catalogo.path(ESTADO).asText()).isEqualTo(esperado.get(ESTADO));
        assertThat(textoONulo(catalogo.path(VIGENCIA), "desde")).isEqualTo(TablasCatalogoBdd.celda(esperado, "desde"));
        assertThat(textoONulo(catalogo.path(VIGENCIA), HASTA)).isEqualTo(TablasCatalogoBdd.celda(esperado, HASTA));
    }

    private static String textoONulo(JsonNode nodo, String propiedad) {
        return nodo.hasNonNull(propiedad) ? nodo.get(propiedad).asText() : null;
    }

    @Entonces("^obtengo los campos ordenados por posición:$")
    public void obtengo_los_campos_ordenados(DataTable campos) {
        List<Campo> esperados = TablasCatalogoBdd.campos(campos);
        JsonNode obtenidos = contexto.getUltimoCuerpo().path("campos");
        assertThat(obtenidos.size()).isEqualTo(esperados.size());
        for (int i = 0; i < esperados.size(); i++) {
            JsonNode campo = obtenidos.get(i);
            Campo esperado = esperados.get(i);
            assertThat(campo.path(NOMBRE).asText()).isEqualTo(esperado.nombre());
            assertThat(campo.path("calificador").asText()).isEqualTo(esperado.calificador());
            assertThat(campo.path("posicion").asInt()).isEqualTo(esperado.posicion());
            assertThat(TipoBdd.de(campo)).isEqualTo(esperado.tipoBdd());
        }
    }

    @Entonces("^obtengo el catálogo \"([^\"]*)\" con estado \"([^\"]*)\"$")
    public void obtengo_el_catalogo_con_estado(String codigo, String estado) {
        assertThat(contexto.getUltimoCuerpo().path(CODIGO).asText()).isEqualTo(codigo);
        assertThat(contexto.getUltimoCuerpo().path(ESTADO).asText()).isEqualTo(estado);
    }

    // ---------- HU-ADM-01-04: buscar y listar ----------

    @Dado("^que el catalogMaster contiene los catálogos:$")
    public void que_el_catalog_master_contiene(DataTable catalogos) {
        catalogos.asMaps().forEach(fila -> fixtures.crearCatalogo(fila.get(CODIGO), fila.get(NOMBRE), null,
                EstadoVigencia.valueOf(fila.get(ESTADO)), null, null,
                CatalogoFixtures.camposPorDefecto(fila.get(CODIGO))));
    }

    @Cuando("^busco un catálogo con (código|nombre) \"([^\"]*)\"$")
    public void busco_un_catalogo(String criterio, String valor) {
        contexto.getConParametro(CATALOGOS, "código".equals(criterio) ? CODIGO : NOMBRE, valor);
    }

    @Cuando("^solicito el listado de catálogos$")
    public void solicito_el_listado_de_catalogos() {
        contexto.get(CATALOGOS);
    }

    @Entonces("^el sistema indica que el catálogo existe$")
    public void el_catalogo_existe() {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().size()).isPositive();
    }

    @Entonces("^el sistema indica que no existe un catálogo con ese (?:código|nombre)$")
    public void no_existe_un_catalogo() {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().size()).isZero();
    }

    @Entonces("^muestra el catálogo \"([^\"]*)\"(?: con estado \"([^\"]*)\")?$")
    public void muestra_el_catalogo(String codigo, String estado) {
        JsonNode catalogo = contexto.getUltimoCuerpo().get(0);
        assertThat(catalogo.path(CODIGO).asText()).isEqualTo(codigo);
        if (estado != null) {
            assertThat(catalogo.path(ESTADO).asText()).isEqualTo(estado);
        }
    }

    @Entonces("^obtengo todos los catálogos del catalogMaster:$")
    public void obtengo_todos_los_catalogos(DataTable catalogos) {
        List<Map<String, String>> obtenidos = new ArrayList<>();
        contexto.getUltimoCuerpo().forEach(catalogo -> obtenidos.add(Map.of(CODIGO, catalogo.path(CODIGO).asText(),
                NOMBRE, catalogo.path(NOMBRE).asText(), ESTADO, catalogo.path(ESTADO).asText())));
        assertThat(obtenidos).isEqualTo(catalogos.asMaps());
    }

    // ---------- HU-ADM-01-05 y 06: descriptores y jerarquía ----------

    /**
     * {@code PUT /catalogos/{codigo}} reemplaza los descriptores: el cuerpo lleva los actuales del
     * catálogo (o unos por defecto si no existe) con los {@code cambios} del paso.
     */
    private void actualizarDescriptores(String codigo, Map<String, Object> cambios) {
        Map<String, Object> cuerpo = new HashMap<>();
        boolean existe = fixtures.existeCatalogo(codigo);
        LocalDate desde = existe ? fixtures.fechaDesdeCatalogo(codigo) : null;
        LocalDate hasta = existe ? fixtures.fechaHastaCatalogo(codigo) : null;
        contexto.setInstantanea(new LocalDate[] { desde, hasta });
        cuerpo.put(NOMBRE, existe ? fixtures.catalogoNombre(codigo) : "Catálogo " + codigo);
        cuerpo.put(PADRE, existe ? fixtures.codigoPadre(codigo) : null);
        cuerpo.put(ESTADO, (existe ? fixtures.estadoCatalogo(codigo) : EstadoVigencia.ACTIVE).name());
        cuerpo.put(VIGENCIA, TablasCatalogoBdd.vigenciaSolicitud(desde, hasta));
        cuerpo.putAll(cambios);
        contexto.put(CATALOGO, cuerpo, codigo);
    }

    @Cuando("^(?:cambio|intento cambiar) el nombre del catálogo \"([^\"]*)\" a \"([^\"]*)\"$")
    public void cambio_el_nombre(String codigo, String nombre) {
        actualizarDescriptores(codigo, Map.of(NOMBRE, nombre));
    }

    @Cuando("^(?:cambio|intento cambiar) la vigencia del catálogo \"([^\"]*)\" a \"([^\"]*)\"$")
    public void cambio_la_vigencia(String codigo, String vigencia) {
        LocalDate[] rango = TablasCatalogoBdd.vigencia(vigencia);
        actualizarDescriptores(codigo, Map.of(VIGENCIA, TablasCatalogoBdd.vigenciaSolicitud(rango[0], rango[1])));
    }

    @Cuando("^intento cambiar el código \"([^\"]*)\" por \"([^\"]*)\"$")
    public void intento_cambiar_el_codigo(String codigo, String nuevo) {
        actualizarDescriptores(codigo, Map.of(CODIGO, nuevo));
    }

    @Entonces("^el catálogo \"([^\"]*)\" queda con nombre \"([^\"]*)\"$")
    public void queda_con_nombre(String codigo, String nombre) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(fixtures.catalogoNombre(codigo)).isEqualTo(nombre);
    }

    @Entonces("^el catálogo \"([^\"]*)\" queda con vigencia \"([^\"]*)\"$")
    public void queda_con_vigencia(String codigo, String vigencia) {
        LocalDate[] rango = TablasCatalogoBdd.vigencia(vigencia);
        assertThat(fixtures.fechaDesdeCatalogo(codigo)).isEqualTo(rango[0]);
        assertThat(fixtures.fechaHastaCatalogo(codigo)).isEqualTo(rango[1]);
    }

    @Entonces("^el catálogo conserva el código \"([^\"]*)\"$")
    public void conserva_el_codigo(String codigo) {
        assertThat(fixtures.existeCatalogo(codigo)).isTrue();
        assertThat(fixtures.existeCatalogo(codigo + "ES")).isFalse();
    }

    @Entonces("^la vigencia del catálogo \"([^\"]*)\" no cambia$")
    public void la_vigencia_no_cambia(String codigo) {
        LocalDate[] antes = (LocalDate[]) contexto.getInstantanea();
        assertThat(fixtures.fechaDesdeCatalogo(codigo)).isEqualTo(antes[0]);
        assertThat(fixtures.fechaHastaCatalogo(codigo)).isEqualTo(antes[1]);
    }

    @Dado("^que existen los catálogos \"([^\"]*)\" \\(sin hijo, ACTIVE\\) y \"([^\"]*)\"$")
    public void que_existen_los_catalogos(String padre, String hijo) {
        fixtures.asegurarCatalogo(padre);
        fixtures.asegurarCatalogo(hijo);
    }

    @Dado("^que \"([^\"]*)\" está INACTIVE y es padre de \"([^\"]*)\"$")
    public void que_esta_inactive_y_es_padre(String padre, String hijo) {
        fixtures.fijarPadre(hijo, padre);
        fixtures.fijarEstadoCatalogo(padre, EstadoVigencia.INACTIVE, Vigencia.hoy());
    }

    @Dado("^que el catálogo \"([^\"]*)\" no tiene registros$")
    public void que_el_catalogo_no_tiene_registros(String codigo) {
        assertThat(fixtures.contarRegistros(codigo)).isZero();
    }

    @Dado("^que el catálogo \"([^\"]*)\" contiene registros enlazados a registros de \"([^\"]*)\"$")
    public void que_contiene_registros_enlazados(String hijo, String padre) {
        assertThat(fixtures.codigoPadre(hijo)).isEqualTo(padre);
        fixtures.crearRegistroActivo(hijo, "H01");
        fixtures.crearRegistroActivo(hijo, "H02");
    }

    @Cuando("^(?:asigno|intento asignar) \"([^\"]*)\" como padre de \"([^\"]*)\"$")
    public void asigno_como_padre(String padre, String hijo) {
        actualizarDescriptores(hijo, Map.of(PADRE, padre));
    }

    @Cuando("^asigno como padre de \"([^\"]*)\" el código \"([^\"]*)\"$")
    public void asigno_como_padre_el_codigo(String hijo, String padre) {
        asigno_como_padre(padre, hijo);
    }

    /** S-04 (modelo de dominio v4.0): una celda vacía de "registro padre" es un registro padre no informado. */
    @Cuando("^(?:asigno|intento asignar) \"([^\"]*)\" como padre de \"([^\"]*)\" confirmando el cambio y con los registros padre:$")
    public void asigno_como_padre_con_registros_padre(String padre, String hijo, DataTable registrosPadre) {
        Map<String, String> correspondencia = new LinkedHashMap<>();
        registrosPadre.asMaps().forEach(fila -> correspondencia.put(fila.get("registro"),
                TablasCatalogoBdd.celda(fila, "registro padre")));
        Map<String, Object> cambios = new HashMap<>();
        cambios.put(PADRE, padre);
        cambios.put(CONFIRMAR_CAMBIO_PADRE, true);
        cambios.put("registrosPadre", correspondencia);
        actualizarDescriptores(hijo, cambios);
    }

    @Cuando("^(?:quito|intento quitar) el padre del catálogo \"([^\"]*)\"( sin confirmar el cambio| confirmando el cambio)?$")
    public void quito_el_padre(String codigo, String confirmacion) {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put(PADRE, null);
        cambios.put(CONFIRMAR_CAMBIO_PADRE, " confirmando el cambio".equals(confirmacion));
        actualizarDescriptores(codigo, cambios);
    }

    @Entonces("^\"([^\"]*)\" queda como catálogo plano$")
    public void queda_como_catalogo_plano(String codigo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(fixtures.codigoPadre(codigo)).isNull();
    }

    @Entonces("^\"([^\"]*)\" sigue siendo un catálogo plano$")
    public void sigue_siendo_un_catalogo_plano(String codigo) {
        assertThat(fixtures.codigoPadre(codigo)).isNull();
        assertThat(fixtures.claves(codigo)).allSatisfy(clave -> assertThat(fixtures.clavePadre(codigo, clave)).isNull());
    }

    @Entonces("^\"([^\"]*)\" no tiene catálogo hijo$")
    public void no_tiene_catalogo_hijo(String codigo) {
        assertThat(fixtures.codigoHijo(codigo)).isNull();
    }

    @Entonces("^los registros de \"([^\"]*)\" quedan sin id de registro padre$")
    public void los_registros_quedan_sin_registro_padre(String codigo) {
        assertThat(fixtures.claves(codigo)).isNotEmpty()
                .allSatisfy(clave -> assertThat(fixtures.clavePadre(codigo, clave)).isNull());
    }

    @Entonces("^los registros \"([^\"]*)\" y \"([^\"]*)\" quedan vinculados al registro padre \"([^\"]*)\"$")
    public void los_registros_quedan_vinculados(String clave1, String clave2, String clavePadre) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        for (String clave : List.of(clave1, clave2)) {
            assertThat(fixtures.clavePadre(fixtures.catalogoDelRegistro(clave), clave)).as(clave).isEqualTo(clavePadre);
        }
    }

    // ---------- HU-ADM-01-07 y 08: inactivar y reactivar ----------

    /** {@code PUT /catalogos/{codigo}/estado} con el {@code CambioEstado} dado. */
    private void cambiarEstado(String codigo, EstadoVigencia estado, LocalDate hasta) {
        Map<String, Object> cuerpo = new HashMap<>();
        if (estado != null) {
            cuerpo.put(ESTADO, estado.name());
        }
        cuerpo.put(HASTA, hasta);
        contexto.put(ESTADO_CATALOGO, cuerpo, codigo);
    }

    @Cuando("^fijo el estado del catálogo \"([^\"]*)\" en \"(ACTIVE|INACTIVE)\"(?: con (la TO DATE vacía|una TO DATE futura))?$")
    public void fijo_el_estado_del_catalogo(String codigo, String estado, String toDate) {
        LocalDate hasta = "una TO DATE futura".equals(toDate) ? fecha("una fecha futura") : null;
        cambiarEstado(codigo, EstadoVigencia.valueOf(estado), hasta);
    }

    @Cuando("^fijo la TO DATE del catálogo \"([^\"]*)\" en (la fecha actual|una fecha pasada|una fecha futura)$")
    public void fijo_la_to_date_del_catalogo(String codigo, String descripcion) {
        cambiarEstado(codigo, null, fecha(descripcion));
    }

    @Cuando("^(?:inactivo|intento inactivar) el catálogo \"([^\"]*)\"$")
    public void inactivo_el_catalogo(String codigo) {
        cambiarEstado(codigo, EstadoVigencia.INACTIVE, null);
    }

    @Cuando("^intento eliminar el catálogo \"([^\"]*)\"$")
    public void intento_eliminar_el_catalogo(String codigo) {
        contexto.delete(CATALOGO, codigo);
    }

    @Cuando("^(?:reactivo el catálogo|intento reactivar el catálogo|intento reactivar) \"([^\"]*)\"(?: con la TO DATE vacía)?$")
    public void reactivo_el_catalogo(String codigo) {
        cambiarEstado(codigo, EstadoVigencia.ACTIVE, null);
    }

    @Cuando("^intento reactivar el catálogo \"([^\"]*)\" manteniendo una TO DATE pasada$")
    public void intento_reactivar_con_to_date_pasada(String codigo) {
        cambiarEstado(codigo, EstadoVigencia.ACTIVE, fixtures.fechaHastaCatalogo(codigo));
    }

    @Dado("^que el catálogo \"([^\"]*)\" fue inactivado junto con sus registros y con el catálogo \"([^\"]*)\"$")
    public void que_fue_inactivado_en_cascada(String codigo, String hijo) {
        fixtures.crearRegistroActivo(codigo, "R01");
        fixtures.crearRegistroActivo(hijo, "H01");
        for (String inactivo : List.of(codigo, hijo)) {
            fixtures.fijarEstadoCatalogo(inactivo, EstadoVigencia.INACTIVE, Vigencia.hoy());
            fixtures.inactivarRegistros(inactivo);
        }
    }

    @Dado("^que el catálogo \"([^\"]*)\" fue inactivado$")
    public void que_el_catalogo_fue_inactivado(String codigo) {
        fixtures.fijarEstadoCatalogo(codigo, EstadoVigencia.INACTIVE, Vigencia.hoy());
    }

    @Dado("^que (?:los catálogos )?\"([^\"]*)\" y \"([^\"]*)\" tienen registros activos$")
    public void que_tienen_registros_activos(String primero, String segundo) {
        fixtures.crearRegistroActivo(primero, "A01");
        fixtures.crearRegistroActivo(segundo, "B01");
    }

    @Entonces("^todos los registros del catálogo \"([^\"]*)\" quedan con estado \"(ACTIVE|INACTIVE)\"$")
    public void todos_los_registros_del_catalogo_quedan(String codigo, String estado) {
        todos_los_registros_de_quedan("\"" + codigo + "\"", estado);
    }

    @Entonces("^los registros del catálogo \"([^\"]*)\" (?:conservan su estado|siguen con estado \"(ACTIVE|INACTIVE)\")$")
    public void los_registros_del_catalogo_conservan(String codigo, String estado) {
        todos_los_registros_de_quedan("\"" + codigo + "\"", estado != null ? estado : "ACTIVE");
    }

    @Entonces("^los catálogos ((?:\"[^\"]*\"(?:, | y )?)+) quedan con estado \"(ACTIVE|INACTIVE)\"$")
    public void los_catalogos_quedan_con_estado(String lista, String estado) {
        codigos(lista).forEach(codigo -> assertThat(fixtures.estadoCatalogo(codigo)).as(codigo)
                .isEqualTo(EstadoVigencia.valueOf(estado)));
    }

    @Entonces("^todos los registros de ((?:\"[^\"]*\"(?:, | y )?)+) quedan con estado \"(ACTIVE|INACTIVE)\"$")
    public void todos_los_registros_de_quedan(String lista, String estado) {
        for (String codigo : codigos(lista)) {
            assertThat(fixtures.claves(codigo)).as("registros de %s", codigo).isNotEmpty()
                    .allSatisfy(clave -> assertThat(fixtures.estadoRegistro(codigo, clave)).as(clave)
                            .isEqualTo(EstadoVigencia.valueOf(estado)));
        }
    }

    @Entonces("^el catálogo \"([^\"]*)\" sigue existiendo en el catalogMaster$")
    public void sigue_existiendo(String codigo) {
        assertThat(fixtures.existeCatalogo(codigo)).isTrue();
    }

    @Entonces("^se permite crear registros en el catálogo \"([^\"]*)\"$")
    public void se_permite_crear_registros(String codigo) {
        contexto.post(REGISTROS, Map.of(VALORES, fixtures.valoresValidos(codigo, "NEW", Map.of())), codigo);
        assertThat(contexto.getUltimoStatus()).isEqualTo(201);
    }
}

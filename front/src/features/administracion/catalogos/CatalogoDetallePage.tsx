import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { catalogosApi, type Catalogo, type ChildCatalogResult } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { CamposEditor, aCampoContrato, problemaDeCampos, type CampoEditable } from './CamposEditor';
import { ROLES_ADMIN_CATALOGOS } from './CatalogosPage';
import { RegistrosCatalogo } from './RegistrosCatalogo';
import { BandaRuta } from '../../../layout/BandaRuta';
import { cadenaDeAncestros, type Eslabon } from './cadenaDeCatalogos';

const CLAVE = 'administracion.catalogos';

const aCamposEditables = (catalogo: Catalogo): CampoEditable[] =>
  [...(catalogo.campos ?? [])]
    .sort((a, b) => (a.posicion ?? 0) - (b.posicion ?? 0))
    .map((f) => ({ nombre: f.nombre ?? '', clave: f.calificador === 'KEY' }));

/**
 * Ficha de un catálogo (flujos alternativos del CU-ADM-01): sus datos, sus
 * campos y sus registros. Se llega desde la lista, al pulsar "Abrir".
 *
 * No hay botón de eliminar: el back sólo permite inactivar ("Un catálogo no
 * puede eliminarse, solo inactivarse"), aunque el contrato exponga el DELETE.
 */
export function CatalogoDetallePage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { codigo = '' } = useParams<{ codigo: string }>();
  const [catalogo, setCatalogo] = useState<Catalogo | null>(null);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [nombre, setNombre] = useState('');
  const [padre, setPadre] = useState('');
  const [activo, setActivo] = useState<'ACTIVE' | 'INACTIVE'>('ACTIVE');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [campos, setCampos] = useState<CampoEditable[]>([]);
  /**
   * El catálogo que cuelga de éste. Un catálogo jerárquico se veía igual que uno
   * suelto: la ficha guardaba el código del padre pero no decía quién colgaba de
   * él, así que la jerarquía no se veía por ninguna parte
   * (observación del 30/09/2026: "no se están visualizando los catálogos hijos").
   *
   * Desde el 05/10/2026 un catálogo tiene como máximo un hijo: con varios, al
   * preguntar por los hijos de un registro saldrían registros de catálogos
   * distintos y con campos distintos. Por eso es uno o ninguno, no una lista.
   */
  const [hijo, setHijo] = useState<ChildCatalogResult | null>(null);
  /**
   * Los catálogos por encima de éste, para la miga de pan. La banda de ruta de
   * arriba sale de la URL y no puede saber de quién cuelga un catálogo, que es
   * un dato del servidor; por eso la jerarquía se pinta aquí.
   */
  const [ancestros, setAncestros] = useState<Eslabon[]>([]);

  const cargar = useCallback(() => {
    catalogosApi
      .consultarCatalogo({ codigo })
      .then(({ data }) => {
        setCatalogo(data);
        setNombre(data.nombre ?? '');
        setPadre(data.padre ?? '');
        setActivo(data.estado ?? 'ACTIVE');
        setDesde(data.vigencia?.desde ?? '');
        setHasta(data.vigencia?.hasta ?? '');
        setCampos(aCamposEditables(data));
        setErrorCarga(null);
      })
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)));
    // El hijo es contexto: si no carga, la ficha funciona igual. El contrato
    // devuelve null cuando el catálogo no tiene ninguno.
    catalogosApi
      .consultarCatalogoHijo({ codigo })
      .then(({ data }) => setHijo(data ?? null))
      .catch(() => setHijo(null));
  }, [codigo, t]);

  const puedeAdministrar = ROLES_ADMIN_CATALOGOS.some(hasRole);
  useEffect(() => {
    if (puedeAdministrar) cargar();
  }, [cargar, puedeAdministrar]);

  const codigoPadre = catalogo?.padre ?? null;
  useEffect(() => {
    let vigente = true;
    cadenaDeAncestros(codigoPadre).then((cadena) => {
      if (vigente) setAncestros(cadena);
    });
    return () => {
      vigente = false;
    };
  }, [codigoPadre]);

  const avisar = async (promesa: Promise<unknown>, exito: string) => {
    try {
      await promesa;
      await Swal.fire({ icon: 'success', text: t(exito) });
      cargar();
      return true;
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
      return false;
    }
  };

  if (!puedeAdministrar) {
    return (
      <p className="aviso-error" role="alert">
        {t(`${CLAVE}.sinPermiso`)}
      </p>
    );
  }
  if (errorCarga) return <p className="aviso-error">{errorCarga}</p>;
  if (!catalogo) return <p className="nota">{t('common.cargando')}</p>;

  const guardarDatos = () =>
    avisar(
      catalogosApi.actualizarDescriptores({
        codigo,
        catalogoDescriptores: {
          nombre: nombre.trim(),
          padre: padre.trim() || null,
          estado: activo,
          vigencia: { desde: desde || null, hasta: hasta || null },
        },
      }),
      `${CLAVE}.datosGuardados`,
    );

  const guardarCampos = async () => {
    const problema = problemaDeCampos(campos);
    if (problema) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.problemas.${problema}`) });
      return;
    }
    await avisar(
      catalogosApi.actualizarCampos({ codigo, campoDefinicion: aCampoContrato(campos) }),
      `${CLAVE}.camposGuardados`,
    );
  };

  /**
   * Inactivar es un cambio de estado: la operación es la misma que reactiva, y
   * en INACTIVE arrastra en cascada los registros del catálogo y su hijo
   * (RN-06). La pantalla sólo ofrece inactivar, como antes.
   */
  const inactivar = () =>
    avisar(catalogosApi.cambiarEstadoCatalogo({ codigo, cambioEstado: { estado: 'INACTIVE' } }), `${CLAVE}.inactivado`);


  return (
    <div className="formcard">
      <div className="formhead">
        <span>{catalogo.nombre} <span className="mono">· {catalogo.codigo}</span></span>
      </div>
      <div className="miga-catalogo">
        <BandaRuta
          tramos={[
            { texto: t(`${CLAVE}.titulo`), ruta: '/catalogos-generales', literal: true },
            ...ancestros.map((a) => ({
              texto: a.nombre,
              ruta: `/catalogos-generales/${encodeURIComponent(a.codigo)}`,
              literal: true,
            })),
            { texto: catalogo.nombre ?? codigo, literal: true },
          ]}
        />
      </div>
      <div className="formbody">

        <section>
          <h2 className="seccion">{t(`${CLAVE}.datos`)}</h2>
          <div className="fr">
            {/* El código identifica al catálogo y no se puede cambiar (Regla 17: el
                contrato deja "code" fuera del cuerpo de actualización a propósito).
                Se muestra igual: estaba sólo en la banda del título, y en los datos
                se veía el código del padre pero no el propio, así que parecía que
                la ficha no lo trajera (observación del 30/09/2026). */}
            <FormRow label={t(`${CLAVE}.codigo`)} controlId="det-codigo">
              <input id="det-codigo" className="mono" type="text" value={catalogo.codigo ?? ''} readOnly />
            </FormRow>
            <FormRow label={t(`${CLAVE}.nombre`)} controlId="det-nombre" required>
              <input id="det-nombre" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} />
            </FormRow>
            <FormRow label={t(`${CLAVE}.catalogoPadre`)} controlId="det-padre">
              <div className="campo-con-accion">
                <input id="det-padre" type="text" value={padre} onChange={(e) => setPadre(e.target.value)} />
                {/* La jerarquía se recorre en los dos sentidos: desde el hijo se
                    sube al padre igual que desde el padre se baja a los hijos. */}
                {catalogo.padre && (
                  <button
                    type="button"
                    className="btn secundario"
                    aria-label={t(`${CLAVE}.abrirPadre`, { codigo: catalogo.padre })}
                    onClick={() => navigate(`/catalogos-generales/${encodeURIComponent(catalogo.padre as string)}`)}
                  >
                    {t(`${CLAVE}.verPadre`)}
                  </button>
                )}
              </div>
            </FormRow>
            <FormRow label={t(`${CLAVE}.estado`)} controlId="det-estado">
              <select id="det-estado" value={activo} onChange={(e) => setActivo(e.target.value as 'ACTIVE' | 'INACTIVE')}>
                <option value="ACTIVE">{t(`${CLAVE}.estados.ACTIVE`)}</option>
                <option value="INACTIVE">{t(`${CLAVE}.estados.INACTIVE`)}</option>
              </select>
            </FormRow>
            <FormRow label={t(`${CLAVE}.vigenciaDesde`)} controlId="det-desde">
              <input id="det-desde" type="date" value={desde} onChange={(e) => setDesde(e.target.value)} />
            </FormRow>
            <FormRow label={t(`${CLAVE}.vigenciaHasta`)} controlId="det-hasta">
              <input id="det-hasta" type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} />
            </FormRow>
          </div>
          <div className="acciones-form">
            <button type="button" className="btn primario" onClick={guardarDatos}>
              {t(`${CLAVE}.guardarDatos`)}
            </button>
            <button type="button" className="btn secundario" onClick={inactivar} disabled={catalogo.estado === 'INACTIVE'}>
              {t(`${CLAVE}.inactivar`)}
            </button>
          </div>
        </section>

        <section>
          <CamposEditor campos={campos} alCambiar={setCampos} />
          <div className="acciones-form">
            <button type="button" className="btn primario" onClick={guardarCampos}>
              {t(`${CLAVE}.guardarCampos`)}
            </button>
          </div>
        </section>

        <section>
          <h2 className="seccion">{t(`${CLAVE}.hijo`)}</h2>
          {hijo === null ? (
            <p className="nota">{t(`${CLAVE}.sinHijos`)}</p>
          ) : (
            <div className="tabla-cont">
              <table>
                <thead>
                  <tr>
                    <th>{t(`${CLAVE}.codigo`)}</th>
                    <th>{t(`${CLAVE}.nombre`)}</th>
                    <th>{t('common.acciones')}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td className="mono">{hijo.codigo}</td>
                    <td>{hijo.nombre}</td>
                    <td>
                      <button
                        type="button"
                        className="btn secundario"
                        aria-label={t(`${CLAVE}.abrirCatalogo`, { nombre: hijo.nombre })}
                        onClick={() => navigate(`/catalogos-generales/${encodeURIComponent(hijo.codigo ?? '')}`)}
                      >
                        {t(`${CLAVE}.abrir`)}
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          )}
        </section>

        <RegistrosCatalogo codigo={codigo} campos={aCamposEditables(catalogo)} catalogoPadre={catalogo.padre} />

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate('/catalogos-generales')}>
            {t('common.regresar')}
          </button>
        </div>
      </div>
    </div>
  );
}

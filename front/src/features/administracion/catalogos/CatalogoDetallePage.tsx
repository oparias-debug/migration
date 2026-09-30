import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { catalogosApi, type CatalogResponse, type CatalogChildResponse } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { CamposEditor, aCampoContrato, problemaDeCampos, type CampoEditable } from './CamposEditor';
import { ROLES_ADMIN_CATALOGOS } from './CatalogosPage';
import { RegistrosCatalogo } from './RegistrosCatalogo';
import { BandaRuta } from '../../../layout/BandaRuta';
import { cadenaDeAncestros, type Eslabon } from './cadenaDeCatalogos';

const CLAVE = 'administracion.catalogos';

const aCamposEditables = (catalogo: CatalogResponse): CampoEditable[] =>
  [...(catalogo.fields ?? [])]
    .sort((a, b) => (a.posicion ?? 0) - (b.posicion ?? 0))
    .map((f) => ({ nombre: f.name ?? '', clave: f.qualifier === 'KEY' }));

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
  const [catalogo, setCatalogo] = useState<CatalogResponse | null>(null);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [nombre, setNombre] = useState('');
  const [padre, setPadre] = useState('');
  const [activo, setActivo] = useState<'ACTIVE' | 'INACTIVE'>('ACTIVE');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [campos, setCampos] = useState<CampoEditable[]>([]);
  /**
   * Los catálogos que cuelgan de éste. Un catálogo jerárquico se veía igual que
   * uno suelto: la ficha guardaba el código del padre pero no decía quiénes eran
   * sus hijos, así que la jerarquía no se veía por ninguna parte
   * (observación del 30/09/2026: "no se están visualizando los catálogos hijos").
   */
  const [hijos, setHijos] = useState<CatalogChildResponse[]>([]);
  /**
   * Los catálogos por encima de éste, para la miga de pan. La banda de ruta de
   * arriba sale de la URL y no puede saber de quién cuelga un catálogo, que es
   * un dato del servidor; por eso la jerarquía se pinta aquí.
   */
  const [ancestros, setAncestros] = useState<Eslabon[]>([]);

  const cargar = useCallback(() => {
    catalogosApi
      .consultarCatalogoPorCodigo({ code: codigo })
      .then(({ data }) => {
        setCatalogo(data);
        setNombre(data.name ?? '');
        setPadre(data.parent ?? '');
        setActivo(data.active ?? 'ACTIVE');
        setDesde(data.fromDate ?? '');
        setHasta(data.toDate ?? '');
        setCampos(aCamposEditables(data));
        setErrorCarga(null);
      })
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)));
    // Los hijos son contexto: si no cargan, la ficha funciona igual.
    catalogosApi
      .consultarCatalogosHijos({ code: codigo })
      .then(({ data }) => setHijos(data))
      .catch(() => setHijos([]));
  }, [codigo, t]);

  const puedeAdministrar = ROLES_ADMIN_CATALOGOS.some(hasRole);
  useEffect(() => {
    if (puedeAdministrar) cargar();
  }, [cargar, puedeAdministrar]);

  const codigoPadre = catalogo?.parent ?? null;
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
      catalogosApi.actualizarDescriptoresCatalogo({
        code: codigo,
        catalogDescriptorsUpdateRequest: {
          name: nombre.trim(),
          parent: padre.trim() || null,
          active: activo,
          fromDate: desde || null,
          toDate: hasta || null,
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
      catalogosApi.actualizarCamposCatalogo({ code: codigo, catalogFieldsUpdateRequest: { fields: aCampoContrato(campos) } }),
      `${CLAVE}.camposGuardados`,
    );
  };

  const inactivar = () =>
    avisar(catalogosApi.inactivarCatalogo({ code: codigo, inactivationRequest: {} }), `${CLAVE}.inactivado`);


  return (
    <div className="formcard">
      <div className="formhead">
        <span>{catalogo.name} <span className="mono">· {catalogo.code}</span></span>
      </div>
      <div className="miga-catalogo">
        <BandaRuta
          tramos={[
            { texto: t(`${CLAVE}.titulo`), ruta: '/catalogos-generales', literal: true },
            ...ancestros.map((a) => ({
              texto: a.name,
              ruta: `/catalogos-generales/${encodeURIComponent(a.code)}`,
              literal: true,
            })),
            { texto: catalogo.name ?? codigo, literal: true },
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
              <input id="det-codigo" className="mono" type="text" value={catalogo.code ?? ''} readOnly />
            </FormRow>
            <FormRow label={t(`${CLAVE}.nombre`)} controlId="det-nombre" required>
              <input id="det-nombre" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} />
            </FormRow>
            <FormRow label={t(`${CLAVE}.catalogoPadre`)} controlId="det-padre">
              <div className="campo-con-accion">
                <input id="det-padre" type="text" value={padre} onChange={(e) => setPadre(e.target.value)} />
                {/* La jerarquía se recorre en los dos sentidos: desde el hijo se
                    sube al padre igual que desde el padre se baja a los hijos. */}
                {catalogo.parent && (
                  <button
                    type="button"
                    className="btn secundario"
                    aria-label={t(`${CLAVE}.abrirPadre`, { codigo: catalogo.parent })}
                    onClick={() => navigate(`/catalogos-generales/${encodeURIComponent(catalogo.parent as string)}`)}
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
            <button type="button" className="btn secundario" onClick={inactivar} disabled={catalogo.active === 'INACTIVE'}>
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
          <h2 className="seccion">{t(`${CLAVE}.hijos`)}</h2>
          {hijos.length === 0 ? (
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
                  {hijos.map((h) => (
                    <tr key={h.code}>
                      <td className="mono">{h.code}</td>
                      <td>{h.name}</td>
                      <td>
                        <button
                          type="button"
                          className="btn secundario"
                          aria-label={t(`${CLAVE}.abrirCatalogo`, { nombre: h.name })}
                          onClick={() => navigate(`/catalogos-generales/${encodeURIComponent(h.code ?? '')}`)}
                        >
                          {t(`${CLAVE}.abrir`)}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <RegistrosCatalogo codigo={codigo} campos={aCamposEditables(catalogo)} catalogoPadre={catalogo.parent} />

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate('/catalogos-generales')}>
            {t('common.regresar')}
          </button>
        </div>
      </div>
    </div>
  );
}

import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { catalogosApi, type Catalog } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { CamposEditor, aCampoContrato, problemaDeCampos, type CampoEditable } from './CamposEditor';
import { ROL_ADMIN_CATALOGOS } from './CatalogosPage';
import { RegistrosCatalogo } from './RegistrosCatalogo';

const CLAVE = 'administracion.catalogos';

const aCamposEditables = (catalogo: Catalog): CampoEditable[] =>
  [...(catalogo.fields ?? [])]
    .sort((a, b) => (a.position ?? 0) - (b.position ?? 0))
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
  const [catalogo, setCatalogo] = useState<Catalog | null>(null);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [nombre, setNombre] = useState('');
  const [padre, setPadre] = useState('');
  const [activo, setActivo] = useState<'ACTIVE' | 'INACTIVE'>('ACTIVE');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [campos, setCampos] = useState<CampoEditable[]>([]);

  const cargar = useCallback(() => {
    catalogosApi
      .consultarCatalogo({ code: codigo })
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
  }, [codigo, t]);

  const puedeAdministrar = hasRole(ROL_ADMIN_CATALOGOS);
  useEffect(() => {
    if (puedeAdministrar) cargar();
  }, [cargar, puedeAdministrar]);

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
    avisar(catalogosApi.inactivarCatalogo({ code: codigo, inactivationRequest: { active: 'INACTIVE' } }), `${CLAVE}.inactivado`);


  return (
    <div className="formcard">
      <div className="formhead">
        <span>{catalogo.name} <span className="mono">· {catalogo.code}</span></span>
      </div>
      <div className="formbody">

        <section>
          <h2 className="seccion">{t(`${CLAVE}.datos`)}</h2>
          <div className="fr">
            <FormRow label={t(`${CLAVE}.nombre`)} controlId="det-nombre" required>
              <input id="det-nombre" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} />
            </FormRow>
            <FormRow label={t(`${CLAVE}.catalogoPadre`)} controlId="det-padre">
              <input id="det-padre" type="text" value={padre} onChange={(e) => setPadre(e.target.value)} />
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

        <RegistrosCatalogo codigo={codigo} campos={aCamposEditables(catalogo)} />

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate('/catalogos-generales')}>
            {t('common.regresar')}
          </button>
        </div>
      </div>
    </div>
  );
}

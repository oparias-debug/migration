import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { etapasApi } from '../../../api/preinversionApi';
import type { FichaInformacionGeneral } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { formatIniciativa } from '../proyectos/proyectoLabels';

// Ficha de proyectos de emergencia habla de "Nivel nacional" como valor de distrito, no de un
// catálogo separado de Unidades Ejecutoras: por eso, a diferencia de las demás páginas de este
// CU, aquí no hay ningún catálogo que consultar para el campo de abajo (ver nota).
const SIN_RESPALDO_CO_EJECUTOR =
  'El contrato no define un endpoint para listar Unidades Ejecutoras candidatas a Co-ejecutor ' +
  '(seleccionarCoEjecutor solo recibe un ID numérico). Mientras ese catálogo no se defina, ' +
  'ingrese el ID directamente.';

// Pantalla "Ficha de información general" (Anexo A.3, CU-PRE-3.5-ver-ficha-informacion-general.feature
// y CU-PRE-3.5-boton-coejecutor.feature). RN14: no editable, salvo la sección Co-ejecutor (RN16).
export function FichaInformacionGeneralPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [ficha, setFicha] = useState<FichaInformacionGeneral | null>(null);
  const [idCoEjecutor, setIdCoEjecutor] = useState('');
  const [guardandoCoEjecutor, setGuardandoCoEjecutor] = useState(false);

  const esCoordinadorSymp = hasRole('COORDINADOR_SYMP');

  useEffect(() => {
    if (!idProyecto) return;
    etapasApi
      .obtenerFichaInformacionGeneral({ idProyecto })
      .then(({ data }) => setFicha(data))
      .catch((fallo) => setErrorCarga(mensajeDeError(toErrorApi(fallo), t)))
      .finally(() => setCargando(false));
  }, [idProyecto, t]);

  const guardarCoEjecutor = async () => {
    const idNumerico = Number(idCoEjecutor);
    if (!idCoEjecutor.trim() || Number.isNaN(idNumerico)) {
      await Swal.fire({ icon: 'error', text: t('preinversion.fichaGeneral.idCoEjecutorInvalido') });
      return;
    }
    setGuardandoCoEjecutor(true);
    try {
      const { data } = await etapasApi.seleccionarCoEjecutor({
        idProyecto,
        seleccionCoEjecutorRequest: { idUnidadEjecutoraCoEjecutora: idNumerico },
      });
      setFicha(data);
      setIdCoEjecutor('');
      await Swal.fire({ icon: 'success', text: t('preinversion.fichaGeneral.coEjecutorAsignado') });
    } catch (fallo) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(fallo), t) });
    } finally {
      setGuardandoCoEjecutor(false);
    }
  };

  if (cargando) return <p>{t('common.cargando')}</p>;

  if (errorCarga || !ficha) {
    return (
      <div className="aviso-error" role="alert">
        <p>{errorCarga}</p>
      </div>
    );
  }

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t('preinversion.fichaGeneral.titulo')}</span>
      </div>
      <div className="formbody">
        <div className="fr">
          <FormRow label={t('preinversion.registro.campoInstitucion')}>
            <p className="campo-asignado">{ficha.institucion.nombre}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoUnidadEjecutora')}>
            <p className="campo-asignado">{ficha.unidadEjecutora.nombre}</p>
          </FormRow>
          <FormRow label={t('preinversion.fichaGeneral.campoCoEjecutor')}>
            <p className="campo-asignado">{ficha.coEjecutor?.nombre ?? t('common.noAplica')}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoIniciativa')}>
            <p className="campo-asignado">{formatIniciativa(ficha.iniciativaInversion)}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoNombre')} ancho>
            <p className="campo-asignado">{ficha.nombreProyecto}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoMonto')}>
            <p className="campo-asignado">{ficha.montoEstimadoInversion.toLocaleString()}</p>
          </FormRow>
          {ficha.montoAjustadoEjecucion != null && (
            <FormRow label={t('preinversion.fichaGeneral.campoMontoAjustadoEjecucion')}>
              <p className="campo-asignado">{ficha.montoAjustadoEjecucion.toLocaleString()}</p>
            </FormRow>
          )}
          <FormRow label={t('preinversion.registro.campoSector')}>
            <p className="campo-asignado">{ficha.sector.nombre}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoMacrosector')}>
            <p className="campo-asignado">{ficha.sector.macrosector.nombre}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoEjeTematico')}>
            <p className="campo-asignado">{ficha.ejeTematico.nombre}</p>
          </FormRow>
          <FormRow label={t('preinversion.fichaGeneral.campoGrdGrcAcc')}>
            <p className="campo-asignado">{ficha.esProyectoGrdGrcAcc ? t('common.si') : t('common.no')}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoEmergencia')}>
            <p className="campo-asignado">{ficha.esProyectoEmergencia ? t('common.si') : t('common.no')}</p>
          </FormRow>
          {ficha.esProyectoEmergencia && (
            <>
              <FormRow label={t('preinversion.registro.campoTipoEvento')}>
                <p className="campo-asignado">{ficha.tipoEvento ?? t('common.noAplica')}</p>
              </FormRow>
              <FormRow label={t('preinversion.registro.campoNumeroDecreto')}>
                <p className="campo-asignado">{ficha.numeroDecretoLegislativo ?? t('common.noAplica')}</p>
              </FormRow>
            </>
          )}
          <FormRow label={t('preinversion.registro.campoEjePlanGobierno')}>
            <p className="campo-asignado">{ficha.ejePlanGobierno?.nombre ?? t('common.noAplica')}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoPlanSectorialRegional')}>
            <p className="campo-asignado">{ficha.planSectorialRegional?.nombre ?? t('common.noAplica')}</p>
          </FormRow>
          <FormRow label={t('preinversion.fichaGeneral.campoObjetivoProyecto')} ancho>
            <p className="campo-asignado">{ficha.objetivoProyecto ?? t('common.noAplica')}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoDescripcion')} ancho>
            <p className="campo-asignado">{ficha.descripcionProyecto}</p>
          </FormRow>
        </div>

        {esCoordinadorSymp && (
          <section className="card mb-4">
            <div className="card-header">
              <h2 className="h6 mb-0">{t('preinversion.fichaGeneral.tituloCoEjecutor')}</h2>
            </div>
            <div className="card-body">
              <p className="nota">{SIN_RESPALDO_CO_EJECUTOR}</p>
              <FormRow controlId="idCoEjecutor" label={t('preinversion.fichaGeneral.campoIdCoEjecutor')}>
                <input
                  type="text"
                  inputMode="numeric"
                  id="idCoEjecutor"
                  value={idCoEjecutor}
                  onChange={(e) => setIdCoEjecutor(e.target.value)}
                  disabled={guardandoCoEjecutor}
                />
              </FormRow>
              <button type="button" className="btn primario" onClick={guardarCoEjecutor} disabled={guardandoCoEjecutor}>
                {t('preinversion.registro.botonGuardar')}
              </button>
            </div>
          </section>
        )}

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/etapas`)}>
            {t('preinversion.registro.botonRegresar')}
          </button>
        </div>
      </div>
    </div>
  );
}

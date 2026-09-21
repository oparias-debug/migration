import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { calendariosApi, consultasCalendarioApi, type CalendarioResumen } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';

export const CLAVE_CALENDARIO = 'administracion.calendario';
/** Gestionar un calendario exige uno de estos dos roles (RN12); consultarlo, ninguno (RN18/RN22). */
export const ROLES_CALENDARIO = ['ADMINISTRADOR', 'ADMINISTRADOR_CALENDARIO'];

/**
 * Calendario (CU-ADM-04), bajo Administración junto a Catálogos: la lista de
 * calendarios y el alta de uno nuevo. Al abrir uno se pasa a su ficha, donde se
 * registran los días festivos y las fechas especiales que el back usa después
 * para contar días hábiles entre dos fechas.
 */
export function CalendarioPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const [calendarios, setCalendarios] = useState<CalendarioResumen[]>([]);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [creando, setCreando] = useState(false);

  const puedeAdministrar = ROLES_CALENDARIO.some(hasRole);

  const cargar = useCallback(() => {
    setCargando(true);
    consultasCalendarioApi
      .listarCalendarios()
      .then(({ data }) => {
        setCalendarios(data ?? []);
        setErrorCarga(null);
      })
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
  }, [t]);

  useEffect(() => {
    cargar();
  }, [cargar]);

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t(`${CLAVE_CALENDARIO}.titulo`)}</span>
      </div>
      <div className="formbody">
        <p className="nota">{t(`${CLAVE_CALENDARIO}.intro`)}</p>

        {/* Cualquier usuario ve la lista (RN22); crear es de ADMINISTRADOR y
            ADMINISTRADOR_CALENDARIO (RN12). */}
        {puedeAdministrar && creando && (
          <NuevoCalendario
            alCancelar={() => setCreando(false)}
            alCrear={(codigo) => navigate(`/administracion/calendario/${encodeURIComponent(codigo)}`)}
          />
        )}
        {puedeAdministrar && !creando && (
          <div className="acciones-form">
            <button type="button" className="btn primario" onClick={() => setCreando(true)}>
              {t(`${CLAVE_CALENDARIO}.nuevo`)}
            </button>
          </div>
        )}

        {cargando && <p className="nota">{t('common.cargando')}</p>}
        {errorCarga && <p className="aviso-error">{errorCarga}</p>}
        {!cargando && !errorCarga && calendarios.length === 0 && (
          <p className="nota">{t(`${CLAVE_CALENDARIO}.sinCalendarios`)}</p>
        )}

        {calendarios.length > 0 && (
          <div className="tabla-cont">
            <table>
              <thead>
                <tr>
                  <th>{t(`${CLAVE_CALENDARIO}.codigo`)}</th>
                  <th>{t(`${CLAVE_CALENDARIO}.nombre`)}</th>
                  <th>{t(`${CLAVE_CALENDARIO}.estado`)}</th>
                  <th>{t('common.acciones')}</th>
                </tr>
              </thead>
              <tbody>
                {calendarios.map((c) => (
                  <tr key={c.codigo}>
                    <td className="mono">{c.codigo}</td>
                    <td>{c.nombre}</td>
                    <td>{t(`${CLAVE_CALENDARIO}.estados.${c.estado}`)}</td>
                    <td>
                      <button
                        type="button"
                        className="btn secundario"
                        aria-label={t(`${CLAVE_CALENDARIO}.abrirCalendario`, { nombre: c.nombre })}
                        onClick={() => navigate(`/administracion/calendario/${encodeURIComponent(c.codigo)}`)}
                      >
                        {t(`${CLAVE_CALENDARIO}.abrir`)}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

/** Alta de un calendario: sólo sus datos; los períodos y excepciones se cargan en su ficha. */
function NuevoCalendario({
  alCancelar,
  alCrear,
}: {
  readonly alCancelar: () => void;
  readonly alCrear: (codigo: string) => void;
}) {
  const { t } = useTranslation();
  const [codigo, setCodigo] = useState('');
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [guardando, setGuardando] = useState(false);

  const crear = async () => {
    if (!codigo.trim() || !nombre.trim() || !desde || !hasta) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE_CALENDARIO}.faltanDatos`) });
      return;
    }
    setGuardando(true);
    try {
      await calendariosApi.crearCalendario({
        crearCalendarioRequest: {
          codigo: codigo.trim(),
          nombre: nombre.trim(),
          descripcion: descripcion.trim() || undefined,
          fechaInicio: desde,
          fechaFin: hasta,
          estado: 'ACTIVO',
        },
      });
      await Swal.fire({ icon: 'success', text: t(`${CLAVE_CALENDARIO}.creado`) });
      alCrear(codigo.trim());
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <section>
      <h2 className="seccion">{t(`${CLAVE_CALENDARIO}.tituloCrear`)}</h2>
      <div className="fr">
        <FormRow label={t(`${CLAVE_CALENDARIO}.codigo`)} controlId="cal-codigo" required>
          <input id="cal-codigo" type="text" value={codigo} onChange={(e) => setCodigo(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE_CALENDARIO}.nombre`)} controlId="cal-nombre" required>
          <input id="cal-nombre" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE_CALENDARIO}.descripcion`)} controlId="cal-descripcion">
          <input id="cal-descripcion" type="text" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE_CALENDARIO}.fechaInicio`)} controlId="cal-inicio" required>
          <input id="cal-inicio" type="date" value={desde} onChange={(e) => setDesde(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE_CALENDARIO}.fechaFin`)} controlId="cal-fin" required>
          <input id="cal-fin" type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} />
        </FormRow>
      </div>
      <div className="acciones-form">
        <button type="button" className="btn neutro" onClick={alCancelar}>
          {t('common.cancelar')}
        </button>
        <button type="button" className="btn primario" onClick={crear} disabled={guardando}>
          {t(`${CLAVE_CALENDARIO}.crear`)}
        </button>
      </div>
    </section>
  );
}

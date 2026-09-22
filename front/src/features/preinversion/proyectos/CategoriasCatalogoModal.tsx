import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { catalogoPreinversionApi, TipoMedidaCatalogo } from '../../../api/preinversionApi';
import type { MedidaCatalogo } from '../../../api/preinversionApi';

interface CategoriasCatalogoModalProps {
  readonly onClose: () => void;
}

const TIPOS = [
  { tipo: TipoMedidaCatalogo.Grd, tituloKey: 'preinversion.registro.categoriasGrd' },
  { tipo: TipoMedidaCatalogo.Grc, tituloKey: 'preinversion.registro.categoriasGrc' },
  { tipo: TipoMedidaCatalogo.Acc, tituloKey: 'preinversion.registro.categoriasAcc' },
] as const;

// Botón "Ver descripción de categorías" (escenario @ui-only): ventana emergente con
// las tablas de los Anexos C.1 (GRD), C.1.5 (GRC) y C.2 (ACC).
export function CategoriasCatalogoModal({ onClose }: CategoriasCatalogoModalProps) {
  const { t } = useTranslation();
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [catalogos, setCatalogos] = useState<Record<string, MedidaCatalogo[]>>({});
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  useEffect(() => {
    let activo = true;
    Promise.all(TIPOS.map(({ tipo }) => catalogoPreinversionApi.listarMedidasCatalogo({ tipo }).then((res) => [tipo, res.data] as const))).then(
      (entradas) => {
        if (!activo) return;
        setCatalogos(Object.fromEntries(entradas));
        setCargando(false);
      },
    );
    return () => {
      activo = false;
    };
  }, []);

  return (
    <dialog
      ref={dialogRef}
      className="modal-categorias"
      onClose={onClose}
    >
      <div className="mc-caja">
        <div className="mc-cabecera">
          <h2>{t('preinversion.registro.categoriasTitulo')}</h2>
          <button type="button" className="mc-cerrar" aria-label={t('common.cerrar')} onClick={onClose}>
            ×
          </button>
        </div>
        <div className="mc-cuerpo">
            {cargando ? (
              <p>...</p>
            ) : (
              TIPOS.map(({ tipo, tituloKey }) => (
                <div key={tipo} className="mc-grupo">
                  <h3>{t(tituloKey)}</h3>
                  <table>
                    <thead>
                      <tr>
                        <th>{t('preinversion.registro.categoriasCodigo')}</th>
                        <th>{t('preinversion.registro.categoriasDescripcion')}</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(catalogos[tipo] ?? []).map((entrada) => (
                        <tr key={entrada.codigo}>
                          <td>{entrada.codigo}</td>
                          <td>{entrada.descripcion}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ))
            )}
        </div>
        <div className="mc-pie">
          <button type="button" className="btn neutro" onClick={onClose}>
            {t('common.cerrar')}
          </button>
        </div>
      </div>
    </dialog>
  );
}

import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { catalogoPreinversionApi, TipoMedidaCatalogo } from '../../../api/preinversionApi';
import type { MedidaCatalogo } from '../../../api/preinversionApi';

interface CategoriasCatalogoModalProps {
  onClose: () => void;
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
  const [catalogos, setCatalogos] = useState<Record<string, MedidaCatalogo[]>>({});
  const [cargando, setCargando] = useState(true);

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
    <div className="modal d-block" tabIndex={-1} role="dialog" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
      <div className="modal-dialog modal-lg modal-dialog-scrollable" role="document">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">{t('preinversion.registro.categoriasTitulo')}</h5>
            <button type="button" className="btn-close" aria-label="Close" onClick={onClose} />
          </div>
          <div className="modal-body">
            {cargando ? (
              <p>...</p>
            ) : (
              TIPOS.map(({ tipo, tituloKey }) => (
                <div key={tipo} className="mb-4">
                  <h6>{t(tituloKey)}</h6>
                  <table className="table table-sm table-bordered">
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
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              {t('common.cerrar')}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

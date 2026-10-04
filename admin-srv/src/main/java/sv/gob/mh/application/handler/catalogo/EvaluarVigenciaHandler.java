package sv.gob.mh.application.handler.catalogo;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.Vigencia;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * HU-ADM-01-16 (SF-14): evaluación diaria de vigencia, sin endpoint. Los catálogos y registros
 * guardados ACTIVE cuya TO DATE ya llegó pasan a INACTIVE con la cascada de RN-06 y RN-14; sin
 * fechas siguen ACTIVE (RN-15). Nunca reactiva un elemento inactivo (S-03).
 *
 * <p>Se considera vencida la TO DATE igual a hoy o anterior, el mismo criterio de RN-12b y de
 * {@link Vigencia#vencido} (pendiente P-01 del CU).</p>
 */
@Service
public class EvaluarVigenciaHandler {

    /** Cuántos elementos inactivó directamente la evaluación (sin contar los de la cascada). */
    public record Resultado(int catalogosInactivados, int registrosInactivados) {
    }

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final InactivacionEnCascada cascada;

    public EvaluarVigenciaHandler(CatalogoRepository catalogoRepository, RegistroRepository registroRepository,
            InactivacionEnCascada cascada) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.cascada = cascada;
    }

    @Transactional
    public Resultado ejecutar() {
        var catalogos = 0;
        for (Catalogo catalogo : catalogoRepository.listarPorCodigo()) {
            // Se relee: la cascada de un catálogo anterior pudo haberlo inactivado ya.
            var actual = catalogoRepository.obtenerPorCodigo(catalogo.getCodigo());
            if (actual.getEstado() == EstadoVigencia.ACTIVE && Vigencia.vencido(actual.getFechaHasta())) {
                actual.inactivarEnCascada(actual.getFechaHasta());
                cascada.guardarCatalogoInactivo(actual);
                catalogos++;
            }
        }
        var registros = 0;
        LocalDate hoy = Vigencia.hoy();
        for (Registro vencido : registroRepository.listarActivosVencidos(hoy)) {
            var actual = registroRepository.obtenerPorClave(vencido.getCatalogo().getCodigo(), vencido.getClave());
            if (actual.getEstado() == EstadoVigencia.ACTIVE) {
                actual.inactivarEnCascada(actual.getFechaHasta());
                cascada.guardarRegistroInactivo(actual);
                registros++;
            }
        }
        return new Resultado(catalogos, registros);
    }
}

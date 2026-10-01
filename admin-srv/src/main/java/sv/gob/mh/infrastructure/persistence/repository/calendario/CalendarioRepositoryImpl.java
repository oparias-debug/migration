package sv.gob.mh.infrastructure.persistence.repository.calendario;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;
import sv.gob.mh.infrastructure.persistence.entity.calendario.CalendarioEntity;

/** Adaptador JPA de {@link CalendarioRepository}. */
@Repository
public class CalendarioRepositoryImpl implements CalendarioRepository {

    private final CalendarioJpaRepository jpa;

    public CalendarioRepositoryImpl(CalendarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Calendario> buscarPorCodigo(String codigo) {
        return jpa.findByCodigo(codigo).map(CalendarioPersistenceMapper::aModelo);
    }

    @Override
    public boolean existeCodigo(String codigo) {
        return jpa.existsByCodigo(codigo);
    }

    @Override
    public List<Calendario> listarPorCodigo() {
        return jpa.findAllByOrderByCodigoAsc().stream().map(CalendarioPersistenceMapper::aModelo).toList();
    }

    /**
     * Un calendario ya persistido está gestionado en la transacción: basta el flush, que persiste en
     * cascada sus CalendarItems nuevos sobre las mismas instancias (un merge los copiaría, y el id
     * quedaría en la copia).
     */
    @Override
    public Calendario guardar(Calendario calendario) {
        CalendarioEntity entidad = calendario.getId() == null ? new CalendarioEntity()
                : jpa.findById(calendario.getId()).orElseThrow();
        CalendarioPersistenceMapper.copiar(calendario, entidad);
        if (entidad.getId() == null) {
            jpa.save(entidad);
        }
        jpa.flush();
        return CalendarioPersistenceMapper.aModelo(entidad);
    }
}

package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.repository.DepartamentoRepository;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.dto.AplicaActualizacionOtDto;
import sv.gob.mh.siip.model.preinversion.dto.ContenidoIniciativaResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoIndicadorDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.UbicacionGeograficaDto;
import sv.gob.mh.siip.model.preinversion.mapper.SeleccionYRegistroDeEtapasMapper;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.TipoCostoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional(readOnly = true)
public class CatalogosSeleccionEtapasServiceImpl implements CatalogosSeleccionEtapasService {

    /** RN: "Nivel nacional" (campo Coordenadas de la Ficha de emergencia) es un valor mas de distrito. */
    private static final String NIVEL_NACIONAL = "Nivel nacional";

    private final TipoCostoRepository tipoCostoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final MunicipioRepository municipioRepository;
    private final ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository;
    private final ActorContexto actorContexto;
    private final SeleccionYRegistroDeEtapasMapper mapper;

    public CatalogosSeleccionEtapasServiceImpl(TipoCostoRepository tipoCostoRepository,
            DepartamentoRepository departamentoRepository, MunicipioRepository municipioRepository,
            ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository, ActorContexto actorContexto,
            SeleccionYRegistroDeEtapasMapper mapper) {
        this.tipoCostoRepository = tipoCostoRepository;
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.productoIndicadorCatalogoRepository = productoIndicadorCatalogoRepository;
        this.actorContexto = actorContexto;
        this.mapper = mapper;
    }

    @Override
    public List<TipoCostoResumenDto> listarTiposCosto() {
        actorContexto.exigir();
        return tipoCostoRepository.findAllByOrderByNombreAsc().stream().map(mapper::toDto).toList();
    }

    @Override
    public List<UbicacionGeograficaDto> listarUbicacionesGeograficas(String departamento, String busqueda) {
        actorContexto.exigir();
        List<UbicacionGeograficaDto> resultado = new ArrayList<>();

        for (Municipio municipio : municipioRepository.findAllByOrderByNombreAsc()) {
            resultado.add(new UbicacionGeograficaDto()
                    .distrito(municipio.getNombre())
                    .departamento(municipio.getDepartamento().getNombre())
                    .region(municipio.getDepartamento().getRegion()));
        }
        for (Departamento depto : departamentoRepository.findAll()) {
            resultado.add(new UbicacionGeograficaDto()
                    .distrito(depto.getNombre() + " - Nivel departamental")
                    .departamento(depto.getNombre())
                    .region(depto.getRegion()));
        }
        // "Nivel nacional" no tiene Departamento/Region propios; se usa el mismo valor como
        // sentinela en los 3 campos (UbicacionGeografica los exige @NotNull), igual que el CU
        // modela "Nivel nacional" como un valor mas de distrito, no como un concepto propio.
        resultado.add(new UbicacionGeograficaDto().distrito(NIVEL_NACIONAL).departamento(NIVEL_NACIONAL).region(NIVEL_NACIONAL));

        if (departamento != null && !departamento.isBlank()) {
            resultado.removeIf(u -> !departamento.equalsIgnoreCase(u.getDepartamento()));
        }
        if (busqueda != null && !busqueda.isBlank()) {
            String palabraClave = busqueda.toLowerCase();
            resultado.removeIf(u -> !u.getDistrito().toLowerCase().contains(palabraClave));
        }
        return resultado;
    }

    @Override
    public List<ProductoIndicadorDto> listarProductosIndicadores() {
        actorContexto.exigir();
        return mapper.toProductoIndicadorDtoList(productoIndicadorCatalogoRepository.findAllByOrderByCodigoProductoAsc());
    }

    @Override
    public List<ContenidoIniciativaResumenDto> listarContenidoIniciativasProyecto() {
        actorContexto.exigir();
        return CONTENIDO_INICIATIVAS_PROYECTO;
    }

    /**
     * Anexo F — "Contenido de Iniciativas de Proyecto" (RN20), 30 filas, transcritas íntegras desde
     * el archivo fuente `CU-PRE-3_5_ANEXO_F.xlsx` (ver
     * docs/casos-de-uso/1 - Preinversion/UC-PRE-3.5-Seleccion_y_Registro_de_Etapas.md, sección
     * "Catálogos Detectados › Anexo F"). No incluye las 4 filas de encabezado de sección del Excel
     * original ("1. Identificación del proyecto", "2. Formulación del proyecto", "3. Evaluación",
     * "4. Programación", "5. Documentos anexos"): son agrupadores visuales sin marca propia, no
     * contenido habilitable.
     */
    private static final List<ContenidoIniciativaResumenDto> CONTENIDO_INICIATIVAS_PROYECTO = List.of(
            fila("Antecedentes", "CUPRE-04", true, true, true, true, true, true, AplicaActualizacionOtDto.SIN_DATO),
            fila("Problema Central", "CUPRE-04", true, true, true, true, true, true, AplicaActualizacionOtDto.SIN_DATO),
            fila("Objetivo General", "CUPRE-04", true, true, true, true, true, true, AplicaActualizacionOtDto.SIN_DATO),
            fila("Objetivos Específicos", "CUPRE-04", true, true, true, true, true, true, AplicaActualizacionOtDto.SIN_DATO),
            fila("Análisis de Alternativas de Solución", "CUPRE-05", true, true, false, false, false, false, AplicaActualizacionOtDto.SIN_DATO),
            fila("Análisis de Interesados", "CUPRE-06", true, true, true, true, false, false, AplicaActualizacionOtDto.APLICA),
            fila("Análisis de la Población", "CUPRE-07", true, true, true, true, true, true, AplicaActualizacionOtDto.APLICA),
            fila("Área de Influencia", "CUPRE-08", true, true, true, true, false, false, AplicaActualizacionOtDto.APLICA),
            fila("Análisis de Mercado", "CUPRE-09", true, true, true, true, false, false, AplicaActualizacionOtDto.APLICA),
            fila("Situación Base Optimizada", "CUPRE-10", true, false, false, false, false, false, AplicaActualizacionOtDto.NO_APLICA_AL_CU),
            fila("Descripción Técnica", "CUPRE-11", true, true, true, true, true, true, AplicaActualizacionOtDto.APLICA),
            fila("Localización", "CUPRE-12", true, true, true, true, true, true, AplicaActualizacionOtDto.APLICA),
            fila("Tamaño", "CUPRE-13", true, true, true, true, true, true, AplicaActualizacionOtDto.NO_APLICA_AL_CU),
            fila("Análisis Ambiental", "CUPRE-14", true, true, true, true, false, false, AplicaActualizacionOtDto.APLICA),
            fila("Análisis de Riesgos", "CUPRE-15", true, true, true, true, true, false, AplicaActualizacionOtDto.APLICA),
            fila("Análisis Legal", "CUPRE-16", true, true, true, true, false, false, AplicaActualizacionOtDto.APLICA),
            fila("Presupuesto de Inversión", "CUPRE-17", true, true, true, true, true, true, AplicaActualizacionOtDto.APLICA),
            fila("Fuentes de Financiamiento", "CUPRE-17", true, true, true, true, true, true, AplicaActualizacionOtDto.APLICA),
            fila("Presupuesto de O&M", "CUPRE-18", true, true, true, true, true, false, AplicaActualizacionOtDto.APLICA),
            fila("Productos del Proyecto", "CUPRE-23", true, true, true, true, true, false, AplicaActualizacionOtDto.APLICA),
            fila("Flujo de Beneficios", "CUPRE-20", true, true, true, true, false, false, AplicaActualizacionOtDto.APLICA),
            fila("Flujo de Caja e Indicadores", "CUPRE-21", true, true, true, true, false, false, AplicaActualizacionOtDto.APLICA),
            fila("Programación Financiera Preinversión", "CUPRE-22.1", true, true, true, false, false, true, AplicaActualizacionOtDto.NO_APLICA_AL_CU),
            fila("Programación Física Preinversión", "CUPRE-22.3", true, true, true, false, false, true, AplicaActualizacionOtDto.NO_APLICA_AL_CU),
            fila("Programación Financiera Inversión", "CUPRE-22.2", true, true, true, true, true, false, AplicaActualizacionOtDto.APLICA),
            fila("Programación Física Inversión", "CUPRE-22.4", true, true, true, true, true, false, AplicaActualizacionOtDto.NO_APLICA_AL_CU),
            fila("Nota de solicitud de OT", null, true, true, true, true, true, true, AplicaActualizacionOtDto.APLICA),
            fila("Documento de Preinversión", null, true, true, true, true, true, true, AplicaActualizacionOtDto.APLICA),
            fila("Otros documentos", null, true, true, true, true, true, true, AplicaActualizacionOtDto.APLICA),
            fila("Marco Lógico", null, false, false, false, false, true, false, AplicaActualizacionOtDto.SIN_DATO));

    private static ContenidoIniciativaResumenDto fila(String contenido, String ubicacionCasoUso,
            boolean perfil, boolean prefactibilidad, boolean factibilidad, boolean diseno, boolean programa,
            boolean estudioGeneral, AplicaActualizacionOtDto aplicaActualizacionOt) {
        return new ContenidoIniciativaResumenDto().contenido(contenido).ubicacionCasoUso(ubicacionCasoUso)
                .aplicaPerfil(perfil).aplicaPrefactibilidad(prefactibilidad).aplicaFactibilidad(factibilidad)
                .aplicaDiseno(diseno).aplicaPrograma(programa).aplicaEstudioGeneral(estudioGeneral)
                .aplicaActualizacionOt(aplicaActualizacionOt);
    }
}

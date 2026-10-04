package sv.gob.mh.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.api.dto.catalogo.CalificadorDto;
import sv.gob.mh.api.dto.catalogo.CambioEstadoDto;
import sv.gob.mh.api.dto.catalogo.CampoDefinicionDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDescriptoresDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDto;
import sv.gob.mh.api.dto.catalogo.EstadoDto;
import sv.gob.mh.api.dto.catalogo.TipoCampoDto;
import sv.gob.mh.api.dto.catalogo.VigenciaDto;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.NuevoCampo;
import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.enums.TipoCampo;

/** Traducción del contrato CU-ADM-01 en los casos que no cubren los escenarios BDD. */
class CatalogoApiMapperTest {

    @Test
    @DisplayName("Sin lista de campos no hay campos nuevos, y un CambioEstado ausente no trae estado ni fecha")
    void solicitudesSinDatosOpcionales() {
        LocalDate ayer = LocalDate.of(2026, 9, 29);

        assertThat(CatalogoApiMapper.aNuevosCampos(null)).isNull();
        assertThat(CatalogoApiMapper.estado(null)).isNull();
        assertThat(CatalogoApiMapper.fechaHasta(null)).isNull();
        CambioEstadoDto inactivar = new CambioEstadoDto().estado(EstadoDto.INACTIVE).hasta(ayer);
        assertThat(CatalogoApiMapper.estado(inactivar)).isEqualTo(EstadoVigencia.INACTIVE);
        assertThat(CatalogoApiMapper.fechaHasta(inactivar)).isEqualTo(ayer);
    }

    @Test
    @DisplayName("NUMERIC y FECHA del contrato son NUMBER y DATE en el dominio, ida y vuelta")
    void tiposDeCampoIdaYVuelta() {
        List<CampoDefinicionDto> campos = List.of(
                new CampoDefinicionDto("codPais", CalificadorDto.KEY, 1).tipo(TipoCampoDto.STRING).longitudMaxima(3),
                new CampoDefinicionDto("poblacion", CalificadorDto.FIELD, 2).tipo(TipoCampoDto.NUMERIC)
                        .minimo(BigDecimal.ZERO).maximo(BigDecimal.TEN),
                new CampoDefinicionDto("fundacion", CalificadorDto.FIELD, 3).tipo(TipoCampoDto.FECHA)
                        .fechaDesde(LocalDate.of(1000, 1, 1)).fechaHasta(LocalDate.of(2100, 12, 31)),
                new CampoDefinicionDto("continente", CalificadorDto.FIELD, 4).tipo(TipoCampoDto.ENUM)
                        .valores(List.of("AMERICA", "EUROPA")));

        List<NuevoCampo> nuevos = CatalogoApiMapper.aNuevosCampos(campos);
        CatalogoDto respuesta = CatalogoApiMapper
                .aCatalogo(Catalogo.nuevo("PAIS", "Países", null, null, null, null, nuevos));

        assertThat(nuevos).extracting(campo -> campo.definicion().tipo())
                .containsExactly(TipoCampo.STRING, TipoCampo.NUMBER, TipoCampo.DATE, TipoCampo.ENUM);
        assertThat(respuesta.getCampos()).extracting(campo -> campo.getTipo())
                .containsExactly(TipoCampoDto.STRING, TipoCampoDto.NUMERIC, TipoCampoDto.FECHA, TipoCampoDto.ENUM);
        assertThat(respuesta.getCampos().get(3).getValores()).containsExactly("AMERICA", "EUROPA");
    }

    @Test
    @DisplayName("RN-04, S-09: un campo sin tipo es STRING{80} y se ignoran sus demás parámetros de tipo")
    void campoSinTipoEsStringOchenta() {
        CampoDefinicionDto sinTipo = new CampoDefinicionDto("descripcion", CalificadorDto.FIELD, 2).longitudMaxima(5)
                .valores(List.of("A"));

        NuevoCampo nuevo = CatalogoApiMapper.aNuevosCampos(List.of(sinTipo)).get(0);

        assertThat(nuevo.definicion().notacion()).isEqualTo("STRING{80}");
        assertThat(nuevo.definicion().esValida(false)).isTrue();
    }

    @Test
    @DisplayName("E-05: los valores ENUM repetidos llegan al dominio para poder rechazarlos")
    void valoresEnumRepetidos() {
        CampoDefinicionDto campo = new CampoDefinicionDto("campo", CalificadorDto.FIELD, 2).tipo(TipoCampoDto.ENUM)
                .valores(List.of("A", "A"));

        assertThat(CatalogoApiMapper.aNuevosCampos(List.of(campo)).get(0).definicion().esValida(false)).isFalse();
    }

    @Test
    @DisplayName("SF-04 / SF-15: los descriptores se reemplazan, con la confirmación y los registros padre de S-04")
    void descriptoresDeReemplazo() {
        CatalogoDescriptoresDto request = new CatalogoDescriptoresDto("Departamentos", EstadoDto.ACTIVE)
                .padre("PAIS")
                .vigencia(new VigenciaDto().desde(LocalDate.of(2026, 1, 1)))
                .confirmarCambioPadre(true)
                .registrosPadre(Map.of("ANT", "COL"));

        CambioDescriptores cambio = CatalogoApiMapper.aCambioDescriptores(request);

        assertThat(cambio.codigo()).isNull();
        assertThat(cambio.nombre()).isEqualTo("Departamentos");
        assertThat(cambio.padre()).isEqualTo("PAIS");
        assertThat(cambio.estado()).isEqualTo(EstadoVigencia.ACTIVE);
        assertThat(cambio.fechaDesde()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(cambio.fechaHasta()).isNull();
        assertThat(cambio.confirmarCambioPadre()).isTrue();
        assertThat(cambio.registrosPadre()).containsEntry("ANT", "COL");
        assertThat(CatalogoApiMapper.aCambioDescriptores(new CatalogoDescriptoresDto("Países", EstadoDto.INACTIVE)
                .confirmarCambioPadre(null)).confirmarCambioPadre()).isFalse();
    }
}

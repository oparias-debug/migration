package sv.gob.mh.domain.model.catalogo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.domain.model.catalogo.CambioDescriptores.Descriptor;
import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.enums.TipoCampo;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/** Reglas del agregado {@link Catalogo} y de la vigencia (CU-ADM-01, Reglas 1-23). */
class CatalogoTest {

    private static final List<NuevoCampo> CAMPOS = List.of(new NuevoCampo("codigo", true),
            new NuevoCampo("descripcion", false));

    private static Catalogo catalogo(String padre) {
        return Catalogo.nuevo("PAISES", "Países", padre, null, null, null, CAMPOS);
    }

    @Test
    @DisplayName("Reglas 18, 2 y 3: al menos un campo, al menos un KEY y nombres únicos")
    void validaLosCamposDelCatalogo() {
        List<NuevoCampo> sinKey = List.of(new NuevoCampo("descripcion", false));
        List<NuevoCampo> repetidos = List.of(new NuevoCampo("codigo", true), new NuevoCampo("codigo", false));

        assertThatThrownBy(() -> Catalogo.validarCampos(null)).hasFieldOrPropertyWithValue("codigo",
                "CATALOGO_SIN_CAMPOS");
        assertThatThrownBy(() -> Catalogo.validarCampos(List.of())).hasFieldOrPropertyWithValue("codigo",
                "CATALOGO_SIN_CAMPOS");
        assertThatThrownBy(() -> Catalogo.validarCampos(sinKey)).hasFieldOrPropertyWithValue("codigo",
                "CATALOGO_SIN_CAMPO_KEY");
        assertThatThrownBy(() -> Catalogo.validarCampos(repetidos)).hasFieldOrPropertyWithValue("codigo",
                "NOMBRE_CAMPO_DUPLICADO");
    }

    @Test
    @DisplayName("Reglas 13 y 14: nace INACTIVE si se pide así o si su toDate ya llegó")
    void elEstadoInicialRespetaLaSolicitudYLaFechaHasta() {
        LocalDate ayer = Vigencia.hoy().minusDays(1);

        assertThat(catalogo(null).getEstado()).isEqualTo(EstadoVigencia.ACTIVE);
        assertThat(Catalogo.nuevo("A", "A", null, EstadoVigencia.INACTIVE, null, null, CAMPOS).getEstado())
                .isEqualTo(EstadoVigencia.INACTIVE);
        assertThat(Catalogo.nuevo("B", "B", null, null, null, ayer, CAMPOS).estadoEfectivo())
                .isEqualTo(EstadoVigencia.INACTIVE);
        // Guardado ACTIVE pero con la toDate ya vencida: hoy es INACTIVE (Regla 14).
        assertThat(new Catalogo(3L, "C", "C", null, new Periodo(EstadoVigencia.ACTIVE, null, ayer), List.of())
                .estadoEfectivo()).isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    @DisplayName("Regla 14: una toDate vencida al actualizar deja el catálogo INACTIVE aunque se pida ACTIVE")
    void actualizarConFechaVencidaInactiva() {
        Catalogo catalogo = catalogo(null);
        LocalDate hoy = Vigencia.hoy();

        catalogo.actualizarDescriptores(new CambioDescriptores(EnumSet.of(Descriptor.NOMBRE, Descriptor.PADRE,
                Descriptor.FECHA_DESDE, Descriptor.FECHA_HASTA), "Naciones", "REGIONES", EstadoVigencia.ACTIVE,
                hoy.minusDays(10), hoy));

        assertThat(catalogo.getNombre()).isEqualTo("Naciones");
        assertThat(catalogo.getCatalogoPadreCodigo()).isEqualTo("REGIONES");
        assertThat(catalogo.getFechaDesde()).isEqualTo(hoy.minusDays(10));
        assertThat(catalogo.getEstado()).isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    @DisplayName("Un cambio de descriptores sin ninguno informado, o con nombre nulo, es inválido")
    void validaElCambioDeDescriptores() {
        CambioDescriptores vacio = new CambioDescriptores(Set.of(), null, null, null, null, null);
        CambioDescriptores nombreNulo = new CambioDescriptores(Set.of(Descriptor.NOMBRE), null, null, null, null,
                null);
        CambioDescriptores valido = new CambioDescriptores(Set.of(Descriptor.NOMBRE), "Nuevo", null, null, null,
                null);

        assertThatThrownBy(vacio::validar).isInstanceOf(ErrorCatalogoException.class);
        assertThatThrownBy(nombreNulo::validar).isInstanceOf(ErrorCatalogoException.class);
        valido.validar();
        assertThat(valido.informa(Descriptor.NOMBRE)).isTrue();
    }

    @Test
    @DisplayName("Regla 9: no se inactiva con una fecha futura")
    void noSeInactivaConFechaFutura() {
        Catalogo catalogo = catalogo(null);
        LocalDate manana = Vigencia.hoy().plusDays(1);

        assertThatThrownBy(() -> catalogo.inactivar(manana)).hasFieldOrPropertyWithValue("codigo",
                "FECHA_INACTIVACION_FUTURA");
        catalogo.inactivar(null);
        assertThat(catalogo.getFechaHasta()).isEqualTo(Vigencia.hoy());
        assertThat(catalogo.getEstado()).isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    @DisplayName("Reglas 8 y 23: el registro padre se exige solo en un catálogo con padre")
    void elRegistroPadreDependeDelCatalogoPadre() {
        Catalogo sinPadre = catalogo(null);
        Catalogo conPadre = catalogo("REGIONES");

        assertThat(sinPadre.catalogoDelRegistroPadre(null)).isEmpty();
        assertThatThrownBy(() -> sinPadre.catalogoDelRegistroPadre("CA")).hasFieldOrPropertyWithValue("codigo",
                "REGISTRO_PADRE_INEXISTENTE");
        assertThatThrownBy(() -> conPadre.catalogoDelRegistroPadre(null)).hasFieldOrPropertyWithValue("codigo",
                "REGISTRO_PADRE_REQUERIDO");
        assertThatThrownBy(() -> conPadre.catalogoDelRegistroPadre(" ")).hasFieldOrPropertyWithValue("codigo",
                "REGISTRO_PADRE_REQUERIDO");
        assertThat(conPadre.catalogoDelRegistroPadre("CA")).contains("REGIONES");
    }

    @Test
    @DisplayName("Reglas 1, 4 y 5: valores completos, campos existentes y proyección por defecto")
    void valoresYCamposProyectados() {
        Catalogo catalogo = catalogo(null);
        List<ValorCampo> soloKey = List.of(new ValorCampo("codigo", "SV"));
        List<String> inexistente = List.of("capital");

        assertThatThrownBy(() -> catalogo.valoresCompletos(soloKey)).hasFieldOrPropertyWithValue("codigo",
                "VALOR_CAMPO_FALTANTE");
        assertThat(catalogo.camposProyectados(null)).extracting(CampoDefinicion::getNombre)
                .containsExactly("descripcion");
        assertThat(catalogo.camposProyectados(List.of(" codigo ", ""))).extracting(CampoDefinicion::getNombre)
                .containsExactly("codigo");
        assertThatThrownBy(() -> catalogo.camposProyectados(inexistente)).hasFieldOrPropertyWithValue("codigo",
                "CAMPO_INEXISTENTE");
    }

    @Test
    @DisplayName("Un catálogo reconstituido sin campo KEY no tiene clave para sus registros")
    void sinCampoKeyNoHayClave() {
        Catalogo sinKey = new Catalogo(1L, "X", "X", null, new Periodo(EstadoVigencia.ACTIVE, null, null),
                List.of(new CampoDefinicion(2L, "descripcion", TipoCampo.STRING, false, 1)));

        assertThatThrownBy(sinKey::campoKey).isInstanceOf(IllegalStateException.class);
        assertThat(sinKey.getCampos()).hasSize(1);
        assertThat(sinKey.getId()).isEqualTo(1L);
    }
}

package sv.gob.mh.domain.model.catalogo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/** Reglas del agregado {@link Catalogo}, de sus registros y de la vigencia (CU-ADM-01). */
class CatalogoTest {

    private static final NuevoCampo KEY = new NuevoCampo("codPais", true, 1, DefinicionTipo.texto(3));
    private static final NuevoCampo NOMBRE = new NuevoCampo("nombre", false, 2, DefinicionTipo.texto(60));
    private static final NuevoCampo POBLACION = new NuevoCampo("poblacion", false, 3,
            DefinicionTipo.numerico(BigDecimal.ZERO, new BigDecimal("2000000000")));
    private static final List<NuevoCampo> CAMPOS = List.of(KEY, NOMBRE, POBLACION);

    private static Catalogo catalogo(String padre) {
        return Catalogo.nuevo("PAIS", "Países", padre, null, null, null, CAMPOS);
    }

    private static String codigoDe(Runnable accion) {
        try {
            accion.run();
        } catch (ErrorCatalogoException ex) {
            return ex.getCodigo();
        }
        return null;
    }

    @Test
    @DisplayName("RN-20, RN-03, RN-04 y S-05: campos, KEY único, nombres y posiciones")
    void validaLosCamposDelCatalogo() {
        NuevoCampo otroKey = new NuevoCampo("codIso", true, 2, DefinicionTipo.texto(2));
        NuevoCampo sinKey = new NuevoCampo("codPais", false, 1, DefinicionTipo.texto(3));
        NuevoCampo nombreRepetido = new NuevoCampo("nombre", false, 3, DefinicionTipo.texto(80));
        NuevoCampo posicionRepetida = new NuevoCampo("otro", false, 2, DefinicionTipo.texto(10));
        NuevoCampo posicionCero = new NuevoCampo("otro", false, 0, DefinicionTipo.texto(10));

        assertThat(codigoDe(() -> Catalogo.validarCampos(null))).isEqualTo("E-02");
        assertThat(codigoDe(() -> Catalogo.validarCampos(List.of(sinKey, NOMBRE)))).isEqualTo("E-03");
        assertThat(codigoDe(() -> Catalogo.validarCampos(List.of(KEY, otroKey)))).isEqualTo("E-23");
        assertThat(codigoDe(() -> Catalogo.validarCampos(List.of(KEY, NOMBRE, nombreRepetido))))
                .isEqualTo("E-04");
        assertThat(codigoDe(() -> Catalogo.validarCampos(List.of(KEY, NOMBRE, posicionRepetida))))
                .isEqualTo("S-05");
        assertThat(codigoDe(() -> Catalogo.validarCampos(List.of(KEY, posicionCero)))).isEqualTo("S-05");
        assertThat(codigoDe(() -> Catalogo.validarCampos(CAMPOS))).isNull();
    }

    @Test
    @DisplayName("RN-04 (E-05): la restricción debe ser la de su tipo y consistente")
    void validaLaDefinicionDeTipo() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 12, 31);

        assertThat(DefinicionTipo.numerico(BigDecimal.TEN, BigDecimal.ONE).esValida(false)).isFalse();
        assertThat(DefinicionTipo.texto(0).esValida(false)).isFalse();
        assertThat(DefinicionTipo.texto(256).esValida(true)).isFalse();
        assertThat(DefinicionTipo.texto(256).esValida(false)).isTrue();
        assertThat(DefinicionTipo.fecha(fin, inicio).esValida(false)).isFalse();
        assertThat(DefinicionTipo.enumerado(List.of()).esValida(false)).isFalse();
        assertThat(DefinicionTipo.enumerado(List.of("A", "A")).esValida(false)).isFalse();
        assertThat(DefinicionTipo.enumerado(List.of("A", "B")).esValida(false)).isTrue();
        assertThat(DefinicionTipo.porDefecto().notacion()).as("RN-04, S-09").isEqualTo("STRING{80}");
        assertThat(new DefinicionTipo(sv.gob.mh.shared.enums.TipoCampo.STRING, BigDecimal.ONE, null, 10, null, null,
                null).esValida(false)).as("parámetros de otro tipo").isFalse();
        assertThat(codigoDe(() -> Catalogo.validarCampos(List.of(KEY,
                new NuevoCampo("campo", false, 2, DefinicionTipo.texto(0))))))
                .isEqualTo("E-05");
    }

    @Test
    @DisplayName("RN-01, RN-11 (E-14): el valor cumple el tipo y su restricción, y se notifica en notación del CU")
    void validaLosValores() {
        DefinicionTipo poblacion = POBLACION.definicion();
        DefinicionTipo fundacion = DefinicionTipo.fecha(LocalDate.of(1000, 1, 1), LocalDate.of(2100, 12, 31));
        DefinicionTipo continente = DefinicionTipo.enumerado(List.of("AMERICA", "EUROPA"));

        assertThat(poblacion.admite("52000000")).isTrue();
        assertThat(poblacion.admite("-5")).isFalse();
        assertThat(poblacion.admite("muchos")).isFalse();
        assertThat(fundacion.admite("1810-07-20")).isTrue();
        assertThat(fundacion.admite("0999-12-31")).isFalse();
        assertThat(fundacion.admite("20/07/1810")).isFalse();
        assertThat(continente.admite("ASIA")).isFalse();
        assertThat(KEY.definicion().admite("COLO")).isFalse();
        assertThat(KEY.definicion().admite(null)).isFalse();

        assertThat(poblacion.notacion()).isEqualTo("NUMERIC{0:2000000000}");
        assertThat(fundacion.notacion()).isEqualTo("FECHA{1000-01-01:2100-12-31}");
        assertThat(continente.notacion()).isEqualTo("ENUM{AMERICA,EUROPA}");
        assertThat(KEY.definicion().notacion()).isEqualTo("STRING{3}");
    }

    @Test
    @DisplayName("RN-15, RN-16 y RN-12a: estado inicial según la solicitud y la TO DATE")
    void elEstadoInicialRespetaLaSolicitudYLaFechaHasta() {
        LocalDate ayer = Vigencia.hoy().minusDays(1);

        assertThat(catalogo(null).getEstado()).isEqualTo(EstadoVigencia.ACTIVE);
        Catalogo inactivo = Catalogo.nuevo("A", "A", null, EstadoVigencia.INACTIVE, null, null, CAMPOS);
        assertThat(inactivo.getEstado()).isEqualTo(EstadoVigencia.INACTIVE);
        assertThat(inactivo.getFechaHasta()).isEqualTo(Vigencia.hoy());
        assertThat(Catalogo.nuevo("B", "B", null, EstadoVigencia.ACTIVE, null, ayer, CAMPOS).getEstado())
                .isEqualTo(EstadoVigencia.INACTIVE);
        assertThat(codigoDe(() -> Catalogo.nuevo("C", "C", null, null, Vigencia.hoy(), ayer, CAMPOS)))
                .isEqualTo("E-09");
        // Guardado ACTIVE pero con la TO DATE ya vencida: hoy es INACTIVE (RN-16).
        assertThat(new Catalogo(3L, "D", "D", null, null, new Periodo(EstadoVigencia.ACTIVE, null, ayer), List.of())
                .estadoEfectivo()).isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    @DisplayName("RN-12: la inactivación conserva una TO DATE vencida y si no fija hoy; RN-06: la cascada respeta una TO DATE anterior")
    void inactivacion() {
        Catalogo catalogo = catalogo(null);
        Catalogo conFechaPasada = catalogo(null);
        LocalDate hoy = Vigencia.hoy();

        catalogo.inactivar(hoy.plusDays(1));
        assertThat(catalogo.getFechaHasta()).isEqualTo(hoy);
        assertThat(catalogo.estaActivo()).isFalse();
        conFechaPasada.inactivar(hoy.minusDays(3));
        assertThat(conFechaPasada.getFechaHasta()).isEqualTo(hoy.minusDays(3));

        Catalogo hijo = new Catalogo(2L, "DEPTO", "Departamentos", "PAIS", null,
                new Periodo(EstadoVigencia.ACTIVE, null, hoy.minusDays(5)), List.of());
        hijo.inactivarEnCascada(hoy);
        assertThat(hijo.getFechaHasta()).isEqualTo(hoy.minusDays(5));
        assertThat(hijo.getEstado()).isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    @DisplayName("SF-06 (E-16, S-02): se reactiva con la TO DATE vacía o futura")
    void reactivacion() {
        Catalogo catalogo = catalogo(null);
        catalogo.inactivar(null);

        assertThat(codigoDe(() -> catalogo.reactivar(null, Vigencia.hoy().minusDays(1))))
                .isEqualTo("E-16");
        catalogo.reactivar(null, null);
        assertThat(catalogo.estaActivo()).isTrue();
        assertThat(catalogo.getFechaHasta()).isNull();
    }

    @Test
    @DisplayName("RN-19 (E-11): el código enviado, si viene, debe ser el del catálogo")
    void validaElCambioDeDescriptores() {
        CambioDescriptores otroCodigo = new CambioDescriptores("PAISES", "Países", null, EstadoVigencia.ACTIVE, null,
                null, false, null);
        CambioDescriptores mismoCodigo = new CambioDescriptores("PAIS", "Países", null, EstadoVigencia.ACTIVE, null,
                null, false, null);
        CambioDescriptores sinCodigo = new CambioDescriptores(null, "Países", null, EstadoVigencia.ACTIVE, null, null,
                false, Map.of("ANT", "COL"));

        assertThat(codigoDe(() -> otroCodigo.validar("PAIS"))).isEqualTo("E-11");
        assertThat(codigoDe(() -> mismoCodigo.validar("PAIS"))).isNull();
        assertThat(codigoDe(() -> sinCodigo.validar("PAIS"))).isNull();
        assertThat(mismoCodigo.registrosPadre()).isEmpty();
        assertThat(sinCodigo.registrosPadre()).containsEntry("ANT", "COL");
    }

    @Test
    @DisplayName("RN-05 (E-18): el registro padre se exige solo en un catálogo con padre")
    void elRegistroPadreDependeDelCatalogoPadre() {
        Catalogo plano = catalogo(null);
        Catalogo hijo = catalogo("REGION");

        assertThat(plano.catalogoDelRegistroPadre(null)).isEmpty();
        assertThat(codigoDe(() -> plano.catalogoDelRegistroPadre("CA"))).isEqualTo("E-18");
        assertThat(codigoDe(() -> hijo.catalogoDelRegistroPadre(null))).isEqualTo("E-18");
        assertThat(hijo.catalogoDelRegistroPadre("CA")).contains("REGION");
    }

    @Test
    @DisplayName("SF-07: valores completos, de campos definidos y válidos")
    void valoresParaRegistroNuevo() {
        Catalogo catalogo = catalogo(null);
        List<ValorCampo> soloKey = List.of(new ValorCampo("codPais", "COL"));
        List<ValorCampo> conAdicional = List.of(new ValorCampo("codPais", "COL"), new ValorCampo("moneda", "COP"));
        List<ValorCampo> invalido = List.of(new ValorCampo("codPais", "COL"), new ValorCampo("nombre", "Colombia"),
                new ValorCampo("poblacion", "-5"));
        List<ValorCampo> validos = List.of(new ValorCampo("poblacion", "52000000"), new ValorCampo("codPais", "COL"),
                new ValorCampo("nombre", "Colombia"));

        assertThat(codigoDe(() -> catalogo.valoresParaRegistroNuevo(soloKey))).isEqualTo("E-14");
        assertThat(codigoDe(() -> catalogo.valoresParaRegistroNuevo(conAdicional))).isEqualTo("E-21");
        assertThatThrownBy(() -> catalogo.valoresParaRegistroNuevo(invalido))
                .hasMessage("El valor -5 no es válido para el campo poblacion (NUMERIC{0:2000000000}).");
        assertThat(catalogo.valoresParaRegistroNuevo(validos).keySet()).containsExactly("codPais", "nombre",
                "poblacion");
    }

    @Test
    @DisplayName("Algoritmo 4.1: Field Set por defecto, con KEY antepuesto o tal cual")
    void conjuntoDeCampos() {
        Catalogo catalogo = catalogo(null);

        assertThat(catalogo.conjuntoDeCampos(null)).extracting(CampoDefinicion::getNombre)
                .containsExactly("codPais", "nombre");
        assertThat(catalogo.conjuntoDeCampos(List.of("poblacion"))).extracting(CampoDefinicion::getNombre)
                .containsExactly("codPais", "poblacion");
        assertThat(catalogo.conjuntoDeCampos(List.of("poblacion", " codPais "))).extracting(CampoDefinicion::getNombre)
                .containsExactly("poblacion", "codPais");
        assertThatThrownBy(() -> catalogo.conjuntoDeCampos(List.of("moneda")))
                .hasMessage("El campo moneda no está definido en el catálogo PAIS.");
    }

    @Test
    @DisplayName("SF-08 (E-21, E-19, E-14): actualización de campos no KEY")
    void actualizacionDeRegistro() {
        Catalogo catalogo = catalogo(null);
        Registro registro = Registro.nuevo(catalogo,
                Map.of("codPais", "COL", "nombre", "Colombia", "poblacion", "52000000"), null, null, null);
        List<ValorCampo> cambiaKey = List.of(new ValorCampo("codPais", "CO"));
        List<ValorCampo> inexistente = List.of(new ValorCampo("moneda", "COP"));
        List<ValorCampo> invalido = List.of(new ValorCampo("poblacion", "-1"));

        assertThat(codigoDe(() -> registro.actualizarValores(cambiaKey))).isEqualTo("E-19");
        assertThat(codigoDe(() -> registro.actualizarValores(inexistente))).isEqualTo("E-21");
        assertThat(codigoDe(() -> registro.actualizarValores(invalido))).isEqualTo("E-14");
        registro.actualizarValores(List.of(new ValorCampo("nombre", "República de Colombia")));
        assertThat(registro.getValores()).containsEntry("nombre", "República de Colombia");
        assertThat(registro.getClave()).isEqualTo("COL");
    }

    @Test
    @DisplayName("RN-06: un registro de un catálogo inactivo es inactivo")
    void estadoEfectivoDelRegistro() {
        Catalogo catalogo = catalogo(null);
        Registro registro = Registro.nuevo(catalogo,
                Map.of("codPais", "COL", "nombre", "Colombia", "poblacion", "1"), null, null, null);

        assertThat(registro.estaActivo()).isTrue();
        catalogo.inactivar(null);
        assertThat(registro.estadoEfectivo()).isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    @DisplayName("Un catálogo reconstituido sin campo KEY no tiene clave para sus registros")
    void sinCampoKeyNoHayClave() {
        Catalogo sinKey = new Catalogo(1L, "X", "X", null, null, new Periodo(EstadoVigencia.ACTIVE, null, null),
                List.of(new CampoDefinicion(2L, "descripcion", false, 1, DefinicionTipo.texto(10))));

        assertThatThrownBy(sinKey::campoKey).isInstanceOf(IllegalStateException.class);
        assertThat(sinKey.getCampos()).hasSize(1);
        assertThat(sinKey.getId()).isEqualTo(1L);
    }
}

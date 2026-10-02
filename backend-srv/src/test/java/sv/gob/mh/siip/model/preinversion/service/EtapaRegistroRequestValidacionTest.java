package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import sv.gob.mh.siip.model.preinversion.dto.EtapaRegistroRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;

/** Validación de formato de las fechas de etapa que aplica el contrato CU-PRE-03.5 (RN04/RN19). */
class EtapaRegistroRequestValidacionTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void fechasVacias_seAceptanComoSinFecha() {
        EtapaRegistroRequestDto item = new EtapaRegistroRequestDto().nombreEtapa(NombreEtapaDto.PERFIL)
                .fechaInicio("").fechaFin("");

        assertThat(validator.validate(item)).isEmpty();
    }

    @Test
    void fechasEnFormatoDdMmAaaa_seAceptan() {
        EtapaRegistroRequestDto item = new EtapaRegistroRequestDto().nombreEtapa(NombreEtapaDto.PERFIL)
                .fechaInicio("01/01/2026").fechaFin("31/12/2026");

        assertThat(validator.validate(item)).isEmpty();
    }

    @Test
    void fechasEnOtroFormato_seRechazan() {
        EtapaRegistroRequestDto item = new EtapaRegistroRequestDto().nombreEtapa(NombreEtapaDto.PERFIL)
                .fechaInicio("2026-01-01").fechaFin(" ");

        assertThat(validator.validate(item)).hasSize(2);
    }
}

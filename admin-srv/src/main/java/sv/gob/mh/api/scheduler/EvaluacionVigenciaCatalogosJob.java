package sv.gob.mh.api.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sv.gob.mh.application.handler.catalogo.EvaluarVigenciaHandler;

/**
 * HU-ADM-01-16 (SF-14): disparador de la evaluación diaria de la vigencia de catálogos y
 * registros, el único punto de entrada de CU-ADM-01 que no es HTTP. Por defecto corre a las 00:05
 * de El Salvador; {@code catalogos.evaluacion-vigencia.cron} la cambia y el valor {@code "-"} la
 * desactiva.
 */
@Component
public class EvaluacionVigenciaCatalogosJob {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluacionVigenciaCatalogosJob.class);

    private final EvaluarVigenciaHandler evaluarVigencia;

    public EvaluacionVigenciaCatalogosJob(EvaluarVigenciaHandler evaluarVigencia) {
        this.evaluarVigencia = evaluarVigencia;
    }

    @Scheduled(cron = "${catalogos.evaluacion-vigencia.cron:0 5 0 * * *}", zone = "America/El_Salvador")
    public void evaluar() {
        var resultado = evaluarVigencia.ejecutar();
        LOG.info("Evaluación de vigencia de catálogos: {} catálogos y {} registros inactivados por TO DATE vencida",
                resultado.catalogosInactivados(), resultado.registrosInactivados());
    }
}

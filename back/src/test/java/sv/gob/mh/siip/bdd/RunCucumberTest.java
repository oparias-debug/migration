package sv.gob.mh.siip.bdd;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Punto de entrada de las pruebas BDD: le indica a JUnit Platform que use el
 * motor "cucumber" para ejecutar los .feature de src/test/resources/features,
 * con los step definitions (glue) del paquete sv.gob.mh.siip.bdd.
 *
 * Para agregar un nuevo caso BDD: crear un .feature en
 * src/test/resources/features y su clase de step definitions en
 * sv.gob.mh.siip.bdd.steps — no hace falta tocar esta clase.
 *
 * Desde Cucumber 6+ ya no existe el modo "no estricto": un paso undefined o
 * pending SIEMPRE hace fallar el build (no solo se reporta). Para poder
 * escribir un .feature como especificación antes de implementar sus steps,
 * etiquetarlo (a nivel de Feature o de cada Escenario) con @wip — este filtro
 * excluye esos escenarios de la ejecución hasta que se les quite el tag.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "sv.gob.mh.siip.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @wip")
public class RunCucumberTest {
}

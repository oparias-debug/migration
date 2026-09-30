package sv.gob.mh.bdd;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Punto de entrada de las pruebas BDD: ejecuta con el motor "cucumber" de JUnit Platform los
 * .feature de src/test/resources/features, con los steps (glue) del paquete sv.gob.mh.bdd. Corre
 * con {@code mvn test}, junto con las unitarias; las pruebas Karate de src/test/pruebas siguen
 * aparte, en sus perfiles.
 *
 * <p>Un paso undefined o pending hace fallar el build: para escribir un .feature antes de sus
 * steps, etiquetarlo con {@code @wip} y este filtro lo excluye hasta quitarle el tag. El filtro va
 * aquí y no en cucumber.properties, que el motor de JUnit Platform no lee.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "sv.gob.mh.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "summary")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @wip")
public class RunCucumberTest {
}

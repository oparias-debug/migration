package sv.gob.mh.siip.bdd;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Ejecutor aislado de CU-PRE-22.1.
 *
 * <p>Evita que la validación de este caso dependa de escenarios de otros CU y
 * permite reproducir localmente su contrato BDD contra H2.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/pre")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "sv.gob.mh.siip.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@CU-PRE-22.1")
public class RunCuPre221CucumberIT {
}

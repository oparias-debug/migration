package sv.gob.mh.siip.bdd;

import org.junit.platform.suite.api.*;

/** Ejecución aislada del contrato entregado, sin alterar el runner global. */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/CU-PRE-02-acceder-caso-asignado.feature")
@SelectClasspathResource("features/CU-PRE-02-archivar-solicitud.feature")
@SelectClasspathResource("features/CU-PRE-02-asignar-solicitud.feature")
@SelectClasspathResource("features/CU-PRE-02-consultar-solicitudes-activas.feature")
@ConfigurationParameter(key = "cucumber.glue", value = "sv.gob.mh.siip.bdd")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty")
public class CuPre02BddTest { }

package sv.gob.mh.siip.config;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowableStartupLogger {

	private static final Logger logger = LoggerFactory.getLogger(FlowableStartupLogger.class);

	@Bean
	public CommandLineRunner init(final RepositoryService repositoryService,
			final RuntimeService runtimeService,
			final TaskService taskService) {

		return strings -> {
			logger.info("Number of process definitions : {}",repositoryService.createProcessDefinitionQuery().count());
			logger.info("Number of tasks : {}", taskService.createTaskQuery().count());
		};
	}
}

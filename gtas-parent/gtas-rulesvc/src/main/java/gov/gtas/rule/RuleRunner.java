package gov.gtas.rule;

import static gov.gtas.constant.CommonErrorConstants.RULE_ENGINE_RUNNER_ERROR_CODE;
import static gov.gtas.constant.CommonErrorConstants.RULE_ENGINE_RUNNER_ERROR_MESSAGE;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.config.RuleRunnerConfig;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.svc.TargetingService;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * A Java application for running the Rule Engine in stand alone mode.
 *
 */
public class RuleRunner {

	private static final Logger logger = LoggerFactory
			.getLogger(RuleRunner.class);

	public static void main(String[] args) {
		logger.info("Entering main().");
		ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(
				CommonServicesConfig.class, RuleServiceConfig.class,
				RuleRunnerConfig.class);
		TargetingService targetingService = (TargetingService) ctx
				.getBean("targetingServiceImpl");

		try {
			Set<Long> uniqueFlights = targetingService.runningRuleEngine();
			if (logger.isInfoEnabled()) {
				logger.info("updating hit counts for flight ids " + uniqueFlights);
			}		
			targetingService.updateFlightHitCounts(uniqueFlights);
			logger.info("Exiting main().");
		} catch (Exception exception) {
			exception.printStackTrace();
			ErrorDetailInfo errInfo = ErrorHandlerFactory.createErrorDetails(
					RULE_ENGINE_RUNNER_ERROR_CODE,
					RULE_ENGINE_RUNNER_ERROR_MESSAGE, exception);
			ErrorPersistenceService errorService = (ErrorPersistenceService) ctx
					.getBean("errorPersistenceServiceImpl");
			errorService.create(errInfo);
		} finally {
			ctx.close();
		}
		System.exit(0);
	}
}

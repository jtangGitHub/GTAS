package gov.gtas.rule;

import gov.gtas.bo.RuleExecutionStatistics;

import java.util.List;
/**
 * Interface definition for rule engine execution result objects.
 * @author GTAS3 (AB)
 *
 */
public interface RuleServiceResult {
	/**
	 * Gets the list of Passenger IDs "hit" by the rules.
	 * @return the list of hits.
	 */
  List<?> getResultList();
  /**
   * Gets the statistics of the rule engine execution.
   * @return rule engine execution statistics.
   */
  RuleExecutionStatistics getExecutionStatistics();
}

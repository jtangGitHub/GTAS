package gov.gtas.svc.util;

import static gov.gtas.rule.builder.RuleTemplateConstants.NEW_LINE;
import gov.gtas.model.udr.enumtype.OperatorCodeEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.rule.builder.RuleConditionBuilder;
import gov.gtas.rule.builder.RuleTemplateConstants;
import gov.gtas.rule.builder.util.RuleVariablesUtil;

import java.util.List;

/**
 * Helper class for the UDR service.
 * 
 * @author GTAS3 (AB)
 *
 */
public class WatchlistRuleCreationUtil {
	public static List<String> createWatchlistRule(WatchlistTerm[] wlData,
			String title, StringBuilder ruleOutput) {
		RuleConditionBuilder ruleConditionBuilder = new RuleConditionBuilder(
				RuleVariablesUtil.createEngineRuleVariableMap());

		ruleOutput.append("rule \"").append(title).append(":%d\"")
				.append(NEW_LINE).append("when\n");
		for (WatchlistTerm wlterm : wlData) {
			QueryTerm trm = new QueryTerm(wlterm.getEntity(),
					wlterm.getField(), wlterm.getType(),
					OperatorCodeEnum.EQUAL.toString(), new String[]{wlterm.getValue()});
			ruleConditionBuilder.addRuleCondition(trm);
		}
		ruleConditionBuilder.buildConditionsAndApppend(ruleOutput);
		List<String> causes = ruleConditionBuilder.addWatchlistRuleAction(ruleOutput,
				title, RuleTemplateConstants.PASSENGER_VARIABLE_NAME);

		return causes;
	}

}
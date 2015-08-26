package gov.gtas.rule.builder;

import static gov.gtas.rule.builder.RuleTemplateConstants.GLOBAL_RESULT_DECLARATION;
import static gov.gtas.rule.builder.RuleTemplateConstants.IMPORT_PREFIX;
import static gov.gtas.rule.builder.RuleTemplateConstants.NEW_LINE;
import static gov.gtas.rule.builder.RuleTemplateConstants.RULE_PACKAGE_NAME;
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.match.PnrAddressLink;
import gov.gtas.bo.match.PnrCreditCardLink;
import gov.gtas.bo.match.PnrEmailLink;
import gov.gtas.bo.match.PnrFrequentFlyerLink;
import gov.gtas.bo.match.PnrPassengerLink;
import gov.gtas.bo.match.PnrPhoneLink;
import gov.gtas.bo.match.PnrTravelAgencyLink;
import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Document;
import gov.gtas.model.Email;
import gov.gtas.model.Flight;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Passenger;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A builder pattern class for constructing a Drools rule "file" (actually a
 * text string) from one or more UDR objects. This DRL string is then compiled
 * into a Knowledge Base (KieBase object).
 * 
 * @author GTAS3 (AB)
 *
 */
public class DrlRuleFileBuilder {
	/*
	 * The logger for the DrlRuleBuilder.
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(DrlRuleFileBuilder.class);

	private static final Class<?>[] IMPORT_LIST = { Flight.class,
			Passenger.class, Document.class, Pnr.class, Address.class,
			Phone.class, Email.class, FrequentFlyer.class, CreditCard.class,
			Agency.class, PnrAddressLink.class, PnrCreditCardLink.class,
			PnrEmailLink.class, PnrFrequentFlyerLink.class,
			PnrPassengerLink.class, PnrPhoneLink.class,
			PnrTravelAgencyLink.class};

	private StringBuilder stringBuilder;

	public DrlRuleFileBuilder() {
		this.stringBuilder = new StringBuilder();
		addPackageAndImport();
		// add the global result declaration;
		this.stringBuilder.append(GLOBAL_RESULT_DECLARATION);
	}

	public DrlRuleFileBuilder addRule(final UdrRule udrRule) {
		logger.info("DrlRuleFileBuilder - generating DRL code for UDR with title:"
				+ udrRule.getTitle());
		for (Rule rule : udrRule.getEngineRules()) {
			String drl = String.format(rule.getRuleDrl(), udrRule.getId(),
					rule.getId());
			this.stringBuilder.append(drl).append(StringUtils.LF);
		}
		return this;
	}

	public String build() {
		return this.stringBuilder.toString();
	}

	private void addPackageAndImport() {
		this.stringBuilder.append(RULE_PACKAGE_NAME)
		        .append(IMPORT_PREFIX)
				.append(RuleHitDetail.class.getName())
				.append(";").append(NEW_LINE);
		for (Class<?> clazz : IMPORT_LIST) {
			this.stringBuilder.append(IMPORT_PREFIX).append(clazz.getName())
					.append(";").append(NEW_LINE);
		}
	}
}

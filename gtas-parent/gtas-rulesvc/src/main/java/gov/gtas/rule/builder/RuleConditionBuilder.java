package gov.gtas.rule.builder;

import gov.gtas.bo.RuleHitDetail;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.error.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.enumtype.OperatorCodeEnum;
import gov.gtas.model.udr.json.QueryTerm;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Generates the "when" part of a DRL rule.
 * 
 * @author GTAS3 (AB)
 */
public class RuleConditionBuilder {

	private PassengerConditionBuilder passengerConditionBuilder;
	private DocumentConditionBuilder documentConditionBuilder;
	private FlightConditionBuilder flightConditionBuilder;

	private String passengerVariableName;
	private String flightVariableName;

	private StringBuilder conditionDescriptionBuilder;
	
	private boolean flightCriteriaPresent;

	// private List<String> causeList;

	/**
	 * Constructor for the Simple Rules:<br>
	 * (i.e., One Passenger, one document, one flight.)
	 * 
	 */
	public RuleConditionBuilder(final String passengerVariableName,
			final String flightVariableName, final String documentVariableName) {
		
		this.passengerVariableName = passengerVariableName;
		this.flightVariableName = flightVariableName;
		
		this.passengerConditionBuilder = new PassengerConditionBuilder(
				passengerVariableName);
		this.documentConditionBuilder = new DocumentConditionBuilder(
				documentVariableName, passengerVariableName);
		this.flightConditionBuilder = new FlightConditionBuilder(
				flightVariableName, passengerVariableName);

		// this.causeList = new LinkedList<String>();
	}

	/**
	 * @return the flightCriteriaPresent
	 */
	public boolean isFlightCriteriaPresent() {
		return flightCriteriaPresent;
	}

	/**
	 * Appends the generated "when" part (i.e., the LHS) of the rule to the rule
	 * document.
	 * 
	 * @param parentStringBuilder
	 *            the rule document builder.
	 * @throws ParseException
	 *             if the UDR has invalid formatting.
	 */
	public void buildConditionsAndApppend(
			final StringBuilder parentStringBuilder) {

		generatePassengerLink();

		parentStringBuilder.append(documentConditionBuilder.build());
		parentStringBuilder.append(passengerConditionBuilder.build());
		parentStringBuilder.append(flightConditionBuilder.build());
		passengerConditionBuilder.reset();
		documentConditionBuilder.reset();
		flightConditionBuilder.reset();

	}

	/**
	 * Creates linking passenger criteria for documents and flights.
	 * 
	 */
	private void generatePassengerLink() {
		if (!documentConditionBuilder.isEmpty()) {
			// add a link condition to the passenger builder.
			passengerConditionBuilder
					.addLinkByIdCondition(documentConditionBuilder
							.getPassengerIdLinkExpression());
		}

		// if there are passenger conditions then add a link to
		// the Flight builder
		if (!passengerConditionBuilder.isEmpty()) {
			flightConditionBuilder
					.addLinkedPassenger(this.passengerVariableName);
		}
	}

	/**
	 * Creates linking passenger criteria for PNR related objects.
	 */
	private void generatePnrPassengerLink() {
		// TODO for address, phone, credit card, frequent flier, email, travel
		// agency
	}

	/**
	 * Adds a rule condition to the builder.
	 * 
	 * @param trm
	 *            the condition to add.
	 */
	public void addRuleCondition(final QueryTerm trm) {
		// add the hit reason description
		if (conditionDescriptionBuilder == null) {
			conditionDescriptionBuilder = new StringBuilder();
		} else {
			conditionDescriptionBuilder
					.append(RuleHitDetail.HIT_REASON_SEPARATOR);
		}

		try {
			RuleConditionBuilderHelper.addConditionDescription(trm,
					conditionDescriptionBuilder);

			EntityEnum entity = EntityEnum.getEnum(trm.getEntity());
			TypeEnum attributeType = TypeEnum.getEnum(trm.getType());
			OperatorCodeEnum opCode = OperatorCodeEnum.getEnum(trm
					.getOperator());
			switch (entity) {
			case PASSENGER:
				passengerConditionBuilder.addCondition(opCode, trm.getField(),
						attributeType, trm.getValue());
				break;
			case DOCUMENT:
				documentConditionBuilder.addCondition(opCode, trm.getField(),
						attributeType, trm.getValue());
				break;
			case FLIGHT:
				flightConditionBuilder.addCondition(opCode, trm.getField(),
						attributeType, trm.getValue());
				this.flightCriteriaPresent = true;
				break;
			default:
				break;
			}
		} catch (ParseException pe) {
			StringBuilder bldr = new StringBuilder("[");
			for (String val : trm.getValue()) {
				bldr.append(val).append(",");
			}
			bldr.append("]");
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.INPUT_JSON_FORMAT_ERROR_CODE,
					bldr.toString(), trm.getType(), "Engine Rule Creation");
		} catch (NullPointerException | IllegalArgumentException ex) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
					String.format("QueryTerm (operator=%s, type=%s)",
							trm.getOperator(), trm.getType()),
					"Engine Rule Creation");

		}

	}

	private static final String ACTION_PASSENGER_HIT = "resultList.add(new RuleHitDetail(%s, %s, \"%s\", %s, null, \"%s\"));\n";
	private static final String ACTION_PASSENGER_HIT_WITH_FLIGHT = "resultList.add(new RuleHitDetail(%s, %s, \"%s\", %s, %s, \"%s\"));\n";

	public List<String> addRuleAction(StringBuilder ruleStringBuilder,
			UdrRule parent, Rule rule, String passengerVariableName) {
		String cause = conditionDescriptionBuilder.toString()
				.replace("\"", "'");
		ruleStringBuilder.append("then\n");
		if(isFlightCriteriaPresent()){
			ruleStringBuilder.append(String.format(ACTION_PASSENGER_HIT_WITH_FLIGHT, 
					"%dL", // the UDR ID may not be available
					"%dL", // the rule ID may not be available
					parent.getTitle(), 
					this.passengerVariableName,
					this.flightVariableName,
					cause));
		}else {
			ruleStringBuilder.append(String.format(ACTION_PASSENGER_HIT, 
					"%dL", // the UDR ID may not be available
					"%dL", // the rule ID may not be available
					parent.getTitle(), passengerVariableName, cause));
			
		}
		ruleStringBuilder.append("end\n");
		conditionDescriptionBuilder = null;
		return Arrays.asList(cause.split(RuleHitDetail.HIT_REASON_SEPARATOR));
	}
}

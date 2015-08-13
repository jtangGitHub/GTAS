package gov.gtas.rule.builder;

import gov.gtas.enumtype.EntityEnum;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlightConditionBuilder extends EntityConditionBuilder {
	/*
	 * The logger for the FlightConditionBuilder.
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(FlightConditionBuilder.class);
	
	private String defaultPassengerVariableName;
    private List<String> linkedPassengerList;
	public FlightConditionBuilder(final String drlVariableName, final String defaultPassengerVarName){
		super(drlVariableName, EntityEnum.FLIGHT.getEntityName());
		this.linkedPassengerList = new LinkedList<String>();
		this.defaultPassengerVariableName = defaultPassengerVarName;
	}
	public void addLinkedPassenger(final String passengerVariable){
		this.linkedPassengerList.add(passengerVariable);
	}
	
	
	/* (non-Javadoc)
	 * @see gov.gtas.rule.builder.EntityConditionBuilder#init()
	 */
	@Override
	public void reset() {
		super.reset();
		linkedPassengerList.clear();
	}
//	@Override
//	protected void addSpecialConditionsWithoutActualConditions(
//			StringBuilder bldr) {
//		//NO OP
//		if(logger.isDebugEnabled()){
//		    logger.debug("FlightConditionBuilder - no flight condition specified.");
//		}
//	}

	@Override
	protected void addSpecialConditions(StringBuilder bldr) {
		if(linkedPassengerList.isEmpty()){
			bldr.append(defaultPassengerVariableName).append(":")
			.append(EntityEnum.PASSENGER.getEntityName()).append("()")
		    .append(" from ")
			.append(getDrlVariableName())
			.append(".passengers\n");
		} else {
			if(linkedPassengerList.size() == 1){
				String passengerVariable = linkedPassengerList.get(0);
				bldr.append(EntityEnum.PASSENGER.getEntityName()).append("(id == ")
				    .append(passengerVariable).append(".id) from ")
					.append(getDrlVariableName())
					.append(".passengers\n");
	        } else {
				bldr.append(EntityEnum.PASSENGER.getEntityName()).append("(id in (");
				boolean firstTime = true;
	        	for(String passengerVariable:linkedPassengerList){
	        		if(firstTime){       			
	        			firstTime = false;
	        		} else {
	        			bldr.append(", ");
	        		}
	        		bldr.append(passengerVariable).append(".id");
	        	}
				
	        	bldr.append(")) from ")
				.append(getDrlVariableName())
				.append(".passengers\n");
			}
		}
	}
}

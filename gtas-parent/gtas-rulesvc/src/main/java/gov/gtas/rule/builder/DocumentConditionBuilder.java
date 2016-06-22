package gov.gtas.rule.builder;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.querybuilder.mappings.DocumentMapping;

public class DocumentConditionBuilder extends EntityConditionBuilder {
    /*
     * The logger for the DocumentConditionBuilder.
     */
    public DocumentConditionBuilder(final String drlVariableName,
            final String passengerVariableName) {
        super(drlVariableName, EntityEnum.DOCUMENT.getEntityName());
    }

    @Override
    protected void addSpecialConditions(StringBuilder bldr) {
    }

    public String getPassengerIdLinkExpression(){
        return getDrlVariableName()+"."+DocumentMapping.DOCUMENT_OWNER_ID.getFieldName();
    }

}

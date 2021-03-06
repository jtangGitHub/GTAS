package gov.gtas.model.lookup;

public enum PassengerTypeCode {
    P("Passenger"), // passenger
    C("Crew"), // crew
    I("In-transit Passenger");  // In-transit pax
    
    private String passengerTypeName;
    private PassengerTypeCode(final String name){
        this.passengerTypeName = name;
    }
    /**
     * @return the passengerTypeName
     */
    public String getPassengerTypeName() {
        return passengerTypeName;
    }
    
}

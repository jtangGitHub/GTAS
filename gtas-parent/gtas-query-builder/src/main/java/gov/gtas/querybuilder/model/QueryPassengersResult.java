package gov.gtas.querybuilder.model;

/**
 * 
 * @author GTAS5
 *
 */
public class QueryPassengersResult extends BaseFlightPaxQueryResult {

	private String firstName;
	private String lastName;
	private String passengerType;
	private String gender;
	private String dob;
	private String citizenship;
	private String documentNumber;
	private String documentType;
	private String documentIssuanceContry;
	private String seatNumber;
	private boolean isRuleHit;
	private boolean isOnWatchList;
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPassengerType() {
		return passengerType;
	}
	
	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getDob() {
		return dob;
	}
	
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public String getCitizenship() {
		return citizenship;
	}
	
	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}
	
	public String getDocumentNumber() {
		return documentNumber;
	}
	
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	
	public String getDocumentType() {
		return documentType;
	}
	
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	public String getDocumentIssuanceContry() {
		return documentIssuanceContry;
	}
	
	public void setDocumentIssuanceContry(String documentIssuanceContry) {
		this.documentIssuanceContry = documentIssuanceContry;
	}
	
	public String getSeatNumber() {
		return seatNumber;
	}
	
	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}
	
	public boolean isRuleHit() {
		return isRuleHit;
	}
	
	public void setRuleHit(boolean isRuleHit) {
		this.isRuleHit = isRuleHit;
	}

	public boolean isOnWatchList() {
		return isOnWatchList;
	}

	public void setOnWatchList(boolean isOnWatchList) {
		this.isOnWatchList = isOnWatchList;
	}
	
}

package gov.gtas.vo.passenger;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.validators.Validatable;

public class FrequentFlyerVo implements Validatable {
    private String carrier;
    private String number;

    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
	@Override
	public boolean isValid() {
		return StringUtils.isNotBlank(this.carrier) 
		       && StringUtils.isNotBlank(this.number);
	}
}

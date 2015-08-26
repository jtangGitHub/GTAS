package gov.gtas.util;

import java.util.Iterator;

import gov.gtas.delegates.vo.PnrDataVo;
import gov.gtas.model.Address;
import gov.gtas.model.Email;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;

public class PnrServiceDelegateUtils {

	public static Pnr mapPnrFromPnrDataVo(PnrDataVo vo, Pnr pnr){
		return pnr;
	}
	
	public static Pnr updateExistingReservation(Pnr source, Pnr target){
		Iterator it = source.getAddresses().iterator();
		while(it.hasNext()){
			Address a = (Address)it.next();
			target.addAddress(a);
		}
		Iterator it1=source.getEmails().iterator();
		while(it1.hasNext()){
			Email e = (Email)it.next();
			target.addEmail(e);
		}
		Iterator it2 = source.getFrequentFlyers().iterator();
		while(it2.hasNext()){
			FrequentFlyer ff =(FrequentFlyer) it2.next();
			target.addFrequentFlyer(ff);
		}
		Iterator it3 = source.getPhones().iterator();
		while(it3.hasNext()){
			Phone p = (Phone) it3.next();
			target.addPhone(p);
		}
		target.setAgency(source.getAgency());
		target.setBagCount(source.getBagCount());
		target.setChangeDate();
		target.setUpdatedBy("SYSTEM");
		target.setDateBooked(source.getDateBooked());
		target.setDateReceived(source.getDateReceived());
		target.setDepartureDate(source.getDepartureDate());
		target.setFormOfPayment(source.getFormOfPayment());
		target.setOrigin(source.getOrigin());
		target.setOriginCountry(source.getOriginCountry());
		return target;
	}
}
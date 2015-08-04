package gov.gtas.services;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Flight;
import gov.gtas.model.Pax;
import gov.gtas.model.Phone;
import gov.gtas.model.PnrData;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.LookUpRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CommonServicesConfig.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PnrDataServiceIT {

	
	@Autowired
	private PnrDataService pnrService;

	@Autowired
	private FlightService testTarget;

	@Autowired
	private AirportService aService;

	@Autowired
	private CountryService cService;

	@Autowired
	private CarrierService crService;

	@Autowired
	private LookUpRepository lookupDao;

	@Autowired
	private ApisMessageRepository apisMessageRepository;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testPnrDataSave() {
		System.out.println("##################################################");
		Flight f = new Flight();
		prepareFlightData( f);
		Pax passengerToUpdate = new Pax();
		preparePassengerData(passengerToUpdate);
		PnrData pnr = new PnrData();
		preparePnrData(pnr);
		//Set hs = new HashSet<Flight>();
		//hs.add(f);
		passengerToUpdate.getFlights().add(f);
		//Set<Traveler> passengers = new HashSet<Traveler>();
		//passengers.add(passengerToUpdate);
		//f.setPassengers(passengers);
		f.getPassengers().add(passengerToUpdate);
		passengerToUpdate.getPnrs().add(pnr);
		pnr.getPassengers().add(passengerToUpdate);
		//pnr.setPassenger(passengerToUpdate);
		pnrService.create(pnr);
		System.out.println("#####################pnr.getId()#############################"+pnr.getId());
		assertNotNull(pnr.getId());

	}

	private void preparePnrData(PnrData pnr){
		pnr.setBagCount(2);
		pnr.setBooked("Yes");
		String cr = "AA";
		pnr.setCarrier(cr);
		pnr.setCreatedAt(new Date());
		pnr.setCreationDate();
		pnr.setCreatedBy("JUNIT TEST");
		pnr.setDaysBookedBeforeTravel(30);
		pnr.setDepartureDate(new Date());
		pnr.setFormOfPayment("CC");
		String a = "IAD";
		String c = "US";
		pnr.setOrigin(a);
		pnr.setOriginCountry(c);
		pnr.setPassengerCount(1);
		pnr.setTotalDwellTime(120);
		CreditCard cc = new CreditCard();
		cc.setCardExpiration("0417");
		cc.setCardHolderName("Srinivasarao Vempati");
		cc.setCardNumber("2222-3333-4444-5555");
		cc.setCardType("VISA");
		//cc.setPnrData(pnr);
		pnr.setCreditCard(cc);
		Address add = new Address();
		add.setAddressType("H");
		add.setCity("ALDIE");
		add.setCountry("USA");
		add.setLine1("41000 Zirocn dr");
		add.setPostalCode("20105");
		add.setState("VA");
		add.setCreationDate();
		add.setCreatedBy("JUNIT");
		add.setPnrData(pnr);
		Set adds = new HashSet<Address>();
		adds.add(add);
		pnr.setAdresses(adds);
		Phone p = new Phone();
		p.setPhoneNumber("24243534455");
		p.setPhoneType("H");
		p.setPnrData(pnr);
		p.setCreationDate();
		p.setCreatedBy("JUNIT");
		Set phones = new HashSet();
		phones.add(p);
		pnr.setPhones(phones);
		Agency ag = new Agency();
		ag.setAgencyCity("Aldie");
		ag.setAgencyCountry("USA");
		ag.setAgencyIdentifier("123456C");
		ag.setAgencyName("Some Test Agency");
		ag.setCreatedAt(new Date());
		pnr.setAgency(ag);
	}
	
	private void prepareFlightData(Flight f){
		f.setCreatedAt(new Date());
		f.setCreatedBy("JUNIT");
		// Airport a = new Airport(3616l,"Washington","IAD","KAID");
		String a = "IAD";
		System.out.println(a);
		f.setOrigin(a);
		String b = "JFK";
		// Airport b = new Airport (3584l,"Atlanta","ATL","KATL");

		f.setDestination(b);
		f.setEta(new Date());
		f.setEtd(new Date("7/31/2015"));
		f.setFlightDate(new Date());
		f.setFlightNumber("528");

		String c = "US";
		f.setDestinationCountry(c);
		f.setOriginCountry(c);

		String cr = "AA";
		f.setCarrier(cr);
		f.setUpdatedAt(new Date());
		f.setUpdatedBy("TEST");
	}
	
	private void preparePassengerData(Pax passengerToUpdate){
		passengerToUpdate.setAge(30);
		String c = "US";
		String b = "JFK";
		passengerToUpdate.setCitizenshipCountry(c);
		passengerToUpdate.setDebarkation(b);
		passengerToUpdate.setDebarkCountry(c);
		passengerToUpdate.setDob(new Date("04/06/1966"));
		passengerToUpdate.setEmbarkation(b);
		passengerToUpdate.setEmbarkCountry(c);
		passengerToUpdate.setFirstName("Srinivas");
	}
}

package gov.gtas.bo;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RuleHitDetailTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCodeEquals() {
		Passenger p = new Passenger();
		p.setPassengerType("P");
		p.setId(1L);
		Flight f = new Flight();
		f.setId(35L);
		
		RuleHitDetail det1 = new RuleHitDetail(25L, 31L, "foo", p, f, "bar");
		RuleHitDetail det2 = new RuleHitDetail(25L, 31L, "blah", p, f, "blah");
		assertEquals(det1,det2);
		
		Set<RuleHitDetail> detset = new HashSet<RuleHitDetail>();
		detset.add(det1);
		detset.add(det2);
		assertEquals(1, detset.size());
	}

}

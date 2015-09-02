package gov.gtas.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.gtas.constant.CommonErrorConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BasicErrorDetailsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDetails() {
		Throwable cause = new NullPointerException();
		ErrorDetails err = new BasicErrorDetails(new Exception(new Exception("Test Error", cause)));
		assertNotNull(err.getErrorId());
		assertEquals(CommonErrorConstants.SYSTEM_ERROR_CODE, err.getFatalErrorCode());
		String[] det = err.getErrorDetails();
		assertNotNull(det);
		assertEquals("Exception class:Exception", det[0]);
		assertTrue(det[1].endsWith("Test Error"));
		assertTrue(det.length > 0);
		System.out.println(String.join("\n", det));
	}

}
package gov.gtas.svc;

import java.util.List;

import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.udr.json.JsonUdrListElement;
import gov.gtas.model.udr.json.UdrSpecification;

/**
 * The service interface for managing User Defined Rules (UDR).<br>
 * 1. CRUD on UDR.<br>
 * 2. Generation of Drools Rules and creation of versioned Knowledge Base.
 * @author GTAS3 (AB)
 *
 */
public interface UdrService {
	/**
	 * Retrieves the UDR domain object from the DB and converts it to the corresponding JSON object.
	 * @param userId the userId of the author.
	 * @param title the title of the UDR.
	 * @return the JSON UDR object.
	 */
   UdrSpecification fetchUdr(String userId, String title);
	/**
	 * Retrieves the UDR domain object from the DB and converts it to the corresponding JSON object.
	 * @param id the id of the UDR record in the DB.
	 * @return the JSON UDR object.
	 */
  UdrSpecification fetchUdr(Long id);
	/**
	 * Retrieves a list of UDR summary JSON objects authored by the specified user.
	 * @param userId the userId of the author.
	 * @return the list of JSON UDR summary objects.
	 */
  List<JsonUdrListElement> fetchUdrSummaryList(String userId);
	/**
	 * Retrieves a list of UDR summary JSON objects.
	 * @return the list of JSON UDR summary objects.
	 */
  List<JsonUdrListElement> fetchUdrSummaryList();
  /**
   * Creates a new UDR object in the database and returns it in JSON object format.
   * @param userId the userId of the author.
   * @param udrToCreate the JSON UDR object to be inserted into tte DB.
   * @return the service response JSON format.
   */
  JsonServiceResponse createUdr(String userId, UdrSpecification udrToCreate);
  /**
   * Updates the UDR by replacing the UDR object in the DB with the same ID.
   * @param userId the userId of the author.
   * @param udrToUpdate the updated object image to use for replacing the DB object.
   * @return the updated object.
   */
  JsonServiceResponse updateUdr(String userId, UdrSpecification udrToUpdate);
  /**
   * Deletes a UDR object.
   * @param userId the userId of the author.
   * @param id the id of the UDR record in the DB to be deleted.
   * @return the service response JSON format.
   */
  JsonServiceResponse deleteUdr(String userId, Long id);
}

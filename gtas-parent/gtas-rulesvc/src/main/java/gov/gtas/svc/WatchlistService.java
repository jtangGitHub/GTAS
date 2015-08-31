package gov.gtas.svc;

import gov.gtas.model.udr.json.JsonServiceResponse;
import gov.gtas.model.watchlist.json.WatchlistSpec;

import java.util.List;

/**
 * The service interface for managing User Defined Rules (UDR).<br>
 * 1. CRUD on UDR.<br>
 * 2. Generation of Drools Rules and creation of versioned Knowledge Base.
 * 
 * @author GTAS3 (AB)
 *
 */
public interface WatchlistService {
	/**
	 * Retrieves the UDR domain object from the DB and converts it to the
	 * corresponding JSON object.
	 * 
	 * @param wlName
	 *            the name of the watch list.
	 * @return the Watch list object.
	 */
	WatchlistSpec fetchWatchlist(String wlName);

	/**
	 * Creates a new UDR object in the database and returns it in JSON object
	 * format.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @param udrToCreate
	 *            the JSON UDR object to be inserted into tte DB.
	 * @return the service response JSON format.
	 */
	JsonServiceResponse createOrUpdateWatchlist(String userId,
			WatchlistSpec wlToCreateUpdate);

	/**
	 * Fetches all watch lists
	 * 
	 * @return the list of all available watch list objects.
	 */
	List<WatchlistSpec> fetchAllWatchlists();

	/**
	 * Compiles all watch lists into a named knowledge base
	 * @param knowledgeBaseName name of the knowledge base to compile the watch list rules into.
	 * @return the list of all available watch list objects.
	 */
	JsonServiceResponse activateAllWatchlists(String knowledgeBaseName);
	/**
	 * Compiles all watch lists into the default knowledge base for watch lists.
	 * @return the list of all available watch list objects.
	 */
	JsonServiceResponse activateAllWatchlists();

	/**
	 * Deletes the named watch list.
	 * @param wlName the name of the watch list to be deleted.
	 * @return the delete result.
	 */
	JsonServiceResponse deleteWatchlist(String wlName);
}

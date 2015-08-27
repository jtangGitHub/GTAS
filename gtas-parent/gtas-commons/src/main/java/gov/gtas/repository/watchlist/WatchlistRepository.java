package gov.gtas.repository.watchlist;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.watchlist.Watchlist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
/**
 * Watch list Repository with custom queries.
 * @author GTAS3 (AB)
 *
 */
public interface WatchlistRepository extends CrudRepository<Watchlist, Long>, JpaSpecificationExecutor<Watchlist> {
    public List<Watchlist> findByWatchlistEntity(EntityEnum entity);
    
	@Query("SELECT wl FROM Watchlist wl WHERE wl.watchlistName = :name")
	public Watchlist getWatchlistByName(@Param("name") String name);

	@Query("SELECT wl FROM Watchlist wl WHERE wl.compileTimestamp is null or wl.editTimestamp > wl.compileTimestamp")
	public List<Watchlist> fetchUncompiledWatchlists();
	
	@Query("SELECT watchlistName, watchlistEntity FROM Watchlist")
	public List<Object[]> fetchWatchlistSummary();
}
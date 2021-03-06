package gov.gtas.repository.watchlist;

import gov.gtas.model.watchlist.WatchlistItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
/**
 * Watch list Repository with custom queries.
 */
public interface WatchlistItemRepository extends CrudRepository<WatchlistItem, Long>, JpaSpecificationExecutor<WatchlistItem> {
    
    @Query("SELECT wli FROM WatchlistItem wli WHERE wli.watchlist.watchlistName = :watchlistName")
    public List<WatchlistItem> getItemsByWatchlistName(@Param("watchlistName") String watchlistName);   

    @Query("DELETE FROM WatchlistItem wli WHERE wli.watchlist.watchlistName = :watchlistName")
    public void deleteItemsByWatchlistName(@Param("watchlistName") String watchlistName);   
}

package gov.gtas.repository;

import gov.gtas.model.lookup.Country;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CountryRepository extends CrudRepository<Country, Long>{
    
    @Query("SELECT c FROM Country c WHERE UPPER(c.iso2) = UPPER(:countryCode)")
    public List<Country> getCountryByTwoLetterCode(@Param("countryCode") String countryCode);
    
    @Query("SELECT c FROM Country c WHERE UPPER(c.iso3) = UPPER(:countryCode)")
    public List<Country> getCountryByThreeLetterCode(@Param("countryCode") String countryCode);
    
    public Country findByName(String name);

}

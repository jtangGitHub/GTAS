package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long>{
	public Phone findByNumber(String number);
}

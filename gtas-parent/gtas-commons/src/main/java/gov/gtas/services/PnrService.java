package gov.gtas.services;

import java.util.List;

import gov.gtas.model.Pnr;

public interface PnrService {
	
	public Pnr create(Pnr pnr);
	public Pnr delete(Long id);
	public Pnr update(Pnr pnr);
	public Pnr findById(Long id);
	public List<Pnr> findAll();

}
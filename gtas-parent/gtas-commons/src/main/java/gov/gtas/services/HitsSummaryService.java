package gov.gtas.services;

import gov.gtas.model.HitsSummary;

import java.util.List;

public interface HitsSummaryService {

	public List<Long> findByTravelerId(Long travelerId);

}

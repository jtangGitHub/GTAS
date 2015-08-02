package gov.gtas.controller;

import gov.gtas.constants.Constants;
import gov.gtas.services.HitsSummaryService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.HITS_SUMMARY_SERVICE)
public class HitsSummaryController {
	private static final Logger logger = LoggerFactory
			.getLogger(HitsSummaryController.class);

	@Autowired
	HitsSummaryService hitsSummaryService;

	@RequestMapping(value = Constants.HITS_SUMMARY_RULES_BY_TRAVELER_ID, method = RequestMethod.GET)
	public List<Long> getRules(@PathVariable Long id) {
		List<Long> temp = new ArrayList<Long>();
		temp.add(1L);
		temp.add(2L);
		return temp;
		//return hitsSummaryService.findByTravelerId(id);
	}
}

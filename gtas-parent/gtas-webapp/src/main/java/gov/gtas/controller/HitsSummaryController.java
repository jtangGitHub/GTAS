package gov.gtas.controller;

import gov.gtas.dataobject.HitDetailVo;
import gov.gtas.model.HitDetail;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.services.HitsSummaryService;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.UdrService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HitsSummaryController {
	private static final Logger logger = LoggerFactory
			.getLogger(HitsSummaryController.class);

	@Autowired
    private HitsSummaryService hitsSummaryService;

	@Autowired
	private RulePersistenceService rulePersistenceService;
	
	@Autowired
	private UdrService udrService;

	
	@RequestMapping(value = "/hit/passenger", method = RequestMethod.GET)
    @Transactional
	public @ResponseBody List<HitDetailVo> getRules(@RequestParam(value = "passengerId", required = false) String id) {

		return getHitDetailsMapped(hitsSummaryService.findByPassengerId(Long.parseLong(id)));
	}

	
	@Transactional
	public List<HitDetailVo> getHitDetailsMapped(List<HitDetail> tempHitDetailList){
	
	int i = 0;
	List<HitDetailVo> tempList = new ArrayList<HitDetailVo>();
	HitDetailVo hdetailVo = new HitDetailVo();
	UdrRule tempRule = new UdrRule();
	HashMap<Integer,HitDetailVo> _tempMap = new HashMap<Integer, HitDetailVo>();
	HashSet<HitDetailVo> tempSet = new HashSet<HitDetailVo>();
	
	for(HitDetail htd: tempHitDetailList){
		
				
		if((i != htd.getRuleId().intValue()) && (!_tempMap.containsKey(Integer.valueOf(htd.getRuleId().intValue())))){
			//get Rule Desc
			i = htd.getRuleId().intValue();
			hdetailVo = new HitDetailVo();
			//tempRule = rulePersistenceService.findById(htd.getRuleId());
			//	tempRule = udrService.getUDRByID(htd.getRuleId());
			hdetailVo.setRuleId(htd.getRuleId());
			hdetailVo.setRuleTitle("");
			hdetailVo.setRuleDesc("");
			hdetailVo.getHitsDetailsList().add(htd);
			_tempMap.put(Integer.valueOf(i), hdetailVo);
		}else{
			hdetailVo = _tempMap.get(Integer.valueOf(i));
			hdetailVo.getHitsDetailsList().add(htd);
		}
		
		tempSet.add(hdetailVo);
		
	}
	
	if(!tempSet.isEmpty()){
		Iterator iter = tempSet.iterator();
		while(iter.hasNext()){
			tempList.add((HitDetailVo)iter.next());
		}
	}
	
	return tempList;
}
	
	
}

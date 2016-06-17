package gov.gtas.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.Disposition;
import gov.gtas.model.Flight;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.repository.DispositionStatusRepository;
import gov.gtas.repository.HitsSummaryRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.vo.passenger.PassengerVo;

@Service
public class PassengerServiceImpl implements PassengerService {

	@Resource
	private PassengerRepository passengerRespository;

    @Resource
    private HitsSummaryRepository hitsSummaryRepository;

    @Resource
    private DispositionStatusRepository dispositionStatusRepo;
    
	@Override
	@Transactional
	public Passenger create(Passenger passenger) {
		return passengerRespository.save(passenger);
	}

    @Override
    @Transactional
    public PassengersPageDto getPassengersByCriteria(Long flightId, PassengersRequestDto request) {
        List<PassengerVo> rv = new ArrayList<>();
        Pair<Long, List<Object[]>> tuple = passengerRespository.findByCriteria(flightId, request);
        int count = 0;
        for (Object[] objs : tuple.getRight()) {
            if (count == request.getPageSize()) {
                break;
            }
            
            Passenger p = (Passenger)objs[0];
            Flight f = (Flight)objs[1];
            HitsSummary hit = (HitsSummary)objs[2];
            
            if (hit != null && f.getId() != hit.getFlight().getId()) {
                continue;
            }
            
            PassengerVo vo = new PassengerVo();
            BeanUtils.copyProperties(p, vo);
            rv.add(vo);
            count++;

            if (hit != null) {
                String hitType = hit.getHitType();
                if (hitType.contains(HitTypeEnum.R.toString())) {
                    vo.setOnRuleHitList(true);
                }
                if (hitType.contains(HitTypeEnum.P.toString())) {
                    vo.setOnWatchList(true);
                }
                if (hitType.contains(HitTypeEnum.D.toString())) {
                    vo.setOnWatchListDoc(true);
                }
            }

            // grab flight info
            vo.setFlightId(f.getId().toString());
            vo.setFlightNumber(f.getFlightNumber());
            vo.setFullFlightNumber(f.getFullFlightNumber());
            vo.setCarrier(f.getCarrier());
            vo.setEtd(f.getEtd());
            vo.setEta(f.getEta());            
        }

        return new PassengersPageDto(rv, tuple.getLeft());
    }    
    
	@Override
	@Transactional
	public Passenger update(Passenger passenger) {
		Passenger passengerToUpdate = this.findById(passenger.getId());
		if(passengerToUpdate != null){
			passengerToUpdate.setAge(passenger.getAge());
			passengerToUpdate.setCitizenshipCountry(passenger.getCitizenshipCountry());
			passengerToUpdate.setDebarkation(passenger.getDebarkation());
			passengerToUpdate.setDebarkCountry(passenger.getDebarkCountry());
			passengerToUpdate.setDob(passenger.getDob());
			passengerToUpdate.setEmbarkation(passenger.getEmbarkation());
			passengerToUpdate.setEmbarkCountry(passenger.getEmbarkCountry());
			passengerToUpdate.setFirstName(passenger.getFirstName());
			passengerToUpdate.setFlights(passenger.getFlights());
			passengerToUpdate.setGender(passenger.getGender());
			passengerToUpdate.setLastName(passenger.getLastName());
			passengerToUpdate.setMiddleName(passenger.getMiddleName());
			passengerToUpdate.setResidencyCountry(passenger.getResidencyCountry());
			passengerToUpdate.setDocuments(passenger.getDocuments());
			passengerToUpdate.setSuffix(passenger.getSuffix());
			passengerToUpdate.setTitle(passenger.getTitle());
		}
		return passengerToUpdate;
	}

	@Override
	@Transactional	
    public List<Disposition> getPassengerDispositionHistory(Long passengerId, Long flightId) {
		return passengerRespository.getPassengerDispositionHistory(passengerId, flightId);
	}
	
	@Override
    public List<DispositionStatus> getDispositionStatuses() {
		Iterable<DispositionStatus> i = dispositionStatusRepo.findAll();
		if (i != null) {
			return IteratorUtils.toList(i.iterator());
		}
		return new ArrayList<>();
	}
	
	@Override
	@Transactional
	public Passenger findById(Long id) {
		return passengerRespository.findOne(id);
	}

	@Override
	@Transactional
	public List<Passenger> getPassengersByLastName(String lastName) {
		List<Passenger> passengerList = passengerRespository.getPassengersByLastName(lastName);
		return passengerList;
	}

	@Override
    public void fillWithHitsInfo(PassengerVo vo, Long flightId, Long passengerId) {
        List<HitsSummary> hitsSummary = hitsSummaryRepository.findByFlightIdAndPassengerId(flightId, passengerId);
        if (!CollectionUtils.isEmpty(hitsSummary)) {
            for (HitsSummary hs : hitsSummary) {
                String hitType = hs.getHitType();
                if (hitType.contains(HitTypeEnum.R.toString())) {
                    vo.setOnRuleHitList(true);
                }
                if (hitType.contains(HitTypeEnum.P.toString())) {
                    vo.setOnWatchList(true);
                }
                if (hitType.contains(HitTypeEnum.D.toString())) {
                    vo.setOnWatchListDoc(true);
                }
            }
        }
    }
}
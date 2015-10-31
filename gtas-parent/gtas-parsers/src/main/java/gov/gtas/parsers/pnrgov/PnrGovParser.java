package gov.gtas.parsers.pnrgov;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.pnrgov.segment.ABI;
import gov.gtas.parsers.pnrgov.segment.ADD;
import gov.gtas.parsers.pnrgov.segment.APD;
import gov.gtas.parsers.pnrgov.segment.DAT_G1;
import gov.gtas.parsers.pnrgov.segment.DAT_G10;
import gov.gtas.parsers.pnrgov.segment.DAT_G6;
import gov.gtas.parsers.pnrgov.segment.EBD;
import gov.gtas.parsers.pnrgov.segment.EQN;
import gov.gtas.parsers.pnrgov.segment.FAR;
import gov.gtas.parsers.pnrgov.segment.FOP;
import gov.gtas.parsers.pnrgov.segment.FOP.Payment;
import gov.gtas.parsers.pnrgov.segment.FTI;
import gov.gtas.parsers.pnrgov.segment.FTI.FrequentFlierDetails;
import gov.gtas.parsers.pnrgov.segment.IFT;
import gov.gtas.parsers.pnrgov.segment.LTS;
import gov.gtas.parsers.pnrgov.segment.MON;
import gov.gtas.parsers.pnrgov.segment.MSG;
import gov.gtas.parsers.pnrgov.segment.ORG;
import gov.gtas.parsers.pnrgov.segment.PTK;
import gov.gtas.parsers.pnrgov.segment.RCI;
import gov.gtas.parsers.pnrgov.segment.RCI.ReservationControlInfo;
import gov.gtas.parsers.pnrgov.segment.REF;
import gov.gtas.parsers.pnrgov.segment.RPI;
import gov.gtas.parsers.pnrgov.segment.SAC;
import gov.gtas.parsers.pnrgov.segment.SRC;
import gov.gtas.parsers.pnrgov.segment.SSD;
import gov.gtas.parsers.pnrgov.segment.SSR;
import gov.gtas.parsers.pnrgov.segment.TBD;
import gov.gtas.parsers.pnrgov.segment.TBD.BagDetails;
import gov.gtas.parsers.pnrgov.segment.TIF;
import gov.gtas.parsers.pnrgov.segment.TIF.TravelerDetails;
import gov.gtas.parsers.pnrgov.segment.TKT;
import gov.gtas.parsers.pnrgov.segment.TRA;
import gov.gtas.parsers.pnrgov.segment.TRI;
import gov.gtas.parsers.pnrgov.segment.TVL;
import gov.gtas.parsers.pnrgov.segment.TVL_L0;
import gov.gtas.parsers.pnrgov.segment.TXD;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.vo.passenger.AddressVo;
import gov.gtas.vo.passenger.AgencyVo;
import gov.gtas.vo.passenger.CreditCardVo;
import gov.gtas.vo.passenger.FlightVo;
import gov.gtas.vo.passenger.FrequentFlyerVo;
import gov.gtas.vo.passenger.PassengerVo;
import gov.gtas.vo.passenger.PhoneVo;
import gov.gtas.vo.passenger.PnrVo;


public final class PnrGovParser extends EdifactParser<PnrVo> {
   
    public PnrGovParser() {
        this.parsedMessage = new PnrVo();
    }

    protected String getPayloadText(String message) throws ParseException {
        return EdifactLexer.getMessagePayload(message, "SRC", "UNT");
    }
    
    @Override
    public void parsePayload() throws ParseException {
        MSG msg = getMandatorySegment(MSG.class);
        if(msg != null && msg.getMessageTypeCode() != null){
        	parsedMessage.setMessageCode(msg.getMessageTypeCode().getCode());
        }
        
        getMandatorySegment(ORG.class);
        TVL_L0 tvl = getMandatorySegment(TVL_L0.class, "TVL");
        getMandatorySegment(EQN.class);
        getMandatorySegment(SRC.class);       
        processGroup1_PnrStart(tvl);
    }

    /**
     * start of a new PNR
     */
    private void processGroup1_PnrStart(TVL_L0 tvl_l0) throws ParseException {
        parsedMessage.setCarrier(tvl_l0.getCarrier());
        parsedMessage.setOrigin(tvl_l0.getOrigin());
        parsedMessage.setDepartureDate(tvl_l0.getEtd());
        
        RCI rci = getMandatorySegment(RCI.class);
        ReservationControlInfo controlInfo = rci.getReservations().get(0);
        parsedMessage.setRecordLocator(controlInfo.getReservationControlNumber());

        for (;;) {
            SSR ssr = getConditionalSegment(SSR.class, "SSR");
            if (ssr == null) {
                break;
            }
        }

        DAT_G1 dat = getConditionalSegment(DAT_G1.class, "DAT");
        if (dat != null) {
            parsedMessage.setDateBooked(dat.getTicketIssueDate());
        }

        for (;;) {
            IFT ift = getConditionalSegment(IFT.class);
            if (ift == null) {
                break;
            }
        }

        ORG org = getMandatorySegment(ORG.class);
        processAgencyInfo(org);

        for (;;) {
            ADD add = getConditionalSegment(ADD.class);
            if (add == null) {
                break;
            }
            AddressVo address = PnrUtils.createAddress(add);
            if (address.isValid()) {
                parsedMessage.getAddresses().add(address);
            }
            if (address.getPhoneNumber() != null) {
                PhoneVo p = PnrUtils.createPhone(address.getPhoneNumber());
                if (p.isValid()) {
                    parsedMessage.getPhoneNumbers().add(p);
                }
            }
        }

        for (;;) {
            // excess baggage information for all passengers
            EBD ebd = getConditionalSegment(EBD.class);
            if (ebd == null) {
                break;
            }
            processExcessBaggage(ebd);
        }
        
        TIF tif = getMandatorySegment(TIF.class);
        processGroup2_Passenger(tif);
        for (;;) {
            tif = getConditionalSegment(TIF.class);
            if (tif == null) {
                break;
            }
            processGroup2_Passenger(tif);
        }

        for (;;) {
            TVL tvl = getConditionalSegment(TVL.class);
            if (tvl == null) {
                break;
            }
            processGroup5_Flight(tvl);
        }
    }

    /**
     * Passenger
     */
    private void processGroup2_Passenger(TIF tif) throws ParseException {
        FTI fti = getConditionalSegment(FTI.class);
        if (fti != null) {
            FrequentFlyerVo ffvo = new FrequentFlyerVo();
            FrequentFlierDetails ffdetails = fti.getFrequentFlierInfo().get(0);
            ffvo.setCarrier(ffdetails.getAirlineCode());
            ffvo.setNumber(ffdetails.getFreqTravelerNumber());
            if (ffvo.isValid()) {
                parsedMessage.getFrequentFlyerDetails().add(ffvo);
            }
        }
        
        for (;;) {
            IFT ift = getConditionalSegment(IFT.class);
            if (ift == null) {
                break;
            }
        }

        getConditionalSegment(REF.class);
        getConditionalSegment(EBD.class);

        for (;;) {
            FAR far = getConditionalSegment(FAR.class);
            if (far == null) {
                break;
            }
        }

        // SSR’s in GR.2 apply to the specific passenger.
        for (;;) {
            SSR ssr = getConditionalSegment(SSR.class);
            if (ssr == null) {
                break;
            }
            String code = ssr.getTypeOfRequest();
            if (SSR.DOCS.equals(code)) {
                PassengerVo p = PnrUtils.createPassenger(ssr, tif);
                if (p != null) {
                    parsedMessage.getPassengers().add(p);
                    parsedMessage.setPassengerCount(parsedMessage.getPassengerCount() + 1);
                }
            } else if (SSR.DOCA.equals(code)) {
                AddressVo addr = PnrUtils.createAddress(ssr);
                if (addr.isValid()) {
                    parsedMessage.getAddresses().add(addr);
                }
            }
        }

        for (;;) {
            ADD add = getConditionalSegment(ADD.class);
            if (add == null) {
                break;
            }
            AddressVo addr = PnrUtils.createAddress(add);
            if (addr.isValid()) {
                parsedMessage.getAddresses().add(addr);
            }
        }

        for (;;) {
            TKT tkt = getConditionalSegment(TKT.class);
            if (tkt == null) {
                break;
            }
            processGroup3_TicketCost(tkt);
        }
    }

    /**
     * Ticket cost info. Repeats for each ticket associated with a passenger.
     * Not currently using this.
     */
    private void processGroup3_TicketCost(TKT tkt) throws ParseException {
        getConditionalSegment(MON.class);
        getConditionalSegment(PTK.class);

        for (;;) {
            TXD txd = getConditionalSegment(TXD.class);
            if (txd == null) {
                break;
            }
        }

        getConditionalSegment(DAT_G1.class, "DAT");

        FOP fop = getConditionalSegment(FOP.class);
        processGroup4_FormOfPayment(fop);
    }

    /**
     * Form of payment info: get credit card if exists
     */
    private void processGroup4_FormOfPayment(FOP fop) throws ParseException {
        List<CreditCardVo> newCreditCards = new ArrayList<>();

        if (fop != null) {
            List<Payment> payments = fop.getPayments();
            if (!CollectionUtils.isEmpty(payments)) {
                // arbitrarily select first payment type
                parsedMessage.setFormOfPayment(payments.get(0).getPaymentType());
                for (Payment p : payments) {
                    if (p.isCreditCard()) {
                        CreditCardVo cc = new CreditCardVo();
                        cc.setCardType(p.getVendorCode());
                        cc.setExpiration(p.getExpirationDate());
                        cc.setNumber(p.getAccountNumber());
                        if (cc.isValid()) {
                            newCreditCards.add(cc);
                        }
                    }                    
                }
            }
        }
        
        IFT ift = getConditionalSegment(IFT.class);
        if (ift != null) {
            if (newCreditCards.size() > 0 && ift.isSponsorInfo()) {
                List<String> msgs = ift.getMessages();
                if (msgs.size() >= 1) {
                    for (CreditCardVo cc : newCreditCards) {
                        cc.setAccountHolder(msgs.get(0));
                    }
                }
            }
        }

        for (CreditCardVo cc : newCreditCards) {
            if (cc.isValid()) {
                parsedMessage.getCreditCards().add(cc);
            }
        }
        
        ADD add = getConditionalSegment(ADD.class);
        if (add != null) {
            AddressVo addr = PnrUtils.createAddress(add);
            if (addr.isValid()) {
                parsedMessage.getAddresses().add(addr);
            }
        }
    }

    /**
     * Flight info: repeats for each flight segment in the passenger record’s
     * itinerary.
     */
    private void processGroup5_Flight(TVL tvl) throws ParseException {
        FlightVo f = new FlightVo();
        f.setCarrier(tvl.getCarrier());
        f.setDestination(tvl.getDestination());
        f.setOrigin(tvl.getOrigin());
        f.setEta(tvl.getEta());
        f.setEtd(tvl.getEtd());
        f.setFlightNumber(ParseUtils.padFlightNumberWithZeroes(tvl.getFlightNumber()));
        ParseUtils.initEtaEtdDate(f);
        Date flightDate = ParseUtils.determineFlightDate(tvl.getEtd(), tvl.getEta(), parsedMessage.getTransmissionDate());
        if (flightDate == null) {
            throw new ParseException("Could not determine flight date");
        }
        f.setFlightDate(flightDate);
        parsedMessage.getFlights().add(f);
        
        TRA tra = getConditionalSegment(TRA.class);
        RPI rpi = getConditionalSegment(RPI.class);
        APD apd = getConditionalSegment(APD.class);
        
        for (;;) {
            SSR ssr = getConditionalSegment(SSR.class);
            if (ssr == null) {
                break;
            }
        }

        RCI rci = getConditionalSegment(RCI.class);

        for (;;) {
            IFT ift = getConditionalSegment(IFT.class);
            if (ift == null) {
                break;
            }
        }

        for (;;) {
            DAT_G6 dat = getConditionalSegment(DAT_G6.class, "DAT");
            if (dat == null) {
                break;
            }
            processGroup6_Agent(dat);
        }
        
        for (;;) {
            EQN eqn = getConditionalSegment(EQN.class);
            if (eqn == null) {
                break;
            }
            processGroup8_SplitPassenger(eqn);
        }

        for (;;) {
            MSG msg = getConditionalSegment(MSG.class);
            if (msg == null) {
                break;
            }
            processGroup9_NonAir(msg);
        }

        for (;;) {
            ABI abi = getConditionalSegment(ABI.class);
            if (abi == null) {
                break;
            }
            processGroup10_History(abi);
        }

        for (;;) {
            LTS lts = getConditionalSegment(LTS.class);
            if (lts == null) {
                break;
            }
        }        
    }
    
    /**
     * the agent info that checked-in the passenger
     */
    private void processGroup6_Agent(DAT_G6 dat) throws ParseException {
        ORG org = getConditionalSegment(ORG.class, "ORG");
        processAgencyInfo(org);

        TRI tri = getMandatorySegment(TRI.class);
        processGroup7_SeatInfo(tri);
        for (;;) {
            tri = getConditionalSegment(TRI.class);
            if (tri == null) {
                break;
            }
            processGroup7_SeatInfo(tri);
        }        
    }
    
    /**
     * boarding, seat number and checked bag info
     */
    private void processGroup7_SeatInfo(TRI tri) throws ParseException {
        PassengerVo thePax = null;
        String refNumber = tri.getTravelerReferenceNumber();
        if (refNumber != null) {
            for (PassengerVo pax : parsedMessage.getPassengers()) {
                if (refNumber.equals(pax.getTravelerReferenceNumber())) {
                    thePax = pax;
                    break;
                }
            }
        }
        
        TIF tif = getConditionalSegment(TIF.class);
        if (thePax == null && tif != null) {
            // try finding pax based on tif info
            String surname = tif.getTravelerSurname();
            List<TravelerDetails> td = tif.getTravelerDetails();
            if (td != null && td.size() > 0) {
                String firstName = td.get(0).getTravelerGivenName();
                for (PassengerVo pax : parsedMessage.getPassengers()) {
                    if (surname.equals(pax.getLastName()) && firstName.equals(pax.getFirstName())) {
                        thePax = pax;
                        break;
                    }
                }
            }
        }
        
        SSD ssd = getConditionalSegment(SSD.class);
        if (thePax != null && ssd != null) {
            thePax.setSeat(ssd.getSeatNumber());
        }
        
        TBD tbd = getConditionalSegment(TBD.class);
        if (tbd == null) {
            return;
        }
        
        Integer n = tbd.getNumBags();
        if (n != null) {
            parsedMessage.setBagCount(parsedMessage.getBagCount() + n);
        } else {
            for (BagDetails bd : tbd.getBagDetails()) {
                int tmp = bd.getNumConsecutiveTags();
                parsedMessage.setBagCount(parsedMessage.getBagCount() + tmp);                
            }
        }
    }
    
    private void processGroup8_SplitPassenger(EQN eqn) throws ParseException {
        getMandatorySegment(RCI.class);
    }

    /**
     * non-air segments: car, hotel, rail.  Not used.
     */
    private void processGroup9_NonAir(MSG msg) throws ParseException {
        for (;;) {
            TVL tvl = getConditionalSegment(TVL.class);
            if (tvl == null) {
                break;
            }
        }
    }

    private void processGroup10_History(ABI abi) throws ParseException {
        DAT_G10 dat = getConditionalSegment(DAT_G10.class, "DAT");
        for (;;) {
            SAC sac = getConditionalSegment(SAC.class);
            if (sac == null) {
                break;
            }
            processGroup11_HistoryCredit(sac);
        }        
    }

    private void processGroup11_HistoryCredit(SAC sac) throws ParseException {
        TIF tif = getConditionalSegment(TIF.class);
        SSR ssr = getConditionalSegment(SSR.class);
        IFT ift = getConditionalSegment(IFT.class);
        TBD tbd = getConditionalSegment(TBD.class);
        for (;;) {
            TVL tvl = getConditionalSegment(TVL.class);
            if (tvl == null) {
                break;
            }
            processGroup12_HistoryFlightInfo(tvl);
        }        

    }

    private void processGroup12_HistoryFlightInfo(TVL tvl) throws ParseException {
        RPI rpi = getConditionalSegment(RPI.class);
    }
    
    private void processExcessBaggage(EBD ebd) {
        if (ebd != null) {
            Integer n = ParseUtils.returnNumberOrNull(ebd.getNumberInExcess());
            if (n != null) {
                parsedMessage.setBagCount(parsedMessage.getBagCount() + n);
            }
        }
    }
    
    private void processAgencyInfo(ORG org) {
        if (org == null) {
            return;
        }
        
        AgencyVo agencyVo = new AgencyVo();
        agencyVo.setName(org.getAirlineCode());
        agencyVo.setLocation(org.getLocationCode());
        agencyVo.setIdentifier(org.getTravelAgentIdentifier());
        agencyVo.setCountry(org.getOriginatorCountryCode());
        if (agencyVo.isValid()) {
            parsedMessage.getAgencies().add(agencyVo);
        }
    }
}

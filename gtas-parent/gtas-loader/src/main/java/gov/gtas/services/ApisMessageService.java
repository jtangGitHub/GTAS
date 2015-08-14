package gov.gtas.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.EdifactMessage;
import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.edifact.MessageVo;
import gov.gtas.parsers.paxlst.ApisMessageVo;
import gov.gtas.parsers.paxlst.PaxlstParserUNedifact;
import gov.gtas.parsers.paxlst.PaxlstParserUSedifact;
import gov.gtas.parsers.util.FileUtils;
import gov.gtas.repository.ApisMessageRepository;

@Service
public class ApisMessageService implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(ApisMessageService.class);

    @Autowired
    private ApisMessageRepository msgDao;

    @Autowired
    private LoaderRepository loaderRepo;

    private ApisMessage apisMessage;
    
    public MessageVo parse(String filePath) {
        this.apisMessage = new ApisMessage();
        this.apisMessage.setCreateDate(new Date());
        this.apisMessage.setStatus(MessageStatus.RECEIVED);
        this.apisMessage.setFilePath(filePath);
        
        MessageVo vo = null;
        try {            
            byte[] raw = FileUtils.readSmallFile(filePath);
            String message = new String(raw, StandardCharsets.US_ASCII);

            EdifactParser<ApisMessageVo> parser = null;
            if (isUSEdifactFile(message)) {
                parser = new PaxlstParserUSedifact();
            } else {
                parser = new PaxlstParserUNedifact();                
            }
    
            vo = parser.parse(message);
            loaderRepo.checkHashCode(vo.getHashCode());

            this.apisMessage.setStatus(MessageStatus.PARSED);
            this.apisMessage.setHashCode(vo.getHashCode());
            EdifactMessage em = new EdifactMessage();
            em.setTransmissionDate(vo.getTransmissionDate());
            em.setTransmissionSource(vo.getTransmissionSource());
            em.setMessageType(vo.getMessageType());
            em.setVersion(vo.getVersion());
            this.apisMessage.setEdifactMessage(em);

        } catch (Exception e) {
            this.apisMessage.setStatus(MessageStatus.FAILED_PARSING);
            String stacktrace = ExceptionUtils.getStackTrace(e);
            this.apisMessage.setError(stacktrace);
            logger.error(stacktrace);
            return null;
        } finally {
            createMessage(apisMessage);
        }
        
        return vo;
    }

    public void load(MessageVo message) {
        ApisMessageVo m = (ApisMessageVo)message;
        try {
            loaderRepo.processReportingParties(this.apisMessage, m.getReportingParties());
            loaderRepo.processFlightsAndPassengers(this.apisMessage, m.getFlights(), m.getPassengers());
            this.apisMessage.setStatus(MessageStatus.LOADED);

        } catch (Exception e) {
            this.apisMessage.setStatus(MessageStatus.FAILED_LOADING);
            String stacktrace = ExceptionUtils.getStackTrace(e);
            this.apisMessage.setError(stacktrace);
            logger.error(stacktrace);
        } finally {
            createMessage(apisMessage);            
        }
    }  

    @Transactional
    private ApisMessage createMessage(ApisMessage m) {
        return msgDao.save(m);
    }

    private boolean isUSEdifactFile(String msg) {
        return (msg.contains("CDT") || msg.contains("PDT"));
    }
}

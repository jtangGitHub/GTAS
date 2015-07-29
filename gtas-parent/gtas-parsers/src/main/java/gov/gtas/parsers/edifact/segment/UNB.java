package gov.gtas.parsers.edifact.segment;

import java.util.Date;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Element;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.ParseUtils;

/**
 * <p>
 * UNB: INTERCHANGE HEADER
 * <p>
 * Function: To start, identify and specify an interchange. Specifies the sender
 * and intended recipient of the message.
 * <p>
 * Segment format should be standard across message types.
 * <p>
 * Examples:
 * <ul>
 * <li>UNB+UNOA:4+AIRLINE1+NZCS+130628:0900+000000001’
 * <li>UNB+IATA:1+DL++101209:2100+020A07'
 * </ul>
 */
public class UNB extends Segment {
    private static final String DATE_TIME_FORMAT = "yyMMddhhmm";

    private String sender;
    private String recipient;
    private Date dateAndTimeOfPreparation;

    public UNB(Composite[] composites) throws ParseException {
        super(UNB.class.getSimpleName(), composites);
        for (int i = 0; i < this.composites.length; i++) {
            Composite c = this.composites[i];
            Element[] e = c.getElements();
            switch (i) {
            case 1:
                if (c.getValue() != null) {
                    this.sender = c.getValue();
                } else if (e != null && e.length >= 1) {
                    this.sender = e[0].getValue();
                }
                break;
            case 2:
                if (c.getValue() != null) {
                    this.recipient = c.getValue();
                } else if (e != null && e.length >= 1) {
                    this.recipient = e[0].getValue();
                }
                break;
            case 3:
                String tmp = e[0].getValue() + e[1].getValue();
                this.dateAndTimeOfPreparation = ParseUtils.parseDateTime(tmp, DATE_TIME_FORMAT);
                break;
            }
        }
    }

    public String getSenderIdentification() {
        return sender;
    }

    public String getRecipientIdentification() {
        return recipient;
    }

    public Date getDateAndTimeOfPreparation() {
        return dateAndTimeOfPreparation;
    }
}

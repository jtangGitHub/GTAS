package gov.cbp.taspd.gtas.parsers.paxlst.segments;

import gov.cbp.taspd.gtas.parsers.unedifact.Composite;
import gov.cbp.taspd.gtas.parsers.unedifact.Element;
import gov.cbp.taspd.gtas.parsers.unedifact.Segment;

public class CNT extends Segment {
    public CNT(Composite[] composites) {
        super(CNT.class.getSimpleName(), composites);
        for (int i=0; i<this.composites.length; i++) {
            Composite c = this.composites[i];
            Element[] e = c.getElements();
            switch (i) {
            case 0:
            }
        }
    }

}
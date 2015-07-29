package gov.gtas.parsers.edifact;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.parsers.edifact.segment.UNA;
import gov.gtas.parsers.util.ParseUtils;

/**
 * Parses a segment text into composites and elements.
 */
public class SegmentTokenizer {
    private UNA una;
    
    @SuppressWarnings("unused")
    private SegmentTokenizer() { }
    
    public SegmentTokenizer(UNA una) {
        this.una = una;
    }
    
    public Composite[] tokenize(String segmentText) {
        if (StringUtils.isBlank(segmentText)) return null;
        
        String[] stringComposites = ParseUtils.splitWithEscapeChar(
                segmentText, 
                una.getDataElementSeparator(), 
                una.getReleaseCharacter()); 

        int numComposites = stringComposites.length;
        Composite[] rv = new Composite[numComposites];
        for (int i=0; i<numComposites; i++) {
            String[] elementsText = ParseUtils.splitWithEscapeChar(
                    stringComposites[i], 
                    una.getComponentDataElementSeparator(),
                    una.getReleaseCharacter());
            int numElements = elementsText.length;
            if (numElements == 1) {
                // hold single value in segment value field
                rv[i] = new Composite(elementsText[0].trim());
            } else { 
                // create array of elements
                Element[] elements = new Element[numElements];
                for (int j = 0; j < numElements; j++) {
                    elements[j] = new Element(elementsText[j].trim());
                }
                rv[i] = new Composite(elements);
            }
        }

        return rv;        
    }
}
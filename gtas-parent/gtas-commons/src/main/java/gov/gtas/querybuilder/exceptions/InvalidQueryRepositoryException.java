package gov.gtas.querybuilder.exceptions;

public class InvalidQueryRepositoryException extends Exception {

    private static final long serialVersionUID = 1L;
    private Object object;
    
    public InvalidQueryRepositoryException() { }
    
    public InvalidQueryRepositoryException(String message, Object object) {
        super(message);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
    
}

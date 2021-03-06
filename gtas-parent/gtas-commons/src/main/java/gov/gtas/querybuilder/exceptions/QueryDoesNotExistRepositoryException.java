package gov.gtas.querybuilder.exceptions;

import gov.gtas.querybuilder.model.UserQuery;

public class QueryDoesNotExistRepositoryException extends Exception {

    private static final long serialVersionUID = 1L;
    private UserQuery userQuery;
    
    public QueryDoesNotExistRepositoryException(String message, UserQuery userQuery) {
        super(message);
        this.userQuery = userQuery;
    }

    public UserQuery getUserQuery() {
        return userQuery;
    }
    
}

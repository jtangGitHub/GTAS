package gov.gtas.querybuilder.exceptions;

import gov.gtas.querybuilder.model.UserQuery;

public class QueryAlreadyExistsRepositoryException extends Exception {

    private static final long serialVersionUID = 1L;
    private UserQuery userQuery;
    
    public QueryAlreadyExistsRepositoryException(String message, UserQuery userQuery) {
        super(message);
        this.userQuery = userQuery;
    }

    public UserQuery getUserQuery() {
        return userQuery;
    }

}

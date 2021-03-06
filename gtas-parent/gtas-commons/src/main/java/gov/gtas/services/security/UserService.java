package gov.gtas.services.security;

import java.util.List;

public interface UserService {
    public UserData create(UserData user);

    public void delete(String id);

    public List<UserData> findAll();

    public UserData update(UserData user);

    public UserData findById(String id);
}

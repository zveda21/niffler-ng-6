package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.user.UserEntity;

import java.util.Optional;
import java.util.UUID;

public class UserDBClient {

    private final UserDao userDao = new UserDaoJdbc();

    public UserEntity create(UserEntity userEntity) { // The parameter must be changed to UserJson
        return userDao.createUser(userEntity);
    }

    public Optional<UserEntity> findById(UUID id) {
        return userDao.findById(id);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public void delete(UserEntity user) {
        userDao.delete(user);
    }

}

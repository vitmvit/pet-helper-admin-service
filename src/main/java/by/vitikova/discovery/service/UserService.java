package by.vitikova.discovery.service;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.update.PasswordUpdateDto;
import by.vitikova.discovery.update.UserUpdateDto;
import org.springframework.data.domain.Page;

public interface UserService {

    UserDto findByLogin(String login);

    UserDto findByLoginAndRole(String login, RoleName role);

    Page<UserDto> findAll(Integer offset, Integer limit);

    UserDto create(UserCreateDto userDto);

//    UserDto update(UserUpdateDto userDto);

    UserDto updatePassword(PasswordUpdateDto passwordUpdateDto);

    void delete(String login, String token);
}

package by.vitikova.discovery.service.impl;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.auth.SignUpCreateDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.exception.*;
import by.vitikova.discovery.feign.AuthClient;
import by.vitikova.discovery.feign.UserClient;
import by.vitikova.discovery.service.UserService;
import by.vitikova.discovery.update.PasswordUpdateDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static by.vitikova.discovery.constant.Constant.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserClient userClient;
    private final AuthClient authClient;

    /**
     * Находит пользователя по логину.
     *
     * @param login логин пользователя
     * @return объект UserDto, представляющий найденного пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Override
    public UserDto findByLogin(String login) {
        try {
            logger.info("UserService: find user by login: " + login);
            return userClient.findByLogin(login).getBody();
        } catch (Exception e) {
            logger.error("UserService: Entity not found error");
            throw new EntityNotFoundException();
        }
    }

    @Override
    public UserDto findByLoginAndRole(String login, RoleName role) {
        try {
            logger.info("UserService: find user by login and role: " + login);
            return userClient.findByLoginAndRole(login, role).getBody();
        } catch (Exception e) {
            logger.error("UserService: Entity not found error");
            throw new EntityNotFoundException();
        }
    }

    /**
     * Возвращает список пользователей с пагинацией.
     *
     * @param offset начальный индекс страницы
     * @param limit  количество элементов на странице
     * @return объект Page<UserDto>, представляющий список пользователей
     * @throws EmptyListException если список пользователей пуст
     */
    @Override
    public Page<UserDto> findAll(Integer offset, Integer limit) {
        try {
            logger.info("UserService: find all users");
            return userClient.findAll(offset, limit).getBody();
        } catch (Exception e) {
            logger.error("UserService: Empty list exception");
            throw new EmptyListException();
        }
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param dto объект UserCreateDto с данными для создания пользователя
     * @return объект UserDto созданного пользователя
     * @throws InvalidJwtException если произошла ошибка при создании пользователя
     * @throws NoAccessError       если у создаваемого пользователя указана роль ROLE_USER
     */
    @Transactional
    @Override
    public UserDto create(SignUpCreateDto dto) {
        if (!dto.password().equals(dto.passwordConfirm())) {
            throw new InvalidJwtException(PASSWORD_ERROR);
        }
        if (!dto.role().equals(RoleName.USER)) {
            try {
                logger.info("UserService: create user with login: " + dto.login());
                return authClient.signUp(dto).getBody();
            } catch (Exception e) {
                logger.error("UserService: Invalid jwt exception");
                throw new InvalidJwtException(USERNAME_IS_EXIST);
            }
        }
        logger.error("UserService: No access error");
        throw new NoAccessError();
    }

//    /**
//     * Обновляет данные пользователя.
//     *
//     * @param dto объект UserUpdateDto с данными для обновления пользователя
//     * @return объект UserDto обновленного пользователя
//     * @throws EntityNotFoundException если пользователь не найден
//     * @throws NoAccessError           если обновляемому пользователю указана роль ROLE_USER
//     */
//    @Override
//    public UserDto update(UserUpdateDto dto) {
//        try {
//            logger.info("UserService: update user with login: " + dto.getLogin());
//            var user = userClient.findByLogin(dto.getLogin()).getBody();
//            if (!user.getRole().equals(RoleName.USER)) {
//                return userClient.update(dto).getBody();
//            }
//            logger.error("UserService: No access error");
//            throw new NoAccessError();
//        } catch (Exception e) {
//            logger.error("UserService: Entity not found error");
//            throw new EntityNotFoundException();
//        }
//    }

    /**
     * Обновляет пароль пользователя.
     *
     * @param passwordUpdateDto информация об обновлении пароля
     * @return объект с обновленными данными пользователя типа UserDto
     * @throws EntityNotFoundException если не найдена сущность
     */
    @Transactional
    @Override
    public UserDto updatePassword(PasswordUpdateDto passwordUpdateDto) {
        try {
            logger.info("UserService: update password: " + passwordUpdateDto.getLogin());
            return userClient.updatePassword(passwordUpdateDto).getBody();
        } catch (Exception e) {
            logger.error("UserService: Entity not found error");
            throw new EntityNotFoundException();
        }
    }

    /**
     * Удаляет пользователя.
     *
     * @param login логин пользователя
     * @param token токен аутентификации
     * @throws DeleteException если удаление пользователя не удалось
     */
    @Transactional
    @Override
    public void delete(String login, String token) {
        try {
            logger.info("UserService: delete user with login: " + login);
            userClient.delete(login, token);
        } catch (Exception e) {
            logger.error("UserService: Delete exception");
            throw new DeleteException(DELETE_EXCEPTION);
        }
    }
}
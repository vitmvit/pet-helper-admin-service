package by.vitikova.discovery.feign;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.create.UserCreateDto;
import by.vitikova.discovery.update.PasswordUpdateDto;
import by.vitikova.discovery.update.UserUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static by.vitikova.discovery.constant.Constant.LIMIT_DEFAULT;
import static by.vitikova.discovery.constant.Constant.OFFSET_DEFAULT;

/**
 * Feign-клиент для взаимодействия с микросервисом управления пользователями
 */
@FeignClient(contextId = "userClient", value = "${feign.user-service.value}", url = "${feign.user-service.url}")
public interface UserClient {

    /**
     * Находит пользователя по его логину.
     *
     * @param login логин пользователя
     * @return объект ResponseEntity со статусом ответа и найденным пользователем в теле ответа
     */
    @GetMapping("/{login}")
    ResponseEntity<UserDto> findByLogin(@PathVariable("login") String login);

    /**
     * Находит пользователя по его логину и роли.
     *
     * @param login логин пользователя
     * @param role роль пользователя
     * @return объект ResponseEntity со статусом ответа и найденным пользователем в теле ответа
     */
    @GetMapping("/{login}/{role}")
    ResponseEntity<UserDto> findByLoginAndRole(@PathVariable("login") String login, @PathVariable("role") RoleName role);

    /**
     * Находит пользователей по дате последнего посещения.
     *
     * @param lastVisit дата последнего посещения, по которой будут искаться пользователи
     * @return объект ResponseEntity со статусом ответа и списком найденных пользователей в теле ответа
     */
    @GetMapping("/lastVisit")
    ResponseEntity<List<UserDto>> findUsersByLastVisit(@RequestParam LocalDateTime lastVisit);

    /**
     * Находит всех пользователей с пагинацией.
     *
     * @param offset смещение (начиная с какого элемента)
     * @param limit  количество элементов на странице
     * @return объект ResponseEntity со статусом ответа и списком пользователей на текущей странице в теле ответа
     */
    @GetMapping
    ResponseEntity<Page<UserDto>> findAll(@RequestParam(value = "offset", defaultValue = OFFSET_DEFAULT) Integer offset,
                                          @RequestParam(value = "limit", defaultValue = LIMIT_DEFAULT) Integer limit);

    /**
     * Создает нового пользователя.
     *
     * @param userCreateDto объект UserCreateDto, содержащий данные для создания пользователя
     * @return объект ResponseEntity со статусом ответа и созданным пользователем в теле ответа
     */
    @PostMapping
    ResponseEntity<UserDto> create(@RequestBody UserCreateDto userCreateDto);

//    /**
//     * Обновляет данные пользователя.
//     *
//     * @param userUpdateDto объект UserUpdateDto, содержащий обновленные данные пользователя
//     * @return объект ResponseEntity со статусом ответа и обновленным пользователем в теле ответа
//     */
//    @PutMapping
//    ResponseEntity<UserDto> update(@RequestBody UserUpdateDto userUpdateDto);

    /**
     * Обновляет пароль пользователя.
     *
     * @param dto информация о обновлении пароля
     * @return ResponseEntity с обновленными данными пользователя типа UserDto
     */
    @PutMapping("/password")
    ResponseEntity<UserDto> updatePassword(@RequestBody PasswordUpdateDto dto);

    /**
     * Удаляет пользователя по его логину.
     *
     * @param login логин пользователя, которого нужно удалить
     * @param auth  токен аутентификации, переданный в заголовке "Authorization"
     * @return объект ResponseEntity со статусом ответа
     */
    @DeleteMapping("/{login}")
    ResponseEntity<Void> delete(@PathVariable("login") String login, @RequestHeader("Authorization") String auth);

    /**
     * Удаляет список пользователей.
     *
     * @param list список пользователей, которых нужно удалить
     * @return объект ResponseEntity со статусом ответа
     */
    @DeleteMapping
    ResponseEntity<Void> deleteAll(@RequestBody List<UserDto> list);
}

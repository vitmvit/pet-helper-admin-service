# pet-helper-admin-service

[Точка входа в приложение](https://github.com/vitmvit/pet-helper-api-gateway-service)

Данный микросервис предоставляет функционал для работы с пользователями от лица администратора.

## Доступ

Роли:

- ADMIN

## Swagger

http://localhost:8083/api/doc/swagger-ui/index.html#/

## Порт

```text
8083
```

## UserController

Все операции подтягиваются с user-service с помощью feign-client

Контроллер поддерживает следующие операции:

- поиск пользователя по логину
- поиск пользователя по логину и роли
- поиск пользователей по последнему визиту (для шедулеров удаления)
- вывод всех пользователей
- создание пользователя
- обновление пароля
- удаление пользователя по логину
- удаление списка пользователей

### GET-запросы:

- findByLogin(@PathVariable("login") String login)
- findByLoginAndRole(@PathVariable("login") String login, @PathVariable("role") RoleName role)
- findUsersByLastVisit(@RequestParam LocalDateTime lastVisit)
- findAll(@RequestParam(value = "offset", defaultValue = OFFSET_DEFAULT) Integer offset, @RequestParam(value = "limit",
  defaultValue = LIMIT_DEFAULT) Integer limit)

### POST-запросы:

- create(@RequestBody UserCreateDto userCreateDto)

### PUT-запросы:

- updatePassword(@RequestBody PasswordUpdateDto dto)

### DELETE-запросы:

Не возвращают ничего:

- delete(@PathVariable("login") String login, @RequestHeader("Authorization") String auth)
- deleteAll(@RequestBody List<UserDto> list)
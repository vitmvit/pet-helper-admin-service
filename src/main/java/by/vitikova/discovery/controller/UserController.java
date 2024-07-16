package by.vitikova.discovery.controller;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.auth.SignUpCreateDto;
import by.vitikova.discovery.constant.RoleName;
import by.vitikova.discovery.service.UserService;
import by.vitikova.discovery.update.PasswordUpdateDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static by.vitikova.discovery.constant.Constant.LIMIT_DEFAULT;
import static by.vitikova.discovery.constant.Constant.OFFSET_DEFAULT;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
public class UserController {

    private final UserService userService;

    @GetMapping("/{login}")
    public ResponseEntity<UserDto> findByLogin(@PathVariable("login") String login) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByLogin(login));
    }

    @GetMapping("/{login}/{role}")
    public ResponseEntity<UserDto> findByLoginAndRole(@PathVariable("login") String login, @PathVariable("role") RoleName role) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByLoginAndRole(login, role));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> findAll(@RequestParam(value = "offset", defaultValue = OFFSET_DEFAULT) Integer offset,
                                                 @RequestParam(value = "limit", defaultValue = LIMIT_DEFAULT) Integer limit) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAll(offset, limit));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody SignUpCreateDto userCreateDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(userCreateDto));
    }

    @PutMapping("/password")
    public ResponseEntity<UserDto> updatePassword(@RequestBody PasswordUpdateDto dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updatePassword(dto));
    }

    @DeleteMapping("/{login}")
    public ResponseEntity<Void> delete(@PathVariable("login") String login, @RequestHeader("Authorization") String auth) {
        userService.delete(login, auth);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
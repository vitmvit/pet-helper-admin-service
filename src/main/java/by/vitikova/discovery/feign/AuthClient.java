package by.vitikova.discovery.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign-клиент для взаимодействия с микросервисом аутентификации
 */
@FeignClient(contextId = "authClient", value = "${feign.auth-service.value}", url = "${feign.auth-service.url}")
public interface AuthClient {

    /**
     * Проверяет действительность токена аутентификации.
     *
     * @param auth токен аутентификации, переданный в заголовке "Authorization"
     * @return объект ResponseEntity со статусом ответа и результатом проверки (true, если токен действителен, иначе - false)
     */
    @PostMapping("/check")
    ResponseEntity<Boolean> check(@RequestHeader("Authorization") String auth);
}
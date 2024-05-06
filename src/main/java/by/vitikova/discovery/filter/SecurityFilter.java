package by.vitikova.discovery.filter;

import by.vitikova.discovery.converter.UserConverter;
import by.vitikova.discovery.exception.InvalidJwtException;
import by.vitikova.discovery.feign.AuthClient;
import by.vitikova.discovery.model.entity.TokenPayload;
import by.vitikova.discovery.model.entity.User;
import by.vitikova.discovery.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

import static by.vitikova.discovery.constant.Constant.INVALID_TOKEN_ERROR;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private AuthClient authClient;
    private UserService userService;
    private UserConverter userConverter;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    /**
     * Метод, выполняющий перехват запросов и проверку токена
     *
     * @param request     объект HttpServletRequest, представляющий HTTP запрос
     * @param response    объект HttpServletResponse, представляющий HTTP ответ
     * @param filterChain объект FilterChain, представляющий цепочку фильтров
     * @throws ServletException если произошла ошибка в сервлете
     * @throws IOException      если произошла ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain){
        try {
            // Проверяем, является ли запрос запросом на Swagger UI
            if (request.getRequestURI().contains("/swagger-ui") || request.getRequestURI().contains("/api/doc") || request.getRequestURI().contains("/v3/api-docs")) {
                // Если да, пропускаем фильтр и передаем запрос дальше
                filterChain.doFilter(request, response);
                return;
            }
            var token = this.recoverToken(request);
            var login = getUsername(token);
            User user = userConverter.convert(userService.findByLogin(login));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (Boolean.FALSE.equals(authClient.check(token).getBody())) {
                throw new InvalidJwtException(INVALID_TOKEN_ERROR);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new InvalidJwtException(INVALID_TOKEN_ERROR);
        }
    }

    /**
     * Получает имя пользователя из токена.
     *
     * @param token строка, представляющая JWT токен
     * @return имя пользователя из токена
     * @throws JsonProcessingException при возникновении ошибки при разборе JSON
     */
    private String getUsername(String token) throws JsonProcessingException {
        String[] chinks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chinks[1]));
        TokenPayload tokenPayload = objectMapper.readValue(payload, TokenPayload.class);
        return tokenPayload.getUsername();
    }

    /**
     * Метод для извлечения токена из HTTP запроса
     *
     * @param request объект HttpServletRequest, представляющий HTTP запрос
     * @return строковое значение токена
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }
}
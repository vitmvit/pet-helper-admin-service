package by.vitikova.discovery.scheduler;

import by.vitikova.discovery.feign.UserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Планировщик задач для очистки пользователей.
 * <p>
 * Данный планировщик выполняет задачу периодической очистки пользователей на основе даты последнего посещения.
 * Использует Feign-клиент для взаимодействия с микросервисом управления пользователями.
 */
@Component
public class UserScheduler {

    private static final Logger log = LoggerFactory.getLogger(UserScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final UserClient userClient;

    @Value("${user-scheduler.year-period}")
    private int period;

    @Autowired
    public UserScheduler(UserClient userClient) {
        this.userClient = userClient;
    }

    /**
     * Выполняет очистку старых пользователей по расписанию.
     */
    @Scheduled(cron = "${user-scheduler.cron-expression}")
    public void cleanupOldUsers() {
        log.info("UserScheduler {}", dateFormat.format(new Date()));

        LocalDateTime threshold = LocalDateTime.now().minusYears(period);

        var userDtoList = userClient.findUsersByLastVisit(threshold).getBody();
        userClient.deleteAll(userDtoList);
    }
}
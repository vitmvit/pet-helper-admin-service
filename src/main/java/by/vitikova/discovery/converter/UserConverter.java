package by.vitikova.discovery.converter;

import by.vitikova.discovery.UserDto;
import by.vitikova.discovery.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {

    /**
     * Преобразование объекта UserDto в объект User
     *
     * @param source исходный комментарий типа UserDto
     * @return преобразованный комментарий типа User
     */
    User convert(UserDto source);
}

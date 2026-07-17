package su.ternovskii.notificationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.dto.response.NotificationResponse;
import su.ternovskii.notificationservice.entity.NotificationEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    NotificationEntity toEntity(NotificationRequest notificationRequest);

    NotificationResponse toResponse(NotificationEntity notificationEntity);

    List<NotificationResponse> toResponseList(List<NotificationEntity> notifications);
}

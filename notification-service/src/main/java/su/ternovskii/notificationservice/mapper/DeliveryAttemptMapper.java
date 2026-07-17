package su.ternovskii.notificationservice.mapper;

import org.mapstruct.Mapper;
import su.ternovskii.notificationservice.dto.response.DeliveryAttemptResponse;
import su.ternovskii.notificationservice.entity.DeliveryAttemptEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryAttemptMapper {

    DeliveryAttemptResponse toResponse(DeliveryAttemptEntity entity);

    List<DeliveryAttemptResponse> toResponseList(List<DeliveryAttemptEntity> entities);
}
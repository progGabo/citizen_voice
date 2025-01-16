package sk.tuke.service.dto;

import lombok.Data;
import sk.tuke.domain.enumeration.Status;
import java.io.Serializable;
import java.time.Instant;

@Data
public class EventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String description;

    private Instant dateFrom;

    private Instant dateTo;

    private Status status = Status.ACTIVE;

    private Instant createdAt;

    private Instant updatedAt;

    private Long cityId;

    private Long userId;
}

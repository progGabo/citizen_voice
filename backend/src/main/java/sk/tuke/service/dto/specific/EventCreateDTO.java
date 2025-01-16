package sk.tuke.service.dto.specific;


import lombok.Data;

import java.io.Serializable;
import java.time.Instant;


@Data
public class EventCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private Instant dateFrom;

    private Instant dateTo;

    private Long cityId;

}

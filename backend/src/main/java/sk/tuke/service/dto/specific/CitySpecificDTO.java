package sk.tuke.service.dto.specific;

import lombok.Data;

import java.io.Serializable;

@Data
public class CitySpecificDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

}

package sk.tuke.service.dto;

import lombok.Data;
import sk.tuke.service.dto.specific.CitySpecificDTO;

import java.io.Serializable;
import java.time.Instant;

@Data
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String street;

    private String postcode;

    private Integer houseNum;

    private Instant createdAt;

    private Instant updatedAt;

    private CityDTO city;
}

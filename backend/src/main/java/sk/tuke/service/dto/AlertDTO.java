package sk.tuke.service.dto;

import lombok.Data;
import sk.tuke.domain.enumeration.TypeEnum;

import java.io.Serializable;

@Data
public class AlertDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String template;

    private TypeEnum type;
}

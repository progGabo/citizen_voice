package sk.tuke.service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sk.tuke.domain.County;

import java.io.Serializable;
import java.time.Instant;

@Data
public class DistrictDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private Instant createdAt;

    private Instant updatedAt;

    private CountyDTO county;
}

package sk.tuke.service.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Data
public class CountyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private Instant createdAt;

    private Instant updatedAt;
}
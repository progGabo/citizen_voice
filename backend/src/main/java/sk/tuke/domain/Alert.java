package sk.tuke.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sk.tuke.domain.enumeration.TypeEnum;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(schema = "cv1", name = "alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "template", nullable = false)
    private String template;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeEnum type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert alert = (Alert) o;
        return Objects.equals(id, alert.id) && Objects.equals(template, alert.template) && Objects.equals(type, alert.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, template, type);
    }

    @Override
    public String toString() {
        return "Alert{" +
            "id=" + id +
            ", template='" + template + '\'' +
            ", type=" + type +
            '}';
    }
}

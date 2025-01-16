package sk.tuke.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(schema = "cv1", name = "signee")
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Signee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "date_of_signing", nullable = false, updatable = false)
    private Instant dateOfSigning;

    @NotNull
    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", referencedColumnName = "id")
    private Petition petition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signee signee = (Signee) o;
        return Objects.equals(id, signee.id) && Objects.equals(dateOfSigning, signee.dateOfSigning) && Objects.equals(createdAt, signee.createdAt) && Objects.equals(updatedAt, signee.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateOfSigning, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "CityDTO{" +
            "id=" + id +
            ", dateOfSigning='" + dateOfSigning +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}

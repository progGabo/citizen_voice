package sk.tuke.domain;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonStringType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import sk.tuke.domain.enumeration.Status;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "cv1", name = "petition")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Petition implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @EqualsAndHashCode.Include
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "no_signees", nullable = false)
    private Integer noSignees;

    @NotNull
    @Column(name = "required_signees")
    private Integer requiredSignees;

    @Type(JsonStringType.class)
    @Column(name = "content", nullable = false)
    private JsonNode content;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "authority_notified")
    private Boolean authorityNotified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private City city;

    @OneToMany(mappedBy = "petition", fetch = FetchType.LAZY)
    private List<Signee> signees = new ArrayList<>();

    @Override
    public String toString() {
        return "Petition{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", noSignees=" + noSignees +
            ", content=" + content +
            ", isActive=" + status +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }

    public void upSignCount() {
        this.setNoSignees(getNoSignees() + 1);
    }
}

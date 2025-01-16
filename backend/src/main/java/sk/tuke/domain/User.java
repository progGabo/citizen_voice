package sk.tuke.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.domain.enumeration.AuthRole;
import sk.tuke.domain.enumeration.Gender;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A user.
 */
@Entity
@Table(name = "users", schema = "cv1")
@Getter
@Setter
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true, nullable = false, name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @JsonIgnore
    @NotNull
    @Size(min = 5, max = 120)
    @Column(name = "passwd", nullable = false)
    private String passwd;

    @Column(name = "title")
    private String title;

    @Size(max = 255)
    @Column(name = "firstname", length = 255)
    private String firstName;

    @Size(max = 255)
    @Column(name = "lastname", length = 255)
    private String lastName;

    @Column(name = "alerts_on")
    private Boolean alertsOn = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "active_status", nullable = false)
    private ActiveStatus activeStatus = ActiveStatus.NONE;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false)
    private AuthRole role;

    @ManyToOne()
    private City city;

    @OneToOne(cascade = {CascadeType.ALL})
    private Address address;

    @OneToMany(fetch =  FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Signee> signings = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", genderType=" + gender +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", isActivated=" + activeStatus +
            ", dateOfBirth=" + dateOfBirth +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}

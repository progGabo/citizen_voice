package sk.tuke.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sk.tuke.domain.enumeration.Status;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "cv1", name = "voting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "vote_count", nullable = false)
    private Integer voteCount;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne()
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "voting")
    private List<VotingQuestion> voteQuestions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voting voting = (Voting) o;
        return Objects.equals(id, voting.id) && Objects.equals(content, voting.content) && Objects.equals(voteCount, voting.voteCount) && Objects.equals(title, voting.title) && Objects.equals(createdAt, voting.createdAt) && Objects.equals(updatedAt, voting.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, voteCount, title, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Voting{" +
            "id=" + id +
            ", content=" + content +
            ", voteCount=" + voteCount +
            ", title='" + title + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}

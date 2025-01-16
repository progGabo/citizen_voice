package sk.tuke.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "cv1", name = "voting_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VotingQuestion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "vote_count", nullable = false)
    private Integer voteCount;

    @NotNull
    @Column(name = "mandatory", nullable = false)
    private Boolean mandatory;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_id", nullable = false)
    private Voting voting;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "voteQuestion")
    private List<VotingAnswers> voteAnswers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VotingQuestion that = (VotingQuestion) o;
        return Objects.equals(id, that.id) && Objects.equals(content, that.content) && Objects.equals(voteCount, that.voteCount) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, voteCount, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "VoteQuestion{" +
            "id=" + id +
            ", content='" + content + '\'' +
            ", voteCount=" + voteCount +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}

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
import java.util.Objects;

@Entity
@Table(schema = "cv1", name = "voting_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VotingAnswers implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "vote_count", nullable = false)
    private Integer voteCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_q_id")
    private VotingQuestion voteQuestion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VotingAnswers that = (VotingAnswers) o;
        return Objects.equals(id, that.id) && Objects.equals(content, that.content) && Objects.equals(voteCount, that.voteCount) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, voteCount, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "VoteAnswers{" +
            "id=" + id +
            ", content='" + content + '\'' +
            ", voteCount=" + voteCount +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}

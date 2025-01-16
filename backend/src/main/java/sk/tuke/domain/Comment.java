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
@Table(schema = "cv1", name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "comment", nullable = false)
    private String comment;

    @NotNull
    @Column(name = "likes", nullable = false)
    private Integer likes;

    @NotNull
    @Column(name = "comment_date", nullable = false)
    private Instant commentDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne()
    private User user;

    @ManyToOne()
    private Article article;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment1 = (Comment) o;
        return Objects.equals(id, comment1.id) && Objects.equals(comment, comment1.comment) && Objects.equals(likes, comment1.likes) && Objects.equals(commentDate, comment1.commentDate) && Objects.equals(createdAt, comment1.createdAt) && Objects.equals(updatedAt, comment1.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comment, likes, commentDate, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + id +
            ", comment='" + comment + '\'' +
            ", likes=" + likes +
            ", commentDate=" + commentDate +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}

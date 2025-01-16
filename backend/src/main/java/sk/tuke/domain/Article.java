package sk.tuke.domain;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonStringType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.domain.enumeration.Status;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(schema = "cv1", name = "article")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(JsonStringType.class)
    @Column(name = "content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "likes", nullable = false)
    private Integer likes;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name= "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @Column(name = "publish_date", nullable = false)
    private Instant publishDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne
    private City city;

    @ManyToOne()
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id) && Objects.equals(content, article.content) && Objects.equals(likes, article.likes) && Objects.equals(title, article.title) && Objects.equals(publishDate, article.publishDate) && Objects.equals(createdAt, article.createdAt) && Objects.equals(updatedAt, article.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, likes, title, publishDate, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Article{" +
            "id=" + id +
            ", content=" + content +
            ", likes=" + likes +
            ", title='" + title + '\'' +
            ", publishDate=" + publishDate +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}

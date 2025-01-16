package sk.tuke.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(schema = "cv1", name = "user_liked_articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLikedArticles implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    private Article article;
}

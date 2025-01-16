package sk.tuke.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(schema = "cv1", name = "user_voting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVoting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "vote_answer_id", nullable = false)
    private Long votingAnswerId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // tu treba vztahy/join table idk
}

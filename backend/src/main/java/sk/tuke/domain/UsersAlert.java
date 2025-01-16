package sk.tuke.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(schema = "cv1", name = "users_alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersAlert implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    private User user;

    @ManyToOne()
    private Alert alert;

}

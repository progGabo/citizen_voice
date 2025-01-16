package sk.tuke.service.dto;

import lombok.Data;
import sk.tuke.domain.enumeration.Status;
import java.io.Serializable;
import java.time.Instant;

@Data
public class ArticleDTO implements Serializable {

    private Long id;

    private String content;

    private Integer likes;

    private String title;

    private Instant publishDate;

    private Instant createdAt;

    private Instant updatedAt;

    private Status status;

    private String firstname;
    private String lastname;

    private Long userId;

    private Long cityId;
}

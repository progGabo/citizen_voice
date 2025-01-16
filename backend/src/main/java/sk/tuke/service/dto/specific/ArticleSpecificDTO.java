package sk.tuke.service.dto.specific;

import lombok.Data;
import sk.tuke.domain.enumeration.Status;

import java.io.Serializable;
import java.time.Instant;

@Data
public class ArticleSpecificDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private Integer likes;

    private String content;

    private Status status;

    private Instant publishDate;

    private String firstname;

    private String lastname;
}

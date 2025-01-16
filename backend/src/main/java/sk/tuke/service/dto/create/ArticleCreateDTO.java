package sk.tuke.service.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
@Data
public class ArticleCreateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String content;

    private String title;

    private Instant publishDate;
}


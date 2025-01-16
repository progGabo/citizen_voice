package sk.tuke.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class CommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String comment;

    private Integer likes;

    private Instant commentDate;

    private Instant createdAt;

    private Instant updatedAt;

    private UserInfoDTO user;

    private ArticleDTO article;
}

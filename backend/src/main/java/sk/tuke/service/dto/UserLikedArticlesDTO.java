package sk.tuke.service.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLikedArticlesDTO implements Serializable {
    private Long userId;

    private Long articleId;
}

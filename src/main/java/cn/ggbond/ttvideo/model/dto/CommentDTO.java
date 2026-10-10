package cn.ggbond.ttvideo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CommentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 视频 id
     */
    @NotNull
    private Long videoId;

    /**
     * 父评论 id（可选，回复某条评论时传入；不传为顶级评论）
     */
    private Long parentId;

    /**
     * 回复目标用户 id（可选，回复某用户时传入；不传为顶级评论）
     */
    private Long replyToUserId;


    /**
     * 评论内容
     */
    @NotBlank
    private String content;
}

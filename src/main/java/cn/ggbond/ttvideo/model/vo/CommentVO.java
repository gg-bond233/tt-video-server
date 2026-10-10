package cn.ggbond.ttvideo.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class CommentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评论 id
     */
    private Long commentId;


    /**
     * 发表用户 id（前端回复时用作 replyToUserId）
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTime;

    /**
     * 点赞数量
     */
    private Long likeCount;

    /**
     * 子回复列表（仅顶级评论有；回复本身不再嵌套）
     */
    private List<CommentVO> replies;

}

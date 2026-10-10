package cn.ggbond.ttvideo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_comment")
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /*
    * 评论id
    * */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /*
    * 视频id
    * */
    @TableField("video_id")
    private Long videoId;

    /**
     * 父评论 id（顶级评论为 NULL）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 回复目标用户 id（顶级评论为 NULL）
     */
    @TableField("reply_to_user_id")
    private Long replyToUserId;

    /*
    * 用户id
    * */
    @TableField("user_id")
    private Long userId;

    /*
    * 评论内容
    * */
    @TableField("content")
    private String content;

    /*
    * 评论时间
    * */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;

    /*
    * 评论点赞数量
    * */
    @TableField("like_count")
    private Long likeCount;
}

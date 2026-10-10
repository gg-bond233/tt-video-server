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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 视频实体，对应 tb_video 表
 * </p>
 *
 * @author ttvideo
 * @since 2025-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_video")
public class Video implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 视频 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long videoId;

    /**
     * 视频名称
     */
    @TableField("name")
    private String videoName;

    /**
     * 上传者名称
     */
    @TableField("uploader_name")
    private String uploaderName;

    /**
     * 分类：动漫 / 少儿 / 综艺
     */
    @TableField("category")
    private String category;

    /**
     * 时长（秒数）
     */
    @TableField("duration")
    private String duration;

    /**
     * 缩略图 url
     */
    @TableField("thumbnail_url")
    private String thumbnailUrl;

    /**
     * 视频 url
     */
    @TableField("video_url")
    private String videoUrl;

    /**
     * 视频简介（映射到 intro 列）
     */
    @TableField("intro")
    private String intro;

    /**
     * 发布日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("release_time")
    private LocalDate releaseTime;

    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;

}

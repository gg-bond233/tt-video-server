package cn.ggbond.ttvideo.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 视频列表项 VO，与前端 Video 接口对齐
 */
@Data
public class VideoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 视频 id
     */
    private Long videoId;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 上传者名称
     */
    private String uploaderName;

    /**
     * 分类：动漫 / 少儿 / 综艺
     */
    private String category;

    /**
     * 时长（秒数）
     */
    private String duration;

    /**
     * 缩略图 url
     */
    private String thumbnailUrl;

    /**
     * 视频 url
     */
    private String videoUrl;

    /**
     * 喜欢状态：0 未喜欢，1 已喜欢
     */
    private Integer likeStatus;

    /**
     * 收藏状态：0 未收藏，1 已收藏
     */
    private Integer collectStatus;

    /**
     * 发布日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseTime;

}

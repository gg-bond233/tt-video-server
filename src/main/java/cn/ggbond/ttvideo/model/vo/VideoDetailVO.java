package cn.ggbond.ttvideo.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 视频详情 VO，与前端 VideoDetail 接口对齐
 * = VideoVO 所有字段 + intro（视频简介） + comments（评论列表）
 */
@Data
public class VideoDetailVO implements Serializable {

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
     * 视频简介（对应数据库 lyric 列）
     */
    private String intro;

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
     * 发布日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseTime;

    /**
     * 喜欢状态：0 未喜欢，1 已喜欢
     */
    private Integer likeStatus;

    /**
     * 收藏状态：0 未收藏，1 已收藏
     */
    private Integer collectStatus;

    /**
     * 评论列表
     */
    private List<CommentVO> comments;

}

package cn.ggbond.ttvideo.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 视频分页查询参数
 * getAllVideos: { pageNum, pageSize, videoName?, category? }
 * getVideosByCategory: { category, pageNum, pageSize, videoName? }
 * getLikedVideos / getCollectedVideos: { pageNum, pageSize }
 */
@Data
public class VideoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    @NotNull
    private Integer pageNum;

    /**
     * 每页数量
     */
    @NotNull
    private Integer pageSize;

    /**
     * 视频名（可选，模糊查询）
     */
    private String videoName;

    /**
     * 分类（可选）：动漫 / 少儿 / 综艺
     */
    private String category;

}

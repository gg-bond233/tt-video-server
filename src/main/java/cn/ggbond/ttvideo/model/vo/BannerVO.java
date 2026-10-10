package cn.ggbond.ttvideo.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 轮播图 VO，与前端 banner 结构对齐：
 * { bannerId, bannerUrl, videoId, title, category, uploader }
 * 其中 videoId/title/category/uploader 来自关联的 tb_video 记录。
 */
@Data
public class BannerVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 轮播图 id
     */
    private Long bannerId;

    /**
     * 轮播图 url
     */
    private String bannerUrl;

    /**
     * 轮播图状态：0-启用，1-禁用（管理端列表展示用）
     */
    private Integer bannerStatus;

    /**
     * 关联视频 id（前端用于跳转视频详情）
     */
    private Long videoId;

    /**
     * 标题（取自关联视频名称）
     */
    private String title;

    /**
     * 分类（取自关联视频分类）
     */
    private String category;

    /**
     * 上传者（取自关联视频的上传者名称，前端 hero 副标题展示）
     */
    private String uploader;

}

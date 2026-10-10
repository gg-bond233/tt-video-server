package cn.ggbond.ttvideo.model.dto;


import cn.ggbond.ttvideo.constant.MessageConstant;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 视频新增 / 修改参数
 */
@Data
public class VideoAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 视频 id（新增时为空，修改时必填）
     */
    private Long videoId;

    /**
     * 视频名称
     */
    @NotBlank(message = MessageConstant.NOT_NULL)
    private String videoName;

    /**
     * 上传者 / UP主名称
     */
    @NotBlank(message = MessageConstant.NOT_NULL)
    private String uploaderName;

    /**
     * 分类：动漫 / 少儿 / 综艺
     */
    @NotBlank(message = MessageConstant.NOT_NULL)
    private String category;

    /**
     * 视频简介（可空）
     */
    private String intro;

    /**
     * 视频时长（秒数字符串，可空，上传视频文件时由后端填充）
     */
    private String duration;

    /**
     * 发布日期
     */
    @NotNull(message = MessageConstant.NOT_NULL)
    private LocalDate releaseTime;

}

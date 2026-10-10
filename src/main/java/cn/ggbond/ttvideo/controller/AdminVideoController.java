package cn.ggbond.ttvideo.controller;

import cn.ggbond.ttvideo.model.dto.VideoAddDTO;
import cn.ggbond.ttvideo.model.dto.VideoDTO;
import cn.ggbond.ttvideo.model.vo.VideoVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IAdminVideoService;
import cn.ggbond.ttvideo.service.MinioService;
import cn.ggbond.ttvideo.util.BindingResultUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminVideoController {
    private final IAdminVideoService adminVideoService;
    private final MinioService minioService;

    /**
     * 获取视频数量（可选按分类）
     *
     * @param category 分类（可选）
     * @return 视频数量
     */
    @GetMapping("/admin/getAllVideosCount")
    public Result<Long> getAllVideoCount(@RequestParam(required = false) String category){
        return adminVideoService.getAllVideosCount(category);
    }

    /**
     * 分页查询视频列表（管理端，支持按视频名 / 分类搜索）
     *
     * @param videoDTO 视频查询条件
     * @return 视频分页信息
     */
    @PostMapping("/admin/getAllVideos")
    public Result<PageResult<VideoVO>> getAllVideos(@RequestBody @Valid VideoDTO videoDTO){
        return adminVideoService.getAllVideos(videoDTO);
    }
    /**
     * 新增视频
     *
     * @param videoAddDTO   视频信息
     * @param bindingResult 绑定结果
     * @return 结果
     */
    @PostMapping("/admin/addVideo")
    public Result addVideo(@RequestBody @Valid VideoAddDTO videoAddDTO, BindingResult bindingResult){
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if(errorMessage!=null){
            return Result.error(errorMessage);
        }
        return adminVideoService.addVideo(videoAddDTO);
    }

    /**
     * 修改视频信息
     *
     * @param videoAddDTO   视频信息
     * @param bindingResult 绑定结果
     * @return 结果
     */
    @PutMapping("/admin/updateVideo")
    public Result updateVideo(@RequestBody @Valid VideoAddDTO videoAddDTO, BindingResult bindingResult){
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if(errorMessage!=null){
            return Result.error(errorMessage);
        }
        return adminVideoService.updateVideo(videoAddDTO);
    }

    /**
     * 更新视频封面（缩略图）
     *
     * @param videoId 视频 id
     * @param cover   封面图片文件
     * @return 结果
     */
    @PatchMapping("/admin/updateVideoCover/{id}")
    public Result updateVideoCover(@PathVariable("id") Long videoId,@RequestParam("cover") MultipartFile cover){
        String coverUrl = minioService.uploadFile(cover, "videoThumbnails");
        return adminVideoService.updateVideoCover(videoId, coverUrl);
    }

    /**
     * 更新视频文件
     *
     * @param videoId  视频 id
     * @param video    视频文件
     * @param duration 视频时长（秒数，字符串）
     * @return 结果
     */
    @PatchMapping("/admin/updateVideoFile/{id}")
    public Result updateVideoFile(@PathVariable("id") Long videoId,
                                  @RequestParam("video") MultipartFile video,
                                  @RequestParam("duration")String duration){
        String videoUrl = minioService.uploadFile(video, "videos");
        return adminVideoService.updateVideoFile(videoId,videoUrl,duration);

    }

    /**
     * 删除视频
     *
     * @param videoId 视频 id
     * @return 结果
     */
    @DeleteMapping("/admin/deleteVideo/{id}")
    public Result deleteVideo(@PathVariable("id") Long videoId) {
        return adminVideoService.deleteVideo(videoId);
    }
    /**
     * 批量删除视频
     *
     * @param videoIds 视频 id 列表
     * @return 结果
     */
    @DeleteMapping("/admin/deleteVideos")
    public Result deleteVideos(@RequestBody List<Long> videoIds) {
        return adminVideoService.deleteVideos(videoIds);
    }
}

package cn.ggbond.ttvideo.controller;

import cn.ggbond.ttvideo.model.dto.VideoDTO;
import cn.ggbond.ttvideo.model.vo.VideoDetailVO;
import cn.ggbond.ttvideo.model.vo.VideoVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IUserCollectionService;
import cn.ggbond.ttvideo.service.IUserLikeService;
import cn.ggbond.ttvideo.service.IVideoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private final IVideoService videoService;

    private final IUserLikeService userLikeService;

    private final IUserCollectionService userCollectionService;
    /**
     * 获取所有视频（支持按分类/关键词分页查询）
     */
    @PostMapping("/getAllVideos")
    public Result<PageResult<VideoVO>> getAllVideos(@RequestBody @Valid VideoDTO videoDTO, HttpServletRequest request){
        return videoService.getAllVideos(videoDTO,request);
    }

    /**
     * 获取推荐视频
     */
    @GetMapping("/getRecommendedVideos")
    public Result<List<VideoVO>> getRecommendedVideos(HttpServletRequest request){
        return videoService.getRecommendedVideos(request);
    }

    /**
     * 按分类获取视频
     */
    @PostMapping("/getVideosByCategory")
    public Result<PageResult<VideoVO>> getVideosByCategory(@RequestBody @Valid VideoDTO videoDTO, HttpServletRequest request){
        return videoService.getVideoByCategory(videoDTO,request);
    }

    /**
     * 获取视频详情
     */
    @GetMapping("/getVideoDetail/{id}")
    public Result<VideoDetailVO> getVideoDetail(@PathVariable("id")Long videoId, HttpServletRequest request){
        return videoService.getVideoDetail(videoId,request);
    }
    /**
     * 喜欢视频
     */
    @PostMapping("/like")
    public Result like(@RequestParam Long videoId){
        return userLikeService.like(videoId);
    }

    /*
    * 取消喜欢视频
    * */
    @DeleteMapping("/cancelLike")
    public Result cancelLike(@RequestParam Long videoId){
        return userLikeService.cancelLike(videoId);
    }

    /**
     * 获取当前用户喜欢的视频列表
     */
    @PostMapping("/getLikedVideos")
    public Result<PageResult<VideoVO>> getLikedVideos(@RequestBody @Valid VideoDTO videoDTO){
        return videoService.getLikedVideos(videoDTO);
    }

    /*
    * 收藏视频
    * */
    @PostMapping("/collect")
    public Result collect(@RequestParam Long videoId){
        return userCollectionService.collect(videoId);
    }

    /*
    * 取消收藏视频
    * */
    @DeleteMapping("/cancelCollect")
    public Result cancelCollect(@RequestParam Long videoId){
        return userCollectionService.cancelCollect(videoId);
    }

    /**
     * 获取当前用户收藏的视频列表
     */
    @PostMapping("/getCollectedVideos")
    public Result<PageResult<VideoVO>> getCollectedVideos(@RequestBody @Valid VideoDTO videoDTO){
        return videoService.getCollectedVideos(videoDTO);
    }
}

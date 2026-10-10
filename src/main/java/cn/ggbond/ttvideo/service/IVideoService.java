package cn.ggbond.ttvideo.service;

import cn.ggbond.ttvideo.model.dto.VideoDTO;
import cn.ggbond.ttvideo.model.entity.Video;
import cn.ggbond.ttvideo.model.vo.VideoDetailVO;
import cn.ggbond.ttvideo.model.vo.VideoVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IVideoService extends IService<Video> {
    //获取所有歌曲
    Result<PageResult<VideoVO>> getAllVideos(VideoDTO videoDTO, HttpServletRequest request);

    //获取推荐视频
    Result<List<VideoVO>> getRecommendedVideos(HttpServletRequest request);

    //按分类获取视频
    Result<PageResult<VideoVO>> getVideoByCategory(VideoDTO videoDTO, HttpServletRequest request);

    //获取视频详情
    Result<VideoDetailVO> getVideoDetail(Long videoId, HttpServletRequest request);

    //获取喜欢视频列表
    Result<PageResult<VideoVO>> getLikedVideos(VideoDTO videoDTO);

    //获取收藏视频列表
    Result<PageResult<VideoVO>> getCollectedVideos(VideoDTO videoDTO);
}

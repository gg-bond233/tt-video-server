package cn.ggbond.ttvideo.service;

import cn.ggbond.ttvideo.model.dto.VideoAddDTO;
import cn.ggbond.ttvideo.model.dto.VideoDTO;
import cn.ggbond.ttvideo.model.vo.VideoVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;

import java.util.List;

public interface IAdminVideoService {
    // 管理端：获取视频数量（可选按分类）
    Result<Long> getAllVideosCount(String category);

    // 管理端：分页查询视频
    Result<PageResult<VideoVO>> getAllVideos(VideoDTO videoDTO);

    // 管理端：新增视频
    Result addVideo(VideoAddDTO videoAddDTO);

    // 管理端：修改视频信息
    Result updateVideo(VideoAddDTO videoAddDTO);

    // 管理端：更新视频封面
    Result updateVideoCover(Long videoId, String coverUrl);

    // 管理端：更新视频文件
    Result updateVideoFile(Long videoId, String videoUrl, String duration);

    // 管理端：删除视频
    Result deleteVideo(Long videoId);

    //管理端：批量删除视频
    Result deleteVideos(List<Long> videoIds);
}

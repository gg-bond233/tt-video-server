package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.mapper.VideoMapper;
import cn.ggbond.ttvideo.model.dto.VideoAddDTO;
import cn.ggbond.ttvideo.model.dto.VideoDTO;
import cn.ggbond.ttvideo.model.entity.Video;
import cn.ggbond.ttvideo.model.vo.VideoVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IAdminVideoService;
import cn.ggbond.ttvideo.service.MinioService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@CacheConfig(cacheNames = "videoCache")
@RequiredArgsConstructor
public class AdminVideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IAdminVideoService {
    private final VideoMapper videoMapper;
    private final MinioService minioService;

    /**
     * 管理端：获取视频数量（可选按分类）
     */
    @Override
    public Result<Long> getAllVideosCount(String category) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        return Result.success(videoMapper.selectCount(queryWrapper));
    }

    /**
     * 管理端：分页查询视频
     */
    @Override
    @Cacheable(key = "'admin-' + #videoDTO.pageNum + '-' + #videoDTO.pageSize + '-' + #videoDTO.videoName + '-' + #videoDTO.category")
    public Result<PageResult<VideoVO>> getAllVideos(VideoDTO videoDTO) {
        Page<VideoVO> page = new Page<>(videoDTO.getPageNum(), videoDTO.getPageSize());
        IPage<VideoVO> videoPage=videoMapper.getVideos(page,videoDTO.getVideoName(),videoDTO.getCategory());
        if (videoPage.getRecords().isEmpty()) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }
        return Result.success(new PageResult<>(videoPage.getTotal(), videoPage.getRecords()));
    }

    /**
     * 管理端：新增视频
     */
    @Override
    @CacheEvict(allEntries = true)
    public Result addVideo(VideoAddDTO videoAddDTO) {
        Video video = new Video();
        video.setVideoName(videoAddDTO.getVideoName())
                .setUploaderName(videoAddDTO.getUploaderName())
                .setCategory(videoAddDTO.getCategory())
                .setIntro(videoAddDTO.getIntro())
                .setDuration(videoAddDTO.getDuration())
                .setReleaseTime(videoAddDTO.getReleaseTime())
                .setCreateTime(LocalDateTime.now());

        if(videoMapper.insert(video)==0){
            return Result.error(MessageConstant.ADD + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS);
    }

    /**
     * 管理端：修改视频信息
     */
    @Override
    @CacheEvict(allEntries = true)
    public Result updateVideo(VideoAddDTO videoAddDTO) {
        Video video = new Video();
        video.setVideoId(videoAddDTO.getVideoId())
                .setVideoName(videoAddDTO.getVideoName())
                .setUploaderName(videoAddDTO.getUploaderName())
                .setCategory(videoAddDTO.getCategory())
                .setIntro(videoAddDTO.getIntro())
                .setDuration(videoAddDTO.getDuration())
                .setReleaseTime(videoAddDTO.getReleaseTime());

        if(videoMapper.updateById(video)==0){
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }

    /**
     * 管理端：更新视频封面
     */
    @Override
    @CacheEvict(allEntries = true)
    public Result updateVideoCover(Long videoId, String coverUrl) {
        Video video=new Video();
        video.setVideoId(videoId).setThumbnailUrl(coverUrl);

        if (videoMapper.updateById(video)==0){
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }

    /**
     * 管理端：更新视频文件
     */
    @Override
    @CacheEvict(allEntries = true)
    public Result updateVideoFile(Long videoId, String videoUrl, String duration) {
        Video video=new Video();
        video.setVideoId(videoId).setVideoUrl(videoUrl).setDuration(duration);
        if (videoMapper.updateById(video)==0){
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }

    /**
     * 管理端：删除视频（同时清理 MinIO 中的封面与视频文件）
     */
    @Override
    @CacheEvict(allEntries = true)
    public Result deleteVideo(Long videoId) {
        Video video = videoMapper.selectById(videoId);
        if(video==null){
            return Result.error(MessageConstant.NOT_FOUND);
        }
        if (video.getThumbnailUrl()!=null && !video.getThumbnailUrl().isEmpty()){
            minioService.deleteFile(video.getThumbnailUrl());
        }
        if (video.getVideoUrl()!=null && !video.getVideoUrl().isEmpty()){
            minioService.deleteFile(video.getVideoUrl());
        }
        if (videoMapper.deleteById(videoId)==0){
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }

    /**
     * 管理端：批量删除视频
     */
    @Override
    @CacheEvict(allEntries = true)
    public Result deleteVideos(List<Long> videoIds) {
        for (Long videoId : videoIds) {
            Video video = videoMapper.selectById(videoId);
            if (video != null) {
                if (video.getThumbnailUrl() != null && !video.getThumbnailUrl().isEmpty()) {
                    minioService.deleteFile(video.getThumbnailUrl());
                }
                if (video.getVideoUrl() != null && !video.getVideoUrl().isEmpty()) {
                    minioService.deleteFile(video.getVideoUrl());
                }
            }
        }
        if (videoMapper.deleteByIds(videoIds) == 0) {
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }
}

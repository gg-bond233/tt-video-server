package cn.ggbond.ttvideo.mapper;

import cn.ggbond.ttvideo.model.entity.Video;
import cn.ggbond.ttvideo.model.vo.VideoDetailVO;
import cn.ggbond.ttvideo.model.vo.VideoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {

    /**
     * 分页查询视频列表，支持按视频名与分类过滤
     */
    @Select("""
            SELECT
                v.id            AS videoId,
                v.name          AS videoName,
                v.uploader_name AS uploaderName,
                v.category      AS category,
                v.duration      AS duration,
                v.thumbnail_url AS thumbnailUrl,
                v.video_url     AS videoUrl,
                v.release_time  AS releaseTime
            FROM tb_video v
            WHERE
                (#{videoName} IS NULL OR #{videoName} = '' OR v.name LIKE CONCAT('%', #{videoName}, '%'))
                AND (#{category} IS NULL OR #{category} = '' OR v.category = #{category})
            ORDER BY v.release_time DESC, v.id DESC
            """)
    IPage<VideoVO> getVideos(Page<VideoVO> page,
                             @Param("videoName") String videoName,
                             @Param("category") String category);

    /**
     * 随机获取指定数量的视频
     *
     * @return
     */
    @Select("""
           SELECT 
                v.id             AS videoId,
                v.name           AS videoName,
                v.uploader_name  AS uploaderName,
                v.category       AS category,
                v.duration       AS duration,
                v.thumbnail_url  AS thumbnailUrl,
                v.video_url     AS videoUrl,
                v.release_time  AS releaseTime
           FROM tb_video v           
           ORDER BY RAND()
           LIMIT #{limit}
            """)
    List<VideoVO> getRandomVideos(@Param("limit") int limit);


    /**
     * 根据视频 id 获取视频详情（含评论列表），定义在 VideoMapper.xml
     */
    VideoDetailVO getVideoDetailById(@Param("videoId") Long videoId);

    /**
     * 根据视频 id 列表分页查询视频，定义在 VideoMapper.xml
     */
    IPage<VideoVO> getVideosByIds(Page<VideoVO> page, @Param("videoIds") List<Long> likedVideoIds);
}

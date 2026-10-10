package cn.ggbond.ttvideo.mapper;

import cn.ggbond.ttvideo.model.entity.Banner;
import cn.ggbond.ttvideo.model.vo.BannerVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BannerMapper extends BaseMapper<Banner> {


    /**
     * 查询启用的轮播图列表，并 LEFT JOIN tb_video 填充关联视频信息
     * （videoId / title / category / uploader），供用户端首页 hero 使用。
     * 结果按 banner id 倒序，最多 9 条。
     */
    @Select("""
            SELECT
                b.id          AS bannerId,
                b.banner_url  AS bannerUrl,
                v.id          AS videoId,
                v.name        AS title,
                v.category    AS category,
                v.uploader_name AS uploader
            FROM tb_banner b
            LEFT JOIN tb_video v ON b.video_id = v.id
            WHERE b.status = 0
            ORDER BY b.id DESC
            LIMIT 9
            """)
    List<BannerVO> getBannerVOList();

    /**
     * 管理端分页查询轮播图，LEFT JOIN tb_video 填充关联视频信息
     * （含 videoId / title / category / uploader），便于管理员查看每条轮播图关联的视频。
     * status 为 null 时查全部状态。
     */
    IPage<BannerVO> selectBannerVOPage(IPage<BannerVO> page, @Param("status")Integer status);
}

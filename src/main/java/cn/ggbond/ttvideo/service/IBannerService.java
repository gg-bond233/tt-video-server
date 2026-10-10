package cn.ggbond.ttvideo.service;

import cn.ggbond.ttvideo.mapper.BannerMapper;
import cn.ggbond.ttvideo.model.dto.BannerDTO;
import cn.ggbond.ttvideo.model.entity.Banner;
import cn.ggbond.ttvideo.model.vo.BannerVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

public interface IBannerService extends IService<Banner> {
    //获取轮播图列表
    Result<List<BannerVO>> getBannerList();

    //获取轮播图列表（管理端）
    Result<PageResult<BannerVO>> getAllBanners(BannerDTO bannerDTO);

    //添加轮播图
    Result addBanner(String bannerUrl, Long videoId);

    //更新轮播图
    Result updateBanner(Long bannerId,String bannerUrl, Long videoId);

    //修改轮播图状态
    Result updateBannerStatus(Long bannerId, Integer bannerStatus);

    //删除轮播图
    Result deleteBanner(Long bannerId);

    //批量删除轮播图
    Result deleteBanners(List<Long> bannerIds);
}

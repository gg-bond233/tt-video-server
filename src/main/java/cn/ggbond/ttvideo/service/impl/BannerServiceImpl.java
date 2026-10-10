package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.enumeration.BannerStatusEnum;
import cn.ggbond.ttvideo.mapper.BannerMapper;
import cn.ggbond.ttvideo.model.dto.BannerDTO;
import cn.ggbond.ttvideo.model.entity.Banner;
import cn.ggbond.ttvideo.model.vo.BannerVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IBannerService;
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

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "bannerCache")
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {

    private final BannerMapper bannerMapper;

    private final MinioService minioService;


    /**
     * 获取轮播图列表（用户端）
     * LEFT JOIN tb_video 填充 videoId/title/category/uploader，
     * 前端首页 hero 的"立即播放"按钮据此跳转到关联视频。
     *
     * @return 轮播图列表
     */
    @Override
    @Cacheable(key = "'bannerList'")
    public Result<List<BannerVO>> getBannerList() {
        return Result.success(bannerMapper.getBannerVOList());
    }

    /**
     * 获取轮播图列表（管理端，带关联视频信息）
     *
     * @return 轮播图列表
     */
    @Override
    @Cacheable(
            cacheNames = "bannerCache",
            key = "'bannerList:' + #bannerDTO.pageNum + ':' + #bannerDTO.pageSize + ':' + " +
                    "(#bannerDTO.bannerStatus == null ? 'all' : #bannerDTO.bannerStatus.id)"
    )
    public Result<PageResult<BannerVO>> getAllBanners(BannerDTO bannerDTO) {
        //构建分页条件
        Page<BannerVO> page=new Page<>(bannerDTO.getPageNum(),bannerDTO.getPageSize());
        Integer status = bannerDTO.getBannerStatus() != null ? bannerDTO.getBannerStatus().getId() : null;

        //执行分页条件
        IPage<BannerVO> bannerPage = bannerMapper.selectBannerVOPage(page, status);
        if(bannerPage.getRecords().isEmpty()){
            return Result.success(MessageConstant.DATA_NOT_FOUND,new PageResult<>(0L,null));
        }
        return Result.success(new PageResult<>(bannerPage.getTotal(),bannerPage.getRecords()));
    }

    /**
     * 添加轮播图
     *
     * @param bannerUrl 轮播图URL
     * @param videoId   关联视频ID（可为空，表示纯展示型轮播图）
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "bannerCache", allEntries = true)
    public Result addBanner(String bannerUrl, Long videoId) {
        Banner banner=new Banner();
        banner.setBannerUrl(bannerUrl).setVideoId(videoId).setBannerStatus(BannerStatusEnum.ENABLE);

        if (bannerMapper.insert(banner)==0){
            return Result.error(MessageConstant.ADD+MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD+MessageConstant.SUCCESS);
    }

    /**
     * 更新轮播图（替换图片并更新关联视频）
     *
     * @param bannerId  轮播图ID
     * @param bannerUrl 新轮播图URL
     * @param videoId   关联视频ID（可为空，表示取消关联）
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "bannerCache", allEntries = true)
    public Result updateBanner(Long bannerId,String bannerUrl, Long videoId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner==null){
            return Result.error(MessageConstant.DATA_NOT_FOUND);
        }
        String oldBannerUrl = banner.getBannerUrl();
        if (oldBannerUrl!=null && !oldBannerUrl.isEmpty()){
            minioService.deleteFile(oldBannerUrl);
        }
        
        banner.setBannerUrl(bannerUrl);
        banner.setVideoId(videoId);
        if (bannerMapper.updateById(banner) == 0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }


    /**
     * 更新轮播图状态
     *
     * @param bannerId     轮播图ID
     * @param bannerStatus 轮播图状态
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "bannerCache", allEntries = true)
    public Result updateBannerStatus(Long bannerId, Integer bannerStatus) {
        BannerStatusEnum statusEnum;
        if (bannerStatus==0){
            statusEnum = BannerStatusEnum.ENABLE;
        }else if (bannerStatus==1){
            statusEnum = BannerStatusEnum.DISABLE;
        }else{
            return Result.error(MessageConstant.BANNER_STATUS_INVALID);
        }

        Banner banner=new Banner();
        banner.setBannerId(bannerId).setBannerStatus(statusEnum);
        if (bannerMapper.updateById(banner)==0){
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }

    /**
     * 删除轮播图
     *
     * @param bannerId 轮播图ID
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "bannerCache", allEntries = true)
    public Result deleteBanner(Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner==null){
            return Result.error(MessageConstant.DATA_NOT_FOUND);
        }

        String bannerUrl = banner.getBannerUrl();
        if (bannerUrl!=null && !bannerUrl.isEmpty()){
            minioService.deleteFile(bannerUrl);
        }

        if (bannerMapper.deleteById(bannerId)==0){
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }

    /**
     * 批量删除轮播图
     *
     * @param bannerIds 轮播图ID列表
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "bannerCache", allEntries = true)
    public Result deleteBanners(List<Long> bannerIds) {
        List<Banner> banners = bannerMapper.selectByIds(bannerIds);
        for (Banner banner : banners) {
            String url = banner.getBannerUrl();
            if (url != null && !url.isEmpty()) {
                minioService.deleteFile(url);
            }
        }
        if (bannerMapper.deleteByIds(bannerIds)==0){
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }


}

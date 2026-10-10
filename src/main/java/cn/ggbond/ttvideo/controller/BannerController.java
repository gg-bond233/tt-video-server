package cn.ggbond.ttvideo.controller;

import cn.ggbond.ttvideo.model.dto.BannerDTO;
import cn.ggbond.ttvideo.model.vo.BannerVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.MinioService;
import cn.ggbond.ttvideo.service.impl.BannerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BannerController {
    private final BannerServiceImpl bannerService;

    private final MinioService minioService;
    /**
     * 获取轮播图列表（用户端）
     *
     * @return 轮播图列表
     */
    @GetMapping("/banner/getBannerList")
    public Result<List<BannerVO>> getBannerList() {
        return bannerService.getBannerList();
    }

    /**
     * 获取轮播图列表（管理端，带关联视频信息）
     *
     * @return 轮播图列表
     */
    @PostMapping("/admin/getAllBanners")
    public Result<PageResult<BannerVO>>getAllBanners(@RequestBody BannerDTO bannerDTO){
        return bannerService.getAllBanners(bannerDTO);
    }


    /**
     * 添加轮播图
     *
     * @param banner  轮播图图片文件
     * @param videoId 关联视频ID（可选，不传则为纯展示型轮播图）
     * @return 结果
     */
    @PostMapping("/admin/addBanner")
    public Result addBanner(@RequestParam("banner") MultipartFile banner,
                            @RequestParam(value = "videoId", required = false) Long videoId){
        String bannerUrl = minioService.uploadFile(banner,"banners");
        return bannerService.addBanner(bannerUrl,videoId);
    }

    /**
     * 更新轮播图（替换图片并更新关联视频）
     *
     * @param bannerId 轮播图ID
     * @param banner   新轮播图图片文件
     * @param videoId  关联视频ID（可选，不传表示取消关联）
     * @return 结果
     */
    @PatchMapping("/admin/updateBanner/{id}")
    public Result updateBanner(@PathVariable("id")Long bannerId,
                               @RequestParam("banner") MultipartFile banner,
                               @RequestParam(value = "videoId",required = false)Long videoId){
        String bannerUrl = minioService.uploadFile(banner, "banners");
        return bannerService.updateBanner(bannerId,bannerUrl,videoId);
    }


    /**
     * 更新轮播图状态
     *
     * @param bannerStatus 轮播图状态
     * @return 结果
     */
    @PatchMapping("/admin/updateBannerStatus/{id}")
    public Result updateBannerStatus(@PathVariable("id")Long bannerId,
                                     @RequestParam("status")Integer bannerStatus){
        return bannerService.updateBannerStatus(bannerId,bannerStatus);
    }


    /**
     * 删除轮播图
     *
     * @param bannerId 轮播图id
     * @return 结果
     */
    @DeleteMapping("/admin/deleteBanner/{id}")
    public Result deleteBanner(@PathVariable("id")Long bannerId){
        return bannerService.deleteBanner(bannerId);
    }


    /**
     * 批量删除轮播图
     *
     * @param bannerIds 轮播图id列表
     * @return 结果
     */
    @DeleteMapping("/admin/deleteBanners")
    public Result deleteBanners(@RequestBody List<Long> bannerIds){
        return bannerService.deleteBanners(bannerIds);
    }

}

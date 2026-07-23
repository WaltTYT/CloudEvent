package cn.edu.scau.article.feign;

import cn.edu.scau.pojo.Category;
import cn.edu.scau.pojo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 分类服务 Feign 远程调用客户端
 * 用于向 cloud-event-category 服务发起 HTTP 请求，获取分类数据
 */
@FeignClient("cloud-event-category")
public interface CategoryFeignClient {

    /**
     * 根据分类ID获取分类详情
     * @param id 分类ID
     * @return 封装了分类信息的 Result 对象
     */
    @GetMapping("/category/detail")
    Result<Category> getCategoryById(@RequestParam("id") Integer id);
}

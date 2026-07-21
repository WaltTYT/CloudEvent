package cn.edu.scau.article.feign;

import cn.edu.scau.pojo.Category;
import cn.edu.scau.pojo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cloud-event-category")
public interface CategoryFeignClient {

    @GetMapping("/category/detail")
    Result<Category> getCategoryById(@RequestParam("id") Integer id);
}

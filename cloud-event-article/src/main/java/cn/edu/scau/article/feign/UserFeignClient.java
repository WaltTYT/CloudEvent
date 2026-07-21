package cn.edu.scau.article.feign;

import cn.edu.scau.pojo.Result;
import cn.edu.scau.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cloud-event-user")
public interface UserFeignClient {

    @GetMapping("/user/findById")
    Result<User> getUserById(@RequestParam("id") Integer id);
}

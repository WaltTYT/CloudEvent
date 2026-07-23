package cn.edu.scau.article.feign;

import cn.edu.scau.pojo.Result;
import cn.edu.scau.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务 Feign 远程调用客户端
 * 用于向 cloud-event-user 服务发起 HTTP 请求，获取用户数据
 */
@FeignClient("cloud-event-user")
public interface UserFeignClient {

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 封装了用户信息的 Result 对象
     */
    @GetMapping("/user/findById")
    Result<User> getUserById(@RequestParam("id") Integer id);
}

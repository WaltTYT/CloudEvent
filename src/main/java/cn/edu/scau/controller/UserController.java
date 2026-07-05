package cn.edu.scau.controller;

import cn.edu.scau.pojo.Result;
import cn.edu.scau.pojo.User;
import cn.edu.scau.service.UserService;
import cn.edu.scau.utils.JwtUtil;
import cn.edu.scau.utils.Md5Util;
import cn.edu.scau.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Validated//验证参数是否合法
@RestController // 标注当前类为 RESTful 风格的控制层组件，响应 JSON 数
@RequestMapping("/user")// 设置基础请求路径，所有接口都以 /user 开头
public class UserController {
    @Autowired// 自动注入 Spring 容器中的 UserService Bean
    private UserService userService;
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$")String password) {
            //查询用户
            User u = userService.findByUsername(username);
            if (u == null) {//添加用户
                userService.register(username, password);
                return Result.success();
            }else{//用户已存在
                return Result.error("用户已存在");
            }
    }
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$")String password){

        User u = userService.findByUsername(username);
        //查询用户
        if(u == null){
            return Result.error("用户不存在");
        }
        //验证密码
        if(Md5Util.getMD5String( password).equals(u.getPassword())){
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",u.getId());
            claims.put("username",u.getUsername());
            String token = JwtUtil.getToken(claims);
            //将token存入redis里面
//            stringRedisTemplate.opsForValue().set(token,token, 1 ,TimeUnit.HOURS);
            return Result.success( token);
        }
        return Result.error("密码错误");
    }
    @GetMapping("/userInfo")
    public Result<User> userinfo(/*@RequestHeader(name = "Authorization") String token*/){
        /*Map<String,Object> map = JwtUtil.parseeToken(token);
        String username = (String) map.get("username");*/
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUsername(username);
        return Result.success(user);
    }
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String  token ){
        String old_pwd = params.get("old_pwd");
        String new_pwd = params.get("new_pwd");
        String re_pwd = params.get("re_pwd");
        //判断是否为空
        if(!StringUtils.hasLength(old_pwd)|| !StringUtils.hasLength(new_pwd)|| !StringUtils.hasLength(re_pwd))
            return Result.error("参数不能为空");
        //验证旧密码
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findByUsername(username);
        if(!Md5Util.getMD5String(old_pwd).equals(loginUser.getPassword()))
            return Result.error("原密码错误");
        //验证新密码
        if(!new_pwd.equals(re_pwd))
            return Result.error("两次密码不一致");
        userService.updatePwd(new_pwd);
        //删除 Redis里面的token
//        ValueOperations<String, String> Operations = stringRedisTemplate.opsForValue();
//        Operations.getOperations().delete(token);
        return Result.success();
    }
}

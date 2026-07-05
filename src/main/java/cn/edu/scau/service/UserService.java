package cn.edu.scau.service;

import cn.edu.scau.pojo.User;

public interface UserService {
    // 根据用户名查询用户
    User findByUsername(String username);
    // 注册用户
    void register(String username, String password);
    //更新用户
    void update(User user);
    //更新用户头像
    void updateAvatar(String avatarUrl);
    //更新密码
    void updatePwd(String newPwd);
}

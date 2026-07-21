package cn.edu.scau.user.service;

import cn.edu.scau.pojo.User;

public interface UserService {
    User findByUsername(String username);

    User findById(Integer id);

    void register(String username, String password);

    void update(User user);

    void updateAvatar(String avatarUrl);

    void updatePwd(String newPwd);
}

package cn.edu.scau.service.impl;
import cn.edu.scau.mapper.UserMapper;
import cn.edu.scau.pojo.User;
import cn.edu.scau.service.UserService;
import cn.edu.scau.utils.Md5Util;
import cn.edu.scau.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service//标识服务层 Bean，由 Spring 容器管理
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        User u = userMapper.findByUsername(username);
        return u;
    }

    @Override
    public void register(String username, String password) {
        //加密
        String md5password = Md5Util.getMD5String(password);
        //添加
        userMapper.add(username, md5password);
    }
    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer)map.get("id");
        userMapper.updateAvatar(avatarUrl, id);
    }

    @Override
    public void updatePwd(String new_pwd) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer)map.get("id");
        userMapper.updatePwd(Md5Util.getMD5String(new_pwd), id);
    }
}

package cn.edu.scau.user.mapper;

import cn.edu.scau.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from user where username=#{username}")
    User findByUsername(String username);

    @Select("select * from user where id=#{id}")
    User findById(Integer id);

    @Insert("insert into user(username,nickname,email,password,create_time,update_time) " +
            "values (#{username},'', '',#{md5password},now(),now())")
    void add(String username, String md5password);

    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);

    @Update("update user set user_pic= #{avatarUrl},update_time=now() where id= #{id}")
    void updateAvatar(String avatarUrl, Integer id);

    @Update("update user set password= #{md5String},update_time=now() where id= #{id}")
    void updatePwd(String md5String, Integer id);
}

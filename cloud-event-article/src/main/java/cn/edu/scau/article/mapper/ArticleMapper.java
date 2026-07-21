package cn.edu.scau.article.mapper;

import cn.edu.scau.pojo.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Insert("insert into article(title,content,cover_img,state,category_id,create_user,create_time,update_time)" +
            "values (#{title},#{content},#{coverImg},#{state},#{categoryId},#{createUser},#{createTime},#{updateTime})")
    void add(Article article);

    List<Article> list(Integer userId, Integer categoryId, String state);

    List<Article> listPublished(Integer categoryId);

    @Select("select * from article where id=#{id}")
    Article findById(Integer id);

    @Update("update article set state=#{state},update_time=now() where id=#{id}")
    void updateState(Integer id, String state);

    @Delete("delete from article where id=#{id}")
    void deleteById(Integer id);
}

package cn.edu.scau.mapper;

import cn.edu.scau.pojo.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper {
    //新增文章
    @Insert("insert into article(title,content,cover_img,state,category_id,create_user,create_time,update_time)" +
            "values (#{title},#{content},#{coverImg},#{state},#{categoryId},#{createUser},#{createTime},#{updateTime})" )
    void add(Article article);
    //条件分类，文章列表查询
    List<Article> list(Integer userId, Integer categoryId, String state);

    //查询所有已发布文章（不按用户过滤）
    List<Article> listPublished(Integer categoryId);

    @Select("select a.*, c.category_name as categoryName, u.nickname as authorName from article a left join category c on a.category_id = c.id left join user u on a.create_user = u.id where a.id=#{id}")
    Article findById(Integer id);

    @Update("update article set state=#{state},update_time=now() where id=#{id}")
    void updateState(Integer id, String state);

    @Delete("delete from article where id=#{id}")
    void deleteById(Integer id);
}

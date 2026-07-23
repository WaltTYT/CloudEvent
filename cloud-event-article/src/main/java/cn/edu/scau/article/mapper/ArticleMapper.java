package cn.edu.scau.article.mapper;

import cn.edu.scau.pojo.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 文章数据访问层
 * 提供文章表的增删改查操作
 */
@Mapper
public interface ArticleMapper {
    /**
     * 新增文章
     * @param article 文章实体
     */
    @Insert("insert into article(title,content,cover_img,state,category_id,create_user,create_time,update_time)" +
            "values (#{title},#{content},#{coverImg},#{state},#{categoryId},#{createUser},#{createTime},#{updateTime})")
    void add(Article article);

    /**
     * 分页查询当前用户的文章列表（管理端）
     * @param userId 用户ID
     * @param categoryId 分类ID（可选）
     * @param state 文章状态（可选）
     * @return 文章列表
     */
    List<Article> list(Integer userId, Integer categoryId, String state);

    /**
     * 分页查询已发布文章列表（前台）
     * @param categoryId 分类ID（可选）
     * @return 文章列表
     */
    List<Article> listPublished(Integer categoryId);

    /**
     * 根据ID查询文章
     * @param id 文章ID
     * @return 文章实体
     */
    @Select("select * from article where id=#{id}")
    Article findById(Integer id);

    /**
     * 更新文章状态
     * @param id 文章ID
     * @param state 目标状态
     */
    @Update("update article set state=#{state},update_time=now() where id=#{id}")
    void updateState(Integer id, String state);

    /**
     * 根据ID删除文章
     * @param id 文章ID
     */
    @Delete("delete from article where id=#{id}")
    void deleteById(Integer id);
}

package cn.edu.scau.article.service;

import cn.edu.scau.pojo.Article;
import cn.edu.scau.pojo.PageBean;

/**
 * 文章业务服务接口
 * 定义文章相关的增删改查及状态管理功能
 */
public interface ArticleService {

    /**
     * 新增文章
     * @param article 文章实体
     */
    void add(Article article);

    /**
     * 分页查询文章列表（管理端）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param categoryId 分类ID（可选）
     * @param state 文章状态（可选）
     * @return 分页结果
     */
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);

    /**
     * 分页查询已发布文章列表（前台）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param categoryId 分类ID（可选）
     * @return 分页结果
     */
    PageBean<Article> listPublished(Integer pageNum, Integer pageSize, Integer categoryId);

    /**
     * 根据ID查询文章详情
     * @param id 文章ID
     * @return 文章实体
     */
    Article findById(Integer id);

    /**
     * 更新文章状态
     * @param id 文章ID
     * @param state 目标状态
     */
    void updateState(Integer id, String state);

    /**
     * 根据ID删除文章
     * @param id 文章ID
     */
    void deleteById(Integer id);
}

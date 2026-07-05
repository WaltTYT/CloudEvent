package cn.edu.scau.service;

import cn.edu.scau.pojo.Article;
import cn.edu.scau.pojo.PageBean;

public interface ArticleService {
    //添加文章
    void add(Article article);
    //条件分类，文章列表查询
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);

    PageBean<Article> listPublished(Integer pageNum, Integer pageSize, Integer categoryId);

    Article findById(Integer id);

    void updateState(Integer id, String state);

    void deleteById(Integer id);
}

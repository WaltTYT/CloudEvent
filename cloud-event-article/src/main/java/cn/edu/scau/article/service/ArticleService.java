package cn.edu.scau.article.service;

import cn.edu.scau.pojo.Article;
import cn.edu.scau.pojo.PageBean;

public interface ArticleService {
    void add(Article article);

    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);

    PageBean<Article> listPublished(Integer pageNum, Integer pageSize, Integer categoryId);

    Article findById(Integer id);

    void updateState(Integer id, String state);

    void deleteById(Integer id);
}

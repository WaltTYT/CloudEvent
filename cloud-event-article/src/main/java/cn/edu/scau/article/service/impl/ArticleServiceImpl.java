package cn.edu.scau.article.service.impl;

import cn.edu.scau.article.feign.CategoryFeignClient;
import cn.edu.scau.article.feign.UserFeignClient;
import cn.edu.scau.article.mapper.ArticleMapper;
import cn.edu.scau.article.service.ArticleMessageService;
import cn.edu.scau.article.service.ArticleService;
import cn.edu.scau.pojo.Article;
import cn.edu.scau.pojo.Category;
import cn.edu.scau.pojo.PageBean;
import cn.edu.scau.pojo.Result;
import cn.edu.scau.pojo.User;
import cn.edu.scau.utils.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired(required = false)
    private CategoryFeignClient categoryFeignClient;

    @Autowired(required = false)
    private UserFeignClient userFeignClient;

    @Autowired
    private ArticleMessageService articleMessageService;

    @Override
    public void add(Article article) {
        article.setUpdateTime(LocalDateTime.now());
        article.setCreateTime(LocalDateTime.now());
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateUser(userId);
        articleMapper.add(article);
        // 异步发送文章发布消息
        articleMessageService.sendArticlePublish(article);
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        PageBean<Article> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        List<Article> as = articleMapper.list(userId, categoryId, state);
        Page<Article> p = (Page<Article>) as;
        pb.setTotal(p.getTotal());
        pb.setItems(as);
        return pb;
    }

    @Override
    public PageBean<Article> listPublished(Integer pageNum, Integer pageSize, Integer categoryId) {
        PageBean<Article> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Article> as = articleMapper.listPublished(categoryId);

        // 通过 Feign 远程调用分类服务和用户服务，填充分类名称和作者名称
        if (as != null) {
            for (Article article : as) {
                // 填充分类名称
                if (categoryFeignClient != null) {
                    try {
                        Result<Category> categoryResult = categoryFeignClient.getCategoryById(article.getCategoryId());
                        if (categoryResult != null && categoryResult.getData() != null) {
                            article.setCategoryName(categoryResult.getData().getCategoryName());
                        }
                    } catch (Exception e) {
                        // 远程调用失败时忽略，不阻塞查询
                    }
                }
                // 填充作者名称
                if (userFeignClient != null && article.getCreateUser() != null) {
                    try {
                        Result<User> userResult = userFeignClient.getUserById(article.getCreateUser());
                        if (userResult != null && userResult.getData() != null) {
                            article.setAuthorName(userResult.getData().getUsername());
                        }
                    } catch (Exception e) {
                        // 远程调用失败时忽略
                    }
                }
            }
        }

        Page<Article> p = (Page<Article>) as;
        pb.setTotal(p.getTotal());
        pb.setItems(as);
        return pb;
    }

    @Override
    public Article findById(Integer id) {
        Article article = articleMapper.findById(id);

        // 通过 Feign 远程调用分类服务，填充分类名称
        if (article != null && categoryFeignClient != null) {
            try {
                Result<Category> categoryResult = categoryFeignClient.getCategoryById(article.getCategoryId());
                if (categoryResult != null && categoryResult.getData() != null) {
                    article.setCategoryName(categoryResult.getData().getCategoryName());
                }
            } catch (Exception e) {
                // 远程调用失败时忽略
            }
        }

        return article;
    }

    @Override
    public void updateState(Integer id, String state) {
        articleMapper.updateState(id, state);
        // 异步发送文章操作日志消息
        Article article = articleMapper.findById(id);
        articleMessageService.sendArticleLog(article, "updateState");
    }

    @Override
    public void deleteById(Integer id) {
        // 删除前先查询文章信息，用于发送日志消息
        Article article = articleMapper.findById(id);
        articleMapper.deleteById(id);
        // 异步发送文章操作日志消息
        articleMessageService.sendArticleLog(article, "delete");
    }
}

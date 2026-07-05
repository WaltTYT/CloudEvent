package cn.edu.scau.service.impl;

import cn.edu.scau.mapper.ArticleMapper;
import cn.edu.scau.pojo.Article;
import cn.edu.scau.pojo.PageBean;
import cn.edu.scau.service.ArticleService;
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
    @Override
    public void add(Article article) {
        //补充属性值
        article.setUpdateTime(LocalDateTime.now());
        article.setCreateTime(LocalDateTime.now());
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
        article.setCreateUser(userId);
        articleMapper.add(article);
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        //创建对象
        PageBean<Article> pb = new PageBean<>() ;
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        //调用Mapper
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
        List< Article> as = articleMapper.list(userId,categoryId,state);
        //封装数据
        Page< Article> p = (Page< Article>) as;
        //封装总记录数
        pb.setTotal(p.getTotal());
        pb.setItems( as);

        return pb;
    }

    @Override
    public PageBean<Article> listPublished(Integer pageNum, Integer pageSize, Integer categoryId) {
        PageBean<Article> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Article> as = articleMapper.listPublished(categoryId);
        Page<Article> p = (Page<Article>) as;
        pb.setTotal(p.getTotal());
        pb.setItems(as);
        return pb;
    }

    @Override
    public Article findById(Integer id) {
        return articleMapper.findById(id);
    }

    @Override
    public void updateState(Integer id, String state) {
        articleMapper.updateState(id, state);
    }

    @Override
    public void deleteById(Integer id) {
        articleMapper.deleteById(id);
    }
}

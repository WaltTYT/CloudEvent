package cn.edu.scau.article.controller;

import cn.edu.scau.article.service.ArticleService;
import cn.edu.scau.pojo.Article;
import cn.edu.scau.pojo.PageBean;
import cn.edu.scau.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    public Result add(@RequestBody @Validated Article article) {
        articleService.add(article);
        return Result.success();
    }

    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state
    ) {
        PageBean<Article> pb = articleService.list(pageNum, pageSize, categoryId, state);
        return Result.success(pb);
    }

    @GetMapping("/published")
    public Result<PageBean<Article>> published(
            Integer pageNum, Integer pageSize,
            @RequestParam(required = false) Integer categoryId) {
        PageBean<Article> pb = articleService.listPublished(pageNum, pageSize, categoryId);
        return Result.success(pb);
    }

    @GetMapping("/detail")
    public Result<Article> detail(Integer id) {
        Article article = articleService.findById(id);
        return Result.success(article);
    }

    @PatchMapping("/state")
    public Result updateState(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String state = (String) params.get("state");
        articleService.updateState(id, state);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(Integer id) {
        articleService.deleteById(id);
        return Result.success();
    }
}

package cn.edu.scau.article.controller;

import cn.edu.scau.article.service.ArticleService;
import cn.edu.scau.pojo.Article;
import cn.edu.scau.pojo.PageBean;
import cn.edu.scau.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 文章管理控制器
 * 该控制器提供文章的增删改查功能，包括：
 * - 文章的创建、删除、修改
 * - 文章列表分页查询（支持分类和状态筛选）
 * - 已发布文章列表查询
 * - 文章详情查询
 * - 文章状态变更（如发布、下架等）
 * @see ArticleService
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 新增文章
     * 接收前端传入的文章数据，经过参数校验后保存到数据库。
     * @param article 文章实体对象，包含文章的标题、内容、分类等信息
     * @return 操作结果
     */
    @PostMapping
    public Result add(@RequestBody @Validated Article article) {
        articleService.add(article);
        return Result.success();
    }

    /**
     * 分页查询文章列表
     * 支持按分类ID和文章状态进行筛选，返回分页后的文章数据。
     * @param pageNum    当前页码
     * @param pageSize   每页显示数量
     * @param categoryId 分类ID（可选，用于按分类筛选）
     * @param state      文章状态（可选，用于按状态筛选，如 draft/published）
     * @return 分页文章数据
     */
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

    /**
     * 查询已发布的文章列表
     * 仅返回状态为"已发布"的文章，支持按分类筛选。
     * 该接口主要面向前台用户展示。
     * @param pageNum    当前页码
     * @param pageSize   每页显示数量
     * @param categoryId 分类ID（可选，用于按分类筛选）
     * @return 分页文章数据
     */
    @GetMapping("/published")
    public Result<PageBean<Article>> published(
            Integer pageNum, Integer pageSize,
            @RequestParam(required = false) Integer categoryId) {
        PageBean<Article> pb = articleService.listPublished(pageNum, pageSize, categoryId);
        return Result.success(pb);
    }

    /**
     * 查询文章详情
     * 根据文章ID查询单篇文章的详细信息。
     * @param id 文章ID
     * @return 文章详细信息
     */
    @GetMapping("/detail")
    public Result<Article> detail(Integer id) {
        Article article = articleService.findById(id);
        return Result.success(article);
    }

    /**
     * 更新文章状态
     * 通过请求体传递文章ID和目标状态，用于发布、下架文章等操作。
     * @param params 请求参数，包含：
     *               - id: 文章ID
     *               - state: 目标状态（如 published, unpublished）
     * @return 操作结果
     */
    @PatchMapping("/state")
    public Result updateState(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String state = (String) params.get("state");
        articleService.updateState(id, state);
        return Result.success();
    }

    /**
     * 删除文章
     * 根据文章ID删除指定文章。
     * @param id 文章ID
     * @return 操作结果
     */
    @DeleteMapping
    public Result delete(Integer id) {
        articleService.deleteById(id);
        return Result.success();
    }
}

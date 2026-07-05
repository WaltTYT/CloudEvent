package cn.edu.scau.service;

import cn.edu.scau.pojo.Category;

import java.util.List;

public interface CategoryService {
    //添加分类
    void add(Category category);
    //查询所有列表
    List<Category> list();
    //根据ID查询
    Category findById(Integer id);
    //修改更新
    void update(Category category);
    //删除
    void deleteById(Integer id);
}

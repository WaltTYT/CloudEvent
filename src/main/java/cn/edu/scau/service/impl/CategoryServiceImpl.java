package cn.edu.scau.service.impl;

import cn.edu.scau.mapper.CategoryMapper;
import cn.edu.scau.pojo.Category;
import cn.edu.scau.service.CategoryService;
import cn.edu.scau.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public void add(Category category) {
        //补充属性值
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
        category.setCreateUser(userId);
        categoryMapper.add( category);
    }

    @Override
    public List<Category> list() {
        Map<String,Object>map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
        return categoryMapper.list( userId);
    }

    @Override
    public Category findById(Integer id) {
        return categoryMapper.findById(id);
    }

    @Override
    public void update(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.update(category);
    }

    // CategoryServiceImpl.java 增强delete逻辑
    @Override
    public void deleteById(Integer id) {
        categoryMapper.deleteById(id);
    }

}

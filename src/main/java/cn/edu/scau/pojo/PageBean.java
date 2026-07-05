package cn.edu.scau.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//分页返回结果对象
@Data
@NoArgsConstructor//创建无参构造方法
@AllArgsConstructor//创建全参构造方法
public class PageBean <T>{
    private Long total;//总条数
    private List<T> items;//当前页数据集合
}

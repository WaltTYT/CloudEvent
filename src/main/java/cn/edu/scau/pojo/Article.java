package cn.edu.scau.pojo;

import java.time.LocalDateTime;

import cn.edu.scau.anno.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class Article {
    private Integer id;//主键ID
    @NotEmpty
    @Pattern(regexp = "^\\S{1,10}$")
    private String title;//文章标题
    @NotEmpty
    private String content;//文章内容
    @NotEmpty
    @URL
    private String coverImg;//封面图像
    @State
    private String state;//发布状态 已发布|草稿
    @NotNull
    private Integer categoryId;//文章分类id
    private String categoryName;//文章分类名称（联表查询）
    private Integer createUser;//创建人ID
    private String authorName;//作者名称（联表查询）
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
}

package cn.edu.scau.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import cn.edu.scau.anno.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    @NotEmpty
    @Pattern(regexp = "^\\S{1,10}$")
    private String title;
    @NotEmpty
    private String content;
    @Pattern(regexp = "^$|https?://[\\w\\-.]+(:\\d+)?(/[\\w\\-.]*)*/?.*$")
    private String coverImg;
    @State
    private String state;
    @NotNull
    private Integer categoryId;
    private String categoryName;
    private Integer createUser;
    private String authorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

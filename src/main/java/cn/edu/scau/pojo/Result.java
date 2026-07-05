package cn.edu.scau.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor//无参构造方法
@AllArgsConstructor //全参构造方法
@Data//作用是在编译阶段自动生成常用的代码方法，避免手动编写样板代码。
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    //快速返回带数据的成功结果
    public static <E> Result<E> success(E data) {
        return new Result<>(0, "操作成功", data);
    }
    //快速返回成功结果
    public static  Result success() {
        return new Result(0, "操作成功", null);
    }
    //快速返回失败结果
    public static <E> Result<E> error(String message) {
        return new Result<>(1, message, null);
    }
}

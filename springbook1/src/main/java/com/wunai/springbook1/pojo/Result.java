package com.wunai.springbook1.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//统一响应结果
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {
    private Integer code; // 业务状态码 0-成功 1-失败
    private String message; // 提示信息
    private T data; // 响应数据

    // 快速返回操作成功响应结果(带响应数据)
    public static <E> Result<E> success(E data) {

        return new Result<>(0, "操作成功", data);
    }

    // 快速返回操作成功响应结果
    public static Result success() {
        return new Result(0, "操作成功", null);
    }

    // 快速返回操作失败响应结果
    public static Result error(String message) {

        return new Result(1, message, null);
    }

    // 为了兼容HTTP状态码的习惯，也可以添加这些方法
    public static Result ok(String data) {

        return new Result(200, "success", data);
    }

    public static Result fail(String data)
    {

        return new Result(500, "error", data);
    }

    public  static Result Return(String data, Integer code,String message)
    {
        return  new Result(code,message,data);

    }
    public static Result<String> ok(User loginUser) {
        return new Result(200, "success", loginUser);
    }

    public static Result success(String fileUrl) {
        return new Result(200, "success", fileUrl);
    }
}

//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
////统一响应结果
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//public class Result<T> {
//    private Integer code;//业务状态码  0-成功  1-失败
//    private String message;//提示信息
//    private T data;//响应数据
//
//    //快速返回操作成功响应结果(带响应数据)
//    public static <E> Result<E> success(E data) {
//        return new Result<>(0, "操作成功", data);
//    }
//
//    //快速返回操作成功响应结果
//    public static Result success() {
//        return new Result(0, "操作成功", null);
//    }
//
//    public static Result error(String message) {
//        return new Result(1, message, null);
//    }
//}

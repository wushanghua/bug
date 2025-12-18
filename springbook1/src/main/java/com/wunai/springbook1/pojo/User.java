package com.wunai.springbook1.pojo;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wunai.springbook1.constant.RoleConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jdk.jfr.DataAmount;
import jdk.jfr.MetadataDefinition;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
//lombok 编译阶段自动生成setter getter tostring
// pom 文件中引入依赖 在实体上添加注释
@Data
public class User {
//    @NonNull
    private Integer id;//主键ID
    private String username;//用户名
//    @JsonIgnore //用来password，最后json字符串返回时不显示password
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;//密码

//    @NotEmpty
//    @Pattern(regexp = "^\\S{1,10}$")
    private String nickname;//昵称
//    @NotEmpty
//    @Email
    private String email;//邮箱
    private String userPic;//用户头像地址
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间

    private String role = RoleConstant.USER; // 默认普通用户
    public static final String ROLE_USER = "USER";

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;
}

package com.wunai.springbook1.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;

    //@NotNull(message = "漏洞ID不能为空")
    private Integer bugId; // 关联的漏洞ID

   // @NotNull(message = "用户ID不能为空")
    private Integer userId; // 评论用户ID

   // @NotEmpty(message = "评论内容不能为空")
    private String content; // 评论内容

    private LocalDateTime createTime; // 创建时间
    private Integer isAdmin; // 是否管理员评论(0-否,1-是)
}
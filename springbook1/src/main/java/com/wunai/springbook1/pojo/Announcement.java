package com.wunai.springbook1.pojo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Announcement {
    private Integer id;

   // @NotEmpty(message = "公告标题不能为空")
    private String title; // 公告标题

    //@NotEmpty(message = "公告内容不能为空")
    private String content; // 公告内容

    private Integer createUser; // 创建人ID
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private Integer status; // 状态(0-草稿,1-发布)
}
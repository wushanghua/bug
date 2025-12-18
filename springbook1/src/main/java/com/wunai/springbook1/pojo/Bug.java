package com.wunai.springbook1.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Bug {
    private Integer id;
    //@NotEmpty //不能为空
    private String title; // bug标题
    //@NotEmpty//不能为空
    //@PositiveOrZero//不能为非负数
    private Integer severity; // 严重程度
    //@NotNull
    //@PositiveOrZero
    private Integer priority; // 处理优先级
    //@NotNull
    private Integer module; // 所属模块
    private Integer user_id; // 报告人ID
    private Integer status; // 状态（新增：0-待审核,1-已受理,2-处理中,3-已修复,4-已拒绝,5-已关闭）
    @JsonProperty("create_time")
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private String remark; // 额外备注
    private String document; // 存储的文件
    private Integer handlerId; // 处理人ID
    private String handlerName; // 处理人昵称
    private String submitterName; // 提交人昵称
    private String feedback; // 处理反馈
    private Integer pageSize;
    private Integer pageNum;
    private Integer count; // 数量

}
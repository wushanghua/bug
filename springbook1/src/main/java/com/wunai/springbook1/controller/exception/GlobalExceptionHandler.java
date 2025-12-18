package com.wunai.springbook1.controller.exception;

import com.wunai.springbook1.pojo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public Result handleException(Exception e) {
            e.printStackTrace();
            return Result.error(StringUtils.hasLength(e.getMessage())? e.getMessage():"操作失败");

        }

    // 处理文件大小超限异常（视频/图片通用）
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public Result handleFileTooLarge(MaxUploadSizeExceededException e) {
            return Result.error("文件过大！视频最大支持100MB，图片最大支持10MB");
    }

    // 处理文件类型/空文件等业务异常
        @ExceptionHandler(RuntimeException.class)
        public Result handleBusinessException(RuntimeException e) {
            return Result.error(e.getMessage());
    }



}



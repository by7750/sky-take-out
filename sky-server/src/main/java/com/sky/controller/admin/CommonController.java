package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/24 - 0:10
 * @className CommonController
 * @since 1.0
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
            String originalFilename = file.getOriginalFilename();
            // 截取扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newName = UUID.randomUUID() + extension;

            String path = aliOssUtil.upload(file.getBytes(), newName);
            return Result.success(path);
        } catch (IOException e) {
            log.error("文件上传失败，{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}

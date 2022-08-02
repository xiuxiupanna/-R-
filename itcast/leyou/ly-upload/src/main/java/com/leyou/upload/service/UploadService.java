package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UploadService {
    private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg", "image/png", "image/bmp");

    @Autowired
    private FastFileStorageClient storageClient;

    public String uploadImage(MultipartFile file) {

        try {
            //校验图片
            //校验后缀类型
            String contentType = file.getContentType();
            if (!ALLOW_TYPES.contains(contentType)) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);

            }
            //校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            //获取文件名称
            String filename = file.getOriginalFilename();
//            //文件存储路径
//            File destDir = new File("D:\\software\\nginx\\nginx-1.18.0\\nginx-1.18.0\\html", filename);
//            file.transferTo(destDir);
            //获取文件后缀名
            String extension = StringUtils.substringAfterLast(filename, ".");
            //文件上传到FastDFS
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            //返回图片地址
            String url = "http://image.leyou.com/" + storePath.getFullPath();
            return url;

        } catch (IOException e) {
            log.error("【上传微服务】图片上传失败", e);
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);

        }


    }
}

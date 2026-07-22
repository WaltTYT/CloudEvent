package cn.edu.scau.file.controller;

import cn.edu.scau.file.service.FileMessageService;
import cn.edu.scau.pojo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUploadController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private FileMessageService fileMessageService;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        file.transferTo(new File("D:\\study\\JavaStudy\\SpringBootStudy\\CloudEvent\\Repo\\" + filename));
        // 返回到 Nginx 静态文件服务的 URL（通过 80 端口访问）
        String url = request.getScheme() + "://" + request.getServerName() + ":80/repo/" + filename;
        // 异步发送文件上传消息
        fileMessageService.sendFileUpload(filename, url);
        return Result.success(url);
    }
}

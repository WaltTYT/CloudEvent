package cn.edu.scau.controller;

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

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        file.transferTo(new File("D:\\study\\JavaStudy\\SpringBootStudy\\CloudEvent\\Repo\\" + filename));
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/repo/" + filename;
        return Result.success(url);
    }
}

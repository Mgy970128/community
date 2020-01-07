package top.mgy.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import top.mgy.community.dto.FileDTO;
import top.mgy.community.provider.UCloudProvider;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

/**
 * 处理文件上传
 */
@Controller
public class FileController {

    @Autowired
    private UCloudProvider uCloudProvider;

    @ResponseBody
    @RequestMapping("/file/upload")
    public FileDTO upload(HttpServletRequest request){
        //云存储
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        FileDTO fileDTO = new FileDTO();
        try {
            String url = uCloudProvider.upload(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            fileDTO.setSuccess(1);
            fileDTO.setUrl(url);
            return fileDTO;
        }catch (Exception e){
            fileDTO.setSuccess(0);
            fileDTO.setMessage(e.getMessage());
            return fileDTO;
        }




        //本地存储
//        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
//        MultipartFile file = multipartRequest.getFile("editormd-image-file");
//        String realPath = System.getProperty("user.dir");
//        realPath+="\\target\\classes\\static\\uploadFile\\";
//        System.out.println(realPath);
//        File folder = new File(realPath);
//        if(!folder.isDirectory()){
//            //文件夹不存在,创建
//            folder.mkdirs();
//        }
//        //文件重命名
//        String oldFile = file.getOriginalFilename();
//        String newFile = UUID.randomUUID().toString()+oldFile.substring(oldFile.lastIndexOf("."),oldFile.length());
//        String url;
//        FileDTO fileDTO = null;
//        try {
//            //保存文件
//            file.transferTo(new File(folder,newFile));
//            //拼接url
//            url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/uploadFile/"+newFile;
//            fileDTO = new FileDTO();
//            fileDTO.setSuccess(1);
//            fileDTO.setUrl(url);
//            return fileDTO;
//
//        }catch (Exception e){
//            fileDTO.setSuccess(0);
//            e.printStackTrace();
//
//        }
//        return null;

    }
}

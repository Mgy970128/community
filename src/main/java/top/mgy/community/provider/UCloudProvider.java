package top.mgy.community.provider;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.BucketAuthorization;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileBucketLocalAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import cn.ucloud.ufile.http.OnProgressListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.mgy.community.exception.CustomErrorCode;
import top.mgy.community.exception.CustomizeException;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 * UCloud对象云存储
 */
@Service
public class UCloudProvider {

    @Value("${ucloud.ufile.public-key}")
    private String publicKey;

    @Value("${ucloud.ufile.private-key}")
    private String privateKey;

    @Value("${ucloud.ufile.bucket-name}")
    private String bucketName;

    @Value("${ucloud.ufile.region}")
    private String region;

    @Value("${ucloud.ufile.suffix}")
    private String suffix;

    @Value("${ucloud.ufile.expries}")
    private int expries;


    public String upload(InputStream fileStream,String mineType,String fileName){
        String newFileName;
        String[] split = fileName.split("\\.");
        if(split.length > 1){
            newFileName = UUID.randomUUID().toString()+"."+split[split.length-1];
        }else {
            return null;
        }
        try {
            //bucket授权器
            ObjectAuthorization objectAuthorization = new UfileObjectLocalAuthorization(publicKey, privateKey);
            //对象操作需要ObjectConfig来配置您的地区和域名后缀
            ObjectConfig config = new ObjectConfig(region, suffix);
            PutObjectResultBean response = UfileClient.object(objectAuthorization, config)
                    .putObject(fileStream, mineType)
                    .nameAs(newFileName)
                    .toBucket(bucketName)
                    .setOnProgressListener((bytesWritten, contentLength) -> {
                    })
                    .execute();
                    if(response != null && response.getRetCode() == 0){
                        String url = UfileClient.object(objectAuthorization, config).
                                getDownloadUrlFromPrivateBucket(newFileName, bucketName, expries).createUrl();
                        return url;
                    }else {
                        throw new CustomizeException(CustomErrorCode.FILE_UPLOAD_FAIL);
                    }
        } catch (UfileClientException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomErrorCode.FILE_UPLOAD_FAIL);
        } catch (UfileServerException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomErrorCode.FILE_UPLOAD_FAIL);
        }

    }




}

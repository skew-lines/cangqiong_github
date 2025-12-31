package com.sky.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;
import com.sky.properties.AliOSSProperties;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云 OSS 工具类
 */

public class AliOSSUtils {

//    private static final String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
//    private static final String bucketName = "202512121633";
//    // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
//    private static final String region = "cn-hangzhou";


//    @Value("${aliyun.oss.endpoint}")
//    private static String endpoint;
//
//    @Value("${aliyun.oss.bucketName}")
//    private static String bucketName;
//
//    @Value("${aliyun.oss.region}")
//    private static String region;



    private AliOSSProperties aliOSSProperties;


    public AliOSSProperties getAliOSSProperties() {
        return aliOSSProperties;
    }

    public void setAliOSSProperties(AliOSSProperties aliOSSProperties) {
        this.aliOSSProperties = aliOSSProperties;
    }



    /**
     * 实现上传图片到OSS
     */
    public String upload(MultipartFile file) throws IOException, ClientException {


        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        // 避免文件覆盖,
        String originalFilename = file.getOriginalFilename();
        String objectName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(aliOSSProperties.getEndpoint())
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(aliOSSProperties.getRegion())
                .build();

        try {
            // 获取上传的文件的输入流
            InputStream inputStream = file.getInputStream();
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliOSSProperties.getBucketName(), objectName, inputStream);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (com.aliyun.oss.ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                // 关闭ossClient
                ossClient.shutdown();
            }
        }

        //返回文件访问路径
        String url = aliOSSProperties.getEndpoint().split("//")[0] + "//" + aliOSSProperties.getBucketName() + "." + aliOSSProperties.getEndpoint().split("//")[1] + "/" + objectName;
        return url;// 把上传到oss的路径返回
    }

}

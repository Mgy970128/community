package top.mgy.community.provider;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;
import top.mgy.community.dto.AccessTokenDTO;
import top.mgy.community.dto.GithubUser;

import java.io.IOException;

@Component
public class GithubProvider {

    //获取Token
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
            Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String string = response.body().string();
                String token = string.split("&")[0].split("=")[1];
                System.out.println(string);
                return token;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
    }

    //根据返回的Token获取用户信息
    public GithubUser getUser(String accessToken){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.github.com/user?access_token=" + accessToken).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}

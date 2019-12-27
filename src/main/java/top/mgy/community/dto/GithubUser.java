package top.mgy.community.dto;

import lombok.Data;

@Data
public class GithubUser {

    private String name;
    private Long id;
    private String bio;
    private String login;
    private String avatar_url;


}

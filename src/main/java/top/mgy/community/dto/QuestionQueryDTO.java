package top.mgy.community.dto;

import lombok.Data;

/**
 * 搜索传输类
 */
@Data
public class QuestionQueryDTO {
    private String search;
    private Integer page;
    private Integer size;

}

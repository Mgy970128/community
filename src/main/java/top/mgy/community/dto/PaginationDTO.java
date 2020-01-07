package top.mgy.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页包装类
 */
@Data
public class PaginationDTO <T>{
    private List<T> data;
    private boolean showPrevious;       //是否展示前一页按钮
    private boolean showFirstPage;      //是否展示首页按钮
    private boolean showNext;           //是否展示下一页按钮
    private boolean showEndPage;        //是否展示最后一页按钮
    private Integer page;               //当前页
    private List<Integer> pages = new ArrayList<>();        //展示的页码集合
    private Integer totalPage;
    public void setPagination(Integer totalCount, Integer page, Integer size) {

        //计算总页码
        Integer totalPage = 0;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount /size + 1;
        }
        this.totalPage = totalPage;

        if(page < 1){
            page = 1;
        }

        if(page > totalPage){
            page = totalPage;
        }
        this.page = page;

        pages.add(page);

        for (int i = 1; i <=3 ; i++) {
            if(page - i > 0){
                pages.add(0,page - i);
            }

            if(page + i <= totalPage){
                pages.add(page + i);
            }
        }

        //判断上一页按钮是否展示
        if(page == 1){
            showPrevious = false;
        }else {
            showPrevious = true;
        }

        //判断下一页按钮是否展示
        if(page.equals(totalPage)){
            showNext = false;
        }else {
            showNext = true;
        }

        //判断首页按钮是否展示
        if(pages.contains(1)){
            showFirstPage = false;
        }else {
            showFirstPage = true;
        }

        //是否展示最后一页按钮
        if(pages.contains(totalPage)){
            showEndPage = false;
        }else {
            showEndPage = true;
        }


    }
}

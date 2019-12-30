package top.mgy.community.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomizeErrorController implements ErrorController {


    @Override
    public String getErrorPath() {
        return "error";
    }


    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, Model model){
        HttpStatus status = getStatus(request);

        if(status.is4xxClientError()){
            model.addAttribute("message","请求页面错了，换个姿势试试!");
        }

        if(status.is5xxServerError()){
            model.addAttribute("message","服务器冒烟了，要不稍后再试试!");
        }

        return new ModelAndView("error");
    }

    private HttpStatus getStatus(HttpServletRequest request) {

        Integer code = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if(code == null){
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return HttpStatus.valueOf(code);
    }



}

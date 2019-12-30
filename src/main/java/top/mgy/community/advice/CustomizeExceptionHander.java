package top.mgy.community.advice;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import top.mgy.community.exception.CustomizeException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class CustomizeExceptionHander {

    @ExceptionHandler(Exception.class)
    ModelAndView hander(HttpServletRequest request, Throwable e, Model model){
        //HttpStatus status = getStatus(request);
        if(e instanceof CustomizeException){
            //问题不存在异常
            model.addAttribute("message",e.getMessage());
        }else {
            model.addAttribute("message","服务器冒烟了，要不稍后再试试");
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

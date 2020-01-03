package top.mgy.community.advice;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import top.mgy.community.dto.ResultDTO;
import top.mgy.community.exception.CustomErrorCode;
import top.mgy.community.exception.CustomizeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class CustomizeExceptionHander {

    @ExceptionHandler(Exception.class)
    ModelAndView hander(HttpServletRequest request, Throwable e, Model model, HttpServletResponse response){
        //HttpStatus status = getStatus(request);
        ResultDTO resultDTO;
        String contentType = request.getContentType();
        if("application/json".equals(contentType)){
            //返回json
            if(e instanceof CustomizeException){
                resultDTO = ResultDTO.errorOf((CustomizeException) e);

            }else {
                resultDTO = ResultDTO.errorOf(CustomErrorCode.SYS_ERROR);
            }

            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.setStatus(200);
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            }catch (IOException io){

            }
            return null;
        }else {
            //错误跳转
            if(e instanceof CustomizeException){
                //问题不存在异常
                model.addAttribute("message",e.getMessage());
            }else {
                model.addAttribute("message",CustomErrorCode.SYS_ERROR.getMessage());
            }

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

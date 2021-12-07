package com.yjxxt.gymsystem;

import com.alibaba.fastjson.JSON;
import com.yjxxt.gymsystem.base.ResultInfo;
import com.yjxxt.gymsystem.exceptions.NoLoginException;
import com.yjxxt.gymsystem.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception e) {
        if (e instanceof NoLoginException){
            ModelAndView mav = new ModelAndView("redirect:/index");
            return mav;
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("code",400);
        mav.addObject("msg","系统异常，请稍后再试");
        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            if (responseBody == null){
                /*返回视图*/
                if (e instanceof ParamsException){
                   ParamsException pe =  (ParamsException) e;
                   mav.addObject("code",pe.getCode());
                   mav.addObject("msg",pe.getMsg());
                }
                return mav;
            }else {
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试");
                if (e instanceof ParamsException){
                    ParamsException pe = (ParamsException) e;
                   resultInfo.setCode(pe.getCode());
                   resultInfo.setMsg(pe.getMsg());
                }
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter out = null;
                try {
                    out = resp.getWriter();
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }finally {
                    if (out!=null){
                        out.close();
                    }
                }
                return null;
            }
        }



        return mav;
    }
}

package cn.ggbond.ttvideo.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class BindingResultUtil {

    /**
     * 处理校验失败的BindingResult，返回错误信息
     *
     * @param bindingResult 校验结果
     * @return 错误信息字符串
     */

    public static String handleBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage=new StringBuilder("输入参数校验失败：");
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                errorMessage.append(objectError.getDefaultMessage());
            }
            return errorMessage.toString();
        }
        return null;
    }
}

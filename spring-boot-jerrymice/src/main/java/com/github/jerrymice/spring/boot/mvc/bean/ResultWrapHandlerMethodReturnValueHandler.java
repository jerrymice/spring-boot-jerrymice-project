package com.github.jerrymice.spring.boot.mvc.bean;

import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.ResultInfo;
import com.github.jerrymice.common.entity.entity.Status;
import com.github.jerrymice.spring.boot.mvc.annotation.WrapResponseBody;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * @author tumingjian
 * 创建时间: 2019-10-08 13:27
 * 功能说明:一个统一全局JSON响应的类,统一JSON响应类为Result类.原方法的返回值将赋值给Result.body属性
 * @see Result
 * @see ResultInfo
 */
public class ResultWrapHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private HandlerMethodReturnValueHandler delegate;

    public ResultWrapHandlerMethodReturnValueHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return this.delegate.supportsReturnType(returnType) || (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), WrapResponseBody.class) ||
                returnType.hasMethodAnnotation(WrapResponseBody.class));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        WrapResponseBody methodAnnotation = returnType.getMethodAnnotation(WrapResponseBody.class);
        Result result;
        if (returnValue instanceof Result && (result = ((Result) returnValue)).isEmpty() && !result.getCode().equals(GlobalErrorCode.REQUEST_SUCCESS.getCode())) {
            /**
             * 如果是错误的结果且body为空,那么序列化时不序列化body.也就是JSON没有body:null字符串,否则原样返回
             */
            Status wrapped = Status.wrapped(result.getCode(), result.getMessage());
            this.delegate.handleReturnValue(wrapped, returnType, mavContainer, webRequest);
        } else if (methodAnnotation == null || methodAnnotation.value()) {
            ResultInfo resultInfo = new ResultInfo(true).setBody(returnValue);
            this.delegate.handleReturnValue(resultInfo, returnType, mavContainer, webRequest);
        } else {
            this.delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        }
    }
}

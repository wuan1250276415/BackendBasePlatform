package pro.wuan.common.web.validator.controller;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.web.validator.extract.IConstraintExtract;
import pro.wuan.common.web.validator.model.FieldValidatorDesc;
import pro.wuan.common.web.validator.model.ValidConstraint;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @program: tellhowcloud
 * @description: 数据校验类
 * @author: HawkWang
 * @create: 2021-08-30 17:10
 **/
@RequestMapping
public class ValidatorFormController {
    private static final String FORM_VALIDATOR_URL = "/form/validator";
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final IConstraintExtract constraintExtract;

    public ValidatorFormController(IConstraintExtract constraintExtract, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.constraintExtract = constraintExtract;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /**
     * 支持第一种拉取方式
     * 注意： 具体的方法必须在参数上面标注 @Validated 才有效
     *
     * @param request 请求
     * @return 验证规则
     * @throws Exception 异常
     */
    @RequestMapping(FORM_VALIDATOR_URL + "/**")
    @ResponseBody
    public Result<Collection<FieldValidatorDesc>> standardByPathVar(HttpServletRequest request) throws Exception {
        String requestUri = request.getRequestURI();
        String formPath = CharSequenceUtil.subAfter(requestUri, FORM_VALIDATOR_URL, false);
        return Result.success(localFieldValidatorDescribe(request, formPath));
    }

    /**
     * 支持第二种拉取方式
     *
     * @param formPath 表单地址
     * @param request  请求
     * @return 验证规则
     * @throws Exception 异常
     */
    @RequestMapping(FORM_VALIDATOR_URL)
    @ResponseBody
    public Result<Collection<FieldValidatorDesc>> standardByQueryParam(@RequestParam(value = "formPath", required = false) String formPath, HttpServletRequest request) throws Exception {
        return Result.success(localFieldValidatorDescribe(request, formPath));
    }

    private Collection<FieldValidatorDesc> localFieldValidatorDescribe(HttpServletRequest request, String formPath) throws Exception {
        HandlerExecutionChain chains = requestMappingHandlerMapping.getHandler(request);
        if (chains == null) {
            return Collections.emptyList();
        }
        HandlerMethod method = (HandlerMethod) chains.getHandler();
        return loadValidatorDescribe(method);
    }

    /**
     * 官方验证规则： （可能还不完整）
     * A, 普通对象形：
     * B、@RequestBody形式：
     * <p>
     * 1，类或方法或参数上有 @Validated
     * 2，参数有 @Valid
     *
     * <p>
     * C、普通参数形式：
     * 类上有 有 @Validated
     * 参数有 任意注解
     *
     * <p>
     * 步骤：
     * 1，先判断类上是否存在
     * 2，判断方法上是否存在
     * 3，判断
     *
     * @param handlerMethod 处理方法
     * @return 验证规则
     * @throws Exception 异常
     */
    private Collection<FieldValidatorDesc> loadValidatorDescribe(HandlerMethod handlerMethod) throws Exception {
        Method method = handlerMethod.getMethod();
        Parameter[] methodParams = method.getParameters();
        if (methodParams == null || methodParams.length < 1) {
            return Collections.emptyList();
        }
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        if (methodParameters.length < 1) {
            return Collections.emptyList();
        }

        // 类上面的验证注解  handlerMethod.getBeanType().getAnnotation(Validated.class)
        Validated classValidated = method.getDeclaringClass().getAnnotation(Validated.class);

        List<ValidConstraint> validatorStandard = getValidConstraints(methodParams, methodParameters, classValidated);
        return constraintExtract.extract(validatorStandard);
    }

    @NonNull
    private List<ValidConstraint> getValidConstraints(Parameter[] methodParams, MethodParameter[] methodParameters, Validated classValidated) {
        List<ValidConstraint> validatorStandard = new ArrayList<>();
        for (int i = 0; i < methodParameters.length; i++) {
            // 方法上的参数 (能正确获取到 当前类和父类Controller上的 参数类型)
            MethodParameter methodParameter = methodParameters[i];
            // 方法上的参数 (能正确获取到 当前类和父类Controller上的 参数注解)
            Parameter methodParam = methodParams[i];

            Validated methodParamValidate = methodParam.getAnnotation(Validated.class);

            //在参数和类上面找注解
            if (methodParamValidate == null && classValidated == null) {
                continue;
            }

            // 优先获取方法上的 验证组，在取类上的验证组
            Class<?>[] group = null;
            if (methodParamValidate != null) {
                group = methodParamValidate.value();
            }
            if (group == null) {
                group = classValidated.value();
            }

            validatorStandard.add(new ValidConstraint(methodParameter.getParameterType(), group));
        }
        return validatorStandard;
    }
}


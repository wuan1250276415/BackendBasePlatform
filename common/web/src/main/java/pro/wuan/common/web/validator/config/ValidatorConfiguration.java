package pro.wuan.common.web.validator.config;

import jakarta.validation.Configuration;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.DefaultPropertyNodeNameProvider;
import org.hibernate.validator.internal.properties.DefaultGetterPropertySelectionStrategy;
import org.hibernate.validator.internal.properties.javabean.JavaBeanHelper;
import org.hibernate.validator.spi.nodenameprovider.PropertyNodeNameProvider;
import org.hibernate.validator.spi.properties.GetterPropertySelectionStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pro.wuan.common.web.validator.controller.ValidatorFormController;
import pro.wuan.common.web.validator.extract.DefaultConstraintExtractImpl;
import pro.wuan.common.web.validator.extract.IConstraintExtract;

/**
 * @program: tellhowcloud
 * @description: 数据校验配置类 应用程序添加注解@EnableFormValidator开启验证
 * @create: 2021-08-30 20:44
 **/
public class ValidatorConfiguration {

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = warp(Validation.byProvider(HibernateValidator.class)
                .configure()
                //快速失败返回模式
                .addProperty("hibernate.validator.fail_fast", "true"))
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
    private Configuration<HibernateValidatorConfiguration> warp(HibernateValidatorConfiguration configuration) {
        addValidatorMapping(configuration);
        //其他操作
        return configuration;
    }

    private void addValidatorMapping(HibernateValidatorConfiguration configuration) {
        // 增加一个我们自定义的校验处理器与length的映射
        GetterPropertySelectionStrategy getterPropertySelectionStrategyToUse = new DefaultGetterPropertySelectionStrategy();
        PropertyNodeNameProvider defaultPropertyNodeNameProvider = new DefaultPropertyNodeNameProvider();
        ConstraintMapping mapping = new DefaultConstraintMapping(new JavaBeanHelper(getterPropertySelectionStrategyToUse, defaultPropertyNodeNameProvider));

        configuration.addMapping(mapping);
    }

    /**
     * Method:  开启快速返回
     * Description:
     * 如果参数校验有异常，直接抛异常，不会进入到 controller，使用全局异常拦截进行拦截
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator);
        return postProcessor;
    }

    @Bean
    public IConstraintExtract constraintExtract(Validator validator) {
        return new DefaultConstraintExtractImpl(validator);
    }

    @Bean
    public ValidatorFormController getFormValidatorController(IConstraintExtract constraintExtract, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new ValidatorFormController(constraintExtract, requestMappingHandlerMapping);
    }

}

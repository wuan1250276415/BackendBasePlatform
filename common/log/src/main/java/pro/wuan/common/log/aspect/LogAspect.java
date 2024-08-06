package pro.wuan.common.log.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.wuan.common.core.model.BusLog;
import pro.wuan.common.core.model.GlobalContext;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.utils.AddrUtil;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.mq.constant.GroupEnum;
import pro.wuan.common.mq.constant.TagEnum;
import pro.wuan.common.mq.model.MqMessage;
import pro.wuan.common.mq.service.MqProducerService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LogAspect {
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private MqProducerService mqProducerService;


    @Pointcut("@annotation(pro.wuan.common.log.AspectLog)")
    public void log() {
    }


    @Around("log()")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long time = System.currentTimeMillis();
        String success = "Y";
        log.info("========log monitor start: calssName={},methodName={},paramt={}",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), this.toJSONString(joinPoint.getArgs()));
        Result result = null;
        // 异常信息
        String exceptionDetail = null;
        try {

            return joinPoint.proceed();
        } catch (DataIntegrityViolationException e) {
            success = "N";
            exceptionDetail = stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace());
            if (exceptionDetail.contains("Data too long for")) {
                result = Result.failure("数据输入过长");
            } else {
                result = Result.failure(e.getMessage());
            }
        }catch(ConstraintViolationException e){
            //后台对列表的数据校验异常需要抛出到全局进行处理
            success = "N";
            log.error("========log monitor error: calssName={},methodName={},error={}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), e.getMessage());
            throw e;
        }catch (Exception e) {
            success = "N";
            exceptionDetail = stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace());
            log.error("========log monitor error: calssName={},methodName={},error={}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), e.getMessage());
            //throw e;
            result = Result.failure(e.getMessage());
        } finally {
            time = System.currentTimeMillis() - time;
            // 方法执行完成后增加日志存储
            addOperationLog(joinPoint, time, success, exceptionDetail);
            log.info("========log monitor end: calssName={},methodName={}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        }
        return result;
    }

    private void addOperationLog(JoinPoint joinPoint, long time, String success, String exceptionDetail) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AspectLog auditLog = signature.getMethod().getAnnotation(AspectLog.class);
        if (auditLog != null) {
            //日志表新增逻辑
            BusLog busLog = getAudit(auditLog, joinPoint);
            busLog.setLogSuccess(success);
            busLog.setParameters(this.toJSONString(joinPoint.getArgs()));
            busLog.setResponseTime(time);
            if (exceptionDetail != null) {
                busLog.setMethodName(OperationType.OPERATION_EXCEPTION);
                busLog.setExceptionDetail(exceptionDetail.length() > 1000 ? exceptionDetail.substring(0, 1000) : exceptionDetail);
            }
            busLog.setCreateUserId(GlobalContext.getCurrentUserId());
            busLog.setCreateUserName(GlobalContext.getCurrentUserName());
            busLog.setUpdateUserId(GlobalContext.getCurrentUserId());
            busLog.setTenantId(GlobalContext.getTenantId());

            try {
                MqMessage mqMessage = new MqMessage();
                mqMessage.setGroupEnum(GroupEnum.LOGGROUP);
                mqMessage.setTagEnum(TagEnum.LOG);
                mqMessage.setContent(JSON.toJSONString(busLog));
                mqProducerService.asynSendMsg(mqMessage);
            } catch (Exception e) {
                log.error("mqProducerService sendMsg error:" + ExceptionUtil.getMessage(e));
            }
        }
    }

    /**
     * joinPoint.getArgs()返回的数组中携带有Request或者Response对象时，会导致序列化异常，需要进行过滤
     *
     * @param args
     * @return
     */
    private String toJSONString(Object[] args) {
        /*Stream<?> stream = ArrayUtils.isEmpty(args) ? Stream.empty() : Arrays.asList(args).stream();
        List<Object> logArgs = stream.filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse))).collect(Collectors.toList());
        return JSON.toJSONString(logArgs);*/
        return null;
    }


    private BusLog getAudit(AspectLog auditLog, JoinPoint joinPoint) {
        BusLog audit = new BusLog();
        audit.setApplicationName(applicationName);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        audit.setClassName(joinPoint.getTarget().getClass().getName());
        audit.setMethodName(methodSignature.getName());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader("x-userid-header");
        String userName = request.getHeader("x-user-header");
        String clientId = request.getHeader("x-tenant-header");
        //获取请求的路径
        String url = request.getRequestURI();
        audit.setRequestUrl(url);
        String operation = StringUtils.isNotEmpty(auditLog.description()) ? auditLog.description() : methodSignature.getName();
        audit.setOperation(operation);
        audit.setOperationType(auditLog.type());
        audit.setIp(AddrUtil.getRemoteAddr(request));
        audit.setUserAgent(request.getHeader("user-agent"));
        return audit;
    }


    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut("execution(* pro.wuan.*.*..*Controller.*(..))")
    public void operExceptionLogPoinCut() {
    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     *
     * @param joinPoint 切入点
     * @param e         异常信息
     */
    @AfterThrowing(pointcut = "log()", throwing = "e")
    public Object saveExceptionLog(JoinPoint joinPoint, Throwable e) {
        try {
            // 异常信息
            String exceptionDetail = stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace());
            addOperationLog(joinPoint, 0, "N", exceptionDetail);

        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return Result.failure(ExceptionUtil.getMessage(e));
    }

    /**
     * 转换request 请求参数
     *
     * @param paramMap request获取的参数数组
     */
    public Map<String, String> converMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<String, String>();
        for (String key : paramMap.keySet()) {
            rtnMap.put(key, paramMap.get(key)[0]);
        }
        return rtnMap;
    }

    /**
     * 转换异常信息为字符串
     *
     * @param exceptionName    异常名称
     * @param exceptionMessage 异常信息
     * @param elements         堆栈信息
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strbuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet + "\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
        return message;
    }
}

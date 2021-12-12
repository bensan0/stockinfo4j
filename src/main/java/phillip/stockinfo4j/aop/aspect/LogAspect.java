package phillip.stockinfo4j.aop.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import phillip.stockinfo4j.aop.anno.Log;
import phillip.stockinfo4j.aop.dto.LogDTO;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("@annotation(phillip.stockinfo4j.aop.anno.Log)")
    public void logAnno() {
    }

    @Pointcut("@annotation(phillip.stockinfo4j.aop.anno.Log) || execution(* phillip.stockinfo4j.controller.*.*(..))")
    public void annoAndController() {
    }
//    @Before("logAnno()")
//    public void before(){
//        log.info("=================before通知=====================");
//    }
//
//    @After("logAnno()")
//    public void after(){
//        log.info("=================after通知=====================");
//    }
//
//    @AfterReturning(value = "logAnno()", returning = "result")
//    public void afterReturning(Object result){
//        log.info("=================afterReturning通知=====================" + result);
//    }
//
//    @AfterThrowing(value = "logAnno()", throwing = "e")
//    public void afterThrowing(Throwable e){
//        log.info("=================afterThrowing通知=====================" + e.getMessage());
//    }

    @Around("annoAndController()")
    public Object logAround(ProceedingJoinPoint joinPoint) {

        Object result = null;
        LogDTO dto = new LogDTO();
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String className = signature.getDeclaringType().getSimpleName(); // 取得方法所在的類別名稱
            Method method = signature.getMethod(); // 取得被annotation標注的方法
            String methodName = method.getName(); // 取得方法名稱

            Log logInfo = method.getAnnotation(Log.class); // 取得標注的annotation

            Parameter[] parameters = method.getParameters(); // 取得方法輸入參數資訊
            Object[] args = joinPoint.getArgs(); // 取得輸入參數值
            String paramsJson = getParamsMapJsonString(parameters, args);
            log.info(className + "-" + methodName + " start, params: " + paramsJson);
            Long startTime = System.currentTimeMillis();
            result = joinPoint.proceed();
            Long timeCost = System.currentTimeMillis() - startTime;
            dto.setClassName(className);
            dto.setArgs(paramsJson);
            dto.setStartTime(startTime);
            dto.setTimeCost(timeCost.intValue());
            dto.setResult(result);
            dto.setMethodName(methodName);
        } catch (Throwable e) {
            log.error(e.getMessage());
            dto.setThrowable(e);
        } finally {
            log.info(dto.toString());
        }
        return result;
    }

    private String getParamsMapJsonString(Parameter[] parameters, Object[] args) throws JsonProcessingException {
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            params.put(parameters[i].getName(), args[i]);
        }
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw e;
        }
    }
}

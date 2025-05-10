package com.example.ceos.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.example.ceos.controller.*.*(..))")
    public void controllerLayer() {}

    @Pointcut("execution(* com.example.ceos.service.*.*(..))")
    public void serviceLayer() {}

    @Around("controllerLayer() || serviceLayer()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("开始执行 {}.{}", className, methodName);
        if (args.length > 0) {
            logger.debug("参数: {}", args);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();

            logger.info("完成执行 {}.{}, 耗时: {}ms", className, methodName, stopWatch.getTotalTimeMillis());
            if (result != null) {
                logger.debug("返回值: {}", result);
            }

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            logger.error("执行 {}.{} 时发生错误, 耗时: {}ms", className, methodName, stopWatch.getTotalTimeMillis(), e);
            throw e;
        }
    }
} 
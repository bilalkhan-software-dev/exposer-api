package com.exposer.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectForLogging {

    @Around("execution(* com.exposer.controllers..*(..))")
    public Object loggingForController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint);
    }

    @Around("execution(* com.exposer.dao..*(..))")
    public Object loggingForDao(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint);
    }

    @Around("execution(* com.exposer.services..*(..))")
    public Object loggingForServices(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint);
    }

    private Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        Signature signature = joinPoint.getSignature();
        String className = signature.getName();
        String methodName = signature.getDeclaringType().getSimpleName();
        log.info("Calling :: {}.{}()", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.currentTimeMillis() - startTime);
            log.info("End :: {}.{}() ==> duration={}ms", className, methodName, durationMs);
            return result;
        } catch (Throwable ex) {
            long durationMs = (System.currentTimeMillis() - startTime);
            log.warn("Exception occurred in {}.{}() after {}ms", className, methodName, durationMs);
            throw ex;
        }
    }


}

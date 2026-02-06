package com.example.ecommerce_system.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerLayer() {}

    @Pointcut("within(@org.springframework.graphql.data.method.annotation.MutationMapping *) ||" +
                "within(@org.springframework.graphql.data.method.annotation.QueryMapping * )")
    public void graphqlControllerLayer() {}

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryLayer(){}

    @Around("restControllerLayer() || graphqlControllerLayer()")
    public Object logControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("API Request: {}.{}()", className, methodName);

        try {
            Object result = joinPoint.proceed();
            logger.info("API Response: {}.{}() - Success", className, methodName);
            return result;
        } catch (Exception e) {
            logger.error("API Error: {}.{}() - {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    @Around("serviceLayer()")
    public Object logServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.debug("Entering: {}.{}() with arguments: {}",
                className, methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            logger.debug("Exiting: {}.{}()", className, methodName);
            return result;
        } catch (Exception e) {
            logger.error("Exception in {}.{}(): {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    @Around("repositoryLayer()")
    public Object logRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.debug("DB Call: {}.{}()", className, methodName);

        try {
            Object result = joinPoint.proceed();
            logger.debug("DB Result: {}.{}()", className, methodName);
            return result;
        } catch (Exception e) {
            logger.error("DB Error: {}.{}(): {}", className, methodName, e.getMessage());
            throw e;
        }
    }

}

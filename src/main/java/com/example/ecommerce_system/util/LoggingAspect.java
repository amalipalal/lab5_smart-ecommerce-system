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
        return logMethodExecution(joinPoint, "API Request", "API Response", "API Error");
    }

    private Object logMethodExecution(ProceedingJoinPoint joinPoint,
                                      String entryPrefix,
                                      String successPrefix,
                                      String errorPrefix) throws Throwable {
        Logger logger = getLogger(joinPoint);
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("{}: {}.{}()", entryPrefix, className, methodName);

        try {
            Object result = joinPoint.proceed();
            logger.info("{}: {}.{}() - Success", successPrefix, className, methodName);
            return result;
        } catch (Exception e) {
            logger.error("{}: {}.{}() - {}", errorPrefix, className, methodName, e.getMessage());
            throw e;
        }
    }

    private Logger getLogger(ProceedingJoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
    }

    @Around("serviceLayer()")
    public Object logServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = getLogger(joinPoint);
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.debug("Entering: {}.{}() with arguments: {}",
                className, methodName, Arrays.toString(joinPoint.getArgs()));

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
        return logMethodExecution(joinPoint, "DB Call", "DB Result", "DB Error");
    }
}

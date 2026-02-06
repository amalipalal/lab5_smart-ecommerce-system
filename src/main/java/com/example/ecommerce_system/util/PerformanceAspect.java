package com.example.ecommerce_system.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object measureRest(ProceedingJoinPoint joinPoint) throws Throwable {
        return measure(joinPoint, "REST");
    }

    @Around("within(com.example.ecommerce_system.controller.graphql..*)")
    public Object measureGraphQL(ProceedingJoinPoint joinPoint) throws Throwable {
        return measure(joinPoint, "GraphQL");
    }

    private Object measure(ProceedingJoinPoint joinPoint, String type) throws Throwable {
        long start = System.currentTimeMillis();
        long payloadSize = calculatePayloadSize(joinPoint.getArgs());

        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        logger.info("[{}] {} - Duration: {}ms, Payload Size: {} bytes",
                type,
                joinPoint.getSignature().toShortString(),
                duration,
                payloadSize);

        return result;
    }

    private long calculatePayloadSize(Object[] args) {
        if (args == null || args.length == 0) {
            return 0L;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            for (Object arg : args) {
                if (arg != null) {
                    oos.writeObject(arg);
                }
            }
            oos.flush();
            return baos.size();
        } catch (Exception e) {
            logger.debug("Unable to calculate payload size: {}", e.getMessage());
            return -1L;
        }
    }
}

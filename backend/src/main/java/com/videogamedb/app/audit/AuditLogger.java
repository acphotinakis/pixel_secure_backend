package com.videogamedb.app.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogger {

    private static final Logger logger = LoggerFactory.getLogger("SENSITIVE_DATA_AUDIT");

    @Around("@annotation(sensitiveDataAudit)")
    public Object logSensitiveDataAccess(ProceedingJoinPoint joinPoint,
            SensitiveDataAudit sensitiveDataAudit) throws Throwable {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String method = joinPoint.getSignature().getName();

        logger.info("SENSITIVE_ACCESS - User: {}, Action: {}, Resource: {}, Method: {}, Time: {}",
                username, sensitiveDataAudit.action(), sensitiveDataAudit.resource(),
                method, LocalDateTime.now());

        try {
            Object result = joinPoint.proceed();
            logger.info("SENSITIVE_ACCESS_SUCCESS - User: {}, Action: {}", username, sensitiveDataAudit.action());
            return result;
        } catch (Exception e) {
            logger.error("SENSITIVE_ACCESS_FAILED - User: {}, Action: {}, Error: {}",
                    username, sensitiveDataAudit.action(), e.getMessage());
            throw e;
        }
    }
}
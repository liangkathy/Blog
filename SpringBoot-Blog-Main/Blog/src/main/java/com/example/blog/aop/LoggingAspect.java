package com.example.blog.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.blog.service..*.*(..))") //for all service layers in program
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logAroundMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Around method: " + proceedingJoinPoint.getSignature().toShortString() + "; Before Trigger time: " + System.currentTimeMillis());
        Object result = proceedingJoinPoint.proceed(); //run the method
        log.info("After running the method :" + proceedingJoinPoint.getSignature().toShortString() + "; After Trigger time: " + System.currentTimeMillis());
        return result; //provide return in postman
    }

    //Logging after any methods in program when Exception is thrown
    @AfterThrowing(pointcut = "execution(* com.example.blog.*..*.*(..))", throwing = "throwable")
    public void allMethodExceptions(JoinPoint joinPoint, Throwable throwable) {
        String methodName = joinPoint.getSignature().toShortString(); //get method name and shorten if needed
        String arguments = Arrays.toString(joinPoint.getArgs()); //get arguments passed in method

        //log error message with details
        log.error("Exception caught in method: "
        + methodName + " with arguments: " + arguments + ". Exception message: " + throwable.getMessage());
    }


}

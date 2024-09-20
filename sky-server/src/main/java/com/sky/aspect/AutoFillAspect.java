package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
@Slf4j
@Aspect  // 表示此类为一个切面类
public class AutoFillAspect {

    // 切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillAspect(){}


    /**
     * 使用前置通知因为必须在sql语句执行之前将公共字段赋值
     */
    @Before("autoFillAspect()")  // 通知注解里面的参数是用来指定切入点的，而我们需要在切点表达式生效后再进此方法
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("开始进行公共字段的填充...");

        // 获取当前被拦截到的方法上数据库的操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();  // 因为拦截到的是方法但Signature是接口所以需要向下转型为MethodSignature
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);  // 获得方法上的注解对象
        OperationType operationType = autoFill.value();  // 获得数据库的操作类型
        // 获取当前被拦截到的方法的参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object object = args[0];  // 因为update只有一个参数所以args[0]就是Employee类 参数较多时约定在第一位即可
        // 准备赋值数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据当前不同的操作类型，通过反射来赋值
        if(operationType == OperationType.INSERT){  // 如果是插入操作
            try {
                // 先用getClass获取类再用getDeclaredMethod来获取类的方法
                Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateuser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateuser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 通过反射来为对象赋值
                setCreateTime.invoke(object,now);
                setCreateuser.invoke(object,currentId);
                setUpdateuser.invoke(object,currentId);
                setUpdateTime.invoke(object,now);
            } catch (Exception e){
                e.printStackTrace();
            }

        }else if(operationType == OperationType.UPDATE){
            try {
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateuser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 通过反射来为对象赋值
                setUpdateuser.invoke(object,currentId);
                setUpdateTime.invoke(object,now);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}

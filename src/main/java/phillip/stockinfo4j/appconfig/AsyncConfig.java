package phillip.stockinfo4j.appconfig;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync(/*proxyTargetClass = true /*mode = AdviceMode.ASPECTJ*/)
/*
* 在写SpringBoot单元测试中，需要mock一个Bean的一个方法，但是该方法有@Async注解。不管怎么写Mock方法，都会在Mock校验的时候报错，提示该方法无法被Mock。
在经过大量搜索后，有两种解决方案：

1.去掉 @EnableAsync 注解
2.把 @EnableAsync 加上参数，@EnableAsync(mode = AdviceMode.ASPECTJ) ，当把mode设置为AdviceMode.ASPECTJ，Mock方法能被正常执行，但是该方法会变成同步调用。
*/
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, obj) -> {
            System.out.println("多線程異常處理");
            System.out.println("Exception Caught in Thread - " + Thread.currentThread().getName());
            System.out.println("Exception message - " + throwable.getMessage());
            System.out.println("Method name - " + method.getName());
            for (Object param : obj) {
                System.out.println("Parameter value - " + param);
            }
        };
    }
}

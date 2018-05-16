package co.ke.bigfootke.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync 
public class BulkSmsApplication{// implements AsyncConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(BulkSmsApplication.class, args);
	}

//	@Bean
//	@Override
//	public Executor getAsyncExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(4);
//        executor.setMaxPoolSize(4);
//        executor.setQueueCapacity(500);
//        executor.setThreadNamePrefix("MyExecutor_");
//        executor.initialize();
//        return executor;
//	}
//
//	@Bean
//	@Override
//	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//		return new SimpleAsyncUncaughtExceptionHandler();
//	}
}

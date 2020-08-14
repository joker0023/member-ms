package com.jokerstation.member.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.jokerstation.member.service.AppService;

@Configuration
@EnableScheduling
public class ScheduledConfig implements SchedulingConfigurer {
	
	/**
	 * 更新处理记录
	 */
	@Scheduled(cron="${scheduled.cleanToken.cron}")
	public void cleanToken() {
		AppService.cleanToken();
	}
	
	@Bean
    public TaskScheduler myScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
        return scheduler;
    }
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(myScheduler());
	}
}

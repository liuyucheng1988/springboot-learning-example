package org.spring.springboot.util.cron;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;

@Configuration
public class QuartzConfig {

    private static String jobName = "MonthCallSizeJob";
    private static String jobGroup = "MonthCallSizeJobGroup";

    @Bean
    public JobDetail jobDetail(){
        return JobBuilder.newJob(MonthCallSizeJob.class)
                .withIdentity(jobName,jobGroup)
                .storeDurably().build();
    }

    @Bean
    public Trigger trigger() throws ParseException {
        String triName = "triggerMonthCallSizeJob";
        // 出发表达式
        String triPress = "0 0 1 1 * ?";
//        String triPress = "0 28 17 3 * ?";
//        String triPress = "*/10 * * * * ?";
        CronExpression express = new CronExpression(triPress);
        // 构建触发器
        return TriggerBuilder.newTrigger()
                .withIdentity(triName, jobGroup)
                .forJob(jobDetail())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(express))
                .build();
    }
}
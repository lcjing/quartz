package com.lcjing.quartz.calendar;

import com.lcjing.quartz.job.MyJob1;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.AnnualCalendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 用于排除某些节假日
 *
 * @author lcjing
 * @date 2020/08/10
 */
public class CalendarDemo {
    public static void main(String[] args) throws Exception {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        scheduler.start();

        // 定义日历
        AnnualCalendar holidays = new AnnualCalendar();

        // 排除中秋节
        Calendar midAutumn = new GregorianCalendar(2019, 9, 13);
        holidays.setDayExcluded(midAutumn, true);
        // 排除圣诞节
        Calendar christmas = new GregorianCalendar(2019, 12, 25);
        holidays.setDayExcluded(christmas, true);

        // 调度器添加日历
        scheduler.addCalendar("holidays", holidays, false, false);

        JobDetail jobDetail = JobBuilder.newJob(MyJob1.class)
                .withIdentity("job1", "group1")
                .usingJobData("name", "quartz")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .modifiedByCalendar("holidays")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(2)
                        .repeatForever())
                .build();

        Date firstRunTime = scheduler.scheduleJob(jobDetail, trigger);
        System.out.println(jobDetail.getKey() + " 第一次触发： " + firstRunTime);
    }
}

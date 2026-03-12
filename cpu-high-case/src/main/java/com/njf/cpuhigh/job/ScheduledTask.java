package com.example.cpudemo.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
@Slf4j
public class ScheduledTask {

    private final Random random = new Random();
    private volatile boolean highCpuTaskRunning = false;

    /**
     * 模拟正常定时任务
     */
    @Scheduled(fixedDelay = 30000) // 每30秒执行一次
    public void normalTask() {
        log.debug("正常定时任务执行");
    }

    /**
     * 模拟有问题的定时任务 - 偶尔CPU飙高
     */
    @Scheduled(fixedDelay = 60000) // 每分钟执行一次
    public void problematicTask() {
        if (random.nextInt(10) < 2) { // 20%概率触发问题
            log.warn("⚠️ 触发有问题的定时任务，将占用CPU 5秒钟");
            highCpuTaskRunning = true;

            long endTime = System.currentTimeMillis() + 5000;
            int count = 0;

            while (System.currentTimeMillis() < endTime) {
                count++;
                // 模拟CPU密集型计算
                for (int i = 0; i < 1000; i++) {
                    Math.sin(random.nextDouble());
                    Math.cos(random.nextDouble());
                }
            }

            highCpuTaskRunning = false;
            log.warn("有问题的定时任务完成，计算次数: {}", count);
        }
    }
}
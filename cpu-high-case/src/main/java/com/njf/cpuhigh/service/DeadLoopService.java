package com.njf.cpuhigh.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 死循环服务
 */
@Service
@Slf4j
public class DeadLoopService {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicBoolean running = new AtomicBoolean(false);

    public String startDeadLoop(boolean infinite) {
        if (running.compareAndSet(false, true)) {
            executor.submit(() -> {
                log.info("死循环线程启动");
                long counter = 0;
                long startTime = System.currentTimeMillis();

                // 问题1: 纯CPU计算死循环
                if (infinite) {
                    while (running.get()) {
                        counter++;
                        // 模拟一些计算
                        Math.pow(counter % 100, 2);
                    }
                }
                // 问题2: 有条件的死循环（bug导致条件永真）
                else {
                    boolean condition = true;
                    int retryCount = 0;

                    while (condition && running.get()) {
                        counter++;
                        retryCount++;

                        // 模拟一个有bug的条件判断
                        if (retryCount > 1000000) {
                            // 这个条件应该为假，但因为bug永远不会满足
                            condition = (retryCount % 2 == 0) && (retryCount % 3 == 0)
                                    && (retryCount % 5 == 0) && (retryCount % 7 == 0);
                        }

                        // 模拟一些业务逻辑
                        processData(counter);
                    }
                }

                long duration = System.currentTimeMillis() - startTime;
                log.info("死循环结束，循环次数: {}, 持续时间: {}ms", counter, duration);
            });

            return "死循环已启动 (infinite: " + infinite + ")";
        }
        return "死循环已经在运行中";
    }

    private void processData(long data) {
        // 模拟数据处理
        String str = String.valueOf(data);
        int hash = str.hashCode();
        // 无意义的计算，消耗CPU
        for (int i = 0; i < 10; i++) {
            hash = Integer.rotateLeft(hash, i);
        }
    }

    public void stopDeadLoop() {
        if (running.compareAndSet(true, false)) {
            log.info("停止死循环");
        }
    }

    @PreDestroy
    public void shutdown() {
        stopDeadLoop();
        executor.shutdownNow();
    }
}
package com.njf.cpuhigh.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 锁竞争服务
 */
@Service
@Slf4j
public class LockContentionService {

    private final ReentrantLock globalLock = new ReentrantLock();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicInteger activeThreads = new AtomicInteger(0);
    private volatile boolean isRunning = false;

    public String startLockContention(int threadCount) {
        if (!isRunning) {
            isRunning = true;
            activeThreads.set(0);

            log.info("启动锁竞争测试，线程数: {}", threadCount);

            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    activeThreads.incrementAndGet();
                    int operationCount = 0;

                    while (isRunning && operationCount < 1000000) {
                        // 激烈竞争同一把锁
                        globalLock.lock();
                        try {
                            operationCount++;

                            // 模拟持有锁时的处理
                            int sum = 0;
                            for (int j = 0; j < 1000; j++) {
                                sum += j;
                            }

                            // 偶尔释放CPU，模拟I/O等待
                            if (operationCount % 100 == 0) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        } finally {
                            globalLock.unlock();
                        }
                    }

                    activeThreads.decrementAndGet();
                    log.debug("线程 {} 结束，操作次数: {}", threadId, operationCount);
                });
            }

            return String.format("已启动 %d 个线程竞争同一把锁，CPU将因锁竞争而飙高", threadCount);
        }
        return "锁竞争测试已在运行中";
    }

    public void stopContention() {
        isRunning = false;
        log.info("停止锁竞争测试");
    }

    @PostConstruct
    public void init() {
        // 监控线程，定期报告状态
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
        monitor.scheduleAtFixedRate(() -> {
            if (isRunning) {
                int blockedThreads = Thread.getAllStackTraces().keySet().stream()
                        .filter(t -> t.getState() == Thread.State.BLOCKED)
                        .mapToInt(t -> 1)
                        .sum();

                if (blockedThreads > 0) {
                    log.info("当前有 {} 个线程阻塞，{} 个线程活跃",
                            blockedThreads, activeThreads.get());
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}
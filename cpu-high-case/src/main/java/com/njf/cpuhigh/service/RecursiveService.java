package com.njf.cpuhigh.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 递归问题
 */
@Service
@Slf4j
public class RecursiveService {

    private final ConcurrentHashMap<Integer, Long> fibonacciCache = new ConcurrentHashMap<>();
    private final AtomicInteger callCount = new AtomicInteger(0);

    public String deepRecursive(int depth) {
        if (depth <= 0) {
            depth = 10000; // 默认深度
        }

        int finalDepth = depth;
        new Thread(() -> {
            log.info("开始深度递归，深度: {}", finalDepth);
            long startTime = System.currentTimeMillis();

            try {
                // 有问题的递归：没有缓存，重复计算
                long result = fibonacciWithoutCache(finalDepth);

                long duration = System.currentTimeMillis() - startTime;
                log.info("递归计算完成，fib({}) = {}，调用次数: {}，耗时: {}ms",
                        finalDepth, result, callCount.get(), duration);

                // 重置计数器
                callCount.set(0);
            } catch (StackOverflowError e) {
                log.error("栈溢出错误！递归深度: {}，调用次数: {}", finalDepth, callCount.get());
            }
        }).start();

        return "递归计算已启动，深度: " + depth + " (斐波那契数列)";
    }

    /**
     * 有问题的斐波那契实现：指数级时间复杂度
     */
    private long fibonacciWithoutCache(int n) {
        callCount.incrementAndGet();

        if (n <= 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }

        // 大量重复计算
        return fibonacciWithoutCache(n - 1) + fibonacciWithoutCache(n - 2);
    }

    /**
     * 优化的斐波那契实现：使用缓存
     */
    private long fibonacciWithCache(int n) {
        if (n <= 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }

        return fibonacciCache.computeIfAbsent(n, k ->
                fibonacciWithCache(k - 1) + fibonacciWithCache(k - 2)
        );
    }
}
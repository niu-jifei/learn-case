package com.njf.cpuhigh.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GC问题模拟服务
 */
@Service
@Slf4j
public class GcProblemService {

    // 内存泄漏的集合
    private final List<byte[]> memoryLeakList = Collections.synchronizedList(new ArrayList<>());

    public String createGarbage(int count) {
        log.info("开始创建垃圾对象，数量: {}", count);

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                // 创建大对象，快速消耗内存
                byte[] largeObject = new byte[1024 * 1024]; // 1MB

                // 写入一些数据
                Arrays.fill(largeObject, (byte) (i % 256));

                // 立即丢弃引用，成为垃圾
                // 这里不保持引用，让GC回收
            }
            log.info("垃圾对象创建完成");
        }).start();

        return "正在创建 " + count + " 个1MB的临时对象，将触发频繁GC";
    }

    public String createMemoryLeak() {
        new Thread(() -> {
            int counter = 0;
            while (counter < 100) {
                // 创建对象并添加到泄漏列表
                byte[] data = new byte[1024 * 512]; // 512KB
                memoryLeakList.add(data);

                // 模拟一些处理
                for (int i = 0; i < data.length; i += 1000) {
                    data[i] = (byte) (counter % 256);
                }

                counter++;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            log.info("内存泄漏模拟完成，泄漏了 {} 个对象，总计约 {}MB",
                    memoryLeakList.size(), memoryLeakList.size() * 512 / 1024);
        }).start();

        return "内存泄漏模拟已启动，将持续添加对象到列表而不释放";
    }
}
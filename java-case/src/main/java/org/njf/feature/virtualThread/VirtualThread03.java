package org.njf.feature.virtualThread;

import java.util.concurrent.Executors;

/**
 * 执行模型
 */
public class VirtualThread03 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 虚拟线程执行演示 ===");

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 创建 5 个虚拟线程
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    // 可以通过 threadId() 获取唯一的线程ID
                    System.out.println("任务 " + taskId + " 开始 - 载体: " +
                            Thread.currentThread().getName() + " - id: " + Thread.currentThread().threadId());

                    try {
                        Thread.sleep(5000);  // 模拟 I/O
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("任务 " + taskId + " 中间 - 载体: " +
                            Thread.currentThread().getName() + " - id: " + Thread.currentThread().threadId());

                    try {
                        Thread.sleep(5000);  // 再次模拟 I/O
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("任务 " + taskId + " 结束 - 载体: " +
                            Thread.currentThread().getName() + " - id: " + Thread.currentThread().threadId());
                });
            }

            Thread.sleep(100000);  // 等待所有任务完成
        }

        System.out.println("=== 所有任务完成 ===");
    }
}

package org.njf.feature.virtualThread;

import java.util.concurrent.Executors;

/**
 * 虚拟线程
 *
 */
public class VirtualThread01 {
    public static void main(String[] args) throws InterruptedException {
        // 创建虚拟线程, 虚拟线程是非阻塞的
        Thread virtualThread = Thread.ofVirtual()
                .start(() -> {
                    System.out.println("Virtual thread started");
                    try {
                        // 虚拟线程 sleep 不阻塞平台线程
                        // 虚拟线程挂起，不占用 OS 线程
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Virtual thread finished");
                });

        // 使用虚拟线程执行器
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                System.out.println("另一个虚拟线程");
            });
        }

        // 等待虚拟线程结束
        virtualThread.join();

    }
}

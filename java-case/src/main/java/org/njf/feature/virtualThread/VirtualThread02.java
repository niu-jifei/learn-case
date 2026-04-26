package org.njf.feature.virtualThread;


import java.util.concurrent.Executors;

/**
 * 虚拟线程非阻塞
 */
public class VirtualThread02 {
//    public static void main(String[] args) {
//        // 创建虚拟线程执行器
//        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            // 提交多个虚拟线程
//            for (int i = 0; i < 10; i++) {
//                final int taskId = i;
//                executor.submit(() -> {
//                    System.out.println("虚拟线程 " + taskId + " 开始执行");
//                    try {
//                        Thread.sleep(1000);  // 阻塞操作
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("虚拟线程 " + taskId + " 结束");
//                });
//            }
//        }
//    }


//    public static void main(String[] args) throws InterruptedException {
//        // 载体线程池大小：Java 虚拟线程的载体线程池默认大小就是 CPU 核数
//        System.out.println("载体线程数: " + Runtime.getRuntime().availableProcessors());
//        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            // 创建 100 个虚拟线程
//            for (int i = 0; i < 100; i++) {
//                final int taskId = i;
//                executor.submit(() -> {
//                    System.out.println("任务 " + taskId + " 在线程 " +
//                            Thread.currentThread().getName() +  "  " + Thread.currentThread().getId() + " 执行");
//
//                    try {
//                        Thread.sleep(5000);  // 模拟 I/O 操作
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    System.out.println("任务 " + taskId + " 完成");
//                });
//            }
//
//            Thread.sleep(5000);  // 等待所有任务完成
//        }
//    }


    // 测试虚拟线程的切换,先执行虚拟线程1， 虚拟线程1阻塞了【挂起】，载体线程切换到虚拟线程2，再执行虚拟线程2，虚拟线程2执行完成后，载体线程切换回虚拟线程1【恢复】，虚拟线程1继续执行
//    public static void main(String[] args) {
//        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            executor.submit(() -> {
//                System.out.println("虚拟线程1 开始，载体线程: " +
//                        Thread.currentThread().getName());
//
//                try {
//                    Thread.sleep(2000);  // 阻塞操作
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println("虚拟线程1 继续，载体线程: " +
//                        Thread.currentThread().getName());
//            });
//
//            executor.submit(() -> {
//                try {
//                    Thread.sleep(500);  // 先休眠一下
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println("虚拟线程2 执行，载体线程: " +
//                        Thread.currentThread().getName());
//            });
//        }
//    }

    /*
// JVM 内部的伪代码
class VirtualThread {
    private Runnable task;
    private CarrierThread currentCarrier;

    void run() {
        while (!task.isDone()) {
            // 在载体线程上执行
            currentCarrier.execute(task);

            // 如果遇到阻塞操作
            if (task.isBlocking()) {
                // 挂起虚拟线程，释放载体线程
                this.unmount(currentCarrier);

                // 等待阻塞完成
                waitForUnblock();

                // 重新调度到载体线程
                this.mount(findAvailableCarrier());
            }
        }
    }
}
     */
    public static void main(String[] args) {
        // Java 虚拟线程：看起来像同步代码
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                // 这些操作会自动挂起虚拟线程
                try {
                    Thread.sleep(1000);     // 阻塞, 自动处理虚拟线程的切换
                    System.out.println("Hello");  // 执行
                    Thread.sleep(1000);     // 阻塞
                    System.out.println("World");  // 执行
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}

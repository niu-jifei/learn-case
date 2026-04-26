package org.njf.feature.virtualThread;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class VirtualThreadCarrierDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 虚拟线程载体切换演示 ===");
        System.out.println("可用CPU核心数: " + Runtime.getRuntime().availableProcessors());
        
        // 记录所有使用过的载体线程
        Set<String> carrierThreads = ConcurrentHashMap.newKeySet();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            int taskCount = 50;
            
            for (int i = 1; i <= taskCount; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    Thread virtualThread = Thread.currentThread();
                    
                    // 第一次执行时的载体线程
                    String firstCarrier = getCarrierThreadName(virtualThread);
                    carrierThreads.add(firstCarrier);
                    System.out.println("任务 " + taskId + " 第1次 - 载体: " + firstCarrier);
                    
                    try {
                        Thread.sleep(1000);  // 阻塞，释放载体线程
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    // sleep 后可能被调度到不同的载体线程
                    String secondCarrier = getCarrierThreadName(virtualThread);
                    carrierThreads.add(secondCarrier);
                    boolean switched = !firstCarrier.equals(secondCarrier);
                    
                    System.out.println("任务 " + taskId + " 第2次 - 载体: " + secondCarrier + 
                            (switched ? " ⚡ [已切换]" : " [未切换]"));
                    
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    String thirdCarrier = getCarrierThreadName(virtualThread);
                    carrierThreads.add(thirdCarrier);
                    
                    System.out.println("任务 " + taskId + " 第3次 - 载体: " + thirdCarrier);
                });
            }
            
            Thread.sleep(4000);
            
            System.out.println("\n=== 统计信息 ===");
            System.out.println("总任务数: " + taskCount);
            System.out.println("使用的不同载体线程数: " + carrierThreads.size());
            System.out.println("载体线程列表:");
            carrierThreads.stream().sorted().forEach(ct -> 
                System.out.println("  - " + ct));
        }
        
        System.out.println("\n=== 所有任务完成 ===");
    }
    
    /**
     * 获取当前载体线程的名称
     * 虚拟线程的 toString() 方法会包含载体线程信息
     */
    private static String getCarrierThreadName(Thread virtualThread) {
        // 虚拟线程的 toString() 格式: VirtualThread[#123]/runnable@.../ForkJoinPool-1-worker-1
        // 最后部分就是载体线程名称
        String threadInfo = virtualThread.toString();
        
        // 从 toString 中提取载体线程信息
        int lastSlash = threadInfo.lastIndexOf('/');
        if (lastSlash > 0) {
            String carrierInfo = threadInfo.substring(lastSlash + 1);
            return carrierInfo;
        }
        
        return threadInfo;
    }
}
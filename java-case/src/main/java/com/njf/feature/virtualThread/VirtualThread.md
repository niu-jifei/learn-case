### 虚拟线程

Java 19+ 引入虚拟线程（Virtual Threads）
虚拟线程 = Java 的协程实现

#### 虚拟线程 vs 平台线程

| 特性 | 平台线程 | 虚拟线程 |
|------|----------|----------|
| 创建成本 | 高（约 1-2MB 栈内存） | 极低（约几 KB） |
| 数量限制 | 受系统限制（几千个） | 几乎无限制（百万级） |
| 阻塞行为 | 阻塞底层 OS 线程 | 不阻塞 OS 线程 |
| 调度方式 | OS 级调度 | JVM 级调度 |
| 适用场景 | CPU 密集型 | I/O 密集型 |

```java
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
```


### 虚拟线程的阻塞行为，是非阻塞的

```java
import java.util.concurrent.Executors;

public class VirtualThreadBlocking {
    public static void main(String[] args) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 创建 10 万个虚拟线程
            for (int i = 0; i < 100_000; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(1000);  // 虚拟线程挂起，不占用 OS 线程
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}

// ✅ 虚拟线程：10 万个任务，只需少量 OS 线程
// ❌ 平台线程：10 万个任务会耗尽系统资源
```

### Java 协程特性

- Java 19+ 引入虚拟线程
- 功能类似 Python 协程
- 无需修改现有同步代码
- 适合 I/O 密集型任务


### 虚拟线程的执行模型

- 核心概念：载体线程（Carrier Thread）
- 虚拟线程不是在当前线程内执行，而是由载体线程调度执行


```text
┌─────────────────────────────────────────┐
│           JVM 事件循环                   │
├─────────────────────────────────────────┤
│  载体线程池（Carrier Thread Pool）      │
│  ┌──────┐ ┌──────┐ ┌──────┐            │
│  │ OS   │ │ OS   │ │ OS   │            │
│  │ 线程1│ │ 线程2│ │ 线程3│            │
│  └──┬───┘ └──┬───┘ └──┬───┘            │
└─────┼────────┼────────┼────────────────┘
      │        │        │
      ▼        ▼        ▼
   ┌────┐   ┌────┐   ┌────┐
   │V-T1│   │V-T2│   │V-T3│  虚拟线程
   └────┘   └────┘   └────┘
```

### 执行机制

```java
import java.util.concurrent.Executors;

public class VirtualThreadExecution {
    public static void main(String[] args) {
        // 创建虚拟线程执行器
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 提交多个虚拟线程
            for (int i = 0; i < 10; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("虚拟线程 " + taskId + " 开始执行");
                    try {
                        Thread.sleep(1000);  // 阻塞操作
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("虚拟线程 " + taskId + " 结束");
                });
            }
        }
    }
}
```

### 多个虚拟线程如何执行？
#### 1. 载体线程调度

```java
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualThreadScheduling {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("载体线程数: " + Runtime.getRuntime().availableProcessors());
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 创建 100 个虚拟线程
            for (int i = 0; i < 100; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("任务 " + taskId + " 在线程 " + 
                        Thread.currentThread().getName() + " 执行");
                    
                    try {
                        Thread.sleep(100);  // 模拟 I/O 操作
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    System.out.println("任务 " + taskId + " 完成");
                });
            }
            
            Thread.sleep(5000);  // 等待所有任务完成
        }
    }
}
```

#### 执行特点
- 少量载体线程（默认 = CPU 核心数）
- 大量虚拟线程（可以创建百万级）
- 按需调度：虚拟线程只在需要 CPU 时才占用载体线程

### Java 虚拟线程执行机制

#### 1. 不是在当前线程内执行
- 虚拟线程由载体线程池调度
- 默认载体线程数 = CPU 核心数

#### 2. 可以"同时"执行
- 通过时间片轮转实现并发
- 在 I/O 阻塞时自动挂起

#### 3. 不需要显式事件循环
- JVM 内部有调度器
- 自动处理阻塞操作

#### 4. 优势
- 无需改写现有同步代码
- 自动挂起/恢复机制
- 适合大规模 I/O 密集型任务

#### 5. 关键区别
- Python：显式事件循环 + async/await
- Java：隐式调度器 + 同步代码风格

两种方式都能实现高效并发，只是设计理念不同！

### 常见问题

**问题 1：载体线程专门用于虚拟线程吗？**
✅ 是的，载体线程专门用于执行虚拟线程，由 JVM 的 ForkJoinPool 管理

**问题 2：没有虚拟线程时还有载体线程吗？**
❌ 没有，载体线程只在创建虚拟线程时才会被创建

**问题 3：JVM 会自动调度虚拟线程吗？**
✅ 是的，JVM 会自动调度：

核心优势：
- 遇到阻塞操作时自动挂起虚拟线程
- 阻塞结束时自动恢复虚拟线程
- 在少量载体线程上高效调度大量虚拟线程

#### 核心优势

- 自动调度：无需手动管理
- 高效利用：少量载体线程处理大量虚拟线程
- 透明处理：阻塞操作自动转换为挂起/恢复

### JVM 虚拟线程调度的底层实现

核心技术：Continuation（续体）
Continuation 是虚拟线程实现的核心概念

#### 如何检测阻塞操作？
JVM 使用字节码插桩和拦截机制

#### 挂起和恢复的详细流程

##### 挂起流程（Park）

```java
// 详细的挂起流程
class VirtualThread {
    
    void park() {
        // 步骤1: 检查状态
        if (this.state != State.RUNNING) {
            throw new IllegalStateException("Not running");
        }
        
        // 步骤2: 保存执行上下文
        Continuation.Scope scope = Continuation.captureScope();
        this.savedScope = scope;
        
        // 步骤3: 切换状态
        this.state = State.PARKED;
        
        // 步骤4: 释放载体线程
        CarrierThread currentCarrier = this.carrier;
        this.carrier = null;
        
        // 步骤5: 让出载体线程的控制权
        Continuation.yield(scope);
        
        // 步骤6: 载体线程可以执行其他虚拟线程了
        currentCarrier.scheduleNextVirtualThread();
    }
}
```

##### 恢复流程（Unpark）

```java
// 详细的恢复流程
class VirtualThread {
    
    void unpark() {
        // 步骤1: 检查状态
        if (this.state != State.PARKED) {
            return;  // 已经在运行或未挂起
        }
        
        // 步骤2: 切换状态
        this.state = State.RUNNABLE;
        
        // 步骤3: 提交到调度器
        Scheduler.schedule(() -> {
            // 步骤4: 获取可用的载体线程
            CarrierThread carrier = Scheduler.acquireCarrier();
            
            // 步骤5: 在载体线程上恢复执行
            carrier.execute(() -> {
                this.state = State.RUNNING;
                this.carrier = carrier;
                
                // 步骤6: 恢复保存的执行上下文
                Continuation.run(this.savedScope);
            });
        });
    }
}
```

##### 完整的调度流程示例

```java
import java.util.concurrent.Executors;

public class CompleteSchedulingFlow {
    public static void main(String[] args) {
        Thread virtualThread = Thread.ofVirtual().start(() -> {
            System.out.println("1. 虚拟线程开始执行");
            System.out.println("   载体线程: " + Thread.currentThread().getName());
            
            try {
                System.out.println("2. 准备执行 Thread.sleep(2000)");
                
                // JVM 内部流程：
                // a) 检测到 Thread.sleep() 调用
                // b) 创建定时器任务：2秒后调用 unpark()
                // c) 调用 park() 挂起虚拟线程
                // d) 保存执行栈状态
                // e) 释放载体线程
                Thread.sleep(2000);
                
                // 2秒后：
                // a) 定时器触发，调用 unpark()
                // b) 虚拟线程状态变为 RUNNABLE
                // c) 重新调度到载体线程
                // d) 恢复执行栈状态
                // e) 继续执行下面的代码
                
                System.out.println("3. 虚拟线程恢复执行");
                System.out.println("   载体线程: " + Thread.currentThread().getName());
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("4. 虚拟线程执行完成");
        });
        
        try {
            virtualThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```
### 总结

JVM 调度机制的核心要点：
- Continuation 技术：保存和恢复执行栈状态
- 字节码插桩：在编译时或运行时修改字节码
- 方法拦截：拦截阻塞调用，转换为挂起操作
- 事件循环集成：与 NIO 事件循环集成处理 I/O
- 自动调度：无需显式 await，JVM 自动处理

#### 关键区别

| 特性 | Python 协程 | Java 虚拟线程 |
|------|-------------|--------------|
| 挂起方式 | 显式 await | 隐式自动检测 |
| 状态保存 | 生成器对象 | Continuation |
| 事件循环 | 显式 asyncio.run() | JVM 内部调度器 |
| 代码风格 | 异步语法 | 同步语法 |

JVM 的虚拟线程实现通过底层技术实现了"看起来像同步，实际上是异步"的效果，这就是它的强大之处！
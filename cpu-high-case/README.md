
# CPU飙高案例排查指南

## 项目说明
这是一个专门为学习CPU问题排查而设计的Java Spring Boot应用。它模拟了多种常见的CPU飙高场景。

## 快速开始

### 1. 编译运行

#### 克隆项目
```bash
git clone <repository>
cd cpu-high-demo
```

#### 编译打包
```bash
mvn clean package
```

#### 运行应用
```bash
java -jar target/cpu-high-demo-1.0.0.jar
```

或者使用 Docker
```bash
docker-compose up -d
```

### 2. 访问应用

- 应用主页: http://localhost:8080/health
- 查看应用状态和可用接口

## 模拟的问题场景

### 场景1：死循环

触发死循环
```bash
curl "http://localhost:8080/api/deadloop?infinite=true"
```

触发有bug的条件循环
```bash
curl "http://localhost:8080/api/deadloop?infinite=false"
```

**现象**：单个线程CPU使用率接近100%

**排查步骤**：
1. `top` 找到高CPU的Java进程
2. `top -Hp <PID>` 查看进程内线程
3. `jstack <PID>` 分析线程堆栈
4. 查找 `RUNNABLE` 状态的线程

### 场景2：GC问题

创建大量临时对象
```bash
curl "http://localhost:8080/api/gc?count=10000"
```

创建内存泄漏
```bash
curl "http://localhost:8080/api/memory-leak"
```

**现象**：CPU使用率周期性飙高，伴随内存增长

**排查步骤**：
1. `jstat -gc <PID> 1000` 观察GC情况
2. `jmap -histo:live <PID>` 查看对象分布
3. 分析GC日志

### 场景3：锁竞争

启动10个线程竞争锁
```bash
curl "http://localhost:8080/api/lock?threadCount=10"
```

**现象**：多个线程CPU使用率高，有大量BLOCKED线程

**排查步骤**：
1. `jstack <PID>` 查看线程状态
2. 查找 `BLOCKED` 状态的线程
3. 分析锁持有者

### 场景4：无限递归

触发深度递归计算
```bash
curl "http://localhost:8080/api/recursive?depth=40"
```

**现象**：CPU持续高，可能栈溢出

## 排查工具速查

### 基本命令

1. 找到Java进程
   ```bash
   jps -l
   ps aux | grep java
   ```

2. 查看CPU使用
   ```bash
   top
   top -Hp <PID>
   ```

3. 线程分析
   ```bash
   jstack <PID> > thread_dump.log
   ```

4. GC分析
   ```bash
   jstat -gcutil <PID> 1000 10
   ```

5. 内存分析
   ```bash
   jmap -histo <PID> | head -20
   ```

### 一键诊断脚本

```bash
#!/bin/bash

PID=$1
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
DIR="diagnosis_${TIMESTAMP}"
mkdir -p $DIR

echo "=== 收集诊断信息 ==="
top -b -n 1 > $DIR/top.log
jstack $PID > $DIR/jstack.log
jstat -gcutil $PID 1000 5 > $DIR/gc.log
vmstat 1 5 > $DIR/vmstat.log

echo "诊断信息已保存到: $DIR"
```

## 预期排查结果

### 死循环问题
- 在jstack中会看到线程处于RUNNABLE状态
- 堆栈显示在某个方法的while/for循环中
- 线程名可能为"Thread-0"或类似

### GC问题
- jstat显示频繁的Full GC
- GC时间占比高
- 老年代使用率持续高位

### 锁竞争
- 多个线程BLOCKED在同一个锁上
- 锁持有者线程可能在做耗时操作
- 可以通过`jstack -l`查看锁信息

## 学习建议

1. 每次只触发一个问题场景
2. 使用工具观察系统变化
3. 尝试多种排查命令
4. 对比不同问题的特征差异
5. 尝试优化代码并验证效果

## 构建和运行脚本

### run-demo.sh

```bash
#!/bin/bash
# run-demo.sh

# 编译打包
echo "编译项目..."
mvn clean package

if [ $? -eq 0 ]; then
    echo "编译成功，启动应用..."

    # 设置JVM参数，便于观察GC
    export JAVA_OPTS="-Xmx512m -Xms256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./logs/gc.log"
    
    # 创建日志目录
    mkdir -p logs
    
    # 启动应用
    java $JAVA_OPTS -jar target/cpu-high-demo-1.0.0.jar
else
    echo "编译失败"
    exit 1
fi
```

### 使用方法

#### 编译和启动应用
```bash
chmod +x run-demo.sh
./run-demo.sh
```

#### 触发问题
在另一个终端执行

```bash
# 场景1：死循环
curl "http://localhost:8080/api/deadloop?infinite=true"

# 场景2：GC问题
curl "http://localhost:8080/api/gc?count=5000"

# 场景3：锁竞争
curl "http://localhost:8080/api/lock?threadCount=20"

# 场景4：递归问题
curl "http://localhost:8080/api/recursive?depth=45"
```

#### 使用排查命令
```bash
# 1. 查找Java进程
jps -l

# 2. 查看CPU使用
top

# 3. 分析Java线程
top -Hp <PID>
printf "%x\n" <高CPU线程ID>
jstack <PID> | grep -A 10 -B 5 <十六进制线程ID>

# 4. 查看GC情况
jstat -gc <PID> 1000
```

## 学习目标

通过这个案例，你可以：

- **实践排查流程**：按照"进程→线程→堆栈"的流程排查
- **识别问题特征**：区分死循环、GC、锁竞争等不同问题
- **使用工具链**：熟悉top、jstack、jstat等工具
- **理解根本原因**：通过代码理解问题产生的根源
- **优化代码**：尝试修复代码中的问题

这个案例设计得非常安全，所有问题都有停止方法（/api/stop），不会真的导致系统崩溃。你可以放心地在自己的机器上运行和实验。

祝你排查愉快！在实际操作中，你会更深刻地理解各种CPU问题的特征和排查方法。
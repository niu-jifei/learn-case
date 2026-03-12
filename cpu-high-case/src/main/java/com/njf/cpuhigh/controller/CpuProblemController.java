package com.njf.cpuhigh.controller;

import com.njf.cpuhigh.service.*;
import com.njf.cpuhigh.service.DeadLoopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "CPU问题模拟", description = "模拟各种CPU飙高场景的API接口")
public class CpuProblemController {

    @Autowired
    private DeadLoopService deadLoopService;

    @Autowired
    private GcProblemService gcProblemService;

    @Autowired
    private LockContentionService lockContentionService;

    @Autowired
    private RecursiveService recursiveService;

    @Operation(summary = "触发死循环", description = "模拟死循环问题，CPU使用率会立即飙高到100%")
    @Parameter(name = "infinite", description = "是否无限循环，true为无限循环，false为有条件的死循环")
    @GetMapping("/deadloop")
    public String triggerDeadLoop(@RequestParam(defaultValue = "false") boolean infinite) {
        return deadLoopService.startDeadLoop(infinite);
    }

    @Operation(summary = "触发GC问题", description = "创建大量临时对象，导致频繁GC，CPU使用率周期性飙高")
    @Parameter(name = "count", description = "创建垃圾对象的数量，默认1000")
    @GetMapping("/gc")
    public String triggerGcProblem(@RequestParam(defaultValue = "1000") int count) {
        return gcProblemService.createGarbage(count);
    }

    @Operation(summary = "触发锁竞争", description = "启动多个线程竞争同一把锁，产生大量BLOCKED线程，CPU使用率高")
    @Parameter(name = "threadCount", description = "竞争锁的线程数量，默认10")
    @GetMapping("/lock")
    public String triggerLockContention(@RequestParam(defaultValue = "10") int threadCount) {
        return lockContentionService.startLockContention(threadCount);
    }

    @Operation(summary = "触发无限递归", description = "模拟深度递归问题，可能导致栈溢出和CPU持续高")
    @Parameter(name = "depth", description = "递归深度，默认0")
    @GetMapping("/recursive")
    public String triggerRecursive(@RequestParam(defaultValue = "0") int depth) {
        return recursiveService.deepRecursive(depth);
    }

    @Operation(summary = "触发内存泄漏", description = "模拟内存泄漏问题，内存逐渐增加导致频繁GC和CPU飙高")
    @GetMapping("/memory-leak")
    public String triggerMemoryLeak() {
        return gcProblemService.createMemoryLeak();
    }

    @Operation(summary = "停止所有问题", description = "停止所有正在运行的CPU问题线程，恢复系统正常状态")
    @GetMapping("/stop")
    public String stopAllProblems() {
        deadLoopService.stopDeadLoop();
        lockContentionService.stopContention();
        return "已尝试停止所有问题线程";
    }
}
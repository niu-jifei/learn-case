package com.njf.cpuhigh.controller;

import com.njf.cpuhigh.service.*;
import com.njf.cpuhigh.service.DeadLoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CpuProblemController {

    @Autowired
    private DeadLoopService deadLoopService;

    @Autowired
    private GcProblemService gcProblemService;

    @Autowired
    private LockContentionService lockContentionService;

    @Autowired
    private RecursiveService recursiveService;

    /**
     * 触发死循环 - CPU立即飙高
     */
    @GetMapping("/deadloop")
    public String triggerDeadLoop(@RequestParam(defaultValue = "false") boolean infinite) {
        return deadLoopService.startDeadLoop(infinite);
    }

    /**
     * 触发GC问题 - 产生大量垃圾对象
     */
    @GetMapping("/gc")
    public String triggerGcProblem(@RequestParam(defaultValue = "1000") int count) {
        return gcProblemService.createGarbage(count);
    }

    /**
     * 触发锁竞争 - 多线程竞争同一把锁
     */
    @GetMapping("/lock")
    public String triggerLockContention(@RequestParam(defaultValue = "10") int threadCount) {
        return lockContentionService.startLockContention(threadCount);
    }

    /**
     * 触发无限递归 - 栈溢出和CPU高
     */
    @GetMapping("/recursive")
    public String triggerRecursive(@RequestParam(defaultValue = "0") int depth) {
        return recursiveService.deepRecursive(depth);
    }

    /**
     * 触发内存泄漏 - 内存逐渐增加导致频繁GC
     */
    @GetMapping("/memory-leak")
    public String triggerMemoryLeak() {
        return gcProblemService.createMemoryLeak();
    }

    /**
     * 停止所有问题线程
     */
    @GetMapping("/stop")
    public String stopAllProblems() {
        deadLoopService.stopDeadLoop();
        lockContentionService.stopContention();
        return "已尝试停止所有问题线程";
    }
}
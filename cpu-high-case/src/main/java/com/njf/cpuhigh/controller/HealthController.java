package com.njf.cpuhigh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.management.ManagementFactory;

@RestController
@Tag(name = "健康检查", description = "应用健康状态和系统信息查询")
public class HealthController {

    @Operation(summary = "健康检查", description = "获取应用运行状态、内存使用情况和可用接口列表")
    @GetMapping("/health")
    public String health() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;

        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;

        return String.format("""
            ====== 应用状态 ======
            JVM: %s
            运行时间: %d秒
            内存使用: %dMB / %dMB
            线程数: %d
            =====================
            可用接口:
            - GET /health (本接口)
            - GET /api/deadloop?infinite=true (死循环)
            - GET /api/gc?count=10000 (GC问题)
            - GET /api/lock?threadCount=20 (锁竞争)
            - GET /api/recursive?depth=10000 (递归)
            - GET /api/memory-leak (内存泄漏)
            - GET /api/stop (停止所有)
            """,
                jvmName, uptime, usedMemory, totalMemory,
                Thread.activeCount()
        );
    }
}
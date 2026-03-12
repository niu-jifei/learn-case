package com.njf.cpuhigh;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CpuHighDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CpuHighDemoApplication.class, args);
        System.out.println("===================================================");
        System.out.println("CPU高负载测试应用启动成功！");
        System.out.println("请访问 http://localhost:8080/health 查看应用状态");
        System.out.println("请访问 http://localhost:8080/swagger-ui.html 查看 Swagger UI");
        System.out.println("===================================================");
    }
}

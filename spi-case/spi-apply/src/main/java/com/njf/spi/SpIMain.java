package com.njf.spi;

import java.util.ServiceLoader;

/**
 * @author niujifei
 * @create 2026/4/29
 * @desc
 **/
public class SpIMain {
    public static void main(String[] args) {
        System.out.println("=== SPI 功能测试演示 ===\n");

        // 使用SPI机制加载数据库驱动
        ServiceLoader<DatabaseDriver> drivers = ServiceLoader.load(DatabaseDriver.class);
        System.out.println("发现的所有数据库驱动实现:");
        for (DatabaseDriver driver : drivers) {
            System.out.println("- " + driver.getDriverName());
        }

        System.out.println("\n=== 测试各个驱动实现 ===\n");

        ServiceLoader<DatabaseDriver> driverLoader = ServiceLoader.load(DatabaseDriver.class);
        for (DatabaseDriver driver : driverLoader) {
            System.out.println("测试驱动: " + driver.getDriverName());
            driver.connect("jdbc:example://localhost:3306/testdb");
            driver.executeQuery("SELECT * FROM users");
            driver.disconnect();
            System.out.println();
        }
    }
}

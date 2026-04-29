package com.njf.spi.driver;

import com.njf.spi.DatabaseDriver;

/**
 * @author niujifei
 * @create 2026/4/29
 * @desc
 **/
public class PostgreSQLDriver implements DatabaseDriver {
    @Override
    public void connect(String url) {
        System.out.println("PostgreSQL连接到: " + url);
    }

    @Override
    public void executeQuery(String sql) {
        System.out.println("PostgreSQL执行查询: " + sql);
    }

    @Override
    public void disconnect() {
        System.out.println("PostgreSQL断开连接");
    }

    @Override
    public String getDriverName() {
        return "PostgreSQL Driver";
    }
}

package com.njf.spi;

/**
 * @author niujifei
 * @create 2026/4/29
 * @desc
 **/
public interface DatabaseDriver extends Spi {
    void connect(String url);

    void executeQuery(String sql);

    void disconnect();

    String getDriverName();
}

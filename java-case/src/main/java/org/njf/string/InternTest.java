package org.njf.string;

import org.junit.Test;

public class InternTest {

    @Test
    public void test() {
        String s1 = new String("a");  // 常量池中已经有a了，只创建堆上对象
        s1.intern();  // 指向常量池
        String s2 = "a";
        System.out.println(s1 == s2); // false


        String s3 = new String("a") + new String("a");   // 常量池中没有aa， 创建堆对象
        s3.intern();  // 放入常量池，并返回引用
        String s4 = "aa";   // 返回是的堆内的引用
        System.out.println(s3 == s4);//true
    }

    @Test
    public void test1() {
        String s3 = new String("1") + new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4);
    }


    @Test
    public void test2() {
        String s3 = new String("1") + new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4);
    }

    @Test
    public void test3() {
        String s3 = new String("1") + new String("2");
        s3.intern();
        String s4 = "12";
        System.out.println(s3 == s4);
    }

    @Test
    public void test4() {
        String s3 = new String("2") + new String("1");
        s3.intern();
        String s4 = "21";
        System.out.println(s3 == s4);
    }

    @Test
    public void test5() {
        String s3 = new String("2") + new String("2");
        s3.intern();
        String s4 = "22";
        System.out.println(s3 == s4);
    }

    @Test
    public void test6() {
        // 方式1：字面量（直接入池）
        String s1 = "hello";  // 常量池中创建

        // 方式2：new 创建（堆中对象）
        String s2 = new String("hello");  // 堆中创建，常量池已有"hello"

        // 方式3：拼接创建
        String s3 = new String("he") + new String("llo");  // 堆中创建，常量池无"hello"
    }

    @Test
    public void test7() {
        // JDK 6
        String s = new String("a") + new String("b");
        s.intern();  // 在永久代创建"ab"的副本
        String s2 = "ab";
        System.out.println(s == s2);  // false：堆对象 vs 永久代副本

//        // JDK 7+
//        String s = new String("a") + new String("b");
//        s.intern();  // 将堆中s的引用存入常量池
//        String s2 = "ab";
//        System.out.println(s == s2);  // true：指向同一堆对象
    }

}

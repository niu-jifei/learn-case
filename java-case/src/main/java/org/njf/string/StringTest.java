package org.njf.string;

public class StringTest {
    public static void main(String[] args) {
        String s1 = "hello";
        String s2 = "world";
        String s3 = "hello" + "world";  // 编译期间会放在运行时常量池

        String s4 = s1 + s2;    // 会使用StringBuilder的append进行拼接，编译期间无法放在运行时常量池，可以使用intern方法将其放入运行时常量池

        String s5 = new String("hello") + new String("world");  // 会创建两个对象，s5和s5.intern()，s5.intern()会返回运行时常量池中的对象


        System.out.println(s3 == s4);   // false
        System.out.println(s3 == s4.intern());   // true
        System.out.println(s5 == s5.intern());   // false
    }
}

package org.njf.typeparam;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Test1 {

    /**
     * 可以将任何带有参数的类型传递给原始类型List，但却不能把List<String>赋值给List<Object>，因为会产生编译错误(不支持协变)
     * @param args
     */
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();

        test1(list);
    }

    public static void test1(List list) {
        list.add("1");
        list.add(2);
    }

    public static void test2(List<Object> list) {
        list.add(new Test1());
    }

    public static void test3(List<?> list) {
        // 编译出错
//        list.add("1");


        list.get(0);
    }




    @Test
    public void test() throws Exception {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Method method = list.getClass().getMethod("add", Object.class);
        method.invoke(list, "Java反射机制实例");
        System.out.println(list.get(0));
    }
}

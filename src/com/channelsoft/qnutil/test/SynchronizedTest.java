package com.channelsoft.qnutil.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * <dl>
 * <dd>Description:</dd>
 * <dd>Copyright: Copyright (C) 2006</dd>
 * <dd>Company: 青牛（北京）技术有限公司</dd>
 * <dd>CreateDate: 2015/8/18</dd>
 * </dl>
 *
 * @author 安宁
 */
public class SynchronizedTest extends Thread{
    private String testName;

    public SynchronizedTest( String testName) {
        this.testName = testName;
    }

    public static void main(String args[]){
        SynchronizedTest test1 = new SynchronizedTest("肖迪");
        SynchronizedTest test2 = new SynchronizedTest("东强");
        test1.start();
        test2.start();
    }

    @Override
    public void run() {
        try {
            Test test = new Test(this.testName);
            Method m = Test.class.getMethod("test");
            m.invoke(test);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
class Test {
    private String testName;

    public Test(String name) {
        this.testName = name;
    }

    public synchronized void test(){
        System.out.println(this.testName+"进入测试方法");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.testName + "走出测试方法");
    }
}

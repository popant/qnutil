package com.channelsoft.qnutil.util;

import java.math.BigDecimal;

public class Arith {
    /**
     * 提供精确加法计算的add方法
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1,double value2){
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.add(b2).doubleValue();
    }
     
    /**
     * 提供精确减法运算的sub方法
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(double value1,double value2){
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.subtract(b2).doubleValue();
    }
     
    /**
     * 提供精确乘法运算的mul方法
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double mul(double value1,double value2){
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.multiply(b2).doubleValue();
    }
     
    /**
     * 提供精确的除法运算方法div
     * @param value1 被除数
     * @param value2 除数
     * @param mode 模式
     * @return 两个参数的商
     */
    public static double div(double value1,double value2,int mode){
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.divide(b2, 2, mode).doubleValue();
    }

    public static double round(double v, int scale) {
        if (scale < 0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static Integer round(double v) {
        int scale = 0;
        if (scale < 0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).intValue();
    }
    public static void main(String args[]){
        double a = 4.515;
        Double round = Arith.round(a, 0);
        System.out.println(round.intValue());
    }

}
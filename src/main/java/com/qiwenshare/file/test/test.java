package com.qiwenshare.file.test;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        System.out.println(JSON.toJSONString(list));
    }
}

package com.qiwenshare.common.util;

/**
 * @author dehui dou
 * @date 2020/10/21 17:24
 * @description
 */
public class IDUtils {
    private static final SequenceWorker sequenceWorker = new SequenceWorker();
    public static Long nextId(){
        return sequenceWorker.nextId();
    }
}

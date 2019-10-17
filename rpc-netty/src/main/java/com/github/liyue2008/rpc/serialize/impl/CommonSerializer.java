package com.github.liyue2008.rpc.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.github.liyue2008.rpc.serialize.Serializer;

import java.nio.charset.StandardCharsets;

/**
 * @author:wangyi
 * @Date:2019/10/17 使用json进行序列化
 */
public class CommonSerializer implements Serializer {

    @Override
    public int size(Object entry) {
        String str = JSON.toJSONString(entry);
        return str.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public void serialize(Object entry, byte[] bytes, int offset, int length) {
        String str = JSON.toJSONString(entry);
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(strBytes, 0, bytes, offset, strBytes.length);
    }

    @Override
    public Object parse(byte[] bytes, int offset, int length) {
        byte[] objBytes = new byte[length];
        System.arraycopy(bytes, offset, objBytes, 0, length);
        String jsonStr = new String(objBytes, StandardCharsets.UTF_8);
        return JSON.parse(jsonStr);
    }

    @Override
    public byte type() {
        return Types.TYPE_JSON;
    }

    @Override
    public Class getSerializeClass() {
        return Object.class;
    }
}

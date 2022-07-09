package com.example.netdisk.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 该类用于批量操作数据
 * 将多行数据中的某一列设定为同一个值
 * @author monody
 * @date 2022/4/29 5:31 下午
 */
@Data
public class BatchOperation {
    private String username;
    /**
     * 将要被设定的列
     */
    private String setColumn;
    /**
     * 要被设定的列值
     */
    private Object value;
    /**
     * 查询条件列
     */
    private String whereColumn;
    /**
     * 批量操作的查询条件
     */
    private List list;


    /**
     * 路径
     */
    private String path;

    /**
     * 排序字段
     */
    private String field;

    /**
     * 排序方式
     * asc 升序
     * desc 降序
     */
    private String order;

    public BatchOperation() {
    }

    public BatchOperation(String username, List list) {
        this.username = username;
        this.list = list;
    }

    public BatchOperation(String username, String path, String field, String order) {
        this.username = username;
        this.path = path;
        this.field = field;
        this.order = order;
    }

    public BatchOperation(String username, String setColumn, Object value, String whereColumn, List list) {
        this.username = username;
        this.setColumn = setColumn;
        this.value = value;
        this.whereColumn = whereColumn;
        this.list = list;
    }
}

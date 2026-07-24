package com.lanhai.lanaicodemother.common;

import lombok.Data;

/**
 * 通用分页与排序请求基类，提供页码、页大小、排序字段和排序方向。
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}

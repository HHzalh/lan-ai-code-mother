package com.lanhai.lanaicodemother.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求，携带待删除业务记录的标识。
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
}

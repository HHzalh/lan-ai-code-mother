package com.lanhai.lanaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 应用部署请求，携带需要发布的应用标识。
 */
@Data
public class AppDeployRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 应用 id
     */
    private Long appId;
}

package com.lanhai.lanaicodemother.utils;

import com.lanhai.lanaicodemother.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 静态资源检查工具类
 * 用于检查应用生成的静态资源是否可访问
 *
 * @author <a href="https://gitee.com/hhzalh">致爱蓝海</a>
 */
@Slf4j
public class StaticResourceCheckerUtils {

    /**
     * 默认超时时间（秒）
     */
    private static final int DEFAULT_TIMEOUT_SECONDS = 5;

    /**
     * HTTP客户端（复用，提高性能）
     */
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
            .build();

    /**
     * 检查静态资源是否存在
     *
     * @param baseUrl     基础URL（如：http://localhost:8123/api）
     * @param codeGenType 代码生成类型
     * @param appId       应用ID
     * @return 如果静态资源存在且可访问返回true，否则返回false
     */
    public static boolean checkStaticResourceExists(String baseUrl, String codeGenType, Long appId) {
        if (baseUrl == null || codeGenType == null || appId == null || appId <= 0) {
            log.warn("参数无效：baseUrl={}, codeGenType={}, appId={}", baseUrl, codeGenType, appId);
            return false;
        }

        try {
            // 构建静态资源URL
            String staticUrl = buildStaticResourceUrl(baseUrl, codeGenType, appId);
            log.debug("检查静态资源URL: {}", staticUrl);

            // 创建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(staticUrl))
                    .GET()
                    .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                    .build();

            // 发送请求
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            // 状态码200表示资源存在
            boolean exists = statusCode == 200;
            log.info("静态资源检查结果: URL={}, statusCode={}, exists={}", staticUrl, statusCode, exists);
            return exists;
        } catch (Exception e) {
            log.warn("检查静态资源失败: codeGenType={}, appId={}, error={}", codeGenType, appId, e.getMessage());
            return false;
        }
    }

    /**
     * 构建静态资源URL
     *
     * @param baseUrl     基础URL
     * @param codeGenType 代码生成类型
     * @param appId       应用ID
     * @return 完整的静态资源URL
     */
    private static String buildStaticResourceUrl(String baseUrl, String codeGenType, Long appId) {
        // 确保baseUrl不以斜杠结尾
        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        // 构建基础路径：/static/{codeGenType}_{appId}/
        String basePath = String.format("%s/static/%s_%d/", normalizedBaseUrl, codeGenType, appId);

        // 根据代码生成类型确定具体文件路径
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == null) {
            // 未知类型，默认返回index.html
            return basePath + "index.html";
        }

        return switch (codeGenTypeEnum) {
            case VUE_PROJECT -> basePath + "dist/index.html";
            case HTML, MULTI_FILE -> basePath + "index.html";
        };
    }
}


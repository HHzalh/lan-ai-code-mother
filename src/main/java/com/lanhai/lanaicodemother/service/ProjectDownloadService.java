package com.lanhai.lanaicodemother.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ProjectDownloadService {

    /**
     * 下载项目为压缩包
     *
     * @param projectPath      需要打包的项目文件的路径
     * @param downloadFileName 下载时显示的文件名
     * @param response         HTTP响应对象，用于将ZIP文件写入响应流中
     */
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}

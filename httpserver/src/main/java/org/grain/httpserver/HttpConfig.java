package org.grain.httpserver;

import org.grain.log.ILog;

public class HttpConfig {
	public static String ENCODE = "UTF-8";
	public static String CONTENT_TYPE_JSON = "application/json";
	public static String PROJECT_PATH;
	public static String PROJECT_NAME;
	public static Class<IUploadProgress> UPLOAD_PROGRESS_CLASS;
	/**
	 * 下载块大小，默认大小（64kb）65536经测试，超过这个值传输速度完全依赖线程睡眠时间，低于这个值会影响传输速度
	 */
	public static int DOWNLOAD_BLOCK_SIZE = 65536;
	/**
	 * 下载文件睡眠间隔，小于等于0说明不睡眠
	 */
	public static int DOWNLOAD_FILE_SLEEP_TIME = 256;
	/**
	 * 下载图片睡眠间隔，小于等于0说明不睡眠
	 */
	public static int DOWNLOAD_IMAGE_SLEEP_TIME = 64;
	public static ILog log;
}

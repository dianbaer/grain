package org.grain.httpserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

public class FileHttpFilter implements IHttpFilter {
	private String tempPath;
	private DiskFileItemFactory diskFileItemFactory;

	/**
	 * 初始化缓存文件夹
	 */
	public FileHttpFilter() {
		tempPath = HttpConfig.PROJECT_PATH + "/" + HttpConfig.PROJECT_NAME + "_TEMP";
		if (HttpConfig.log != null) {
			HttpConfig.log.info("缓存文件路径为：" + tempPath);
		}
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setSizeThreshold(1024);
		diskFileItemFactory.setRepository(file);
	}

	/**
	 * 解析from表单类型的
	 */
	@Override
	public boolean httpFilter(HttpPacket httpPacket) {
		// 是表单类型的
		if (ServletFileUpload.isMultipartContent(httpPacket.hSession.request)) {
			// 设置已经用表单方式解析，不需要后续进行json解析
			httpPacket.isFromAnalysis = true;
			IUploadProgress uploadProgress = null;
			try {
				// 进度类
				if (HttpConfig.UPLOAD_PROGRESS_CLASS != null) {
					uploadProgress = HttpConfig.UPLOAD_PROGRESS_CLASS.newInstance();
					uploadProgress.init(httpPacket.hSession.headParam.fileUuid);
				}
				ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
				servletFileUpload.setSizeMax(1024 * 1024 * 1024 * 1024L);
				ServletRequestContext servletRequestContext = new ServletRequestContext(httpPacket.hSession.request);
				if (uploadProgress != null) {
					servletFileUpload.setProgressListener(uploadProgress);
				}
				// 解析
				List<FileItem> list = servletFileUpload.parseRequest(servletRequestContext);
				if (list == null || list.size() == 0) {
					if (HttpConfig.log != null) {
						HttpConfig.log.warn("解析的文件个数为0，未解析出文件，请查问题");
					}
					return false;
				}
				List<FileData> fileList = new ArrayList<FileData>();
				for (FileItem fileItem : list) {
					// 文件
					if (!fileItem.isFormField()) {
						String name = fileItem.getName();
						int postfixIndex = name.lastIndexOf(".");
						String newName;
						if (postfixIndex != -1) {
							String postfix = name.substring(postfixIndex);
							newName = UUID.randomUUID().toString().trim().replaceAll("-", "") + postfix;
						} else {
							newName = UUID.randomUUID().toString().trim().replaceAll("-", "");
						}
						// 保存上传文件
						File file = new File(tempPath, newName);
						fileItem.write(file);
						FileData fileData = new FileData(file, name);
						fileList.add(fileData);
					} else {
						// 参数
						String name = fileItem.getFieldName();
						String value = fileItem.getString();
						// 找关键字
						if (value != null && !value.equals("")) {
							if (name.equals(AllowParam.HOPCODE)) {
								httpPacket.hSession.headParam.hOpCode = value;
							} else if (name.equals(AllowParam.TOKEN)) {
								httpPacket.hSession.headParam.token = value;
							} else if (name.equals(AllowParam.FILE_UUID)) {
								httpPacket.hSession.headParam.fileUuid = value;
							} else if (name.equals(AllowParam.PACKET)) {
								httpPacket.hSession.headParam.packet = value;
							}
							httpPacket.hSession.headParam.parameterParam.put(name, value);
						}
					}
				}
				// 上传完成
				httpPacket.fileList = fileList;
				if (uploadProgress != null) {
					uploadProgress.finish();
				}
				httpPacket.putMonitor("读取文件流完成");
				return true;
			} catch (Exception e) {
				if (uploadProgress != null) {
					uploadProgress.fail();
				}
				if (HttpConfig.log != null) {
					HttpConfig.log.error("上传文件异常", e);
				}
				httpPacket.putMonitor("读取文件流失败");
				return false;
			}
		}
		return true;
	}
}

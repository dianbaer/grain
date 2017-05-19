package http.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import http.AllowParam;
import http.HSession;
import http.HttpConfig;
import log.LogManager;
import util.IdUtil;

public class FileHttpFilter implements IHttpFilter {
	private String tempPath;
	private DiskFileItemFactory diskFileItemFactory;

	public FileHttpFilter() {
		tempPath = HttpConfig.PROJECT_PATH + "/" + HttpConfig.UPLOAD_TEMP_FOLDER;
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		diskFileItemFactory = new DiskFileItemFactory();

		diskFileItemFactory.setSizeThreshold(1024);

		diskFileItemFactory.setRepository(file);
	}

	@Override
	public boolean httpFilter(HSession hSession) {
		if (hSession.headParam.sendType.equals(AllowParam.SEND_TYPE_FILE_SAVE_SESSION)) {
			if (ServletFileUpload.isMultipartContent(hSession.request)) {
				IUploadProgress uploadProgress = null;
				try {
					uploadProgress = (IUploadProgress) Class.forName(HttpConfig.UPLOAD_PROGRESS_CLASS).newInstance();
					uploadProgress.init(hSession.headParam.fileUuid);
					ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

					servletFileUpload.setSizeMax(1024 * 1024 * 1024 * 1024L);
					ServletRequestContext servletRequestContext = new ServletRequestContext(hSession.request);
					servletFileUpload.setProgressListener(uploadProgress);
					List<FileItem> list = servletFileUpload.parseRequest(servletRequestContext);
					if (list == null || list.size() == 0) {
						LogManager.httpLog.warn("解析的文件个数为0，未解析出文件，请查问题");
						return false;
					}
					List<FileData> fileList = new ArrayList<FileData>();
					for (FileItem fileItem : list) {
						if (!fileItem.isFormField()) {
							String name = fileItem.getName();
							int postfixIndex = name.lastIndexOf(".");
							String newName;
							if(postfixIndex != -1){
								String postfix = name.substring(postfixIndex);
								newName = IdUtil.getUuid() + postfix;
							}else{
								newName = IdUtil.getUuid();
							}
							File file = new File(tempPath, newName);
							fileItem.write(file);
							FileData fileData = new FileData(file, name);
							fileList.add(fileData);
						}
						fileItem.delete();
					}
					hSession.fileList = fileList;
					uploadProgress.finish();
					hSession.putMonitor("读取文件流完成");
					return true;
				} catch (Exception e) {
					if (uploadProgress != null) {
						uploadProgress.fail();
					}
					LogManager.httpLog.error("上传文件异常", e);
					return false;
				}

			} else {
				LogManager.httpLog.warn("发送类型是：" + hSession.headParam.sendType + "，不包含文件，请及时解决");
				return false;
			}
		}
		return true;
	}

}

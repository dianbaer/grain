package http.filter;

import log.LogManager;

public class UploadProgressListener implements IUploadProgress {
	public UploadProgressListener() {

	}

	@Override
	public void update(long arg0, long arg1, int arg2) {
		// LogManager.httpLog.info("arg0:" + arg0 + ",arg1:" + arg1 + ",arg2" +
		// arg2);
	}

	@Override
	public void init(String uuid) {

		LogManager.httpLog.info("上传文件开始");
	}

	@Override
	public void finish() {

		LogManager.httpLog.info("上传文件完成");
	}

	@Override
	public void fail() {
		LogManager.httpLog.info("上传文件失败");

	}
}

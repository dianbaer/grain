package test;

import org.grain.httpserver.HttpConfig;
import org.grain.httpserver.IUploadProgress;

public class UploadProgressListener implements IUploadProgress {
	public UploadProgressListener() {

	}

	@Override
	public void update(long arg0, long arg1, int arg2) {
		if (HttpConfig.log != null) {
			HttpConfig.log.info("arg0:" + arg0 + ",arg1:" + arg1 + ",arg2" + arg2);
		}
	}

	@Override
	public void init(String uuid) {
		if (HttpConfig.log != null) {
			HttpConfig.log.info("上传文件开始");
		}
	}

	@Override
	public void finish() {
		if (HttpConfig.log != null) {
			HttpConfig.log.info("上传文件完成");
		}
	}

	@Override
	public void fail() {
		if (HttpConfig.log != null) {
			HttpConfig.log.info("上传文件失败");
		}
	}
}

package http.filter;

import org.apache.commons.fileupload.ProgressListener;

public interface IUploadProgress extends ProgressListener {
	public void init(String uuid);

	public void finish();

	public void fail();
}

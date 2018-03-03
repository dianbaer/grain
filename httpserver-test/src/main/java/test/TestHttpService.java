package test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.grain.httpserver.HttpConfig;
import org.grain.httpserver.HttpException;
import org.grain.httpserver.HttpPacket;
import org.grain.httpserver.IHttpListener;
import org.grain.httpserver.ReplyFile;
import org.grain.httpserver.ReplyImage;
import org.grain.httpserver.ReplyString;

import protobuf.http.UserGroupProto.GetTokenS;

public class TestHttpService implements IHttpListener {

	@Override
	public Map<String, String> getHttps() {
		HashMap<String, String> map = new HashMap<>();
		map.put("1", "onTestC");
		map.put("2", "onFileC");
		map.put("3", "onImageC");
		map.put("4", "onStringC");
		map.put("5", "onReplyStringC");
		map.put("6", "onException");
		return map;
	}

	public HttpPacket onTestC(HttpPacket httpPacket) throws HttpException {
		GetTokenS.Builder builder = GetTokenS.newBuilder();
		builder.setHOpCode(httpPacket.gethOpCode());
		builder.setTokenId("111111");
		builder.setTokenExpireTime("222222");
		HttpPacket packet = new HttpPacket(httpPacket.gethOpCode(), builder.build());
		return packet;
	}

	public ReplyFile onFileC(HttpPacket httpPacket) throws HttpException {
		File file = new File(HttpConfig.PROJECT_PATH + "/" + HttpConfig.PROJECT_NAME + "/k_nearest_neighbors.png");
		ReplyFile replyFile = new ReplyFile(file, "你好.png");
		return replyFile;
	}

	public ReplyImage onImageC(HttpPacket httpPacket) throws HttpException {
		File file = new File(HttpConfig.PROJECT_PATH + "/" + HttpConfig.PROJECT_NAME + "/k_nearest_neighbors.png");
		ReplyImage image = new ReplyImage(file);
		return image;
	}

	public String onStringC(HttpPacket httpPacket) throws HttpException {

		return "<html><head></head><body><h1>xxxxxxxxxxxx<h1></body></html>";
	}

	public ReplyString onReplyStringC(HttpPacket httpPacket) throws HttpException {
		String str = "<html><head></head><body><h1>xxxxxxxxxxxx<h1></body></html>";
		ReplyString replyString = new ReplyString(str, "text/html");
		return replyString;
	}

	public void onException(HttpPacket httpPacket) throws HttpException {
		GetTokenS.Builder builder = GetTokenS.newBuilder();
		builder.setHOpCode("0");
		builder.setTokenId("111111");
		builder.setTokenExpireTime("222222");
		throw new HttpException("0", builder.build());
	}

}

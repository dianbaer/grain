package org.grain.httpserver;

import java.io.IOException;
import java.io.InputStream;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.util.TextUtils;

public class PacketUtils {
	public static JsonFormat jsonFormat = new JsonFormat();

	public static Message jsonToProtoBuf(String jsonStr, Builder builder) {
		InputStream inputStream = null;
		try {
			inputStream = TextUtils.toInputStream(jsonStr);
			jsonFormat.merge(inputStream, builder);
			Message message = builder.build();
			return message;
		} catch (Exception e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("json转换成protobuf,异常", e);
			}
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (HttpConfig.log != null) {
						HttpConfig.log.error("json转换成protobuf,关闭输入流失败", e);
					}
				}
			}
		}
	}

	public static String protoBufToJson(Message message) {
		return jsonFormat.printToString(message);
	}
}

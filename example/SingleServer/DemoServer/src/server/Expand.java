package server;

import java.net.URLEncoder;

import http.HOpCodeDemoServer;
import init.IExpand;
import init.Init;
import log.LogManager;
import net.sf.json.JSONObject;
import service.DemoService;

public class Expand implements IExpand {

	@Override
	public void init() throws Exception {
		HOpCodeDemoServer.init();
		Init.registerService(DemoService.class);
		JSONObject js = new JSONObject();
		js.put("hOpCode", 1);
		js.put("userName", "dianbaer");
		js.put("userPassword", "123456");
		String packet = URLEncoder.encode(js.toString(), "UTF-8");
		String url = "http://localhost:8080/DemoServer/s?hOpCode=1&sendType=sendTypePacket&packet=" + packet;
		LogManager.initLog.info("尝试请求此地址：" + url);
	}

	@Override
	public void threadInit() throws Exception {

	}
}

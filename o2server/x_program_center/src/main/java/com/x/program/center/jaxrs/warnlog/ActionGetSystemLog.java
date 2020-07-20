package com.x.program.center.jaxrs.warnlog;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.jaxrs.WoValue;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.ListTools;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ActionGetSystemLog extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetSystemLog.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String tag) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> woList = new ArrayList<>();
		String key = effectivePerson.getDistinguishedName();
		if(key.indexOf("@") > -1){
			key = key.split("@")[1] + tag;
		}

		if(Config.node().equals(Config.resource_node_centersPirmaryNode())){
			woList = getSystemLog(key);
		}else{
			List<NameValuePair> headers = ListTools.toList(new NameValuePair(HttpToken.X_Token, effectivePerson.getToken()));
			woList = ConnectionAction.get(Config.url_x_program_center_jaxrs("warnlog", "view", "system", "log", "tag", tag), headers).getDataAsList(Wo.class);
		}

		result.setData(woList);
		return result;
	}

	synchronized private List<Wo> getSystemLog(String key) throws Exception{
		Nodes nodes = Config.nodes();
		List<Wo> allLogs = new ArrayList<>();
		for (String node : nodes.keySet()){
			if(nodes.get(node).getApplication().getEnable() || nodes.get(node).getCenter().getEnable()){
				try (Socket socket = new Socket(node, nodes.get(node).nodeAgentPort())) {
					socket.setKeepAlive(true);
					socket.setSoTimeout(5000);
					try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						 DataInputStream dis = new DataInputStream(socket.getInputStream())){
						Map<String, Object> commandObject = new HashMap<>();
						commandObject.put("command", "readLog:readLog");
						commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));

						dos.writeUTF(XGsonBuilder.toJson(commandObject));
						dos.flush();

						long lastPoint = 0;
						String cacheKey = ApplicationCache.concreteCacheKey(key, node.toLowerCase());
						Element element = cacheLog.get(cacheKey);
						CacheLogObject clo = null;
						if ((null != element) && (null != element.getObjectValue())) {
							clo = (CacheLogObject) element.getObjectValue();
							lastPoint = clo.getLastPoint();
						}
						dos.writeLong(lastPoint);
						dos.flush();

						//logger.debug("socket dispatch getSystemLog to {}:{} lastPoint={}", node, nodes.get(node).nodeAgentPort(), lastPoint);

						String result = dis.readUTF();
						if(StringUtils.isNotEmpty(result) && result.startsWith("[")){
							List<Wo> list = gson.fromJson(result, new TypeToken<List<Wo>>(){}.getType());
							allLogs.addAll(list);
							long returnLastPoint = dis.readLong();
							if(clo==null){
								clo = new CacheLogObject();
								clo.setUserToken(key);
								clo.setNode(node);
								clo.setLastPoint(returnLastPoint);
							}else{
								clo.setLastPoint(returnLastPoint);
							}
							cacheLog.put(new Element(cacheKey, clo));
						}
					}

				} catch (Exception ex) {
					logger.warn("socket dispatch getSystemLog to {}:{} error={}", node, nodes.get(node).nodeAgentPort(), ex.getMessage());
				}
			}
		}
		allLogs.stream().sorted((o1, o2) -> {
			return o1.logTime.compareTo(o2.logTime);
		});
		return allLogs;
	}

	public static class Wo extends GsonPropertyObject {
		private String logTime;

		private String lineLog;

		private String node;

		private String logLevel;

		public String getLogTime() {
			return logTime;
		}

		public void setLogTime(String logTime) {
			this.logTime = logTime;
		}

		public String getLineLog() {
			return lineLog;
		}

		public void setLineLog(String lineLog) {
			this.lineLog = lineLog;
		}

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public String getLogLevel() {
			return logLevel;
		}

		public void setLogLevel(String logLevel) {
			this.logLevel = logLevel;
		}
	}

}

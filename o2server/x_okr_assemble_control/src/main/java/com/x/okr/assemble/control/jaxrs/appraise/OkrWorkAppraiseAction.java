package com.x.okr.assemble.control.jaxrs.appraise;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 具体工作项有短期工作还长期工作，短期工作不需要自动启动定期汇报，由人工撰稿汇报即可
 */

@Path("okrworkappraise")
@JaxrsDescribe("工作考核信息管理服务")
public class OkrWorkAppraiseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkAppraiseAction.class);

	@JaxrsMethodDescribe(value = "通知OKR系统同步流程审批状态信息", action = ActionWorkAppraiseStatusSync.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void syncStatus(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionWorkAppraiseStatusSync.Wo> result = new ActionResult<>();
		Boolean check = true;
		if (check) {
			try {
				result = new ActionWorkAppraiseStatusSync().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("system excute ActionWorkAppraiseStatusSync got an exception. ");
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
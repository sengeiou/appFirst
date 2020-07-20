package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("attachment")
@JaxrsDescribe("附件操作")
public class AttachmentAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AttachmentAction.class);

	@JaxrsMethodDescribe(value = "测试文件是否存在.", action = ActionAvailable.class)
	@GET
	@Path("{id}/available")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void available(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionAvailable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAvailable().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work和附件Id获取单个附件信息.", action = ActionGetWithWork.class)
	@GET
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionGetWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWork().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted和附件Id获取单个附件信息", action = ActionGetWithWorkCompleted.class)
	@GET
	@Path("{id}/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionGetWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work获取Attachment列表.", action = ActionListWithWork.class)
	@GET
	@Path("list/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<List<ActionListWithWork.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWork().execute(effectivePerson, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted获取Attachment列表.", action = ActionListWithWorkCompleted.class)
	@GET
	@Path("list/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<List<ActionListWithWorkCompleted.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWorkCompleted().execute(effectivePerson, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据工作或已完成工作获取Attachment列表.", action = ActionListWithWorkOrWorkCompleted.class)
	@GET
	@Path("list/workorworkcompleted/{workOrWorkCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
		ActionResult<List<ActionListWithWorkOrWorkCompleted.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除指定work下的附件.", action = ActionDeleteWithWork.class)
	@DELETE
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWork().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除指定workCompleted下的附件. ", action = ActionDeleteWithWorkCompleted.class)
	@DELETE
	@Path("{id}/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionDeleteWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件", action = ActionDownloadWithWork.class)
	@GET
	@Path("download/{id}/work/{workId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,匹配文件有个扩展名", action = ActionDownloadWithWork.class)
	@GET
	@Path("download/{id}/work/{workId}/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,设定是否使用stream输出", action = ActionDownloadWithWorkStream.class)
	@GET
	@Path("download/{id}/work/{workId}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,设定是否使用stream输出.匹配文件有个扩展名", action = ActionDownloadWithWorkStream.class)
	@GET
	@Path("download/{id}/work/{workId}/stream/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件", action = ActionDownloadWithWorkCompleted.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件,匹配文件有个扩展名.", action = ActionDownloadWithWorkCompleted.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件", action = ActionDownloadWithWorkCompletedStream.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件,匹配文件有个扩展名.", action = ActionDownloadWithWorkCompletedStream.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/stream/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传附件.", action = ActionUpload.class)
	@POST
	@Path("upload/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void upload(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					   @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
					   @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
					   @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
					   @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
					   @FormDataParam(FILE_FIELD) byte[] bytes,
					   @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if(StringUtils.isEmpty(extraParam)){
				extraParam = this.request2Json(request);
			}
			if(bytes==null){
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for(String key: map.keySet()){
					FormDataBodyPart part = map.get(key).get(0);
					if("application".equals(part.getMediaType().getType())){
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUpload().execute(effectivePerson, workId, site, fileName, bytes, disposition,
					extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传附件.", action = ActionUploadWithWorkCompleted.class)
	@POST
	@Path("upload/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void uploadWithWorkCompleted(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if(StringUtils.isEmpty(extraParam)){
				extraParam = this.request2Json(request);
			}
			if(bytes==null){
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for(String key: map.keySet()){
					FormDataBodyPart part = map.get(key).get(0);
					if("application".equals(part.getMediaType().getType())){
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUploadWithWorkCompleted().execute(effectivePerson, workCompletedId, site, fileName,
					bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));

	}

	@JaxrsMethodDescribe(value = "上传附件.", action = ActionUploadCallback.class)
	@POST
	@Path("upload/work/{workId}/callback/{callback}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.TEXT_HTML_UTF_8)
	public void uploadCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadCallback.Wo<ActionUploadCallback.WoObject>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadCallback().execute(effectivePerson, workId, callback, site, fileName, bytes,
					disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新附件.", action = ActionUpdate.class)
	@PUT
	@Path("update/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void update(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if(StringUtils.isEmpty(extraParam)){
				extraParam = this.request2Json(request);
			}
			if(bytes==null){
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for(String key: map.keySet()){
					FormDataBodyPart part = map.get(key).get(0);
					if("application".equals(part.getMediaType().getType())){
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新Attachment信息", action = ActionUpdateContent.class)
	@PUT
	@Path("update/content/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateContent(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					   @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
					   @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionUpdateContent.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateContent().execute(effectivePerson, id, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新附件,使用callback方式,为了与前台兼容使用POST方法.", action = ActionUpdateCallback.class)
	@POST
	@Path("update/{id}/work/{workId}/callback/{callback}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.TEXT_HTML_UTF_8)
	public void updateCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdateCallback.Wo<ActionUpdateCallback.WoObject>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateCallback().execute(effectivePerson, id, workId, callback, fileName, bytes,
					disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/** 与update方法同,为了兼容ntko对于附件上传只能设置post方法 */
	@JaxrsMethodDescribe(value = "更新附件.", action = ActionUpdate.class)
	@POST
	@Path("update/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void updatePost(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if(StringUtils.isEmpty(extraParam)){
				extraParam = this.request2Json(request);
			}
			if(bytes==null){
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for(String key: map.keySet()){
					FormDataBodyPart part = map.get(key).get(0);
					if("application".equals(part.getMediaType().getType())){
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "由指定的工作拷贝附件.", action = ActionCopyToWork.class)
	@POST
	@Path("copy/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void copyToWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<List<ActionCopyToWork.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCopyToWork().execute(effectivePerson, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新附件.", action = ActionChangeSite.class)
	@GET
	@Path("{id}/work/{workId}/change/site/{site}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changeSite(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("位置") @PathParam("site") String site) {
		ActionResult<ActionChangeSite.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionChangeSite().execute(effectivePerson, id, workId, site);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新附件.", action = ActionEdit.class)
	@PUT
	@Path("edit/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, id, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置附件排序号.", action = ActionEdit.class)
	@GET
	@Path("{id}/work/{workId}/change/ordernumber/{orderNumber}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changeOrderNumber(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@PathParam("orderNumber") Integer orderNumber) {
		ActionResult<ActionChangeOrderNumber.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionChangeOrderNumber().execute(effectivePerson, id, workId, orderNumber);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新附件文本.", action = ActionEditText.class)
	@PUT
	@Path("edit/{id}/work/{workId}/text")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void exitText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionEditText.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEditText().execute(effectivePerson, id, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取附件文本.", action = ActionGetText.class)
	@GET
	@Path("{id}/work/{workId}/text")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionGetText.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetText().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "将HTML版式公文转换成Word文件并添加在附件中.", action = ActionDocToWord.class)
	@POST
	@Path("doc/to/word/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void docToWord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionDocToWord.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDocToWord().execute(effectivePerson, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "pdf格式预览文件,支持doc,docx.", action = ActionPreviewPdf.class)
	@GET
	@Path("{id}/preview/pdf")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewPdf(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionPreviewPdf.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewPdf().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "pdf格式预览文件获取接口.", action = ActionPreviewPdfResult.class)
	@GET
	@Path("preview/pdf/{flag}/result")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewPdfResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
		ActionResult<ActionPreviewPdfResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewPdfResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "image格式预览文件,支持doc,docx", action = ActionPreviewImage.class)
	@GET
	@Path("{id}/preview/image/page/{page}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewImage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("页数") @PathParam("page") Integer page) {
		ActionResult<ActionPreviewImage.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewImage().execute(effectivePerson, id, page);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "image格式预览文件获取接口.", action = ActionPreviewImageResult.class)
	@GET
	@Path("preview/image/{flag}/result")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewImageResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
		ActionResult<ActionPreviewImageResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewImageResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work或WorkCompleted批量下载附件并压缩,设定是否使用stream输出", action = ActionBatchDownloadWithWorkOrWorkCompletedStream.class)
	@GET
	@Path("batch/download/work/{workId}/site/{site}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void batchDownloadWithWorkOrWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("*附件框分类,多值~隔开,(0)表示全部") @PathParam("site") String site,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName,
			@JaxrsParameterDescribe("通过uploadWorkInfo上传返回的工单信息存储id") @QueryParam("flag") String flag) {
		ActionResult<ActionBatchDownloadWithWorkOrWorkCompletedStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBatchDownloadWithWorkOrWorkCompletedStream().execute(effectivePerson, workId, site, fileName, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work或WorkCompleted批量下载附件并压缩", action = ActionBatchDownloadWithWorkOrWorkCompleted.class)
	@GET
	@Path("batch/download/work/{workId}/site/{site}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void batchDownloadWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("*附件框分类,多值~隔开,(0)表示全部") @PathParam("site") String site,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName,
			@JaxrsParameterDescribe("通过uploadWorkInfo上传返回的工单信息存储id") @QueryParam("flag") String flag) {
		ActionResult<ActionBatchDownloadWithWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBatchDownloadWithWorkOrWorkCompleted().execute(effectivePerson, workId, site, fileName, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传工单的表单、审批记录等html信息到缓存", action = ActionUploadWorkInfo.class)
	@PUT
	@Path("upload/work/{workId}/save/as/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void uploadWorkInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							  @JaxrsParameterDescribe("Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
							  @JaxrsParameterDescribe("另存为格式：(0)表示不转换|pdf表示转为pdf|word表示转为word") @PathParam("flag") String flag,
							   JsonElement jsonElement) {
		ActionResult<ActionUploadWorkInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadWorkInfo().execute(effectivePerson, workId, flag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "下载工单的表单、审批记录等html信息", action = ActionDownloadWorkInfo.class)
	@GET
	@Path("download/work/{workId}/att/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWorkInfo(@Suspended final AsyncResponse asyncResponse,
													 @Context HttpServletRequest request,
													 @JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
													 @JaxrsParameterDescribe("*通过uploadWorkInfo上传返回的附件id") @PathParam("flag") String flag){
		ActionResult<ActionDownloadWorkInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWorkInfo().execute(effectivePerson, workId, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员上传附件.", action = ActionManageUpload.class)
	@POST
	@Path("upload/work/{workId}U/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void manageUpload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							 @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
							 @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
							 @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
							 @JaxrsParameterDescribe("上传到指定用户") @FormDataParam("person") String person,
							 @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
							 @FormDataParam(FILE_FIELD) final byte[] bytes,
							 @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionManageUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageUpload().execute(effectivePerson, workId, site, fileName, bytes, disposition,
					extraParam, person);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
package com.x.cms.assemble.control.jaxrs.comment;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.express.tools.filter.QueryFilter;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ActionListPrevWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPrevWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		ResultObject resultObject = null;
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = null;		
		Boolean check = true;
		String cacheKey = null;
		Element element = null;
		QueryFilter queryFilter = null;
		
		if ( StringUtils.isEmpty( flag ) || "(0)".equals(flag)) {
			flag = null;
		}

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCommentQuery(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if( check ) {
			queryFilter = wrapIn.getQueryFilter();
		}
		
		if( check ) {
			cacheKey = ApplicationCache.concreteCacheKey( "ActionListPrevWithFilter", effectivePerson.getDistinguishedName(), 
					flag, count, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter.getContentSHA1() );
			element = commentInfoCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {
				try {				
					Long total = documentCommentInfoQueryService.countWithFilter( effectivePerson, queryFilter );					
					List<DocumentCommentInfo>  documentCommentInfoList = documentCommentInfoQueryService.listWithFilter( effectivePerson, count, flag, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter );
					
					if( ListTools.isNotEmpty( documentCommentInfoList )) {
						for( DocumentCommentInfo documentCommentInfo : documentCommentInfoList ) {
							Wo wo = Wo.copier.copy(documentCommentInfo);
							wo.setContent( documentCommentInfoQueryService.getCommentContent(documentCommentInfo.getId() ));
							wos.add( wo );
						}
					}
					
					resultObject = new ResultObject( total, wos );
					commentInfoCache.put(new Element( cacheKey, resultObject ));
					result.setCount( resultObject.getTotal() );
					result.setData( resultObject.getWos() );
				} catch (Exception e) {
					check = false;
					logger.warn("系统查询评论信息列表时发生异常!");
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}		
		}
		return result;
	}

	public static class Wi extends WrapInQueryDocumentCommentInfo{
		
	}
	
	public static class Wo extends DocumentCommentInfo {
		
		private Long rank;
		
		@FieldDescribe("内容")
		private String content = "";
		
		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<DocumentCommentInfo, Wo> copier = WrapCopierFactory.wo( DocumentCommentInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
	
	public static class ResultObject {

		private Long total;
		
		private List<Wo> wos;

		public ResultObject() {}
		
		public ResultObject(Long count, List<Wo> data) {
			this.total = count;
			this.wos = data;
		}

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public List<Wo> getWos() {
			return wos;
		}

		public void setWos(List<Wo> wos) {
			this.wos = wos;
		}
	}
}
package com.x.teamwork.assemble.control.jaxrs.taskview;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.TaskView;

import net.sf.ehcache.Element;

public class ActionListMyTaskView extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListMyTaskView.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String projectId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		ResultObject resultObject = null;
		List<Wo> wos = new ArrayList<>();
		Boolean check = true;
		String cacheKey = null;
		Element element = null;
		
		if ( StringUtils.isEmpty( projectId ) ) {
			check = false;
			Exception exception = new ProjectIdEmptyException( );
			result.error(exception);
		}
		
		if( Boolean.TRUE.equals( check ) ){
			cacheKey = ApplicationCache.concreteCacheKey( "ActionListMyTaskView", projectId, effectivePerson.getDistinguishedName(), effectivePerson.isManager() );
			element = taskViewCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {
				try {
					List<TaskView>  taskViewList = taskViewQueryService.listViewWithPersonAndProject( effectivePerson, projectId );
					Long total = 0L;
					if( ListTools.isNotEmpty( taskViewList )) {
						total = Long.parseLong( taskViewList.size() + "" );
						wos = Wo.copier.copy(taskViewList);
					}
					
					resultObject = new ResultObject( total, wos );
					taskViewCache.put( new Element( cacheKey, resultObject ));
					
					result.setCount( resultObject.getTotal() );
					result.setData( resultObject.getWos() );
				} catch (Exception e) {
					check = false;
					logger.warn("系统查询工作任务视图列表时发生异常!");
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}		
		}
		return result;
	}
	
	public static class Wo extends TaskView {
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskView, Wo> copier = WrapCopierFactory.wo( TaskView.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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
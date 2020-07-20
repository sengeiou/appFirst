//package com.x.processplatform.assemble.surface.jaxrs.work;
//
//import java.util.Objects;
//
//import org.apache.commons.lang3.BooleanUtils;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.project.Applications;
//import com.x.base.core.project.x_processplatform_service_processing;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.processplatform.assemble.surface.Business;
//import com.x.processplatform.assemble.surface.ThisApplication;
//import com.x.processplatform.assemble.surface.WorkControl;
//import com.x.processplatform.core.entity.content.Work;
//import com.x.processplatform.core.entity.element.ActivityType;
//import com.x.processplatform.core.entity.element.Process;
//
//class ActionCheckDraft extends BaseAction {
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
//		ActionResult<Wo> result = new ActionResult<>();
//		Wo wo = new Wo();
//		Work work = null;
//		Process process = null;
//		WoControl control = null;
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			work = emc.find(id, Work.class);
//			if (null != work) {
//				process = business.process().pick(work.getProcess());
//				control = business.getControl(effectivePerson, work, WoControl.class);
//			}
//		}
//		if ((null != process) && (null != control)) {
//			if (BooleanUtils.isTrue(process.getCheckDraft())) {
//				if (BooleanUtils.isTrue(control.getAllowDelete())) {
//					if (BooleanUtils.isFalse(work.getDataChanged())
//							&& Objects.equals(ActivityType.manual, work.getActivityType())) {
//						wo = ThisApplication.context().applications()
//								.deleteQuery(x_processplatform_service_processing.class,
//										Applications.joinQueryUri("work", work.getId()), work.getJob())
//								.getData(Wo.class);
//						wo.setId(work.getId());
//					}
//				}
//			}
//		}
//		result.setData(wo);
//		return result;
//	}
//
//	public static class Wo extends WoId {
//
//	}
//
//	public static class WoControl extends WorkControl {
//	}
//}
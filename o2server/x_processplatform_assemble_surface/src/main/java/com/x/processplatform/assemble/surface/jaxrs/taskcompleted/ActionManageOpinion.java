package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import java.util.Objects;

public class ActionManageOpinion extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionManageOpinion.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(id, TaskCompleted.class);
			}
			Process process = business.process().pick(taskCompleted.getProcess());
			Application application = business.application().pick(taskCompleted.getApplication());

			if (!business.canManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			emc.beginTransaction(TaskCompleted.class);
			if (StringTools.utf8Length(wi.getOpinion()) > JpaObject.length_255B) {
				taskCompleted.setOpinionLob(wi.getOpinion());
				taskCompleted.setOpinion(StringTools.utf8SubString(wi.getOpinion(), JpaObject.length_255B));
			} else {
				taskCompleted.setOpinion(Objects.toString(wi.getOpinion(), ""));
				taskCompleted.setOpinionLob(null);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("意见")
		private String opinion;

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

	}

	public static class Wo extends WrapBoolean {
	}

}
package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;

class ActionListWithWorkOrWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithWorkOrWorkCompleted.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);

			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);

			List<String> units = business.organization().unit().listWithPerson(effectivePerson);

			final String job = business.job().findWithWorkOrWorkCompleted(workOrWorkCompleted);

			List<Wo> wos = new ArrayList<>();

			for (Attachment attachment : this.list(business, job)) {
				if (this.read(attachment, effectivePerson, identities, units)) {
					Wo wo = Wo.copier.copy(attachment);
					wo.getControl().setAllowRead(true);
					wo.getControl().setAllowEdit(this.edit(wo, effectivePerson, identities, units));
					wo.getControl().setAllowControl(this.control(wo, effectivePerson, identities, units));
					wos.add(wo);
				}
			}

			wos = wos.stream().sorted(Comparator.comparing(Wo::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
					.thenComparing(Comparator.comparing(Wo::getCreateTime, Comparator.nullsLast(Date::compareTo))))
					.collect(Collectors.toList());

			result.setData(wos);
			return result;
		}
	}

	private List<Attachment> list(Business business, String job) throws Exception {
		List<Attachment> os = business.entityManagerContainer().listEqual(Attachment.class, Attachment.job_FIELDNAME,
				job);
		return os;
	}

	public static class Wo extends Attachment {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo(Attachment.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private WoControl control = new WoControl();

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}

	}

	public static class WoControl extends GsonPropertyObject {

		private Boolean allowRead = false;
		private Boolean allowEdit = false;
		private Boolean allowControl = false;

		public Boolean getAllowRead() {
			return allowRead;
		}

		public void setAllowRead(Boolean allowRead) {
			this.allowRead = allowRead;
		}

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowControl() {
			return allowControl;
		}

		public void setAllowControl(Boolean allowControl) {
			this.allowControl = allowControl;
		}

	}

}
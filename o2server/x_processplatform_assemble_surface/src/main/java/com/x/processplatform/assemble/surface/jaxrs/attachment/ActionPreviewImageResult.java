package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import net.sf.ehcache.Element;

class ActionPreviewImageResult extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPreviewImageResult.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		Element element = cachePreviewImage.get(flag);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		if (null != element && null != element.getObjectValue()) {
			PreviewImageResultObject obj = (PreviewImageResultObject) element.getObjectValue();
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), obj.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			wo = new Wo(obj.getBytes(), this.contentType(true, obj.getName()),
					this.contentDisposition(true, obj.getName()));
			result.setData(wo);
		} else {
			throw new ExceptionPreviewImageResultObject(flag);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
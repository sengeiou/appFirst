package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryViewNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionQueryViewNotExists( String id ) {
		super("ID为{}的默认视图信息不存在。", id );
	}
}

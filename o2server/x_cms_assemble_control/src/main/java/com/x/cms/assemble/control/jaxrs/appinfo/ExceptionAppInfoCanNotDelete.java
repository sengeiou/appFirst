package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoCanNotDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoCanNotDelete( String message ) {
		super( message  );
	}
}

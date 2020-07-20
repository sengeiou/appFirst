package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTaskNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionTaskNotExists( String id ) {
		super("指定的待办信息不存在!ID:" + id );
	}
	
}

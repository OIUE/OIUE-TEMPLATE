package org.oiue.service.template;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oiue.tools.StatusResult;

public interface TemplateService extends Serializable {
	
	public StatusResult render(HttpServletRequest request, HttpServletResponse response, Map<String, ?> parameter);
	
}

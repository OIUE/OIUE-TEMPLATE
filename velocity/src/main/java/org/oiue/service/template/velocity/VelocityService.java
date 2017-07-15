package org.oiue.service.template.velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Dictionary;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.template.TemplateService;
import org.oiue.tools.StatusResult;
import org.osgi.service.http.HttpContext;

@SuppressWarnings("unused")
public class VelocityService implements TemplateService {
    private static final long serialVersionUID = 1L;
    private ResourceLoader resourceLoader;
    private LogService logService;
    private Logger logger;

	private Map<String, VelocityEngine> ves = new ConcurrentHashMap<String, VelocityEngine>();
	
    public VelocityService(LogService logService, ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.logService = logService;
    }

    @Override
    public StatusResult render(HttpServletRequest request, HttpServletResponse response, Map<String, ?> parameter) {
    	String domain = (String) request.getAttribute("domain");
    	String domain_path = (String) request.getAttribute("domain_path");
    	String path = (String) request.getAttribute("resName");
    	VelocityEngine ve = ves.get(domain);
        if (ve == null) {
        	ServletContext httpContext = (ServletContext) request.getAttribute("httpContext");
            if(httpContext!=null&&URLResourceLoader.httpContext==null)
            URLResourceLoader.httpContext = httpContext;
            ve = new VelocityEngine();
            Properties p = new Properties();
            p.setProperty("resource.loader", "urlrl");
            p.setProperty("urlrl.resource.loader.class", resourceLoader.getClass().getName());
            p.setProperty("urlrl.resource.loader.domain", domain);
            p.setProperty("urlrl.resource.loader.domain_path", domain_path);
            p.setProperty("input.encoding", "UTF-8");
            ve.init(p);
            ves.put(domain, ve);
        }
		PrintWriter out = null;
		StatusResult sr = new StatusResult();
        try {
        	out=response.getWriter();
        	Template template = ve.getTemplate(path);
        	VelocityContext context = new VelocityContext(parameter);
        	template.merge(context, out);
        	sr.setResult(StatusResult._SUCCESS);
		} catch (Throwable e) {
			sr.setResult(StatusResult._ncriticalAbnormal);
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
        return sr;
    }

    public void updated(Dictionary<String, ?> props) {

    }

}

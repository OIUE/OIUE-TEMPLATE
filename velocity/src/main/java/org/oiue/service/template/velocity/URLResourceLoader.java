package org.oiue.service.template.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.oiue.service.log.Logger;

@SuppressWarnings({"unchecked","rawtypes"})
public class URLResourceLoader extends ResourceLoader {
    static Logger logger;
    static ServletContext httpContext=null;
    private final String uuid = UUID.randomUUID().toString();
    private Map urlMap = new HashMap();
    private String domain_path = null;

    public void init(ExtendedProperties configuration) {
        if(logger.isDebugEnabled()){
            logger.debug("["+this.uuid+"]init:"+configuration);
        }
        domain_path = configuration.getString("domain_path");
    }

    public synchronized InputStream getResourceStream(String resourceName) throws ResourceNotFoundException {
        try {
            if(logger.isDebugEnabled()){
                logger.debug("["+this.uuid+"]getResourceStream:"+resourceName+"|"+this.rsvc);
            }
            if("VM_global_library.vm".equals(resourceName))
                return null;
            URL url = getURL(resourceName);

            if (url == null) {
                throw new ResourceNotFoundException("Can not find resource: " + resourceName);
            }
            return url.openStream();
        } catch (IOException e) {
            throw new ResourceNotFoundException("Can not find resource: " + resourceName + " - Reason: " + e.getMessage(),e);
        }
    }

    public long getLastModified(Resource res) {
        try {
            URL url = getURL(res.getName());
            long lm = url.openConnection().getLastModified();
            return lm;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    public boolean isSourceModified(Resource res) {
        long lastModified = getLastModified(res);
        return (lastModified != res.getLastModified());
    }

    private URL getURL(String resourceName) throws MalformedURLException {
        if (urlMap.containsKey(resourceName)) {
            return (URL) urlMap.get(resourceName);
        }
        resourceName = resourceName.startsWith("/")?resourceName:"/"+resourceName;

        if(logger.isDebugEnabled()){
            logger.debug("["+this.uuid+"]getURL:"+domain_path+resourceName+"|"+this.rsvc);
        }
        URL url = httpContext.getResource(domain_path+resourceName);

        if (url != null) {
            urlMap.put(resourceName, url);
        }

        return url;
    }
}

package com.gnipcentral.client.util;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Package-protected abstraction for loading services from <code>META-INF/services</code>.  This
 * class is not intended for external use.
 */
class ServiceLoader {    
    
    static List<Class> loadServices(Class service) {
        if(service == null) {
            return Collections.emptyList();
        }

        String path = "META-INF/services/" + service.getName();

        LinkedList<Class> classes = null;
        InputStream is = null;
        BufferedReader r = null;
        try {
            Enumeration<URL> configs = ClassLoader.getSystemResources(path);

            while(configs.hasMoreElements()) {
                URL url = configs.nextElement();
                is = url.openStream();
                r = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                int idx;
                String line;
                while((line = r.readLine()) != null) {
                    idx = line.indexOf("#");
                    if(idx > 0)
                        line = line.substring(0, idx);
                    line = line.trim();

                    Class c = Class.forName(line);
                    if(classes == null) {
                        classes = new LinkedList<Class>();
                    }

                    classes.add(c);
                }
            }
        } catch(IOException e) {
            System.err.println("Exception occurred loading service implementations for service " + service.getName() + ".  Cause: " + e.getMessage());
        } catch(ClassNotFoundException e) {
            System.err.println("Exception occurred loading service implementations for service " + service.getName() + ".  Cause: " + e.getMessage());
        } finally {
            try{if(is != null) is.close();} catch(IOException ignore) {}
            try{if(r != null) r.close();} catch(IOException ignore) {}
        }

        if(classes == null)
            return Collections.emptyList();
        else return classes; 
    }
}

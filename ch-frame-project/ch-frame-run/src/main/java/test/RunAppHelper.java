package test;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 */
public class RunAppHelper {
    public static void run(int port, String contentPath, String env) {
        run(port, contentPath, null, env);
    }

    public static void run(int port, String contentPath, String webprjdir, String env) {
    	if(env != null)
    		System.setProperty("ch.env", env);
        try {
            if (webprjdir == null) {
                webprjdir = ".";
            }
            if (contentPath == null) {
                contentPath = "/";
            }
            Server server = new Server(port);
            System.out.println("Begin start jetty...");
            HandlerCollection hc = new HandlerCollection();
            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath(contentPath);
            File webappdir = new File(new File(webprjdir), "/web");
            //System.out.println(webappdir.getCanonicalPath());
            if (!webappdir.exists()) {
                throw new RuntimeException("目录" + webprjdir + "下不存在/web");
            }
            webapp.setWar(webappdir.getAbsolutePath().replace("%20", ""));
            InputStream fis = RunAppHelper.class.getResourceAsStream("/jettyetc/webdefault.xml");
            File tempfie = new File("webdefault.xml");

            FileOutputStream fos = new FileOutputStream(tempfie);
            IOUtils.copy(fis, fos);
            fos.close();

            String deffile = tempfie.getAbsolutePath().replace("%20", " ");
            webapp.setDefaultsDescriptor(deffile);
            webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
            webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
            hc.addHandler(webapp);
            System.out.println("Add web app for container and contextPath is " + contentPath);
            server.setHandler(hc);
            server.start();
            System.out.println("Start webserver for port:" + port + " successful!");
            tempfie.delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

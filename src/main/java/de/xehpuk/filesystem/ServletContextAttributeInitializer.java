package de.xehpuk.filesystem;

import java.nio.file.Paths;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletContextAttributeInitializer implements ServletContextListener {
	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		final ServletContext c = sce.getServletContext();
		c.setAttribute("root", Paths.get(c.getInitParameter("de.xehpuk.filesystem.root")));
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {}
}
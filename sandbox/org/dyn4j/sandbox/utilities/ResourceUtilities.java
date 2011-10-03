package org.dyn4j.sandbox.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility class to read resources from the class path.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceUtilities {
	/**
	 * Returns a list of urls to the resources inside the given package.
	 * @param pack the package name using / instead of .
	 * @return List&lt;URL&gt;
	 * @throws IOException thrown if an IO error occurs
	 * @throws URISyntaxException  thrown if the given pack is not a valid URI
	 */
	public static final List<String> getResources(String pack) throws IOException, URISyntaxException {
		URL url = ResourceUtilities.class.getResource(pack);
		if (url != null && url.getProtocol().equals("file")) {
			// use the file api
			String[] fileNames = new File(url.toURI()).list();
			List<String> urls = new ArrayList<String>();
			for (String fileName : fileNames) {
				urls.add(pack + "/" + fileName);
			}
			return urls;
		}
		
		if (url.getProtocol().equals("jar")) {
			// TODO test this
			/* A JAR path */
//			String jarPath = url.getPath().substring(5,
//					dirURL.getPath().indexOf("!")); // strip out only the JAR
//													// file
			List<String> urls = new ArrayList<String>();
			JarFile jar = new JarFile(url.getPath());
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(pack)) { // filter according to the path
					urls.add(name);
				}
			}
			
			return urls;
		}
		
		return null;
	}
}

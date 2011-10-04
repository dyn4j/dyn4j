package org.dyn4j.sandbox.utilities;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Utility class for loading, managing, and modifying images.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageUtilities {
	/**
	 * Returns a BufferedImage from the classpath given the path.
	 * @param path the path within the classpath
	 * @return BufferedImage
	 * @throws IOException thrown if an error occurs reading the file
	 */
	public static final BufferedImage getImageFromClassPath(String path) throws IOException {
		URL url = ImageUtilities.class.getResource(path);
		return ImageIO.read(url);
	}
	
	/**
	 * Returns an Icon from the classpath given the path.
	 * @param path the path within the classpath
	 * @return ImageIcon
	 * @throws IOException thrown if an error occurs reading the file
	 */
	public static final ImageIcon getIconFromClassPath(String path) throws IOException {
		BufferedImage bi = ImageUtilities.getImageFromClassPath(path);
		return new ImageIcon(bi);
	}
	
	/**
	 * Returns a BufferedImage from the classpath given the path.
	 * @param path the path within the classpath
	 * @return BufferedImage
	 */
	public static final BufferedImage getImageFromClassPathSuppressExceptions(String path) {
		try {
			return ImageUtilities.getImageFromClassPath(path);
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Returns an Icon from the classpath given the path.
	 * @param path the path within the classpath
	 * @return ImageIcon
	 */
	public static final ImageIcon getIconFromClassPathSuppressExceptions(String path) {
		try {
			BufferedImage bi = ImageUtilities.getImageFromClassPath(path);
			return new ImageIcon(bi);
		} catch (IOException e) {
			return null;
		}
	}
}

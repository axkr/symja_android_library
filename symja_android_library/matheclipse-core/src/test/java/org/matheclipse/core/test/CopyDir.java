package org.matheclipse.core.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Copy a source directory or file to a target directory or file.
 */
public class CopyDir {

	public static void copyDirectory(File source, File target) throws IOException {

		if (source.isDirectory()) {
			// ignore .SVN and CVS dirs
			if (source.getName().toLowerCase().equals(".svn") || source.getName().toLowerCase().equals("cvs")) {
				return;
			}
			if (!target.exists()) {
				// only copy if the directory already exists
				System.out.println(source.getName() + " - target doesn't exist!");
				return;
			}
			String[] children = source.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(source, children[i]), new File(target, children[i]));
			}
		} else {

			if (target.exists()) {
				if (source.lastModified() <= target.lastModified()) {
					// only copy if timestamp is newer than existing ones
					System.out
							.println(source.getName() + " - " + source.lastModified() + " " + target.lastModified());
					return;
				}
			} else {
				// only copy if file already exists
				return;
			}
			FileInputStream in = new FileInputStream(source);
			FileOutputStream out = new FileOutputStream(target);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void main(String[] args) {

		try {
			File sourceLocation = new File("C:\\temp\\readme");
			File targetLocation = new File("C:\\temp\\readme");
			copyDirectory(sourceLocation, targetLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
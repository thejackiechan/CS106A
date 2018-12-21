/*
 * CS 106A Critters
 * Downloads the contents of a .zip file from the web to disk.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.util;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import critters.model.*;
import critters.gui.*;
import stanford.cs106.gui.*;
import stanford.cs106.io.*;
import stanford.cs106.net.*;
import stanford.cs106.reflect.*;

public class ZipDownloader implements Runnable {
	private String zipFile;

	private CritterModel model;

	private JFrame frame;

	private JButton button;

	public ZipDownloader(String zipFile, CritterModel model, JFrame frame,
			JButton button) {
		this.zipFile = zipFile;
		this.model = model;
		this.frame = frame;
		this.button = button;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		button.setEnabled(false);
		try {
			String filename = JOptionPane.showInputDialog("File to read?",
					zipFile);
			if (filename == null || (filename = filename.trim()).length() == 0) {
				return;
			}
			Map<String, byte[]> zipFilesMap = Downloader
					.getZipFileContents(new URL(filename));

			// remove ".class" from file names
			Set<String> classNames = new TreeSet<String>();
			for (String fileName : zipFilesMap.keySet()) {
				classNames.add(fileName.replace(".class", ""));
			}

			// filter out inner classes from list
			Set<String> innerClasses = new TreeSet<String>();
			for (Iterator<String> i = classNames.iterator(); i.hasNext();) {
				String className = i.next();
				if (ClassUtils.isInnerClass(className)) {
					i.remove();
					// String outerClassName = className.substring(0, className.indexOf('$'));
					innerClasses.add(className);
				}
			}

			// show dialog box to user so they can select husky file(s)
			ListOptionPane dialog = new ListOptionPane(frame, classNames);
			dialog.setVisible(true);
			
			// at this point, user has closed the dialog box; process their choice
			if (!dialog.pressedOk()) {
				return;
			}
			Object[] selectedItems = dialog.getSelectedValues();

			// include any necessary inner classes in list of files to download
			Set<String> selectedClasses = new TreeSet<String>();
			for (Object item : selectedItems) {
				String className = item.toString();
				selectedClasses.add(className);

				// include any inner classes associated with this class
				for (String innerClassName : innerClasses) {
					if (innerClassName.startsWith(className)) {
						selectedClasses.add(innerClassName);
					}
				}
			}

			// write the selected classes as files to disk
			String folderToUse = ClassUtils.getFirstClassPathFolder();
			
			for (String className : selectedClasses) {
				String fileName = className + ClassUtils.CLASS_EXTENSION;
				byte[] bytes = zipFilesMap.get(fileName);
				IOUtils.writeBytes(bytes, folderToUse + File.separator + fileName);
			}

			// load the classes into the JVM
			for (String className : selectedClasses) {
				if (ClassUtils.isInnerClass(className)) {
					continue;
				}
				String fileName = folderToUse + File.separator + className + ClassUtils.CLASS_EXTENSION;
				Class<? extends Critter> critterClass = (Class<? extends Critter>) ClassUtils.loadClass(fileName);
				model.add(CritterModel.DEFAULT_CRITTER_COUNT, critterClass);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame,
					"Error downloading ZIP data:\n" + e, "I/O error",
					JOptionPane.ERROR_MESSAGE);
			if (CritterGui.PRINT_EXCEPTIONS) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Error loading class data:\n"
					+ e, "Class loading error", JOptionPane.ERROR_MESSAGE);
			if (CritterGui.PRINT_EXCEPTIONS) {
				e.printStackTrace();
			}
		} finally {
			button.setEnabled(true);
		}
	}
}

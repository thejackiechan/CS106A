/*
 * CS 106A Critters
 * A bunch of methods used to dynamically load critter classes sent across
 * the web.  Useful for running 1-on-1 critter tournaments.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/24
 * - initial version for 15sp
 */

package critters.util;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import critters.model.*;
import stanford.cs106.reflect.*;

public final class CritterClassUtils {
	// class constants
	// public static final String CRITTER_CLASS_PACKAGE = "critters.animals.";
	public static final String CRITTER_CLASS_PACKAGE = "";   // default package
	private static final String[] DEFAULT_CRITTERS = {"Ant", "Bird", "Hippo", CritterModel.WOLF_CLASS_NAME, "Vulture"};

	// Adds 25 of each critter class type to the given model.
	// The only critter-specific code in here; a bit of a cheat
	// so that CritterMain.java doesn't have to have this icky code in it.
	public static void addAllCritterClasses(CritterModel model, int count) {
		List<String> classPathFolders = ClassUtils.getClassPathFolders();
		for (String folder : classPathFolders) {
			for (Class<? extends Critter> critterClass : ClassUtils.getClasses(Critter.class, folder)) {
				model.add(count, critterClass);
			}
		}
	}

	// Adds 25 of each Wolf class type to the given model.
	public static void addOtherWolfClasses(CritterModel model, int count) {
		for (String folder : ClassUtils.getClassPathFolders()) {
			for (Class<? extends Critter> critterClass : ClassUtils.getClasses(Critter.class, folder)) {
				if (isNetworkClass(critterClass.getName())) {
					model.add(count, critterClass);
				}
			}
		}
	}
	
	// Returns a list of all critter-extending classes found in this program.
	public static List<Class<? extends Critter>> getAllCritterClasses() {
		List<Class<? extends Critter>> classes = new ArrayList<Class<? extends Critter>>();
		
		// figure out if we are running in an applet/jar
		boolean jar = false;
		try {
			jar = System.getProperty("testrunner.jarmode") != null;
			String classPath = System.getProperty("java.class.path");
			jar = jar || classPath.contains("applet.jar");
		} catch (Exception e) {
			jar = true;
		}
		
		if (jar) {
			// can't easily read the class names from a JAR archive; just hard-code them
			return getDefaultCritterClasses();
		} else {
			for (String folder : ClassUtils.getClassPathFolders()) {
				List<Class<? extends Critter>> list = ClassUtils.getClasses(Critter.class, folder);
				if (list == null) {
					return getDefaultCritterClasses();
				} else {
					classes.addAll(list);
				}
			}
		}
		return classes;
	}
	
	// Returns whether the given class is one that came from the network.
	// Excludes inner classes (ones with $ in their name).
	public static boolean isNetworkClass(String className) {
		return className.indexOf('_') >= 0 && !ClassUtils.isInnerClass(className);
	}

	@SuppressWarnings("unchecked")
	public static List<Class<? extends Critter>> getDefaultCritterClasses() {
		// probably running as an applet; return default classes
		List<Class<? extends Critter>> classes = new ArrayList<Class<? extends Critter>>();
        for (String critterClassName : DEFAULT_CRITTERS) {
        	try {
            	Class<? extends Critter> clazz = (Class<? extends Critter>) Class.forName(CRITTER_CLASS_PACKAGE + critterClassName);
            	classes.add(clazz);
	        } catch (ClassNotFoundException e) {
	        	// System.err.println("Default critter class not found: " + critterClassName);
	        }
        }
        return classes;
	}
    
	public static String truncate(String className, int length) {
		if (className.length() <= length) {
			return className;
		} else {
			return className.substring(0, length) + "~";
		}
	}
}

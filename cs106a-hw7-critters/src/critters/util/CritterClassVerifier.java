/*
 * CS 106A Critters
 * Utility class to make sure the student doesn't do silly things
 * in his/her critter code.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import critters.model.*;
import critters.gui.CritterGui;
import critters.gui.CritterInitialSettingsDialog;

public class CritterClassVerifier {
	public static final String[] CLASSES_TO_CHECK_METHODS = {
		"Ant",
		"Bird",
		"Crab",
		"Hippo",
		"Vulture"
	};

	// Pops up an error message if the student has any methods
	// with the wrong signature or mis-capitalized names.
	public static void checkForSillyMethods() {
		List<String> errors = new ArrayList<String>();

		// methods inherited from class Object
		Set<String> objectMethods = new HashSet<String>();
		objectMethods.add("equals");
		objectMethods.add("getClass");
		objectMethods.add("hashCode");
		objectMethods.add("notify");
		objectMethods.add("notifyAll");
		objectMethods.add("wait");

		// grab all inherited methods
		Map<String, Method> critterMethodMap = new HashMap<String, Method>();
		Map<String, Method> critterMethodMapLC = new HashMap<String, Method>();
		try {
			for (Method method : Critter.class.getMethods()) {
				String methodName = method.getName();
				critterMethodMap.put(methodName, method);
				critterMethodMapLC.put(methodName.toLowerCase(), method);
				// System.out.println("Critter method " + methodName + ": " +
				// method);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

		for (String className : CLASSES_TO_CHECK_METHODS) {
			try {
				Class<?> clazz = Class.forName(className);
				Class<?> superClass = clazz.getSuperclass();
				// if (superClass != Critter.class && superClass == Object.class) {
				if (superClass == Object.class || superClass == null) {
					errors.add("Class " + className
							+ " is supposed to extend Critter, but doesn't.");
					continue;
				}
				for (Method method : clazz.getMethods()) {
					// System.out.println(className + " method: " + method);
					String methodName = method.getName();
					if (objectMethods.contains(methodName)) {
						continue; // inherited from class Object
					}

					String methodNameLC = methodName.toLowerCase();
					if (critterMethodMapLC.containsKey(methodNameLC)) {
						Method critterMethod = critterMethodMapLC
								.get(methodNameLC);
						if (!critterMethodMap.containsKey(methodName)) {
							errors.add("Class "
									+ className
									+ " has incorrect capitalization for method "
									+ methodName + ":\nexpected: "
									+ critterMethod + "\nactual: "
									+ method);
						}
						if (!Arrays.equals(critterMethod.getParameterTypes(),
								method.getParameterTypes())) {
							errors.add("Class " + className
									+ " has incorrect parameters for method "
									+ methodName + ":\nexpected: "
									+ critterMethod + "\nactual: " + method);
						}
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		// System.out.println("ERRORS: " + errors);
		String errorString = "";
		for (String error : errors) {
			errorString += "\n\n" + error;
		}

		if (!errors.isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"I found some possible errors in your critter classes:"
							+ errorString, "Possible critter errors",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public static CritterGui initialSettings() {
		boolean applet = false;
		try {
			System.getProperty("user.dir");          // just try some things that applets wouldn't allow
			System.getProperty("user.name");
			System.getProperty("java.class.path");
			File.createTempFile("critters", "test");
		} catch (Exception e) {
			applet = true;
		}
		
		CritterSecurityManager security = null;
		if (!applet) {
			try {
				security = new CritterSecurityManager(true);
				System.setSecurityManager(security);
			} catch (Exception e) {
				// empty
			}
		}
		List<Class<? extends Critter>> critterClasses = CritterClassUtils.getAllCritterClasses();
		if (security != null) {
			try {
				security.disable();
			} catch (Exception e) {
				// empty
			}
		}
		
		List<String> names = new ArrayList<String>();
		names.add("Width");
		names.add("Height");
		names.add("Number of each critter");
		if (!applet) names.add("Network features");
		if (!applet) names.add("Secure tournament mode");
		names.add("Debug output");
		names.add(null);

		List<Class<?>> types = new ArrayList<Class<?>>();
		types.add(Integer.TYPE);
		types.add(Integer.TYPE);
		types.add(Integer.TYPE);
		if (!applet) types.add(Boolean.TYPE);
		if (!applet) types.add(Boolean.TYPE);
        types.add(Boolean.TYPE);
		types.add(null);

		List<Object> initialValues = new ArrayList<Object>();
		initialValues.add(new Integer(CritterModel.DEFAULT_WIDTH));
		initialValues.add(new Integer(CritterModel.DEFAULT_HEIGHT));
		initialValues.add(new Integer(CritterModel.DEFAULT_CRITTER_COUNT));
		if (!applet) initialValues.add(new Boolean(!applet && CritterGui.DEFAULT_NETWORK_ENABLED));
        if (!applet) initialValues.add(new Boolean(!applet && CritterGui.SECURE));
        initialValues.add(new Boolean(CritterGui.DEFAULT_DEBUG));
		initialValues.add(null);
		
		for (Class<? extends Critter> critterClass : critterClasses) {
			names.add(critterClass.getName());
			types.add(Boolean.TYPE);
			initialValues.add(Boolean.TRUE);
		}
		
		boolean ok = CritterInitialSettingsDialog.showInputDialog(null, "Critters settings", null,
				names.toArray(new String[0]), types.toArray(new Class<?>[0]),
				initialValues.toArray(new Object[0]));
		if (ok) {
			int width = CritterInitialSettingsDialog.getInt("Width");
			int height = CritterInitialSettingsDialog.getInt("Height");
			int count = CritterInitialSettingsDialog.getInt("Number of each critter");
            boolean network = !applet && CritterInitialSettingsDialog.getBoolean("Network features");
            boolean secure = !applet && CritterInitialSettingsDialog.getBoolean("Secure tournament mode");
            boolean debug = CritterInitialSettingsDialog.getBoolean("Debug output");

			CritterModel model = new CritterModel(width, height, debug);  // create simulation

			for (Class<? extends Critter> critterClass : critterClasses) {
				boolean checked = CritterInitialSettingsDialog.getBoolean(critterClass.getName());
				if (checked) {
					try {
						model.add(count, critterClass);
					} catch (BuggyCritterException e) {
						Throwable cause = e.getCause();
						cause.printStackTrace();
						JOptionPane.showMessageDialog(null, "An error occurred while constructing the animals!\n"
								+ e.getMessage() + "\n"
								+ cause + "\n\n"
								+ "See the console for more details about the error.");
					} catch (RuntimeException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "An error occurred while constructing the animals!\n"
								+ e + "\n\n"
								+ "See the console for more details about the error.");
					}
				}
			}

			CritterGui gui = new CritterGui(model, network, secure);
			return gui;
		} else {
			// user canceled
			return null;
		}
	}
}

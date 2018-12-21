/*
 * CS 106A Critters
 * Provides a secure environment for the critters to play in.
 * Forbids them from reading files or network resources.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.util;

import java.io.FileDescriptor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import critters.gui.CritterGui;
import critters.gui.CritterInitialSettingsDialog;

public class CritterSecurityManager extends SecurityManager {
	private static final Set<String> FRIEND_CLASSES = new HashSet<String>(
			Arrays.asList("CritterSecurityManager", "ClassUtils", "CritterClassVerifier",
					"CritterGui", "CritterMain", "CritterNetworkManager"));
	
	private boolean strict;
	private boolean disabled = false;
	
	public CritterSecurityManager() {
		this(false);
	}
	
	public CritterSecurityManager(boolean strict) {
		this.strict = strict;
	}
	
	public void checkAccept(String host, int port) {
		checkConnect(host, port, null);
	}

	public void checkAccess(Thread t) {
		if (strict) {
			throw new SecurityException("cannot access thread: " + t);
		}
	}

	public void checkAccess(ThreadGroup tg) {
		if (strict) {
			throw new SecurityException("cannot access thread group: " + tg);
		}
	}

	public void checkConnect(String host, int port) {
		checkConnect(host, port, null);
	}

	public void checkConnect(String host, int port, Object context) {
		if (strict) {
			throw new SecurityException("cannot accept/connect over network: " + host + " " + port);
		}
	}

	public void checkCreateClassLoader() {
		// empty
	}

	public void checkDelete(String file) {
		throw new SecurityException("cannot delete file: " + file);
	}

	public void checkExec(String cmd) {
		throw new SecurityException("cannot exec: " + cmd);
	}

	public void checkLink(String lib) {
		// empty
	}

	public void checkListen(int port) {
		if (port != CritterNetworkManager.DEFAULT_PORT_1
				&& port != CritterNetworkManager.DEFAULT_PORT_2) {
			throw new SecurityException("cannot listen on network: " + port);
		}
	}

	public void checkMemberAccess(Class<?> clazz, int which) {
		if (which == Modifier.PUBLIC) { return; }
		if (which == Modifier.PROTECTED) { return; }
		
		if (which == Modifier.PRIVATE) {
			String className = (clazz == null) ? "" : clazz.getName();
			if (className.contains(".")) { return; }   // not a critter class
			
			throw new SecurityException("cannot access member: " + clazz + ", " + which);
		}
	}

	public void checkPackageAccess(String pkg) {
		// empty
	}

	public void checkPackageDefinition(String pkg) {
		// empty
	}

	public void checkPermission(Permission perm) {
		checkPermission(perm, null);
	}

    public void checkPermission(Permission perm, Object context) {
        String name = perm.getName();
        if (name == null || (name = name.intern()).length() == 0) {
        	return;
        }
        if (perm instanceof RuntimePermission) {
            // RuntimePermission rperm = (RuntimePermission) perm;
            if (name.equals("setSecurityManager") && !disabled) {
                throw new SecurityException("cannot disable security manager");
            }
        } else if (perm instanceof ReflectPermission) {
        	// ReflectPermission rperm = (ReflectPermission) perm;
        	if (strict && name.equals("suppressAccessChecks")) {
        		// try to figure out what class caused the exception
        		SecurityException sex = new SecurityException("cannot bypass access checks using reflection");
        		for (StackTraceElement element : sex.getStackTrace()) {
        			String className = element.getClassName();
        			if (!className.contains(".") && !className.equals(this.getClass().getName())) {
        				if (!FRIEND_CLASSES.contains(className)) {
        					throw sex;
        				}
        			}
        		}
        	}
        }
    }
    
	public void checkPropertiesAccess() {
		// empty
	}

	public void checkPropertyAccess(String property) {
		// empty
	}

	public void checkRead(FileDescriptor fd) {
		// empty
	}

	public void checkRead(String file) {
		// empty
	}

	public void checkRead(String file, Object context) {
		// empty
	}

	// so the windows won't have a "Java Applet Window" warning
	public boolean checkTopLevelWindow(Object window) {
		return true;
	}

	public void checkWrite(FileDescriptor fd) {
		// throw new SecurityException("cannot write file: " + fd);
	}

	public void checkWrite(String file) {
		if (!file.equals(CritterGui.SAVE_STATE_FILE_NAME)
				&& !file.equals(CritterInitialSettingsDialog.SAVE_STATE_FILE_NAME)
				&& !file.equals(CritterGui.ZIP_FILE_NAME)) {
			throw new SecurityException("cannot write file: " + file + "\n"
					+ "(not allowed to modify any files when in Secure Tournament mode, to prevent hacking!)");
		}
	}
	
	public void disable() {
		disabled = true;
		System.setSecurityManager(null);
	}
}
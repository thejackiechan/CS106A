/*
 * CS 106A Critters
 * A dialog box window that pops up at the start of the program
 * to ask the user what classes they want in the critter world.
 * Used to initialize the GUI and model settings.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/24
 * - initial version for 15sp
 */

package critters.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import stanford.cs106.gui.GuiUtils;

public class CritterInitialSettingsDialog {
	public static final String SAVE_STATE_FILE_NAME = "_critters_saved_settings.txt";

	private static Map<String, Object> values = new TreeMap<String, Object>();
	private static Properties savedSettings = new Properties();

	// load settings
	static {
		try {
			savedSettings.load(new FileInputStream(SAVE_STATE_FILE_NAME));
		} catch (Exception ioe) {
			// empty
		}
	}

	public static boolean getBoolean(String name) {
		Boolean b = (Boolean) values.get(name);
		return b.booleanValue();
	}

	public static int getInt(String name) {
		Integer i = (Integer) values.get(name);
		return i.intValue();
	}

	public static String getString(String name) {
		String s = (String) values.get(name);
		return s;
	}

	public static Map<String, Object> getValues() {
		return values;
	}

	public static void showInputDialog(Frame parent, String title,
			String message, String[] names, Class<?>[] types) {
		showInputDialog(parent, title, message, names, types, null);
	}

	// Shows a dialog box with the given settings in it.
	// Returns true if OK was clicked and false if the dialog was canceled.
	public static boolean showInputDialog(Frame parent, String title,
			String message, final String[] names, final Class<?>[] types,
			final Object[] initialValues) {

		// hack to get a reference to a boolean value
		final boolean[] result = { true };

		final JDialog dialog = new JDialog(parent, title, true);
		final JPanel west = new JPanel(new GridLayout(0, 1));
		// final Container center = new JScrollPane(new JPanel(new GridLayout(0, 1)));
		final Container center = new JPanel(new GridLayout(0, 1));
		final JComponent[] comps = new JComponent[names.length];

		for (int i = 0; i < names.length; i++) {
			west.add(new JLabel(names[i]));
			if (types[i] == Boolean.TYPE) {
				JCheckBox box = new JCheckBox();
				if (names != null && names[i] != null && savedSettings.containsKey(names[i])) {
					box.setSelected(Boolean.parseBoolean(savedSettings.getProperty(names[i])));
				} else {
					box.setSelected(initialValues != null && initialValues[i] != null
							&& String.valueOf(initialValues[i]).equals("true"));
				}
				comps[i] = box;
				center.add(box);
			} else if (types[i] != null) {
				int width = 10;
				if (types[i] == Integer.TYPE || types[i] == Double.TYPE) {
					width = 4;
				}
				JTextField field = new JTextField(width);
				if (names != null && names[i] != null && savedSettings.containsKey(names[i])) {
					field.setText(savedSettings.getProperty(names[i]));
				} else if (initialValues != null && initialValues[i] != null) {
					field.setText(String.valueOf(initialValues[i]));
				}
				comps[i] = field;
				center.add(field);
			} else {
				// null type means blank slot
				center.add(new JPanel());
			}
		}

		JPanel south = new JPanel();
		JButton ok = new JButton("OK");
		ok.setMnemonic('O');
		ok.requestFocus();
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		south.add(ok);

		KeyListener key = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					result[0] = false;
					dialog.setVisible(false);
				}
			}
		};

		if (initialValues != null) {
			JButton reset = new JButton("Reset");
			reset.setMnemonic('R');
			reset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < names.length; i++) {
						if (types[i] == Boolean.TYPE) {
							JCheckBox box = (JCheckBox) comps[i];
							box.setSelected(initialValues != null
									&& initialValues[i].toString().equals(
											"true"));
						} else if (types[i] != null) {
							JTextField field = (JTextField) comps[i];
							field.setText(initialValues[i].toString());
						}
					}
				}
			});
			south.add(reset);
			reset.addKeyListener(key);
		}

		JButton checkAll = new JButton("All");
		checkAll.setMnemonic('A');
		checkAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < center.getComponentCount(); i++) {
					Component comp = center.getComponent(i);
					if (comp instanceof JCheckBox) {
						((JCheckBox) comp).setSelected(true);
					}
				}
			}
		});
		south.add(checkAll);
		checkAll.addKeyListener(key);

		JButton uncheckAll = new JButton("None");
		uncheckAll.setMnemonic('N');
		uncheckAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < center.getComponentCount(); i++) {
					Component comp = center.getComponent(i);
					if (comp instanceof JCheckBox) {
						((JCheckBox) comp).setSelected(false);
					}
				}
			}
		});
		south.add(uncheckAll);
		uncheckAll.addKeyListener(key);

		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				result[0] = false;
				dialog.setVisible(false);
			}
		});
		dialog.addKeyListener(key);
		ok.addKeyListener(key);
		dialog.getContentPane().setLayout(new BorderLayout(10, 5));
		((JComponent) dialog.getContentPane()).setBorder(BorderFactory
				.createEmptyBorder(10, 10, 10, 10));

		if (message != null) {
			JLabel messageLabel = new JLabel(message);
			dialog.add(messageLabel, BorderLayout.NORTH);
		}
		
		Container westCenter = new JPanel(new BorderLayout());
		westCenter.add(west, BorderLayout.WEST);
		westCenter.add(center);
		
		// dialog.add(west, BorderLayout.WEST);
		// dialog.add(new JScrollPane(center));
		dialog.add(new JScrollPane(westCenter));
		dialog.add(south, BorderLayout.SOUTH);
		dialog.pack();
		
		// re-enabling resizing of the dialog so it can scroll
		// dialog.setResizable(false);
		
		GuiUtils.centerWindow(dialog);

		// actually show the dialog box on the screen
		ok.requestFocus();
		dialog.setVisible(true);
		ok.requestFocus();

		// by this point, the dialog has been closed by the user
		values.clear();

		// store all the user's settings in the map for later
		for (int i = 0; i < names.length; i++) {
			if (types[i] == Boolean.TYPE) {
				JCheckBox box = (JCheckBox) comps[i];
				values.put(names[i], new Boolean(box.isSelected()));
				if (savedSettings != null) {
					savedSettings.setProperty(names[i], String.valueOf(box
							.isSelected()));
				}
			} else if (types[i] == Integer.TYPE) {
				JTextField field = (JTextField) comps[i];
				String text = field.getText();
				int value = 0;
				if (initialValues != null) {
					Integer integer = (Integer) initialValues[i];
					value = integer.intValue();
				}

				try {
					value = Integer.parseInt(text);
				} catch (Exception e) {
					// empty
				}

				values.put(names[i], new Integer(value));
				if (savedSettings != null) {
					savedSettings.setProperty(names[i], text);
				}
			} else if (types[i] != null) {
				JTextField field = (JTextField) comps[i];
				values.put(names[i], field.getText());
				if (savedSettings != null) {
					savedSettings.setProperty(names[i], field.getText());
				}
			}
		}

		try {
			savedSettings.store(new FileOutputStream(SAVE_STATE_FILE_NAME),
					"Critters saved settings");
		} catch (Exception ioe) {
			// empty
		}

		return result[0];
	}
}

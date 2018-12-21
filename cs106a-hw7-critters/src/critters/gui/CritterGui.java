/*
 * CS 106A Critters
 * This is the overall graphical user interface for the Critter simulation.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/24
 * - initial version for 15sp
 */

package critters.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;

import critters.model.*;
import critters.util.*;
import stanford.cs106.gui.*;
import stanford.cs106.io.*;
import stanford.cs106.net.*;
import stanford.cs106.reflect.*;
import stanford.cs106.util.*;

public final class CritterGui implements ActionListener, Observer, WindowListener {
	// class constants
	public static final String SAVE_STATE_FILE_NAME = "_critters_network_settings.txt";
	public static final boolean PRINT_EXCEPTIONS = true;
	public static final boolean SHOULD_SAVE_SETTINGS = true;
    public static final boolean DEFAULT_NETWORK_ENABLED = true;
    public static final boolean DEFAULT_DEBUG = false;

	private static final String TITLE = "CS 106A Critters";
	// private static final long serialVersionUID = 0;
	private static final int DELAY = 100; // default MS between redraws
	private static final int MAX_CLASS_NAME_LENGTH = 24;
	
	public static final boolean SECURE = false;  // use security manager?

	// constants for saving/loading GUI state
	private static final String LAST_HOST_NAME_KEY = "lastHostName";
	private static final String FPS_KEY = "fps";
	private static final String ACCEPT_KEY = "accept";
    private static final String BACKGROUND_COLORS_KEY = "backgroundColors";
    // private static final String DEBUG_KEY = "debug";
	private static final String USER_NAME_KEY = "username";
	private static final String ALWAYS_VALUE = "always";
	private static final String ASK_VALUE = "ask";
	private static final String NEVER_VALUE = "never";

	// constant for loading files from the course web site
	public static final String ZIP_FILE_NAME = "wolves-" + Stanford.getCurrentQuarter() + ".zip";
	public static final String ZIP_CODE_BASE = "http://www.martystepp.com/critters/" + ZIP_FILE_NAME;
	public static final String WOLF_WEB_SERVICE_URL = "http://www.martystepp.com/critters/wolf.php";
	
	private static final Font STATUS_FONT = new Font("monospaced", Font.PLAIN, 11);
	private static final Font CLASS_FONT = new Font("sansserif", Font.BOLD, 11);

	static {
		GuiUtils.setSystemLookAndFeel();
	}

	// This is basically the main method that makes the GUI and starts the program.
	// I am "hiding" it here for student readability.
	public static void createGui() {
		CritterClassVerifier.checkForSillyMethods();
		CritterGui gui = CritterClassVerifier.initialSettings();

		if (gui == null) {
			// user canceled
			try {
				System.exit(0);
			} catch (Exception e) {
				// empty
			}
		} else {
			gui.start(); // run the GUI
		}
	}

	// fields
	private CritterModel model;
	private CritterPanel panel;
	private JFrame frame;
	private JButton go, stop, tick, reset, loadFromWeb;
	private JSlider slider;
	private JComponent east;
	private JRadioButton always, never, ask;
	private String lastHostName = "";
	private String lastUserName = "";
	private JLabel moves;
    private JCheckBox backgroundColors;
    private JCheckBox debug;
	private boolean enableNetwork;
	private boolean enableLoadFromWeb;
	private boolean enableSendRequest;

	// receives critters from others and lets me voluntarily send mine out
	// packets = [hostname, classname, critter text]
	private CritterNetworkManager networkSenderListener;

	// lets me host my wolf so others can reach out and request it from me
	// packets = [hostname, classname requested]
	private CritterNetworkManager networkServer;

	// keeps track of which classes already have a ClassPanel on the east side
	// of the window, so we know when we need to add a new one (on network receive etc)
	private Map<String, ClassPanel> counts;

	// Constructs a new GUI to display the given model of critters.
	public CritterGui(CritterModel model) {
		this(model, false, false);
	}

	public CritterGui(CritterModel model, boolean network, boolean secure) {
		this.model = model;
		model.addObserver(this);

		enableNetwork = enableLoadFromWeb = enableSendRequest = network;

		// try to load settings from disk (fail silently)
		Properties props = null;
		if (SHOULD_SAVE_SETTINGS) {
			try {
				props = loadConfiguration();
			} catch (IOException ioe) {
				// empty
			} catch (SecurityException ioe) {
			    // don't print security exceptions
			}
		}

		// important not to store security manager anywhere as a field;
		// prevent evil hands from getting a reference to it
		SecurityManager mgr = null;
		if (secure) {
			try {
				mgr = new CritterSecurityManager();
				model.lock(mgr);
				System.setSecurityManager(mgr);
			} catch (SecurityException e) {
				// empty
			}
		}

		// set up network listeners
		networkSenderListener = new CritterNetworkManager();
		networkSenderListener.getReceiveEvent().addObserver(this);
		networkSenderListener.getErrorEvent().addObserver(this);
		networkServer = new CritterNetworkManager(CritterNetworkManager.DEFAULT_PORT_2);
		networkServer.getReceiveEvent().addObserver(this);
		networkServer.getErrorEvent().addObserver(this);

		// set up critter picture panel and set size
		panel = new CritterPanel(model, true);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// add the animation timer
		UberTimer timer = new UberTimer(mgr); //...;
		timer.setCoalesce(true);

		// east panel to store critter class info
		counts = new TreeMap<String, ClassPanel>();
		// east = new JPanel(new GridLayout(0, 1));
		east = new JPanel();
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));

		// FlowLayout wrapper so that ClassPanels aren't stretched vertically
		JPanel eastWrapper = new JPanel();
		eastWrapper.setLayout(new FlowLayout()); // new BoxLayout(eastWrapper, BoxLayout.Y_AXIS));
		eastWrapper.add(east);
		JScrollPane scrollPane = new JScrollPane(eastWrapper);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// east.setBorder(BorderFactory.createTitledBorder("Critter classes:"));
		if (enableLoadFromWeb) {
			loadFromWeb = GuiUtils.createButton("Load from Web...", 'L', this, east);
			loadFromWeb.setAlignmentX(0.5f);
			GuiUtils.shrinkFont(loadFromWeb);
		}

		// timer controls
		JPanel southcenter = new JPanel();
		go = GuiUtils.createButton("Go", 'G', timer, southcenter);
		go.setBackground(Color.GREEN);
		stop = GuiUtils.createButton("Stop", 'S', timer, southcenter);
		stop.setBackground(new Color(255, 96, 96));
		tick = GuiUtils.createButton("Tick", 'T', timer, southcenter);
		tick.setBackground(Color.YELLOW);
		reset = GuiUtils.createButton("Reset", 'R', timer, southcenter);

		go.addKeyListener(timer);
		stop.addKeyListener(timer);
		tick.addKeyListener(timer);
		reset.addKeyListener(timer);
		
		Container southCenterHolder = Box.createVerticalBox();
		southCenterHolder.add(southcenter);
        Container southCenterCheckboxArea = new JPanel();
		backgroundColors = GuiUtils.createCheckBox(CritterModel.WOLF_CLASS_NAME + " background colors", 'H', this, southCenterCheckboxArea);
        backgroundColors.setAlignmentX(1.0f);
        debug = GuiUtils.createCheckBox("Debug", 'D', /* selected */ model.isDebug(), timer, southCenterCheckboxArea);
        debug.setAlignmentX(1.0f);
        southCenterHolder.add(southCenterCheckboxArea);

		// slider for animation speed
		JPanel southwest = new JPanel();
		southwest.add(new JLabel("Speed:"));
		slider = GuiUtils.createSlider(1, 101, 1000 / DELAY, 20, 5, timer, southwest);
		Dimension size = slider.getPreferredSize();
		slider.setPreferredSize(new Dimension(size.width / 2, size.height));
		slider.addKeyListener(timer);
		moves = new JLabel();
		moves.setFont(STATUS_FONT);
		setMovesText();
		southwest.add(moves);

		// checkbox 
		JPanel southeast = new JPanel(); // new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		southeast.setBorder(BorderFactory.createTitledBorder("Accept requests:"));
		ButtonGroup group = new ButtonGroup();
		always = GuiUtils.createRadioButton("Always", 'A', false, group, this, southeast);
		always.setToolTipText("When selected, automatically accepts critters sent to you "
				+ "and automatically shares requested critters.");
		ask = GuiUtils.createRadioButton("Ask", 'k', true, group, this, southeast);
		ask.setToolTipText("When selected, prompts you when critters are sent to you "
		        + "and when requested to share your critters.");
		never = GuiUtils.createRadioButton("Never", 'N', false, group, this, southeast);
		never.setToolTipText("When selected, never accepts critters sent to you "
				+ "and refuses all requests to share your critters.");

		// south panel to hold various widgets
		Container south = new JPanel(new BorderLayout());
		south.add(southCenterHolder);
		south.add(southwest, BorderLayout.WEST);

		if (enableSendRequest) {
			south.add(southeast, BorderLayout.EAST);
		} else {
			south.add(Box.createHorizontalStrut(southwest.getPreferredSize().width), BorderLayout.EAST);
		}

		JPanel center = new JPanel();
		center.add(panel);

		// use saved settings, if any (fail silently)
		if (props != null) {
			try {
				boolean battleMode = false;
				try {
					if (System.getProperty("critters.battlemode") != null) {
						battleMode = true;
					}
				} catch (Exception e) {
					battleMode = true;
				}
				
				enableNetwork = enableNetwork && !battleMode;
				
				if (!battleMode) {
					slider.setValue(Integer.parseInt(props.getProperty(FPS_KEY)));
				}
				
				String accept = props.getProperty(ACCEPT_KEY);
				if (accept.equals(ALWAYS_VALUE)) {
					always.setSelected(true);
				} else if (accept.equals(NEVER_VALUE)) {
					never.setSelected(true);
				} else if (accept.equals(ASK_VALUE)) {
					ask.setSelected(true);
				}
                backgroundColors.setSelected(battleMode || Boolean.parseBoolean(props.getProperty(BACKGROUND_COLORS_KEY, "true")));
                // debug.setSelected(Boolean.parseBoolean(props.getProperty(DEBUG_KEY, "false")));
                // if (!battleMode && model.isDebug()) {
                // 	model.setDebug(false);
                // }
                // debug.setSelected(!battleMode && model.isDebug());
                
				lastHostName = props.getProperty(LAST_HOST_NAME_KEY, "");
				lastUserName = props.getProperty(USER_NAME_KEY, "");
				// timer.setDelay(Integer.parseInt(props.getProperty(FPS_KEY)));
			} catch (Exception e) {
				// empty
			}
		}

		// enable or disable background colors behind critters
        panel.setBackgroundColors(backgroundColors.isSelected());
        model.setDebug(debug.isSelected(), mgr);

		// create frame and do layout
		frame = new JFrame();
		frame.addKeyListener(timer);

		if (enableNetwork) {
			frame.setTitle(TITLE);
			NetworkManager.findIPAddress(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					 frame.setTitle(TITLE + ": " + CritterNetworkManager.getHostName()
						+ " " + CritterNetworkManager.getIpAddresses());
				}
			});
		} else {
			frame.setTitle(TITLE);
		}

		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
		// frame.setResizable(false);
		frame.add(center, BorderLayout.CENTER);
		frame.add(south, BorderLayout.SOUTH);
		frame.add(scrollPane, BorderLayout.EAST);

		GuiUtils.centerWindow(frame);
		timer.doEnabling();
		go.requestFocus();
	}
	
	// wrapped timer so that there is no reference to the SecurityManager
	// and code to mutate the model
	private class UberTimer extends Timer implements ActionListener, KeyListener, ChangeListener {
		private static final long serialVersionUID = 0;
		
		private SecurityManager mgr;
		
		public UberTimer(SecurityManager mgr) {
			super(DELAY, CritterGui.this);
			this.removeActionListener(CritterGui.this);
			this.addActionListener(this);
			this.mgr = mgr;
		}
		
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == go) {
				this.start();
				stop.requestFocus();
			} else if (src == stop) {
				this.stop();
				go.requestFocus();
			} else if (src == this || (src == tick && !this.isRunning())) {
				try {
					model.update(mgr);
				} catch (BuggyCritterException ex) {
					this.stop();
					Throwable cause = ex.getCause();
					cause.printStackTrace();
					errorMessagePane("An error occurred while updating the simulator!\n"
							+ ex.getMessage() + "\n"
							+ cause + "\n\n"
							+ "See the console for more details about the error.");
				} catch (Throwable ex) {
					this.stop();
					ex.printStackTrace();
					errorMessagePane("An error occurred while updating the simulator!\n"
							+ ex + "\n\n"
							+ "See the console for more details about the error.");
				}
			} else if (src == reset) {
				try {
					model.reset(mgr);
				} catch (BuggyCritterException ex) {
					this.stop();
					Throwable cause = ex.getCause();
					cause.printStackTrace();
					errorMessagePane("An error occurred while resetting the simulator!\n"
							+ ex.getMessage() + "\n"
							+ cause + "\n\n"
							+ "See the console for more details about the error.");
				} catch (Throwable ex) {
					ex.printStackTrace();
					errorMessagePane("An error occurred while resetting the simulator!\n"
							+ ex + "\n\n"
							+ "See the console for more details about the error.");
				}
			} else if (src == debug) {
	            model.setDebug(debug.isSelected(), mgr);
	            setMovesText();
	            panel.repaint();
			}

			doEnabling();
		}
		
		// required method of interface KeyListener
		public void keyPressed(KeyEvent e) {
			if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_LEFT) {
				int value = slider.getValue();
				value = Math.max(value - slider.getMinorTickSpacing(), slider.getMinimum());
				slider.setValue(value);
				this.setDelay(1000 / value);
			} else if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
				int value = slider.getValue();
				value = Math.min(value + slider.getMinorTickSpacing(), slider.getMaximum());
				slider.setValue(value);
				this.setDelay(1000 / value);
			}
		}

		// required method of interface KeyListener
		public void keyReleased(KeyEvent e) {
			// empty
		}

		// required method of interface KeyListener
		public void keyTyped(KeyEvent e) {
			// empty
		}

		// Responds to change events on the slider.
		public void stateChanged(ChangeEvent e) {
			int fps = slider.getValue();
			this.setDelay(1000 / fps);
			// timer.setInitialDelay(1000 / fps);
			// timer.restart();
		}

		// Sets which buttons can be clicked at any given moment.
		private void doEnabling() {
			go.setEnabled(!this.isRunning());
			stop.setEnabled(this.isRunning());
			tick.setEnabled(!this.isRunning());
			reset.setEnabled(!this.isRunning());
		}
	}

	// Responds to action events in the GUI.
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == loadFromWeb) {
			Thread thread = new Thread(new ZipDownloader(ZIP_CODE_BASE, model,
					frame, loadFromWeb));
			thread.start();
		} else if (src == backgroundColors) {
			panel.setBackgroundColors(backgroundColors.isSelected());
			panel.repaint();

			for (ClassPanel cpanel : counts.values()) {
				cpanel.updateBorder();
				cpanel.updateBackground();
			}
        }
	}
	
	// Starts the simulation. Assumes all critters have already been added.
	public void start() {
		setCounts();

		// frame.pack();
		GuiUtils.centerWindow(frame);
		frame.setVisible(true);

		// start network listeners
		try {
			if (enableNetwork) {
				networkSenderListener.start();
				networkServer.start();
			}
		} catch (java.net.BindException e) {
			errorMessagePane("Error: The network is already in use.\n\n"
					+ "If you want to be able to send and receive critters over the network,\n"
					+ "please close all instances of the Critters GUI and run it again.",
					"Network in use");
		} catch (IOException e) {
			errorMessagePane("Error starting network listener:\n" + e, "Network error");
			if (PRINT_EXCEPTIONS) {
				e.printStackTrace();
			}
		}

		frame.toFront();
	}

	// Responds to Observable updates in the model.
	public void update(Observable o, Object arg) {
		if (o == model) {
			// model is notifying us of an update
			if (arg == CritterModel.Event.ADD
					|| arg == CritterModel.Event.REMOVE_ALL
					|| arg == CritterModel.Event.UPDATE
					|| arg == CritterModel.Event.RESET) {
				updateCounts();
				setMovesText();
			}
			// TODO: remove overall gui as observer of model?
		} else if (o == networkSenderListener.getReceiveEvent() && arg != null) {
			// we received a message (a class to load)
			if (never.isSelected()) {
				return;
			}

			String[] strings = (String[]) arg;
			loadClassText(strings);
			// setCounts();
		} else if (o == networkServer.getReceiveEvent() && arg != null) {
			// we received a message (a request to send our wolf)
			if (never.isSelected()) {
				// refuse all requests
				return;
			}

			String[] strings = (String[]) arg;
			String hostName = strings[0];
			String className = strings[1];

			if (ask.isSelected()) {
				// "Always Accept" not checked, so ask to confirm
				int choice = JOptionPane
						.showConfirmDialog(frame, "Host \"" + hostName
								+ "\" requests your " + className
								+ " class.  Send it?", "Critter send request",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (choice != JOptionPane.YES_OPTION) {
					// refuse request (send back a null answer)
					sendJavaFile(null, hostName);
					return;
				}
			}

			// user confirmed, so send the class data
			sendJavaFile(className, hostName);
		} else if (o == networkSenderListener.getErrorEvent()
				|| o == networkServer.getErrorEvent()) {
			// something failed in a network send/receive attempt
			if (!this.frame.isVisible()) {
				// closing down the program; don't bother to show this error
				return;
			}
			Exception e = (Exception) arg;
			String message;
			if (e instanceof UnknownHostException) {
				message = "Cannot reach target computer:\n\n" + e;
			} else if (e instanceof ConnectException) {
				message = "Target computer refused connection:\n\n" + e;
			} else if (e instanceof IOException) {
				message = "I/O error:\n" + e;
			} else {
				message = e.toString();
			}
			
			try {
				errorMessagePane(message, "network error");
			} catch (RuntimeException re) {
				// empty
			}
		}
	}

	// Called when the window is about to close.
	// Used to save the GUI's settings.
	public void windowClosing(WindowEvent e) {
		if (SHOULD_SAVE_SETTINGS) {
			try {
				networkServer.stop();
				networkSenderListener.stop();
				saveConfiguration();
			} catch (SecurityException sex) {
			    // don't print applet security exceptions
			} catch (Exception ex) {
				if (PRINT_EXCEPTIONS) {
					ex.printStackTrace();
				}
			}
		}

		try {
			System.exit(0);
		} catch (Exception ex) {
			// empty
		}
	}

	// Required to implement WindowListener interface.
	public void windowActivated(WindowEvent e) {
		// empty
	}

	public void windowClosed(WindowEvent e) {
		// empty
	}

	public void windowDeactivated(WindowEvent e) {
		// empty
	}

	public void windowDeiconified(WindowEvent e) {
		// empty
	}

	public void windowIconified(WindowEvent e) {
		// empty
	}

	public void windowOpened(WindowEvent e) {
		// empty
	}

	private void errorMessagePane(String message) {
		errorMessagePane(message, "An error has occurred!");
	}
	
	private void errorMessagePane(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
	}

	// Helper method to read an integer input from a set of choices.
	private int getInput(String message, Object defaultValue, Object... choices) {
		Object countStr = JOptionPane.showInputDialog(frame, message,
				"Question", JOptionPane.QUESTION_MESSAGE, null, choices,
				defaultValue);
		if (countStr == null) {
			return -1;
		}
		try {
			return Integer.parseInt(countStr.toString());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	// Helper method to read a String input (with the given initial String in the field)
	// and return a default value if an empty string is entered.
	private String getInput(String message, String initialValue,
			String defaultValue) {
		String input = (String) JOptionPane.showInputDialog(frame, message,
				"Question", JOptionPane.QUESTION_MESSAGE, null, null,
				initialValue);
		if (input != null && input.length() == 0) {
			input = defaultValue;
		}
		return input;
	}

	/*
	 // Loads a class received over the network.
	 // The given array contains [host name, class name, encoded base64 classfile text]
	 @SuppressWarnings("unchecked")
	 private void loadClassEncoded(String[] strings) {
	 String hostName = strings[0];
	 String className = strings[1];
	 String encodedFileText = strings[2];
	 
	 if (encodedFileText == null) {
	 // they refused our request and sent back a null
	 JOptionPane.showMessageDialog(frame, hostName + " refused the request.", 
	 "Request refused", JOptionPane.ERROR_MESSAGE);
	 return;
	 }
	 
	 // find out how many critters to add to the world
	 int count = DEFAULT_NUMBER_OF_CRITTERS;
	 if (ask.isSelected()) {
	 count = getInput("Received " + className + " from host \"" + hostName + 
	 "\".\nHow many to add? (Or Cancel to refuse this class)", 
	 DEFAULT_NUMBER_OF_CRITTERS,
	 0, 1, 25, 50, 100);
	 if (count < 0) {
	 return;
	 }
	 }
	 
	 // try to compile and load the received class, add it to simulation
	 try {
	 Class<? extends Critter> critterClass = (Class<? extends Critter>) ClassUtils
	 .writeAndLoadEncodedClass(encodedFileText, className);
	 model.add(count, critterClass);
	 } catch (CritterModel.TooManyCrittersException e) {
	 JOptionPane.showMessageDialog(frame,
	 "Error: Not enough room to add all critters",
	 "Too many critters", JOptionPane.ERROR_MESSAGE);
	 } catch (CritterModel.InvalidCritterClassException e) {
	 JOptionPane.showMessageDialog(frame,
	 "Problem with critter class:\n" + e + "\n\n" +
	 "This is probably DrJava's fault; DrJava has some issues with dynamically loading code over the network.\n" +
	 "If you've got a " + className + ClassUtils.CLASS_EXTENSION + " file in your program's folder, the file got to you successfully, but\n" +
	 "DrJava wasn't able to load it into the simulator.\n\n" +
	 "Try closing and re-running the simulator; this will give DrJava a chance to see the new animal type and load it.\n" +
	 "Hopefully, when you re-run the simulator, the " + className + " type will appear.",
	 
	 "Problem with critter class",
	 JOptionPane.ERROR_MESSAGE);
	 } catch (ClassNotFoundException cnfe) {
	 JOptionPane.showMessageDialog(frame, "Error loading class:\n" + cnfe, "Error",
	 JOptionPane.ERROR_MESSAGE);
	 cnfe.printStackTrace();
	 } catch (IOException ioe) {
	 JOptionPane.showMessageDialog(frame, "Error loading class:\n" + ioe, "Error",
	 JOptionPane.ERROR_MESSAGE);
	 ioe.printStackTrace();
	 }
	 }
	 */

	// Loads a class received over the network.
	// The given array contains [host name, class name, class text]
	@SuppressWarnings("unchecked")
	private void loadClassText(String[] strings) {
		String hostName = strings[0];
		String className = strings[1];
		String fileText = strings[2];

		if (fileText == null) {
			// they refused our request and sent back a null
			errorMessagePane(hostName + " refused the request.", "Request refused");
			return;
		}

		// find out how many critters to add to the world
		int count = CritterModel.DEFAULT_CRITTER_COUNT;
		if (ask.isSelected()) {
			count = getInput("Received " + className + " from host \""
					+ hostName
					+ "\".\nHow many to add? (Or Cancel to refuse this class)",
					CritterModel.DEFAULT_CRITTER_COUNT, 0, 1, 25, 50, 100);
			if (count < 0) {
				return;
			}
		}

		// try to compile and load the received class, add it to simulation
		try {
			Class<? extends Critter> critterClass = (Class<? extends Critter>) ClassUtils
					.writeAndLoadClass(fileText, className, true);
			model.add(count, critterClass);
		} catch (TooManyCrittersException e) {
			errorMessagePane("Error: Not enough room to add all critters", "Too many critters");
		} catch (InvalidCritterClassException e) {
			errorMessagePane("Problem with critter class:\n"
					+ e + "\n\n"
					+ "This is probably DrJava's fault; DrJava has some issues with dynamically loading code over the network.\n"
					+ "If you've got a " + className + ClassUtils.CLASS_EXTENSION
					+ " file in your program's folder, the file got to you successfully, but\n"
					+ "DrJava wasn't able to load it into the simulator.\n\n"
					+ "Try closing and re-running the simulator; this will give DrJava a chance to see the new animal type and load it.\n"
					+ "Hopefully, when you re-run the simulator, the "
					+ className + " type will appear.",
					"Problem with critter class");
			if (PRINT_EXCEPTIONS) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			errorMessagePane("Unable to find the Java compiler.\n"
					+ "If you aren't using DrJava or jGRASP, try running the simulator from there.");
			if (PRINT_EXCEPTIONS) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			errorMessagePane("Error loading class:\n" + e);
			if (PRINT_EXCEPTIONS) {
				e.printStackTrace();
			}
		}
	}

	private Properties loadConfiguration() throws IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(SAVE_STATE_FILE_NAME));
		return prop;
	}

	private void saveConfiguration() throws IOException {
		Properties prop = new Properties();
		int fps = slider.getValue();
		prop.setProperty(LAST_HOST_NAME_KEY, lastHostName);
		prop.setProperty(FPS_KEY, String.valueOf(fps));
		if (always.isSelected()) {
			prop.setProperty(ACCEPT_KEY, ALWAYS_VALUE);
		} else if (ask.isSelected()) {
			prop.setProperty(ACCEPT_KEY, ASK_VALUE);
		} else if (never.isSelected()) {
			prop.setProperty(ACCEPT_KEY, NEVER_VALUE);
		}
		if (lastUserName != null && !lastUserName.isEmpty()) {
			prop.setProperty(USER_NAME_KEY, lastUserName);
		}
        prop.setProperty(BACKGROUND_COLORS_KEY, String.valueOf(backgroundColors.isSelected()));
        // prop.setProperty(DEBUG_KEY, String.valueOf(debug.isSelected()));
		prop.store(new PrintStream(SAVE_STATE_FILE_NAME), "Critters saved network settings");
	}

	// sends the given class code to the given host computer
	// if className is null, sends null text to signify request refused
	private void sendJavaFile(String className, String hostName) {
		try {
			String fileText = null;
			String newClassName = className;
			if (className != null) {
				// new class name = old one + current user name?
				// e.g. "Wolf" --> "Wolf_Stepp"
				String userName = promptForUserName();
				newClassName += "_" + userName;

				// rename and read
				fileText = ClassUtils.readAndRename(className + ClassUtils.JAVA_EXTENSION, className, newClassName);
			}
			networkSenderListener.sendText(hostName, newClassName, fileText);
		} catch (IOException ioe) {
			errorMessagePane("Error reading file:\n" + ioe, "I/O Error");
		}
	}
	
	private String promptForUserName() {
		String userName = "";
		try {
			userName = System.getProperty("user.name");
		} catch (Exception e) {
			// empty
		}
		userName = JOptionPane.showInputDialog(
				"This feature will upload your " + CritterModel.WOLF_CLASS_NAME + " class to our web server \n"
				+ "so you can compete against other students. \n"
				+ "They won't be able to see your " + CritterModel.WOLF_CLASS_NAME + "'s Java source code, \n"
				+ "but they can run their " + CritterModel.WOLF_CLASS_NAME + " against yours and vice versa. \n"
				+ "\n"
				+ "What is your SUNetID? (the part of your email address before @stanford.edu)", userName);
		if (userName != null) {
			userName = userName.trim().toLowerCase();
			lastUserName = userName;
		}
		return userName;
	}

	// sends the given class code to the given host computer
	// if className is null, sends null text to signify request refused
	private void sendJavaFileHttp(String className) {
		try {
			String fileText = null;
			String newClassName = className;
			String userName = promptForUserName();
			if (className == null || userName == null || (userName = userName.trim().toLowerCase()).isEmpty()) {
				return;
			} else {
				// new class name = old one + current user name
				// e.g. "Wolf" --> "Wolf_stepp"
				newClassName += "_" + userName;

				// rename and read
				String dir = System.getProperty("user.dir") + "/src/";
				String fileName = dir + className + ClassUtils.JAVA_EXTENSION;
				fileText = ClassUtils.readAndRename(fileName, className, newClassName);
			}
			
			String classFileName = ClassUtils.writeAndCompile(fileText, newClassName, /* useTempFolder */ true);
			byte[] classBytes = IOUtils.readEntireFileBytes(classFileName);
			
			String response = networkSenderListener.uploadClass(WOLF_WEB_SERVICE_URL, userName, fileText, classBytes);
			JOptionPane.showMessageDialog(this.frame, response);
		} catch (IOException ioe) {
			errorMessagePane("Error reading file:\n" + ioe, "I/O Error");
		} catch (IORuntimeException ioe) {
			errorMessagePane("Error reading file:\n" + ioe, "I/O Error");
		} catch (ClassNotFoundException e) {
			errorMessagePane("Error reading file:\n" + e, "I/O Error");
		} catch (NoSuchMethodException e) {
			errorMessagePane("Error reading file:\n" + e, "I/O Error");
		} catch (IllegalAccessException e) {
			errorMessagePane("Error reading file:\n" + e, "I/O Error");
		} catch (InvocationTargetException e) {
			errorMessagePane("Error reading file:\n" + e, "I/O Error");
		} catch (CompilerErrorException e) {
			errorMessagePane("Error compiling Java class:\n" + e, "Compilation Error");
		} catch (RuntimeException e) {
			errorMessagePane("Error reading file:\n" + e, "I/O Error");
		}
	}

	// Adds right-hand column of labels showing how many of each type are alive.
	// Updates the counter labels to store the current count information.
	private void setCounts() {
		Set<String> classNames = model.getClassNames();
		if (classNames.size() > 0 && classNames.size() == counts.size()) {
			return; // nothing to do
		}

		for (ClassPanel cpanel : counts.values()) {
			east.remove(cpanel);
		}
		counts.clear();

		panel.ensureAllColors();

		boolean packed = false;
		int count = 0;
		for (String className : classNames) {
			ClassPanel cpanel = new ClassPanel(className);
			east.add(cpanel);
			counts.put(className, cpanel);
			
			if (!packed && count >= 3) {
			    east.validate();
			    frame.pack();
			    frame.setSize(frame.getWidth() + 20, frame.getHeight());
			    packed = true;
			}
		}
		
		if (!packed) {
		    east.validate();
		    frame.pack();
		    
		    // buffer because for some reason Swing underestimates east's width
            frame.setSize(frame.getWidth() + 20, frame.getHeight());
		    packed = true;
		}
		east.validate();
		go.requestFocus();
	}
	
	private void setMovesText() {
		String movesText = "<html>" + StringUtils.padNumber(model.getMoveCount(), 6, true) + " moves<br>\n";
		if (model.isDebug()) {
			movesText += "(" + (model.getPartialIndex() + 1) + "/" + model.getTotalCritterCount() + ")";
		} else {
			movesText += "&nbsp;";
		}
		movesText += "</html>";
		moves.setText(movesText);
	}

	// Adds right-hand column of labels showing how many of each type are alive.
	// Updates the counter labels to store the current count information.
	private void updateCounts() {
		// if list of classes is out of date, may need to update east panel
		Set<String> classNames = model.getClassNames();
		if (classNames.size() != counts.size()) {
			setCounts();
			return;
		}
		
		for (String className : classNames) {
			if (!counts.containsKey(className)) {
				setCounts();
				return;
			}
		}
		
		panel.ensureAllColors();

		for (ClassPanel cpanel : counts.values()) {
			cpanel.updateLabel();
		}
	}

	// One of the east panels representing a critter class.
	private class ClassPanel extends JPanel implements ActionListener, Observer {
		private static final long serialVersionUID = 0;

		// fields
		private String className;
		private JButton send, request, delete;
		private JLabel statusLabel;
		private JPanel center;
		private Color oldBackground;
		private TitledBorder border;

		// Constructs a new ClassPanel to hold info about the given critter class.
		public ClassPanel(String className) {
			this.className = className;
			oldBackground = getBackground();

			border = BorderFactory.createTitledBorder(truncateClassName(className,
					MAX_CLASS_NAME_LENGTH));
			border.setTitleFont(CLASS_FONT);
			
			updateBorder();
			setBorder(border);

			model.addObserver(this);
			this.setToolTipText(className);
			statusLabel = new JLabel(" ");
			statusLabel.setFont(STATUS_FONT);
			updateLabel();

			setLayout(new BorderLayout(0, 0));
			add(statusLabel, BorderLayout.NORTH);
			if (CritterClassUtils.isNetworkClass(className)) {
				delete = GuiUtils.createButton("Remove", '\0', this, null);
				delete.setForeground(Color.RED.darker());
				delete.setToolTipText("Remove all animals of type " + className);
				add(delete, BorderLayout.CENTER);
			} else {
				center = new JPanel();
				if (enableSendRequest && this.className.startsWith(CritterModel.WOLF_CLASS_NAME)) {
					send = GuiUtils.createButton("Send", '\0', this, center);
					GuiUtils.shrinkFont(send);
					request = GuiUtils.createButton("Get", '\0', this, center);
					GuiUtils.shrinkFont(request);
					add(center, BorderLayout.CENTER);
				}
			}
		}

		// Handles action events in this panel.
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == send) {
				if (CritterClassUtils.isNetworkClass(className)) {
					return; // skip network classes
				}

				// ask what computer to send to
//				String hostName = getInput("Send your " + className
//						+ " to what computer name or IP address? \n",
//						lastHostName, "localhost");
//				if (hostName != null) {
					// send class to computer
//					lastHostName = (hostName.equals("localhost") ? ""
//							: hostName);
//					sendJavaFile(className, hostName);
//				}
				sendJavaFileHttp(className);
			} else if (src == request) {
				// ask what computer to request from
				String hostName = getInput("Request " + className
						+ " from what computer name or IP address?\n ",
						lastHostName, "localhost");
				if (hostName != null) {
					// request class from computer
					lastHostName = (hostName.equals("localhost") ? ""
							: hostName);
					networkServer.requestClass(hostName, className);
				}
			} else if (src == delete) {
				// delete this network class from the system
				String classFileName = className + ClassUtils.CLASS_EXTENSION;
				model.removeAll(className);

				// set the .class file to be deleted when the VM exits
				// BUG: if they then send us that same file while GUI is running,
				// I'm unable to cancel the delete-on-exit request and it'll
				// get deleted when you close your GUI.  Oh well.
				int choice = JOptionPane
						.showConfirmDialog(frame, className
								+ " was removed.\nShould I delete the "
								+ classFileName + " file from your disk?",
								"Delete class?", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (choice == JOptionPane.YES_OPTION) {
					String folder = ClassUtils.getFirstClassPathFolder();
					File classFile = new File(folder + File.separator + classFileName);
					if (classFile.exists()) {
						classFile.deleteOnExit();
					}
				}
			}
		}

		// Handles Observable updates from the model.
		public void update(Observable o, Object arg) {
			updateLabel();
			updateBackground();
		}

		private void updateBackground() {
			if (className.equals(model.getWinningClassName())) {
				setBackground(Color.YELLOW);
				if (center != null) {
					center.setBackground(Color.YELLOW);
				}
			} else {
				setBackground(oldBackground);
				if (center != null) {
					center.setBackground(oldBackground);
				}
			}
		}

		private void updateBorder() {
			Color bgColor = panel.getColor(className);
			if (backgroundColors.isSelected() && bgColor != null) {
				// border.setTitleColor(bgColor);
				border.setBorder(BorderFactory.createLineBorder(bgColor, 2));
			} else {
				// border.setTitleColor(Color.BLACK);
				border.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
			}
			setBorder(border);
			validate();
			repaint();
		}

		// Updates the text status about this critter class. 
		private void updateLabel() {
			int count = model.getCount(className);
			int kills = model.getKills(className);
			int food = model.getFoodEaten(className);
			int foodPenalty = model.getFoodPenalty(className);
			int deaths = model.getDeaths(className);
			int total = count + kills + food;
			String status = "<html>" + 
					StringUtils.padNumber(count, 4, true) + " alive (-" + StringUtils.padNumber(deaths, 2, true) + ")<br>" +
			        "+" + StringUtils.padNumber(kills, 3, true) + " kills<br>" +
			        "+" + StringUtils.padNumber(food, 3, true)  + " food<br>";
			// "-" + padString(deaths, 3, true) + " deaths<br>" +
			if (total > 999) {
				status += "<b>= ZOMG!!!1</b>";
			} else {
				status += "<b>=" + StringUtils.padNumber(total, 3, true)
						+ " TOTAL</b>";
			}

			if (foodPenalty > 0) {
				status += "<br><font color='#990000'><b>"
						+ StringUtils.padNumber(foodPenalty, 4, true)
						+ " sleep</b></font>";
			}
			status += "</html>";
			statusLabel.setText(status);
		}
	}

	public static String truncateClassName(String className, int length) {
		if (className.length() <= length) {
			return className;
		} else {
			return className.substring(0, length) + "~";
		}
	}
}

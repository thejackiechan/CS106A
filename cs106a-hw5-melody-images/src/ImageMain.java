/*
 * CS 106A Image Algorithms
 *
 * This instructor-provided file implements the graphical user interface (GUI)
 * for the Image Algorithms program and allows you to test the behavior
 * of your Melody class.
 *
 * Author : Marty Stepp and Keith Schwarz
 * Version: Tue 2015/05/09
 * - initial version for 15sp; modified to use interface
 * 
 * based on Image Steganography GUI written by Keith Schwarz
 *
 * Your program should work properly with an UNMODIFIED version of this file.
 * If you want to modify this file for testing or for fun, that is your choice,
 * but when we grade your program we will do so with the original unmodified
 * version of this file, so your code must still work properly with that code.
 *
 * This file and its contents are copyright (C) Stanford University and Marty Stepp,
 * licensed under Creative Commons Attribution 2.5 License.  All rights reserved.
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import acm.graphics.*;
import acm.gui.*;
import acm.program.*;
import acm.util.*;
import stanford.cs106.diff.*;
import stanford.cs106.gui.*;
import stanford.cs106.io.*;
import stanford.cs106.util.*;

public class ImageMain implements ActionListener {
	/*
	 * List of all image algorithms. If you want to add more, add a String to
	 * this array and code to the actionPerformed method.
	 * You might also want to add a 32x32 px .gif icon to the res/icons folder
	 * to match your algorithm's name. For example, if your algorithm is named
	 * Do Funky Stuff, you can add an icon named res/icons/do-funky-stuff.gif.
	 */
	private static final String[] IMAGE_ALGORITHMS = {
			"Load Image",
			"Save Image",
			"Diff Images",
			"Grayscale",
			"Negative",
			"Rotate Left",
			"Rotate Right",
			"Translate",
			"Blur",
			"Mystery"
	};
	
	/*
	 * A set of buttons that shouldn't be enabled/disabled when an image
	 * is loaded from a file.
	 */
	private static final Set<String> UTILITY_BUTTONS = new HashSet<String>(
			Arrays.asList(IMAGE_ALGORITHMS).subList(0, 1));

	/* Canvas initial size. */
	private static final int CANVAS_WIDTH = 400;
	private static final int CANVAS_HEIGHT = 300;

	/* Valid file extensions for image types that we can write. */
	private static final String[] SAVE_IMAGE_EXTENSIONS = new String[] { "png", "bmp", "wbmp" };

	/* Valid file extensions for images that we can read. */
	private static final String[] LOAD_IMAGE_EXTENSIONS = new String[] { "png", "bmp", "wbmp",
			"jpg", "gif", "jpeg" };

	/*
	 * The directory in which resource files are found in the project; set to
	 * "." if resources are in the same dir as code files.
	 */
	private static final String RESOURCE_FOLDER = "res" + File.separator + "images";
	private static final String ICONS_FOLDER = "res" + File.separator + "icons";

	/**
	 * Runs the overall program.
	 */
	public static void main(String[] args) {
		ImageMain main = new ImageMain();
		main.init();
		main.run();
	}

	// The actual drawing and picture canvas, and other fields
	private PictureCanvas canvas;
	private ImageAlgorithmsInterface imageAlgorithms;
	private JFrame frame;
	private JLabel statusLabel;
	private JPanel centerPane;
	private List<JButton> imageAlgorithmButtons;

	/**
	 * Constructs the overall GUI.
	 */
	public ImageMain() {
		imageAlgorithmButtons = new ArrayList<JButton>();
		GuiUtils.setSystemLookAndFeel();
	}

	/**
	 * Responds to interactor events (clicks on buttons).
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand().intern();
		if (cmd == "Load Image") {
			loadImage();
		} else if (cmd == "Save Image") {
			saveImage();
		} else if (cmd == "Diff Images") {
			diffImages();
		} else if (cmd == "Grayscale") {
			if (canvas != null && canvas.getImage() != null) {
				imageAlgorithms.grayscale(canvas.getImage());
				canvas.updateImage();
				statusLabel.setText("Grayscale filter applied.");
			}
		} else if (cmd == "Negative") {
			if (canvas != null && canvas.getImage() != null) {
				imageAlgorithms.negative(canvas.getImage());
				canvas.updateImage();
				statusLabel.setText("Negative filter applied.");
			}
		} else if (cmd == "Rotate Left") {
			if (canvas != null && canvas.getImage() != null) {
				imageAlgorithms.rotateLeft(canvas.getImage());
				canvas.updateImage();
				statusLabel.setText("Rotate left filter applied.");
			}
		} else if (cmd == "Rotate Right") {
			if (canvas != null && canvas.getImage() != null) {
				imageAlgorithms.rotateRight(canvas.getImage());
				canvas.updateImage();
				statusLabel.setText("Rotate right filter applied.");
			}
		} else if (cmd == "Translate") {
			if (canvas != null && canvas.getImage() != null) {
				int dx = readInteger("dx?");
				int dy = readInteger("dy?");
				imageAlgorithms.translate(canvas.getImage(), dx, dy);
				canvas.updateImage();
				statusLabel.setText("Translate filter applied.");
			}
		} else if (cmd == "Blur") {
			if (canvas != null && canvas.getImage() != null) {
				imageAlgorithms.blur(canvas.getImage());
				canvas.updateImage();
				statusLabel.setText("Blur filter applied.");
			}
		} else if (cmd == "Mystery") {
			if (canvas != null && canvas.getImage() != null) {
				imageAlgorithms.mystery(canvas.getImage());
				canvas.updateImage();
				statusLabel.setText("Mystery filter applied.");
			}
		} else if (cmd != "Mystery" && cmd.startsWith("Mystery")) {
			if (canvas != null && canvas.getImage() != null) {
				runMysteryAlgorithm(cmd);
				canvas.updateImage();
				statusLabel.setText(cmd + " filter applied.");
			}
		}

		updateLayout();
		doEnabling();
	}
	
	/**
	 * Initializes the overall GUI, setting up components, events, and layout as
	 * well as showing the window frame on the screen.
	 */
	public void init() {
		ResourceUtils.setResourceLoaderClass(this.getClass());
		createComponents();
		doLayout();
		doEnabling();
	}

	/**
	 * Shows the window frame on the screen. Precondition: init() has already
	 * been called.
	 */
	public void run() {
		frame.setVisible(true);
	}

	/*
	 * Sets up the graphical components in the window and event listeners.
	 */
	private void createComponents() {
		// disable caching so if we rewrite a file, we see the right version
		MediaTools.setCachingEnabled(false);
		imageAlgorithms = new ImageAlgorithms();

		frame = new JFrame("CS 106A Image Algorithms");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		statusLabel = new JLabel("Ready.");
		statusLabel.setBorder(BorderFactory.createEtchedBorder());
		canvas = new PictureCanvas();
	}
	
	/*
	 * Sets which buttons should be clickable based on whether or not an image
	 * file has been loaded yet.
	 */
	private void doEnabling() {
		boolean loaded = (canvas != null && canvas.getImage() != null);
		for (JButton button : imageAlgorithmButtons) {
			button.setEnabled(loaded);
		}
	}
	
	/*
	 * Performs layout of the components within the graphical window. Also
	 * resizes the window snugly and centers it on the screen.
	 */
	private void doLayout() {
		// add the left toolbar of image manipulation algorithm buttons
		Container bar = new JToolBar(JToolBar.VERTICAL);
		// bar.setLayout(new GridLayout(0, 1));
		// Container bar = new JPanel(new GridLayout(2, 6));
		String iconFolder = ICONS_FOLDER + File.separator;
		String mnemonicsUsed = "";
		List<JButton> buttons = new ArrayList<JButton>();
		for (String algorithm : getImageAlgorithms()) {
			String icon = algorithm.toLowerCase().replace(" ", "-") + ".gif"; // "Load Image"
																				// ->
																				// "load-image.gif"
			if (!ResourceUtils.fileExists(iconFolder + icon)) {
				icon = "unknown.gif";
			}

			// find unused mnemonic
			char mnemonic = ' ';
			for (int i = 0; i < algorithm.length(); i++) {
				char ch = algorithm.charAt(i);
				if (mnemonicsUsed.indexOf(Character.toUpperCase(ch)) < 0) {
					mnemonic = Character.toUpperCase(ch);
					break;
				}
			}
			
			JButton button = GuiUtils.createButton(algorithm, algorithm, iconFolder + icon, mnemonic, this);
			button.setHorizontalAlignment(JButton.LEFT);
			bar.add(button);
			buttons.add(button);
			if (!UTILITY_BUTTONS.contains(algorithm)) {
				imageAlgorithmButtons.add(button);
			}
		}
		
		// hack to make them all want to be the same width for nicer layout
		int maxWidth = 0;
		for (JButton button : buttons) {
			Dimension size = button.getPreferredSize();
			maxWidth = Math.max(maxWidth, size.width);
		}
		for (JButton button : buttons) {
			Dimension size = button.getPreferredSize();
			size.width = maxWidth;
			button.setPreferredSize(size);
		}

		centerPane = GuiUtils.createPanel(canvas);

		// perform overall layout on window frame
		frame.add(centerPane, BorderLayout.CENTER);
		frame.add(bar, BorderLayout.WEST);
		frame.add(statusLabel, BorderLayout.SOUTH);
		frame.pack();
		// frame.setResizable(false);
		GuiUtils.centerWindow(frame);
	}

	/*
	 * Pops up a "Diff Image" window to compare the pixels of two images to each
	 * other.
	 */
	private void diffImages() {
		JFileChooser fc = getFileChooser(LOAD_IMAGE_EXTENSIONS, ".png, .bmp, and .wbmp files");
		if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File imageFile2 = fc.getSelectedFile();
			try {
				Image image2 = ImageIO.read(imageFile2);
				Image image1 = canvas.getImage().getImage();
				new DiffImage(image1, image2);
			} catch (IOException ioe) {
				showErrorMessage("I/O error while reading image data from " + imageFile2.getName()
						+ ":\n" + ioe.getMessage());
			}
		}
	}

	/**
	 * Given an image, converts that image into a BufferedImage.
	 * This code is based on code taken from this source:
	 * http://www.javareference.com/jrexamples/viewexample.jsp?id=112
	 */
	private BufferedImage getBufferedImageFromImage(Image img) {
		// force the image to be loaded into memory
		img = new ImageIcon(img).getImage();

		// create a BufferedImage large enough to hold the result
		BufferedImage bufferedImage = new BufferedImage(img.getWidth(canvas),
				img.getHeight(canvas), BufferedImage.TYPE_INT_RGB);

		// get graphics context for the new BufferedImage and draw image onto it
		Graphics g = bufferedImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();   // clean up

		return bufferedImage;
	}

	/*
	 * Given a filename, returns the extension of that filename, or "" if it
	 * does not have one.
	 */
	private String getExtension(File filename) {
		// find the suffix; fail if none is found
		int lastDot = filename.getName().lastIndexOf('.');
		if (lastDot == -1) {
			return "";
		}
		return filename.getName().substring(lastDot);
	}

	/*
	 * Returns a JFileChooser that can read or write the specified file types.
	 */
	private JFileChooser getFileChooser(String[] extensions, String description) {
		String workingDir = "";
		try {
			workingDir = System.getProperty("user.dir") + File.separator + RESOURCE_FOLDER;
			if (!new File(workingDir).exists()) {
				workingDir = System.getProperty("user.dir");
			}
		} catch (SecurityException se) {
			// empty
		}
		JFileChooser fc = new JFileChooser(workingDir);
		fc.setFileFilter(GuiUtils.getExtensionFileFilter(description, extensions));
		return fc;
	}

	/*
	 * Returns a list of the names of every image manipulation algorithm
	 * available. This always includes the default algorithms as defined in the
	 * constant IMAGE_ALGORITHMS array above, along with any other methods found
	 * in the ImageAlgorithms class whose names begin with "mystery" that accept
	 * a GImage parameter and return a GImage result.
	 */
	private ArrayList<String> getImageAlgorithms() {
		ArrayList<String> algorithms = new ArrayList<String>();
		for (String algorithm : IMAGE_ALGORITHMS) {
			algorithms.add(algorithm);
		}
		try {
			ArrayList<String> mysteryMethods = new ArrayList<String>();
			for (Method method : ImageAlgorithms.class.getDeclaredMethods()) {
				String methodName = method.getName();
				Class<?> returnType = method.getReturnType();
				Class<?>[] parameterTypes = method.getParameterTypes();

				// if this method's name starts with "mystery",
				// it accepts a single parameter of type GImage,
				// and returns a result of type GImage,
				// then include it in the list
				if (!methodName.equals("mystery") && methodName.startsWith("mystery")
						&& returnType == GImage.class && parameterTypes.length == 1
						&& parameterTypes[0] == GImage.class) {
					String titleCase = methodName.substring(0, 1).toUpperCase()
							+ methodName.substring(1).toLowerCase();
					mysteryMethods.add(titleCase);
				}
			}
			// put them in sorted order by name (mystery1, then 2, 3, 4, ...)
			Collections.sort(mysteryMethods);
			algorithms.addAll(mysteryMethods);
		} catch (Exception e) {
			// empty (lots of thinsg can go wrong with reflection; ignore)
		}
		return algorithms;
	}

	/*
	 * Returns whether a given file extension is a valid file extension we can
	 * use when saving files.
	 */
	private boolean isLegalExtension(String extension) {
		// confirm it's one of .png, .bmp, or .wbmp, which are supported
		for (String legal : SAVE_IMAGE_EXTENSIONS) {
			if (legal.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Prompts the user to choose which image they want to load, then loads that
	 * image.
	 */
	private void loadImage() {
		// get a file chooser capable of choosing images
		JFileChooser fc = getFileChooser(LOAD_IMAGE_EXTENSIONS, "Image files");
		if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				// try setting the image; this might fail if we get a bad file
				File file = fc.getSelectedFile();
				canvas.setImage(new GImage(file.getAbsolutePath()));
				statusLabel.setText("Image loaded from " + file.getName() + ".");
				updateLayout();
			} catch (ErrorException ex) {
				showErrorMessage("Could not open that image:\n" + ex.getMessage());
			}
		}
	}

	/*
	 * Pops up dialog boxes asking the user to type an integer repeatedly until
	 * the user types a valid integer.
	 */
	private int readInteger(String prompt) {
		while (true) {
			try {
				String result = JOptionPane.showInputDialog(prompt);
				int num = Integer.parseInt(result);
				return num;
			} catch (NumberFormatException e) {
				// empty; re-prompt
			} catch (NullPointerException e) {
				// empty; re-prompt
			}
		}
	}

	/*
	 * Executes the "Mystery" algorithm method from ImageAlgorithms class,
	 * passing it the current GImage, and displaying the resulting image made.
	 * Uses reflection because we don't know exactly what methods the student
	 * will write based on how much extra stuff they want to add to the class.
	 */
	private void runMysteryAlgorithm(String name) {
		GImage source = canvas.getImage();

		// run the mystery method using reflection;
		// for example, if name is "mystery3", we want to run:
		// GImage result = imageManipulator.mystery3(source);
		try {
			Method mysteryMethod = ImageAlgorithms.class.getDeclaredMethod(name.toLowerCase(),
					new Class<?>[] { GImage.class });
			mysteryMethod.invoke(imageAlgorithms, source);
		} catch (Exception e) {
			showErrorMessage("Error trying to run method " + name.toLowerCase() + ":\n"
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * Pops up a file chooser dialog to select a file name to save the current
	 * image into, and saves it.
	 */
	private void saveImage() {
		JFileChooser fc = getFileChooser(SAVE_IMAGE_EXTENSIONS, ".png, .bmp, and .wbmp files");
		if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			saveImage(canvas.getImage(), fc.getSelectedFile());
		}
	}

	/*
	 * Given a GImage and a file, writes that GImage to the file.
	 */
	private void saveImage(GImage image, File file) {
		// Find the extension, if one exists. If not, pretend it's a png.
		int lastDot = file.getName().lastIndexOf('.');
		if (lastDot == -1) {
			file = new File(file.getAbsolutePath() + ".png");
		}

		// Get that extension
		String extension = getExtension(file.getAbsoluteFile());
		if (!isLegalExtension(extension)) {
			throw new ErrorException("Unsupported file format.");
		}

		if (file.exists()) {
			if (JOptionPane
					.showConfirmDialog(
							frame,
							"File already exists. Overwrite?\n(You probably shouldn't overwrite the instructor-provided images; save them with a different name)",
							"Overwrite?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
		}

		try {
			// turn the GImage into a BufferedImage
			BufferedImage bufferedImage = getBufferedImageFromImage(MediaTools.createImage(image
					.getPixelArray()));

			// Output to that file. The extension to use should first be trimmed
			// of its leading dot.
			ImageIO.write(bufferedImage, extension.substring(1), file);
			statusLabel.setText("Image saved to " + file.getName() + ".");
		} catch (ErrorException e) {
			showErrorMessage("An error occurred saving the image:\n" + e.getMessage());
		} catch (IOException e) {
			showErrorMessage("An error occurred saving the image:\n" + e.getMessage());
		}
	}

	/*
	 * Displays a popup message dialog box to display the given error message.
	 * Also puts the error message into the bottom status label.
	 */
	private void showErrorMessage(String text) {
		JOptionPane.showMessageDialog(frame, text, "Error", JOptionPane.ERROR_MESSAGE);
		statusLabel.setText("Error: " + text);
	}

	/*
	 * Called when the image is loaded or manipulated by algorithms. Causes the
	 * window to resize appropriately to fit its contents.
	 */
	private void updateLayout() {
		GImage image = canvas.getImage();
		if (image != null) {
			Dimension size = new Dimension((int) image.getWidth(), (int) image.getHeight());
			canvas.setPreferredSize(size);
			canvas.revalidate();
			centerPane.revalidate();
			frame.validate();
			frame.pack();
		}
	}

	/**
	 * A canvas that can display images into which secret messages can be
	 * hidden.
	 */
	private class PictureCanvas extends GCanvas {
		// stored image and its pixels
		private GImage image;
		private int[][] pixels;

		/**
		 * Creates a new, empty picture canvas.
		 */
		public PictureCanvas() {
			setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
			setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
		}

		/**
		 * Returns the underlying image.
		 */
		public GImage getImage() {
			return image;
		}
		
		/**
		 * Redraws the image and updates the screen appearance.
		 */
		public void updateImage() {
			if (image != null) {
				pixels = image.getPixelArray();
				Dimension size = new Dimension((int) image.getWidth(), (int) image.getHeight());
				setPreferredSize(size);
				invalidate();
				validate();
				updateLayout();
			}
		}

		/**
		 * Sets the stored image. If null, no image is displayed.
		 */
		public void setImage(GImage newImage) {
			// remove any existing images
			if (image != null) {
				remove(image);
			}

			// set the image
			image = newImage;

			// add it, if it exists
			if (image != null) {
				// image.setBounds(0, 0, getWidth(), getHeight());
				MediaTracker mt = new MediaTracker(this);
				try {
					mt.addImage(image.getImage(), 0);
					mt.waitForAll();
				} catch (InterruptedException ie) {
					// empty
				}
				add(image);
				updateImage();
			}

			// listen for mouse movement events
			addMouseMotionListener(new MouseAdapter() {
				public void mouseMoved(MouseEvent event) {
					updateStatus(event.getX(), event.getY());
				}

				public void mouseDragged(MouseEvent event) {
					updateStatus(event.getX(), event.getY());
				}
			});
		}

		/*
		 * Returns true if the given x/y coordinate is within the bounds of the
		 * current image. False by default if no image has yet been chosen.
		 */
		private boolean inBounds(int x, int y) {
			if (pixels == null) {
				return false;
			} else {
				int height = pixels.length;
				int width = height <= 0 ? 0 : pixels[0].length;
				return x >= 0 && x < width && y >= 0 && y < height;
			}
		}

		/*
		 * Sets the southern status label based on moving the mouse over a given
		 * (x, y) pixel. Shows that pixel's x/y coords and color.
		 */
		private void updateStatus(int x, int y) {
			if (inBounds(x, y)) {
				String status = "(x=" + x + ", y=" + y + ")";
				int pixel = pixels[y][x];
				status += " (R=" + GImage.getRed(pixel) + ", G=" + GImage.getGreen(pixel) + ", B="
						+ GImage.getBlue(pixel) + ")";
				statusLabel.setText(status);
			} else {
				statusLabel.setText(" ");
			}
		}
	}
}

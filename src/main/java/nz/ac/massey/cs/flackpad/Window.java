package nz.ac.massey.cs.flackpad;

import java.awt.Image;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

class Window {
	private String name = "FlackPad";
	private JFrame frame;
	private WindowListener winListener;
	private Config config;
	private FileMIME MIME = new FileMIME();
	private FileIO fileIO;

	private MenuBar menuBar;
	private TextArea textArea;
	private ScrollPane scrollPane;
	
	private ThemeDialog themeDialog;

	private boolean saved = true;
	private File file;
	private String fileName = "Untitled";

	Window() {
		// Create JFrame and set title
		frame = new JFrame(fileName + " - " + name);

		// Add icons
		List<Image> iconList = new ArrayList<Image>();
		iconList.add(new ImageIcon(getClass().getResource("/icons/16x16.png")).getImage());
		iconList.add(new ImageIcon(getClass().getResource("/icons/32x32.png")).getImage());
		iconList.add(new ImageIcon(getClass().getResource("/icons/64x64.png")).getImage());
		iconList.add(new ImageIcon(getClass().getResource("/icons/128x128.png")).getImage());
		frame.setIconImages(iconList);

		// Add window listener
		winListener = new WinListener(this);
		frame.addWindowListener(winListener);

		// Add menu bar
		menuBar = new MenuBar(this);
		frame.add(menuBar);
		frame.setJMenuBar(menuBar);

		// Add text area in a scroll pane
		textArea = new TextArea(this);
		scrollPane = new ScrollPane(textArea);
		
		// Add FileIO instance
		fileIO = new FileIO(this);

		// Get config
		config = new Config(frame);

		// Set font and colours for textarea, scrollpane, etc.
		textArea.setTheme(config);
		scrollPane.setTheme(config);

		frame.add(scrollPane);
		
		// Enable/disable menu items
		updateUndoRedoEnable();
		updateCCDEnable();
		updateZoomEnable();

		// Add key bindings to instance
		new KeyBinder(this);
		
		// Get theme dialog instance
		themeDialog = new ThemeDialog(frame);

		// Set window size, visibility and to not close
		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);

		frame.requestFocus();
		textArea.grabFocus();
		

	}

	void newDoc() {
		int saveChoice = this.isSaved() ? 1 : Dialogs.saveWarning(fileName, frame);
		int saved = FileIO.SAVED;

		if (saveChoice == JOptionPane.CANCEL_OPTION || saveChoice == JOptionPane.CLOSED_OPTION) {
			return;
		}
		
		if (saveChoice == JOptionPane.YES_OPTION) {
			saved = fileIO.save();
		}

		if (saved == FileIO.SAVED) {
			setFile(null);
			textArea.setText("");
			setSaved(true);
			textArea.discardAllEdits();

			// Enable/disable menu items
			updateUndoRedoEnable();
			updateCCDEnable();
		}
	}

	void exit() {
		int saveChoice = this.isSaved() ? 1 : Dialogs.saveWarning(fileName, frame);
		int saved = FileIO.SAVED;

		if (saveChoice == JOptionPane.CANCEL_OPTION || saveChoice == JOptionPane.CLOSED_OPTION) {
			return;
		}
		
		if (saveChoice == JOptionPane.YES_OPTION) {
			saved = fileIO.save();
		}

		if (saved == FileIO.SAVED) {
			config.saveConfigFile();
			frame.dispose();
		}
	}

	void toggleTheme() {
		String currentThemeName = config.getTheme().getThemeName();
		config.setTheme(currentThemeName == "dark" ? "light" : "dark");
		textArea.setTheme(config);
		scrollPane.setTheme(config);
	}
	
	void openThemeDialog() {
		themeDialog.setVisible(true);
	}

	void gutterToggle() {
		scrollPane.setLineNumbersEnabled(!scrollPane.getLineNumbersEnabled());
	}
	
	void undo() {
		textArea.undoLastAction();
	}
	
	void redo() {
		textArea.redoLastAction();
	}

	void addTimeAndDate() {
		textArea.addTimeAndDate();
	}

	void zoomIn() {
		textArea.zoomIn();
		updateZoomEnable();
	}

	void zoomOut() {
		textArea.zoomOut();
		updateZoomEnable();
	}

	void resetZoom() {
		textArea.resetZoom();
		updateZoomEnable();
	}
	
	void updateUndoRedoEnable() {
		menuBar.setUndoEnabled(textArea.canUndo());
		menuBar.setRedoEnabled(textArea.canRedo());
	}
	
	void updateCCDEnable() {
		menuBar.setCCDEnabled(textArea.getSelectedText() != null);
	}
	
	void updateZoomEnable() {
		menuBar.setZoomInEnabled(textArea.getZoomPercentage() < 1000);
		menuBar.setZoomOutEnabled(textArea.getZoomPercentage() > 10);
		menuBar.setResetZoomEnabled(textArea.getZoomPercentage() != 100);
		
		if (textArea.getZoomPercentage() != 100) {
			menuBar.setInformationBarZoomText(Integer.toString(textArea.getZoomPercentage()) + "%");
			menuBar.setInformationBarZoomVisible(true);
		} else {
			menuBar.setInformationBarZoomVisible(false);
		}
	}

	void setInformationBar(String val) {
		menuBar.setInformationBarText(val);
	}

	void updateInformationBar() {
		try {
			// Update text
			menuBar.setInformationBarText(Integer.toString(getText().length()) + " | Char");
		} catch (NullPointerException e) {
			menuBar.setInformationBarText("0");
		}
	}

	JTextField getFindField() {
		return menuBar.getFindField();
	}

	JButton getFindClose() {
		return menuBar.getFindClose();
	}

	void showFindBar() {
		menuBar.showFindBar();
	}

	void hideFindBar() {
		menuBar.hideFindBar();
	}

	JFrame getFrame() {
		return frame;
	}

	TextArea getTextArea() {
		return textArea;
	}

	boolean isSaved() {
		return saved;
	}

	void setSaved(boolean saved) {
		frame.setTitle((saved ? "" : "*") + fileName + " - " + name);
		this.saved = saved;
	}

	File getFile() {
		return file;
	}
	
	FileIO getFileIO() {
		return fileIO;
	}

	void setFile(File file) {
		// Set file and file name used in window title
		this.file = file;
		fileName = file == null ? "Untitled" : file.getName();
		// Set syntax highlighting and info bar text based on file type
		textArea.setSyntaxEditingStyle(MIME.getFileStyle(file));
		menuBar.setInformationBarFileText(MIME.getFileStyle(file));
		// Clear undo/redo history
		textArea.discardAllEdits();
	}

	String getFileName() {
		return fileName;
	}

	String getAppName() {
		return name;
	}

	void setText(String text) {
		textArea.setText(text);
	}

	String getText() {
		return textArea.getText();
	}
	
	ImageIcon getIcon() {
		return new ImageIcon(getClass().getResource("/icons/64x64.png"));
	}
}
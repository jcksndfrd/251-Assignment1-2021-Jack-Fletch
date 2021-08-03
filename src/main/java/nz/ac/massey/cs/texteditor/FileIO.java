package nz.ac.massey.cs.texteditor;

import java.io.*;
import javax.swing.*;

public final class FileIO {
	
	private FileIO() {
		throw new UnsupportedOperationException();
	}

	public static void open(TextEditor frame) {
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			loadFile(fileChooser.getSelectedFile().getAbsoluteFile(), frame);
		};
		frame.setFile(fileChooser.getSelectedFile().getAbsoluteFile());
		frame.setSaved(true);
	}
	
	public static void save(TextEditor frame) {
		if (frame.getFile() != null) {
			saveFile(frame.getFile(), frame);
			frame.setSaved(true);
		} else {
			saveAs(frame);
		}
	}

	public static void saveAs(TextEditor frame) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save As");
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			saveFile(fileChooser.getSelectedFile().getAbsoluteFile(), frame);
			frame.setFile(fileChooser.getSelectedFile().getAbsoluteFile());
			frame.setSaved(true);
		}
	}

	private static void loadFile(File file, TextEditor frame) {
		frame.getTextArea().setText("");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				frame.getTextArea().append(line);
				if ((line = reader.readLine()) != null) frame.getTextArea().append("\n");
			}
		} catch (IOException e) {
			Dialogs.error("Something went wrong when loading that file", frame);
		}
	}
	
	private static void saveFile(File file, TextEditor frame) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
			writer.write(frame.getTextArea().getText());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Dialogs.error("Something went wrong when saving that file", frame);
		}
	}

}
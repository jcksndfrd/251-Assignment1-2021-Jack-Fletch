package nz.ac.massey.cs.flackpad;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JTextArea;

class Printer implements Printable {
	private PrinterJob printerJob;
	JTextArea textArea;
	Font font;

	Printer() {
		printerJob = PrinterJob.getPrinterJob();

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
	}

	void printString(String text, Font font) throws PrinterException {
		textArea.setText(text);
		textArea.setFont(font);

		printerJob.setPrintable(this);
		if (printerJob.printDialog()) {
			printerJob.print();
		}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
			return NO_SUCH_PAGE;
		}

		Graphics2D graphics2D = (Graphics2D) graphics;
		graphics2D.translate(pageFormat.getImageableX() + 10, pageFormat.getImageableY() + 10);
		textArea.setBounds(graphics2D.getClipBounds());
		textArea.paint(graphics2D);

		return PAGE_EXISTS;
	}
}
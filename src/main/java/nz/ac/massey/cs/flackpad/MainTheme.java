package nz.ac.massey.cs.flackpad;

import java.awt.Color;
import java.io.IOException;

import org.fife.ui.rsyntaxtextarea.Theme;
class MainTheme {
	static String theme;
	static Color textBackground;
	static Color gutterBackground;
	static Color gutterLineNumber;
	static Color gutterBorder;
	static Color textForeground;
	static Color currentLineHightlightBackground;
	static Color selectionHighlight;
	static Color caretForeground;
	
	public MainTheme(String theme) {
		MainTheme.theme = theme;
	}

	public void setTheme(TextArea area) {
		setSyntaxTheme(area);
		// Set theme colours
		switch (theme) {
		case "dark":
			setDarkTheme();
			break;
		case "light":
			setLightTheme();
			break;
		default:
			// Incorrect theme parameter
			throw new IllegalArgumentException("Unknown theme " + theme + ". Please choose from \"dark\" or \"light\"");
		}
	}
	public void setThemeString(String val) {
		theme = val;
	}
	public String getThemeString() {
		return theme;
	}
	private void setLightTheme() {
		textBackground = Color.white;
		textForeground = Color.decode("#383838");
		gutterBackground = Color.white;
		gutterBorder =  Color.decode("#383838");
		gutterLineNumber = Color.decode("#990000");
		currentLineHightlightBackground = Color.decode("#eeeeee");
		selectionHighlight = Color.decode("#00ff00");
		caretForeground = Color.decode("#383838");
	}

	private void setDarkTheme() {
		textBackground = Color.decode("#333333");
		textForeground = Color.decode("#aaaaaa");
		gutterBackground = Color.decode("#383838");
		gutterBorder = Color.decode("#770BD8");	
		gutterLineNumber = Color.decode("#aaaaaa");
		currentLineHightlightBackground = Color.decode("#444444");
		selectionHighlight = Color.decode("#770BD8");
		caretForeground = Color.decode("#eeeeee");		
	}
	
	private void setSyntaxTheme(TextArea area) {
		switch (theme) {
			case "light":
			   try {
				      Theme currenttheme = Theme.load(getClass().getResourceAsStream(
				            "/org/fife/ui/rsyntaxtextarea/themes/" +  "default" + ".xml"));				      
				      
				      currenttheme.apply(area);
				   } catch (IOException ioe) {
				      ioe.printStackTrace();
				   }
			   break;
			case "dark":
			   try {
				      Theme currenttheme = Theme.load(getClass().getResourceAsStream(
				            "/org/fife/ui/rsyntaxtextarea/themes/" + "dark" + ".xml"));
				      currenttheme.apply(area);
				   } catch (IOException ioe) {
				      ioe.printStackTrace();
				   }	
			   break;
		}

	}

}
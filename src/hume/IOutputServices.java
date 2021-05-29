/**
 * 
 */
package hume;

import java.awt.Image;

/**
 * @author eckhart
 *
 */
public interface IOutputServices {
	void addView(IView view);
	void removeView(IView view);
	void repaint(IView view);
	
	// logging
	void addLogText(String str);
	void clearLogText();
	void setLogLevel(short level);
	void addLogText(String str, short level);
	
	// report generation
	void addChapter(String heading);
	void addText(String htmlText);
	void addImage(Image image);

}

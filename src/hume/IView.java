/**
 * 
 */
package hume;

import java.awt.Graphics2D;

/**
 * @author eckhart
 *
 */
public interface IView {
	String getName();
	void dataUpdatedNotification();
	void paint(Graphics2D g);
}


package ponggame;

import java.awt.Dimension;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
	
	GamePanel panel;
	
	GameFrame() {
	    panel = new GamePanel();
	    panel.setPreferredSize(new Dimension(600, 400));

	    this.add(panel);
	    this.pack(); // ← 超重要
	    this.setTitle("Pong Game");
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setVisible(true);

	    panel.requestFocusInWindow();
	}
}

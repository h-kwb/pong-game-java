package main;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
	
	GamePanel panel;
	
	GameFrame() {
		panel = new GamePanel();
		this.add(panel);
		this.setTitle("Pong Game");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		panel.requestFocusInWindow();
	}
}

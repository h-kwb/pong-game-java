package ponggame;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Paddle {
	
	int x;
	int y;
	int width = 10;
	int height = 100;
	int speed = 10;
	
	Paddle(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void draw(Graphics g) {
		g.fillRect(x, y, width, height);
	}
	
	public void moveUp() {
		y -= speed;
	}
	
	public void moveDown() {
		y += speed;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
}

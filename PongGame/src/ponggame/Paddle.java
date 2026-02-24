package ponggame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Paddle {

    int x;
    int y;
    int width;
    int height;
    int speed = 10;

    Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
    	g.setColor(Color.BLACK);
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
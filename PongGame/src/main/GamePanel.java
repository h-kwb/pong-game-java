package main;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
	
	public static final int TITLE = 0;
	public static final int DIFFICULTY = 1;
	public static final int PLAY = 2;
	
	final int MAX_SPEED = 10;
	
	int gameState = TITLE;
	
	int aiSpeed = 4;
	int difficulty = 1;
	
	int ballX = 400;
	int ballY = 300;
	int ballXSpeed = 3;
	int ballYSpeed = 3;
	
	Timer timer;
	
	Paddle leftPaddle;
	Paddle rightPaddle;
	
	boolean wPressed = false;
	boolean sPressed = false;	
	
	int leftScore = 0;
	int rightScore = 0;
	
	boolean gameOver = false;
	String winnerText = "";
	
	// コンストラクタ
	GamePanel() {
		
		leftPaddle = new Paddle(20, 250);
		rightPaddle = new Paddle(760, 250);
		
		this.setFocusable(true);
		
		// KeyListener
		this.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent e) {
				
				int key = e.getKeyCode();
				
				if (gameState == TITLE) {
					
					if(key == KeyEvent.VK_ENTER) {
						gameState = DIFFICULTY;
					}
				}
				else if (gameState == DIFFICULTY) {
					
					if (key == KeyEvent.VK_1) {
						aiSpeed = 2;
						gameState = PLAY;
					}
					if (key == KeyEvent.VK_2) {
						aiSpeed = 4;
						gameState = PLAY;
					}
					if (key == KeyEvent.VK_3) {
						aiSpeed = 6;
						gameState = PLAY;
					}
				}
				
				else if (gameState == PLAY) {
					
				}
				
				if(e.getKeyCode() == KeyEvent.VK_W) {
					wPressed = true;
				}
				
				if(e.getKeyCode() == KeyEvent.VK_S) {
					sPressed = true;
				}
				
				// Rキーでリスタート機能追加
				if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
					restartGame();
				}
			
			}
			public void keyReleased(KeyEvent e) {
				
				if(e.getKeyCode() == KeyEvent.VK_W) {
					wPressed = false;
				}
				
				if(e.getKeyCode() == KeyEvent.VK_S) {
					sPressed = false;
				}
			}
		});
		
		timer = new Timer(10, this);
		timer.start();
		
		this.requestFocus();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (gameState == TITLE) {
			drawTitleScreen(g);
		} else if (gameState == DIFFICULTY) {
			drawDifficultyScreen(g);
		} else if (gameState == PLAY) {
			drawGame(g);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// GAME OVER中はボール停止
		if (gameOver) {
			repaint();
			return;
		}
				
		ballX += ballXSpeed;
		ballY += ballYSpeed;
		
		// 上下の壁
		if (ballY <= 0 || ballY >= getHeight() - 20) {
			ballYSpeed *= -1;
		}
		
		// 得点機能の追加
		// 左側に抜けた -> 右の得点
		if (ballX <= 0) {
			rightScore++;
			checkWinner();
			resetBall();
		}
		
		// 右側に抜けた -> 左の得点
		if (ballX >= getWidth() - 20) {
			leftScore++;
			checkWinner();
			resetBall();
		}
		
		// パドル操作（毎フレーム）
		if (wPressed && leftPaddle.y > 0) {
			leftPaddle.moveUp();
		}
		if (sPressed && leftPaddle.y < getHeight() - leftPaddle.height) {
			leftPaddle.moveDown();
		}
		
		// ボールの当たり判定用Rect
		Rectangle ballRect = new Rectangle(ballX, ballY, 20, 20);
		
		// パドルとの衝突反射(left)
		if (ballRect.intersects(leftPaddle.getBounds())) {
			
			ballXSpeed = Math.abs(ballXSpeed);
			
			int paddleCenterY = leftPaddle.y + leftPaddle.height / 2;
			int ballCenterY = ballY + 10;
			
			int distance = ballCenterY - paddleCenterY;
			
			double normalized = (double)distance / (leftPaddle.height / 2);
			
			ballYSpeed = (int)(normalized * 5);
			
			// speed +
			if (Math.abs(ballXSpeed) < MAX_SPEED) {
				ballXSpeed = (int)(ballXSpeed * 1.1);
			}
		}
		
		// パドルとの衝突反射(right)
		if (ballRect.intersects(rightPaddle.getBounds())) {
			
			ballXSpeed = -Math.abs(ballXSpeed);
			
			int paddleCenterY = leftPaddle.y + leftPaddle.height / 2;
			int ballCenterY = ballY + 10;
			
			int distance = ballCenterY - paddleCenterY;
			
			double normalized = (double)distance / (leftPaddle.height / 2);
			
			ballYSpeed = (int)(normalized * 5);
			
			// speed +
			if (Math.abs(ballXSpeed) < MAX_SPEED) {
				ballXSpeed = (int)(ballXSpeed * 1.1);
			}
		}
		
		// CPU設定
		if (ballXSpeed > 0) {
			int paddleCenter = rightPaddle.y + rightPaddle.height / 2;
			int ballCenter = ballY + 10;
			
			if (ballCenter < paddleCenter) {
				rightPaddle.y -= aiSpeed;
				
			}
			if (ballCenter > paddleCenter) {
				rightPaddle.y += aiSpeed;
			}
		}
		repaint();
	}
	
	// リセット後のボールの配置
	public void resetBall() {
		
		ballX = getWidth() / 2;
		ballY = getHeight() / 2;
		
		ballXSpeed = (Math.random() < 0.5) ? 3 : -3;
		ballYSpeed = (Math.random() < 0.5) ? 3 : -3;
	}
	
	// 勝敗判定メソッド
	public void checkWinner() {
		if (leftScore >= 3) {
			gameOver = true;
			winnerText = "PLAYER WINS!";
		}
		
		if (rightScore >= 3) {
			gameOver = true;
			winnerText = "YOU LOSE!";
		}
	}
	
	// Restart処理
	public void restartGame() {
		leftScore = 0;
		rightScore = 0;
		gameOver = false;
		
		resetBall();
	}
	
	// タイトル画面
	public void drawTitleScreen(Graphics g) {
		g.setFont(new Font("Arial", Font.BOLD, 50));
		g.drawString("PONG GAME", 200, 200);
		
		g.setFont(new Font("Arial", Font.PLAIN, 30));
		g.drawString("Press ENTER", 240, 300);
	}
	
	// 難易度選択画面
	public void drawDifficultyScreen(Graphics g) {
		g.setFont(new Font("Arial", Font.BOLD, 40));
		g.drawString("SELCT DIFFICULTY", 150, 150);
		
		g.setFont(new Font("Arial", Font.PLAIN, 30));
		g.drawString("1 : EASY", 250, 250);
		g.drawString("2 : NORMAL", 250, 300);
		g.drawString("3 : HARD", 250, 350);
	}
	
	// 通常ゲーム画面
	public void drawGame(Graphics g) {
		
		for (int i = 0; i < getHeight(); i += 30) {
			g.fillRect(getWidth() / 2 - 2, i, 4, 20);
		}

		leftPaddle.draw(g);
		rightPaddle.draw(g);
		
	    g.fillOval(ballX, ballY, 20, 20);

	    g.setFont(new Font("Arial", Font.BOLD, 40));
	    g.drawString(String.valueOf(leftScore), 300, 50);
	    g.drawString(String.valueOf(rightScore), 460, 50);

	    if (gameOver) {
	        g.setFont(new Font("Arial", Font.BOLD, 50));
	        g.drawString(winnerText, 220, 300);

	        g.setFont(new Font("Arial", Font.PLAIN, 20));
	        g.drawString("Press R to Restart", 300, 350);
	    }
	}
}

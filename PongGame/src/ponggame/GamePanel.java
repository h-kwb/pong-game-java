package ponggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	public static final int PAUSE = 3;
	
	final int MAX_SPEED = 10;
	
	int gameState = TITLE;
	
	// score機能
	int[] tennisScores = {0, 15, 30, 40};
	int leftScoreIndex = 0;
	int rightScoreIndex = 0;
	int advantage = 0;
	
	// Games 得点
	int leftGames = 0;
	int rightGames = 0;
	final int GAME_WIN = 6;
	
	// cpu速度
	int aiSpeed = 4;
	int difficulty = 1;
	
	int ballX = GamePanel.WIDTH / 2;
	int ballY = GamePanel.HEIGHT / 2;
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
	
	// serve管理変数
	boolean leftServe = true;
	
	// 選択カーソル用変数
	int pauseSelect = 0;
	
	// コンストラクタ
	GamePanel() {
		
	    leftPaddle = new Paddle(20, 50, 10, 50);
	    rightPaddle = new Paddle(580, 50, 10, 50);
		
		this.setFocusable(true);
		
		// KeyListener
		this.addKeyListener(new KeyAdapter() {
			
			// Key操作
			public void keyPressed(KeyEvent e) {
				
				int key = e.getKeyCode();
				
				switch(gameState) {
				
				case TITLE:
					
					if(key == KeyEvent.VK_ENTER) {
						gameState = DIFFICULTY;
					}
					break;
					
				case DIFFICULTY:
					
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
					break;
					
				case PLAY:
					
					if (gameOver && key == KeyEvent.VK_R) {
						restartGame();
						gameState = TITLE;
					}
					
					if (!gameOver) {
						if (key == KeyEvent.VK_P) {
							gameState = PAUSE;
						}
						if (key == KeyEvent.VK_W) {
							wPressed = true;
						}
						if (key == KeyEvent.VK_S) {
							sPressed = true;
						}
					}
					break;
					
				case PAUSE:
					
					if (key == KeyEvent.VK_UP) {
						pauseSelect = 0;
					}
					if (key == KeyEvent.VK_DOWN) {
						pauseSelect = 1;
					}
					
					if (key == KeyEvent.VK_ENTER) {
						
						if (pauseSelect == 0) {
							gameState = PLAY;
						}
						else if (pauseSelect == 1) {
							restartGame();
							gameState = TITLE;
						}
					}
					break;
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
	public void setBounds(int x, int y, int width, int height) {
	    super.setBounds(x, y, width, height);

	    int paddleWidth  = width / 40;
	    int paddleHeight = height / 5;

	    leftPaddle.width = paddleWidth;
	    leftPaddle.height = paddleHeight;
	    leftPaddle.x = 20;
	    leftPaddle.y = height/2 - paddleHeight/2;

	    rightPaddle.width = paddleWidth;
	    rightPaddle.height = paddleHeight;
	    rightPaddle.x = width - 20 - paddleWidth;
	    rightPaddle.y = height/2 - paddleHeight/2;
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
		} else if (gameState == PAUSE) {
			drawPauseScreen(g);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// GAME OVER中はボール停止
		if (gameOver) {
			repaint();
			return;
		}
		
		// pause処理
		if (gameState != PLAY) {
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
		if (ballX < -20) {
			if (ballXSpeed < 0) {
				scoreRight();
			}

			resetBall();
		}
		
		// 右側に抜けた -> 左の得点
		else if (ballX > getWidth()) {
			if (ballXSpeed > 0) {
				scoreLeft();
			}
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
			
			ballYSpeed = (int)(normalized * 4);
			
			// speed +
			if (Math.abs(ballXSpeed) < MAX_SPEED) {
				ballXSpeed = (int)(ballXSpeed * 1.4);
			}
		}
		
		// パドルとの衝突反射(right)
		if (ballRect.intersects(rightPaddle.getBounds())) {
			
			ballXSpeed = -Math.abs(ballXSpeed);
			
			int paddleCenterY = rightPaddle.y + rightPaddle.height / 2;
			int ballCenterY = ballY + 10;
			
			int distance = ballCenterY - paddleCenterY;
			
			double normalized = (double)distance / (rightPaddle.height / 2);
			
			ballYSpeed = (int)(normalized * 4);
			
			// speed +
			if (Math.abs(ballXSpeed) < MAX_SPEED) {
				ballXSpeed = (int)(ballXSpeed * 1.4);
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
	
	// リセット後のボールの配置 サーブ権追加
	public void resetBall() {
		
		ballX = getWidth() / 2;
		ballY = getHeight() / 2;
		
		if (leftServe) {
			ballXSpeed = 3;
		} else {
			ballXSpeed = -3;
		}
		
		ballYSpeed = 0;
		
	}
	
	// 勝敗判定メソッド
	public void checkWinner() {
		if (leftScore >= 3) {
			gameOver = true;
			winnerText = "YOU WIN!!";
		}
		
		if (rightScore >= 3) {
			gameOver = true;
			winnerText = "YOU LOSE!";
		}
	}
	
	// Restart処理
	public void restartGame() {
		leftScoreIndex = 0;
		rightScoreIndex = 0;
		advantage = 0;
		leftGames = 0;
		rightGames = 0;
		gameOver = false;
		
		resetBall();
		
		gameState = DIFFICULTY;
	}
	
	// タイトル画面
	public void drawTitleScreen(Graphics g) {

	    int w = getWidth();
	    int h = getHeight();

	    g.setFont(new Font("Arial", Font.BOLD, (int)(h * 0.10)));
	    g.drawString("PONG GAME", (int)(w * 0.25), (int)(h * 0.30));

	    g.setFont(new Font("Arial", Font.PLAIN, (int)(h * 0.05)));
	    g.drawString("Press ENTER", (int)(w * 0.355), (int)(h * 0.45));
	}
	
	// 難易度選択画面
	public void drawDifficultyScreen(Graphics g) {
		int size = getHeight() / 16;
		int cx = getWidth() / 2;
		int cy = getHeight() / 2; 

		g.setFont(new Font("Arial", Font.BOLD, size));
		g.drawString("SELECT DIFFICULTY", cx - size * 4, cy - 50);
		
		g.setFont(new Font("Arial", Font.PLAIN, size));
        g.drawString("1 : EASY", cx - 80, cy + 10);
        g.drawString("2 : NORMAL", cx - 80, cy + 50);
        g.drawString("3 : HARD", cx - 80, cy + 90);
	}
	
	// 通常ゲーム画面
	public void drawGame(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		
		if (gameOver) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, (int)(h * 0.12)));
			g.drawString(winnerText, (int)(w * 0.25), (int)(h * 0.4));
			
			g.setFont(new Font("Arial", Font.PLAIN, (int)(h * 0.05)));
			g.drawString("Press R to Restart", (int)(w * 0.3), (int)(h * 0.55));
			return;
		}
		
		for (int i = 0; i < getHeight(); i += 30) {
			g.fillRect(getWidth() / 2 - 2, i, 4, 20);
		}

		leftPaddle.draw(g);
		rightPaddle.draw(g);
		
	    g.fillOval(ballX, ballY, 20, 20);

	    g.setFont(new Font("Arial", Font.BOLD, (int)(h * 0.08)));
	    
	    Graphics2D g2 = (Graphics2D) g;
	    
	    if (leftScoreIndex == 3 && rightScoreIndex == 3) {
	    	if (advantage == 1) {
	    		g.drawString("AD", (int)(w * 0.20), (int)(h * 0.10));
	    		g.drawString("40", (int)(w * 0.70), (int)(h * 0.10));
	    	}
	    	else if(advantage == 2){
	            g.drawString("40", (int)(w * 0.20), (int)(h * 0.10));
	            g.drawString("AD", (int)(w * 0.70), (int)(h * 0.10));
	    	}
	    	else{

	            g2.setComposite(
	                java.awt.AlphaComposite.getInstance(
	                    java.awt.AlphaComposite.SRC_OVER, 0.4f
	                )
	            );

	            g.drawString("DEUCE", w, (int)(h * 0.05));

	            g2.setComposite(
	                java.awt.AlphaComposite.getInstance(
	                    java.awt.AlphaComposite.SRC_OVER, 1f
	                )
	            );
	        }
	    }
	    else{
	        g.drawString(String.valueOf(tennisScores[leftScoreIndex]), (int)(w * 0.20), (int)(h * 0.10));
	        g.drawString(String.valueOf(tennisScores[rightScoreIndex]), (int)(w * 0.70), (int)(h * 0.10));
	    }
	    
	    // Games: 表示
	    g.setFont(new Font("Arial", Font.PLAIN, (int)(h*0.04)));
	    g.drawString("Games: " + leftGames, (int)(w*0.05), (int)(h*0.05));
	    g.drawString("Games: " + rightGames, (int)(w*0.80), (int)(h*0.05));

	}
	
	// socres
	// left
	public void scoreLeft(){

	    if(leftScoreIndex == 3 && rightScoreIndex == 3){

	        if(advantage == 1){
	            winGameLeft();
	        }
	        else if(advantage == 2){
	            advantage = 0;
	        }
	        else{
	            advantage = 1;
	        }
	        return;
	    }

	    if(leftScoreIndex < 3){
	        leftScoreIndex++;
	    }
	    else{
	        winGameLeft();
	    }
	}
	
	// right
	public void scoreRight(){

	    if(leftScoreIndex == 3 && rightScoreIndex == 3){

	        if(advantage == 2){
	            winGameRight();
	        }
	        else if(advantage == 1){
	            advantage = 0;
	        }
	        else{
	            advantage = 2;
	        }
	        return;
	    }

	    if(rightScoreIndex < 3){
	        rightScoreIndex++;
	    }
	    else{
	        winGameRight();
	    }
	}
	
	// 勝敗判定
	// left
	public void winGameLeft() {
		leftGames++;
		
		// serve権変更
		leftServe = !leftServe;
		
		if (leftGames >= GAME_WIN) {
			gameOver = true;
			winnerText = "YOU WIN!!";
			return;
		}
		resetPoints();
		resetBall();
	}
	// right
	public void winGameRight() {
		rightGames++;
		
		// serve権変更
		leftServe = !leftServe;
		
		if (rightGames >= GAME_WIN) {
			gameOver = true;
			winnerText = "YOU LOSE!";
			return;
		}
		resetPoints();
		resetBall();
	}
	
	// point reset
	public void resetPoints() {
		leftScoreIndex = 0;
		rightScoreIndex = 0;
		advantage = 0;
	}
	
	// PAUSE画面描写
	public void drawPauseScreen(Graphics g)  {
		
		drawGame(g);
		
		int w = getWidth();
		int h = getHeight();
		
		g.setColor(new Color(0, 0, 0, 180));
		g.fillRect(0, 0, w, h);
		
		g.setColor(Color.WHITE);

		g.setFont(new Font("Arial", Font.BOLD, (int)(h*0.12)));
	    g.drawString("PAUSE", (int)(w*0.35), (int)(h*0.35));

	    g.setFont(new Font("Arial", Font.PLAIN, (int)(h*0.05)));

	    if(pauseSelect == 0) {
	        g.drawString("> RESUME", (int)(w*0.35), (int)(h*0.55));
	    } else {
	        g.drawString("  RESUME", (int)(w*0.35), (int)(h*0.55));
	    }

	    if(pauseSelect == 1) {
	        g.drawString("> TITLE", (int)(w*0.35), (int)(h*0.65));
	    } else {
	        g.drawString("  TITLE", (int)(w*0.35), (int)(h*0.65));
	    }
	}

}

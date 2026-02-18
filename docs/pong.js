// =====================
// 定数・ゲームステート
// =====================
const TITLE = 0;
const DIFFICULTY = 1;
const PLAY = 2;

let gameState = TITLE;

let aiSpeed = 4;
let difficulty = 1;

let ballX = 400;
let ballY = 300;
let ballXSpeed = 3;
let ballYSpeed = 3;

const MAX_SPEED = 10;

let leftScore = 0;
let rightScore = 0;

let gameOver = false;
let winnerText = "";

// キー入力
let wPressed = false;
let sPressed = false;

// Canvas
const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");

// =====================
// Paddle クラス
// =====================
class Paddle {
  constructor(x, y) {
    this.x = x;
    this.y = y;
    this.width = 20;
    this.height = 100;
  }

  draw() {
    ctx.fillStyle = "white";
    ctx.fillRect(this.x, this.y, this.width, this.height);
  }

  moveUp() {
    this.y -= 5;
  }

  moveDown() {
    this.y += 5;
  }

  getBounds() {
    return {
      x: this.x,
      y: this.y,
      w: this.width,
      h: this.height
    };
  }
}

let leftPaddle = new Paddle(20, 250);
let rightPaddle = new Paddle(760, 250);

// =====================
// キー入力
// =====================
document.addEventListener("keydown", (e) => {
  if (gameState === TITLE && e.key === "Enter") {
    gameState = DIFFICULTY;
  }

  if (gameState === DIFFICULTY) {
    if (e.key === "1") { aiSpeed = 2; gameState = PLAY; }
    if (e.key === "2") { aiSpeed = 4; gameState = PLAY; }
    if (e.key === "3") { aiSpeed = 6; gameState = PLAY; }
  }

  if (e.key === "w") wPressed = true;
  if (e.key === "s") sPressed = true;

  if (e.key === "r" && gameOver) restartGame();
});

document.addEventListener("keyup", (e) => {
  if (e.key === "w") wPressed = false;
  if (e.key === "s") sPressed = false;
});

// =====================
// ゲームループ
// =====================
function gameLoop() {
  update();
  draw();
  requestAnimationFrame(gameLoop);
}

function update() {
  if (gameOver || gameState !== PLAY) return;

  ballX += ballXSpeed;
  ballY += ballYSpeed;

  // 壁
  if (ballY <= 0 || ballY >= canvas.height - 20) {
    ballYSpeed *= -1;
  }

  // 得点
  if (ballX <= 0) {
    rightScore++;
    checkWinner();
    resetBall();
  }
  if (ballX >= canvas.width - 20) {
    leftScore++;
    checkWinner();
    resetBall();
  }

  // プレイヤー操作
  if (wPressed && leftPaddle.y > 0) leftPaddle.moveUp();
  if (sPressed && leftPaddle.y < canvas.height - leftPaddle.height) leftPaddle.moveDown();

  // 衝突判定
  checkCollision(leftPaddle, true);
  checkCollision(rightPaddle, false);

  // CPU
  if (ballXSpeed > 0) {
    let paddleCenter = rightPaddle.y + rightPaddle.height / 2;
    let ballCenter = ballY + 10;

    if (ballCenter < paddleCenter) rightPaddle.y -= aiSpeed;
    if (ballCenter > paddleCenter) rightPaddle.y += aiSpeed;
  }
}

// =====================
// 衝突処理
// =====================
function checkCollision(paddle, isLeft) {
  let p = paddle.getBounds();

  if (ballX < p.x + p.w &&
      ballX + 20 > p.x &&
      ballY < p.y + p.h &&
      ballY + 20 > p.y) {

    ballXSpeed = isLeft ? Math.abs(ballXSpeed) : -Math.abs(ballXSpeed);

    let paddleCenter = p.y + p.h / 2;
    let ballCenter = ballY + 10;

    let distance = ballCenter - paddleCenter;
    let normalized = distance / (p.h / 2);

    ballYSpeed = normalized * 5;

    if (Math.abs(ballXSpeed) < MAX_SPEED) {
      ballXSpeed *= 1.4; 
    }
  }
}

// =====================
// 画面描画
// =====================
function draw() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  if (gameState === TITLE) return drawTitle();
  if (gameState === DIFFICULTY) return drawDifficulty();
  if (gameState === PLAY) return drawGame();
}

function drawTitle() {
  ctx.fillStyle = "white";
  ctx.font = "50px Arial";
  ctx.fillText("PONG GAME", 250, 200);

  ctx.font = "30px Arial";
  ctx.fillText("Press ENTER", 300, 300);
}

function drawDifficulty() {
  ctx.fillStyle = "white";
  ctx.font = "40px Arial";
  ctx.fillText("SELECT DIFFICULTY", 150, 150);

  ctx.font = "30px Arial";
  ctx.fillText("1 : EASY", 250, 250);
  ctx.fillText("2 : NORMAL", 250, 300);
  ctx.fillText("3 : HARD", 250, 350);
}

function drawGame() {
  ctx.fillStyle = "white";

  // 中央線
  ctx.fillStyle = "white";
  for (let i = 0; i < canvas.height; i += 30) {
    ctx.fillRect(canvas.width / 2 - 2, i, 4, 20);
  }

  leftPaddle.draw();
  rightPaddle.draw();

  ctx.beginPath();
  ctx.arc(ballX + 10, ballY + 10, 10, 0, Math.PI * 2);
  ctx.fill();

  ctx.font = "40px Arial";
  ctx.fillText(leftScore, 300, 50);
  ctx.fillText(rightScore, 460, 50);

  if (gameOver) {
    ctx.fillStyle = "black";
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = "white";
    ctx.font = "50px Arial";
    ctx.fillText(winnerText, 250, 300);

    ctx.font = "20px Arial";
    ctx.fillText("Press R to Restart", 300, 350);
    return;
  }
}

// =====================
// その他
// =====================
function resetBall() {
  ballX = canvas.width / 2;
  ballY = canvas.height / 2;

  ballXSpeed = Math.random() < 0.5 ? 3 : -3;
  ballYSpeed = Math.random() < 0.5 ? 3 : -3;
}

function checkWinner() {
  if (leftScore >= 3) {
    gameOver = true;
    winnerText = "YOU WIN!!";
  }
  if (rightScore >= 3) {
    gameOver = true;
    winnerText = "YOU LOSE!";
  }
}

function restartGame() {
  leftScore = 0;
  rightScore = 0;
  gameOver = false;
  resetBall();
  gameState = DIFFICULTY;
}

// =====================
// ゲーム開始
// =====================
gameLoop();

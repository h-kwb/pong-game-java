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

// ★ テニススコア方式
const tennisScores = [0, 15, 30, 40];
let leftScoreIndex = 0;
let rightScoreIndex = 0;
let advantage = 0; // 0 = なし, 1 = 左のアドバンテージ, 2 = 右がアドバンテージ
let leftGames = 0;
let rightGames = 0;
const GAME_WIN = 6; // 6ゲーム先取

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

  // 得点処理（進行方向ベース）
  if (ballX < -20) {

    if (ballXSpeed < 0) {
      scoreRight();   // 左へ抜けた＝右の得点
    }

    resetBall();
  }
  else if (ballX > canvas.width) {

    if (ballXSpeed > 0) {
      scoreLeft();    // 右へ抜けた＝左の得点
    }

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
// 得点処理（テニス方式）
// =====================
function scoreLeft() {

  // ===== 40以上の世界（Deuce以降）=====
  if (leftScoreIndex === 3 && rightScoreIndex === 3) {

    if (advantage === 1) {
      winGameLeft();
    }
    else if (advantage === 2) {
      advantage = 0;
    }
    else {
      advantage = 1;
    }
    return;
  }

  // ===== 通常ポイント進行 =====
  if (leftScoreIndex < 3) {
    leftScoreIndex++;
  }
  else {
    winGameLeft();
  }
}

function scoreRight() {

  // ===== 40以上の世界（Deuce以降）=====
  if (leftScoreIndex === 3 && rightScoreIndex === 3) {

    if (advantage === 2) {
      winGameRight();
    }
    else if (advantage === 1) {
      advantage = 0;
    }
    else {
      advantage = 2;
    }
    return;
  }

  // ===== 通常ポイント進行 =====
  if (rightScoreIndex < 3) {
    rightScoreIndex++;
  }
  else {
    winGameRight();
  }
}


// Setロジック
function winGameLeft() {
  leftGames++;

  // 試合終了
  if (leftGames >= GAME_WIN) {
    gameOver = true;
    winnerText = "MATCH WINNER: YOU!!";
    return;
  }

  // 次のゲームへ
  resetPoints();
  resetBall();
}

function winGameRight() {
  rightGames++;

  // 試合終了
  if (rightGames >= GAME_WIN) {
    gameOver = true;
    winnerText = "MATCH WINNER: CPU!!";
    return;
  }

  // 次のゲームへ
  resetPoints();
  resetBall();
}

// ポイントリセット関数
function resetPoints() {
  leftScoreIndex = 0;
  rightScoreIndex = 0;
  advantage = 0;
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
function drawGame() {
  ctx.fillStyle = "white";
  ctx.font = "30px Arial";
  ctx.fillText(`Games: ${leftGames}`, 250, 90);
  ctx.fillText(`Games: ${rightGames}`, 450, 90);



  // 中央線
  for (let i = 0; i < canvas.height; i += 30) {
    ctx.fillRect(canvas.width / 2 - 2, i, 4, 20);
  }

  leftPaddle.draw();
  rightPaddle.draw();

  ctx.beginPath();
  ctx.arc(ballX + 10, ballY + 10, 10, 0, Math.PI * 2);
  ctx.fill();

  ctx.font = "40px Arial";

  // ★ デュース・アドバンテージ表示
  if (leftScoreIndex === 3 && rightScoreIndex === 3) {
    if (advantage === 1) {
      ctx.fillText("AD", 300, 50);
      ctx.fillText("40", 460, 50);
    } else if (advantage === 2) {
      ctx.fillText("40", 300, 50);
      ctx.fillText("AD", 460, 50);
    } else {
      ctx.fillText("DEUCE", 330, 50);
    }
  } else {
    ctx.fillText(tennisScores[leftScoreIndex], 300, 50);
    ctx.fillText(tennisScores[rightScoreIndex], 460, 50);
  }

  if (gameOver) {
    ctx.fillStyle = "black";
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = "white";
    ctx.font = "50px Arial";
    ctx.fillText(winnerText, 200, 300);

    ctx.font = "20px Arial";
    ctx.fillText("Press R to Restart Match", 260, 350);
    return;
  }
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

function draw() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  if (gameState === TITLE) {
    drawTitle();
  } else if (gameState === DIFFICULTY) {
    drawDifficulty();
  } else if (gameState === PLAY) {
    drawGame();
  }
}

function drawTitle() {
  ctx.fillStyle = "white";
  ctx.font = "50px Arial";
  ctx.fillText("PONG GAME", 250, 200);

  ctx.font = "30px Arial";
  ctx.fillText("Press ENTER", 300, 300);
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

function restartGame() {
  leftScoreIndex = 0;
  rightScoreIndex = 0;
  advantage = 0;

  leftGames = 0;
  rightGames = 0;

  gameOver = false;
  resetBall();
  gameState = DIFFICULTY;
}

// =====================
// ゲーム開始
// =====================
gameLoop();
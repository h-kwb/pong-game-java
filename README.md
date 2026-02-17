<p align="center">
  <a href="https://h-kwb.github.io/pong-game-java/" target="_blank">
    <img src="https://img.shields.io/badge/PLAY%20NOW-000000?style=for-the-badge&logo=github&logoColor=white" />
  </a>
</p>

# Pong Game (Java)

原初の対戦式テニスゲーム「Pong」を Java（Eclipse）で再現したシンプルなゲームです。  
プレイヤーは左側のパドルを操作し、右側のパドルは CPU が自動で動きます。

---

## 🎮 ゲーム概要

- 画面上を動くボールをパドルで打ち返すクラシックな Pong スタイルのゲーム
- プレイヤー：左側パドルを操作（キーボード操作：W↑ / S↓）
- CPU：右側パドルを自動制御
- - EASY : ゆっくり追従
  - NORMAL : 標準的な反応速度
  - HARD : 高速で正確な追従
- ボールが相手側の壁に到達すると得点（3点先取）
- シンプルなルールで気軽に遊べる内容

---

## 🛠 使用技術

- **Java**
- **Eclipse**（開発環境）
- **AWT / Swing** を用いた描画処理

---

## ▶ 実行方法

1. 本リポジトリをクローン
  ```
  git clone https://github.com/h-kwb/pong-game-java.git
  ```
3. Eclipse にインポート  
4. Main.java を実行するとゲームが起動します

---

## 📁 プロジェクト構成
```
src/
 └─ ponggame/
      ├─ Main.java
      ├─ GameFrame.java
      ├─ GamePanel.java
      └─ Paddle.java
```
---

## ✨ 今後の拡張アイデア

- 効果音の追加

---

## 📜 ライセンス

このプロジェクトは自由に改変・利用できます。


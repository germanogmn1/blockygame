package blockygame;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

import blockygame.GamePlayState;

public class KeyboardInputComponent implements KeyListener {
	
	private GamePlayState game;
	
	private static final int SOFT_DROP_DELAY = 50;
	private int softDropTimer;

	public KeyboardInputComponent(GamePlayState game) {
		this.game = game;
		softDropTimer = SOFT_DROP_DELAY;
	}
	
	@Override
	public boolean isAcceptingInput() {
		return game.isAcceptingCommands();
	}

	public void update(Input input, int delta) {
		if (input.isKeyDown(Input.KEY_DOWN)) {
			softDropTimer -= delta;
			if (softDropTimer <= 0) {
				game.softDrop();
				softDropTimer = SOFT_DROP_DELAY;
			}
		}
	}
	
	@Override
	public void keyPressed(int key, char c) {
		switch (key) {
		case Input.KEY_LEFT:
			game.movePieceHorizontal(-1);
			break;
		case Input.KEY_RIGHT:
			game.movePieceHorizontal(1);
			break;
		case Input.KEY_Z:
			game.rotatePiece(-1);
			break;
		case Input.KEY_UP:
		case Input.KEY_X:
			game.rotatePiece(1);
			break;
		case Input.KEY_SPACE:
			game.hardDrop();
			break;
		}
	}
	
	@Override
	public void keyReleased(int key, char c) {
		
	}
	
	/*
	 * Unused methods
	 */
	@Override
	public void setInput(Input input) {
	}
	
	@Override
	public void inputEnded() {
	}

	@Override
	public void inputStarted() {
	}
}

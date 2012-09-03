package blockygame;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

import blockygame.GamePlayState;

public class KeyboardInputComponent implements KeyListener {
	
	private GamePlayState game;
	
	private static final int SOFT_DROP_DELAY = 50;
	private int softDropTimer;
	
	private static final int HORIZONTAL_DELAY_FIRST = 250;
	private static final int HORIZONTAL_DELAY_AFTER = 60;
	private boolean holdingMove;
	private int horizontalTimer;

	public KeyboardInputComponent(GamePlayState game) {
		this.game = game;
		softDropTimer = SOFT_DROP_DELAY;
	}
	
	@Override
	public boolean isAcceptingInput() {
		return game.isAcceptingCommands();
	}

	public void update(Input input, int delta) {
		// Soft drop
		if (input.isKeyDown(Input.KEY_DOWN)) {
			softDropTimer -= delta;
			if (softDropTimer <= 0) {
				game.softDrop();
				softDropTimer = SOFT_DROP_DELAY;
			}
		}
		
		// Horizontal move
		int side = 0;
		if (input.isKeyDown(Input.KEY_RIGHT)) side++;
		if (input.isKeyDown(Input.KEY_LEFT)) side--;
		if (side != 0) {
			horizontalTimer -= delta;
			if (horizontalTimer <= 0) {
				game.movePieceHorizontal(side);
				if (!holdingMove) {
					horizontalTimer = HORIZONTAL_DELAY_FIRST;
					holdingMove = true;
				} else {
					horizontalTimer = HORIZONTAL_DELAY_AFTER;
				}
			}
		} else {
			horizontalTimer = 0;
			holdingMove = false;
		}
		
	}
	
	@Override
	public void keyPressed(int key, char c) {
		switch (key) {
		case Input.KEY_Z:
			game.rotatePiece(-1);
			return;
		case Input.KEY_UP:
		case Input.KEY_X:
			game.rotatePiece(1);
			return;
		case Input.KEY_SPACE:
			game.hardDrop();
			return;
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

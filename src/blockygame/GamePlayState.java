package blockygame;

import java.util.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import blockygame.util.Tuple;

public class GamePlayState extends BasicGameState {	
	int stateID = -1;
	
	private enum State {
		NEW_PIECE, MOVING_PIECE, FULL_LINE_CHECK, LINE_DESTRUCTION_FX, DESTROY_LINES, GAME_OVER
	}
	
	private State currentState;
	
	/*
	 * Game logic state
	 */
	private Pit pit;
	private Piece nextPiece;
	private Piece currentPiece;
	private Tuple currentPiecePos;
	
	private long score = 0;
	private int totalLines = 0;
	private int level = 1;
	private int combo = 0;
	
	/*
	 * Timers
	 */
	private static final int FALL_DELAY = 500;
	private int currentFallDelay = FALL_DELAY;
	private int fallTimer;
	
	/*
	 * Line fading effect
	 */
	private List<Integer> linesBeingDestroyed = new ArrayList<Integer>();
	private static final int LINE_FADE_DURATION = 400;
	private int lineFadeTimer;

	private KeyboardInputComponent inputComponent;
	private GraphicsComponent graphicsComponent;
	
	GamePlayState(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public int getID() {
		return stateID;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		inputComponent = new KeyboardInputComponent(this);
		container.getInput().addKeyListener(inputComponent);
		
		graphicsComponent = new GraphicsComponent(this);
		
		currentState = State.NEW_PIECE;
		pit = new Pit();
		lineFadeTimer = LINE_FADE_DURATION;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics)
			throws SlickException {
		graphicsComponent.render(graphics);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		graphicsComponent.update(delta);
		switch (currentState) {
		case NEW_PIECE:
			createNewPiece();
			break;
		case MOVING_PIECE:
			updatePiece(container.getInput(), delta);
			break;
		case FULL_LINE_CHECK:
			checkForFullLines(delta);
			break;
		case LINE_DESTRUCTION_FX:
			lineDestructionEffect(delta);
			break;
		case DESTROY_LINES:
			destroyLines(delta);
			break;
		case GAME_OVER:
			System.out.println("Game Over!");
			container.exit();
			break;
		}
	}
	
	private void updatePiece(Input input, int delta) {
		inputComponent.update(input, delta);
		
		fallTimer -= delta;
		if (fallTimer <= 0) {
			movePieceDown();
			fallTimer = currentFallDelay;
		}
	}
	
	/**
	 * @return true if the piece could move down
	 */
	private boolean movePieceDown() {
		if (pit.canPutPieceAt(currentPiece, currentPiecePos.add(0, 1))) {
			currentPiecePos = currentPiecePos.add(0, 1);
			return true;
		} else {
			releaseCurrentPiece();
			currentState = State.FULL_LINE_CHECK;
			return false;
		}
	}
	
	/**
	 * @param side -1 for left, +1 for right
	 */
	void movePieceHorizontal(int side) {
		assert side == -1 || side == 1 : "-1 or 1";
		Tuple newPos = currentPiecePos.add(side, 0);
		
		if (pit.canPutPieceAt(currentPiece, newPos))
			currentPiecePos = newPos;
	}
	
	/**
	 * @param side -1 for left, +1 for right
	 */
	void rotatePiece(int side) {
		assert side == -1 || side == 1 : "-1 or 1";
		currentPiece.rotate(side);
		if (!pit.canPutPieceAt(currentPiece, currentPiecePos))
			currentPiece.rotate(-side); // rotate back
	}
	
	void softDrop() {
		if (movePieceDown())
			score += 1;
	}
	
	void hardDrop() {
		while (movePieceDown()) {
			score += 2;
		}
	}

	private void releaseCurrentPiece() {
		pit.addPieceAt(currentPiece, currentPiecePos);
		
		currentPiece = null;
		fallTimer = currentFallDelay;
		currentState = State.FULL_LINE_CHECK;
	}
	
	private void createNewPiece() {
		if (nextPiece == null) {
			currentPiece = PieceFactory.makeRandomPiece();
		} else {
			currentPiece = nextPiece;
		}
		nextPiece = PieceFactory.makeRandomPiece();
		
		currentPiecePos = Pit.SPAWN_POSITION;
		
		if (!pit.canPutPieceAt(currentPiece, currentPiecePos)) {
			currentState = State.GAME_OVER;
		} else {
			currentState = State.MOVING_PIECE;
		}
	}
	
	private void checkForFullLines(int delta) {
		int linesCleared = 0;
		for (int i = 0; i < Pit.HEIGHT; i++) {
			if (pit.isLineFull(i)) {
				linesCleared++;
				linesBeingDestroyed.add(i);
			}
		}
		
		computeLineClear(linesCleared);
		currentState = (linesCleared == 0) ? State.NEW_PIECE : State.LINE_DESTRUCTION_FX;
	}

	private void computeLineClear(int linesCleared) {
		assert linesCleared >= 0 && linesCleared <= 4 : linesCleared;
		
		if (linesCleared == 0) {
			combo = 0;
			return;
		}
		
		totalLines += linesCleared;
		
		int lineScore = 0;
		String msg = "";
		switch (linesCleared) {
		case 1:
			lineScore = 100;
			msg = "Single";
			break;
		case 2:
			lineScore = 300;
			msg = "Double";
			break;
		case 3:
			lineScore = 500;
			msg = "Triple";
			break;
		case 4:
			lineScore = 800;
			msg = "Tetris";
			break;
		}
		
		lineScore *= level;
		score += lineScore;
		
		graphicsComponent.addMessage(msg+" +"+lineScore);
		
		checkLevelUp();
		
		if (combo > 0) {
			int comboScore = combo * 50 * getLevel();
			if (combo == 1)
				graphicsComponent.addMessage("Combo +"+comboScore);
			else
				graphicsComponent.addMessage(combo+"x Combo +"+comboScore);
		}
		
		combo++;
	}

	private void checkLevelUp() {
		if (totalLines / 5 > (level - 1)) {
			level++;
			graphicsComponent.addMessage("Level Up!");
			currentFallDelay *= 0.9;
		}
	}
	
	private void destroyLines(int delta) {
		for (int line : linesBeingDestroyed) {
			pit.eraseLine(line);
		}
		linesBeingDestroyed.clear();
		currentState = State.NEW_PIECE;
	}
	
	// Delay until the effect is finished
	private void lineDestructionEffect(int delta) {
		lineFadeTimer -= delta;
		
		if (lineFadeTimer <= 0) {
			lineFadeTimer = LINE_FADE_DURATION;
			currentState = State.DESTROY_LINES;
		}
	}
	
	float getLineEffectStage() {
		return (float) lineFadeTimer / (float) LINE_FADE_DURATION;
	}
	
	boolean isAcceptingCommands() {
		return currentState == State.MOVING_PIECE;
	}

	long getScore() {
		return score;
	}
	
	int getLevel() {
		return level;
	}
	
	int getTotalLines() {
		return totalLines;
	}

	Piece getCurrentPiece() {
		return currentPiece;
	}
	
	Piece getNextPiece() {
		return nextPiece;
	}
	
	Tuple getCurrentPiecePos() {
		return currentPiecePos;
	}
	
	Pit getPit() {
		return pit;
	}
	
	List<Integer> getLinesBeingDestroyed() {
		return linesBeingDestroyed;
	}
}

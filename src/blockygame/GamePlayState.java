package blockygame;

import java.awt.Font;
import java.util.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import blockygame.Piece.PieceID;
import blockygame.util.Tuple;

public class GamePlayState extends BasicGameState {	
	int stateID = -1;
	
	private enum State {
		NEW_PIECE,
		MOVING_PIECE,
		FULL_LINE_CHECK,
		LINE_DESTRUCTION_FX,
		DESTROY_LINES,
		GAME_OVER
	}
	
	private State currentState;
	
	/*
	 * Game logic state
	 */
	private Pit pit;
	private Piece currentPiece;
	private Tuple currentPiecePos;
	private int score = 0;
	
	/*
	 * Timers
	 */
	private static final int PIECE_FALL_DELAY = 400;
	private int pieceFallTimer;
	
	/*
	 * Rendering related
	 */
	private static final Tuple PIT_ORIGIN = new Tuple(280, 60);
	private static final Tuple BLOCK_SIZE = new Tuple(24, 24);
	private Map<PieceID, Image> pieceBlocks;
	private Image pitBackground;
	private Image background;
	private List<Integer> linesDestroyed = new ArrayList<Integer>();
	private TrueTypeFont scoreFont;
	
	/*
	 * Line fading effect
	 */
	private static final int LINE_FADE_DURATION = 300;
	private int lineFadeTimer;
	private float lineFadeAlpha = 1.0f;

	private KeyboardInputComponent inputComponent;
	
	GamePlayState(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public int getID() {
		return stateID;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame game)
			throws SlickException {
		inputComponent = new KeyboardInputComponent(this);
		gc.getInput().addKeyListener(inputComponent);
		
		currentState = State.NEW_PIECE;
		pit = new Pit();
		pieceFallTimer = PIECE_FALL_DELAY;
		lineFadeTimer = LINE_FADE_DURATION;
		
		background = new Image("data/background.png");
		pitBackground = new Image("data/pit_bg.png");
		scoreFont = new TrueTypeFont(new Font("data/imagine_font.ttf", Font.PLAIN, 48), false);
		
		pieceBlocks = new HashMap<PieceID, Image>();
		
		for (PieceID id : PieceID.values())
			pieceBlocks.put(id, new Image("data/blocks/"+id+".png"));
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics)
			throws SlickException {
		background.draw(0, 0);
		pitBackground.draw(PIT_ORIGIN.x - 12, PIT_ORIGIN.y - 12);
		
		scoreFont.drawString(100,  100, "Score: "+String.valueOf(score));
		
		// Draw current piece
		if (currentPiece != null) {
			for (int i = 0; i < 4; i++) {
				Tuple block = currentPiecePos.add(currentPiece.getPositionOfBlock(i));
				int x = PIT_ORIGIN.x + (block.x * BLOCK_SIZE.x);
				int y = PIT_ORIGIN.y + (block.y * BLOCK_SIZE.x);
				pieceBlocks.get(currentPiece.getId()).draw(x, y);
			}
		}
		
		// Draw pit
		int x = PIT_ORIGIN.x, y = PIT_ORIGIN.y;
		for (int lineIdx = 0; lineIdx < Pit.HEIGHT; lineIdx++) {
			for (PieceID id : pit.getLine(lineIdx)) {
				if (id != null) {
					if (linesDestroyed.contains(lineIdx)) {
						pieceBlocks.get(id).draw(x, y, new Color(1.0f, 1.0f, 1.0f, lineFadeAlpha));
					} else {
						pieceBlocks.get(id).draw(x, y);
					}
				}
				x += BLOCK_SIZE.x;
			}
			x = PIT_ORIGIN.x;
			y += BLOCK_SIZE.y;
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
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
		
		pieceFallTimer -= delta;
		if (pieceFallTimer <= 0) {
			movePieceDown();
			pieceFallTimer = PIECE_FALL_DELAY;
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
	public void movePieceHorizontal(int side) {
		assert side == -1 || side == 1 : "-1 or 1";
		Tuple newPos = currentPiecePos.add(side, 0);
		
		if (pit.canPutPieceAt(currentPiece, newPos))
			currentPiecePos = newPos;
	}
	
	/**
	 * @param side -1 for left, +1 for right
	 */
	public void rotatePiece(int side) {
		assert side == -1 || side == 1 : "-1 or 1";
		currentPiece.rotate(side);
		if (!pit.canPutPieceAt(currentPiece, currentPiecePos))
			currentPiece.rotate(-side); // rotate back
	}
	
	public void softDrop() {
		if (movePieceDown())
			score += 1;
	}
	
	public void hardDrop() {
		while (movePieceDown()) {
			score += 2;
		}
	}

	private void releaseCurrentPiece() {
		pit.addPieceAt(currentPiece, currentPiecePos);
		
		currentPiece = null;
		pieceFallTimer = PIECE_FALL_DELAY;
		currentState = State.FULL_LINE_CHECK;
	}
	
	private void createNewPiece() {
		currentPiece = PieceFactory.makeRandomPiece();
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
				linesDestroyed.add(i);
			}
		}
		assert linesCleared <= 4 : linesCleared;
		
		switch (linesCleared) {
		case 1:
			score += 100;
			break;
		case 2:
			score += 300;
			break;
		case 3:
			score += 500;
			break;
		case 4:
			score += 800;
			break;
		}
		
		currentState = (linesCleared == 0) ? State.NEW_PIECE : State.LINE_DESTRUCTION_FX;
	}
	
	private void destroyLines(int delta) {
		for (int line : linesDestroyed) {
			pit.eraseLine(line);
		}
		linesDestroyed.clear();
		currentState = State.NEW_PIECE;
	}
	
	private void lineDestructionEffect(int delta) {
		lineFadeTimer -= delta;
		
		if (lineFadeTimer > 0) {
			lineFadeAlpha = (float) lineFadeTimer / (float) LINE_FADE_DURATION;
		} else {
			lineFadeTimer = LINE_FADE_DURATION;
			currentState = State.DESTROY_LINES;
			lineFadeAlpha = 1.0f;
		}
	}

	public boolean isAcceptingCommands() {
		return currentState == State.MOVING_PIECE;
	}
}

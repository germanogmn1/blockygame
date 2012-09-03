package blockygame;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;

import blockygame.Piece.PieceID;
import blockygame.util.Tuple;

public class GraphicsComponent { 
	
	private static final Tuple BLOCK_SIZE = new Tuple(24, 24);
	
	private static final Rectangle PIT_AREA =
			new Rectangle(280, 60, Pit.WIDTH * BLOCK_SIZE.x, Pit.HEIGHT * BLOCK_SIZE.y);
	
	private GamePlayState game;
	
	private Map<PieceID, Image> pieceBlocks;
	private Image pitBackground;
	private Image background;
	private Image nextPieceSlot;
	
	private TrueTypeFont scoreFont;
	private TrueTypeFont controlsFont;
	private TrueTypeFont controlsTitleFont;
	
	private static final int MESSAGE_DELAY = 1500;
	private LinkedHashMap<String, Integer> messageQueue = new LinkedHashMap<String, Integer>();
	
	public GraphicsComponent(GamePlayState game)
			throws SlickException {
		this.game = game;
		
		background = new Image("data/background.png");
		pitBackground = new Image("data/pit_bg.png");
		nextPieceSlot = new Image("data/next_slot.png");
		
		pieceBlocks = new HashMap<PieceID, Image>();
		
		for (PieceID id : PieceID.values())
			pieceBlocks.put(id, new Image("data/blocks/"+id+".png"));
		try {
			loadFonts();
		} catch (Throwable e) {
			throw new SlickException("Failed to load font", e);
		}
	}
	
	private void loadFonts() throws FontFormatException, IOException {
		InputStream is = new FileInputStream("data/imagine_font.ttf");
		Font font = Font.createFont(Font.TRUETYPE_FONT, is);
		
		scoreFont = new TrueTypeFont(font.deriveFont(24.0f), false);
		controlsFont = new TrueTypeFont(font.deriveFont(13.0f), true);
		controlsTitleFont = new TrueTypeFont(font.deriveFont(22.0f), false);
	}
	
	public void render(Graphics graphics) {
		background.draw(0, 0);
		pitBackground.draw(PIT_AREA.getX() - 12, PIT_AREA.getY() - 12);
		nextPieceSlot.draw(575, 57);
		
		drawInstructions();
		drawScore();
		
		// Draw current piece
		drawCurrentPiece(game.getCurrentPiece(), game.getCurrentPiecePos());
		drawNextPiece(game.getNextPiece());
		drawMessages();
		
		/*
		 * Draw pit
		 */
		List<Integer> destroyingLines = game.getLinesBeingDestroyed();
		int x = (int) PIT_AREA.getX(), y = (int) PIT_AREA.getY();
		
		// Do not draw the first two lines
		for (int lineIdx = 2; lineIdx < Pit.HEIGHT; lineIdx++) {
			for (PieceID id : game.getPit().getLine(lineIdx)) {
				if (id != null) {
					if (destroyingLines.contains(lineIdx)) {
						pieceBlocks.get(id).draw(x, y, new Color(1.0f, 1.0f, 1.0f, game.getLineEffectStage()));
					} else {
						pieceBlocks.get(id).draw(x, y);
					}
				}
				x += BLOCK_SIZE.x;
			}
			x = (int) PIT_AREA.getX();
			y += BLOCK_SIZE.y;
		}
	}
	
	public void addMessage(String message) {
		messageQueue.put(message, MESSAGE_DELAY);
	}
	
	public void update(int delta) {
		Iterator<Entry<String, Integer>> iter = messageQueue.entrySet().iterator();
		
		while (iter.hasNext()) {
			Entry<String, Integer> entry = iter.next();
			String key = entry.getKey();
			int val = entry.getValue();
			
			if (val - delta <= 0) {
				iter.remove();
			} else {
				messageQueue.put(key, val - delta);
			}
		}
	}
	
	private void drawMessages() {
		int x = 50, y = (int) PIT_AREA.getCenterY();
		
		for (Entry<String, Integer> entry : messageQueue.entrySet()) {
			float alpha = (float) entry.getValue() / (float) MESSAGE_DELAY;
			
			scoreFont.drawString(x, y, entry.getKey(), new Color(0.0f, 0.0f, 1.0f, alpha));
			y += 30;
		}
	}

	private void drawScore() {
		String[] info = {
				"Score",
				String.format("%,d", game.getScore()),
				"Level",
				String.valueOf(game.getLevel()),
				"Lines",
				String.format("%,d", game.getTotalLines())
			};
		
		float posX = 50;
		float posY = PIT_AREA.getY();
		
		for (String str : info) {
			scoreFont.drawString(posX, posY, str, new Color(0.2f, 0.2f, 0.2f));
			posY += 35;
		}
	}
	
	private void drawInstructions() {
		String[] instructions = {
				"LEFT, RIGHT: Move piece",
				"X or UP: Rotate Right",
				"Z: Rotate Left",
				"DOWN: Soft Drop",
				"SPACE: Hard Drop"
			};
		
		Color fontColor = new Color(0.2f, 0.2f, 0.2f);
		
		int y = 400;
		
		controlsTitleFont.drawString(560, y, "Controls:", fontColor);
		y += 30;
		
		for (String ins : instructions) {
			controlsFont.drawString(560, y, ins, fontColor);
			y += 26;
		}
	}

	private void drawCurrentPiece(Piece piece, Tuple position) {
		if (piece == null)
			return;
		for (int i = 0; i < 4; i++) {
			Tuple block = position.add(piece.getPositionOfBlock(i));
			
			// ignore the two first invisible lines
			block = block.subtract(0, 2);
			if (block.y < 0)
				return;
			
			float x = PIT_AREA.getX() + (block.x * BLOCK_SIZE.x);
			float y = PIT_AREA.getY() + (block.y * BLOCK_SIZE.y);
			pieceBlocks.get(piece.getId()).draw(x, y);
		}
	}
	
	private void drawNextPiece(Piece piece) {
		if (piece == null)
			return;
		
		int xor = 625, yor = 107;
		
		switch (piece.getId()) {
		case I:
			xor -= 12;
			yor -= 12;
			break;
		case O:
			xor -= 12;
		default:
		}
		
		for (int i = 0; i < 4; i++) {
			Tuple block = piece.getPositionOfBlock(i);
			
			int x = (int) xor + (block.x * BLOCK_SIZE.x);
			int y = (int) yor + (block.y * BLOCK_SIZE.x);
			pieceBlocks.get(piece.getId()).draw(x, y);
		}
	}
}

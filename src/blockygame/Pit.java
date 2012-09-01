package blockygame;

import java.util.*;

import blockygame.Piece.PieceID;
import blockygame.util.Tuple;

public class Pit implements Iterable<PieceID[]> {
	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;
	public static final Tuple SPAWN_POSITION = new Tuple(4, 2);
	
	private List<PieceID[]> pit;
	
	public Pit() {
		pit = new ArrayList<PieceID[]>();
		clear();
	}
	
	public void clear() {
		pit.clear();
		
		for (int y = 0; y < HEIGHT; y++) {
			addNewLine();
		}
	}
	
	private void addNewLine() {
		pit.add(new PieceID[WIDTH]);
	}
	
	public PieceID[] getLine(int index) {
		return pit.get(index);
	}
	
	public boolean addPieceAt(Piece piece, Tuple position) {
		if (!canPutPieceAt(piece, position)) {
			return false;
		}
		
		for (int i = 0; i < 4; i++) {
			Tuple pos = piece.getPositionOfBlock(i);
			setBlockAt(piece.getId(), position.add(pos));
		}
		
		return true;
	}
	
	public void setBlockAt(PieceID id, Tuple position) {
		assert position.x >= 0 && position.x < WIDTH : position.x;
		assert position.y >= 0 && position.y < HEIGHT : position.x;
		pit.get(position.y)[position.x] = id;
	}
	
	public boolean canPutPieceAt(Piece piece, Tuple position) {
		for (int i = 0; i < 4; i++) {
			Tuple block = position.add(piece.getPositionOfBlock(i));
			
			if (block.x < 0 || block.x >= WIDTH ||
				block.y < 0 || block.y >= HEIGHT) {
				return false;
			}
			if (pit.get(block.y)[block.x] != null) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isLineFull(int line) {
		for (PieceID id : pit.get(line)) {
			if (id == null)
				return false;
		}
		return true;
	}
	
	public void eraseLine(int line) {
		assert line > 0 && line < HEIGHT : line;
		pit.remove(line);
		pit.add(0, new PieceID[WIDTH]);
	}
	
	@Override
	public Iterator<PieceID[]> iterator() {
		return pit.iterator();
	}
	
	// Debug
	public void drawPit() {
		System.out.println("====================");
		for (PieceID[] line : this) {
			for (PieceID block : line) {
				char s = (block == null) ? ' ' : '#';
				System.out.print("|" + s);
			}
			System.out.println('|');
		}
		System.out.println("====================");
	}
}

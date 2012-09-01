package blockygame.tests;
import static org.junit.Assert.*;

import org.junit.*;

import blockygame.*;
import blockygame.Piece.PieceID;
import blockygame.util.Tuple;

public class PitTest {
	
	private Pit pit;
	
	private final PieceID j = PieceID.J;
	private final PieceID _ = null;
	
	@Before public void setUp() {
		pit = new Pit();
	}
	
	private Piece createJPiece() {
		return new Piece(PieceID.J, new Tuple[][] {
				{new Tuple(-1, -1), new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0)},
				{new Tuple(0, 1), new Tuple(0, 0), new Tuple(0, -1), new Tuple(1, -1)},
				{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, 1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(0, 1), new Tuple(-1, 1)}
			});
	}
	
	@Test public void testIterable() {
		PieceID[] nullArray = new PieceID[Pit.WIDTH];
		
		for (PieceID[] line : pit) {
			assertArrayEquals(nullArray, line);
		}
	}
	
	@Test public void testCanAddPiece() {
		Piece p = createJPiece();
		
		assertTrue(pit.addPieceAt(p, Pit.SPAWN_POSITION));
		assertArrayEquals(new PieceID[] {_, _, _, j, _, _, _, _, _, _}, pit.getLine(1));
		assertArrayEquals(new PieceID[] {_, _, _, j, j, j, _, _, _, _}, pit.getLine(2));
	}
	
	@Test public void testCanAddRotatedPiece() {
		Piece p = createJPiece();
		p.rotate(1);
		
		assertTrue(pit.addPieceAt(p, Pit.SPAWN_POSITION.subtract(1, 0)));
		assertArrayEquals(new PieceID[] {_, _, _, j, j, _, _, _, _, _}, pit.getLine(1));
		assertArrayEquals(new PieceID[] {_, _, _, j, _, _, _, _, _, _}, pit.getLine(2));
		assertArrayEquals(new PieceID[] {_, _, _, j, _, _, _, _, _, _}, pit.getLine(3));
	}
	
	@Test public void testCannotAddPieceOnTakenSpace() {
		Piece p = createJPiece();
		pit.setBlockAt(j, Pit.SPAWN_POSITION);
		
		assertFalse(pit.addPieceAt(p, Pit.SPAWN_POSITION));
		
		pit.clear();
		pit.setBlockAt(j, Pit.SPAWN_POSITION.add(1, 0));
		
		assertFalse(pit.addPieceAt(p, Pit.SPAWN_POSITION));
	}
	
	@Test public void testCannotAddPieceOutsidePit() {
		Piece p = createJPiece();
		
		assertFalse(pit.addPieceAt(p, new Tuple(0, Pit.SPAWN_POSITION.y)));
		assertFalse(pit.addPieceAt(p, new Tuple(Pit.WIDTH + 1, Pit.SPAWN_POSITION.y)));
	}
	
	@Test public void testLineIsFull() {
		int line = Pit.HEIGHT - 1; 
		for (int x = 0; x < Pit.WIDTH; x++)
			pit.setBlockAt(j, new Tuple(x, line));
		
		assertTrue(pit.isLineFull(line));
	}
	
	@Test public void testLineIsNotFull() {
		int line = Pit.HEIGHT - 1; 
		for (int x = 1; x < Pit.WIDTH; x++)
			pit.setBlockAt(j, new Tuple(x, line));
		
		assertFalse(pit.isLineFull(line));
	}
	
	@Test public void testEraseLine() {
		// fill line 17 with L blocks
		for (int x = 1; x < Pit.WIDTH; x += 2)
			pit.setBlockAt(PieceID.L, new Tuple(x, 17));
		PieceID[] line17 = pit.getLine(17);
		
		// fill line 18 with O blocks
		for (int x = 0; x < Pit.WIDTH; x += 2)
			pit.setBlockAt(PieceID.O, new Tuple(x, 18));
		PieceID[] line18 = pit.getLine(18);
		
		// fill last line with I blocks 
		for (int x = 0; x < Pit.WIDTH; x++)
			pit.setBlockAt(PieceID.I, new Tuple(x, 19));
		
		pit.eraseLine(19);
		
		// Assert
		PieceID[] nullArray = new PieceID[Pit.WIDTH];
		
		assertArrayEquals(nullArray, pit.getLine(17));
		assertArrayEquals(line17, pit.getLine(18));
		assertArrayEquals(line18, pit.getLine(19));
	}
	
}

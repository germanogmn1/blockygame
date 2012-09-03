package blockygame.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import blockygame.Piece;
import blockygame.Piece.PieceID;
import blockygame.util.Tuple;

public class PieceTest {
	
	private static Tuple[][] pieceI = new Tuple[][] {
		{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0), new Tuple(2, 0)},
		{new Tuple(1, -1), new Tuple(1, 0), new Tuple(1, 1), new Tuple(1, 2)},
		{new Tuple(-1, 1), new Tuple(0, 1), new Tuple(1, 1), new Tuple(2, 1)},
		{new Tuple(0, -1), new Tuple(0, 0), new Tuple(0, 1), new Tuple(0, 2)}
	};
	
	private static Tuple[][] pieceJ = new Tuple[][] {
		{new Tuple(-1, -1), new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0)},
		{new Tuple(0, 1), new Tuple(0, 0), new Tuple(0, -1), new Tuple(1, -1)},
		{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, 1)},
		{new Tuple(0, -1), new Tuple(0, 0), new Tuple(0, 1), new Tuple(-1, 1)}
	};
	
	@Test
	public void testPieceWidth() {
		assertEquals(4, new Piece(PieceID.I, pieceI).getWidth());
		assertEquals(3, new Piece(PieceID.J, pieceJ).getWidth());
	}

}

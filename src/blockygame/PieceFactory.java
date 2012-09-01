package blockygame;

import java.util.*;

import blockygame.Piece.PieceID;
import blockygame.util.Tuple;

public class PieceFactory {
	
	private static final Map<PieceID, Tuple[][]> matricesMap = initializeMatrices();
	
	private static Map<PieceID, Tuple[][]> initializeMatrices() {
		Map<PieceID, Tuple[][]> result = new HashMap<PieceID, Tuple[][]>();
		
		result.put(PieceID.I, new Tuple[][] {
				{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0), new Tuple(2, 0)},
				{new Tuple(1, -1), new Tuple(1, 0), new Tuple(1, 1), new Tuple(1, 2)},
				{new Tuple(-1, 1), new Tuple(0, 1), new Tuple(1, 1), new Tuple(2, 1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(0, 1), new Tuple(0, 2)}
			});
		
		result.put(PieceID.J, new Tuple[][] {
				{new Tuple(-1, -1), new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0)},
				{new Tuple(0, 1), new Tuple(0, 0), new Tuple(0, -1), new Tuple(1, -1)},
				{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, 1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(0, 1), new Tuple(-1, 1)}
			});
		
		result.put(PieceID.L, new Tuple[][] {
				{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, -1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(0, 1), new Tuple(1, 1)},
				{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(1, 0), new Tuple(-1, 1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(0, 1), new Tuple(-1, -1)}
			});
		
		result.put(PieceID.O, new Tuple[][] {
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, -1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, -1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, -1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, -1)}
			});
		
		result.put(PieceID.S, new Tuple[][] {
				{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(0, -1), new Tuple(1, -1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(1, 0), new Tuple(1, 1)},
				{new Tuple(-1, 1), new Tuple(0, 1), new Tuple(0, 0), new Tuple(1, 0)},
				{new Tuple(-1, -1), new Tuple(-1, 0), new Tuple(0, 0), new Tuple(0, 1)}
			});
		
		result.put(PieceID.T, new Tuple[][] {
				{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(0, -1), new Tuple(1, 0)},
				{new Tuple(0, 1), new Tuple(0, 0), new Tuple(0, -1), new Tuple(1, 0)},
				{new Tuple(0, 1), new Tuple(0, 0), new Tuple(-1, 0), new Tuple(1, 0)},
				{new Tuple(0, 1), new Tuple(0, 0), new Tuple(0, -1), new Tuple(-1, 0)}
			});
		
		result.put(PieceID.Z, new Tuple[][] {
				{new Tuple(-1, -1), new Tuple(0, -1), new Tuple(0, 0), new Tuple(1, 0)},
				{new Tuple(1, -1), new Tuple(1, 0), new Tuple(0, 0), new Tuple(0, 1)},
				{new Tuple(-1, 0), new Tuple(0, 0), new Tuple(0, 1), new Tuple(1, 1)},
				{new Tuple(0, -1), new Tuple(0, 0), new Tuple(-1, 0), new Tuple(-1, 1)}
			});
		
		return result;
	}
	
	private static Random random = new Random();
	private static PieceID[] idList = PieceID.values();
	
	public static Piece makePiece(PieceID id) {
		Tuple[][] matrix = matricesMap.get(id);
		return new Piece(id, matrix);
	}
	
	public static Piece makeRandomPiece() {
		int drawn = random.nextInt(idList.length);
		return makePiece(idList[drawn]);
	}
	
}

package blockygame;

import blockygame.util.Tuple;

public class Piece {
	
	public enum PieceID {
		I, J, L, O, S, T, Z
	}
	
	private final PieceID id;
	private int currentRotation = 0;
	private final Tuple[][] rotations;
	
	public Piece(PieceID id, Tuple[][] rotations) {
		assert rotations.length == 4;
		assert rotations[0].length == 4;
		assert rotations[1].length == 4;
		assert rotations[2].length == 4;
		assert rotations[3].length == 4;
		
		this.id = id;
		this.rotations = rotations;
	}
	
	public PieceID getId() {
		return id;
	}
	
	/**
	 * @param side -1 for left, +1 for right
	 */
	public void rotate(int side) {
		assert side == -1 || side == 1 : "-1 or 1";
		currentRotation += side;
		if (currentRotation > rotations.length - 1)
			currentRotation = 0;
		else if (currentRotation < 0)
			currentRotation = rotations.length - 1;
    }
	
	public Tuple getPositionOfBlock(int blockNumber) {
		assert blockNumber >= 0 && blockNumber < 4 : "Invalid block number: " + blockNumber;
		
        return rotations[currentRotation][blockNumber];
    }

	public int getWidth() {
		int min = 0, max = 0;
		
		for (int i = 0; i < 4; i++) {
			Tuple pos = getPositionOfBlock(i);
			if (pos.x > max)
				max = pos.x;
			if (pos.x < min)
				min = pos.x;
		}
		
		return max - min + 1;
	}
	
}
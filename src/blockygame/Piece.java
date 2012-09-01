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
	
}
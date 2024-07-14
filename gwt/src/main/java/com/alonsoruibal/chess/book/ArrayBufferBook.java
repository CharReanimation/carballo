package com.alonsoruibal.chess.book;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Move;
import com.google.gwt.typedarrays.shared.Uint8Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Polyglot opening book support via GWT
 * @author rui
 */
public class ArrayBufferBook implements Book {
	/**
	 * Logger for this class
	 */
	List<Integer> moves = new ArrayList<>();
	List<Integer> weights = new ArrayList<>();
	long totalWeight;
	
	private final Random random = new Random();

    Uint8Array arrayBuffer;

	public ArrayBufferBook(Uint8Array arrayBuffer) {
        this.arrayBuffer = arrayBuffer;
	}

	/**
	 * "move" is a bit field with the following meaning (bit 0 is the least significant bit)
	 *
	 * 		bits                meaning
	 * ===================================
     * 0,1,2               to file
     * 3,4,5               to row
     * 6,7,8               from file
	 * 9,10,11             from row
	 * 12,13,14            promotion piece
     * "promotion piece" is encoded as follows
     * none       0
     * knight     1
     * bishop     2
     * rook       3
     * queen      4
	 * @param move
	 * @return
	 */
	private String int2MoveString(int move) {
		StringBuilder sb = new StringBuilder();
		sb.append((char)('a' + ((move >> 6) & 0x7)));
		sb.append(((move >> 9) & 0x7) +1);
		sb.append((char)('a' + (move & 0x7)));
		sb.append(((move >> 3) & 0x7) +1);
		if (((move >> 12) & 0x7) != 0) sb.append("nbrq".charAt(((move >> 12) & 0x7) - 1));
		return sb.toString();
	}

	public void generateMoves(Board board) {
		final int KEY_BYTES = 8;
		final int MOVE_INT_BYTES = 2;
		final int WEIGHT_BYTES = 2;
		final int UNUSED_LEARN_FIELD_BYTES = 4;

		int pos = 0;
		totalWeight = 0;
		moves.clear();
		weights.clear();

		long key2Find = board.getKey();

		try {
			while (pos < arrayBuffer.length()) {
				long key = readLong(pos);
				pos += KEY_BYTES;

				if (key == key2Find) {
					int moveInt = readShort(pos);
					pos += MOVE_INT_BYTES;
					int weight = readShort(pos);
					pos += WEIGHT_BYTES + UNUSED_LEARN_FIELD_BYTES;

					int move = Move.getFromString(board, int2MoveString(moveInt), true);
					if (board.getLegalMove(move) != Move.NONE) {
						moves.add(move);
						weights.add(weight);
						totalWeight += weight;
					}
				} else {
					pos += KEY_BYTES; // skipBytes(KEY_BYTES);
				}
			}
		} catch (Exception ignored) {
		}
	}


	private long readLong(int pos) {
		return ((((long) arrayBuffer.get(pos)) << 56) |
				(((long) arrayBuffer.get(pos + 1)) << 48) |
				(((long) arrayBuffer.get(pos + 2)) << 40) |
				(((long) arrayBuffer.get(pos + 3)) << 32) |
				(((long) arrayBuffer.get(pos + 4)) << 24) |
				(((long) arrayBuffer.get(pos + 5)) << 16) |
				(((long) arrayBuffer.get(pos + 6)) << 8) |
				((long) arrayBuffer.get(pos + 7)));
	}

	private int readShort(int pos) {
		return (arrayBuffer.get(pos) << 8) | arrayBuffer.get(pos + 1);
	}

	public int getMove(Board board) {
		generateMoves(board);
        long randomWeight = Float.valueOf(random.nextFloat() * totalWeight).longValue();
		for (int i = 0; i < moves.size(); i++) {
			randomWeight -= weights.get(i);
			if (randomWeight <= 0) {
				return moves.get(i);
			}
		}
		return 0;
	}

}
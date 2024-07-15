package com.alonsoruibal.chess.pgn;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Move;

import java.util.Date;

public class PgnImportExport {

	/**
	 * Parses a PGN and does all the moves in a board
	 */
	public static void setBoard(Board b, String pgnString) {
		Game game = PgnParser.parsePgn(pgnString);
		if (game.getFenStartPosition() != null) {
			b.setFen(game.getFenStartPosition());
		} else {
			b.startPosition();
		}

		for (GameNode gameNode : game.getPv().variation) {
			if (gameNode instanceof GameNodeMove) {
				int move = Move.getFromString(b, ((GameNodeMove) gameNode).move, true);
				b.doMove(move);
			}
		}
	}

	public static String getPgn(PgnParams params) {
		return generatePgn(params);
	}

	private static String generatePgn(PgnParams params) {
		StringBuilder sb = new StringBuilder();
		Board board = params.getBoard();

		String whiteName = params.getWhiteName();
		String blackName = params.getBlackName();
		String event = params.getEvent();
		String site = params.getSite();
		String result = params.getResult();

		whiteName = (whiteName == null || whiteName.isEmpty()) ? "?" : whiteName;
		blackName = (blackName == null || blackName.isEmpty()) ? "?" : blackName;
		event = (event == null) ? "Chess Game" : event;
		site = (site == null) ? "-" : site;

		appendTag(sb, "Event", event);
		appendTag(sb, "Site", site);
		appendTag(sb, "Date", getCurrentDate());
		appendTag(sb, "Round", "?");
		appendTag(sb, "White", whiteName);
		appendTag(sb, "Black", blackName);

		if (result == null) {
			result = determineResult(board);
		}
		appendTag(sb, "Result", result);

		if (!Board.FEN_START_POSITION.equals(board.initialFen)) {
			appendTag(sb, "FEN", board.initialFen);
		}

		appendTag(sb, "PlyCount", String.valueOf(board.moveNumber - board.initialMoveNumber));
		sb.append("\n");

		sb.append(formatMoves(board, result));

		return sb.toString();
	}

	private static void appendTag(StringBuilder sb, String tagName, String tagValue) {
		sb.append(String.format("[%s \"%s\"]\n", tagName, tagValue));
	}

	private static String getCurrentDate() {
		Date d = new Date();
		return String.format("%d.%d.%d", d.getYear() + 1900, d.getMonth() + 1, d.getDate());
	}

	private static String determineResult(Board board) {
		String result = "*";
		switch (board.isEndGame()) {
			case 1:
				result = "1-0";
				break;
			case -1:
				result = "0-1";
				break;
			case 99:
				result = "1/2-1/2";
				break;
		}
		return result;
	}

	private static String formatMoves(Board board, String result) {
		StringBuilder line = new StringBuilder();
		for (int i = board.initialMoveNumber; i < board.moveNumber; i++) {
			line.append(" ");
			if ((i & 1) == 0) {
				line.append((i >>> 1) + 1).append(". ");
			}
			line.append(board.getSanMove(i));
		}
		line.append(" ").append(result);

		return wrapLine(line.toString(), 80);
	}

	private static String wrapLine(String line, int lineLength) {
		String[] tokens = line.split("[ \\t\\n\\x0B\\f\\r]+");
		StringBuilder sb = new StringBuilder();
		int length = 0;
		for (String token : tokens) {
			if (length + token.length() + 1 > lineLength) {
				sb.append("\n");
				length = 0;
			} else if (length > 0) {
				sb.append(" ");
				length++;
			}
			length += token.length();
			sb.append(token);
		}
		return sb.toString();
	}
}
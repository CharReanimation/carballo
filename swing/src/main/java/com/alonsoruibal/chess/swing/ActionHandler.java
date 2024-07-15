package com.alonsoruibal.chess.swing;

import com.alonsoruibal.chess.log.Logger;
import com.alonsoruibal.chess.search.SearchEngineThreaded;
import com.alonsoruibal.chess.search.SearchParameters;
import com.alonsoruibal.chess.pgn.PgnImportExport;
import com.alonsoruibal.chess.pgn.PgnParams;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ActionHandler {

    private static final Logger logger = Logger.getLogger("ChessApplet");

    private BoardJPanel boardPanel;
    private SearchEngineThreaded engine;
    private SearchParameters searchParameters;
    private JComboBox comboOpponent;
    private JComboBox comboTime;
    private JComboBox comboElo;
    private JComboBox comboPieces;
    private JComboBox comboBoards;
    private JTextField fenField;
    private JLabel message;
    private PgnDialog pgnDialog;
    private boolean flip;
    private boolean userToMove;
    private final int[] timeValues;
    private final int[] eloValues;
    private final String[] piecesValues;
    private final String[] boardsValues;

    private Map<String, Consumer<ActionEvent>> actionMap;

    public ActionHandler(BoardJPanel boardPanel, SearchEngineThreaded engine, SearchParameters searchParameters,
                         JComboBox comboOpponent, JComboBox comboTime, JComboBox comboElo,
                         JComboBox comboPieces, JComboBox comboBoards, JTextField fenField, JLabel message,
                         PgnDialog pgnDialog, boolean flip, boolean userToMove, int[] timeValues,
                         int[] eloValues, String[] piecesValues, String[] boardsValues) {
        this.boardPanel = boardPanel;
        this.engine = engine;
        this.searchParameters = searchParameters;
        this.comboOpponent = comboOpponent;
        this.comboTime = comboTime;
        this.comboElo = comboElo;
        this.comboPieces = comboPieces;
        this.comboBoards = comboBoards;
        this.fenField = fenField;
        this.message = message;
        this.pgnDialog = pgnDialog;
        this.flip = flip;
        this.userToMove = userToMove;
        this.timeValues = timeValues;
        this.eloValues = eloValues;
        this.piecesValues = piecesValues;
        this.boardsValues = boardsValues;

        initializeActionMap();
    }

    private void initializeActionMap() {
        actionMap = new HashMap<>();
        actionMap.put("restart", this::handleRestart);
        actionMap.put("back", this::handleBack);
        actionMap.put("pgn", this::handlePgn);
        actionMap.put("fen", this::handleFen);
        actionMap.put("go", this::handleGo);
        actionMap.put("opponent", this::handleOpponent);
        actionMap.put("time", this::handleTime);
        actionMap.put("elo", this::handleElo);
        actionMap.put("flip", this::handleFlip);
        actionMap.put("pieces", this::handlePieces);
        actionMap.put("boards", this::handleBoards);
    }

    public void actionPerformed(ActionEvent oAE) {
        String actionCommand = oAE.getActionCommand();
        Consumer<ActionEvent> action = actionMap.get(actionCommand);
        if (action != null) {
            action.accept(oAE);
        }
    }

    private void handleRestart(ActionEvent oAE) {
        logger.debug("restart!!!!");
        boardPanel.unhighlight();
        userToMove = true;
        engine.stop();
        engine.getBoard().startPosition();
        checkUserToMove();
    }

    private void handleBack(ActionEvent oAE) {
        boardPanel.unhighlight();
        userToMove = true;
        engine.stop();
        logger.debug("undoing move");
        engine.getBoard().undoMove();
        update(false);
    }

    private void handlePgn(ActionEvent oAE) {
        if (!engine.isSearching()) {
            int o = comboOpponent.getSelectedIndex();
            String whiteName = (o == 0 || o == 3 ? "Computer" : "Player");
            String blackName = (o == 1 || o == 3 ? "Computer" : "Player");

            // PgnParams getPgn
            PgnParams params = new PgnParams(engine.getBoard(), whiteName, blackName);
            pgnDialog.setText(PgnImportExport.getPgn(params));
            pgnDialog.setVisible(true);
        }
    }


    private void handleFen(ActionEvent oAE) {
        boardPanel.unhighlight();
        userToMove = true;
        engine.stop();
        engine.getBoard().setFen(fenField.getText());
        update(false);
    }

    private void handleGo(ActionEvent oAE) {
        if (!engine.isSearching()) checkUserToMove();
    }

    private void handleOpponent(ActionEvent oAE) {
        if (!engine.isSearching()) checkUserToMove();
    }

    private void handleTime(ActionEvent oAE) {
        searchParameters.setMoveTime(timeValues[comboTime.getSelectedIndex()]);
    }

    private void handleElo(ActionEvent oAE) {
        int engineElo = eloValues[comboElo.getSelectedIndex()];
        logger.debug("Setting elo " + engineElo);
        engine.getConfig().setLimitStrength(true);
        engine.getConfig().setElo(engineElo);
    }

    private void handleFlip(ActionEvent oAE) {
        flip = !flip;
        boardPanel.unhighlight();
        boardPanel.setFen(boardPanel.getLastFen(), flip, true);
    }

    private void handlePieces(ActionEvent oAE) {
        PieceJLabel.style = piecesValues[comboPieces.getSelectedIndex()];
        if (boardPanel != null) {
            boardPanel.setFen(boardPanel.getLastFen(), flip, true);
        }
    }

    private void handleBoards(ActionEvent oAE) {
        ImageLoader.loadImages(this.getClass().getResource(boardsValues[comboBoards.getSelectedIndex()]));
        if (boardPanel != null) {
            boardPanel.setFen(boardPanel.getLastFen(), flip, true);
        }
    }

    private void checkUserToMove() {
        // Implement checkUserToMove logic
    }

    private void update(boolean thinking) {
        // Implement update logic
    }
}

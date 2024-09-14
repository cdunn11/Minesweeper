package Minesweeper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class MinesweeperModel implements Serializable {
    int numRows;
    int numCols;
    int mineCount;
    MinesweeperTile[][] board;
    Random random;
    int tilesClicked;
    boolean gameOver;
    ArrayList<MinesweeperTile> mineList = new ArrayList<>();
    JLabel textLabel;

    public MinesweeperModel(int numRows, int numCols, int mineCount, JLabel textLabel) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.mineCount = mineCount;
        this.board = new MinesweeperTile[numRows][numCols];
        this.random = new Random();
        this.tilesClicked = 0;
        this.gameOver = false;
        this.textLabel = textLabel;
        this.mineList = new ArrayList<>();
    }

    public void initializeBoard() {
        board = new MinesweeperTile[numRows][numCols];
        mineList.clear();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                board[r][c] = new MinesweeperTile(r, c);
            }
        }

        setMines();
    }

    public MinesweeperTile[][] getBoard() {
        return board;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getTilesClicked() {
        return tilesClicked;
    }

    public int getMineCount() {
        return mineCount;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public JLabel getTextLabel() {
        return textLabel;
    }

    public ArrayList<MinesweeperTile> getMineList() {
        return mineList;
    }

    public void setMines() {
        int mineLeft = mineCount;
        mineList.clear();
    
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
    
            MinesweeperTile tile = board[r][c];
            if (!tile.isMine()) {
                tile.setMine(true);
                mineList.add(tile);
                mineLeft--;
            }
        }
    }

    public void revealMines() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MinesweeperTile tile = board[r][c];
                if (tile.isMine()) {
                    tile.setText("M");
                }
            }
        }

        gameOver = true;
    }

    public void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }

        MinesweeperTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;

        //top 3
        minesFound += countMine(r-1, c-1);
        minesFound += countMine(r-1, c);
        minesFound += countMine(r-1, c+1);

        //left and right
        minesFound += countMine(r, c-1);
        minesFound += countMine(r, c+1);

        //bottom 3
        minesFound += countMine(r+1, c-1);
        minesFound += countMine(r+1, c);
        minesFound += countMine(r+1, c+1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        }
        else {
            tile.setText("");
            
            //top 3
            checkMine(r-1, c-1);  
            checkMine(r-1, c);
            checkMine(r-1, c+1);   

            //left and right
            checkMine(r, c-1);
            checkMine(r, c+1); 

            //bottom 3
            checkMine(r+1, c-1);
            checkMine(r+1, c);  
            checkMine(r+1, c+1);
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
        }
    }

    public int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }
}

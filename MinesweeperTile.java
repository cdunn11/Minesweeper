package Minesweeper;

import javax.swing.*;

public class MinesweeperTile extends JButton {
    
    private int row;
    private int col;
    private boolean isMine;

    public MinesweeperTile(int row, int col) {
        this.row = row;
        this.col = col;
        this.isMine = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }
}

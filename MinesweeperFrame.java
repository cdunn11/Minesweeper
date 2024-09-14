package Minesweeper;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MinesweeperFrame {
    private JFrame frame;
    private MinesweeperModel model;
    private JLabel textLabel;
    private JPanel textPanel;
    private JPanel boardPanel;

    public MinesweeperFrame(int numRows, int numCols, int mineCount) {
        this.frame = new JFrame("Minesweeper");
        this.textLabel = new JLabel();
        this.model = new MinesweeperModel(numRows, numCols, mineCount, textLabel);
        this.textPanel = new JPanel();
        this.boardPanel = new JPanel();

        frame.setVisible(true);
        model.initializeBoard();

        initializeFrame();
        initializeMenuBar();
        initializeTextPanel();
        initializeBoardPanel();
    }

    public void initializeFrame() {
        frame.setSize(model.getNumCols() * 40, model.getNumRows() * 40);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
    }

    public void initializeMenuBar() {

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JFileChooser fileChooser = new JFileChooser();

        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem quitMenuItem = new JMenuItem("Quit");

        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] difficulties = {"Easy", "Medium", "Hard"};
                String difficulty = (String) JOptionPane.showInputDialog(frame, "Select Difficulty:",
                        "New Game", JOptionPane.QUESTION_MESSAGE, null,  difficulties, difficulties[0]);

                switch (difficulty) {
                    case "Easy":
                        frame.dispose();
                        new MinesweeperFrame(9, 9, 10);
                        break;
                    case "Medium":
                        frame.dispose();
                        new MinesweeperFrame(16, 16, 40);
                        break;
                    case "Hard":
                        frame.dispose();
                        new MinesweeperFrame(16, 30, 99);
                        break;
                }
            }
        });

        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int option = fileChooser.showSaveDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileOutputStream fileOut = new FileOutputStream(file);
                        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                        objectOut.writeObject(model);
                        objectOut.close();
                        fileOut.close();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                }
            }
        });

        loadMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileInputStream fileIn = new FileInputStream(file);
                        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                        Object loadedObject = objectIn.readObject();
                
                        if (loadedObject instanceof MinesweeperModel) {
                            model = (MinesweeperModel) loadedObject;
                            updateGameBoard();
                        }

                        objectIn.close();
                        fileIn.close();
                    } catch (IOException err) {
                        JOptionPane.showMessageDialog(frame, "Could not load file",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (ClassNotFoundException err) {
                        err.printStackTrace();
                    }
                }
            }
        });

        quitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(newMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
    }

    public void initializeTextPanel() {
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("");
        textLabel.setOpaque(true);
        textPanel.setLayout(new BorderLayout());
        
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);
    }

    public void initializeBoardPanel() {
        boardPanel.setLayout(new GridLayout(model.getNumRows(), model.getNumCols()));
    
        MinesweeperTile[][] board = model.getBoard();
        for (int r = 0; r < model.getNumRows(); r++) {
            for (int c = 0; c < model.getNumCols(); c++) {
                MinesweeperTile tile = board[r][c];
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 35));
    
                tile.addMouseListener(new MinesweeperMouseAdapter(model, this, tile));
    
                boardPanel.add(tile);
            }
        }

        frame.add(boardPanel);
        frame.setVisible(true);
    }

    private void updateGameBoard() {
        boardPanel.removeAll();
    
        MinesweeperTile[][] board = model.getBoard();
        for (int r = 0; r < model.getNumRows(); r++) {
            for (int c = 0; c < model.getNumCols(); c++) {
                MinesweeperTile tile = board[r][c];
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 35));

                tile.addMouseListener(new MinesweeperMouseAdapter(model, this, tile));

                boardPanel.add(tile);
            }
        }
    
        boardPanel.validate();
        boardPanel.repaint();
    }

    public class MinesweeperMouseAdapter extends MouseAdapter {
        private MinesweeperModel model;
        private MinesweeperTile tile;

        public MinesweeperMouseAdapter(MinesweeperModel model, MinesweeperFrame frame, MinesweeperTile tile) {
            this.model = model;
            this.tile = tile;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (model.isGameOver()) {
                return;
            }

            if (e.getButton() == MouseEvent.BUTTON1) {
                if (tile.getText().equals("")) {
                    if (model.getMineList().contains(tile)) {
                        model.revealMines();
                        textLabel.setText("Boom! Game over.");
                    } else {
                        model.checkMine(tile.getRow(), tile.getCol());
                    }
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if (tile.getText().equals("") && tile.isEnabled()) {
                    tile.setText("F");
                } else if (tile.getText().equals("F")) {
                    tile.setText("");
                }
            }
        }
    }
}

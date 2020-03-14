// @author Vsevolod Ivanov
// @author Isabelle Charette
// @brief Game view class which handles the Kakuro game interface.

package kakuro.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;

import kakuro.controllers.GameController;
import kakuro.controllers.GameController.UserActions;
import kakuro.controllers.MenuBarController;
import kakuro.controllers.ChronoController;
import kakuro.core.Cell;
import kakuro.core.LinePanel;

public class GameView
{
    private GameController controller;
     
    //UI components
    private JTextField[][] cells;
    private JPanel currentBoard;
    
    //Main application frame and properties
    private JFrame frame;
    private int gridSizeX;
    private int gridSizeY;
    
    //Number formatter for displaying
    NumberFormat numberFormat = NumberFormat.getInstance();
    NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
     
    public GameView(final GameController controller, Boolean X11, JComponent chronoLabel, JComponent menuBar)
    {
        //Initialize number formatter
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setMinimum(1);
        numberFormatter.setMaximum(9);
        
        if (controller != null)
        {
            this.controller = controller;
            gridSizeX = controller.model.getRows();
            gridSizeY = controller.model.getColumns();
        }
        
        if (X11) {
            frame = new JFrame("KAKURO");
            //buttonMenu = new ButtonMenuView(frame, gridSizeX, gridSizeY, controller);
            
            //Initialize the application frame
            int frameSize = 60;
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            int x = frameSize*gridSizeX;
            int y = frameSize*gridSizeY;
            frame.setSize(x, y);
            frame.setResizable(false);
            
            //Create a timer at the top
            frame.getContentPane().add(chronoLabel, BorderLayout.BEFORE_FIRST_LINE);
            //Main game UI stays at the center
            currentBoard = getBoardUI(controller.model.board);
            frame.getContentPane().add(currentBoard);
            //Button menu stays at the bottom 
            frame.getContentPane().add(menuBar, BorderLayout.AFTER_LAST_LINE);
            
            frame.pack();
            frame.setSize(x,y);
            frame.setVisible(true);
        }
    }
    
    /*Public methods - Interface of GameView*/
    public JTextField[][] getSavedInput(){
        return cells;
    }
    
    public void updateView() {
        //If a panel is already attached to the frame, remove it
        JPanel newBoardPanel = getBoardUI(controller.model.board);
        if(currentBoard != null)
            frame.getContentPane().remove(currentBoard);
        
        //Save a reference to the new panel
        frame.getContentPane().add(newBoardPanel);
        currentBoard = newBoardPanel;;
    }
    
    public void showBoard() {
        currentBoard.setVisible(true);
    }
    
    public void hideBoard() {
        currentBoard.setVisible(false);
    }

    //Number formatter methods
    public int getMinNumberValid() {
        return (int) numberFormatter.getMinimum();
    }
    
    public Object getNumberFormatterClassType() {
        return numberFormatter.getClass();
    }
    
    public int getMaxNumberValid() {
        return (int) numberFormatter.getMaximum();
    }
    
    /*Private methods*/
    private JPanel getBoardUI(Cell[][] board) {
        //creating grid of cells
        cells = new JTextField[gridSizeX][gridSizeY];
        JPanel panel = new JPanel(new GridLayout(gridSizeX,gridSizeY));
        //creating window of the game
     
        //this allows temporary invalid input, particularly to be able to delete and try again
        //if invalid input, when clicking onto another cell, the input will be deleted
        numberFormatter.setAllowsInvalid(true);

        //identifies type of each cell and populates it
        //input or non-playable
        for(int row = 0; row < gridSizeX; row++)
        {
            for(int column = 0; column < gridSizeY; column++)
            {
                JFormattedTextField textField = null;

                //tracking the type of each cell
                Cell cell = board[row][column];
                //adding extra panel that will overlay the cells that are non-playable with game level numbers and diagonal line
                JPanel diagonalPanel = null;

                //according to type of cell, populate
                switch (cell.getType())
                {
                    case EMPTY:
                        textField = new JFormattedTextField(numberFormatter);
                        settingTextField(textField);
                        textField.setBorder(new LineBorder(Color.GRAY,1));
                        panel.add(textField);
                        break;

                    case INPUT:
                        textField = new JFormattedTextField(numberFormatter);
                        textField.setHorizontalAlignment(JTextField.CENTER);
                        textField.setBorder(new LineBorder(Color.GRAY,1));
                        //When you load a game, there is some data exists. We have to check to make sure we are displaying the saved input
                        if(cell.getFirstValue()!=-1)
                            textField.setValue(cell.getFirstValue());
                        panel.add(textField);
                        break;

                    case FILLED01:
                        textField = new JFormattedTextField(cell.getSecondValue());
                        settingTextField(textField);
                        //adding diagonal line in board cell
                        diagonalPanel = new LinePanel(new BorderLayout(), textField, true);
                        diagonalPanel.setBorder(new LineBorder(Color.GRAY,1));
                        panel.add(diagonalPanel);
                        break;

                    case FILLED10:
                        textField = new JFormattedTextField(cell.getFirstValue());
                        settingTextField(textField);
                        //adding diagonal line in board cell
                        diagonalPanel = new LinePanel(new BorderLayout(), textField, false);
                        diagonalPanel.setBorder(new LineBorder(Color.GRAY,1));
                        panel.add(diagonalPanel);
                        break;

                    case FILLED11:
                        textField = new JFormattedTextField(cell.getFirstValue());
                        JFormattedTextField textFieldRIGHT = new JFormattedTextField(cell.getSecondValue());
                        settingTextField(textField);
                        settingTextField(textFieldRIGHT); 
                        //using constructor that expects two text values to place in board cell
                        diagonalPanel = new LinePanel(new BorderLayout(), textField, textFieldRIGHT);
                        diagonalPanel.setBorder(new LineBorder(Color.GRAY,1));
                        panel.add(diagonalPanel);
                        break;

                    default:
                        break;
                }
                
                //placing textfield value in input array to track user input
                this.cells[row][column] = textField;
            }
        }
        
        return panel;
    }
    
    private void settingTextField(JTextField txt) {
        txt.setBackground(Color.black);
        txt.setForeground(Color.white);
        txt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        txt.setEditable(false);
    }
}

package parser;

import javax.swing.*;

import types.InvalidInputException;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * This is the windowed application for syntax checking a Java program
 * @author Michael Smith and Nathan Jean
 */
public class SimpleWindow{
    
    private final int WINDOW_WIDTH = 1500;//height of the window
    private final int WINDOW_HEIGHT = 750;//height of the window
    
    //components
    private JLabel messageLabel;//gives instructions for inputing file name
    private JLabel instructionLabel;//gives instructions for using application
    private JTextField inputTextField;//field to get filename
    private static JTextArea outputField;//field to display parsed results
    private JButton checkButton;//button to check syntax
    private JButton clearButton;//button to clear fields
    private JButton findButton;//button to select a file from a file browser window
    private JPanel northPanel;//panel to contain instructions, buttons, and inputTextField
    private JPanel centerPanel;//panel to contain outputField
    private JScrollPane scrollPane;//to make the outputField scrollable
    private JPanel northPanelUpper;//subpanel for arranging instructions
    private JPanel northPanelMid;//subpanel for arranging instructions and inputTextField
    private JPanel northPanelLower;//subpanel for arranging buttons
    private JPanel mainPanel;//main panel for holding north and center panels
    private JFrame window;//window to hold all content
    private ArrayDeque<String> outputQueue;//queue to hold the lines of output
    
    
    /**
     * constructor
     */ 
    public SimpleWindow(){
        
        messageLabel = new JLabel("Enter the path of the file to check:");//
        instructionLabel = new JLabel(
            "Welcome to the Java Syntax Checker!  Enter the path of the file to check, or select \"Find File\" to select a file.  Click \"Check\" to see the parse tree.");
        inputTextField = new JTextField(20);//Create a text field for the input file
        outputField = new JTextArea(40, 75);//create a output field
        checkButton = new JButton("Check Syntax");//create button to check syntax
        clearButton = new JButton("Clear");//create clear button
        findButton = new JButton("Find File");//create find file button
        northPanel = new JPanel();//north panel
        centerPanel = new JPanel();//centerPanel
        scrollPane = new JScrollPane(outputField);//Scrollable text field
        northPanelUpper = new JPanel();//upper part of north panel
        northPanelMid = new JPanel();//middle part of north panel
        northPanelLower = new JPanel();//lower part of north panel
        window = new JFrame();//Create a window.
        mainPanel = new JPanel();//create a mainPanel to squish everything in
    }//end constructor
    
    
    public void buildWindow() throws IOException {


        //register action listeners
        checkButton.addActionListener(new checkButtonListener());
        clearButton.addActionListener(new clearButtonListener());
        findButton.addActionListener(new findButtonListener());


        //arrange the fields, buttons, and text
        northPanelUpper.add(instructionLabel);
        northPanelMid.add(messageLabel);
        northPanelMid.add(inputTextField);
        northPanelLower.add(findButton);
        northPanelLower.add(checkButton);
        northPanelLower.add(clearButton);

        northPanel.setLayout(new BorderLayout());
        northPanel.add(northPanelUpper, BorderLayout.NORTH);
        northPanel.add(northPanelMid, BorderLayout.CENTER);
        northPanel.add(northPanelLower, BorderLayout.SOUTH);

        centerPanel.add(scrollPane);
        outputField.setEditable(false);//make output read only


        //make mainPanel with stuff in it
        mainPanel.setLayout(new BorderLayout());//change layout to a border layout

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);


        //Set the title.
        window.setTitle("Java Syntax Checker");

        //Set the size of the window.
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        //Specify what happens when the close button is clicked.
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //add mainPanel
        window.add(mainPanel);

        //Display the window.
        window.setVisible(true);
    }//end of buildWindow

    //main
    public static void main(String args[]){
        SimpleWindow theWindow = new SimpleWindow();
        try{
            theWindow.buildWindow();//start the window
        }
        catch(IOException exception){
            outputField.setText("IO Error.");
        }

    }

    /**
     * action to take when find file button is pushed
     */

    private class findButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JFileChooser fileChooser = new JFileChooser();//create a file chooser window
            int action = JFileChooser.ERROR_OPTION;//action to take

            action = fileChooser.showOpenDialog(null);

            if(action == JFileChooser.APPROVE_OPTION){
                File selectedFile = fileChooser.getSelectedFile();//get the selected file
                inputTextField.setText(selectedFile.getPath());//set the file path (in the text field) to the selected file's path
            }

        }
    }//end findButtonListener

    /**
     * action to take when check button is pushed
     */
    private class checkButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            String filename = inputTextField.getText();
            File aFile = new File(filename);//make a File object with the filename of the argument
            String inputString = "";
            try{
                inputString = fileToString(aFile);//convert the file to a single string
            }
            catch(IOException exception){
                outputField.setText("IO Error.");
            }
            
            outputField.setText("");//reset outputField
            
            Parser parser = new Parser(inputString);
            int lineNumber = 1;//for line numbering
            
            try {
                parser.start();
                outputQueue = parser.getOutputQueue();
                
                while (!outputQueue.isEmpty()) {//get output one line at a time
                    outputField.append(lineNumber + ": " + outputQueue.remove());
                    lineNumber++;
                }
            } catch (InvalidInputException e1) {//if syntax error, display error
                outputQueue = parser.getOutputQueue();
                outputField.setText("");
                while (!outputQueue.isEmpty()) {
                    outputField.append(lineNumber + ": " + outputQueue.remove());
                    lineNumber++;
                }
                outputField.append(parser.getErrorMessage());
            } // end try/catch
            
            
        }//end actionPreformed


        /**
         * This method takes a file and condenses it into a single string.
         * @param theFile The file to convert to a string.
         * @return The string containing the contents of the file 
         */
        private String fileToString(File theFile) throws IOException{
            String returnString = "";//the string to return
            Scanner fileReader = new Scanner(theFile);//set up a Scanner to read from the file
            while(fileReader.hasNext()){//while the file still has a next line
                returnString = returnString + fileReader.nextLine() + '\n';
            }
            fileReader.close();//close the file

            return returnString;
        }//end fileToString method
    }//end checkButtonListener

    /**
     * action to take when clear button is pushed
     */
    private class clearButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            outputField.setText("");
            inputTextField.setText("");
        }
    }//end clearButtonListener
}//end of file

//**********************************************************************
//Programmer: Nathan Jean
//Date: 4/21/2017
//Name: SimpleWindow
//Purpose:  This is a GUI for our project
//**********************************************************************
package parser;

import javax.swing.*;

import types.InvalidInputException;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SimpleWindow{
	//declarations
	final int WINDOW_WIDTH = 1500;//height of the window
	final int WINDOW_HEIGHT = 750;//height of the window
	JLabel messageLabel = new JLabel("Enter the path of the file to check:");//create label
	JLabel instructionLabel = new JLabel(
		"Welcome to the Java Syntax Checker!  Enter the path of the file to check, or select \"Find File\" to select a file.  Click \"Check\" to see the parse tree.");
	JTextField inputTextField = new JTextField(20);//Create a text field for the input file
	JTextArea outputField = new JTextArea(40, 75);//create a output field
	JButton checkButton = new JButton("Check Syntax");//create button to check syntax
	JButton clearButton = new JButton("Clear");//create clear button
	JButton findButton = new JButton("Find File");//create find file button
	JPanel northPanel = new JPanel();//north panel
	//JPanel southPanel = new JPanel();//south panel
	JPanel centerPanel = new JPanel();//centerPanel
	JScrollPane scrollPane = new JScrollPane(outputField);//Scrollable text field
	JPanel northPanelUpper = new JPanel();//upper part of north panel
	JPanel northPanelMid = new JPanel();//middle part of north panel
	JPanel northPanelLower = new JPanel();//lower part of north panel


	JFrame window = new JFrame();//Create a window.

	ArrayDeque<String> outputQueue;

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
		JPanel mainPanel = new JPanel();//create a mainPanel to squish everything in
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
			System.out.println("IO Error.");
			System.exit(1);
		}

		//System.exit(0);
	}

	/**
	 * action to take when find file button is pushed
	 */

	private class findButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JFileChooser fileChooser = new JFileChooser();
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
			//System.out.println("Starting parser.");
			//System.out.printf("Here is the file to parse.\n%s\n", inputString);
			Parser parser = new Parser(inputString);//this is where it has problems
			//System.out.println("created parser.");
			try {
				parser.start();
				outputQueue = parser.getOutputQueue();
				outputField.setText("");
				while (!outputQueue.isEmpty()) {
					outputField.append(outputQueue.remove());
				}
			} catch (InvalidInputException e1) {
				outputQueue = parser.getOutputQueue();
				while (!outputQueue.isEmpty()) {
					outputField.append(outputQueue.remove());
				}
				outputField.append(parser.getErrorMessage());
			}
			
		}

		public String fileToString(File theFile) throws IOException{
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

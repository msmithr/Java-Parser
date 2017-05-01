package parser;

import java.io.IOException;

public class Driver {

    public static void main(String args[]){
        ParserWindow theWindow = new ParserWindow();
        try{
            theWindow.buildWindow();//start the window
        } // end try
        catch(IOException exception){
            theWindow.outputField.setText("IO Error.");
		} // end catch
	} // end main
} // end Driver

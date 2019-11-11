package speech_assistant;
/* For this class to work do the following.
 * 1. In the "resource" folder there will be "Free TTS 1.2.2" folder.
 * 2. In "Free TTS 1.2.2" folder there will be a few .jar files
 * 3. Configure JRE System Library and add those .jar files to the JRE System library.
 */

// Import required packages for the program to work
import java.awt.EventQueue;
import java.sql.*;
import com.sun.speech.freetts.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class Main 
{
	// Declare a JFrame variable
	private JFrame frame;
	
	// Declare a JTextField variable
	private JTextField textField,listfield,from,fromdisplay,to,todisplay;
	
	// Declare JButton variables
	private JButton btnA,btnB,btnC,btnD,btnE,btnF,btnG,btnH,btnI,btnJ,btnK,btnL,btnM;
	private JButton btnN,btnO,btnP,btnQ,btnR,btnS,btnT,btnU,btnV,btnW,btnX,btnY,btnZ;
	private JButton btnspace,btnbackspace,btnspeak,btnEmail,btnAlert,btnList,btndone,btnok;
	
	// Declare JList variable
	private JList list;
	
	// Declare a JScrollPane variable
	private JScrollPane scrollPane, scrollPane_2;
	
	// Declare a JPasswordField
	private JPasswordField password;
	
	// Declare a JLabel 
	private JLabel from1,password1,to1,from2,to2;	
	
	// Declare a JTextArea field
	private JTextArea errorarea;
	
	// Declare an integer row, column, and index variable for step scanning function
	private int row=0,coloumn=-1,index=-1;
	
	// Declare a boolean variable for list scanning 
	private boolean listvalue=false;
	
	// Declare an integer variable send to check if message was send
	private int send;
	
	// Declare a connection object to connect to database
	Connection conn=null;
	
	// Declare a PreaparedStatement object to send queries to database
	PreparedStatement pst=null;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					Main window = new Main();
					window.frame.setVisible(true);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	
	/********** Function for suggestion of words while typing **********/
	// This function is called every time the user enter a letter.
	// The parameter of the function is the entire text that is written in the text field
	public void suggest(String text)
	{
		try
		{
			// Create a new model of the list
			DefaultListModel DLM=new DefaultListModel();
			
			// The text that is typed in the text field we will split it based on spaces,
			// and we will store each word in the array parts[]
			String parts[]=text.split(" ");
			
			// We will find the length of the array parts[]
			int last=parts.length;
			
			// Take the last word of the array parts[], i.e. the last word typed by user,
			// as we need to find the suggestions for it.
			String lastword=parts[last-1];
			
			// Concatenate % to the end of the typed word
			lastword=lastword+'%';
			
			// Use an SQL select query to find the list of words similar to the one typed by the user,
			// This is a simple pattern matching query
			String query="select * from Savedwords where words like ? order by count desc";
			
			// Create a prepared statement from the query
			pst=conn.prepareStatement(query);
			
			// Complete the query
			pst.setString(1,lastword);
			
			// Execute the query
			ResultSet rs=pst.executeQuery();
			
			// Take each element of the result set, i.e. take each element returned by the query
			while(rs.next())
			{
				// Add each element to the list model
				DLM.addElement(rs.getString("words"));
			}
			
			// Set the new model of the list field,
			// i.e. in the list field show all the  words similar to the one typed by the user
			list.setModel(DLM);
			
			// Close the prepared statement object
			pst.close();
			
			// Close the result set object
			rs.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/******************************/
	
	/********** Function to print all the words in the database **********/
	// This function is called when the main program executes so that the user will have a list of words to choose from.
	public void extractall()
	{
		try
		{
			// Create a model of the list
			DefaultListModel DLM=new DefaultListModel();
			
			// A query to select all the words from the database
			// Select all words and rearrange them by decreasing order of their count value
			// count - it is variable to keep track of how many times the word has been used
			String query="select * from Savedwords order by count desc";
			
			// Make a prepared statement using the query
			pst=conn.prepareStatement(query);
			
			// Generate the results of the query and store it in result set
			ResultSet rs=pst.executeQuery();
			
			// Iterate through each result set
			while(rs.next())
			{
				// Add each 'word' to the list model
				// Our data base that store frequently used words have two columns:
				// words - to keep track of all the words that have been typed
				// count - to count how many times the word has been typed
				DLM.addElement(rs.getString("words"));
			}
			
			// Change the current model of the list to new model
			list.setModel(DLM);
			
			// Close prepared statement
			pst.close();
			
			// Close result set
			rs.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/********************/
	
	
	/********** Saving the words into the database **********/
	// This function will be called when the speak button is pressed	
	// The parameter 'text' is the sentence that is spoken by software which is passed to the function
	public void save(String text)
	{
		try
		{
			// Split the sentence using space and take each word
			for(String w:text.split("\\s"))
			{
				// Check if the word length is greater than 3.
				// If it is greater than 3, save word to database
				if(w.length()>=3)
				{
					// Make a query. 
					// This query will select all words from the database, that is similar to the word typed
					String query="select * from Savedwords where words = ?";
					
					// Make a prepared statement
					pst=conn.prepareStatement(query);
					
					// Complete the prepared statement
					pst.setString(1,w);
					
					// Execute the query and store result in result set
					ResultSet rs=pst.executeQuery();
					
					// If the word already exist, just increment its count value
					if(rs.next())
					{
						// Make a query that will update the count of the word
						query="update Savedwords set count=count+1 where words = ?";
						
						// Create a prepared statement
						pst=conn.prepareStatement(query);
						
						// Compete the prepared statement
						pst.setString(1,w);
						
						// Execute the query
						pst.execute();
						
						// Close the prepared statement
						pst.close();
						
						// Close the result set
						rs.close();
					}
					// If the word does not exist, insert the word to database and set count value to 1
					else
					{
						// Make a query to insert word into database
						query="insert into Savedwords values(?,?)";
						
						// Make a prepared statement with query
						pst=conn.prepareStatement(query);
						
						// Complete the prepared statement and set the first parameter to word
						pst.setString(1, w);
						
						// Complete the prepared statement and set second parameter to count i.e. 1
						pst.setInt(2, 1);
						
						// Execute the prepared statement
						pst.execute();
						
						// Close the prepared statement
						pst.close();
						
						// Close the result set
						rs.close();
					}
				}
			}
		}
		catch(Exception e2) 
		{	e2.printStackTrace();	}
	}
	/********************/			
	
	
	/********** Highlighting rows - functions *********/	
	/* 	ALERT - SPEAK - BACKSPACE - LIST - EMAIL - Y - Z
	   	A - B - C - D - E - F - G - H
		I - J - K - L - M - N - O - P
	 	Q - R - S - T - U - V - W - X
	 */
	
	// Highlight row 1 
	public void row1()
	{
		// To highlight row 1 we must set its previous row i.e. letters of row 4 to white
		btnQ.setBackground(Color.white);
		btnR.setBackground(Color.white);
		btnS.setBackground(Color.white);
		btnT.setBackground(Color.white);
		btnU.setBackground(Color.white);
		btnV.setBackground(Color.white);
		btnW.setBackground(Color.white);
		btnX.setBackground(Color.white);
		
		// Highlight row 1 to red color
		btnAlert.setBackground(Color.red);
		btnspeak.setBackground(Color.red);
		btnspace.setBackground(Color.red);
		btnbackspace.setBackground(Color.red);
		btnList.setBackground(Color.red);
		btnEmail.setBackground(Color.red);
		btnY.setBackground(Color.red);
		btnZ.setBackground(Color.red);
	}
	
	// Highlight row 2
	public void row2()
	{
		// To highlight row 2 we must rest the color of previous row i.e. row 1 to white
		btnAlert.setBackground(Color.white);
		btnspeak.setBackground(Color.white);
		btnspace.setBackground(Color.white);
		btnbackspace.setBackground(Color.white);
		btnList.setBackground(Color.white);
		btnEmail.setBackground(Color.white);
		btnY.setBackground(Color.white);
		btnZ.setBackground(Color.white);
		
		// Set color of row 2 to red
		btnA.setBackground(Color.red);
		btnB.setBackground(Color.red);
		btnC.setBackground(Color.red);
		btnD.setBackground(Color.red);
		btnE.setBackground(Color.red);
		btnF.setBackground(Color.red);
		btnG.setBackground(Color.red);
		btnH.setBackground(Color.red);
	}
	
	// Highlight row 3
	public void row3()
	{
		// To highlight row 3 we must rest the color of previous row i.e. row 2 to white
		btnA.setBackground(Color.white);
		btnB.setBackground(Color.white);
		btnC.setBackground(Color.white);
		btnD.setBackground(Color.white);
		btnE.setBackground(Color.white);
		btnF.setBackground(Color.white);
		btnG.setBackground(Color.white);
		btnH.setBackground(Color.white);
		
		// Set color of row 3 to red
		btnI.setBackground(Color.red);
		btnJ.setBackground(Color.red);
		btnK.setBackground(Color.red);
		btnL.setBackground(Color.red);
		btnM.setBackground(Color.red);
		btnN.setBackground(Color.red);
		btnO.setBackground(Color.red);
		btnP.setBackground(Color.red);
	}
	
	// Highlight row 4
	public void row4()
	{
		// To highlight row 4 we must rest the color of previous row i.e. row 3 to white
		btnI.setBackground(Color.white);
		btnJ.setBackground(Color.white);
		btnK.setBackground(Color.white);
		btnL.setBackground(Color.white);
		btnM.setBackground(Color.white);
		btnN.setBackground(Color.white);
		btnO.setBackground(Color.white);
		btnP.setBackground(Color.white);
		
		// Highlight row 4 to red
		btnQ.setBackground(Color.red);
		btnR.setBackground(Color.red);
		btnS.setBackground(Color.red);
		btnT.setBackground(Color.red);
		btnU.setBackground(Color.red);
		btnV.setBackground(Color.red);
		btnW.setBackground(Color.red);
		btnX.setBackground(Color.red);
	}
	/********************/
	
	/********** Functions to indicate which row has been selected **********/
	// Highlight first button of row 1 i.e. alert, if the first row has been selected
	public void alert()
	{
		// Set alert button color to red
		btnAlert.setBackground(Color.red);
		
		// Set color of every other button of row 1 to white
		btnspeak.setBackground(Color.white);
		btnspace.setBackground(Color.white);
		btnbackspace.setBackground(Color.white);
		btnList.setBackground(Color.white);
		btnEmail.setBackground(Color.white);
		btnY.setBackground(Color.white);
		btnZ.setBackground(Color.white);
		
	}
	
	// Highlight first button of row 2 i.e. A, if the second row has been selected
	public void A()
	{
		// Set color of A button to red
		btnA.setBackground(Color.red);
		
		// Set color of every other button of row 2 to white
		btnB.setBackground(Color.white);
		btnC.setBackground(Color.white);
		btnD.setBackground(Color.white);
		btnE.setBackground(Color.white);
		btnF.setBackground(Color.white);
		btnG.setBackground(Color.white);
		btnH.setBackground(Color.white);
	}
	
	// Highlight first button of row 3 i.e. I, if the third row has been selected
	public void I()
	{
		// Set color of I to red
		btnI.setBackground(Color.red);
		
		// Set color of every other button of row 3 to white
		btnJ.setBackground(Color.white);
		btnK.setBackground(Color.white);
		btnL.setBackground(Color.white);
		btnM.setBackground(Color.white);
		btnN.setBackground(Color.white);
		btnO.setBackground(Color.white);
		btnP.setBackground(Color.white);
	}
	
	// Highlight first button of row 4 i.e. Q, if the fourth row has been selected
	public void Q()
	{
		// Set color of Q to red
		btnQ.setBackground(Color.red);
		
		// Set color of every other button of row 4 to white
		btnR.setBackground(Color.white);
		btnS.setBackground(Color.white);
		btnT.setBackground(Color.white);
		btnU.setBackground(Color.white);
		btnV.setBackground(Color.white);
		btnW.setBackground(Color.white);
		btnX.setBackground(Color.white);
		
	}
	/********************/
	
	// To reset the row value, column value and color of button once a letter has been printed
	public void reset(JButton btn)
	{	
		// Set row to 0
		row=0;
		
		// Set column to -1
		coloumn=-1;
		
		// Reset button background color to white
		btn.setBackground(Color.white);	
	}
	
	/********** Action of buttons **********/
	// Instead of creating separate functions for buttons we have created one function that house the functionality of all buttons
	// We are going to pass the button object as parameter when the button is pressed
	public void buttonpress(JButton btn)
	{	
		// If the button is backspace
		if(btn==btnbackspace)
		{
			// Set a variable to empty
			String backspace=null;
			
			// If the text field has length greater than 0 i.e. there is something written in text field
			if(textField.getText().length()>0)
			{
				// Create a string builder object with the contents of text field
				StringBuilder strb=new StringBuilder(textField.getText());
				
				// Delete the last character typed i.e. (length of string - 1)th character
				strb.deleteCharAt(textField.getText().length()-1);
				
				// Convert the string builder object back to string and store it in the variable
				backspace=strb.toString();
				
				// Add this last character deleted string to the text filed.
				// textfield.setText will automatically clear the text and overwrite the already written text
				textField.setText(backspace);
				
				// Now we will find the suggestions for newly written text
				suggest(textField.getText());
				}
		}
		
		// If the button is email button
		else if(btn==btnEmail)
		{
			try 
			{
				// Take the text written in the text field
				String text=textField.getText();
				
				// Create a query to take the every row from the sender database i.e. the sender name and password
				String query="select * from Sender";
				
				// Create a prepared statement using the query
				pst=conn.prepareStatement(query);
				
				// Execute the query to get result set
				ResultSet rs=pst.executeQuery();
				
				// Extract the sender user name
				String username=rs.getString("sendername");
				
				// Extract the senders password
				String password=rs.getString("senderpsw");
				
				// Call the mail send function
				send=MailSend.mailsend(text,username,password,todisplay.getText());
				
				// Save the written words to database
				save(text);
				
				// Clear the text field
				textField.setText(null);
				
				// If the message is successfully sent
				if(send==1)
				{
					// Print "EMAIL SEND SUCCESSFULLY"
					errorarea.setText("EMAIL SUCCESSFULLY SEND");
				}
				
				// Else print error message and what caused the error
				else
				{
					// Possible reasons for not sending mail successfully
					String message="EMAIL NOT SUCESSFULLY SEND."
							+ "\nCheck if "
							+ "\n1. Sender ID is correct. "
							+ "\n2. Sender Password is correct."
							+ "\n3. Reciever ID is correct."
							+ "\n4. Acesss to less secure app is made ON, "
							+ "\n on senders email account."
							+ "\n5. Internet connection is there.";
					
					// Print the message to error area
					errorarea.setText(message);
				}
				
				// Close result set
				rs.close();
				
				// Close prepared statement
				pst.close();
			} 
			
			// If any exception occurs
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		}
		
		// If the button pressed is a letter from A to Z or space
		else
		{
			// Take the text written in text field and also the text of the button
			String EnterLetter=textField.getText()+btn.getText();
			
			// Set the new text to text field
			textField.setText(EnterLetter);	
			
			// If the button pressed is not backspace
			if(btn!=btnspace)
			{
				// Show suggestions for the text
				suggest(EnterLetter);	
			}
		}
	}
	
	// Since the talk has an entirely different functionality we have created a separate function.
	// For more details on the text to speech converter we have used you can check this site
	// https://freetts.sourceforge.io/
	public void talk(JButton btn)
	{
		// Give the name i.e. which voice we are going to use.
		String VOICENAME="kevin16";	
		
		// Create a voice object
		Voice voice;
		
		// Create a voice manager object
		VoiceManager vm=VoiceManager.getInstance();
		
		// Enable the voice
		voice=vm.getVoice(VOICENAME);
		
		// Allocate the voice
		voice.allocate();
		
		// If button pressed is speak
		if(btn==btnspeak)
		{
			try
			{
				// Get text from text field
				String text=textField.getText();
				
				// Speak the written text
				voice.speak(text);		
				
				// Save written text to database
				save(text);
				
				// Clear text field
				textField.setText(null);
			}
			// Exceptions
			catch(Exception e)
			{
				
			}
			
		}
		
		// If alert button is pressed
		// This button is used to alert the people nearby if the patient is dealing with some emergency
		else if(btn==btnAlert)
		{
			try
			{
				// Just say "EMERGENCY" 6 times
				String text="EMERGENCY. EMERGENCY. EMERGENCY. EMERGENCY. EMERGENCY. EMERGENCY.";
				
				// Speak the text
				voice.speak(text);		
			}
			// Exception
			catch(Exception e)
			{
				
			}
			
		}
	}
	
	// This function is used to show the senders mail address on the screen
	// The sender mail address has to be manually set by someone who can use mouse and keyboard
	// This cannot be done by the patient.
	// The function for setting the sender mail ID and password is way below i.e near button "Done"
	// i.e. after some one has set the mail ID of sender and receiver, we need to press button 'Done'
	// This cannot be done by step scanning, but has to be manually done
	public void setsender()
	{
		try 
		{
			// Create a query to select the sender name from the database
			String query="select sendername from Sender";
			
			// Create a prepared statement using the query
			pst=conn.prepareStatement(query);
			
			// Execute the query
			ResultSet rs=pst.executeQuery();
			
			// Iterate through the result set
			while(rs.next())
			{
				// Display the senders mail ID on screen
				fromdisplay.setText(rs.getString("sendername"));
				
				// break from loop
				break;
			}
			
			// Close prepared statement 
			pst.close();
			
			// Close result set
			rs.close();
		}
		// Exception
		catch (Exception e)
		{	
			e.printStackTrace();
		}
	}
	
	// This function is used to show the receivers mail address on the screen
	// This also has to be manually done by some one who can use keyboard and mouse
	public void setreciever()
	{
		try 
		{
			// Create a query to extract name from receiver database
			String query="select name from Reciever";
			
			// Create a prepared statement
			pst=conn.prepareStatement(query);
			
			// Create a result set and execute the query
			ResultSet rs=pst.executeQuery();
			
			// Iterate through the result set
			while(rs.next())
			{
				// Display the receivers mail ID on screen
				todisplay.setText(rs.getString("name"));
				
				// break from loop
				break;
			}
			
			// Close prepared statement 
			pst.close();
			
			// Close result set
			rs.close();
		}
		
		// Exceptions
		catch (Exception e)
		{	
			e.printStackTrace();
		}
	}
	
	// Selecting words from the list using step scanning
	// Parameter is the word selected from the list
	public void listselection(String word)
	{
		// If we have selected the full word
		// If we want to select 'HELLO' from the list and print it
		// i.e if the last word typed is a space
		if(textField.getText().endsWith(" "))
		{
			// Just append the word to the end of the text field (already written text) and add a space
			textField.setText(textField.getText()+listfield.getText()+" ");
		}
		
		// Else if we have typed a part of a word and want to paste the full word
		// If we have typed 'HEL' and we saw suggestion for 'HELLO' and we want to go to list and select 'HELLO' 
		// 'HEL' will be deleted and 'HELLO' will be typed
		else
		{	
			// Split the text entered in the text field by space and store each word in an array
			String parts[]=textField.getText().split(" ");
			
			// Find the length of the array
			int last=parts.length;
			
			// Select the last element of the array
			String lastword=parts[last-1];		
			
			// Find the length of the last element in the array
			int length=lastword.length();
			
			// Select the text from the text field
			String backspace=textField.getText();
			
			// Continue till we have deleted each character from the last word typed
			while(length>0)
			{
				// Create a sting builder object with the text from the text field
				StringBuilder strb=new StringBuilder(backspace);
				
				// Delete the last character from the text field
				strb.deleteCharAt(backspace.length()-1);
				
				// Convert it back to string
				backspace=strb.toString();
				
				// Reduce length to delete the next character
				length--;
			}
			
			// Set the text filed with the new text and space
			textField.setText(backspace+word+" ");
		}
	}
	/********************/
	
	// Main function where the program execution starts
	public Main() 
	{
		// Establish connection to database
		conn=SQLiteConnection.dbConnector();
		
		// Initialize contents of the frame
		initialize();
		
		// Show all words in the database for suggestion
		extractall();
		
		// Set the senders mail address by extracting the information from the database
		setsender();
		
		// Set the receivers mail address by extracting the information from the database
		setreciever();
	}

	// Initialize the contents of the frame.
	private void initialize() 
	{
		// Initialize the frame object
		frame = new JFrame();
		
		// Set Foreground color to green
		frame.setForeground(Color.GREEN);
		
		// Set background color to black
		frame.setBackground(Color.BLACK);
		
		// Set content pane color to black
		frame.getContentPane().setForeground(Color.BLACK);
		
		// Set content pane color to white
		frame.getContentPane().setBackground(Color.WHITE);
		
		// Create a mouse listener that will listen for mouse actions
		frame.getContentPane().addMouseListener(new MouseAdapter() 
		{
			// If mouse button is clicked
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				/********** Highlighting of rows and columns **********/
				// If the mouse button clicked was 3rd button i.e. right mouse button
				while(e.getButton()==3)
				{
					/********** Row highlighting **********/
					// If row highlighted is last row and no column is selected
					if(row==4&&coloumn==-1)
					{
						// Reset row counter to start highlighting from first row itself						
						row=0;
					}
					
					// If row value is 0 and no column is selected
					if(row==0&&coloumn==-1)
					{	
						// Highlight row 1
						row1();	
						
						// Increment row value to highlight next row for next mouse click
						row++;
						
						// Break out of while loop
						break;	
					}
					
					// If row value is 1 and no column is selected
					else if(row==1&&coloumn==-1)
					{	
						// Highlight row 2
						row2(); 
						
						// Increment row value to highlight next row for next mouse click
						row++; 
						
						// Break out of while loop
						break;	
					}
					
					// If row value is 2 and no column is selected
					else if(row==2&&coloumn==-1)
					{	
						// Highlight row 3
						row3(); 
						
						// Increment row value to highlight next row for next mouse click
						row++; 
						
						// Break out of while loop
						break;	
					}
					
					// If row value is 3 and no column is selected
					else if(row==3&&coloumn==-1)
					{	
						// Highlight row 3
						row4(); 
						
						// Increment row value to highlight next row for next mouse click
						row++; 
						
						// Break out of while loop
						break;	
					}
					/********************/
					
					
					/********** Highlighting columns of row 1 **********/
					// If we have selected first row and if we have highlighted till the last column
					if(row==1&&coloumn==8&&listvalue==false)
					{
						// Reset column counter to start highlighting from the first column
						coloumn=0;
					}
					
					// If we have selected first row and if we need to highlight first column
					if(row==1&&coloumn==0&&listvalue==false)	//Alert
					{	
						// Highlight alert button
						alert();	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
										
					// If we have selected first row and if we need to highlight second column
					else if(row==1&&coloumn==1&&listvalue==false)//Speak
					{	
						// Reset color of previous column
						btnAlert.setBackground(Color.white);
						
						// Set color of current column to red
						btnspeak.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected first row and if we need to highlight third column
					else if(row==1&&coloumn==2&&listvalue==false)//Space
					{	
						// Reset color of previous column
						btnspeak.setBackground(Color.white);
						
						// Set color of current column to red
						btnspace.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected first row and if we need to highlight fourth column
					else if(row==1&&coloumn==3&&listvalue==false)//Backspace
					{	
						// Reset color of previous column
						btnspace.setBackground(Color.white);
						
						// Set color of current column to red
						btnbackspace.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected first row and if we need to highlight fifth column
					else if(row==1&&coloumn==4&&listvalue==false)//List
					{	
						// Reset color of previous column
						btnbackspace.setBackground(Color.white);
						
						// Set color of current column to red
						btnList.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected first row and if we need to highlight sixth column
					else if(row==1&&coloumn==5&&listvalue==false)//Email
					{	
						// Reset color of previous column
						btnList.setBackground(Color.white);
						
						// Set color of current column to red
						btnEmail.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected first row and if we need to highlight seventh column
					else if(row==1&&coloumn==6&&listvalue==false)//Y
					{	
						// Reset color of previous column
						btnEmail.setBackground(Color.white);
						
						// Set color of current column to red
						btnY.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected first row and if we need to highlight eighth column
					else if(row==1&&coloumn==7&&listvalue==false)//Z
					{	
						// Reset color of previous column
						btnY.setBackground(Color.white);
						
						// Set color of current column to red
						btnZ.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					/********************/
					
					
					/********** Highlighting columns of row 2 **********/
					// If we have selected second row and if we have highlighted till the last column
					if(row==2&&coloumn==8)
					{
						// Reset column counter to start highlighting from the first column
						coloumn=0;
					}
					
					// If we have selected first row and if we need to highlight first column
					if(row==2&&coloumn==0)	//A
					{	
						// Highlight button A
						A();	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected second row and if we need to highlight second column
					else if(row==2&&coloumn==1)//B
					{	
						// Reset color of previous column
						btnA.setBackground(Color.white);
						
						// Set color of current column to red
						btnB.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected second row and if we need to highlight third column
					else if(row==2&&coloumn==2)//C
					{	
						// Reset color of previous column
						btnB.setBackground(Color.white);
						
						// Set color of current column to red
						btnC.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected second row and if we need to highlight fourth column
					else if(row==2&&coloumn==3)//D
					{	
						// Reset color of previous column
						btnC.setBackground(Color.white);
						
						// Set color of current column to red
						btnD.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected second row and if we need to highlight fifth column
					else if(row==2&&coloumn==4)//E
					{	
						// Reset color of previous column
						btnD.setBackground(Color.white);
						
						// Set color of current column to red
						btnE.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected second row and if we need to highlight sixth column
					else if(row==2&&coloumn==5)//F
					{	
						// Reset color of previous column
						btnE.setBackground(Color.white);
						
						// Set color of current column to red
						btnF.setBackground(Color.red);
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected second row and if we need to highlight seventh column
					else if(row==2&&coloumn==6)//G
					{	
						// Reset color of previous column
						btnF.setBackground(Color.white);
						
						// Set color of current column to red
						btnG.setBackground(Color.red);
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected second row and if we need to highlight eighth column
					else if(row==2&&coloumn==7)//H
					{	
						// Reset color of previous column
						btnG.setBackground(Color.white);
						
						// Set color of current column to red
						btnH.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					/********************/					
					
					/********** Highlighting columns of row 3 **********/
					// If we have selected third row and if we have highlighted till the last column
					if(row==3&&coloumn==8)
					{
						// Reset column counter to start highlighting from the first column
						coloumn=0;
					}
					
					// If we have selected third row and if we need to highlight first column
					if(row==3&&coloumn==0)	//I
					{	
						// Highlight button I
						I();	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected third row and if we need to highlight second column
					else if(row==3&&coloumn==1)//J
					{	
						// Reset color of previous column
						btnI.setBackground(Color.white);
						
						// Set color of current column to red
						btnJ.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected third row and if we need to highlight third column
					else if(row==3&&coloumn==2)//K
					{	
						// Reset color of previous column
						btnJ.setBackground(Color.white);
						
						// Set color of current column to red
						btnK.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected third row and if we need to highlight fourth column
					else if(row==3&&coloumn==3)//L
					{	
						// Reset color of previous column
						btnK.setBackground(Color.white);
						
						// Set color of current column to red
						btnL.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected third row and if we need to highlight fifth column
					else if(row==3&&coloumn==4)//M
					{	
						// Reset color of previous column
						btnL.setBackground(Color.white);
						
						// Set color of current column to red
						btnM.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected third row and if we need to highlight sixth column
					else if(row==3&&coloumn==5)//N
					{	
						// Reset color of previous column
						btnM.setBackground(Color.white);
						
						// Set color of current column to red
						btnN.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected third row and if we need to highlight seventh column
					else if(row==3&&coloumn==6)//O
					{	
						// Reset color of previous column
						btnN.setBackground(Color.white);
						
						// Set color of current column to red
						btnO.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected third row and if we need to highlight eighth column
					else if(row==3&&coloumn==7)//P
					{	
						// Reset color of previous column
						btnO.setBackground(Color.white);
						
						// Set color of current column to red
						btnP.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					/********************/						
					
					/********** Highlighting columns of row 4 **********/
					// If we have selected fourth row and if we have highlighted till the last column
					if(row==4&&coloumn==8)
					{
						// Reset column counter to start highlighting from the first column
						coloumn=0;
					}
					
					// If we have selected fourth row and if we need to highlight first column
					if(row==4&&coloumn==0)	//Q
					{	
						// Highlight button Q
						Q();	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected fourth row and if we need to highlight second column
					else if(row==4&&coloumn==1)//R
					{	
						// Reset color of previous column
						btnQ.setBackground(Color.white);
						
						// Set color of current column to red
						btnR.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected fourth row and if we need to highlight third column
					else if(row==4&&coloumn==2)//S
					{	
						// Reset color of previous column
						btnR.setBackground(Color.white);
						
						// Set color of current column to red
						btnS.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected fourth row and if we need to highlight fourth column
					else if(row==4&&coloumn==3)//T
					{	
						// Reset color of previous column
						btnS.setBackground(Color.white);
						
						// Set color of current column to red
						btnT.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected fourth row and if we need to highlight fifth column
					else if(row==4&&coloumn==4)//U
					{	
						// Reset color of previous column
						btnT.setBackground(Color.white);
						
						// Set color of current column to red
						btnU.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected fourth row and if we need to highlight sixth column
					else if(row==4&&coloumn==5)//V
					{	
						// Reset color of previous column
						btnU.setBackground(Color.white);
						
						// Set color of current column to red
						btnV.setBackground(Color.red);
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected fourth row and if we need to highlight seventh column
					else if(row==4&&coloumn==6)//W
					{	
						// Reset color of previous column
						btnV.setBackground(Color.white);
						
						// Set color of current column to red
						btnW.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If we have selected fourth row and if we need to highlight eighth column
					else if(row==4&&coloumn==7)//X
					{	
						// Reset color of previous column
						btnW.setBackground(Color.white);
						
						// Set color of current column to red
						btnX.setBackground(Color.red);	
						
						// Increment column value to highlight next column in next mouse click
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					/********************/						
					
					/********** Highlighting columns list **********/
					// If we have selected row, column and decided to select elements from list
					if(row==1&&coloumn==5&&listvalue==true)
					{
						// Get current model of list
						ListModel LM=list.getModel();
						
						// Get number of elements in list
						int n=LM.getSize();
						
						// If we have highlighted till last element
						if(index==n-1)
						{
							// Reset index to highlight from first element
							index=-1;
						}
						
						// Increment index to highlight next element
						index++;
						
						// Highlight current list element
						// Instead of highlighting list element, we are going to show the element on a text field.
						// Located on top of the list
						listfield.setText((String)LM.getElementAt(index));
						
						// Break out of while loop
						break;
					}
					break;
					/********************/
				}
				/********************/
				
				/********** Selection of rows and columns **********/
				// If we have clicked first mouse button i.e. left mouse button
				while(e.getButton()==1)
				{
					/********** Selection of row **********/
					// If row 1 is highlighted and we need to select it
					if(row==1&&coloumn==-1)//ROW1
					{	
						// Increment column to 0 to select it
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If row 2 is highlighted and we need to select it
					else if(row==2&&coloumn==-1)//ROW2
					{	
						// Increment column to 0 to select it
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If row 3 is highlighted and we need to select it
					else if(row==3&&coloumn==-1)//ROW3
					{	
						// Increment column to 0 to select it
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					
					// If row 4 is highlighted and we need to select it
					else if(row==4&&coloumn==-1)//ROW4
					{	
						// Increment column to 0 to select it
						coloumn++;	
						
						// Break out of while loop
						break;	
					}
					/********************/
					
					/********** Selection of columns of row 1 **********/
					// For row 1 and column 1 i.e. alert button
					if(row==1&&coloumn==1)//Alert action
					{	
						// Call function that enables functionality of alert button
						talk(btnAlert);	
						
						// Reset the alert button and row and column values.
						reset(btnAlert);
						
						// Break out of while loop
						break;	
					}
					
					// For row 1 and column 2 i.e. speak button
					if(row==1&&coloumn==2)//Speak
					{	
						// Call function that enables functionality of speak button
						talk(btnspeak);	
						
						// Reset the speak button and row and column values
						reset(btnspeak);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 1 and column 3 i.e. space
					if(row==1&&coloumn==3)//space
					{	
						// Call function that enables functionality of space button
						buttonpress(btnspace);	
						
						// Reset the space button and row and column values
						reset(btnspace);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 1 and column 4 i.e. backspace
					if(row==1&&coloumn==4)//backspace
					{	
						// Call function that enables functionality of back space button
						buttonpress(btnbackspace);	
						
						// Reset the back space button and row and column values
						reset(btnbackspace);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 1 and column 5 i.e. list value selection
					if(row==1&&coloumn==5&&listvalue==false)//word is to be selected from list
					{	
						// Set listvalue to true as we want to select value from the list
						listvalue=true;	
						
						// Break out of while loop
						break;	
					}
					
					// For row 1 and column 5 and we need to select value from list
					if(row==1&&coloumn==5&&listvalue==true)//list value selection
					{	
						// Call function that will print the selected value from the list
						listselection(listfield.getText());	
						
						// Reset the list button and row and column values
						reset(btnList);	
						
						// Set listvalue to false after selecting value from the list
						listvalue=false;	
						
						// Set the area where we show the selected value from list to null
						listfield.setText(null);	
						
						// Set index to -1
						index=-1;	
						
						// Break out of while loop
						break;	
					}
					
					// For row 1 and column 6 i.e. email button
					if(row==1&&coloumn==6)//email
					{	
						// Call function that enables functionality of email button
						buttonpress(btnEmail);	
						
						// Reset the email button and row and column values
						reset(btnEmail);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 1 and column 7 i.e. Y button
					if(row==1&&coloumn==7)//y
					{	
						// Call function that enables functionality of Y button
						buttonpress(btnY);	
						
						// Reset the Y button and row and column values
						reset(btnY);
						
						// Break out of while loop
						break;	
					}
					
					// For row 1 and column 8 i.e. Z button
					if(row==1&&coloumn==8)//z
					{	
						// Call function that enables functionality of Z button
						buttonpress(btnZ);	
						
						// Reset the Z button and row and column values
						reset(btnZ);	
						
						// Break out of while loop
						break;	
					}
					/********************/					
					
					/********** Selection of columns of row 2 **********/
					// For row 2 and column 1 i.e. A button
					if(row==2&&coloumn==1)//A
					{	
						// Call function that enables functionality of A button
						buttonpress(btnA);	
						
						// Reset the A button and row and column values
						reset(btnA);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 2 and column 2 i.e. B button
					if(row==2&&coloumn==2)//B
					{	
						// Call function that enables functionality of B button
						buttonpress(btnB);	
						
						// Reset the B button and row and column values
						reset(btnB);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 2 and column 3 i.e. C button
					if(row==2&&coloumn==3)//C
					{	
						// Call function that enables functionality of C button
						buttonpress(btnC);	
						
						// Reset the C button and row and column values
						reset(btnC);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 2 and column 4 i.e. D button
					if(row==2&&coloumn==4)//D
					{	
						// Call function that enables functionality of D button
						buttonpress(btnD);	
						
						// Reset the D button and row and column values
						reset(btnD);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 2 and column 5 i.e. E button
					if(row==2&&coloumn==5)//E
					{	
						// Call function that enables functionality of E button
						buttonpress(btnE);	
						
						// Reset the E button and row and column values
						reset(btnE);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 2 and column 6 i.e. F button
					if(row==2&&coloumn==6)//F
					{	
						// Call function that enables functionality of F button
						buttonpress(btnF);	
						
						// Reset the F button and row and column values
						reset(btnF);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 2 and column 7 i.e. G button
					if(row==2&&coloumn==7)//G
					{	
						// Call function that enables functionality of G button
						buttonpress(btnG);	
						
						// Reset the G button and row and column values
						reset(btnG);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 2 and column 8 i.e. H button
					if(row==2&&coloumn==8)//H
					{	
						// Call function that enables functionality of H button
						buttonpress(btnH);	
						
						// Reset the H button and row and column values
						reset(btnH);	
						
						// Break out of while loop
						break;	
					}
					/********************/								
					
					/********** Selection of columns of row 3 **********/
					// For row 3 and column 1 i.e. I button
					if(row==3&&coloumn==1)//I
					{	
						// Call function that enables functionality of I button
						buttonpress(btnI);	
						
						// Reset the I button and row and column values
						reset(btnI);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 3 and column 2 i.e. J button
					if(row==3&&coloumn==2)//J
					{	
						// Call function that enables functionality of J button
						buttonpress(btnJ);
						
						// Reset the J button and row and column values
						reset(btnJ);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 3 and column 3 i.e. K button
					if(row==3&&coloumn==3)//K
					{	
						// Call function that enables functionality of K button
						buttonpress(btnK);
						
						// Reset the K button and row and column values
						reset(btnK);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 3 and column 4 i.e. L button
					if(row==3&&coloumn==4)//L
					{	
						// Call function that enables functionality of L button
						buttonpress(btnL);	
						
						// Reset the L button and row and column values
						reset(btnL);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 3 and column 5 i.e. M button
					if(row==3&&coloumn==5)//M
					{	
						// Call function that enables functionality of M button
						buttonpress(btnM);	
						
						// Reset the M button and row and column values
						reset(btnM);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 3 and column 6 i.e. N button
					if(row==3&&coloumn==6)//N
					{	
						// Call function that enables functionality of N button
						buttonpress(btnN);	
						
						// Reset the N button and row and column values
						reset(btnN);
						
						// Break out of while loop
						break;	
					}
					
					// For row 3 and column 7 i.e. O button
					if(row==3&&coloumn==7)//O
					{	
						// Call function that enables functionality of O button
						buttonpress(btnO);
						
						// Reset the O button and row and column values
						reset(btnO);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 3 and column 8 i.e. P button
					if(row==3&&coloumn==8)//P
					{	
						// Call function that enables functionality of P button
						buttonpress(btnP);	
						
						// Reset the P button and row and column values
						reset(btnP);	
						
						// Break out of while loop
						break;	
					}
					/********************/						
					
					/********** Selection of columns of row 4 **********/
					// For row 4 and column 1 i.e. Q button
					if(row==4&&coloumn==1)//Q
					{	
						// Call function that enables functionality of Q button
						buttonpress(btnQ);	
						
						// Reset the Q button and row and column values
						reset(btnQ);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 4 and column 2 i.e. R button
					if(row==4&&coloumn==2)//R
					{	
						// Call function that enables functionality of R button
						buttonpress(btnR);	
						
						// Reset the R button and row and column values
						reset(btnR);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 4 and column 3 i.e. S button
					if(row==4&&coloumn==3)//S
					{	
						// Call function that enables functionality of S button
						buttonpress(btnS);	
						
						// Reset the S button and row and column values
						reset(btnS);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 4 and column 4 i.e. T button
					if(row==4&&coloumn==4)//T
					{	
						// Call function that enables functionality of T button
						buttonpress(btnT);	
						
						// Reset the T button and row and column values
						reset(btnT);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 4 and column 5 i.e. U button
					if(row==4&&coloumn==5)//U
					{	
						// Call function that enables functionality of U button
						buttonpress(btnU);	
						
						// Reset the U button and row and column values
						reset(btnU);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 4 and column 6 i.e. V button
					if(row==4&&coloumn==6)//V
					{	
						// Call function that enables functionality of V button
						buttonpress(btnV);	
						
						// Reset the V button and row and column values
						reset(btnV);	
						
						// Break out of while loop
						break;	
					}
					
					// For row 4 and column 7 i.e. W button
					if(row==4&&coloumn==7)//W
					{	
						// Call function that enables functionality of W button
						buttonpress(btnW);	
						
						// Reset the W button and row and column values
						reset(btnW);
						
						// Break out of while loop
						break;	
					}
					
					// For row 4 and column 8 i.e. X button
					if(row==4&&coloumn==8)//X
					{	
						// Call function that enables functionality of X button
						buttonpress(btnX);	
						
						// Reset the X button and row and column values
						reset(btnX);	
						
						// Break out of while loop
						break;	
					}
					/********************/	
					
					// Break out of while loop
					break;
				}
				/********************/
			}
		});		
		frame.setBounds(100, 100, 1000, 600);	// Set the x, y coordinates and the length and width of appliction
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// Text filed to show the text
		textField = new JTextField();
		textField.setForeground(Color.BLACK);
		textField.setBackground(Color.WHITE);
		textField.setFont(new Font("Arial", Font.BOLD, 30));
		textField.setBounds(10, 500, 960, 50);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		textField.getCaret().setVisible(true);// Used to show the cursor in text field.
		
		/* For each button I have provided the facility to perform action by clicking on the button with the mouse.
		 * So the application will work using step scanning and also by clicking on each button with the mouse.
		 */
		
		// Speak button
		btnspeak = new JButton("SPEAK");
		btnspeak.setForeground(Color.BLACK);
		btnspeak.setBackground(Color.WHITE);
		btnspeak.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				talk(btnspeak);
			}
		});
		btnspeak.setFont(new Font("Arial", Font.BOLD, 17));
		btnspeak.setBounds(340, 10, 90, 30);
		frame.getContentPane().add(btnspeak);
		
		// Button A
		btnA = new JButton("A");
		btnA.setForeground(Color.BLACK);
		btnA.setBackground(Color.WHITE);
		btnA.setFont(new Font("Arial", Font.BOLD, 30));
		btnA.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnA);
			}
		});
		btnA.setBounds(250, 40, 90, 30);
		frame.getContentPane().add(btnA);
		
		// Button B
		btnB = new JButton("B");
		btnB.setForeground(Color.BLACK);
		btnB.setBackground(Color.WHITE);
		btnB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnB);
			}
		});
		btnB.setFont(new Font("Arial", Font.BOLD, 30));
		btnB.setBounds(340, 40, 90, 30);
		frame.getContentPane().add(btnB);
		
		// Button C
		btnC = new JButton("C");
		btnC.setForeground(Color.BLACK);
		btnC.setBackground(Color.WHITE);
		btnC.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnC);
			}
		});
		btnC.setFont(new Font("Arial", Font.BOLD, 30));
		btnC.setBounds(430, 40, 90, 30);
		frame.getContentPane().add(btnC);
		
		// Button D
		btnD = new JButton("D");
		btnD.setForeground(Color.BLACK);
		btnD.setBackground(Color.WHITE);
		btnD.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnD);
			}
		});
		btnD.setFont(new Font("Arial", Font.BOLD, 30));
		btnD.setBounds(520, 40, 90, 30);
		frame.getContentPane().add(btnD);
		
		// Button E
		btnE = new JButton("E");
		btnE.setForeground(Color.BLACK);
		btnE.setBackground(Color.WHITE);
		btnE.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnE);
			}
		});
		btnE.setFont(new Font("Arial", Font.BOLD, 30));
		btnE.setBounds(610, 40, 90, 30);
		frame.getContentPane().add(btnE);
		
		// Button F
		btnF = new JButton("F");
		btnF.setForeground(Color.BLACK);
		btnF.setBackground(Color.WHITE);
		btnF.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnF);
			}
		});
		btnF.setFont(new Font("Arial", Font.BOLD, 30));
		btnF.setBounds(700, 40, 90, 30);
		frame.getContentPane().add(btnF);
		
		// Button G
		btnG = new JButton("G");
		btnG.setForeground(Color.BLACK);
		btnG.setBackground(Color.WHITE);
		btnG.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnG);
			}
		});
		btnG.setFont(new Font("Arial", Font.BOLD, 30));
		btnG.setBounds(790, 40, 90, 30);
		frame.getContentPane().add(btnG);
		
		// Button H
		btnH = new JButton("H");
		btnH.setForeground(Color.BLACK);
		btnH.setBackground(Color.WHITE);
		btnH.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnH);
			}
		});
		btnH.setFont(new Font("Arial", Font.BOLD, 30));
		btnH.setBounds(880, 40, 90, 30);
		frame.getContentPane().add(btnH);
		
		// Button I
		btnI = new JButton("I");
		btnI.setForeground(Color.BLACK);
		btnI.setBackground(Color.WHITE);
		btnI.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnI);
			}
		});
		btnI.setFont(new Font("Arial", Font.BOLD, 30));
		btnI.setBounds(250, 70, 90, 30);
		frame.getContentPane().add(btnI);
		
		// Button J
		btnJ = new JButton("J");
		btnJ.setForeground(Color.BLACK);
		btnJ.setBackground(Color.WHITE);
		btnJ.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnJ);
			}
		});
		btnJ.setFont(new Font("Arial", Font.BOLD, 30));
		btnJ.setBounds(340, 70, 90, 30);
		frame.getContentPane().add(btnJ);
		
		// Button K
		btnK = new JButton("K");
		btnK.setForeground(Color.BLACK);
		btnK.setBackground(Color.WHITE);
		btnK.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnK);
			}
		});
		btnK.setFont(new Font("Arial", Font.BOLD, 30));
		btnK.setBounds(430, 70, 90, 30);
		frame.getContentPane().add(btnK);
		
		// Button L
		btnL = new JButton("L");
		btnL.setForeground(Color.BLACK);
		btnL.setBackground(Color.WHITE);
		btnL.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnL);
			}
		});
		btnL.setFont(new Font("Arial", Font.BOLD, 30));
		btnL.setBounds(520, 70, 90, 30);
		frame.getContentPane().add(btnL);
		
		// Button M
		btnM = new JButton("M");
		btnM.setForeground(Color.BLACK);
		btnM.setBackground(Color.WHITE);
		btnM.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnM);
			}
		});
		btnM.setFont(new Font("Arial", Font.BOLD, 30));
		btnM.setBounds(610, 70, 90, 30);
		frame.getContentPane().add(btnM);
		
		// Button N
		btnN = new JButton("N");
		btnN.setForeground(Color.BLACK);
		btnN.setBackground(Color.WHITE);
		btnN.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnN);
			}
		});
		btnN.setFont(new Font("Arial", Font.BOLD, 30));
		btnN.setBounds(700, 70, 90, 30);
		frame.getContentPane().add(btnN);
		
		// Button O
		btnO = new JButton("O");
		btnO.setForeground(Color.BLACK);
		btnO.setBackground(Color.WHITE);
		btnO.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnO);
			}
		});
		btnO.setFont(new Font("Arial", Font.BOLD, 30));
		btnO.setBounds(790, 70, 90, 30);
		frame.getContentPane().add(btnO);
		
		// Button P
		btnP = new JButton("P");
		btnP.setForeground(Color.BLACK);
		btnP.setBackground(Color.WHITE);
		btnP.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnP);
			}
		});
		btnP.setFont(new Font("Arial", Font.BOLD, 30));
		btnP.setBounds(880, 70, 90, 30);
		frame.getContentPane().add(btnP);
		
		// Button Q
		btnQ = new JButton("Q");
		btnQ.setForeground(Color.BLACK);
		btnQ.setBackground(Color.WHITE);
		btnQ.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnQ);
			}
		});
		btnQ.setFont(new Font("Arial", Font.BOLD, 30));
		btnQ.setBounds(250, 100, 90, 30);
		frame.getContentPane().add(btnQ);
		
		// Button R
		btnR = new JButton("R");
		btnR.setForeground(Color.BLACK);
		btnR.setBackground(Color.WHITE);
		btnR.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnR);
			}
		});
		btnR.setFont(new Font("Arial", Font.BOLD, 30));
		btnR.setBounds(340, 100, 90, 30);
		frame.getContentPane().add(btnR);
		
		// Button S
		btnS = new JButton("S");
		btnS.setForeground(Color.BLACK);
		btnS.setBackground(Color.WHITE);
		btnS.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnS);
			}
		});
		btnS.setFont(new Font("Arial", Font.BOLD, 30));
		btnS.setBounds(430, 100, 90, 30);
		frame.getContentPane().add(btnS);
		
		// Button T
		btnT = new JButton("T");
		btnT.setForeground(Color.BLACK);
		btnT.setBackground(Color.WHITE);
		btnT.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnT);
			}
		});
		btnT.setFont(new Font("Arial", Font.BOLD, 30));
		btnT.setBounds(520, 100, 90, 30);
		frame.getContentPane().add(btnT);
		
		// Button U
		btnU = new JButton("U");
		btnU.setForeground(Color.BLACK);
		btnU.setBackground(Color.WHITE);
		btnU.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnU);
			}
		});
		btnU.setFont(new Font("Arial", Font.BOLD, 30));
		btnU.setBounds(610, 100, 90, 30);
		frame.getContentPane().add(btnU);
		
		// Button V
		btnV = new JButton("V");
		btnV.setForeground(Color.BLACK);
		btnV.setBackground(Color.WHITE);
		btnV.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{
				buttonpress(btnV);
			}
		});
		btnV.setFont(new Font("Arial", Font.BOLD, 30));
		btnV.setBounds(700, 100, 90, 30);
		frame.getContentPane().add(btnV);
		
		// Button W
		btnW = new JButton("W");
		btnW.setForeground(Color.BLACK);
		btnW.setBackground(Color.WHITE);
		btnW.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnW);
			}
		});
		btnW.setFont(new Font("Arial", Font.BOLD, 27));
		btnW.setBounds(790, 100, 90, 30);
		frame.getContentPane().add(btnW);
		
		// Button X
		btnX = new JButton("X");
		btnX.setForeground(Color.BLACK);
		btnX.setBackground(Color.WHITE);
		btnX.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnX);
			}
		});
		btnX.setFont(new Font("Arial", Font.BOLD, 30));
		btnX.setBounds(880, 100, 90, 30);
		frame.getContentPane().add(btnX);
		
		// Button Y
		btnY = new JButton("Y");
		btnY.setForeground(Color.BLACK);
		btnY.setBackground(Color.WHITE);
		btnY.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnY);
			}
		});
		btnY.setFont(new Font("Arial", Font.BOLD, 30));
		btnY.setBounds(790, 10, 90, 30);
		frame.getContentPane().add(btnY);
		
		// Button Z
		btnZ = new JButton("Z");
		btnZ.setForeground(Color.BLACK);
		btnZ.setBackground(Color.WHITE);
		btnZ.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnZ);
			}
		});
		btnZ.setFont(new Font("Arial", Font.BOLD, 30));
		btnZ.setBounds(880, 10, 90, 30);
		frame.getContentPane().add(btnZ);
		
		// Button space
		btnspace = new JButton(" ");
		btnspace.setForeground(Color.BLACK);
		btnspace.setBackground(Color.WHITE);
		btnspace.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnspace);
			}
		});
		btnspace.setBounds(430, 10, 90, 30);
		frame.getContentPane().add(btnspace);
		
		// Button back space
		btnbackspace = new JButton("<-");
		btnbackspace.setForeground(Color.BLACK);
		btnbackspace.setBackground(Color.WHITE);
		btnbackspace.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				buttonpress(btnbackspace);
			}
		});
		btnbackspace.setFont(new Font("Arial", Font.BOLD, 28));
		btnbackspace.setBounds(520, 10, 90, 30);
		frame.getContentPane().add(btnbackspace);
				
		// Button Email
		btnEmail = new JButton("EMAIL");
		btnEmail.setForeground(Color.BLACK);
		btnEmail.setBackground(Color.WHITE);
		btnEmail.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonpress(btnEmail);
			}
		});
		btnEmail.setFont(new Font("Arial", Font.BOLD, 18));
		btnEmail.setBounds(700, 10, 90, 30);
		frame.getContentPane().add(btnEmail);
		
		// Alert button
		btnAlert = new JButton("ALERT");
		btnAlert.setForeground(Color.BLACK);
		btnAlert.setBackground(Color.WHITE);
		btnAlert.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{
				talk(btnAlert);
			}
		});
		btnAlert.setFont(new Font("Arial", Font.BOLD, 17));
		btnAlert.setBounds(250, 10, 90, 30);
		frame.getContentPane().add(btnAlert);
		
		// Create a scroll pane to add list of words
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 61, 230, 428);
		frame.getContentPane().add(scrollPane);
		
		// To select a word from list, by clicking on the word using mouse
		list = new JList();
		list.setBackground(Color.WHITE);
		list.setForeground(Color.BLACK);
		list.setVisibleRowCount(10);
		scrollPane.setViewportView(list);
		list.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				listselection((String)(list.getSelectedValue()));
			}
		});
		list.setFont(new Font("Arial", Font.BOLD, 17));
		
		// To show the highlighted word from the list
		listfield = new JTextField();
		listfield.setForeground(Color.BLACK);
		listfield.setBackground(Color.WHITE);
		listfield.setFont(new Font("Arial", Font.BOLD, 17));
		listfield.setBounds(10, 10, 230, 40);
		frame.getContentPane().add(listfield);
		listfield.setColumns(10);
		
		// This button is used in step scanning
		// This button is used when we want to select a word from the list using step scanning
		// If we manually click on this button with mouse it provides no action.
		btnList = new JButton("LIST");
		btnList.setForeground(Color.BLACK);
		btnList.setBackground(Color.WHITE);
		btnList.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
			}
		});
		btnList.setFont(new Font("Arial", Font.BOLD, 18));
		btnList.setBounds(610, 10, 90, 30);
		frame.getContentPane().add(btnList);
		
		// To write the sender mail address
		from = new JTextField();
		from.setForeground(Color.BLACK);
		from.setBackground(Color.WHITE);
		from.setFont(new Font("Arial", Font.BOLD, 14));
		from.setBounds(700, 140, 180, 20);
		frame.getContentPane().add(from);
		from.setColumns(10);
		
		// To display the senders mail address
		fromdisplay = new JTextField();
		fromdisplay.setForeground(Color.BLACK);
		fromdisplay.setBackground(Color.WHITE);
		fromdisplay.setFont(new Font("Arial", Font.BOLD, 14));
		fromdisplay.setBounds(700, 230, 270, 20);
		frame.getContentPane().add(fromdisplay);
		fromdisplay.setColumns(10);
		
		// To type password of sender
		password = new JPasswordField();
		password.setForeground(Color.BLACK);
		password.setBackground(Color.WHITE);
		password.setBounds(700, 170, 180, 20);
		frame.getContentPane().add(password);
		
		// To type address of receiver
		to = new JTextField();
		to.setForeground(Color.BLACK);
		to.setBackground(Color.WHITE);
		to.setFont(new Font("Arial", Font.BOLD, 14));
		to.setBounds(700, 200, 180, 20);
		frame.getContentPane().add(to);
		to.setColumns(10);
		
		// To display address of receiver
		todisplay = new JTextField();
		todisplay.setForeground(Color.BLACK);
		todisplay.setBackground(Color.WHITE);
		todisplay.setFont(new Font("Arial", Font.BOLD, 14));
		todisplay.setBounds(700, 260, 270, 20);
		frame.getContentPane().add(todisplay);
		todisplay.setColumns(10);
		
		// Labels
		// From label
		from1 = new JLabel("From");
		from1.setForeground(Color.BLACK);
		from1.setHorizontalAlignment(SwingConstants.CENTER);
		from1.setFont(new Font("Arial", Font.BOLD, 12));
		from1.setBounds(610, 140, 90, 20);
		frame.getContentPane().add(from1);
		
		// Password label
		password1 = new JLabel("Password");
		password1.setForeground(Color.BLACK);
		password1.setHorizontalAlignment(SwingConstants.CENTER);
		password1.setFont(new Font("Arial", Font.BOLD, 12));
		password1.setBounds(610, 170, 90, 20);
		frame.getContentPane().add(password1);
		
		// To label
		to1 = new JLabel("To");
		to1.setForeground(Color.BLACK);
		to1.setHorizontalAlignment(SwingConstants.CENTER);
		to1.setFont(new Font("Arial", Font.BOLD, 12));
		to1.setBounds(610, 200, 90, 20);
		frame.getContentPane().add(to1);
		
		// When this button is clicked with mouse all the details of new 
		// Sender, sender password will be stored in the database
		btndone = new JButton("Done");
		btndone.setForeground(Color.BLACK);
		btndone.setBackground(UIManager.getColor("Button.background"));
		btndone.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				// If password or user name filed
				if(from.getText().equals("")||password.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "Username and password coloumn cannot be empty ");
				}
				else
				{
					// display the new sender mail address
					fromdisplay.setText(from.getText());
					
					// Delete current sender address and password
					String query="delete from Sender";
					
					// Insert new sender address and password
					String query2="insert into Sender values(?,?)";
					try 
					{
						// Create a prepared statement using first query
						pst=conn.prepareStatement(query);
						
						// Execute the first query
						pst.execute();
						
						// Create a prepared statement using second query
						pst=conn.prepareStatement(query2);
						
						// Set the new user name
						pst.setString(1,from.getText());
						
						// Set the new password
						pst.setString(2, password.getText());
						
						// Execute the second query
						pst.execute();
						
						// Close the prepared statemnt
						pst.close();
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					// Set from text field to empty
					from.setText(null);
					
					// Set password text field to empty
					password.setText(null);
				}
			}
		});
		btndone.setFont(new Font("Arial", Font.BOLD, 18));
		btndone.setBounds(880, 150, 90, 30);
		frame.getContentPane().add(btndone);
		
		// When this button is clicked with mouse all the details of new 
		// receiver will be stored in the database.
		btnok = new JButton("OK");
		btnok.setForeground(Color.BLACK);
		btnok.setBackground(UIManager.getColor("Button.background"));
		btnok.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				// If receiver field is empty
				if(to.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "Sender coloumn cannot be emty");
				}
				else
				{
					// Display new receiver address
					todisplay.setText(to.getText());
					
					// Create a query to delete old receiver address
					String query="delete from Reciever";
					
					// Create a query to insert new receiver values to database
					String query2="insert into Reciever values (?)";
					try 
					{
						// Create a prepared statement using first query
						pst=conn.prepareStatement(query);
						
						// Execute first query
						pst.execute();
						
						// Create a prepared statement using second query
						pst=conn.prepareStatement(query2);
						
						// set new receiver address
						pst.setString(1, to.getText());
						
						// Execute the second query
						pst.execute();
						
						// Close the prepared statement
						pst.close();
					} 
					catch (Exception e2) 
					{
						e2.printStackTrace();	
					}
					// Set the to text field to null
					to.setText(null);
				}
			}
		});
		btnok.setFont(new Font("Arial", Font.BOLD, 18));
		btnok.setBounds(880, 200, 90, 20);
		frame.getContentPane().add(btnok);
		
		// Create a from label 
		from2 = new JLabel("From");
		from2.setForeground(Color.BLACK);
		from2.setFont(new Font("Arial", Font.BOLD, 12));
		from2.setHorizontalAlignment(SwingConstants.CENTER);
		from2.setBounds(610, 230, 90, 20);
		frame.getContentPane().add(from2);
		
		// Create a to label
		to2 = new JLabel("To");
		to2.setForeground(Color.BLACK);
		to2.setHorizontalAlignment(SwingConstants.CENTER);
		to2.setFont(new Font("Arial", Font.BOLD, 12));
		to2.setBounds(610, 260, 90, 20);
		frame.getContentPane().add(to2);
		
		// This scroll pane is to display error messages when email is not properly send
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(700, 290, 270, 200);
		frame.getContentPane().add(scrollPane_2);
		
		// This is to display errors when email is not send properly
		errorarea = new JTextArea();
		errorarea.setForeground(Color.BLACK);
		errorarea.setBackground(Color.WHITE);
		scrollPane_2.setViewportView(errorarea);
		errorarea.setTabSize(1);
		errorarea.setRows(8);
		errorarea.setFont(new Font("Arial", Font.BOLD, 15));			
	} // end of initialize() function
	
	private static class __Tmp 
	{
		private static void __tmp() 
		{
			  javax.swing.JPanel __wbp_panel = new javax.swing.JPanel();
		}
	}
}

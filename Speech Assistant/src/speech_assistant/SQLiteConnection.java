package speech_assistant;
/* For this class to work do the following.
 * 1. In the "resource" folder there will be "JDBC SQLite Connector" folder.
 * 2. In "JDBC SQLite Connector" folder there will be a "sqlite-jdbc-3.16.1.jar" file.
 * 3. Configure JRE System Library and add the "sqlite-jdbc-3.16.1.jar" file to the JRE System library.
 * 4. The folder "Database" contains the .sqlite file that contains the list of words and email address etc.
*/
import java.sql.*;
import javax.swing.*;

// For more details on connecting data base to java application, you can visit this link
// https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
public class SQLiteConnection 
{
	// Create a connection object, and set to null
	Connection conn=null;
	public static Connection dbConnector()
	{
		try
		{
			// Invoke the database connector file
			Class.forName("org.sqlite.JDBC");
			
			// Get the current directory, i.e. project folder
			String currentDirectory = System.getProperty("user.dir");
			
			// This is the path of the database file
			String path = "jdbc:sqlite:" + currentDirectory + "\\resources\\Database\\JAW.sqlite" ;
			
			// Connect to database from the path
			Connection conn=DriverManager.getConnection(path);
			
			// Print "connection successful"
			System.out.println("Connection successful");
			return conn;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}

}
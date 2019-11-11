package speech_assistant;
/* For this class to work do the following.
 * 1. In the "resource" folder there will be "Java Mail 1.4.5" folder.
 * 2. In "Java Mail 1.4.5" folder there will be a "mail.jar" file.
 * 3. Configure JRE System Library and add the "mail.jar" file to the JRE System library.
*/

// Import the required classes
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

// For more details on sending mail by java you can check this site 
// https://www.javatpoint.com/example-of-sending-email-using-java-mail-api
public class MailSend 
{
	/* Create a function mailsend().
	 * Its parameters are text, username, password, to.
	 * text - the body of the message.
	 * username - from which mail address the message is being send i.e. sender mail address
	 * password - the password for the mail address i.e. sender password.
	 * to - to whom we are sending the mail i.e. receiver mail address.
	 * WE CAN ONLY SEND MAIL TO ONE USER. IF WE WANT TO SEND MAIL TO ANOTHER USER WE WILL HAVE TO MANUALLY CHANGE THE ADDRESS OF RECEIVER
	 * BUT IT WILL OVERWRITE THE ALREADY SAVED MAIL ADDRESS
	*/
	
	/* Also the account of the sender if it is a Gmail account it should enable "access to less secure apps" 
	 * which can be found in the privacy and security portion of the account.
	 * If it is not enabled then Gmail will not allow this API to push the message from the account of the sender.
	 * This will only work if the sender uses a Gmail account. 
	*/
	public static int mailsend(String text,String username,String password,String to) 
	{    
		// Properties of this mail.
		Properties props = new Properties();
		
		// Create an SMTP server.
		props.put("mail.smtp.auth", "true");
		
		// Enable TLS hand shake of the SMTP server. 
		props.put("mail.smtp.starttls.enable","true");
		
		/* The host server is Gmail account.
		 * This is why this will only work if the sender has a Gmail account.
		 * As we are setting the host server as Gmail.
		 * If you want to use this for other account you can change the server here
		*/
		props.put("mail.smtp.host", "smtp.gmail.com");
		
		// Set the SMTP mail sending port to 587
		props.put("mail.smtp.port", "587");
		
		// Create a new session, and authenticate username and password
		Session session = Session.getDefaultInstance(props, 
		new javax.mail.Authenticator() 
		{
			protected PasswordAuthentication getPasswordAuthentication() 
			{
				return new PasswordAuthentication(username, password);
			}
		});
		
		try 
		{
			// Create a new message object from the session
			Message message = new MimeMessage(session);
			
			// Set the from address
			message.setFrom(new InternetAddress(username));
			
			// Set the to address
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			// Set the subject. Here subject is set to null
			message.setSubject("");
			
			// Set the body of the message
			message.setText(text);
			
			// Send message to receiver
	  	  	Transport.send(message);
	  	  	System.out.println("Message sent");
	  	  	return 1;      
		}
		catch (MessagingException e) 
		{
			//JOptionPane.showMessageDialog(null,e);
			return 0;
	    }
	}
}

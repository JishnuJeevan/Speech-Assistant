# Speech-Assistant
B.Tech mini project create by me and my class mates Wreni Stanley and Anu Joseph. <br>
Done at Albertian Institute of Science and Technology. <br>
Under the guidance of Mrs. Sini Joy P.J, Assistant Professor, Computer Science Department, AISAT. <br>
During the academic year December - May, 2017. <br>
Semester 6. <br>
## Abstract
Speech assistant is software that can be used to help paralyzed people who cannot speak and write. People who are paralyzed will be having difficulty to write text using the keyboard as they will have difficulty going over to the entire keys in the keyboard. Our software can help them to type text to convey messages with the help of mouse. The mouse need not be dragged to the required key or to the required function like the speak operation. They can use the left and right mouse button to type the text. The right mouse button is used to scan through the list of keys and functions while the left mouse button is used to select the scanned key or function. An email facility is provided which will help the user to send email to a person that they rely on the most. A database is used to store a set of words that the user can choose from it. Here suggestions are provided for the most frequently used words, so that they don’t have to write the entire word. They can just select the required word from the database. New words written by the user are added into the database. The user interface in a 4x8 matrix format which includes alphabets, white space, backspace, alert button, speak button, email button to send email and list button for suggestions
## Prerequisites 
Once you have cloned the repository you need to do the following to make sure you can run the project. <br>
1. Make sure you have Net Beans or Eclipse IDE for Java Installed. <br>
2. For Main.java class to work, do the following. <br>
2.1. In the "resource" folder there will be "Free TTS 1.2.2" folder. <br>
2.2. In "Free TTS 1.2.2" folder there will be a few .jar files. <br>
2.3. Configure JRE System Library and add those .jar files to the JRE System library.<br>
3. For MailSend.Java to work, do the following. <br>
3.1. In the "resource" folder there will be "Java Mail 1.4.5" folder.<br>
3.2. In "Java Mail 1.4.5" folder there will be a "mail.jar" file.<br>
3.3. Configure JRE System Library and add the "mail.jar" file to the JRE System library.<br>
3.4. If we want to send email, we need to provide our email address and password to our mail account. But it will only work if it is a Gmail account. For it to work for other accounts, do the following.<br>
3.4.1. In the mail send file, change the server in line 48, the second paramerter. <br>
3.4.2. props.put("mail.smtp.host", "smtp.gmail.com"); If it is hot mail change it to "smtp.hotmail.com"<br>
4. For SQLiteConnection.java to work do the following.<br>
4.1. In the "resource" folder there will be "JDBC SQLite Connector" folder.<br>
4.2. In "JDBC SQLite Connector" folder there will be a "sqlite-jdbc-3.16.1.jar" file.<br>
4.3. Configure JRE System Library and add the "sqlite-jdbc-3.16.1.jar" file to the JRE System library.<br>
4.4. The folder "Database" contains the .sqlite file that contains the list of words and email address etc.<br>
## Authors and Institution
1. Jishnu Jeevan - Albertian Institute of Science and Technology. <br>
2. Wreni Stanley - Albertian Institute of Science and Technology. <br>
3. Anu Joseph - Albertian Institute of Science and Technology. <br>
## Reference.
More reference can be found in the project report that is presented with this repository. But here I will be providing the reference for the code used.
1. For more details on the text to speech converter we have used you can check this site - https://freetts.sourceforge.io/
2. For more details on connecting data base to java application, you can visit this link - https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
3. For more details on sending mail by java you can check this site - https://www.javatpoint.com/example-of-sending-email-using-java-mail-api
## Further Acknowledgment 
This work is inspired by the work done by the engineers at Intel. The have created a software called Assistive Context-Aware Toolkit (ACAT), which has been used by the late physicists Stephen Hawking for communication.<br>
If you want to know more about the work done by Intel, you can check it here - https://github.com/intel/acat
## Who is it for?
1. This is for people who are having disability and need some assistance in communicating with other.
2. This can also be used by researchers and UI engineers who want to know more about different ways of interacting with a computer system.
## Please Note.
1. This was done by us during our B.Tech. So when you read the project report, you will see that there is some difference in the code provided in the report and the actual code. This is because when we wrote the code we were in a hurry to finish the project so we did not comment it properly or even write it properly. But after B.Tech was over we had time to properly edit it and comment it to make it more readable. We didn't change the program functions or anything, we changed the code to make it more readable and changed the class names to make it more suitable for the function that they performed. 
2. Also in the report you will see lack of proper citations. This was also done due to the inexperience in our part, when writing a report. So all the reference and code that were not cited in the report can be found here in the README.md file.
3. We are providing the original report that was submited after the completion of the project.
4. Make sure you cite this repository if you are going to use it.
5. If you want to make any changes, it will be better if you leave this repository as it is and don't make any changes. 
6. If you do want to make changes, make a copy of this repository and make changes to that copy and upload that copy. Make sure that you cite this repository if you use it.

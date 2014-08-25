/* 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * All rights preserved.                                               *
 * Yingjing Feng (SID:1374873) from University of Birmingham           *
 * GMail client (version : 1.0,  update date:2013.11.14)               *
 * This is the GUI implementation of the program                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 */

/*
 *  SendMail.java   
 */


package MyGMail;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

/*
 * to send an email 
 */
public class SendMail extends Client {

        void resetConnectProp(){
        	Properties props = System.getProperties();
        	if(props.getProperty("mail.store.protocol")!=null)
        		props.remove("mail.store.protocol");
        }

        // this is referenced from Shan He's teaching example code
		public void send() throws IOException {
			
			resetConnectProp();
			System.out.println(username+password);
			String smtphost = "smtp.gmail.com";
			// Step 1: Set all Properties
			// Get system properties
			Properties props = System.getProperties();
			
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", 587);
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", smtphost);


			// Set Property with username and password for authentication 

				props.setProperty("mail.user", username);		
				props.setProperty("mail.password", password);

			//Step 2: Establish a mail session (java.mail.Session)
			   Session session = Session.getDefaultInstance(props);

			try {

				// Step 3: Create a message
				MimeMessage message = new MimeMessage(session);
				
				
				message.setFrom(new InternetAddress(username));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(MyGmailBox.getReiverText()));
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(MyGmailBox.getCcText()));
				message.setSubject(MyGmailBox.getSubjectText());
				message.setText(MyGmailBox.getMainText());
				
				if(MyGmailBox.attachments.size() != 0)
				{
					// cope with the attachment
					Multipart multipart = new MimeMultipart("related");
					
					for( int i=0; i<MyGmailBox.attachments.size(); i++){
						System.out.println("the "+i+" attachment");
						File att = MyGmailBox.attachments.get(i) ;
						
						MimeBodyPart tmpMimeBodyPart = new MimeBodyPart();
						FileDataSource fds=new FileDataSource(att);
						tmpMimeBodyPart.setDataHandler(new DataHandler(fds));
						tmpMimeBodyPart.setFileName(att.getName());
						
						multipart.addBodyPart(tmpMimeBodyPart);
					}

					message.setContent(multipart);
					message.saveChanges();  
				}
 
				
				// Step 4: Send the message by javax.mail.Transport .			
				Transport tr = session.getTransport("smtp");	// Get Transport object from session		
				tr.connect(smtphost, username, password); // We need to connect
				

                if( isRegister() == false )
                {
                	JOptionPane.showMessageDialog(null,"You haven't sign up!");
                }
                else
                {
                	tr.sendMessage(message, message.getAllRecipients()); // Send message
                	tr.close();
                	JOptionPane.showMessageDialog(null,"Send!");
                	
                }

			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
			finally{
			}
			
	
			
		}


	

}

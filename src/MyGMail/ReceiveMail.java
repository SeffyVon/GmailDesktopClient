/* 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * All rights preserved.                                               *
 * Yingjing Feng (SID:1374873) from University of Birmingham           *
 * GMail client (version : 1.0,  update date:2013.11.14)               *
 * This is the GUI implementation of the program                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 */

/*
 *  ReceiveMail.java   
 */

package MyGMail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.sun.mail.imap.IMAPFolder;



public class ReceiveMail extends Client{
	static ArrayList<News> Summary = new ArrayList<News> ();
	static String mainBody;
	static String attachment;
	static boolean isText = true;
	static long currentMailUID = 0;
	static int messagesCount = 0;
	ReceiveMail()throws Exception{
		mainBody = new String();
		
	}
	/*
	 * get all the client's mail boxes
	 */
	public ArrayList<String> getBoxes()throws MessagingException, IOException, MessagingException{
		ArrayList<String> boxes = new ArrayList<String>();
		Summary.clear();
		IMAPFolder folder = null;
		Store store = null;
	
		// Step 1: Set all Properties
		Properties props = System.getProperties();
		if(props.getProperty("mail.store.protocol").length() == 0)
			props.setProperty("mail.store.protocol", "imaps");
		
		// Set Property with username and password for authentication  
			props.setProperty("mail.user", username);
		
			props.setProperty("mail.password", password);
	
		//Step 2: Establish a mail session (java.mail.Session)
		Session session = Session.getDefaultInstance(props);
	
		try 
		{
			// We need to get Store from mail session
			// A store needs to connect to the IMAP server  
			System.out.println(password);
			store = session.getStore("imaps");
			store.connect("imap.googlemail.com",username, password);
			// Choose folder, in this case, we chose your inbox
			Folder[] folders = store.getDefaultFolder().list("*");
			
			for(Folder fd:folders)
				if(!fd.getFullName().equals("[Gmail]"))
					boxes.add(fd.getFullName());
			
		} catch (NoSuchProviderException e) {

			e.printStackTrace();
		} catch (MessagingException e) {

			e.printStackTrace();
		}
		finally 
		{
			if (folder != null && folder.isOpen()) { folder.close(true); }
			if (store != null) { store.close(); }
		}
		return boxes;
	}
	
	/*
	 * get the list of client's mails in a specific mailbox
	 */
	static public void receiveEmailSummary(String folderName) throws MessagingException, IOException, MessagingException {

		Summary.clear();
		IMAPFolder folder = null;
		Store store = null;
	
		// Step 1: Set all Properties
		Properties props = System.getProperties();
		if(props.getProperty("mail.store.protocol")!= null)
			props.setProperty("mail.store.protocol", "imaps");
		
		// Set Property with username and password for authentication  
			props.setProperty("mail.user", username);
		
			props.setProperty("mail.password", password);
	
		//Step 2: Establish a mail session (java.mail.Session)
		Session session = Session.getDefaultInstance(props);
	
		try 
		{
			// We need to get Store from mail session
			// A store needs to connect to the IMAP server  
			store = session.getStore("imaps");
			store.connect("imap.googlemail.com",username, password);

			// Choose folder, in this case, we chose your inbox
			folder = (IMAPFolder) store.getFolder(folderName); 
			

			if(!folder.isOpen())
			{
				if (folderName == "[Gmail]/Sent Mail")
					folder.open(Folder.READ_ONLY);
				else
					folder.open(Folder.READ_WRITE);
			}
			
			System.out.println("No of Messages : " + folder.getMessageCount());
			if (folderName.equals( "INBOX"))
				messagesCount = folder.getMessageCount();
			
			Message messages[] = folder.getMessages();

			// Get all messages
			for(Message message:messages) {
				
				Flags mes_flag = message.getFlags();
				// Get subject of each message 
				String subject = new String("");
				if( message.getSubject() != null)
					subject = message.getSubject();
				Address[] email = message.getFrom();
				long newsID = folder.getUID(message);
				
				Summary.add(0,new News(email[0].toString(),subject,newsID, mes_flag.contains(Flag.SEEN)));		
			}	

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		finally 
		{
			if (folder != null && folder.isOpen()) { folder.close(true); }
			if (store != null) { store.close(); }
		
		}
	}

	
    /* 
     * to get email from a specific id
     * */
	public void getEmailFromIDAndSave(long UID, String folderName) throws IOException, MessagingException{
		currentMailUID = UID;
		IMAPFolder folder = null;
		Store store = null;
		isText = true;
		attachment = new String();

		BufferedWriter writer = null;
	    writer = new BufferedWriter( new FileWriter( "fnew.html"));
	    
		// Step 1: Set all Properties
		Properties props = System.getProperties();
		if(props.getProperty("mail.store.protocol")!= null)
				props.setProperty("mail.store.protocol", "imaps");
				
		// Set Property with username and password for authentication  
		props.setProperty("mail.user", username);
		props.setProperty("mail.password", password);
	
		//Step 2: Establish a mail session (java.mail.Session)
		Session session = Session.getDefaultInstance(props);
	
		/*
		 *  the code here is referenced from http://adventure.iteye.com/blog/380610
		 *  to analyze a message 
		 */
		try 
		{
			// We need to get Store from mail session
			// A store needs to connect to the IMAP server  
			store = session.getStore("imaps");
			store.connect("imap.googlemail.com",username, password);

			// Choose folder
			folder = (IMAPFolder) store.getFolder(folderName); 

			if(!folder.isOpen())
			{
				if (folderName == "[Gmail]/Sent Mail")
					folder.open(Folder.READ_ONLY);
				else
					folder.open(Folder.READ_WRITE);
			}
			
			
			UIDFolder ufolder = (UIDFolder) folder;
			Message message = ufolder.getMessageByUID(UID);
			
	        Address[] froms = message.getFrom();  
	        
	        if(froms != null) {  
	            System.out.println("from:" + froms[0]);  
	            InternetAddress addr = (InternetAddress)froms[0];  
	            System.out.println("address:" + addr.getAddress());  
	            System.out.println("sender:" + addr.getPersonal());  
	        }  
	
	        System.out.println("subject:" + message.getSubject());
	        
	        // the code here is referenced from http://adventure.iteye.com/blog/380610
	        // if it is text/plain
	        if(message.getContentType().contains("TEXT/PLAIN")) {
	        	mainBody=(String) message.getContent();
			}
	        // it is multipart
	        else
	        {
	        	isText = true;
				Multipart multipart = (Multipart) message.getContent();
				// MultiPart  
				System.out.println("parts:" + multipart.getCount() + "");  
				
				
				for (int j = 0, n = multipart.getCount(); j < n; j++) {  
				System.out.println("the " + j + "th part");  
				Part part = multipart.getBodyPart(j);
				
				// Content-Type: multipart/alternative  
				if (part.getContent() instanceof Multipart) {  
					isText = true;
					Multipart p = (Multipart) part.getContent();
					
					for (int k = 0; k < p.getCount(); k++) {  
						System.out.println("type:"  
						+ p.getBodyPart(k).getContentType());  
						if(p.getBodyPart(k).getContentType().startsWith("text/plain")) {  
							
							Object o = p.getBodyPart(k).getContent();
							mainBody = (String)o;		
						} 
						else{  
							isText = false;
							 System.out.println("isText = false");
							Object o = p.getBodyPart(k).getContent();  
							if (  o instanceof String )
								mainBody = (String)o;
							
							try
							{
							    writer.write( copeWithHTMLString(mainBody));
							}
							catch ( IOException e)
							{
								e.printStackTrace();
							}
							finally
							{
							    try
							    {
							        if ( writer != null)
							        writer.close( );
							    }
							    catch ( IOException e)
							    {
							    	e.printStackTrace();
							    }
							}
							System.out.println(o);
						}  
					}  
				}
				else {  
						// html 
						isText = false; 
						System.out.println("isText = false");
						Object o = part.getContent();  
						if ( o instanceof String )
							mainBody = (String)o;
						
						try
						{
						    writer = new BufferedWriter( new FileWriter( "fnew.html"));
						    writer.write( copeWithHTMLString(mainBody));
						}
						catch ( IOException e)
						{
							e.printStackTrace();
						}
						finally
						{
						    try
						    {
						        if ( writer != null)
						        writer.close( );
						    }
						    catch ( IOException e)
						    {
						    	e.printStackTrace();
						    }
						}
						System.out.println(o);
					}
			
			    // Content-Disposition: attachment;  
				String disposition = part.getDisposition();
				if (disposition != null) {  
					String dir = System.getProperty("user.dir");
					String newline = System.getProperty("line.separator");
					attachment = attachment + "the " + (j+1) + "th attachment"+ newline;
					attachment = attachment + "attachment saved in: " + dir + part.getFileName() + newline;
					attachment = attachment + "attachment type: " + part.getContentType() + newline;
					 
					java.io.InputStream in = part.getInputStream();
					
					java.io.FileOutputStream out = new FileOutputStream(part.getFileName());  
					int data;  
					while((data = in.read()) != -1) {  
						out.write(data);  
					}  
					in.close();  
					out.close();  
				}  
				
			}
		}   
		 
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			if (folder != null && folder.isOpen()) { folder.close(true); }
			if (store != null) { store.close(); }
		}
	}
	
	/*
	 * to cope with HTML string of the message
	 * eg. "cid:image001.jpg@dafddafdsfa@ --> "image001.jpg"
	 */
	static String copeWithHTMLString(String htmlStr){
		String newStr = new String();
		System.out.println(htmlStr);
		int j1 = -1;
		int j2 = 0;
		int last2 = 0;
		for(int i=0;i<htmlStr.length()-4;i++){
			
			if(((String) htmlStr.substring(i,i+4)).equals("cid:"))
			{
				String sub = (String) htmlStr.substring(i+4,htmlStr.length());
				j1 = sub.indexOf('@');
				j2 = sub.indexOf('\"');
				if((j1 - i+4) >30  || j1 == -1 && last2<=i) 
					newStr = newStr + htmlStr.substring(last2, i) + htmlStr.substring(i+4,i+4+j2) + ".png";	//for google

				else
					newStr = newStr + htmlStr.substring(last2,i) + htmlStr.substring(i+4,(j1+i+4));
				i = i+4+j2;
				last2 = i;
			}	
		}
		newStr = newStr + htmlStr.substring( last2 ,htmlStr.length());
		return newStr;
	}
	
	/*
	 * to delete an email in the server
	 * @param name of folder
	 * @return boolean - if it is successfully deleted
	 */
	boolean deleteAnEmail(String folderName) throws MessagingException{
		IMAPFolder folder = null;
		Store store = null;
	
		// Step 1: Set all Properties
		Properties props = System.getProperties();
		if(props.getProperty("mail.store.protocol")!=null)
			props.setProperty("mail.store.protocol", "imaps");
		
		// Set Property with username and password for authentication  
			props.setProperty("mail.user", username);
		
			props.setProperty("mail.password", password);
	
		//Step 2: Establish a mail session (java.mail.Session)
		Session session = Session.getDefaultInstance(props);
	
		try 
		{
			// We need to get Store from mail session
			// A store needs to connect to the IMAP server  
			store = session.getStore("imaps");
			store.connect("imap.googlemail.com",username, password);

			// Choose folder, in this case, we chose your inbox
			folder = (IMAPFolder) store.getFolder(folderName); 
			

			if(!folder.isOpen())
			{
				if (folderName == "[Gmail]/Sent Mail")
					folder.open(Folder.READ_ONLY);
				else
					folder.open(Folder.READ_WRITE);
			}
			
			Message m = folder.getMessageByUID(currentMailUID);
			m.setFlag(Flags.Flag.DELETED, true);
			
			if(folderName.equals("INBOX"))
				messagesCount--;
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		finally 
		{
			if (folder != null && folder.isOpen()) { folder.close(true); }
			if (store != null) { store.close(); }
		}
			
	}
	
	/*
	 * save an email in draft box in the server
	 * @param the content of the write panel
	 * @return boolean - if it is successfully saved in the server
	 */
	boolean saveAnEmail() throws MessagingException{
		IMAPFolder folder = null;
		Store store = null;
	
		// Step 1: Set all Properties
		Properties props = System.getProperties();
		if(props.getProperty("mail.store.protocol").length() == 0)
			props.setProperty("mail.store.protocol", "imaps");
		
		// Set Property with username and password for authentication  
			props.setProperty("mail.user", username);
		
			props.setProperty("mail.password", password);
	
		//Step 2: Establish a mail session (java.mail.Session)
		Session session = Session.getDefaultInstance(props);
	
		try 
		{
			// We need to get Store from mail session
			// A store needs to connect to the IMAP server  
			store = session.getStore("imaps");
			store.connect("imap.googlemail.com",username, password);

			// Choose folder, in this case, we chose your inbox
			folder = (IMAPFolder) store.getFolder("[Gmail]/Drafts"); 
			folder.open(Folder.READ_WRITE);

			MimeMessage draftMessage = new MimeMessage(session);
			
			
			draftMessage.setFrom(new InternetAddress(username));
			draftMessage.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(MyGmailBox.getReiverText()));
			draftMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(MyGmailBox.getCcText()));
			draftMessage.setSubject(MyGmailBox.getSubjectText());
			draftMessage.setText(MyGmailBox.getMainText());
			
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

			draftMessage.setContent(multipart);
			draftMessage.saveChanges();   
			
			MimeMessage[] draftMessages = {draftMessage};
			folder.appendMessages(draftMessages);
			
			JOptionPane.showMessageDialog(null, "Successfully Save!");
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally 
		{
			if (folder != null && folder.isOpen()) { folder.close(true); }
			if (store != null) { store.close(); }
		}
			
		return true;
	}

	/*
	 * to detect whether there are any new mails
	 */
	static boolean detectNewMails () throws MessagingException{
		
			IMAPFolder folder = null;
			Store store = null;
		
			// Step 1: Set all Properties
			Properties props = System.getProperties();
			if(props.getProperty("mail.store.protocol") != null)
				props.setProperty("mail.store.protocol", "imaps");
			
			// Set Property with username and password for authentication  
				props.setProperty("mail.user", username);
			
				props.setProperty("mail.password", password);
		
			//Step 2: Establish a mail session (java.mail.Session)
			Session session = Session.getDefaultInstance(props);
		
			try 
			{
				// We need to get Store from mail session
				// A store needs to connect to the IMAP server  
				store = session.getStore("imaps");
				store.connect("imap.googlemail.com",username, password);

				// Choose folder, in this case, we chose your inbox
				folder = (IMAPFolder) store.getFolder("INBOX"); 
				
				folder.open(Folder.READ_WRITE);

				System.out.println("~~~No of Messages : " + folder.getMessageCount());
				System.out.println("~~~messagesCount : " + messagesCount);
				Message messages[] = folder.getMessages();
				final int unreceiveCount = folder.getMessageCount() - messagesCount;
				if( unreceiveCount != 0)
				{
		            System.out.println("No. of new Messages : " + unreceiveCount);
	           
		            ArrayList<News> newSummary = new ArrayList<News>();
		            
		            for( int i=0; i < unreceiveCount; i++ ){
		            	Message message = messages[messages.length-1-i];
						Flags mes_flag = message.getFlags();
						// Get subject of each message 
						
						String subject = new String("");
						if( message.getSubject() != null)
							subject = message.getSubject();
						Address[] email = message.getFrom();
						long newsID = folder.getUID(message);
						
		            	newSummary.add(0,new News(email[0].toString(), subject, newsID, mes_flag.contains(Flag.SEEN)));		
		            }
		            if(MyGmailBox.boxesNames[MyGmailBox.currentBoxIndex].equals("INBOX"))
		            	messagesCount += unreceiveCount;
		            
		            MyGmailBox.inboxModel.AddUnreceivedNews(newSummary);
					MyGmailBox.inboxModel.fireTableDataChanged();
					SwingUtilities.invokeLater(new Runnable(){

						@Override
						public void run() {
							 MyGmailBox.reminderLabel.setText("Incoming messages : " + unreceiveCount + " No. of unread messages:" + MyGmailBox.inboxModel.unreadMessages());	
						}
						
					});
					
					return true;
		            
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally 
			{
				if (folder != null && folder.isOpen()) { folder.close(true); }
				if (store != null) { store.close(); }
			}
			return false;
	}
	



}


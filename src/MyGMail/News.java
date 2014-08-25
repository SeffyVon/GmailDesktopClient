/* 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * All rights preserved.                                               *
 * Yingjing Feng (SID:1374873) from University of Birmingham           *
 * GMail client (version : 1.0,  update date:2013.11.14)               *
 * This is the GUI implementation of the program                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 */

/*
 *  News.java   
 */

package MyGMail;

/*
 * to construct a class for news
 * @member 
 * newsID: UID of the message
 * emailAddress: email address
 * emailSubject: email subject
 * seen: if email is seen or not
 */
public class News{
	String emailAddress, emailSubject;
	long newsID;
	boolean seen;
	void setSeen(boolean read){
		seen = read;
	}
	
	News(String emailAdd, String emailSub, long ID)
	{
		emailAddress = new String(emailAdd);
		emailSubject = new String(emailSub);
		newsID = ID;
		seen = false;
	}
	
	News(String emailAdd, String emailSub, long ID, boolean _seen)
	{
		emailAddress = new String(emailAdd);
		emailSubject = new String(emailSub);
		newsID = ID;
		seen = _seen;
	}
	
	String getAddress(){
		return emailAddress;
	}
	
	String getSubject(){
		return emailSubject;
	}
	
	long getID(){
		return newsID;
	}
}
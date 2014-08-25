package MyGMail;
/* 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * All rights preserved.                                               *
 * Yingjing Feng (SID:1374873) from University of Birmingham           *
 * GMail client (version : 1.0,  update date:2013.11.14)               *
 * This is the GUI implementation of the program                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 */

/*
 *  Contact.java   
 */

 /*
  * A class to store contact
  */
public class Contact {

	String name;
	String emailAddress;
	String group;
	
	Contact(String _name, String _emailAddress, String _group){
		name = _name;
		emailAddress = _emailAddress;
		group = _group;
	}
	
	String getName(){
		return name;
	}
	
	String getEmail(){
		return emailAddress;
	}
	
	String getGroup(){
		return group;
	}
}

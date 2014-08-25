/* 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * All rights preserved.                                               *
 * Yingjing Feng (SID:1374873) from University of Birmingham           *
 * GMail client (version : 1.0,  update date:2013.11.14)               *
 * This is the GUI implementation of the program                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 */

/*
 *  Client.java   
 *  this is to manage the account list and current account
 */

package MyGMail;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.JOptionPane;

public class Client {
	Client() {
		accounts = new ArrayList<Account>();
		
	}
	static boolean isRegister=false;
	// current client;
	static String username ;
	static String password ;      
	ArrayList<Account> accounts;
	
	/*
	 * this is an Gmail account 
	 */
	class Account{
		String username;
		String password;
		
		Account(String u, String p){
			username = u;
			password = p;
		}
		
		String getUsername(){
			return username;
		}
		
		String getPassword(){
			return password;
		}
	}
	
	String getUsername(){
		return username;
	}
	
	String getPassword(){
		return password;
	}
	
	private void setCurrentUsernameAndPassword(String un, String pw){
		username = un;
		password = pw;
	}
	
	/*
	 * to add an new Account(user name, password) to accounts
	 * @return 
	 * - true  : add an new one
	 * - false : exist in account list or not registered in gmail
	 */
	boolean AddUsernameAndPassword(String un, String pw) throws MessagingException{
		for(int i = 0; i < accounts.size(); i++)
			if(accounts.get(i).getUsername() == un){
				setCurrentUsernameAndPassword(un, pw);
				return false;
			}
		
		// Try to get connection ....
		try{
			Store store = null;
			
			// Step 1: Set all Properties
			Properties props = System.getProperties();
			if(props.getProperty("mail.store.protocol") == null)
				props.setProperty("mail.store.protocol", "imaps");
			
			// Set Property with username and password for authentication  
			if(props.getProperty("mail.user") == null)
				props.setProperty("mail.user", un);
			if(props.getProperty("mail.password") == null)
				props.setProperty("mail.password", pw);
		
			//Step 2: Establish a mail session (java.mail.Session)
			Session session = Session.getDefaultInstance(props);
			store = session.getStore("imaps");
			store.connect("imap.googlemail.com",un, pw);
			
		} catch (NoSuchProviderException e) {
			
			JOptionPane.showMessageDialog(null,"You can't use this user/password combination to Gmail!");
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(null,"You can't use this user/password combination to Gmail!");
			e.printStackTrace();
			return false;
		}
		accounts.add(new Account(un,pw));
		isRegister = true;
		System.out.println("register!" + accounts.size());
		return true;
	}

	/*
	 * package-private function 
	 * set an current account 
	 */
	void SetCurrentAccount(int index){
		setCurrentUsernameAndPassword(accounts.get(index).getUsername(), accounts.get(index).getPassword());
	}
	
	public int lookUpIndex(String name){
		for(int i=0;i<accounts.size();i++){
			System.out.println(name + " user " + accounts.get(i).getUsername());
			if( accounts.get(i).getUsername().equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * package-private function
	 * to check if there is an current account
	 */
	boolean isRegister (){
		return isRegister;
	}
	
}

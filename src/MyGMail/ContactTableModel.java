package MyGMail;

import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

public class ContactTableModel extends AbstractTableModel{
	
	//the view contact
	ArrayList<Contact> contactsBook;
	
	//all contact
	static ArrayList<Contact> allContactsBook;
	String columnName[] = {"name","email address","group"};
	
	static ArrayList<Contact> getAllContactsBook(){
		return allContactsBook;
	}
	ContactTableModel(){
		
		allContactsBook = new ArrayList<Contact>();
		contactsBook = new ArrayList<Contact>( );
		allContactsBook.add(new Contact("a","b@163.com","friend"));
		allContactsBook.add(new Contact("a2","b2@163.com","family"));
		allContactsBook.add(new Contact("a3","b3@163.com","family"));
		allContactsBook.add(new Contact("a4","b4@163.com","friend"));
		allContactsBook.add(new Contact("me","feng0823yj@gmail.com","friend"));
		contactsBook.addAll(allContactsBook);
	}
	void addContact(Contact c){
		allContactsBook.add(0,c);
		if(MyGmailBox.currentGroupName.equals("All") || c.getGroup().equals(MyGmailBox.currentGroupName)){
			contactsBook.add(0,c);
		}
			
		
	}
	@Override
    public String getColumnName(int col) {
		System.out.println("!!"+columnName[col]);
    	return columnName[col];
    }
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnName.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return contactsBook.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		if(col == 0)
			return contactsBook.get(row).getName();
		else if(col == 1)
			return contactsBook.get(row).getEmail();
		else
			return contactsBook.get(row).getGroup();
	}
	public void deleteContactByRow(int rowForContact) {
		String email = contactsBook.get(rowForContact).getEmail();
		contactsBook.remove(rowForContact);
		for(int i = 0; i < allContactsBook.size(); i++){
			if(allContactsBook.get(i).getEmail().equals(email))
				allContactsBook.remove(i);
		}
		
	}
	public void searchContactByRow(String groupName) {
		contactsBook.clear();
		if(groupName.equals("All"))
			contactsBook.addAll(allContactsBook);
		else{
			System.out.println("[size]"+allContactsBook.size());
			for(Contact contact : allContactsBook){
				System.out.println("groupname:"+contact.getGroup());
				if(contact.getGroup().equals(groupName))
					contactsBook.add(contact);
			}
		}
		
		
		
	}
	public ArrayList<Contact> searchContactByName(String name) {
		ArrayList<Contact> result = new ArrayList<Contact>();
		for(int i = 0; i < allContactsBook.size(); i++){
			if((allContactsBook.get(i).getName()).toLowerCase().indexOf(name)!=-1)
				result.add(allContactsBook.get(i));
		}
		return result;
	}
	public ArrayList<Contact> searchContactByEmail(String email) {
		ArrayList<Contact> result = new ArrayList<Contact>();
		for(int i = 0; i < allContactsBook.size(); i++){
			if((allContactsBook.get(i).getEmail()).toLowerCase().indexOf(email)!=-1)
				result.add(allContactsBook.get(i));
		}
		return result;
	}

}


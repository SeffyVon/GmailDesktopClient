/* 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * All rights preserved.                                               *
 * Yingjing Feng (SID:1374873) from University of Birmingham           *
 * GMail client (version : 1.0,  update date:2013.11.14)               *
 * This is the GUI implementation of the program                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 */

/*
 *  MyGamilBox.java   
 */


package MyGMail;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.html.HTMLEditorKit;

import MyGMail.Client.Account;

/*
 * once in a time: refresh the box, read the mail
 */
/*
 * main class for GUI
 */
public class MyGmailBox implements ActionListener ,TableModelListener{
	
	/*
	 * components for GUI
	 */
	JPanel totalGUI, buttonPanel, mainTextPanel;
	JButton sendButton,saveButton,cancelButton,attachButton;
	JSplitPane splitPanel;
	JScrollPane tableScrollPane, contentScrollPane;
	static JTextPane contentText;
	JPanel inboxButtonPanel;
	static MyTableModel inboxModel;
	JButton deleteButton1;	 
	JLabel receiverL, ccL, subjectL, attachL;
    JComboBox foldersBox;
	static JEditorPane mainText;
	JTable inboxTable ,outboxTable, deleteboxTable;
	static JTextField receiverT;
	static JTextField ccT;
	static JTextField subjectT;
	
	JTextField attachT;
	String receiverS = "To:";
	String ccS = "Cc:";
	String subjectS = "Subject:";
	String attachS = "Attach:";
	
	static ExecutorService refreshExecutor = Executors.newSingleThreadExecutor();
	ExecutorService listSelectorExecutor = Executors.newSingleThreadExecutor();
	
	//save all names of mail boxes
	static String boxesNames[];
	
	//save the index of current box
	static int currentBoxIndex;
	
	//save the 
	ArrayList<JRadioButtonMenuItem> radioItems;
	
	// save the current account
	String selectedAccount;
	
	// used to save all attachment
	static ArrayList<File> attachments; 
	
	 static public String getReiverText(){
		 return receiverT.getText(); 
	 }
	 
	 static public String getSubjectText(){
		 return subjectT.getText(); 
	 }
	 static public String getCcText(){
		 return ccT.getText(); 
	 }
	 static public String getMainText(){
		 return mainText.getText(); 
	 }
	 
	
	 
	 /*
	  * create the "send" panel
	  * @param null
	  * @return JPanel totalGUI
	  */
	 public JPanel createSendPanel(){
		 attachments = new ArrayList<File>();
		 
		 totalGUI = new JPanel();
		 totalGUI.setLayout(null);
		 
		 //Create Panel of Send and Save buttons 
		 buttonPanel = new JPanel();
		 buttonPanel.setLayout(null);
		 buttonPanel.setLocation(0,10);
		 buttonPanel.setSize(1000,40);
		 totalGUI.add(buttonPanel);
		 
		 // Send Button
		 sendButton = new JButton("send");
		 sendButton.setSize(100,40);
		 sendButton.setLocation(0,0);
		 sendButton.addActionListener(this);
		
		 // Save Button
		 saveButton = new JButton("save");
		 saveButton.setSize(100,40);
		 saveButton.setLocation(110,0);	
		 saveButton.addActionListener(this);
		 
		 // cancel Button
		 cancelButton = new JButton("cancel");
		 cancelButton.setSize(100,40);
		 cancelButton.setLocation(220,0);	
		 cancelButton.addActionListener(this);
		 
		 buttonPanel.add(sendButton);
		 buttonPanel.add(saveButton);
		 buttonPanel.add(cancelButton);
		 
		 mainTextPanel = new JPanel();
		 mainTextPanel.setLayout(null);
		 mainTextPanel.setLocation(0,30);
		 mainTextPanel.setSize(1250,750);
		 totalGUI.add(mainTextPanel);
		 
		 receiverT = new JTextField(900);
		 receiverT.addActionListener(this);
		 receiverT.setActionCommand(receiverS);
		 receiverT.setSize(1150,40);
		 receiverT.setLocation(60,30);
		 
		 /*
		  * this refers to http://twaver.servasoft.com/twaver-java
		  * to match email in the contact book
		  * to complete it automatically
		  */
		 
		 // use to store the content in the popup
		 final DefaultComboBoxModel cbmodel = new DefaultComboBoxModel();

		 final JComboBox cbInput = new JComboBox(cbmodel) {
				public Dimension getPreferredSize() {
					return new Dimension(super.getPreferredSize().width, 0);
				}
			};
			 
		 contactModel = new ContactTableModel();
		 receiverT.setLayout(new BorderLayout());
		 receiverT.add(cbInput, BorderLayout.SOUTH);
		 cbInput.setLocation(0,40);
		 receiverT.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateList();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateList();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateList();
			}
			
			private void updateList() {
				
				String input = receiverT.getText();
				if(!input.isEmpty()){
					cbmodel.removeAllElements();
					for( Contact c : ContactTableModel.getAllContactsBook()){
						if(c.getEmail().toLowerCase().startsWith(input))
							cbmodel.addElement(c.getEmail());
					}
					cbInput.setPopupVisible(cbmodel.getSize() > 0);
				}
					
				
			}
		 });
		

		 /*
		  * detect the keyboard event
		  */
		 receiverT.addKeyListener(new KeyAdapter(){
			 @Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {//if enter a space then it select
						if (cbInput.isPopupVisible()) {
							e.setKeyCode(KeyEvent.VK_ENTER);
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
						e.setSource(cbInput);
						cbInput.dispatchEvent(e);
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {//if enter a enter then it select, then set text
							receiverT.setText(cbInput.getSelectedItem().toString());
							cbInput.setPopupVisible(false);
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {//if enter a "ESC", then eliminate the popup
						cbInput.setPopupVisible(false);
					}
				}
		 });
		 
		 receiverL = new JLabel(receiverS);
		 receiverL.setLabelFor(receiverT);
		 receiverL.setSize(60,40);
		 receiverL.setLocation(0,30);
		
		 mainTextPanel.add(receiverT);
		 mainTextPanel.add(receiverL);
		 
		 ccT = new JTextField(900);
		 ccT.addActionListener(this);
		 ccT.setActionCommand(ccS);
		 ccT.setSize(1150,40);
		 ccT.setLocation(60,70);
		 
		 ccL = new JLabel(ccS);
		 ccL.setLabelFor(ccT);
		 ccL.setSize(60,40);
		 ccL.setLocation(0,70);
		 
		 mainTextPanel.add(ccT);
		 mainTextPanel.add(ccL);
		 
		 subjectT = new JTextField(900);
		 subjectT.addActionListener(this);
		 subjectT.setActionCommand(subjectS);
		 subjectT.setSize(1150,40);
		 subjectT.setLocation(60,110);
		 
		 subjectL = new JLabel(subjectS);
		 subjectL.setLabelFor(subjectT);
		 subjectL.setSize(60,40);
		 subjectL.setLocation(0,110);

		 mainTextPanel.add(subjectT);
		 mainTextPanel.add(subjectL);
		 
		 attachT = new JTextField(900);
		 attachT.addActionListener(this);
		 attachT.setActionCommand(attachS);
		 attachT.setSize(1000,40);
		 attachT.setLocation(60,150);
		 
		 attachL = new JLabel(attachS);
		 attachL.setLabelFor(attachT);
		 attachL.setSize(60,40);
		 attachL.setLocation(0,150);
		 
		 attachButton = new JButton("add attachments");
		 attachButton.setLocation(1080,150);
		 attachButton.setSize(120,40);
		 attachButton.addActionListener(this);

		 mainTextPanel.add(attachT);
		 mainTextPanel.add(attachL);
		 mainTextPanel.add(attachButton);
		 
		 mainText = new JEditorPane();
		 mainText.setEditable(true);
		 
		 JScrollPane mainTextScrollPane = new JScrollPane(mainText);
		 mainTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 mainTextScrollPane.setBounds(0,195,1220,430);
		 mainTextPanel.add(mainTextScrollPane);
		 
		 totalGUI.setOpaque(true);
		 return totalGUI;
	}

	 /*
	  * create the "receive" panel
	  * @param null
	  * @return JPanel totalGUI
	  */ 
	
	 public JPanel createInboxPanel() throws Exception{
		 JPanel receiverPanel = new JPanel();
		 inboxButtonPanel = new JPanel();
		 
		 // use to hold the summary scroll pane as well as the context scroll pane 
		 JPanel splitPanel = new JPanel();
		
		 inboxModel = new MyTableModel();
		 inboxTable = new JTable(inboxModel);
         ListSelectionModel tableSelectionModel = inboxTable.getSelectionModel();
         tableSelectionModel.setSelectionInterval(0, 1);
         tableSelectionModel.addListSelectionListener( new inboxListSelectionHandler());
         inboxTable.setSelectionModel(tableSelectionModel);
		 
         
		 tableScrollPane = new JScrollPane(inboxTable);
		
	     contentText = new JTextPane();
	     contentScrollPane = new JScrollPane(contentText);
	     
	     JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, contentScrollPane);
	     splitPane.setOneTouchExpandable(true);
	     splitPane.setDividerLocation(300);
	     
	     tableScrollPane.setMinimumSize(new Dimension(40,720));
	     contentScrollPane.setMinimumSize(new Dimension(40,720));
	   
	     splitPane.setPreferredSize(new Dimension(1230, 600));
         
	     inboxButtonPanel.setSize(1230,100);	   
	     
	     // combo boxes listing the boxes
	     ReceiveMail r = new ReceiveMail();	 
	     ArrayList<String> boxes = r.getBoxes();

	     boxesNames = new String[boxes.size()];
	     boxesNames = boxes.toArray(boxesNames);
	     foldersBox = new JComboBox(boxesNames);
	     foldersBox.addActionListener(this);
	     inboxButtonPanel.add(foldersBox);

	     deleteButton1 = new JButton("delete");
	     deleteButton1.setSize(100, 40);
	     deleteButton1.addActionListener(this);
	     inboxButtonPanel.add(deleteButton1);
	   
	     splitPanel.add(splitPane);
	   
	     receiverPanel.setLayout(new BorderLayout());
	   
	     receiverPanel.add(inboxButtonPanel,BorderLayout.PAGE_START);
	     receiverPanel.add(splitPane,BorderLayout.PAGE_END);
	     
		 return receiverPanel;
	 }
	 
	/*
	 * create tab pane for "receive" panel and "send" panel
	 * @param null
	 * @return JTabbedPane containing "receive" panel and "send" panel
	 */
	public JTabbedPane createTabbedPane() throws Exception{
		 while(client.isRegister() == false){
	    	 
			 // add an account
			 JTextField un = new JTextField();
			 JTextField pw = new JPasswordField();
			 Object[] message = {
			     "Username:", un,
			     "Password:", pw
			 };

			 int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
			 if(option == JOptionPane.OK_OPTION){
				 client.AddUsernameAndPassword(un.getText(), pw.getText());
				 if(client.isRegister()){
					 client.SetCurrentAccount(0);
						
				 }
			 }
			 else{
				System.exit(0); 
			 }
			 
//			 client.AddUsernameAndPassword("feng0823yj@gmail.com", "fyj7251627@fyj");
//			 client.SetCurrentAccount(0); 
	     }
		 JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

		 tabbedPane.addTab("Send",createSendPanel());
		 tabbedPane.addTab("Receive",createInboxPanel());
		 tabbedPane.addTab("Contacts", createContactPanel());	
		 
		 System.out.println(client.getUsername());
		 System.out.println(client.getPassword());
		 
		 try {
				Runnable worker = new Runnable(){
					@Override
					public void run() {
						try {
							 ReceiveMail.receiveEmailSummary(boxesNames[currentBoxIndex]);
							 System.out.println("summary size:"+ReceiveMail.Summary.size());
							 inboxModel.AddManyNews(ReceiveMail.Summary);
							 inboxModel.fireTableDataChanged();
							 SwingUtilities.invokeLater(new Runnable(){
								@Override
								public void run() {
									reminderLabel.setText("");
								}
							 });
							 
						} catch (MessagingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				 };
				 // only allow one refresh one time
				 refreshExecutor.execute(worker);
		 }
		 catch(Exception e2){
			 e2.printStackTrace();
		 }
		 
		 return tabbedPane;
	 }
	
	JButton deleteContact, addContact, searchContact;
	JPanel contactButtonPanel,contactTablePanel;
	JTable contactTable;
	ContactTableModel contactModel;
	
	private Component createContactPanel() {
		JPanel contactPanel = new JPanel();
		contactPanel.setSize(1230,570);
		contactPanel.setLayout(new BorderLayout());
		
		contactButtonPanel = new JPanel();
		contactButtonPanel.setSize(1230,50);
		
		deleteContact = new JButton("delete");
		deleteContact.addActionListener(this);
		
		addContact = new JButton("add");
		addContact.addActionListener(this);
		

        groupsListBox = new JComboBox(groupsNames);
        groupsListBox.addActionListener(this);
        
		searchContact = new JButton("search");
		searchContact.addActionListener(this);
		
		contactButtonPanel.add(addContact);
		contactButtonPanel.add(deleteContact);
		contactButtonPanel.add(searchContact);
		contactButtonPanel.add(new JLabel("groups:"));
		contactButtonPanel.add(groupsListBox);
		
		
		contactTable = new JTable(contactModel);
		contactTable.setPreferredSize(new Dimension(1230,550));
		
        ListSelectionModel contactTableSelectionModel = contactTable.getSelectionModel();
        contactTableSelectionModel.setSelectionInterval(0, 0);
        contactTableSelectionModel.addListSelectionListener( new contactListSelectionHandler());
        contactTable.setSelectionModel(contactTableSelectionModel);
		
        contactTablePanel = new JPanel();
        contactTablePanel.setSize(1230,550);
        contactTablePanel.add(contactTable);
        
        
        
		contactPanel.add(contactButtonPanel,BorderLayout.PAGE_START);
		contactPanel.add(contactTablePanel);
		return contactPanel;
	}

	/*
	 * to perform the action event
	 * @param ActionEvent: the event to trigger the the action listener
	 * @return null
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	 @Override
	public void actionPerformed(ActionEvent e) {
		 // receive email
		 if( e.getSource() == sendButton){
			 final SendMail s = new SendMail();
			 System.out.println("send"+ s.isRegister());
			
			
			 if(receiverT.getText().length() == 0)
			{
				JOptionPane.showMessageDialog(null,"You haven't set an receiver!");
			}
			else{
				if(subjectT.getText().length() == 0)
				{
					int res = JOptionPane.showConfirmDialog (null, "This email has no subject. "
							+ "Do you want to send it as well?", "Remind", JOptionPane.OK_CANCEL_OPTION);
				    if( res == JOptionPane.CANCEL_OPTION)
				    	return;
				}
				Thread t = new Thread(){
					public void run(){
						try {
							s.send();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				t.start();
			}
		 }
		 else if ( e.getSource() == saveButton){
			 System.out.println("save");
			 Thread t = new Thread(){
					@Override
					public void run() {
			 try {
				
				ReceiveMail r = new ReceiveMail();
				r.saveAnEmail();
			} catch (MessagingException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
				}} ;
			t.start();
			 
		 }
		 else if ( e.getSource()== cancelButton){
			 System.out.println("cancel");
			 int res = JOptionPane.showConfirmDialog(null, "Are you sure to abandon the email?","Remind", JOptionPane.OK_CANCEL_OPTION);
			 if( res == JOptionPane.CANCEL_OPTION)
			    	return;
			 subjectT.setText("");
			 ccT.setText("");
			 attachT.setText("");
			 mainText.setText("");
			 
		 }
		 else if ( e.getSource() == attachButton){
			 
			 System.out.println("att");
			 JFileChooser chooser = new JFileChooser();
			 chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			 int returnVal = chooser.showOpenDialog(totalGUI);
			 
			 if(returnVal == JFileChooser.APPROVE_OPTION){
				 File file = chooser.getSelectedFile();
				 attachT.setText(attachT.getText() + file.getName() + ";");
				 attachments.add(file);
			 }
		 }
		 else if ( e.getSource() == deleteButton1){
			 // delete an email in the list
			 
			 System.out.println("delte an email");
			 try {
				final ReceiveMail r = new ReceiveMail();
				int option = JOptionPane.showConfirmDialog(null, "Are you sure to delete this email?","Remind",JOptionPane.OK_CANCEL_OPTION);
				if(option == JOptionPane.CANCEL_OPTION)
					return;
				Thread t = new Thread(){
					public void run(){
						try {
							final boolean deletedFlag = r.deleteAnEmail(boxesNames[currentBoxIndex]);					
							if(deletedFlag){
								JOptionPane.showMessageDialog(null, "Successful delete the email!");
								SwingUtilities.invokeLater(new Runnable(){
									 public void run(){
										 inboxModel.deleteMessage(rowForBox);
										 inboxModel.fireTableDataChanged();
										 SwingUtilities.invokeLater(new Runnable(){
												@Override
												public void run() {
													reminderLabel.setText("");
													 contentText.setText("");
												}
											 });
										 
									 }
								});
								
					        }
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				t.start();
				
				
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Fail to delete the email!");
				e1.printStackTrace();
			}
			 
			 
		 }
		 else if(e.getSource() == aboutItem){
			 JOptionPane.showMessageDialog(null, "This is a GMail client.\n"
			 		+ "1. add multi-account and select from different account.\n"
			 		+ "2. receive emails from different boxes.\n"
			 		+ "3. check the email and save the attachment.\n"
			 		+ "4. save the email as draft or send the email with attachment.\n");
		 }
		 else
			try {
				// add an account
				if ( e.getSource() == addItem){
					 System.out.println("add an account");
					 
					 JTextField un = new JTextField();
					 JTextField pw = new JPasswordField();
					 Object[] message = {
					     "Username:", un,
					     "Password:", pw
					 };

					 int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
					 if(option == JOptionPane.OK_OPTION){
						 try {
							if(client.AddUsernameAndPassword(un.getText(), pw.getText())){							
								JRadioButtonMenuItem newAccount = new JRadioButtonMenuItem(un.getText());
								newAccount.setName(un.getText());
								menu1.insert(newAccount, client.accounts.size()+1);
								group.add(newAccount);
								radioItems.add(newAccount);
								newAccount.addActionListener(this);
							}
						} catch (MessagingException e1) {
							e1.printStackTrace();
						}
					 }
				 }
				
				 // select an box
				 else if( e.getSource() == foldersBox){
					 
					 JComboBox jcb = (JComboBox)e.getSource();
					 currentBoxIndex = jcb.getSelectedIndex();
					 SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run() {
								reminderLabel.setText("Loading " + boxesNames[currentBoxIndex] + " emails...");
								contentText.setText("");
							}
						 });
						try {
							Runnable worker = new Runnable(){
								@Override
								public void run() {
									try {
										 ReceiveMail.receiveEmailSummary(boxesNames[currentBoxIndex]);
										 System.out.println("summary size:"+ReceiveMail.Summary.size());
										 inboxModel.AddManyNews(ReceiveMail.Summary);
										 inboxModel.fireTableDataChanged();
									
									} catch (MessagingException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							 };
							 // only allow one refresh one time
							 refreshExecutor.execute(worker);
							 SwingUtilities.invokeLater(new Runnable(){
								 public void run(){
									 contentText.setText("");
								 }});

						} catch (Exception e2) {
							e2.printStackTrace();	
						}
					 
					 
					 System.out.println(currentBoxIndex);
				 }
				 // select an account
				 else if ( e.getSource() instanceof JRadioButtonMenuItem ){ 
					 JRadioButtonMenuItem jbmi = (JRadioButtonMenuItem)e.getSource();
					 selectedAccount = jbmi.getName();
					 int selectedIndex = client.lookUpIndex(selectedAccount);
					 System.out.println(selectedIndex + "selectedAccount");
					 client.SetCurrentAccount(selectedIndex);
				 }
				 else if (e.getSource() == addContact){
					 JTextField name = new JTextField();
					 JTextField email = new JTextField();
					 Object[] contactMessage = {
					     "name:", name,
					     "email:", email
					 };

					 int option = JOptionPane.showConfirmDialog(null, contactMessage, "Add Contact", JOptionPane.OK_CANCEL_OPTION);
					 if(option == JOptionPane.OK_OPTION){
						String group = (String)JOptionPane.showInputDialog(
			                    null,
			                    "please choose the group for the contact",
			                    "Choose a group for the contact",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    groupsNames,
			                    "");
						contactModel.addContact(new Contact(name.getText(), email.getText(),group));
						contactModel.fireTableDataChanged();
						 
					 }

				 }
				 else if (e.getSource() == deleteContact){
					int option = JOptionPane.showConfirmDialog(null, "Are you sure to delete this contact?","Remind",JOptionPane.OK_CANCEL_OPTION);
					if(rowForContact == -1 || option == JOptionPane.CANCEL_OPTION)
						return;
					 contactModel.deleteContactByRow(rowForContact);
					 contactModel.fireTableDataChanged();
				 }
				 else if (e.getSource() == groupsListBox){
					 JComboBox jcb = (JComboBox)e.getSource();
					 currentGroupName = groupsNames[jcb.getSelectedIndex()];
					
					 
					 contactModel.searchContactByRow(currentGroupName);
					 contactModel.fireTableDataChanged();
				 }
				 else if( e.getSource() == searchContact ){
					 JTextField name = new JTextField();
					 String columns[] = {"name","email"};
					 String searchBy = (String)JOptionPane.showInputDialog(
		                    null,
		                    "please choose what you want to search",
		                    "Choose a class for the search",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    columns,
		                    "");
					 if(searchBy == "name"){
						String inputName = JOptionPane.showInputDialog("name");
						ArrayList<Contact> result = contactModel.searchContactByName(inputName);
						if(result==null)
						{
							JOptionPane.showConfirmDialog(null,"no result!",
									"result",
									JOptionPane.OK_OPTION);
						}
						else{
							String r ="";
							for(Contact c : result)
								r += "name:"+c.getName()
									+" email:"+c.getEmail()
									+" group:"+c.getGroup()
									+"\n";
							JOptionPane.showConfirmDialog(null,
									r,
									"reesult",
									JOptionPane.OK_OPTION);
						}
					 }
					 else if (searchBy == "email"){
						String inputEmail = JOptionPane.showInputDialog("email");
						ArrayList<Contact> result =contactModel.searchContactByEmail(inputEmail);
						if(result==null)
						{
							JOptionPane.showConfirmDialog(null,"no result!",
									"result",
									JOptionPane.OK_OPTION);
						}
						else{
							String r2 ="";
							for(Contact c : result)
								r2 += "name:"+c.getName()
									+" email:"+c.getEmail()
									+" group:"+c.getGroup()
									+"\n";
							JOptionPane.showConfirmDialog(null,
									r2,
									"result",
									JOptionPane.OK_OPTION);
						}
					 }
					
				 }
				 
				
				
			} catch (HeadlessException e1) {
				e1.printStackTrace();
			}
		 
		
	}
	 //contact group 
	 String groupsNames[]={"All","friend","family","colleges"};
	 static String currentGroupName="All";
	 // the component for the group
	 JComboBox groupsListBox;

	/*
	 * create and show GUI, called by GUI thread
	 * @param null
	 * @return null
	 * 
	 */
	public static void createAndShowGUI() throws Exception{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("My Gmail Box");
		client = new Client();

		//Create and set up the content pane.
		MyGmailBox mgb = new MyGmailBox();
		frame.setContentPane(mgb.createContentPane());
		frame.setJMenuBar(mgb.createMenuBar());
		try {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.setSize(1280,800);
		frame.setVisible(true);

	}
	
	/*
	 * this is to create the content pane to the main frame
	 * it concludes reminderLabel and tab pane
	 */
	static JLabel reminderLabel ;
	public JPanel createContentPane() throws Exception{
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		MyGmailBox mgb = new MyGmailBox();
		reminderLabel =  new JLabel("Loading INBOX emails...");
		reminderLabel.setSize(1230,30);
		contentPane.add(reminderLabel,BorderLayout.PAGE_START);
		contentPane.add(mgb.createTabbedPane(),BorderLayout.PAGE_END);
			
		return contentPane;
    }
	
	JMenuBar menuBar;
	JMenu menu1, menu2;
	JMenuItem addItem, aboutItem;
	JRadioButtonMenuItem rdItem1, rdItem2, rdItem3;
	static Client client;
	ButtonGroup group;
	private JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		menu1 = new JMenu("Gmail account");
		addItem = new JMenuItem("add an account");
		addItem.addActionListener( this);
		menuBar.add(menu1);
		menu1.add(addItem);
		radioItems = new ArrayList<JRadioButtonMenuItem> ();
		
		menu1.addSeparator();
	    
		menu1.add(new JMenuItem("email account(s) list:"));
		group = new ButtonGroup();
		
		
		for( Account a: client.accounts){
			JRadioButtonMenuItem accItem = new JRadioButtonMenuItem(a.getUsername());
			accItem.setSelected(true);
			accItem.setName(a.getUsername());
			radioItems.add(accItem);
			menu1.add(accItem);
			group.add(accItem);
			accItem.addActionListener(this);
		}
		
		
		menu2 = new JMenu("Help");
		menuBar.add(menu2);
		
		aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(this);
		menu2.add(aboutItem);
		
		
		return menuBar;
	}

	/*
	 * main function
	 */
	public static void main(String[] args){

		/*
		 * create an GUI thread
		 */
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
   					createAndShowGUI();
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
		 BackGround b = new BackGround();
		 b.start();

	}

	/*
	 * table model change function
	 * leave it empty
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
			
	}
	/*
	 * 
	 */
	int rowForBox = 0;
	
	class inboxListSelectionHandler implements ListSelectionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			//create a thread for listen to list selection change
			Runnable worker = 
			new Runnable()
			{ 
				public void run(){
					if (! e.getValueIsAdjusting())
					{
						ListSelectionModel lsm = (ListSelectionModel)e.getSource();
						lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						rowForBox = lsm.getMinSelectionIndex();
						try {
							inboxModel.getNewsfromRow(rowForBox);
							inboxModel.setReaded(rowForBox);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					    if(rowForBox>=0)
					    	//create a thread for get news from row 
							try {
								SwingUtilities.invokeLater(new Runnable(){

									@Override
									public void run() {

										contentText.setEditable(false);
										contentText.setContentType("text/html");
										contentText.setEditorKit(new HTMLEditorKit());
										System.out.println("isText = " + ReceiveMail.isText);
										
										String dir = System.getProperty("user.dir");
										System.out.println(dir);
										
										File tmp = new File("fnew.html");
										
										System.out.print("fnew:" + tmp.length());
										if( tmp.length() != 0)
											try {
												contentText.setPage("file://"+ dir +"/fnew.html");
											} catch (IOException e) {
												e.printStackTrace();
											}
										else
										{
											contentText.setContentType("text/plain");
											if(ReceiveMail.mainBody.equals(""))
												contentText.setText(ReceiveMail.attachment + "\n");
											else
												contentText.setText(ReceiveMail.attachment + "\n"
																	+"============================================================================="+
																	"\n" + ReceiveMail.mainBody);
										}	
									}
									
								
								});
								
								
							} catch (Exception e1) {
								e1.printStackTrace();
							}
					}
			    }
			};
			listSelectorExecutor.execute(worker);
				

		}
		
		
	}
	int rowForContact;
	class contactListSelectionHandler implements ListSelectionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (! e.getValueIsAdjusting()){
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowForContact = lsm.getMinSelectionIndex();
			System.out.println("you select" + rowForContact);
			}
		}
	
	}
}

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
 *  BackGround.java   
 */

import javax.swing.SwingUtilities;

/*
 * this is back ground thread receiving email
 */

public class BackGround extends Thread{

	BackGround(){}
	
	void detectEmail() throws Exception{
		while(true){
	
			if(MyGmailBox.inboxModel != null && MyGmailBox.inboxModel.getRowCount() != 0 && Client.isRegister)
			{
				if(!ReceiveMail.detectNewMails())
				{
						SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run() {
								MyGmailBox.reminderLabel.setText("No. of unread messages:" + MyGmailBox.inboxModel.unreadMessages());
								
							}
						});
						
				}
				
			}
			sleep(3000);
		}
	}
	

	public void run(){
		try {
			detectEmail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

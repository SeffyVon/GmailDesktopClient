/* 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * All rights preserved.                                               *
 * Yingjing Feng (SID:1374873) from University of Birmingham           *
 * GMail client (version : 1.0,  update date:2013.11.14)               *
 * This is the GUI implementation of the program                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 */

/*
 *  MyTableModel.java   
 */


package MyGMail;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {
	
	private String boxName;
    private static ArrayList<News> letters;
    
    MyTableModel(){
        letters = new ArrayList<News>();
    }
    
    public String getBoxName (){
    	return boxName;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return letters.size();
    }

    public String getColumnName(int col) {
    	return "";
    }

    /*
     * get the value at the table 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
    	
    	News thisNew = letters.get(row);
    	// if the email is already read
    	if( thisNew.seen ){
	    	if (col == 0)
	    		return thisNew.getAddress();
	    	else 
	    		return thisNew.getSubject();
    	}
    	// the email is not read
    	else{
	    	if (col == 0)
	    		return "<html><b>"+thisNew.getAddress()+"</b></html>";
	    	else 
	    		return "<html><b> [Unread]"+ thisNew.getSubject()+"</b></html>";
    	}
    	
    }
    
    public void AddNews(News n){
    	letters.add(n);
    }
    
    /*
     * at a list of news at a time
     * @param ArrayList<News> the list of news
     * @return null
     */
    
    
    public void AddManyNews(ArrayList<News> nm){
    	letters = nm;
    //	letters.addAll(0,nm);
    }
    public void AddUnreceivedNews(ArrayList<News> nm){
    	letters.addAll(0,nm);
    }
    
    /*
     * get a specific new from the row 
     * @param the index of row
     * @return the JEditorPane mainText will show the specific email 
     */
    public void getNewsfromRow(int row) throws Exception{
    	ReceiveMail r = new ReceiveMail();
    	if(row != -1  && row<letters.size())
    	r.getEmailFromIDAndSave(letters.get(row).getID(), MyGmailBox.boxesNames[MyGmailBox.currentBoxIndex]);
    }
    
	void setReaded(int row){
		if(row != -1 )
		{
			System.out.println("row"+row);
			if(row<letters.size()){
				News n = letters.get(row);
				n.setSeen(true);
				letters.remove(row);
				letters.add(row, n);
			}
		}
	}

    void deleteMessage(int row) {
		if(row != -1 && row<letters.size())
		{
			System.out.println("row!!!!" + row);
			letters.remove(row);
		}
			
	}
    
    int unreadMessages(){

		System.out.println("[ls]"+letters.size());
    	int count=0;
    	for(News n : letters)
    		if(n.seen == false)
    		{
    			System.out.println("[ur]"+n.getSubject());
    			count++;
    		}
    			
    	return count;
    }
    
}

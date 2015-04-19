
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;



public class threaded extends swingmain implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected  Socket s1= null;
	protected  InputStream is = null;
	protected  DataInputStream dis = null;
	protected  int nn;
	protected  byte[] buf = new byte[1024*1024*6];
	protected  JButton acpt = new JButton("Accept");
	protected  JButton canc = new JButton("cancel");
	protected  JPanel j1 = new JPanel();
	protected  JPanel j2 = new JPanel();
	protected JTextArea filN ;
	private  String fst;
	private  String Fname;
	private  String PrtName;
	
	//Receiving The New Socket By accept method
	//=========================================
	public threaded(Socket sx){
		s1=sx;	
		j1.setBackground(Color.WHITE);
		j2.setBackground(Color.WHITE);
	}
	public void run(){
		try{
			s1.setReceiveBufferSize(1024*1024*6);
			
			//Defining Receiving Streams
			//==========================
				is = new BufferedInputStream(s1.getInputStream());
				dis = new DataInputStream(is);
				fst = dis.readUTF();
				String Key = fst.substring(0, 1);
				fst = fst.substring(1);
				switch (Key){
				//Case if Text Received
				//=====================
				case "T":
					//history.append(fst);
					JTextArea f = new JTextArea();
					f.setText(fst);
					f.setPreferredSize(new Dimension(240,15));
					f.setEditable(false);
					window.add(f);
					window.updateUI();
					jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
					dis.close();
					s1.close();
					break;
				//Case if Control Acknowledgment Received
				//=======================================
				case "C":
					//history.append(fst);
					JTextArea fx = new JTextArea();
					fx.setText(fst);
					fx.setPreferredSize(new Dimension(240,15));
					fx.setEditable(false);
					window.add(fx);
					window.updateUI();
					jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
					dis.close();
					s1.close();
					break;
					//Case if File Received
					//=====================
				case "F":	
					//Extracting File Parameters From StringUTF
					//=========================================
					filN = new JTextArea();
					PrtName = fst.substring(0, fst.indexOf('#'));
					fst = fst.substring(fst.indexOf('#'));
					fst = fst.substring(1);
					Fname = fst.substring(0, fst.indexOf('#'));
					fst = fst.substring(fst.indexOf('#'));
					fst = fst.substring(1);
					filN.setText(PrtName+" : Receive File \n "+Fname);
					filN.setPreferredSize(new Dimension(245,30));
					filN.setEditable(false);
					//Action Listener For Accept Button
					//=================================
					acpt.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							if(e.getSource() == acpt){
								accepted();
							}
						}
					});
					//Action Listener For Cancel Button
					//=================================
					canc.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							if(e.getSource() == canc){
								canceled();
							}
						}
					});
					
					//Adding Objects to Window
					//========================
					j1.add(filN);
					j2.add(acpt);
					j2.add(canc);
					window.add(j1);
					window.add(j2);
					window.updateUI();
					jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());			
					break;
				default:
						break;
				}

	}
	catch(Exception e){
		System.out.println(e);
		//history.append(e+"gggg");
	}
}	

//Accept Event method
//===================
private void accepted(){
		 receiveFile();
	 }

//Cancel Event method
//===================
private  void canceled(){
	String declFile = filN.getText(); 
	 	//Removing Button Objects
		//=======================
		 window.remove(j1);
		 window.remove(j2);
		 //to remove dangling pointers
		 acpt = new JButton("Accept");	
		 canc = new JButton("cancel");
		 j1= new JPanel();
		 j2= new JPanel();
		 filN = new JTextArea();
		 //Acknowledging File Decline
		 //==========================
			JTextArea declText = new JTextArea(declFile+"\n"+name+" : File Request Declined");
			declText.setEditable(false);
			window.add(declText);
			window.updateUI();
			jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
			sendCntrl("Declined the Offer ",PrtName);
		 try{
			dis.close();
			s1.close();}catch(Exception ee){}
	 }

//Receive File Method
//===================
public void receiveFile(){
	try{
		//Creating File Chooser Object
		//============================
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(null);
		fc.setSelectedFile(new File(Fname));
		
		if(fc.showSaveDialog(sm) == JFileChooser.APPROVE_OPTION){
			 window.remove(j1);
			 window.remove(j2);
			 //to remove dangling pointers
			 acpt = new JButton("Accept");	
			 canc = new JButton("cancel");
			 j1= new JPanel();
			 j2= new JPanel();
			 filN = new JTextArea();
			 window.updateUI();
			 jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
		
			//Getting Selected File
			//=====================
			 File fd = fc.getSelectedFile();				
			 if(fd.exists())		
				 fd.delete();//Temporarily no checking...	
			 fd.createNewFile();
		
		//Defining File Write Stream
		//==========================
		OutputStream dos = new BufferedOutputStream(new FileOutputStream(fd));
		double sum = 0;
		DecimalFormat df = new DecimalFormat("#.##");
		
		//Writing to File
		//===============
		while((nn = dis.read(buf)) != -1){
			dos.write(buf, 0, nn);
			sum = sum +nn;
			System.out.flush();
		}
		
		//Acknowledging File Receive
		//==========================
		sum = sum/1000000;
		JTextArea filN = new JTextArea(PrtName+" : Receiving "+Fname+"\n"+String.valueOf(df.format(sum))+"MB File Received");
		filN.setEditable(false);
		window.add(filN);
		window.updateUI();
		jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
		sendCntrl("File "+Fname+" Sent",PrtName);
		dos.close();
		try{
			dis.close();
			s1.close();}catch(Exception ee){}
		}

	}
	catch(Exception ex)
	{
		 //history.append(ex+"ff");
	}
  }
}

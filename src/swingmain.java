
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
public class swingmain extends JFrame {
	/**
	 * @author Sourav Kumar Dhar.
	 */
	private static final long serialVersionUID = 1L;
	protected static String name;
	//protected static JTextArea history;
	protected static JButton btn;
	protected static JButton btnF;
	protected static JTextField tf;
	protected static JPanel window;
	protected static JTextField prt;
	protected static JTextField ip;
	protected static JScrollPane jp;
	protected static swingmain sm;
	static File selF;
	protected static int nn;
	protected static byte[] buf = new byte[1024*1024*6];

	public static void main(String[] arg){
		
		name=arg[0].toString();
		JLabel lb = new JLabel("Welcome "+name);
		//history = new JTextArea();
		//history.setPreferredSize(new Dimension(250,180));
		//history.setEditable(false);
		
		//Text Field for IP
		//==================
		ip = new JTextField(10);
		ip.setText("127.0.0.1");
		ip.setPreferredSize(new Dimension(250,28));
		
		//Text Field for Message
		//======================
		tf = new JTextField(18);
		tf.setPreferredSize(new Dimension(250,28));
		tf.requestFocusInWindow();
		tf.addKeyListener(new KeyAdapter(){
                    @Override
			public void  keyReleased(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					swingmain.btn.doClick();
				}
			}
		});
		
		
		//Text Field for Port
		//======================
		prt = new JTextField(5);
		prt.setPreferredSize(new Dimension(250,28));
		
		//Send Button
		//===========
		btn = new JButton("send");
		
		//Receive Button
		//==============
		btnF = new JButton("send file");

		//Window for Message Receiving
		//============================
		window = new JPanel();
		BoxLayout bl = new BoxLayout(window, BoxLayout.Y_AXIS);
		window.setLayout(bl);

		//Panel with scroll as ViewPort
		//=============================
		jp = new JScrollPane(window);
		jp.setPreferredSize(new Dimension(260,150));
		jp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		//First Row of Controls
		//=====================
		JPanel pnl1 = new JPanel();
		pnl1.add(ip);
		pnl1.add(prt);
		pnl1.add(btnF);
		
		//Second Row of Controls
		//=====================
		JPanel pnl2 = new JPanel();
		pnl2.add(tf);
		pnl2.add(btn);
		
		
		//Creating The Window Frame by Creating child class of JFrame
		//===========================================================
		swingmain sm = new swingmain();
		sm.add(lb,BorderLayout.NORTH);
		//sm.add(history,BorderLayout.CENTER);
		sm.add(jp,BorderLayout.CENTER);
		sm.add(pnl1, BorderLayout.SOUTH);
		sm.add(pnl2, BorderLayout.SOUTH);
		sm.setVisible(true);
		sm.setLayout(new FlowLayout());
		sm.setDefaultCloseOperation(EXIT_ON_CLOSE);
		sm.setSize(300,320);
		sm.setTitle("The Port "+name);

		//Action Listener For Send Button
		//===============================
		btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(e.getSource() == btn){
					if(!prt.getText().equals("")){
						sendTxt(tf.getText());
						tf.setText("");
					}
					else{
						JOptionPane.showMessageDialog(null, "Please Enter The Receiver Port");
					}
				}
			}
		});
		
		//Action Listener For Send File Button
		//====================================
		btnF.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(e.getSource() == btnF){
					if(!prt.getText().equals("")){
						sendFile();
					}
					else{
						JOptionPane.showMessageDialog(null,"Please Enter The Receiver Port for File Sending");
					}
				}
			}
		});
		
		//Creating Server On given Port
		//=============================
		try{
		ServerSocket sc = new ServerSocket(Integer.parseInt(name));
		//history.append("chat server is on \n");
		JTextArea t = new JTextArea();
		t.setText("Chat Server ON");
		t.setEditable(false);
		window.add(t);
		window.updateUI();
		while(true){
			//Creating Thread Object For Multi Server
			//=======================================
				threaded td = new threaded(sc.accept());
				Thread th = new Thread(td);
				th.start();
			}
		}
		catch(Exception e){
			System.out.println(e);
			//history.append(e+"gggg");
		}
	}


	//Method For Sending Text Message
	//===============================
	public static void sendTxt(String Text){
		try{
		Socket s = new Socket(InetAddress.getByName(ip.getText()),Integer.parseInt(prt.getText()));
		
		//Defining Socket Write Streams
		//=============================
		OutputStream os = new BufferedOutputStream(s.getOutputStream());
		DataOutputStream dos = new DataOutputStream(os);
		
		//Writing To output Streams
		//=========================
		dos.writeUTF("T"+name+" :  "+ Text+ "\n");
		dos.close();
		//history.append(name+" :  "+ Text+ "\n");
		JTextArea f = new JTextArea();
		f.setText(name+" :  "+ Text+ "\n");
		f.setPreferredSize(new Dimension(240,15));
		f.setEditable(false);
		window.add(f);
		window.updateUI();
		jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
		s.close();
		}
		catch(Exception e1){
			System.out.println(e1);
			//history.append(e1+"\nggg");
		}
	}
	
	//Method For Sending Control Acknowledgment
	//=========================================
	public static void sendCntrl(String Text,String Port){
		try{
		Socket s = new Socket(InetAddress.getByName(ip.getText()),Integer.parseInt(Port));
		
		OutputStream os = new BufferedOutputStream(s.getOutputStream());
		
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeUTF("C"+name+" :  "+ Text+ "\n");
		dos.close();
		s.close();
		}
		catch(Exception e1){
			System.out.println(e1);
			//history.append(e1+"\nggg");
		}
	}

	//Method For Sending File
	//=======================
	public static void sendFile(){
		try{
			int n;
			Socket s= null;
			InputStream fis=null;
			OutputStream fos = null;
			byte[] buffer = new byte[1024*1024*6];
			JTextArea f1 = new JTextArea();
			
			//Creating File Chooser Object
			//============================
			JFileChooser fc = new JFileChooser();
			if(fc.showOpenDialog(sm) == JFileChooser.APPROVE_OPTION){	
			selF = fc.getSelectedFile();
			long j = selF.length();
			//history.append(selF.getName().toString()+"\n");
			
			//Display on Sender Window
			//========================
			f1.setText(name +" : Sending File \n"+selF.getName().toString());
			f1.setPreferredSize(new Dimension(240,30));
			f1.setEditable(false);
			window.add(f1);
			window.updateUI();
			jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
			

			//Connect To Server with the Socket
			//=================================
			s = new Socket(InetAddress.getByName(ip.getText()),Integer.parseInt(prt.getText()));
			s.setSendBufferSize(1024*1024*6);	
			
			//Opening Required Streams
			//========================
			fis = new BufferedInputStream(new FileInputStream(selF));
			fos = new BufferedOutputStream(s.getOutputStream());
			DataOutputStream dos = new DataOutputStream(fos);
			
			//Writing To Stream
			//=================
			dos.writeUTF("F"+name+"#"+selF.getName().toString()+"#"+String.valueOf(j)+"#");
				while((n=fis.read(buffer)) != -1){
					fos.write(buffer, 0, n);
					System.out.flush();
				}
			//Closing Streams
			//===============
			fis.close();
			fos.close();
			s.close();
			}
			else{
				window.remove(f1);
				window.updateUI();
				jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
			}
			}
			catch(IOException ioe){
				//history.append(ioe + "\nghghgh");
			}
	}
}







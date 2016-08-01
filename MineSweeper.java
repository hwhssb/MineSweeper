

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

public class MineSweeper  extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private class MineTimerTask extends TimerTask{
		public void run(){
			if(ts == TimerStatus.RUNNING){
				if(gameTime < 999){
					gameTime++;
					r_left.setText(String.format("%03d", gameTime));
				}
			}
		}
	}

	
	private class TempJLabel extends JLabel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private boolean mine = false;
		private boolean flag = false;
		private boolean cross = false;
		
		public void paint(Graphics g) {
			super.paint(g);
			
			//enhance the quality
			Graphics2D g2D = (Graphics2D) g;
	        RenderingHints qualityHints = new RenderingHints(
	                                          RenderingHints.KEY_ANTIALIASING,
	                                          RenderingHints.VALUE_ANTIALIAS_ON);
	        qualityHints.put(RenderingHints.KEY_RENDERING,
	                        RenderingHints.VALUE_RENDER_QUALITY);
	        g2D.setRenderingHints(qualityHints);

			if(mine){
				int t=this.getWidth()/2;
				g2D.setColor(Color.DARK_GRAY.darker());
				g2D.fillOval(this.getWidth()/2-t/2,this.getHeight()/2-t/2,t,t);
				g2D.drawLine(this.getWidth()/2-t/2,this.getHeight()/2-t/2,this.getWidth()/2+t/2,this.getHeight()/2+t/2);
				g2D.drawLine(this.getWidth()/2+t/2,this.getHeight()/2-t/2,this.getWidth()/2-t/2,this.getHeight()/2+t/2);
				g2D.drawLine(this.getWidth()/2-t/2-2,this.getHeight()/2,this.getWidth()/2+t/2+2,this.getHeight()/2);
				g2D.drawLine(this.getWidth()/2,this.getHeight()/2-t/2-2,this.getWidth()/2,this.getHeight()/2+t/2+2);
			}
			if(flag){
				int t=this.getWidth()/6;
				int x[] = {this.getWidth()/2+1, this.getWidth()/2+1, t};
		        int y[] = {t, this.getHeight()/2, this.getHeight()/3};
				g2D.setColor(Color.RED);
		        g2D.fillPolygon(x, y, 3);
				g2D.setColor(Color.BLACK);
				g2D.setStroke(new BasicStroke(2.0f));
				g2D.drawLine(t+2,this.getHeight()-t-1,this.getWidth()-t-2,this.getHeight()-t-1);
				g2D.setStroke(new BasicStroke(1.0f));
				g2D.drawLine(this.getWidth()/2,this.getHeight()/2,this.getWidth()/2,this.getHeight()-t-1);
			}
			if(cross){
				g2D.setColor(Color.RED);
				g2D.drawLine(3,3,this.getWidth()-3,this.getHeight()-3);
				g2D.drawLine(this.getWidth()-3,3,3,this.getHeight()-3);
			}
		}
		
		public void drawMine() {
			mine = true;
			repaint();
		}
		
		public void drawFlag() {
			flag = true;
			repaint();
		}
		
		public void drawCross() {
			cross = true;
			repaint();
		}
		
		public void clear() {
			mine = false;
			flag = false;
			cross = false;
			repaint();
		}
	}
	
	
	

	private static final int mineButtonWidth = 18;
	private static final String normalFace = "`_`";
	private static final String winFace = "^¦Ï^";
	private static final String loseFace = ">_<";
	private static final String pressFace = "`o`";
	private static final Color questionMarkColor = Color.BLACK;
	private static final Color[] numberColor = {
		null, 
		Color.BLUE, Color.GREEN.darker(), Color.RED.brighter(), Color.BLUE.darker(),
		Color.MAGENTA, Color.CYAN.darker(), Color.MAGENTA.darker(), Color.GRAY
    };
	private static final int[][] levelAttr = {
		{9,9,10},	//9*9 fields with 10 mines
		{16,16,40},
		{30,16,99}
	};
	private static final Random rnd = new Random();
	private static final Timer timer = new Timer();	
	
	private enum TimerStatus {
        RUNNING, STOP, PAUSE;
    }
	
	private enum GridStatus {
		NORMAL, FLAG, DONE
    }
	

	private int gameLevel;	//0-Beginner,1-Intermediate,2-Expert.
	private int xnum;	//number of mines in one line
	private int ynum;	//number of mines in one column
	private int minenum;	//total number of mines 
	private int leftMineNum;
	private int steppedGridNum;
	private boolean isMine[];	//whether the grid is a mine.
	private int nunit[][];	//number of mines around the grid, -1 if the the grid is a mine.
	private int gameTime;
	private boolean mouseHold;	//whether a mouse button is held
	private TimerStatus ts;
	private GridStatus gs[][];
	private MineTimerTask mtt;
	
	//menu components
	private JMenuBar menub;
	private JMenu m_game;
	private JMenuItem newMI;
	private JMenuItem exitMI;
	private ButtonGroup levelGroup;
	private JRadioButtonMenuItem beginnerMI;
	private JRadioButtonMenuItem intermediateMI;
	private JRadioButtonMenuItem expertMI;

	//panel components and attributes
	private int windowWidth;
	private int windowHeight;
	private int centerPanelWidth;
	private int centerPanelHeight;
	private JPanel topPanel;
	private JButton faceButton;
	private JLabel l_left;	//to show leftMineNum
	private JLabel r_left;	//to show time
	private JPanel centerPanel;	//contains all the grids
	private TempJLabel gridUnit[][];

	
	//Action Listener of faceButton and newMI
	private ActionListener newAL = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			ts = TimerStatus.STOP;	//stop the game(if the game is running)
			if(mtt != null)
				mtt.cancel();
			leftMineNum = minenum;
			l_left.setText(String.format("%03d", leftMineNum));
			gameTime=0;
			r_left.setText(String.format("%03d", gameTime));
			faceButton.setText(normalFace);
			for(int i=0; i<ynum; i++){	//reset all the grids
				for(int j=0; j<xnum; j++){
					gridUnit[i][j].setText("");
					gridUnit[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
					gridUnit[i][j].setBackground(Color.lightGray);
					gridUnit[i][j].clear();
					gs[i][j] = GridStatus.NORMAL;
				}
			}
			mouseHold = false;
			steppedGridNum = 0;
		}
	};
	
	
	//mouse action of all the grids
	private MouseAdapter ml = new MouseAdapter(){	
		public void mousePressed(MouseEvent e){ 
			int[] ij = gridOfMouse();
			int i = ij[0];
			int j = ij[1];
			if(i == -1 || j == -1)
				return;
			TempJLabel g = gridUnit[i][j];

			//both left and right buttons or middle button
			if( e.getModifiersEx() == ( MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)
						|| (e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) ==  MouseEvent.BUTTON2_DOWN_MASK){
				bothButtonPressed(i,j);
				mouseHold = true;
			}
			//left button
			else if(e.getButton() == MouseEvent.BUTTON1){
				if(gs[i][j] == GridStatus.NORMAL){
					g.setBorder(BorderFactory.createLineBorder(Color.GRAY));
					mouseHold = true;
				}
			}
			//right button
			else if(e.getButton() == MouseEvent.BUTTON3){
				if(g.getText() == "?"){
					g.setText("");
				}
				else if(gs[i][j] == GridStatus.NORMAL && leftMineNum > 0){	//mark with a flag
					g.drawFlag();
					gs[i][j] = GridStatus.FLAG;
					leftMineNum--;
					l_left.setText(String.format("%03d", leftMineNum));
				}
				else if(gs[i][j] == GridStatus.FLAG){
					g.clear();
					g.setText("?"); 
					g.setForeground(questionMarkColor);
					gs[i][j] = GridStatus.NORMAL;
					leftMineNum++;	
					l_left.setText(String.format("%03d", leftMineNum));
				}
			}
		}
		
		public void mouseEntered(MouseEvent e){
			if(e.getModifiersEx() == MouseEvent.NOBUTTON)
				mouseHold = false;
			if(mouseHold == false)
				return;
			JLabel g = (JLabel)e.getSource();
			int i = (g.getY()-5)/mineButtonWidth;
			int j = (g.getX()-5)/mineButtonWidth;

			//both left and right buttons or middle button
			if( e.getModifiersEx() == ( MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)
					|| (e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) ==  MouseEvent.BUTTON2_DOWN_MASK){
				bothButtonPressed(i,j);
			}
			//left button
			else if(e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK){
				if(gs[i][j] == GridStatus.NORMAL)
					g.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			}
		}
		
		public void mouseExited(MouseEvent e){ 
			if(mouseHold == false)
				return;
			JLabel g = (JLabel)e.getSource();
			int i = (g.getY()-5)/mineButtonWidth;
			int j = (g.getX()-5)/mineButtonWidth;

			//both left and right buttons or middle button
			if( e.getModifiersEx() == ( MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)
					|| (e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) ==  MouseEvent.BUTTON2_DOWN_MASK){
				bothButtonReleased(i,j);
			}
			//left button
			else if(e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK){
				if(gs[i][j] == GridStatus.NORMAL)
					g.setBorder(BorderFactory.createRaisedBevelBorder());
			}
		}
		
		public void mouseReleased(MouseEvent e){
			if(mouseHold == false)
				return;
			int[] ij = gridOfMouse();
			int i = ij[0];
			int j = ij[1];
			if(i == -1 || j == -1)
				return;
			JLabel g = gridUnit[i][j];

			//if one of left and right buttons is released and the other is still hold, or middle button is released, 
			if(e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() ==  MouseEvent.BUTTON3_DOWN_MASK
					|| e.getButton() == MouseEvent.BUTTON3 && e.getModifiersEx() ==  MouseEvent.BUTTON1_DOWN_MASK
					||e.getButton() == MouseEvent.BUTTON2 && e.getModifiersEx() !=  (MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)){
				if(ts != TimerStatus.STOP && gs[i][j] == GridStatus.DONE){
					//count the flags in the 8 grids around the current grid
					int mcount = 0;
					if(i-1>=0 && j-1>=0 && gs[i-1][j-1] == GridStatus.FLAG)
						mcount++;
					if(j-1>=0 && gs[i][j-1] == GridStatus.FLAG)
						mcount++;
					if(i+1<ynum && j-1>=0 && gs[i+1][j-1] == GridStatus.FLAG)
						mcount++;
					if(i-1>=0 && j+1<xnum && gs[i-1][j+1] == GridStatus.FLAG)
						mcount++;
					if(j+1<xnum && gs[i][j+1] == GridStatus.FLAG)
						mcount++;
					if(i+1<ynum && j+1<xnum && gs[i+1][j+1] == GridStatus.FLAG)
						mcount++;
					if(i-1>=0 && gs[i-1][j] == GridStatus.FLAG)
						mcount++;
					if(i+1<ynum && gs[i+1][j] == GridStatus.FLAG)
						mcount++;
					
					if(nunit[i][j] == mcount){
						if(i-1>=0 && j-1>=0 && gs[i-1][j-1] == GridStatus.NORMAL)
							stepOn(i-1,j-1);
						if(j-1>=0 && gs[i][j-1] == GridStatus.NORMAL)
							stepOn(i,j-1);
						if(i+1<ynum && j-1>=0 && gs[i+1][j-1] == GridStatus.NORMAL)
							stepOn(i+1,j-1);
						if(i-1>=0 && j+1<xnum && gs[i-1][j+1] == GridStatus.NORMAL)
							stepOn(i-1,j+1);
						if(j+1<xnum && gs[i][j+1] == GridStatus.NORMAL)
							stepOn(i,j+1);
						if(i+1<ynum && j+1<xnum && gs[i+1][j+1] == GridStatus.NORMAL)
							stepOn(i+1,j+1);
						if(i-1>=0 && gs[i-1][j] == GridStatus.NORMAL)
							stepOn(i-1,j);
						if(i+1<ynum && gs[i+1][j] == GridStatus.NORMAL)
							stepOn(i+1,j);
					}
					else
						bothButtonReleased(i,j);
				}
				else
					bothButtonReleased(i,j);
				
				//after the action, disable mouseHold.
				mouseHold = false;
			}
			//left button
			else if(e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() ==  MouseEvent.NOBUTTON){
				if(gs[i][j] == GridStatus.NORMAL){
					g.setBorder(BorderFactory.createRaisedBevelBorder());
					stepOn(i,j);
				}
				mouseHold = false;
			}
		}
	};
	

	public MineSweeper(){
		gameLevel = 2;	//default level is expert
		
		//set menu
		menub=new JMenuBar();
		m_game=new JMenu("Game");
		newMI = new JMenuItem("New");
		exitMI = new JMenuItem("Exit");
		levelGroup= new ButtonGroup();
		beginnerMI = new JRadioButtonMenuItem("Beginner");
		intermediateMI = new JRadioButtonMenuItem("Intermediate");
		expertMI = new JRadioButtonMenuItem("Expert");
		if(gameLevel==0)
			beginnerMI.setSelected(true);
		else if(gameLevel==1)
			intermediateMI.setSelected(true);
		else
			expertMI.setSelected(true);
		
		m_game.setMnemonic('G');
		newMI.setMnemonic('N');
		newMI.setAccelerator(KeyStroke.getKeyStroke("F2"));
		exitMI.setMnemonic('X');
		beginnerMI.setMnemonic('B');
		intermediateMI.setMnemonic('I');
		expertMI.setMnemonic('E');
		levelGroup.add(beginnerMI);
		levelGroup.add(intermediateMI);
		levelGroup.add(expertMI);
		m_game.add(newMI);
		m_game.addSeparator();
		m_game.add(beginnerMI);
		m_game.add(intermediateMI);
		m_game.add(expertMI);
		m_game.addSeparator();
		m_game.add(exitMI);
	    menub.add(m_game);
		this.setJMenuBar(menub);
		
		//menu actions
		newMI.addActionListener(newAL);
		exitMI.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		beginnerMI.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				gameLevel = 0;
				initPaint();
			}
		});
		intermediateMI.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				gameLevel = 1;
				initPaint();
			}
		});
		expertMI.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				gameLevel = 2;
				initPaint();
			}
		});
		
		
		initPaint();	

		
		//minimize action
		this.addWindowStateListener(new WindowStateListener () {
			public void windowStateChanged(WindowEvent state) {
				if(state.getNewState() == JFrame.ICONIFIED) {
					if( ts == TimerStatus.RUNNING){
						ts = TimerStatus.PAUSE;
					}
				}else if(state.getNewState() == JFrame.NORMAL) {
					if(ts == TimerStatus.PAUSE){
						ts = TimerStatus.RUNNING;
					}
				}
			}

		});
		
		//exit action
		this.addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					System.exit(0);
				}
		});
		
		//change face if press left button or middle button
		Toolkit kit = Toolkit.getDefaultToolkit();
		kit.addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				MouseEvent me = (MouseEvent)event;
				if(me.getID()==MouseEvent.MOUSE_PRESSED
						&& ((me.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK
								|| (me.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) == MouseEvent.BUTTON2_DOWN_MASK)
						&& ((MouseEvent)event).getYOnScreen() >= topPanel.getLocationOnScreen().getY()
						&& faceButton.getMousePosition() == null){
					if((ts == TimerStatus.STOP || ts == TimerStatus.RUNNING) && faceButton.getText() == normalFace){
						faceButton.setText(pressFace);
					}
                }
				else if(me.getID()==MouseEvent.MOUSE_RELEASED){
					if(faceButton.getText() == pressFace){
						faceButton.setText(normalFace);
					}
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);

		
		//set frame location
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		//put the game window in the center of the screen
		this.setLocation(screenWidth / 2 - levelAttr[gameLevel][0]*mineButtonWidth/2, screenHeight / 2 - levelAttr[gameLevel][1]*mineButtonWidth/2);
		
		this.setResizable(false);
		this.setVisible(true);
		
	}
	
	
	private void initPaint(){
		gameTime = 0;
		ts = TimerStatus.STOP;
		if(mtt != null)
			mtt.cancel();
		
		xnum = levelAttr[gameLevel][0];
		ynum = levelAttr[gameLevel][1];
		minenum = levelAttr[gameLevel][2];
		leftMineNum = minenum;
		steppedGridNum = 0;
		isMine = new boolean[ynum*xnum];
		nunit = new int[ynum][xnum];
		mouseHold = false;
		gs = new GridStatus[ynum][xnum];
		for(int i=0; i<ynum; i++){
			for(int j=0; j<xnum; j++){
				gs[i][j] = GridStatus.NORMAL;
			}
		}
		
		centerPanelWidth = xnum*mineButtonWidth+10;	//10 margin
		centerPanelHeight = ynum*mineButtonWidth+10;

		windowWidth = centerPanelWidth+15;
		windowHeight = centerPanelHeight+108;
		
		this.setTitle("Minesweeper");
		this.setSize(windowWidth,windowHeight);
		this.setLayout(null);
		this.setBackground(Color.lightGray);
		this.getContentPane().removeAll();
		this.repaint();
		
		//set topPanel
		topPanel = new JPanel(null);
		topPanel.setBackground(Color.lightGray);
		topPanel.setBounds(5,5,windowWidth-15,40);
		topPanel.setBorder(BorderFactory.createLoweredBevelBorder());

		l_left=new JLabel();
		l_left.setBounds(10,topPanel.getHeight()/2-13,40,26);
		l_left.setBorder(BorderFactory.createEtchedBorder());
		l_left.setText(String.format("%03d", leftMineNum));
		l_left.setFont(new Font("Sans Serif", Font.PLAIN, 20));
		l_left.setHorizontalAlignment(JLabel.CENTER);
		l_left.setOpaque(true);
		l_left.setBackground(Color.pink);
		l_left.setForeground(Color.red);
		topPanel.add(l_left);

		faceButton = new JButton();
		faceButton.setBounds(topPanel.getWidth()/2-13,topPanel.getHeight()/2-13,26,26);
		faceButton.setBorder(BorderFactory.createRaisedBevelBorder());
		faceButton.setBackground(Color.YELLOW);
		faceButton.setText(normalFace);
		faceButton.setFont(new Font("Sans Serif", Font.PLAIN, 10));
		faceButton.setFocusable(false);
		faceButton.addActionListener(newAL);
		topPanel.add(faceButton);

		r_left=new JLabel();
		r_left.setBounds(topPanel.getWidth()-50,topPanel.getHeight()/2-13,40,26);
		r_left.setBorder(BorderFactory.createEtchedBorder());
		r_left.setText(String.format("%03d", gameTime));
		r_left.setFont(new Font("Sans Serif", Font.PLAIN, 20));
		r_left.setHorizontalAlignment(JLabel.CENTER);
		r_left.setOpaque(true);
		r_left.setBackground(Color.pink);
		r_left.setForeground(Color.red);
		topPanel.add(r_left);
		
		this.add(topPanel);
		
		//set centerPanel
		centerPanel = new JPanel(null);
		centerPanel.setBackground(Color.GRAY);
		centerPanel.setBounds(topPanel.getX(),topPanel.getY()+topPanel.getHeight()+5,centerPanelWidth,centerPanelHeight);
		centerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		
		gridUnit = new TempJLabel[ynum][xnum];
		for(int i=0; i<ynum; i++){
			for(int j=0; j<xnum; j++){
				gridUnit[i][j] = new TempJLabel();
				gridUnit[i][j].setBounds(5+j*mineButtonWidth,5+i*mineButtonWidth,mineButtonWidth,mineButtonWidth);
				gridUnit[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
				gridUnit[i][j].setOpaque(true);
				gridUnit[i][j].setBackground(Color.lightGray);
				gridUnit[i][j].setHorizontalAlignment(JLabel.CENTER);
				gridUnit[i][j].setFont(new Font("Sans Serif", Font.BOLD, 13));
				centerPanel.add(gridUnit[i][j]);
				gridUnit[i][j].addMouseListener(ml);
			}
		}
		
		this.add(centerPanel);
	}
	

	
	private int[] gridOfMouse(){
		int[] ij = {-1,-1};
		for(int u=0; u<ynum; u++){
			for(int v=0; v<xnum; v++){
				if(gridUnit[u][v].getMousePosition() != null){
					ij[0] = u;
					ij[1] = v;
				}
			}
		}
		return ij;
	}
	

	//if both left and right buttons are pressed, process the 9 grids.
	private void bothButtonPressed(int i, int j){
		if(gs[i][j] == GridStatus.NORMAL)
			gridUnit[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(i-1>=0 && j-1>=0 && gs[i-1][j-1] == GridStatus.NORMAL)
			gridUnit[i-1][j-1].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(j-1>=0 && gs[i][j-1] == GridStatus.NORMAL)
			gridUnit[i][j-1].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(i+1<ynum && j-1>=0 && gs[i+1][j-1] == GridStatus.NORMAL)
			gridUnit[i+1][j-1].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(i-1>=0 && j+1<xnum && gs[i-1][j+1] == GridStatus.NORMAL)
			gridUnit[i-1][j+1].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(j+1<xnum && gs[i][j+1] == GridStatus.NORMAL)
			gridUnit[i][j+1].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(i+1<ynum && j+1<xnum && gs[i+1][j+1] == GridStatus.NORMAL)
			gridUnit[i+1][j+1].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(i-1>=0 && gs[i-1][j] == GridStatus.NORMAL)
			gridUnit[i-1][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(i+1<ynum && gs[i+1][j] == GridStatus.NORMAL)
			gridUnit[i+1][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
	}
	

	//if one of left and right buttons is released and the other is still hold, or middle button is released, 
	//release the 9 grids.
	private void bothButtonReleased(int i, int j){
		if(gs[i][j] == GridStatus.NORMAL)
			gridUnit[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
		if(i-1>=0 && j-1>=0 && gs[i-1][j-1] == GridStatus.NORMAL)
			gridUnit[i-1][j-1].setBorder(BorderFactory.createRaisedBevelBorder());
		if(j-1>=0 && gs[i][j-1] == GridStatus.NORMAL)
			gridUnit[i][j-1].setBorder(BorderFactory.createRaisedBevelBorder());
		if(i+1<ynum && j-1>=0 && gs[i+1][j-1] == GridStatus.NORMAL)
			gridUnit[i+1][j-1].setBorder(BorderFactory.createRaisedBevelBorder());
		if(i-1>=0 && j+1<xnum && gs[i-1][j+1] == GridStatus.NORMAL)
			gridUnit[i-1][j+1].setBorder(BorderFactory.createRaisedBevelBorder());
		if(j+1<xnum && gs[i][j+1] == GridStatus.NORMAL)
			gridUnit[i][j+1].setBorder(BorderFactory.createRaisedBevelBorder());
		if(i+1<ynum && j+1<xnum && gs[i+1][j+1] == GridStatus.NORMAL)
			gridUnit[i+1][j+1].setBorder(BorderFactory.createRaisedBevelBorder());
		if(i-1>=0 && gs[i-1][j] == GridStatus.NORMAL)
			gridUnit[i-1][j].setBorder(BorderFactory.createRaisedBevelBorder());
		if(i+1<ynum && gs[i+1][j] == GridStatus.NORMAL)
			gridUnit[i+1][j].setBorder(BorderFactory.createRaisedBevelBorder());
	}
	
	
	//step on a grid
	private void stepOn(int i, int j){
		if(ts == TimerStatus.STOP){	//if the game hasn't started, start it.
			gameStart(i,j);
		}
		if(nunit[i][j] == -1){	//the grid is a mine
			gridUnit[i][j].drawMine();
			gridUnit[i][j].setBackground(Color.RED);
			gridUnit[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
			gs[i][j] = GridStatus.DONE;
			ts = TimerStatus.STOP;
			mtt.cancel();
			faceButton.setText(loseFace);
			revealAll();
		}
		else{
			revealGrid(i,j);
			if(checkSuccess() == true){
				ts = TimerStatus.STOP;
				mtt.cancel();
				faceButton.setText(winFace);
			}
		}	
	}
	
	
	private void gameStart(int i, int j){
		generateMines(i,j);
		computeNUnit();
		ts = TimerStatus.RUNNING;
		mtt = new MineTimerTask();
		timer.scheduleAtFixedRate(mtt, 0, 1000);
	}
	
	
	//generate mines randomly, not in the grid (i,j)
	private void generateMines(int i, int j){
		//initialize
		for(int k=0; k<isMine.length; k++){
			isMine[k] = false;
		}
		
		for(int k=0; k<minenum;){
			int n = rnd.nextInt(ynum*xnum);
			if(isMine[n] == false && n != i*xnum+j){
				isMine[n] = true;
				k++;
			}
		}
	}
	
	
	//compute the number of mines around every grid
	private void computeNUnit(){
		for(int i=0; i<ynum; i++){
			for(int j=0; j<xnum; j++){
				int n=0;
				if(isMine[i*xnum+j] == true){	//if the grid is a mine, -1.
					nunit[i][j] = -1;
				}
				else{	
					//check the 8 grids around the grid
					if(i-1>=0 && j-1>=0 && isMine[(i-1)*xnum+j-1] == true)
						n++;
					if(j-1>=0 && isMine[(i)*xnum+j-1] == true)
						n++;
					if(i+1<ynum && j-1>=0 && isMine[(i+1)*xnum+j-1] == true)
						n++;
					if(i-1>=0 && j+1<xnum && isMine[(i-1)*xnum+j+1] == true)
						n++;
					if(j+1<xnum && isMine[(i)*xnum+j+1] == true)
						n++;
					if(i+1<ynum && j+1<xnum && isMine[(i+1)*xnum+j+1] == true)
						n++;
					if(i-1>=0 && isMine[(i-1)*xnum+j] == true)
						n++;
					if(i+1<ynum && isMine[(i+1)*xnum+j] == true)
						n++;
					
					nunit[i][j] = n;
				}
			}
		}
	}
	
	
	//reveal the grid(s) recursively
	//before calling this function, the grid (i,j) has been confirmed not to be a mine. 
	private void revealGrid(int i, int j){
		gs[i][j] = GridStatus.DONE;
		steppedGridNum++;
		gridUnit[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		if(nunit[i][j] != 0){	//the grid has adjacent mines
			gridUnit[i][j].setText(""+nunit[i][j]);
			gridUnit[i][j].setForeground(numberColor[nunit[i][j]]);
			return;
		}
		else{	//recursively call this function
			if(i-1>=0 && j-1>=0 && gs[i-1][j-1] == GridStatus.NORMAL)
				revealGrid(i-1,j-1);
			if(j-1>=0 && gs[i][j-1] == GridStatus.NORMAL)
				revealGrid(i,j-1);
			if(i+1<ynum && j-1>=0 && gs[i+1][j-1] == GridStatus.NORMAL)
				revealGrid(i+1,j-1);
			if(i-1>=0 && j+1<xnum && gs[i-1][j+1] == GridStatus.NORMAL)
				revealGrid(i-1,j+1);
			if(j+1<xnum && gs[i][j+1] == GridStatus.NORMAL)
				revealGrid(i,j+1);
			if(i+1<ynum && j+1<xnum && gs[i+1][j+1] == GridStatus.NORMAL)
				revealGrid(i+1,j+1);
			if(i-1>=0 && gs[i-1][j] == GridStatus.NORMAL)
				revealGrid(i-1,j);
			if(i+1<ynum && gs[i+1][j] == GridStatus.NORMAL)
				revealGrid(i+1,j);
		}
	}
	
	
	//reveal all the grids after the game is lost
	private void revealAll(){
		for(int i=0; i<ynum; i++){
			for(int j=0; j<xnum; j++){
				//if the grid hasn't been processed and the grid is a mine, show the mine.
				if(gs[i][j] == GridStatus.NORMAL && nunit[i][j] == -1){
					gridUnit[i][j].drawMine();
					gridUnit[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
				}
				//if the grid has been marked with a flag but the grid is not a mine, draw the cross.
				else if(gs[i][j] == GridStatus.FLAG && nunit[i][j] != -1){
					gridUnit[i][j].clear();
					gridUnit[i][j].drawMine();
					gridUnit[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
					gridUnit[i][j].drawCross();
				}
				gs[i][j] = GridStatus.DONE;
			}
		}
	}
	
	
	//check if the game is won
	private boolean checkSuccess(){
		if(steppedGridNum != levelAttr[gameLevel][0] * levelAttr[gameLevel][1] - levelAttr[gameLevel][2])
			return false;
		
		//if win, mark the remaining grids with flags
		for(int i=0; i<ynum; i++){
			for(int j=0; j<xnum; j++){
				if(gs[i][j] == GridStatus.NORMAL && nunit[i][j] == -1){
					gridUnit[i][j].setText("");
					gridUnit[i][j].drawFlag();
					leftMineNum--;
				}
				gs[i][j] = GridStatus.DONE;
			}
		}
		l_left.setText(String.format("%03d", leftMineNum));
		return true;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MineSweeper();
	}

}

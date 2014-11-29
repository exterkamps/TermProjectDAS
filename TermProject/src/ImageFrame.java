import java.awt.BorderLayout;
//import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
//import java.awt.Graphics2D;
//import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class ImageFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final JPanel Picframe;
	JLabel label;
	map myMap;
	boolean initializedMap = false;
	boolean running = false;
	private Timer timer;
	JPanel lowerPanel;
	JPanel leftPanel;
	int CELLSIZE;
	int stats[];
	
	//component references
	JButton playPause;
	JLabel active_bunnies;
	JLabel dead_bunnies;
	JLabel bunnies_left;
	JLabel recent_events;
	
	int active_bunnies_num = 0;
	int dead_bunnies_num   = 0;
	int bunnies_left_num   = 0;
	String recent_events_list = "Events: /n";
	
	public ImageFrame(int choice)
	{
		
		//make the new map
		int size = 0;
		if(choice == 0 ){
			size = 600;//50x50 grid of 8x8 cells
			CELLSIZE = 24;
		}else{
			size = 800;//100x100 grid of 8x8 cells
			CELLSIZE = 32;
		}
	    //set up the map
		stats = new int[4];
		myMap = new map(size, CELLSIZE,stats);
		initializedMap = true;
		Picframe = new JPanel();
		Picframe.add(label = new JLabel(new ImageIcon(myMap.getMap())));
		
		//add the menu items and panels
		addMenu();
		
		
		//f.setSize(myMap.getMap().getWidth()+45,myMap.getMap().getHeight()+lowerPanel.getHeight()+80);
		this.getContentPane().add( leftPanel, BorderLayout.WEST );
		this.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		label.addMouseListener( new MouseAdapter()
		{
			public void mousePressed( MouseEvent event )
			{
				//set the point, if it is white to green
				
				//MAKING FENCE AT RUNTIME, FOR DEBUG
				Point point = event.getPoint();
				boolean actor_click = false;
				int x = (point.x / myMap.CELLSIZE);
				int y = (point.y / myMap.CELLSIZE);
				if (myMap.occupied(x, y))
				{
					actor_click = true;	
				}
				/*for (Actor a : myMap.actors)
				{
					//search
					if (a.getXY()[0] == x && a.getXY()[1] == y)
					{
						actor_click = true;
						
					}
				}*/
				if (!actor_click)
					drawFence(x,y);
					
			}
		 
			public void mouseClicked( MouseEvent event )
			{
				// remove the current square if double clicked
			//	if ( event.getClickCount() >= 2 )
			//	{
			//		//System.out.println("double click");
			//		Point point = event.getPoint();
			//		if (!isARGBColor( point, -1 ) )
			//			drawSquare( point, Color.WHITE );
			//	}
			}
		} );
		
		label.addMouseMotionListener( new MouseMotionListener()
		{
			public void mouseMoved(MouseEvent event)
			{
				// set the mouse cursor to cross hairs if it is inside
				// a not white rectangle
				Point point = event.getPoint();
				if (! isARGBColor( point, -1 ) )
					setCursor( Cursor.getDefaultCursor() );
				else
					setCursor( Cursor.getPredefinedCursor (Cursor.CROSSHAIR_CURSOR) );
			}
		 
		 public void mouseDragged(MouseEvent event)
		 {
			 
		 }
		} );
		
		timer = new Timer(100, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt) 
			{
				// turn off timer so we are sure that this frame gets drawn before
				// the next timer event fires.
				timer.stop();
				//if (!myMap.WINNER)
				if (!myMap.GAME_OVER)
				{
					myMap.act();
					// display the next frame
					displayBufferedImage();
					// restart timer (draw next frame in MILLESECONDS_BETWEEN_FRAMES)
					timer.restart();
				}
				else
				{
					System.out.println("game over man");
					//stop the timer
					running = false;
					playPause.setText("Game Over!");
				}
				active_bunnies_num = myMap.active_bunnies_num;
				dead_bunnies_num   = myMap.dead_bunnies_num;
				bunnies_left_num   = myMap.bunnies_left_num;
				active_bunnies.setText("Bunnies in garden: " + active_bunnies_num);
				dead_bunnies.setText("Bunnies lost: " + dead_bunnies_num);
				bunnies_left.setText("Bunnies left: " + bunnies_left_num);
				recent_events.setText("Events: \n");
				
			}
		});
		
		
		this.setTitle("TERM PROJECT - EXTERKAMP");
		
		//this.setSize(width,height);
		displayBufferedImage();
		
		
		
		
		
	}	


private void addMenu()
{
	JMenu fileMenu = new JMenu("File");
	JMenu overlayMenu = new JMenu("Overlays");
	
	JMenuItem make = new JMenuItem("Make simulation Field");
	final ImageFrame f = this;
	make.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent event)
		{
			Object[] options = {"Small(resolution < 1920x1080)", "Large(resolution > 1920x1080)"};
			int choice = JOptionPane.showOptionDialog(null,
					"What size simulation would you like?",
					"Size Selection",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,     //do not use a custom Icon
					options,  //the titles of buttons
					options[0]); //default button title

			int size = 0;
			if(choice == 0 ){
				size = 600;//50x50 grid of 8x8 cells
				CELLSIZE = 24;
			}else{
				size = 800;//100x100 grid of 8x8 cells
				CELLSIZE = 32;
			}
		    //get the preferred probability
			
			//reset vars
			active_bunnies.setText("0");
			dead_bunnies.setText("0");
			bunnies_left.setText("0");
			
			myMap = new map(size,CELLSIZE,stats);
			initializedMap = true;
			displayBufferedImage();
			//f.setSize(myMap.getMap().getWidth()+45,myMap.getMap().getHeight()+lowerPanel.getHeight()+80);
			f.getContentPane().add( leftPanel, BorderLayout.WEST );
			f.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		}
	});
	fileMenu.add(make);

	JMenuItem save = new JMenuItem("Save image");
	save.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent event)
		{
			if (true)
			{
				
				try
				{
					
					// retrieve image
					BufferedImage bi = myMap.getMap();
					String name = "saved@.png";
					Random rand = new Random();
					String numbers = "";
					for (int i = 0; i < 10; i++)
					{
						numbers = numbers + rand.nextInt(100);
					}
					name = name.replace("@", numbers);
					File outputfile = new File(name);
					ImageIO.write(bi, "png", outputfile);
					
				}
				catch ( IOException e )
				{
				   //JOptionPane.showInternalMessageDialog( this,
				//	   		          "Error saving file",
					//			  "oops!",
						//		  JOptionPane.ERROR_MESSAGE );
				}
				
				
			}
			
		}
	});
	fileMenu.add(save);
	JMenuItem exitItem = new JMenuItem("Exit");
	exitItem.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent event)
		{
			System.exit(0);
			//f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
		}
	});
	fileMenu.add(exitItem);
	JMenuItem overlayItem = new JMenuItem("No Overlay");
	overlayItem.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent event)
		{
			myMap.setOverlay(0);
		}
	});
	overlayMenu.add(overlayItem);
	JMenuItem overlayItem1 = new JMenuItem("Path Overlay");
	overlayItem1.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent event)
		{
			myMap.setOverlay(1);
		}
	});
	overlayMenu.add(overlayItem1);
	
	
	
	final JButton button = new JButton( "Start" );
	 button.addActionListener( new ActionListener()
	 {
		public void actionPerformed( ActionEvent event )
		 {
			 running = !running;
			 JButton src = (JButton)event.getSource();
			 if (running)
			 {
				 src.setText("Pause");
				 timer.start();
			 }
			 else
			 {
				 src.setText("Start");
				 timer.stop();
			 }
			 
		 }
	});
	button.setPreferredSize(new Dimension(150,25)); 
	playPause = button;
	
	//LEFT SIDE
	
	
	
	final JLabel Fight = new JLabel( "Fight" );
	final JLabel Flight = new JLabel( "Flight" );
	final JLabel Hunger = new JLabel( "Hunger" );
	final JLabel Courage = new JLabel( "Courage" );
	
	JSlider slider = new JSlider();
    slider.setMajorTickSpacing(1);
    slider.setMaximum(10);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.setValue(0);
    ChangeListener changeListener = new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
    	  JSlider theSlider = (JSlider) changeEvent.getSource();
          if (!theSlider.getValueIsAdjusting()) {
            Fight.setText("Fight = " + new Integer(theSlider.getValue()));
            myMap.changeStat(1, new Integer(theSlider.getValue()));
            stats[0] = new Integer(theSlider.getValue());
          }
      }
    };
    slider.addChangeListener(changeListener);
    JSlider slider2 = new JSlider();
    slider2.setMajorTickSpacing(1);
    slider2.setMaximum(10);
    slider2.setPaintTicks(true);
    slider2.setPaintLabels(true);
    slider2.setValue(0);
    ChangeListener changeListener2 = new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
    	  JSlider theSlider = (JSlider) changeEvent.getSource();
          if (!theSlider.getValueIsAdjusting()) {
            Flight.setText("Flight = " + new Integer(theSlider.getValue()));
            myMap.changeStat(2, new Integer(theSlider.getValue()));
            stats[1] = new Integer(theSlider.getValue());
          }
      }
    };
    slider2.addChangeListener(changeListener2);
    JSlider slider3 = new JSlider();
    slider3.setMajorTickSpacing(1);
    slider3.setMaximum(10);
    slider3.setPaintTicks(true);
    slider3.setPaintLabels(true);
    slider3.setValue(0);
    ChangeListener changeListener3 = new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
    	  JSlider theSlider = (JSlider) changeEvent.getSource();
          if (!theSlider.getValueIsAdjusting()) {
            Hunger.setText("Hunger = " + new Integer(theSlider.getValue()));
            myMap.changeStat(3, new Integer(theSlider.getValue()));
            stats[2] = new Integer(theSlider.getValue());
          }
      }
    };
    slider3.addChangeListener(changeListener3);
    JSlider slider4 = new JSlider();
    slider4.setMajorTickSpacing(1);
    slider4.setMaximum(10);
    slider4.setPaintTicks(true);
    slider4.setPaintLabels(true);
    slider4.setValue(0);
    ChangeListener changeListener4 = new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
    	  JSlider theSlider = (JSlider) changeEvent.getSource();
          if (!theSlider.getValueIsAdjusting()) {
            Courage.setText("Courage = " + new Integer(theSlider.getValue()));
            myMap.changeStat(4, new Integer(theSlider.getValue()));
            stats[3] = new Integer(theSlider.getValue());
          }
      }
    };
    slider4.addChangeListener(changeListener4);
	
	JMenuBar menuBar = new JMenuBar();
	menuBar.add(fileMenu);
	menuBar.add(overlayMenu);
	lowerPanel = new JPanel();
	lowerPanel.add(button);
	leftPanel = new JPanel();
	
	leftPanel.add(Fight);
	leftPanel.add(slider);
	leftPanel.add(Flight);
	leftPanel.add(slider2);
	leftPanel.add(Hunger);
	leftPanel.add(slider3);
	leftPanel.add(Courage);
	leftPanel.add(slider4);
	
	JLabel active_bunnies = new JLabel();
	JLabel dead_bunnies = new JLabel();
	JLabel bunnies_left = new JLabel();
	JLabel recent_events = new JLabel();
	this.active_bunnies = active_bunnies;
	this.dead_bunnies = dead_bunnies;
	this.bunnies_left = bunnies_left;
	this.recent_events = recent_events;
	leftPanel.add(active_bunnies);
	leftPanel.add(dead_bunnies);
	leftPanel.add(bunnies_left);
	leftPanel.add(recent_events);
	
	active_bunnies.setText("Bunnies in garden: ");
	dead_bunnies.setText("Bunnies lost: ");
	bunnies_left.setText("Bunnies left: ");
	recent_events.setText("Events: \n");
	
	leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
	
	//leftPanel.setSize(new Dimension(50,100));
	this.getContentPane().add( lowerPanel, BorderLayout.SOUTH );
	
	JScrollPane jpane = new JScrollPane(Picframe);
	this.getContentPane().add(jpane,BorderLayout.CENTER);
	
	this.setJMenuBar(menuBar);	
	this.setSize(myMap.getMap().getWidth()+30,myMap.getMap().getHeight()+lowerPanel.getHeight()+110);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
}

public void displayBufferedImage()
{
	if (initializedMap)
	{
		//System.out.println("displaying");
		//Picframe.removeAll();
		label.setIcon(new ImageIcon(myMap.getMap()));
		//Picframe.add(label);
		Picframe.repaint();
		this.validate();
	}
}

private boolean isARGBColor( Point p, int argb )
{
	//System.out.println("x: " + p.x + " y: " + p.y);
	//System.out.println(myMap.getMap().getRGB(p.x,p.y) + " versus " + argb);
	return (myMap.getMap().getRGB( p.x, p.y ) == argb );
}

/*private void drawSquare( Point point, Color color )
{
	Graphics2D g2d = myMap.getMap().createGraphics();
	int x = ((point.x >> 3) << 3);
	int y = ((point.y >> 3) << 3);

	g2d.setColor( color );
	g2d.fillRect( x, y, 8, 8 );
	displayBufferedImage();
}*/

private void drawFence(int x, int y)
{
	if(myMap.addActor(x, y, actorTYPE.FENCE))
		displayBufferedImage();
}


}

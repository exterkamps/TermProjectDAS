import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
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
		myMap = new map(size, CELLSIZE);
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
				drawFence(point);
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
				
				myMap.act();
				//do action
				
				// display the next frame
				displayBufferedImage();
				// restart timer (draw next frame in MILLESECONDS_BETWEEN_FRAMES)
				timer.restart();
			}
		});
		
		
		this.setTitle("TERM PROJECT - EXTERKAMP");
		
		//this.setSize(width,height);
		displayBufferedImage();
		
		
		
		
		
	}	


private void addMenu()
{
	JMenu fileMenu = new JMenu("File");
	
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
			myMap = new map(size,CELLSIZE);
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
	
	final JButton button = new JButton( "Start" );
	 button.addActionListener( new ActionListener()
	 {
		 @SuppressWarnings("deprecation")
		public void actionPerformed( ActionEvent event )
		 {
			 running = !running;
			 JButton src = (JButton)event.getSource();
			 if (running)
			 {
				 src.setLabel("Pause");
				 timer.start();
			 }
			 else
			 {
				 src.setLabel("Start");
				 timer.stop();
			 }
			 
		 }
	});
	button.setPreferredSize(new Dimension(150,25)); 
	
	final JButton Fight = new JButton( "Fight = 0" );
	
	JSlider slider = new JSlider();
    slider.setMajorTickSpacing(1);
    slider.setMaximum(10);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.setValue(0);
    ChangeListener changeListener = new ChangeListener() {
      @SuppressWarnings("deprecation")
	public void stateChanged(ChangeEvent changeEvent) {
    	  JSlider theSlider = (JSlider) changeEvent.getSource();
          if (!theSlider.getValueIsAdjusting()) {
            Fight.setLabel("Fight = " + new Integer(theSlider.getValue()));
          }
      }
    };
    slider.addChangeListener(changeListener);
	
	
	JMenuBar menuBar = new JMenuBar();
	menuBar.add(fileMenu);
	lowerPanel = new JPanel();
	lowerPanel.add(button);
	leftPanel = new JPanel();
	
	leftPanel.add(slider);
	
	leftPanel.add(Fight);
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

private void drawSquare( Point point, Color color )
{
	Graphics2D g2d = myMap.getMap().createGraphics();
	int x = ((point.x >> 3) << 3);
	int y = ((point.y >> 3) << 3);

	g2d.setColor( color );
	g2d.fillRect( x, y, 8, 8 );
	displayBufferedImage();
}

private void drawFence(Point point)
{
	Graphics2D g2d = myMap.getMap().createGraphics();
	//System.out.println(point.toString());
	int x = (point.x / myMap.CELLSIZE);
	int y = (point.y / myMap.CELLSIZE);
	//g2d.setColor( Color.GRAY );
	//g2d.fillRect( x, y, 24, 24 );
	//displayBufferedImage();
	if(myMap.addActor(x, y, actorTYPE.FENCE))
		displayBufferedImage();
}


}

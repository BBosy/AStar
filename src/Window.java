import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends JFrame implements Runnable{

	BannerPanel bannerLayer = new BannerPanel();
	MenuPanel menuLayer = new MenuPanel();
	MazePanel mazeLayer = new MazePanel();
	TailPanel tailLayer = new TailPanel();
	MazeMenuPanel mazeMenuLayer = new MazeMenuPanel();
	Maze maze;
	Astar pathFinder;
	PathDrawer pathDrawer;
	boolean isAldreadyDrawn = false;
	Thread th1, th2, th3;
	
	int height = 0;
	int width = 0;
	float density = 0.2f;
	int entryX;
	int entryY;
	int exitX;
	int exitY;
	
	private boolean tailFlag = false;
	
	
	public Window() {
		this.setTitle("A* algorithm demo");
		this.setSize(400, 300);
		this.setLayout(new BorderLayout());
		menuLayer.setup(this);
		bannerLayer.setup();
		tailLayer.setup();
		this.getContentPane().add(menuLayer, BorderLayout.CENTER);
		this.getContentPane().add(bannerLayer, BorderLayout.NORTH);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void run() {
	}
	
	private void addTailLayer() {
		this.getContentPane().add(tailLayer, BorderLayout.SOUTH);
		this.setSize(400, 420);
		tailFlag = true;
	}
	
	private void removeTailLayer() {
		this.getContentPane().remove(tailLayer);
		this.setSize(400, 300);
		tailFlag = false;
	}
	
	public void switchToMazeView() {
		this.setSize(1016,1065);
		this.getContentPane().remove(menuLayer);
		this.getContentPane().remove(bannerLayer);
		if(tailFlag==true) this.getContentPane().remove(tailLayer);
		mazeMenuLayer.setup();
		mazeLayer.setup();
		SizeShower ss = new SizeShower(this);

		mazeLayer.enableThread();
		pathFinder.enableThread();
		pathDrawer.enableThread();
		
		//Thread th0 = new Thread(ss);
		th1 = new Thread(mazeLayer);
		th2 = new Thread(pathFinder);
		th3 = new Thread(pathDrawer);
		
		//th0.start();
		th1.start();
		th2.start();
		th3.start();
		this.getContentPane().add(mazeMenuLayer, BorderLayout.NORTH);
		this.getContentPane().add(mazeLayer, BorderLayout.CENTER);
	}
	
	public void switchToMenuView() {
		this.setSize(400, 300);
		this.getContentPane().remove(mazeLayer);
		this.getContentPane().remove(mazeMenuLayer);
		this.getContentPane().add(menuLayer, BorderLayout.CENTER);
		this.getContentPane().add(bannerLayer, BorderLayout.NORTH);
	}
	
	private class SizeShower implements Runnable {
		
		Window win;
		
		public SizeShower(Window win) {
			this.win = win;
		}
		
		public void run() {
			for(;;) {
				System.out.println(win.getSize());
			}
		}
	}
	
	/***********************************************************************************/
	
	
	private class TailPanel extends JPanel{
		
		private static final long serialVersionUID = -7099899309793812446L;
		
		private JTextField customEntryHeight = new JTextField();
		private JTextField customEntryWidth = new JTextField();
		private JTextField customExitHeight = new JTextField();
		private JTextField customExitWidth = new JTextField();
		private JButton acceptCustomEntryButton = new JButton("OK");
		private JLabel entryLabel = new JLabel("Entry");
		private JLabel exitLabel = new JLabel("Exit");
		private JLabel x = new JLabel("x :");
		private JLabel y = new JLabel("y :");
		
		public void setup() {
			GridLayout g2 = new GridLayout(4,3);
			g2.setHgap(5);
			g2.setVgap(5);
			this.setLayout(g2);
			//this.add(new JPanel());
			
			this.acceptCustomEntryButton.addActionListener(new AcceptCustomEntryButtonClass());
			
			this.add(new JPanel());
			this.add(entryLabel);
			this.add(exitLabel);
			this.add(x);
			this.add(customEntryWidth);
			this.add(customExitWidth);
			this.add(y);
			this.add(customEntryHeight);
			this.add(customExitHeight);
			this.add(acceptCustomEntryButton);
		}
		
		private class AcceptCustomEntryButtonClass implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				entryY = Integer.parseInt(tailLayer.customEntryHeight.getText());
				entryX = Integer.parseInt(tailLayer.customEntryWidth.getText());
				exitY = Integer.parseInt(tailLayer.customExitHeight.getText());
				exitX = Integer.parseInt(tailLayer.customExitWidth.getText());
				clamp(entryX, entryY, exitX, exitY);
				System.out.printf("%d %d %d %d\n", entryX, entryY, exitX, exitY);
			}
		}
		
		public void clamp(int enX, int enY, int exX, int exY) {
			if(enX < 0) enX = 0;
			if(enX > width-1) enX = width-1;
			if(enY < 0) enX = 0;
			if(enY > height-1) enX = width-1;
			
			
			if(exX < 0) enX = 0;
			if(exX > width-1) enX = width-1;
			if(exY < 0) enX = 0;
			if(exY > height-1) enX = width-1;
		}
	}
	
	
	/*****************************************************************************************/
	
	/**
	 * @author BosyBosy//
	 * @apiNote 5 button menu that let's the user select which entry-exit configuration they want for their maze.
	 */
	private class MenuPanel extends JPanel{
		
		private static final long serialVersionUID = -2512145543304965555L;
		private JButton customizeButton = new JButton("Custom");
		private JButton cornersTopButton = new JButton("Corners Top Bottom");
		private JButton cornersBottomButton = new JButton("Corners Bottom Top");
		private JButton topBottomButton = new JButton("Top Bottom");
		private JButton leftRightButton = new JButton("Left Right");
		private JButton validateSize = new JButton("OK");
		private JButton generateMaze = new JButton("Validate Maze Config");
		private JButton validateDensity = new JButton("OK");
		
		private JLabel sizeLabel = new JLabel("Enter size :");
		private JLabel densityLabel = new JLabel("Enter obstacle density");
		private JLabel configLabel = new JLabel("Select Entry-Exit config :");
		private JTextField heightField = new JTextField();
		private JTextField widthField = new JTextField();
		private JTextField obstacleDensityField = new JTextField();
		private Window win;
		
		public void setup(Window win) {
			this.win = win;
			GridLayout g1 = new GridLayout(8,2);
			g1.setHgap(5);
			g1.setVgap(5);
			this.setLayout(g1);
			
			this.validateSize.addActionListener(new ValidateSizeClass());
			this.cornersTopButton.addActionListener(new CornersTopButtonClass());
			this.cornersBottomButton.addActionListener(new CornersBottomButtonClass());
			this.topBottomButton.addActionListener(new TopBottomButtonClass());
			this.leftRightButton.addActionListener(new LeftRightButtonClass());
			this.customizeButton.addActionListener(new CustomizeButtonClass());
			this.validateDensity.addActionListener(new ValidateDensityButton());
			generateMaze.addActionListener(new GenerateMazeButtonClass());
			
			this.add(sizeLabel);
			this.add(validateSize);
			this.add(heightField);
			this.add(widthField);
			this.add(densityLabel);
			this.add(validateDensity);
			this.add(obstacleDensityField);
			this.add(new JPanel());
			this.add(configLabel);
			generateMaze.setBackground(Color.RED);
			this.add(generateMaze);
			this.add(cornersTopButton);
			this.add(cornersBottomButton);
			this.add(topBottomButton);
			this.add(leftRightButton);
			this.add(customizeButton);
		}
		
		private class ValidateSizeClass implements ActionListener{
			
			@Override
			public void actionPerformed(ActionEvent e) {
				height = Integer.parseInt(heightField.getText());
				width = Integer.parseInt(widthField.getText());
			}
		}
		
		private class CustomizeButtonClass implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				bannerLayer.typeLabel.setText("CUSTOMIZE");
				if(tailFlag == false) addTailLayer();
			}
		}
		
		private class CornersTopButtonClass implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				bannerLayer.typeLabel.setText("Top left to bottom right");
				entryX = 0;
				entryY = 0;
				exitX = width-1;
				exitY = height-1;
				win.update(win.getGraphics());
				if(tailFlag == true) removeTailLayer();
			}
		}
		
		private class CornersBottomButtonClass implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				bannerLayer.typeLabel.setText("Bottom left to top right");
				entryX = 0;
				entryY = height-1;
				exitX = width-1;
				exitY = 0;
				win.update(win.getGraphics());
				if(tailFlag == true) removeTailLayer();				
			}
		}
		
		private class TopBottomButtonClass implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				bannerLayer.typeLabel.setText("Top to bottom");
				entryX = (int) width/2;
				entryY = 0;
				exitX = (int) width/2;
				exitY = height-1;
				win.update(win.getGraphics());
				if(tailFlag == true) removeTailLayer();
			}
		}
		
		private class LeftRightButtonClass implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				bannerLayer.typeLabel.setText("Left to right");
				entryX = 0;
				entryY = (int) height/2;
				exitX = width-1;
				exitY = (int) height/2;
				win.update(win.getGraphics());
				if(tailFlag == true) removeTailLayer();
			}
		}
		
		private class ValidateDensityButton implements ActionListener{
			
			@Override
			public void actionPerformed(ActionEvent e) {
				density = Float.parseFloat(obstacleDensityField.getText());
				if(density < 0) density = 0;
				if(density > 1) density = 1;
			}
		}
		
		private class GenerateMazeButtonClass implements ActionListener{
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(height!=0 && width!=0) {
					maze = new Maze(height, width, density, new Coordinates(entryX, entryY), new Coordinates(exitX, exitY));
					pathFinder = new Astar(maze);
					pathDrawer = new PathDrawer(maze, pathFinder.getCameFrom(), pathFinder.getEnd());
					switchToMazeView();
					System.out.println("lalalalala");
				}
			}
		}
	}
	
	/*******************************************************************************************************/
	
	private class BannerPanel extends JPanel{
		private JLabel typeLabel = new JLabel("Maze Config");
		
		public void setup() {
			this.add(typeLabel);
			typeLabel.setHorizontalAlignment(JLabel.CENTER);
		}
	}
	
	/*****************************************************************************************************/

	private class MazeMenuPanel extends JPanel{

		private static final long serialVersionUID = -7552804089042798039L;
		private JButton getBackToMenuButton = new JButton("Main Menu");
		private JButton regenerateMazeButton = new JButton("Regenerate Maze");
		
		public void setup() {
			this.removeAll();
			
			getBackToMenuButton.addActionListener(new GetBackToMenuButtonClass());
			regenerateMazeButton.addActionListener(new RegenerateMazeButtonClass());
			
			this.setLayout(new GridLayout(1,4));
			this.add(getBackToMenuButton);
			this.add(new JPanel()); this.add(new JPanel());
			this.add(regenerateMazeButton);
		}
		
		private class GetBackToMenuButtonClass implements ActionListener{
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToMenuView();
			}
		}
		
		
		private class RegenerateMazeButtonClass implements ActionListener{
			
			@Override
			public void actionPerformed(ActionEvent e) {
				maze = new Maze(height, width, density, new Coordinates(entryX, entryY), new Coordinates(exitX, exitY));
				pathDrawer.stopThread();
				pathFinder.stopThread();
				mazeLayer.stopThread();
				pathFinder = new Astar(maze);
				pathDrawer = new PathDrawer(maze, pathFinder.getCameFrom(), pathFinder.getEnd());
				switchToMazeView();
			}
		}
	}
	
	/*****************************************************************************************************/

	private class MazePanel extends JPanel implements Runnable{

		private static final long serialVersionUID = 1710077125106960186L;
		private volatile boolean threadFlag;

		public void setup() {
			this.removeAll();
		}
		
		public void paintComponent(Graphics g) {
			paintMaze(g);
		}
		
		private void paintMaze(Graphics g) {
			int M = maze.getHeight();
			int N = maze.getWidth();
			
			Maze.Node[][] matrix = maze.getMazeMatrix();
			
			Coordinates cref = new Coordinates(0,0);
			
			int step = getStepSize();
			//System.out.println(step);
			
			for(int i=0 ; i<M ; i++) {
				
				cref.x = 0;
				
				for(int k=0 ; k<N ; k++) {
					drawBox(g, matrix[i][k].getState(), new Coordinates(k, i), step);
					cref.x += step;
				}
				
				cref.y += step;
			}
			
			
		}
		
		private void drawBox(Graphics g, Maze.State state, Coordinates c, int step) {
			int line_width = step/10;
			//int line_width = step/10 >= 1 ? step/10 : 1;
			int border_width = step - 2*line_width;
			border_width = border_width > 0 ? border_width : 0;
			
			g.setColor(Color.BLACK);
			g.fillRect(c.x*step, c.y*step, step, step);
			
			switch(state) {
				case BLANK : g.setColor(Color.WHITE); g.fillRect(c.x*step+line_width, c.y*step+line_width, border_width, border_width); break;
				case OPEN : g.setColor(Color.BLUE); g.fillRect(c.x*step+line_width, c.y*step+line_width, border_width, border_width); break;
				case CLOSED : g.setColor(Color.RED); g.fillRect(c.x*step+line_width, c.y*step+line_width, border_width, border_width); break;
				case ENTRY : g.setColor(Color.YELLOW); g.fillRect(c.x*step+line_width, c.y*step+line_width, border_width, border_width); break;
				case EXIT : g.setColor(Color.MAGENTA); g.fillRect(c.x*step+line_width, c.y*step+line_width, border_width, border_width); break;
				case PATH : g.setColor(Color.GREEN); g.fillRect(c.x*step+line_width, c.y*step+line_width, border_width, border_width); break;
				case WALL : break;
			}
		}
		
		public int getStepSize() {
			int M = maze.getHeight();
			int N = maze.getWidth();
			
			int h = this.getHeight();
			int w = this.getWidth();
			
			int h_box = h/M;
			int l_box = w/N;
			
			int step = h_box <= l_box ? h_box : l_box;
			step = step >= 1 ? step : 1;
			
			return step;
		}
		
		public void run() {
			while(!threadFlag) {
				System.out.println("Repainting");
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void enableThread() {
			threadFlag = false;
		}
		
		public void stopThread() {
			threadFlag = true;
		}
	}
}

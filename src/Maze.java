import java.util.ArrayList;

public class Maze {
	private int height;
	private int width;
	private float obstacleDensity;
	private Coordinates entry;
	private Coordinates exit;
	private Node[][] mazeMatrix; 
	
	/**
	 * Default constructor for Maze class. Default maze size is 20x20, the obstacle density is 20%, the entry and exit points are in "CORNERSTOP" configuration.
	 */
	public Maze() {
		height = 20;
		width = 20;
		
		mazeMatrix = new Node[height][width];
		
		for(int i=0 ; i< height ; i++) {
			for(int k=0 ; k<width ; k++) {
				mazeMatrix[i][k] = new Node(new Coordinates(k, i), State.BLANK);
			}
		}
		
		entry = new Coordinates(0,0);
		exit = new Coordinates(height-1, width-1);
		
		obstacleDensity = 0.2f;
		
		generateObstacles();
		
		setBoxState(State.ENTRY, entry);
		setBoxState(State.EXIT, exit);
		
		for(int i=0 ; i<height ; i++) {
			for(int k=0 ; k<width ; k++) {
				mazeMatrix[i][k].buildNeighbors(this);
			}
		}
	}
	
	/**
	 * @param height : Height of the maze
	 * @param width : Width of the maze
	 * @param obstacleDensity : Density of obstacles (between 0 and 1)
	 */
	public Maze(int height, int width, float obstacleDensity) {
		this.height = height;
		this.width = width;
		
		mazeMatrix = new Node[height][width];
		
		for(int i=0 ; i< height ; i++) {
			for(int k=0 ; k<width ; k++) {
				mazeMatrix[i][k] = new Node(new Coordinates(k, i), State.BLANK);
			}
		}
		
		entry = new Coordinates(0,0);
		exit = new Coordinates(width-1, height-1);
		
		this.obstacleDensity = obstacleDensity;
		
		generateObstacles();
		
		setBoxState(State.ENTRY, entry);
		setBoxState(State.EXIT, exit);
		
		for(int i=0 ; i<height ; i++) {
			for(int k=0 ; k<width ; k++) {
				mazeMatrix[i][k].buildNeighbors(this);
			}
		}
	}
	
	/**
	 * @param height : Height of the maze
	 * @param width : Width of the maze
	 * @param obstacleDensity : Density of obstacles (between 0 and 1)
	 * @param startEndLoc : "CORNERSTOP", "CORNERSBOTTOM", "LEFTRIGHT", "TOPBOTTOM"
	 */
	public Maze(int height, int width, float obstacleDensity, String startEndLoc) {
		
		this.height = height;
		this.width = width;
		
		mazeMatrix = new Node[height][width];
		
		for(int i=0 ; i< height ; i++) {
			for(int k=0 ; k<width ; k++) {
				mazeMatrix[i][k] = new Node(new Coordinates(k, i), State.BLANK);
			}
		}
		
		switch(startEndLoc) {
			case "CORNERSTOP" : entry = new Coordinates(0, 0); exit = new Coordinates(width-1, height-1); break;
			case "CORNERSBOTTOM" : entry = new Coordinates(0, height-1); exit = new Coordinates(width-1, 0); break;
			case "LEFTRIGHT" : entry = new Coordinates(0, (int) height/2); exit = new Coordinates(width-1, (int) height/2); break;
			case "TOPBOTTOM" : entry = new Coordinates((int) width/2, 0); exit = new Coordinates((int) width/2, height-1); break;
		}
		
		this.obstacleDensity = obstacleDensity;
		
		generateObstacles();
		
		setBoxState(State.ENTRY, entry);
		setBoxState(State.EXIT, exit);
		
		for(int i=0 ; i<height ; i++) {
			for(int k=0 ; k<width ; k++) {
				mazeMatrix[i][k].buildNeighbors(this);
			}
		}
	}
	
	/**
	 @param height : Height of the maze
	 * @param width : Width of the maze
	 * @param obstacleDensity : Density of obstacles (between 0 and 1)
	 * @param entry : Coordinates() of entry point
	 * @param exit : Coordinates() of exit point
	 */
	public Maze(int height, int width, float obstacleDensity, Coordinates entry, Coordinates exit) {
		this.height = height;
		this.width = width;
		
		mazeMatrix = new Node[height][width];
		
		for(int i=0 ; i< height ; i++) {
			for(int k=0 ; k<width ; k++) {
				mazeMatrix[i][k] = new Node(new Coordinates(k, i), State.BLANK);
			}
		}
		
		this.entry = entry;
		this.exit = exit;
		
		this.obstacleDensity = obstacleDensity;
		
		generateObstacles();
		
		setBoxState(State.ENTRY, entry);
		setBoxState(State.EXIT, exit);
		
		for(int i=0 ; i<height ; i++) {
			for(int k=0 ; k<width ; k++) {
				mazeMatrix[i][k].buildNeighbors(this);
			}
		}
	}
	
	private void generateObstacles() {
		for(int i=0 ; i<height ; i++) {
			for(int k=0 ; k<width ; k++) {
				float r = (float) Math.random();
				
				if(r <= obstacleDensity) {
					mazeMatrix[i][k].setState(State.WALL);
				}
			}
		}
	}
	
	public void printMaze() {
		for(int i=0 ; i<height ; i++) {
			
			System.out.print("|");
			
			for(int k=0 ; k<width ; k++) {
				switch(mazeMatrix[i][k].getState()) {
					case BLANK: System.out.print("_|"); break;
					case PATH: System.out.print("O|"); break;
					case WALL: System.out.print("X|"); break;
					case ENTRY: System.out.print("+|"); break;
					case EXIT: System.out.print("-|"); break;
					default: break;
				}
			}
			
			System.out.println();
		}
	}
	
	public void drawPath(Coordinates[][] pathMap, Coordinates end) {
		Coordinates current = pathMap[end.y][end.x];
		
		while(current.x!=getEntry().x || current.y!=getEntry().y) {
			setBoxState(State.PATH, current);
			current = pathMap[current.y][current.x];
		}
	}
	
	public void setBoxState(State state, Coordinates c) {
		mazeMatrix[c.y][c.x].setState(state);
	}
	
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public Coordinates getEntry() {
		return entry;
	}

	public Coordinates getExit() {
		return exit;
	}

	public Node[][] getMazeMatrix() {
		return mazeMatrix;
	}

	public void setMazeMatrix(Node[][] mazeMatrix) {
		this.mazeMatrix = mazeMatrix;
	}
	
	public class Node{
		Coordinates coord;
		State state;
		ArrayList<Node> neighbors;
		
		public Node() {
			coord = new Coordinates(-1,-1);
			state = State.BLANK;
			neighbors = new ArrayList<>();
		}
		
		public Node(Coordinates coord, State state) {
			this.coord = coord;
			this.state = state;
			neighbors = new ArrayList<>();
		}
		
		private void buildNeighbors(Maze maze) {
			int x = coord.x;
			int y = coord.y;
			Node[][] mat = maze.getMazeMatrix();
			
			if(x-1 >= 0 && mat[y][x-1].getState()!=State.WALL) {
				neighbors.add(mat[y][x-1]);
			}
			
			if(x+1 < maze.getWidth() && mat[y][x+1].getState()!=State.WALL) {
				neighbors.add(mat[y][x+1]);
			}
			
			if(y-1 >= 0 && mat[y-1][x].getState()!=State.WALL) {
				neighbors.add(mat[y-1][x]);
			}
			
			if(y+1 < maze.getHeight() && mat[y+1][x].getState()!=State.WALL) {
				neighbors.add(mat[y+1][x]);
			}
			
			if(x-1 >= 0 && y-1 >= 0 && mat[y-1][x-1].getState()!=State.WALL) {
				neighbors.add(mat[y-1][x-1]);
			}
			
			if(x-1 >= 0 && y+1 < height && mat[y+1][x-1].getState()!=State.WALL) {
				neighbors.add(mat[y+1][x-1]);
			}
			
			if(x+1 < width && y-1 >= 0 && mat[y-1][x+1].getState()!=State.WALL) {
				neighbors.add(mat[y-1][x+1]);
			}
			
			if(x+1 < width && y+1 < height && mat[y+1][x+1].getState()!=State.WALL) {
				neighbors.add(mat[y+1][x+1]);
			}
		}
		
		public Coordinates getCoord() {
			return coord;
		}
		
		public void setCoord(Coordinates c) {
			this.coord = c;
		}
		
		public State getState() {
			return state;
		}
		
		public void setState(State state) {
			this.state = state;
		}
	}



	enum State {
		OPEN,
		CLOSED,
		PATH,
		BLANK,
		WALL,
		ENTRY,
		EXIT
	}
}

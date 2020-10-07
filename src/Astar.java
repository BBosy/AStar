import java.util.ArrayList;

public class Astar implements Runnable{
	Maze maze;
	ArrayList<Maze.Node> openSet;
	ArrayList<Maze.Node> closedSet;
	Coordinates pathEnd;
	Coordinates[][] cameFrom;
	int[][][] fgh;
	volatile boolean threadFlag;
	
	public Astar(Maze maze) {
		this.maze = maze;
		
		openSet = new ArrayList<>();
		closedSet = new ArrayList<>();
		cameFrom = new Coordinates[this.maze.getHeight()][this.maze.getWidth()];

		for(int i=0 ; i<this.maze.getHeight() ; i++) {
			for(int k=0 ; k<this.maze.getWidth() ; k++) {
				cameFrom[i][k] = new Coordinates(0,0);
			}
		}
		
		fgh = new int[3][this.maze.getHeight()][this.maze.getWidth()];
		
		openSet.add(this.maze.getMazeMatrix()[this.maze.getEntry().y][this.maze.getEntry().x]);
		
		for(int i=0 ; i<this.maze.getHeight() ; i++) {
			for(int k=0 ; k<this.maze.getWidth() ; k++) {
				fgh[1][i][k] = 999999;
				fgh[2][i][k] = heuristic(this.maze.getMazeMatrix()[i][k].getCoord(), this.maze.getExit());
				fgh[0][i][k] = fgh[1][i][k] + fgh[2][i][k];
			}
		}
		
		fgh[1][this.maze.getEntry().y][this.maze.getEntry().x] = 0;
		fgh[2][this.maze.getEntry().y][this.maze.getEntry().x] = heuristic(this.maze.getEntry(), this.maze.getExit());
		
		fgh[0][this.maze.getEntry().y][this.maze.getEntry().x] = fgh[1][this.maze.getEntry().y][this.maze.getEntry().x] + fgh[2][this.maze.getEntry().y][this.maze.getEntry().x];
		pathEnd = this.maze.getEntry();
	}

	private int heuristic(Coordinates entry, Coordinates exit) {

//		int h = (int) Math.sqrt(Math.pow(entry.x - exit.x, 2) + Math.pow(entry.y - exit.y, 2));
		
		int h = Math.abs(entry.x-exit.x) + Math.abs(entry.y-exit.y);
		
		return h;
	}
	
	public void run(){
		if(!threadFlag) {
			Coordinates[][] x = traverseMaze();
			if(x == null)
				return;
	}
}
	
	public Coordinates[][] traverseMaze() {
		while(openSet.size() > 0 && !threadFlag) {
			System.out.println("Computing");
			Maze.Node x = openSet.get(getOpenSetMin());
			
			for(Maze.Node i : openSet) {
				this.maze.setBoxState(Maze.State.OPEN, i.getCoord());
			}
			
			if(x.getCoord().equals(this.maze.getExit())) {
				System.out.println("Success!");
				return cameFrom;
			}
			openSet.remove(x);
			closedSet.add(x);
			if(x.getCoord().x!=this.maze.getEntry().x || x.getCoord().y!=this.maze.getEntry().y) this.maze.setBoxState(Maze.State.CLOSED, x.getCoord());
			
			for(Maze.Node n : x.neighbors) {
				if(!closedSet.contains(n)) {
					int gScore = fgh[1][x.getCoord().y][x.getCoord().x] + 1;
					boolean isBetter = true;
					
					if(gScore < fgh[1][n.getCoord().y][n.getCoord().x])
						isBetter = true;
					
					if(isBetter) {
						cameFrom[n.getCoord().y][n.getCoord().x] = x.getCoord();
						
						fgh[1][n.getCoord().y][n.getCoord().x] = gScore;
//						fgh[2][n.getCoord().y][n.getCoord().x] = heuristic(n.getCoord(), maze.getExit());
						fgh[0][n.getCoord().y][n.getCoord().x] = fgh[1][n.getCoord().y][n.getCoord().x] + fgh[2][n.getCoord().y][n.getCoord().x];
						if(!openSet.contains(n))
							openSet.add(n);
						
						//System.out.println(fgh[0][n.getCoord().y][n.getCoord().x] + " " + fgh[1][n.getCoord().y][n.getCoord().x] + " " + fgh[2][n.getCoord().y][n.getCoord().x]);
						pathEnd = n.getCoord();
					}
				}
			}
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Failure!");
		return null;
	}
	
	private int getOpenSetMin() {
		int idx = 0;
		int min = fgh[0][openSet.get(idx).getCoord().y][openSet.get(idx).getCoord().x];
		
		for(int i=0 ; i<openSet.size(); i++) {
			
			if(fgh[0][openSet.get(i).getCoord().y][openSet.get(i).getCoord().x] < min) {
				min = fgh[0][openSet.get(i).getCoord().y][openSet.get(i).getCoord().x];
				idx = i;
			}
		}
		
		return idx;
	}
	
	private void colorMaze() {
		for(int i=0 ; i<openSet.size() ; i++) {
			openSet.get(i).setState(Maze.State.OPEN);
		}
		for(int i=0 ; i<closedSet.size() ; i++) {
			if(openSet.get(i).getState() != Maze.State.PATH)
				openSet.get(i).setState(Maze.State.CLOSED);
		}
	}
	
	public Coordinates[][] getCameFrom(){
		return cameFrom;
	}
	
	public Coordinates getEnd() {
		return pathEnd;
	}

	public void enableThread() {
		threadFlag = false;
	}
	
	public void stopThread() {
		threadFlag = true;
	}
}

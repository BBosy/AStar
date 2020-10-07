
public class PathDrawer extends Thread{
	Maze maze;
	Coordinates[][] pathMap;
	Coordinates end;
	int height;
	int width;
	volatile boolean threadFlag;
	
	public PathDrawer(Maze maze, Coordinates[][] pathMap, Coordinates end) {
		this.maze = maze;
		this.pathMap = pathMap;
		this.end = end;
	}
	
	public void drawPath() {
		Coordinates current = pathMap[end.y][end.x];
		
		while(current.x!=maze.getEntry().x || current.y!=maze.getEntry().y) {
			maze.setBoxState(Maze.State.PATH, current);
			current = pathMap[current.y][current.x];
		}
	}

	
	public void erasePath() {
		for(int i=0 ; i<height ; i++) {
			for(int k=0 ; k<width ; k++) {
				if(maze.getMazeMatrix()[i][k].getState() == Maze.State.PATH)
					maze.getMazeMatrix()[i][k].setState(Maze.State.CLOSED);
			}
		}
	}
	
	public void run() {
		while(!threadFlag) {
			System.out.println("Drawing");
			//erasePath();
			drawPath();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
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

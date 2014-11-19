import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

class tuple
{
	Point2D current;
	tuple from;
	
	public tuple(Point2D cur, tuple fro)
	{
		current = cur;
		from = fro;
	}
}

class CoordinateComparator implements Comparator<Point2D.Float> 
{
	public CoordinateComparator() {}

	 @Override
	 public int compare(Point2D.Float p1, Point2D.Float p2) {
	        if (p1.getX() < p2.getX()) 
	         return -1;
	        if (p1.getX() > p2.getX())
	         return 1;
	        if (p1.getY() < p2.getY())
	         return -1;
	        if (p1.getY() > p2.getY())
	         return 1;
	        return 0;
	 }
}

	//CoordinateComparator coordCompare = new CoordinateComparator();
	//TreeSet<Point2D.Float> coordSet = new TreeSet<Point2D.Float>(coordCompare);


public class Astar {
	
	ArrayList<Point2D> myHopesAndDreams;
	
	public Astar(){
		//
		myHopesAndDreams = new ArrayList<Point2D>();
	}
	
	
	public Stack<Point2D> pathfindBreadthFirst(Point2D start, Point2D end, map m)
	{
		boolean DEBUG = false;
		if (DEBUG)
		System.out.println(start.toString() + " -> " + end.toString());
		//right now breadth first
        //openset := {start}    // The set of tentative nodes to be evaluated, initially containing the start node
		Queue<Point2D> frontier = new LinkedList<Point2D>();
		
		frontier.add(start);
		//came_from := the empty map     //The map of navigated nodes.
		
		int[] came_from = new int[625];
		for (int i = 0; i < came_from.length-1;i++)
		{
			came_from[i] = -2;
		}
		//came_from.add(new tuple(start, null));
		came_from[(int)(start.getX() + start.getY()*25)] = -1;
		//while openset is not empty
		while (!frontier.isEmpty())
		{
			
        	//current := the node in openset having the lowest f_score[] value
			Point2D current = frontier.poll();
			
			if (DEBUG)
				System.out.println(current.toString());
        	//if current = goal
            	//return reconstruct_path(came_from, goal)
			if (current.getX() == end.getX() && current.getY() == end.getY())	
				return reconstructPath(came_from,start,end);
			
        	//for each neighbor in neighbor_nodes(current)
			ArrayList<Point2D> validNeighbors = getNeighbors(current,m);
			for (Point2D neighbor : validNeighbors)
			{
				boolean contains = false;
				
				if (came_from[(int)(neighbor.getX() + neighbor.getY()*25)] != -2)
				{
					contains = true;
					if (DEBUG)
						System.out.println("contains duplicate");
				}
				
				if (!contains)
				{
					frontier.add(neighbor);
					//came_from.add(new tuple(neighbor, current));
					came_from[(int)(neighbor.getX() + neighbor.getY()*25)] = (int)(current.getX() + current.getY()*25);
					if (DEBUG)
						System.out.println(neighbor.toString() + " came from " + current.toString());
				}
			}
		}
		//return failure
		System.out.println("FAILURE");
		return null;
	}
	
	public Stack<Point2D> reconstructPath(int[] came_from, Point2D start, Point2D end)
	{
		Point2D current = end;
		//function reconstruct_path(came_from,current)
    	//total_path := [current]
		//ArrayList<Point2D> path = new ArrayList<Point2D>();
		Stack<Point2D> pathInv = new Stack<Point2D>();
		pathInv.add(current);
    	//while current in came_from:
		while (current != start && came_from[(int)(current.getX() + current.getY()*25)] != -1)
		{
        	//current := came_from[current]
			
			int temp = came_from[(int)(current.getX() + current.getY()*25)];
			int x = temp % 25;
			int y = temp / 25;
			//System.out.println(current.toString() + " came from " + new Point2D.Double(x,y).toString());
			current = new Point2D.Double(x,y);
			pathInv.add(current);
        	//total_path.append(current)
			
		}
    	//return total_path
		//System.out.println("Path: ");
		//while (!pathInv.isEmpty())
		//{
			//System.out.print(p.toString());
			//System.out.print(" -> ");
			//path.add(pathInv.pop());
		//}
		//System.out.println("Path: ");
		//for (Point2D p : pathInv)
		//{
		//	System.out.print(p.toString());
		//	System.out.print(" -> ");
		//}
		
		return pathInv;
	}
	
	public ArrayList<Point2D> getNeighbors(Point2D point, map m)
	{
		ArrayList<Point2D> neighbors = new ArrayList<Point2D>();
		for (double xX = point.getX()-1; xX <= point.getX()+1;xX++)
		{
			for (double yY = point.getY()-1; yY <= point.getY()+1;yY++)
			{
				//System.out.println("checking: " + xX + "," + yY);
				if ((point.getX() != xX || point.getY() != yY) && xX >= 0 && xX < 25 && yY >= 0 && yY < 25)//and in bounds
				{
					neighbors.add(new Point2D.Double(xX,yY));
				}
			}
		}
		//check for illegal points! (scan actor list and see if conflict)
		for (Actor a : m.actors)
		{
			//if (!a.isEdible())
			//{
				int[] coor = a.getXY();
				Point2D illegal = new Point2D.Double(coor[0], coor[1]);
				if (neighbors.remove(illegal))
				{
					//System.out.println("removed");
				}
			//}
		}
		//System.out.println("Neighbors: ");
		//for (Point2D p : neighbors)
		//{
		//	System.out.print(p.toString());
		//	System.out.println(" and ");
		//}
		return neighbors;
	}
	
}
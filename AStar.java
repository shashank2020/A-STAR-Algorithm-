import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javafx.geometry.Point2D;
import java.util.Comparator;

class AStar
{
    double sqr_t = Math.sqrt(2);
    //2d array to store the map
    char [][] map;
    Point2D S;
    Point2D G;
    ArrayList<State> frontier;
    Boolean found = false;
    public static void main(  String [] args)
    {
        //check if a map was provided
        if(args.length!=1)
        {
            System.out.println("No Map Provided!");
            System.exit(0);
        }
         File n = new File(args[0]);
         AStar astar = new AStar();
        astar.initialize(n);
    }
    public void initialize( File file)
    {
        Readmap(file);
    }
    //function to read a map, if map is invalid terminates the program
    public void Readmap( File file)
    {
        try{
            int n =1;
            Scanner sc = new Scanner(file);
            char[] outline = (sc.nextLine()).toCharArray();
            String line="";
            if(outline[0]=='+' && outline[outline.length-1]=='+')
            {
                
                System.out.println("first line");
                while(sc.hasNextLine())
                {
                    n++;
                    line=sc.nextLine();
                    outline = line.toCharArray();
                }
                if(outline[0]=='+' && outline[outline.length-1]=='+')
                {
                    map = new char[n][outline.length];
                    System.out.println("last line");
                }
                else
                {
                    error();
                }
            }
            else
            {
                error();
            }
            sc.close();

            //read the map into the 2d array
            sc= new Scanner(file);
            char[] map_line;
            for(int i=0;sc.hasNextLine();i++)
            {
                line = sc.nextLine();
                map_line = line.toCharArray();
                for(int j=0;j<map_line.length;j++)
                {
                    map[i][j] = map_line[j];
                }
            }
            //locate the start and goal coordinates
            locateSG();
            System.out.println("S is at "+S.toString());
            System.out.println("G is at "+G.toString());
            //Search();
            printmap();
            Search();
        }
        catch( Exception ex)
        {
            System.out.println(ex.getStackTrace()[0].getLineNumber());
        }
    }
    public void printF()
    {
        for (State s :frontier) {
            System.out.println(s.x()+" , "+s.y());
            
        }
    }

    //methid serches for the optimal path
    public void Search()
    {   
        try{
        int index=-1;
        //initialise the start state and add it to the frontier 
        State start = new State(S);
        frontier = new ArrayList<State>();
        frontier.add(start);
        int f = 0;

        // //while loop that runs till the goal is found
        while(!found)
        {
           //CHECK
            index = getNextState();
            int x = frontier.get(index).x();
            int y = frontier.get(index).y();
            if(map[y][x]=='G')
            {
                found = true;
               break;
            }
            else
            {
                State s = frontier.get(index);
                frontier.remove(index);
                expand(s);
            }
           //EXPAND
        }
        if(found)
        {
            System.out.println("found");
            printSolution(frontier.get(index));
        }
        else{
            System.out.println("not found");
        }
    }
    catch(Exception e)
    {
        System.out.println(e.getStackTrace()[0].getLineNumber());
        System.out.println(e);
    }
    }
    public void expand(State state)
    {
        State s = state;
        int x = s.x();
        int y = s.y();
        State b;

        if(y+1 < map.length-1 && map[y+1][x]!='X' && map[y+1][x]!='S' && (s.path.indexOf(new Point2D(x, y+1))==-1) && y+1<=map.length-1)
        {
            b= new State(s.path, new Point2D(x, y+1));
            frontier.add(b);
            collisionCheck(b);
        }
        if(y-1 > 0 && map[y-1][x]!='X'  && map[y-1][x]!='S' && (s.path.indexOf(new Point2D(x, y-1))==-1) && y-1>=0 )
        {
            b=new State(s.path, new Point2D(x, y-1));
            frontier.add(b);
            collisionCheck(b);
        }
        if(x+1 < map[0].length-1 && map[y][x+1]!='X'  && map[y][x+1]!='S' && (s.path.indexOf(new Point2D(x+1, y))==-1) && x+1 <= map[y].length-1 )
        {
            b=new State(s.path, new Point2D(x+1, y));
            frontier.add(b);
            collisionCheck(b);
        }
        if(x-1>0 && map[y][x-1]!='X'  && map[y][x-1]!='S' && (s.path.indexOf(new Point2D(x-1, y))==-1) && x-1>=0)
        {
            b= new State(s.path, new Point2D(x-1, y));
            frontier.add(b);
            collisionCheck(b);
        }
        //diag right down
        if(y+1 < map.length-1 && x+1 < map[0].length-1 && map[y+1][x+1]!='X' && map[y+1][x+1]!='S' &&  (s.path.indexOf(new Point2D(x+1, y+1))==-1) )
        {
            b= new State(s.path, new Point2D(x+1, y+1));
            frontier.add(b);
            collisionCheck(b);
        }
        //diag  left down
        if( y+1 < map.length-1 && x-1 > 0 && map[y+1][x-1]!='X' && map[y+1][x-1]!='S' && (s.path.indexOf(new Point2D(x-1, y+1))==-1) )
        {
            b= new State(s.path, new Point2D(x-1, y+1));
            frontier.add(b);
            collisionCheck(b);
        }
        //diag left up
        if(y-1 > 0 && x-1 > 0 && map[y-1][x-1]!='X' && map[y-1][x-1]!='S' && (s.path.indexOf(new Point2D(x-1, y-1))==-1) )
        {
            b= new State(s.path, new Point2D(x-1, y-1));
            frontier.add(b);
            collisionCheck(b);
        }
        //diag right up
        if(y-1 > 0 && x+1 < map[0].length-1 && map[y-1][x+1]!='X' && map[y-1][x+1]!='S' &&  (s.path.indexOf(new Point2D(x+1, y-1))==-1) )
        {
            b= new State(s.path, new Point2D(x+1, y-1));
            frontier.add(b);
            collisionCheck(b);
        }
    }
   
   //gets the next state with the least f value
    public int getNextState()
    {
        State s  = Collections.min(frontier,Comparator.comparing(a -> a.heuristic));
        return frontier.indexOf(s);
    }

    //checks if 2 states converge at a point
    public void collisionCheck(State s)
    {State x;
        for(int i=0;i<frontier.size();i++)
        {
            x = frontier.get(i);
            if(!s.equals(x))
            {
                if(s.x()==x.x()&&s.y()==x.y())
                {
                    if(s.moves<=x.moves)
                    {
                        frontier.remove(x);
                        return;
                    }
                    else
                    {
                        frontier.remove(s);
                        return;
                    }
                }
            }
        }
    }
    public void error()
    {
            System.out.println("Invalid Map");
            System.exit(0);
    }
    public void printmap()
    {
        for(int i=0;i<map.length;i++)
        {
            char[] a = map[i];
            for(int j=0;j<a.length;j++)
            {
                System.out.print(a[j]);
            }
            System.out.println();
        }
    }

    //loops through the map and gets the coordinates of the start and goal position
    public void locateSG()
    {
        for(int i=0;i<map.length;i++)
        {
            for(int j=0;j<map[0].length;j++)
            {
                if(map[i][j]=='S')
                {
                    S = new Point2D(j, i);
                }
                else if(map[i][j]=='G')
                {
                   G = new Point2D(j,i);
                }
            }
        }
    }
    public void printSolution(State s)
    {
        for(Point2D p : s.path)
        {
            if(map[(int)p.getY()][(int)p.getX()]!='S' && map[(int)p.getY()][(int)p.getX()]!='G')
            map[(int)p.getY()][(int)p.getX()] = '*';
        }
        System.out.println("moves = "+s.moves);
        printmap();
    }

    //inner class for each state
     class State 
    {
        //list to store the coords
        ArrayList<Point2D> path ;
        double moves=0;
        double heuristic;
        public State( Point2D p )
        {
            path = new ArrayList<>();
            path.add(p);

            heuristic = Heuristic(this);
        }
        public State( ArrayList<Point2D> pat, Point2D p)
        {
            path = new ArrayList<>(pat);
            path.add(p);
            for(int i=1;i<path.size();i++)
            {
                if(Math.abs(path.get(i).getX()-path.get(i-1).getX())==Math.abs(path.get(i).getY()-path.get(i-1).getY()))
                 moves+=Math.sqrt(2);
                 else
                 moves+=1;
            }
            heuristic = Heuristic(this);
        }
        public int x()
        {
            return (int)(path.get(path.size()-1)).getX();
        }
        public int y()
        {
            return (int)path.get(path.size()-1).getY();
        }
        public double Heuristic( State currs)
        {
             double cost = moves;
             double dist = Math.hypot(G.getX()- currs.path.get(currs.path.size()-1).getX(), G.getY()-currs.path.get(currs.path.size()-1).getY());
            return cost+dist;
        }
    }
}
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
            System.out.println(ex);
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
             //System.out.println(map[y][x]);
            if(map[y][x]=='G')
            {
                found = true;
               break;
            }
            else
            {
                //System.out.println("Expanding");
                State s = frontier.get(index);
                frontier.remove(index);
                // f++;
                // if(f>1000000)
                // {
                //     f=0;
                //     System.out.println(frontier.size());
                // }
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
        System.out.println(e.getCause());
    }
    }

    public void expand(State state)
    {
        State s = state;
        int x = s.x();
        int y = s.y();
        State b;;
        if(map[y+1][x]!='X' && map[y+1][x]!='-' && map[y+1][x]!='S' && (s.path.indexOf(new Point2D(x, y+1))==-1) && y+1<=map.length)
        {
            b= new State(s.path, new Point2D(x, y+1));
            frontier.add(b);
            collisionCheck(b);
            // map[y+1][x]='c';
            //System.out.println("point added under");
        }
        if(map[y-1][x]!='X' && map[y-1][x]!='-' && map[y-1][x]!='S' && (s.path.indexOf(new Point2D(x, y-1))==-1) && y-1>=0 )
        {
            b=new State(s.path, new Point2D(x, y-1));
            frontier.add(b);
            collisionCheck(b);
           // map[y-1][x]='c';
            //System.out.println("point added on top");
        }
        
        if(map[y][x+1]!='X' && map[y][x+1]!='|' && map[y][x+1]!='S' && (s.path.indexOf(new Point2D(x+1, y))==-1) && x+1 <= map[y].length )
        {
            b=new State(s.path, new Point2D(x+1, y));
            frontier.add(b);
            collisionCheck(b);
           // map[y][x+1]='c';
            //System.out.println("point added to RIGHT "+(x+1)+" "+y);
        }
        if(map[y][x-1]!='X' && map[y][x-1]!='|' && map[y+1][x-1]!='S' && (s.path.indexOf(new Point2D(x-1, y))==-1) && x-1>=0)
        {
            b= new State(s.path, new Point2D(x-1, y));
            frontier.add(b);
            collisionCheck(b);
           // map[y+1][x-1]='c';
            //System.out.println("point added to left");
        }
       
        
       
    }
   
    //method interates through the frontier and returns the index of the state with the lowest f value 
    //break after the first if to get a non optimal path quickly
    public int getNextState()
    {
        // int n=0;
        // for(int i=1;i<frontier.size();i++)
        // {
        //     if(frontier.get(i).heuristic<=frontier.get(n).heuristic)
        //     {
        //         n=i;
        //     }
        // }
        // return n;
        State s  = Collections.min(frontier,Comparator.comparing(a -> a.heuristic));

        return frontier.indexOf(s);
    }
    public void collisionCheck(State s)
    {State x;
        for(int i=0;i<frontier.size();i++)
        {
            x = frontier.get(i);
            if(!s.equals(x))
            {
                if(s.x()==x.x()&&s.y()==x.y())
                {
                    if(s.path.size()<x.path.size())
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
        System.out.println("moves = "+(s.path.size()-1));
        printmap();
    }

    //inner class for each state
     class State 
    {
        //list to store the coords
        ArrayList<Point2D> path ;
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
             int cost = currs.path.size();
             double dist = Math.sqrt(Math.pow(Math.abs(G.getX()- currs.path.get(currs.path.size()-1).getX()),2) + Math.pow(Math.abs(G.getY()-currs.path.get(currs.path.size()-1).getY()),2));
            return cost+dist;
        }

     
        
    

    }


}
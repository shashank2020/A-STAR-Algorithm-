import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.geometry.Point2D;


class AStar
{
    //2d array to store the map
    char [][] map;
    Point2D S;
    Point2D G;
    ArrayList<State> frontier;
    public static void main(String [] args)
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
    public void initialize(File file)
    {
        Readmap(file);
    }
    //function to read a map, if map is invalid terminates the program
    public void Readmap(File file)
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
            
        }
        catch(Exception ex)
        {
            System.out.println(ex);
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
    //inner class for each state
    class State
    {
        //list to store the coords
        ArrayList<Point2D> path = new ArrayList<Point2D>();
    }
}
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

class Vertex{
    private final String exp;
    private final LinkedList<Edge> edgeList;
    private boolean visited;
    public Vertex(final String exp){
        this.exp = exp;
        edgeList = new LinkedList<>();
        visited = false;
    }
    public String getExp(){
        return exp;
    }
    public LinkedList<Edge> getEdges(){
        return edgeList;
    }
    public void visit() {
        visited = true;
    }
    public boolean isVisited(){
        return visited;
    }
}

class Edge{
    private final Vertex destVertex;
    public Edge(final Vertex dest){
        this.destVertex = dest;;
    }
    public Vertex getDestVertex(){
        return destVertex;
    }
}

class UninformedSearch{
    private final HashSet<Vertex> nodes;
    public static void main(final String[] args) {
        final Double value = Double.parseDouble(args[0]);
        final Vertex start = new Vertex("4");
        String found = search(start, value);
        System.out.println(found);
    }
    public UninformedSearch(){
        nodes = new HashSet<>();
    }
    public void AddEdge(final Vertex v1, final Vertex v2){
        v1.getEdges().add(new Edge(v2));
    }
    public void AddVertex(final Vertex v){
        nodes.add(v);
    }
    public void expand(Vertex v){
        String exp = v.getExp();
        Vertex v1 = new Vertex(exp + "+4");
        Vertex v2 = new Vertex(exp + "-4");
        Vertex v3 = new Vertex(exp + "*4");
        Vertex v4 = new Vertex(exp + "/4");
        if(eval(exp + "4") != Double.NaN){
            Vertex v6 = new Vertex(exp + "4");
            this.AddEdge(v, v6);
        }
        if(eval(exp + ".4") != Double.NaN){
            Vertex v7 = new Vertex(exp + ".4");
            this.AddEdge(v, v7);
        }
        if(eval("(" + exp + ")") != Double.NaN){
            Vertex v8 = new Vertex("(" + exp + ")");
            this.AddEdge(v, v8);
        }
        this.AddEdge(v, v1);
        this.AddEdge(v, v2);
        this.AddEdge(v, v3);
        this.AddEdge(v, v4);
        
    }
    private static String search(Vertex start, Double value) {
        UninformedSearch graph = new UninformedSearch();
        graph.AddVertex(start);
        Deque<Vertex> q = new LinkedList<>();
        q.add(start);
        while(q.isEmpty() != true){
            Vertex x = q.remove();
            Double evaluated = eval(x.getExp());
            if(evaluated.equals(value)){
                return x.getExp();
            }
            graph.expand(x);
            if(x.isVisited() != true){
                x.visit();
                for (Edge edge : x.getEdges()) {
                    if(edge.getDestVertex().isVisited() != true){
                        q.add(edge.getDestVertex());
                    }
                }
            }
        }
        return "This should never return";

    }

    public static double eval(final String str) {
        try{
            return new Object() {
                int pos = -1, ch;
        
                void nextChar() {
                    ch = (++pos < str.length()) ? str.charAt(pos) : -1;
                }
        
                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }
        
                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                    return x;
                }
        
                // Grammar:
                // expression = term | expression `+` term | expression `-` term
                // term = factor | term `*` factor | term `/` factor
                // factor = `+` factor | `-` factor | `(` expression `)`
                //        | number | functionName factor | factor `^` factor
        
                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if      (eat('+')) x += parseTerm(); // addition
                        else if (eat('-')) x -= parseTerm(); // subtraction
                        else return x;
                    }
                }
        
                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if      (eat('*')) x *= parseFactor(); // multiplication
                        else if (eat('/')) x /= parseFactor(); // division
                        else return x;
                    }
                }
        
                double parseFactor() {
                    if (eat('+')) return parseFactor(); // unary plus
                    if (eat('-')) return -parseFactor(); // unary minus
        
                    double x;
                    int startPos = this.pos;
                    if (eat('(')) { // parentheses
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else if (ch >= 'a' && ch <= 'z') { // functions
                        while (ch >= 'a' && ch <= 'z') nextChar();
                        String func = str.substring(startPos, this.pos);
                        x = parseFactor();
                        if (func.equals("sqrt")) x = Math.sqrt(x);
                        else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                        else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                        else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                        else throw new RuntimeException("Unknown function: " + func);
                    } else {
                        throw new RuntimeException("Unexpected: " + (char)ch);
                    }
        
                    if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
        
                    return x;
                }
            }.parse();
    }
    catch(Exception ex){
        return Double.NaN;
    }
    }

}

 
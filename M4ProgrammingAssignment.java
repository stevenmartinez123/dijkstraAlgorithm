import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;   
import java.io.FileNotFoundException;
import java.io.IOException; 
import java.io.File; 
import java.util.List; 
import java.io.FileWriter; 
import java.io.PrintWriter; 
import java.util.LinkedList; 
import java.util.ArrayList;   
import java.util.Queue;
import java.util.PriorityQueue; 
import java.util.Comparator; 
import java.util.Arrays; 
import java.util.Collections; 

/**
* Allow a user to provide a graph and a source vertex as a text file
* Display the shortest path for source to every other vertex in the graph.
* Output a file text describing the shortest path for 
* source to every other vertex in the graph. 
**/

public class M4ProgrammingAssignment {

/**. 
* Main Method - Prompts the user to enter a text file
* output will be the shortest path for every vertex in graph 
* @param args used-none
* @throws IOException to read and write from Text File
**/

   public static void main(String[] args) throws IOException {
    
      System.out.print("Please enter a file: "); 
        
      try {
      
         Scanner scanner = new Scanner(System.in);
         String fileName = scanner.nextLine();
         File file = new File(fileName); 
         BufferedReader br = new BufferedReader(new FileReader(file));
         int source = Integer.parseInt(br.readLine()); 
         
        // Source of the graph 
         Graph graph = new Graph(source); 
         String line = ""; 
         String vertexU = "";
         String vertexV = "";  
         String weight = ""; 
         int index = 0;
         while ((line = br.readLine()) != null) {
         
         //if line is empty break out of loop 
            if (line.equals("")) { 
               break; 
            }
           //initializes vertexU 
            vertexU = line.charAt(0) + "";
           
           //if vertexU has no directed edges   
            if (line.length() <= 2) {
               graph.addEdge(vertexU, vertexU, "1000000000", index);
               index++; 
               continue; 
            }
         
         //creates an array delimited by comma   
            line = line.substring(2).replace(" ", ",");   
            String[] split = line.split(","); 
            
           //initalizes vertexV and weight
           //adds the edge to the graph 
           
            for (int i = 0; i < split.length; i += 2) {
               vertexV = "" + split[i]; 
               weight = "" + split[i + 1]; 
               graph.addEdge(vertexU, vertexV, weight, index); 
            } 
            index++; 
         }
                    
                              
         Dijkstra(graph, source); 
         
           
      } catch (FileNotFoundException e) {
      
         System.out.println("File not found");
         e.printStackTrace();
      } catch (IOException e) {
         System.out.println("An exception occured while reading the file");
         e.printStackTrace();
      }     
   }
   
  
   
   /**
   * performs Dijkstra's algorithm on a graph to 
   * find the shortest path of each vertex
   */
   
   private static void Dijkstra(Graph graph, int source) throws IOException {
      
     //creates array of vertices
      Vertex[] v = new Vertex[graph.adjacencylist.size()]; 
      
    //creates arraylist for sets edges in algorithm 
      ArrayList<Edge> path = new ArrayList<Edge>();  
      
      
      //for every vertex in graph, add to array v 
      //sets parent to -1 and distance to Max Value 
      //if source set distance to 0
      
      for (int i = 0; i < graph.adjacencylist.size(); i++) {
      
         int vertex = graph.adjacencylist.get(i).get(0).source; 
         v[i] = new Vertex(vertex);
         
         if (v[i].getVertex() == source) {
            v[i].setDistance(0);
         } else {
            v[i].setDistance(Integer.MAX_VALUE);
         }
         v[i].setParent(-1);                    
      }
      
      //creates a queue 
       
      Queue<Vertex> q = new LinkedList<Vertex>();
      
      for (int i = 0; i < graph.adjacencylist.size(); i++) {
         q.add(v[i]); 
      }
      
      //while q is not empty relax edges and add minumum path to set 
      
      while (!q.isEmpty()) {
      
         q = reorderQueue(q);
         Vertex u = q.remove();  
         relax(u.getVertex(), v, graph); 
         path.add(new Edge(u.getParent(), u.getVertex(), u.getDistance()));
      }
      //remove source, output min paths and print to text file 
      path.remove(0); 
      printPath(v, source);  
              
   }
   
   /**
   *reorders queue by distance to extract min element 
   **/
   
   private static Queue<Vertex> reorderQueue(Queue q) {
   
      Queue<Vertex> queue = new PriorityQueue<>(new DistanceComparator());
      Object[] a = q.toArray();  
   
      for (int i = 0; i < a.length; i++) {
         queue.add((Vertex) a[i]); 
      }
      
      return queue; 
       
   }
   
   /**
   * Relaxes edges in graph by weight and sets new parent 
   * once complete min vertex is set to visited 
   **/
   
   private static void relax(int u, Vertex[] v, Graph graph) {
   
      int i = findVertex(graph, u);
      int j = 0;  
      
      if (i < graph.adjacencylist.size()) {
         int length = 0;
          
         while (length < graph.adjacencylist.get(i).size()) {
         
            int vertexV = graph.adjacencylist.get(i).get(j).destination;
            int index = findVertex(graph, vertexV); 
            int weight = graph.adjacencylist.get(i).get(j).weight; 
            int totalCost = v[i].getDistance() + weight;
             
            if (index < v.length && v[index].getDistance() > totalCost 
            && (!v[index].visited)) {
               v[index].setDistance(totalCost); 
               v[index].setParent(u); 
            }
            length++;
            j++;  
         }
         
         v[i].setVisited(true); 
      } 
   
   }
   
   /**
   *prints path of every vertex to output and into a text file.
   *adds vertex and its parents to graph until it reaches the source 
   *reverses the arraylist to print the correct output  
   */
   
   private static void printPath(Vertex[] v, int target) throws IOException {
   
      try {
      
         File file1 = new File("outputShortestPaths.txt"); 
         FileWriter fw = new FileWriter(file1); 
         PrintWriter pw = new PrintWriter(fw); 
         
      //sort vertices
      
         Arrays.sort(v, new  VertexComparator());
      
      //creates an arraylist of each individual min path of each vertex 
         ArrayList<Integer> pathofV = new ArrayList<Integer>(); 
      
         for (int i = 0; i < v.length; i++) {
         
            if (v[i].getVertex() == target) {
               continue; 
            }
         
            System.out.print(v[i].getVertex() + ": ");
            pw.write(v[i].getVertex() + ": ");
          
            pathofV.add(v[i].getVertex());  
            int j = i; 
            while (v[j].getParent() != target) {
            
               pathofV.add(v[j].getParent()); 
               j = findVertex(v, v[j].getParent()); 
            }
         
            for (int index = pathofV.size() - 1; index >= 0; index--) {
               System.out.print(pathofV.get(index) + " "); 
               pw.write(pathofV.get(index) + " "); 
            }
         
            pathofV.clear(); 
            System.out.println(); 
            pw.write("\n"); 
         }
         
         pw.flush(); 
         pw.close(); 
         
      } catch (IOException e) {
         e.printStackTrace();
      }
   
   }
   
   /**
   *returns index of target vertex in the graph  
   **/
   
   private static int findVertex(Graph graph, int u) {
      
      int i = 0; 
      while (u != graph.adjacencylist.get(i).get(0).source) {
         i++; 
      }
         
      return i;  
         
   }
   
   /**
   *returns index of target vertex in array 
   **/
   
   private static int findVertex(Vertex[] v, int target) {
   
      int i = 0; 
   
      while (v[i].getVertex() != target) {
         i++; 
      } 
      return i; 
   
   }
   
   /**
   *comparator to sort queue by its distance 
   **/
   
   static class DistanceComparator implements Comparator<Vertex> {
   
      public int compare(Vertex o1, Vertex o2) {
         int i = o1.getDistance() > o2.getDistance() ? 1 : -1;
         return i; 
      }
   }
   
   /**
   *comparator to sort array by its vertex number  
   **/
   
   static class VertexComparator implements Comparator<Vertex> {
   
      public int compare(Vertex v1, Vertex v2) {
      
         int i = v1.getVertex() > v2.getVertex() ? 1 : -1;
         return i;
      
      }
   
   }
  
   /**
   *class to create edge  
   **/
   static class Edge {
   
      int source;
      int destination;
      int weight;
   
      public Edge(int source, int destination, int weight) {
         this.source = source;
         this.destination = destination;
         this.weight = weight;
      }
   }
   /**
   *class to create graph   
   **/
   static class Graph {
   
      int vertices;
      ArrayList<LinkedList<Edge>> adjacencylist;
   
      Graph(int vertices) {
         this.vertices = vertices; 
         adjacencylist = new ArrayList<LinkedList<Edge>>(vertices + 1);
         
      //initialize adjacency lists for all the vertices
         for (int i = 0; i <= vertices; i++) {
            adjacencylist.add(new LinkedList<>()); 
         }
      }
      
   /**
   *adds edge to graph 
   **/    
   
      private void addEdge(String source, String destination, String weight, int index) {
      
         int sourceNumber = Integer.parseInt(source); 
         int destinationNumber = Integer.parseInt(destination); 
         int weightNumber = Integer.parseInt(weight); 
         Edge edge = new Edge(sourceNumber, destinationNumber, weightNumber);
         
         if (index >= adjacencylist.size()) {
            adjacencylist.add(new LinkedList<>());  
         }
         adjacencylist.get(index).add(edge); 
      }
     
      
   }
   
   /**
   *class to create vertex   
   **/
   
   static class Vertex implements Comparable<Vertex> {
   
      int vertex = 0; 
      int distance = 0; 
      int parent = 0; 
      boolean visited = false; 
   
      public Vertex(int v) {
         this.vertex = v;
         visited = false; 
      }
      
      public int getDistance() {
         return this.distance; 
      }
      public void setDistance(int d) {
         this.distance = d; 
      }
     
      public void setParent(int p) {
         this.parent = p; 
      }
      
      public int getParent() {
         return this.parent; 
      }
      
      public void setVisited(boolean visit) {
         this.visited = visit; 
      }
      
      public int compareTo(Vertex o) {
      
         if (this.getDistance() == o.getDistance()) {
            return 0; 
         } else if (this.getDistance() > o.getDistance()) {
            return 1; 
         } else {
            return -1; 
         }
      
      }
      
      public int getVertex() {
         return this.vertex; 
      }
      
      public boolean equals(Vertex o) {
         return this.getDistance() == o.getDistance(); 
      }
   
   } 
    
}
   
         


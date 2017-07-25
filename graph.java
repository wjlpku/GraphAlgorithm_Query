package Query.memory;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import java.io.*;
import java.util.Random;


//import database.*;
import Query.Utils.*;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.*;

import org.apache.log4j.Logger;

import Query.database.*;



public class graph {

    Random rnd;
	private static final Logger LOG = Logger.getLogger(graph.class);
	
    public List<node> nodes = new ArrayList<node>();
    int size = 0;
    int thresh = 0;
    int remains = 0;

    ArrayList<Integer> targets;
    ArrayList<Integer> virtualNodes;
    public graph() {
        rnd = new Random(100);
    }


    private void generateNodeMemoryGraphXML(String line) {
        int first, second;
        //node firstNode=null, secondNode=null;
        int begin, end;

        if (line.indexOf("<node id=") >= 0) {
            begin = line.indexOf("\"", 0);

            end = line.indexOf("\"", begin + 1);
            first = Integer.parseInt(line.substring(begin + 2, end));

            //gaojun omit buffer
            //firstNode =buffer.getNodebyID(first,false);

            //if (firstNode ==null){
            // firstNode =new node(first);

            //databaseNode.insertIntoNode(database.sta, firstNode);

            databaseNode.insertIntoNode(database.sta, first + "");
            statistic.totalNodes++;
            //   buffer.put(first, firstNode);
            //}
        }

        if (line.length() > 0) {
            char firstChar = line.charAt(0);
            if (java.lang.Character.isDigit(firstChar)) {
                end = line.indexOf(" ", 0);
                first = Integer.parseInt(line.substring(0, end));
                databaseNode.insertIntoNode(database.sta, first + "");

                begin = line.indexOf(" ", end + 1);
                //end   = line.indexOf(" ", begin+1);
                if (begin < 0) {
                    begin = line.indexOf("\t", end + 1);
                }
                first = Integer.parseInt(line.substring(end, begin).trim());
                System.out.println(line);
                databaseNode.insertIntoNode(database.sta, first + "");
                statistic.totalNodes++;
            }
        }

    }

    private void generateNodeMemoryGML(String line) {
        int first, second;
        //node firstNode=null, secondNode=null;
        int begin, end;
        edge currentedge;
//        node [
//          id 1487
//          label "zebrax.blogs.com"
//          value 1
//          source "BlogCatalog"
//  ]
        if (line.indexOf("id") > 0) {
            //    id 15343
            begin = line.indexOf("id", 0);

            end = line.length();
            first = Integer.parseInt(line.substring(begin + 3, end));

            //gaojun omit buffer
            //firstNode =buffer.getNodebyID(first,false);

            //if (firstNode ==null){
            // firstNode =new node(first);

            //databaseNode.insertIntoNode(database.sta, firstNode);

            databaseNode.insertIntoNode(database.sta, first + "");
            statistic.totalNodes++;
            //   buffer.put(first, firstNode);
            //}
        }

    }


    private void generateEdgeMemoryGraphXML(String line) {
        int first, second;
        //node firstNode=null, secondNode=null;
        int begin, end;
        edge currentedge;

        if (line.indexOf("<edge source=") > 0) {
            begin = line.indexOf("\"", 0);

            end = line.indexOf("\"", begin + 1);
            first = Integer.parseInt(line.substring(begin + 2, end));

            begin = line.indexOf("\"", end + 1);
            end = line.indexOf("\"", begin + 1);
            second = Integer.parseInt(line.substring(begin + 2, end));

            int cost = rnd.nextInt(20);
            if (cost == 0) {
                cost = 1;
            }
            databaseEdge.insertIntoEdge(database.sta, first + "", second + "",
                                        cost + "");
            statistic.totalEdges++;

            System.out.println(first + " to " + second + " len=" + cost);
            if (statistic.totalEdges % 1000 == 0) {
                System.out.println("output the edge with " +
                                   statistic.totalEdges);
            }
            /*secondNode =buffer.getNodebyID(second,false);
                   if (secondNode ==null){
              statistic.totalNodes++;
              secondNode =new node(second);
              buffer.put(second, secondNode);

                   }*/

        }

        if (line.length() > 0) {
            char firstChar = line.charAt(0);
            if (java.lang.Character.isDigit(firstChar)) {
                end = line.indexOf(" ", 0);
                first = Integer.parseInt(line.substring(0, end));

                begin = line.indexOf(" ", end + 1);
                if (begin < 0) {
                    begin = line.indexOf("\t", end + 1);
                }

                second = Integer.parseInt(line.substring(end, begin).trim());
                int cost = rnd.nextInt(100);
                if (cost == 0) {
                    cost = 1;
                }

                databaseEdge.insertIntoEdge(database.sta, first + "",
                                            second + "", cost + "");
                statistic.totalNodes++;
            }
        }

    }

    private void generateEdgeMemoryGML(String line) {
        int first, second;
        //node firstNode=null, secondNode=null;
        int begin, end;
        double cost;
        edge currentedge;
        // edge  [ source 10416 target 435  value 0.0227273 ]
        if (line.indexOf("edge") >= 0) {
            begin = line.indexOf("source", 0);

            end = line.indexOf("target", begin + 1);
            if (begin < 0 || end < 0) {
                System.out.print(line);
                return;
            }

            first = Integer.parseInt(line.substring(begin + 6, end).trim());

            begin = line.indexOf("target", 0);
            end = line.indexOf("value", 0);
            int icost = 0;
            if (end != -1) {
                second = Integer.parseInt(line.substring(begin + 6, end).trim());
                begin = line.indexOf("value", 0);
                //end = line.indexOf("]", 0);
                end = line.length();
                try {
                    cost = Double.parseDouble(line.substring(begin + 5, end).
                                              trim()) * 100;
                    icost = (int) cost;
                } catch (Exception e1) {
                    icost = rnd.nextInt(100);
                    if (icost == 0) {
                        icost = 1;
                    }
                }

            } else {
                end = line.indexOf("]", 0);
                second = Integer.parseInt(line.substring(begin + 6, end).trim());
                icost = rnd.nextInt(100);
                if (icost == 0) {
                    icost = 1;
                }
                //       icost =1;
            }
            databaseEdge.insertIntoEdge(database.sta, first + "", second + "",
                                        icost + "");
            statistic.totalEdges++;
            if (statistic.totalEdges % 1000 == 0) {
                System.out.println("output the edge with " +
                                   statistic.totalEdges);
            }

        }

    }


    public int getTotalNode() {
        //return statistic.totalNodes;
        return this.nodeNum() ;
    }

    public int getTotalEdge() {
        return statistic.totalEdges;
    }


    public void ConstructGraph(String fileName, String type) {
        String XPathFile = fileName;
        File file = new File(XPathFile);

        try {
            // Create an BufferedReader so we can read a line at the time.
            //initBuffer("./buffer");
            BufferedReader reader = new BufferedReader(new FileReader(XPathFile));
            String inLine = reader.readLine();
            int lineCount = 0;
            while (inLine != null) {
                if (type.equalsIgnoreCase("graphml") && inLine.length() > 0 &&
                    inLine.charAt(inLine.length() - 1) != '*') { //our system does not deal with the * in the last

                    this.generateNodeMemoryGraphXML(inLine);
                    this.generateEdgeMemoryGraphXML(inLine);

                }
                if (type.equalsIgnoreCase("gml") && inLine.length() > 0 &&
                    inLine.charAt(inLine.length() - 1) != '*') { //our system does not deal with the * in the last
                    if (inLine.trim().equals("node [")) {
                        inLine = reader.readLine();
                        this.generateNodeMemoryGML(inLine);
                    }

                    // edge
                    //[
                    //source 10416
                    //target 435
                    //value 0.0227273
                    //]
//                    if (inLine.trim().equalsIgnoreCase("edge")) {
//                        String inLine1;
//                        for (int i = 0; i < 4; i++) {
//                            inLine1 = reader.readLine();
//                            inLine = inLine + inLine1;
//                        }
//                    }
                    // edge [
                    //source 10416
                    //target 435
                    //]
                    if (inLine.trim().equalsIgnoreCase("edge [")) {
                        String inLine1;
                        for (int i = 0; i < 3; i++) {
                            inLine1 = reader.readLine();
                            inLine = inLine + inLine1;
                        }
                    }

                    this.generateEdgeMemoryGML(inLine);

                }

                inLine = reader.readLine();
                lineCount++;
                if (lineCount % 100 == 0) {
                    Debug.print(4, "the line count is " + lineCount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.out.println("file open error");
        }
    }

    public int nodeNum() {
        return size;
    }

    public graph(int size1) {
        for (int i = 0; i < size1; i++) {
            nodes.add(new node(i));
        }
        size = size1;
    }
    
    //according to the qid to build node by path, ascending sort, if queryid<qid find next , if queryid>qid break
	public void constructNodeFromFile(String path, int qid) {
		// TODO Auto-generated method stub
		try{
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = "";
			node cnode;
			while((line=reader.readLine())!=null){
				String[] nums= line.split(" ");
				int queryid = Integer.parseInt(nums[0]);
				if( queryid < qid)
					continue;
				if( queryid > qid)
					break;
				int num = Integer.parseInt(nums[1]);
				String label = nums[2];
               cnode = new node(num);
               cnode.label = label;
               nodes.add (cnode);
//               System.out.println(cnode.label);
//               LOG.info("node "+ num+" "+label);
			}		
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void constructEdgeFromFile(String path, int qid) {
		// TODO Auto-generated method stub
		try{
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = "";
		node cnode;
		while((line=reader.readLine())!=null){
				String[] nums= line.split(" ");
				int queryid = Integer.parseInt(nums[0]);
				if( queryid < qid)
					continue;
				if( queryid > qid)
					break;
				int from = Integer.parseInt(nums[1]);
				int to = Integer.parseInt(nums[2]);
//				if(from == to)
//					continue;
				String type = nums[3];
				cnode = nodes.get(from);
				cnode.addEdge(nodes.get(to), type);
				System.out.println("inputq "+cnode.id+" "+nodes.get(to).id+" "+type+" "+cnode.getTotalEdge());
//				LOG.info("edge "+ from+" "+to);
		}
		reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
    
	public void constructEdgsFromDB(Statement sta, String prefix, String condition) {
        try {
            String SQLState = " select fromnode, tonode, cost from " + prefix +
                              "edge";
            if (condition.length()!=0){
                SQLState = SQLState + " where " + condition;
            }

            ResultSet rs;
            try {
                rs = sta.executeQuery(SQLState);
            } catch (Exception e1) {
                database.restartConnection();
                rs = database.sta.executeQuery(SQLState);
            }
            String fromID, toID;
            int cost;
            node cnode;
            while (rs.next()) {
                fromID = rs.getString("fromNode").trim();
                toID = rs.getString("toNode").trim();
                cost = rs.getInt("cost");
                if (cost == 0) {
                    cost = 1;
                }
                Integer ID = new Integer(fromID.trim());
                cnode = nodes.get(ID.intValue());
                ID = new Integer(toID.trim());
                cnode.addEdge(nodes.get(ID.intValue()), ""+cost);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void constructNodeFromDB(Statement sta, String prefix, String condition) {
        try {
            String SQLState = " select nodename, nodetype from " + prefix +
                              "node";
            if (condition.length()!=0){
                SQLState = SQLState + " where "+condition;
            }
            SQLState = SQLState +" order by nodename";
            ResultSet rs;
            try {
                rs = sta.executeQuery(SQLState);
            } catch (Exception e1) {
                database.restartConnection();
                rs = database.sta.executeQuery(SQLState);
            }
            int nodename, nodetype;
            node cnode;
            while (rs.next()) {
                nodename = rs.getInt("nodename");
                nodetype = rs.getInt("nodetype");
                cnode = new node(nodename);
                cnode.label = ""+nodetype;
                nodes.add (cnode);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }


    /**
     * return the neighbor node of fromID
     * @param fromID String
     * @return List
     */
    public List<edge> getOutEdge(int fromID) {
        //Integer ID = new Integer(fromID);
        node cnode = nodes.get(fromID);
        return cnode.getOutEdgesInGraph();
    }

    public List<edge> getInEdge(int toID) {
        //Integer ID = new Integer(fromID);
        node cnode = nodes.get(toID);
        return cnode.getInEdgesInGraph();
    }


    public void printNodesAndEdges() {
        int cnt = 0;
        node dnode;
        String content = " the size of the nodes " + nodes.size();
        for (int i = 0; i < nodes.size(); i++) {
            dnode = nodes.get(i);
            cnt = cnt + dnode.getOutEdgesInGraph().size();
        }
        content = content + " the edge between nodes " + cnt;
        System.out.print(content);
    }


//    public void storeIntoDatabase() {
//        databaseEdge.clearEdge(database.sta, "h9wpi");
//        node dnode;
//        for (int i = 0; i < nodes.size(); i++) {
//            dnode = nodes.get(i);
//            databaseEdge.addIntoEdge(database.sta, "h9wpi",
//                                     dnode.getOutEdgesInGraph());
//        }
//    }


    /**
     * find whether the original graph is directly connected, time cost o(n) 
     * @param fromID int
     * @param toID int
     * @return boolean
     */
    public boolean isDirectlyConnected(int fromID, int toID) {
        node dnode = nodes.get(fromID);
        edge toEdge = dnode.getOutEdgeToNodeInGraph(toID);
        if (toEdge == null) {
            return false;
        } else {
            return true;
        }

    }

    public int getDirectCost(int fromID, int toID) {
        node dnode = nodes.get(fromID);
        edge toEdge = dnode.getOutEdgeToNodeInGraph(toID);
        if (toEdge == null) {
            return -1;
        } else {
            return toEdge.distance;
        }
    }


    public List<node> getNodes() {
        return nodes;
    }

    public node getNodeById(int id) {
        if (nodes == null) {
            return null;
        }
        for(int i = 0;i<nodes.size();i++)
        	if(nodes.get(i).id == id)
        		return nodes.get(id);
        return null;
    }










    public void addVirtualNode(int vid) {
        if (this.virtualNodes == null) {
            this.virtualNodes = new ArrayList<Integer>();
        }
        this.virtualNodes.add(vid);
    }












}

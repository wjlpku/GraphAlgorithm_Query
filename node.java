package Query.memory;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.io.Serializable;
import java.util.*;

import Query.Utils.*;


public class node  {

    //unique int id for one node
    public int id;
    //
    private List<edge> outEdgesInGraph = new ArrayList<edge>();
    private List<edge> inEdgesInGraph = new ArrayList<edge>();
    //private List<edge> edgesInSPT=new  ArrayList<edge>();


//    public int cost = GConstant.UnReach; //cost for the DJ algorithm

    public String label="";//模拟节点标签
    
    public double predictedNums=Double.MAX_VALUE;

    public int queryID=0;//产生Query的时候的中间结果

    public int VID=-2; //不要和一些数据冲突，比如id=0的数据

    public int level =-1; //用于产生query 起点的标志

//    List<EvaluationNode> evlNodes;//和这个节点相关的evalutionNode; 相关是有对应的出边

  
    int beginClock=0;//初始生存时钟

    boolean hasNewMessage=false;//

    static int CURRENTID=0;
    
    boolean queryRoot=false;
    
    boolean carryDid = false;
    double num=0;
    double hopcost=0;
    double pathcost=1;
    public boolean selfCircle = false;
    int frequent=0;
    int lasteid=-1;
    
    public node(int id1) {
        id = id1;
        label = this.generateType();
//        evlNodes = new ArrayList<EvaluationNode>();
    }
    
    public node (){
    	id = CURRENTID;
    	CURRENTID++;
    }

    /*public void setNextNodeInList(int nextNodeInList1) {
        nextNodeInList = nextNodeInList1;
         }

         public int getNextNodeInList() {
        return nextNodeInList;
         }*/



    public void addOutEdgeIntoGraph(edge edge1) {
        if (!outEdgesInGraph.contains(edge1)) {
            outEdgesInGraph.add(edge1);
        }
    }

    public HashSet getRelatedNodeTypes(){
        HashSet<Integer> types = new HashSet<Integer>();
        edge cedge;
        for (int i=0;i<outEdgesInGraph.size();i++){
            cedge = outEdgesInGraph.get(i);
            types.add(new Integer(cedge.toNode.label));
        }
        //就是要考虑不需要出边的情况，这种情况下是终止条件
//        types.add(new Integer(GConstant.virtualLabel));
        return types;
    }

    public void addInEdgeIntoGraph(edge edge1) {
        if (!inEdgesInGraph.contains(edge1)) {
            inEdgesInGraph.add(edge1);
        }
    }
  

    /*public void addEdgeIntoSPT(edge edge1){
        if (!edgesInSPT.contains(edge1)) {
            edgesInSPT.add(edge1);
        }

         }*/

    public void addEdge(node toNode1, String type) {
        edge newEdge = new edge();
        newEdge.fromNode = this;
        newEdge.toNode = toNode1;
        newEdge.type = type;
        this.addOutEdgeIntoGraph(newEdge);
        newEdge = new edge();
        newEdge.fromNode = toNode1;
        newEdge.toNode = this;
        newEdge.type = "-"+type;
        toNode1.addOutEdgeIntoGraph(newEdge);
        //outEdgesInGraph.add(newEdge);
        //toNode1.inEdgesInGraph.add(newEdge);

    }

    public String generateType(){
        int range =20;
        Random r1 = new Random(10);
        return ""+(r1.nextInt() % range);

    }


    public edge addChildNode(node childNode) {
        edge edge1 = new edge();
        edge1.fromNode = this;
        edge1.toNode = childNode;
        //edge1.fromNodeID = this.id;
        //edge1.toNodeID = childNode.id;

        this.addOutEdgeIntoGraph(edge1);
        childNode.addInEdgeIntoGraph(edge1);
        return edge1;
    }
    
    public edge addChildNode(node childNode, String type) {
        edge edge1 = new edge();
        edge1.fromNode = this;
        edge1.toNode = childNode;
        edge1.type = type;
        //edge1.fromNodeID = this.id;
        //edge1.toNodeID = childNode.id;

        this.addOutEdgeIntoGraph(edge1);
        childNode.addInEdgeIntoGraph(edge1);
        return edge1;
    }

    public void removeOutEdgeInGraph(edge edge1) {
        boolean moved = outEdgesInGraph.remove(edge1);
        if (!moved) {
            System.out.println("error in moving edge");
        }
    }

    /*public void removeEdgeInSPT(edge edge1){
        boolean moved = edgesInSPT.remove(edge1);
        //System.out.println("removed edge is "+edge1.fromNode.id+"  "+edge1.toNode.id);
        if (!moved) {
            System.out.println("error in moving edge");
        }

         }*/



    public int getTotalEdge() {
        return outEdgesInGraph.size();
    }


    public Vector getToNodesString() {
        Vector toNodes = new Vector();
        edge cedge;
        for (int i = 0; i < outEdgesInGraph.size(); i++) {
            cedge = outEdgesInGraph.get(i);
            toNodes.add(String.valueOf(cedge.getToNode()));
        }
        return toNodes;
    }

    public List<node> getToNodesList() {
        List<node> toNodes = new ArrayList<node>();
        edge cedge;
        for (int i = 0; i < outEdgesInGraph.size(); i++) {
            cedge = outEdgesInGraph.get(i);
            toNodes.add(cedge.toNode);
        }
        return toNodes;

    }

    public int[] getToNodes() {
        int[] toNodes = new int[outEdgesInGraph.size()];
        edge cedge;
        for (int i = 0; i < outEdgesInGraph.size(); i++) {
            cedge = outEdgesInGraph.get(i);
            toNodes[i] = cedge.getToNodeID();
        }
        return toNodes;
    }

    public List<edge> getOutEdgesInGraph() {
        return outEdgesInGraph;
    }

    public List<edge> getInEdgesInGraph() {
        return inEdgesInGraph;
    }





    public List<node> getFromNodes() {
        List<node> fromNodes = new ArrayList<node>();
        edge cedge;
        for (int i = 0; i < inEdgesInGraph.size(); i++) {
            cedge = inEdgesInGraph.get(i);
            fromNodes.add(cedge.fromNode);
        }
        return fromNodes;
    }


    public edge getTargetedOutEdge(int toNodeID, String type){
    	edge cedge;
        for (int i = 0; i < outEdgesInGraph.size(); i++) {
            cedge = outEdgesInGraph.get(i);
            if (cedge.getToNodeID() == toNodeID && cedge.type.equals(type)) {
                return cedge;
            }
        }
        return null;
    }
   


    /**
     * 返回到给点节点的边
     * @param toNodeID int
     * @return edge
     */
    public edge getOutEdgeToNodeInGraph(int toNodeID) {
        edge cedge;
        for (int i = 0; i < outEdgesInGraph.size(); i++) {
            cedge = outEdgesInGraph.get(i);
            if (cedge.getToNodeID() == toNodeID) {
                return cedge;
            }
        }
        return null;
    }

    /**
     * 返回到给点节点的边
     * @param toNodeID int
     * @return edge
     */
    public edge getInEdgeFromNodeInGraph(int fromNodeID) {
        edge cedge;
        for (int i = 0; i < inEdgesInGraph.size(); i++) {
            cedge = inEdgesInGraph.get(i);
            if (cedge.fromNode.id == fromNodeID) {
                return cedge;
            }
        }
        return null;
    }
    
}

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
import java.util.Vector;

import Query.Utils.*;

public class edge implements Serializable {

    //public int fromNodeID, toNodeID;

    public node fromNode;

    public node toNode = null;

    public int distance;
    
    public String type="--";
    public boolean selected=false;
    //这是产生计划的时候用到的
    //每个节点的所有边，要么在incoming 的时候设置为handle，要么在outgoing的时候设置为handled


    public boolean handled=false;

    public int beginClock;// 边存在的时钟
    
    public boolean covered=false;//这个是为了看看我们的边是否已经覆盖了
    
    
    public edge() {
//        beginClock= GConstant.timeClock; //这个放在这里不太好，先放在这里吧

    }
    public edge(String _type){
    	type = _type;
    }
     public void setFromNode(node fromNode1) {
        fromNode = fromNode1;
    }

    public void setToNode1(node toNode1) {
        toNode = toNode1;
    }

    public int getToNodeID() {
        return toNode.id;
    }

    public node getToNode() {
        return toNode;
    }
	@Override
    public boolean equals(Object edge1) {
		edge e = (edge)edge1;
        if (e.fromNode.id == this.fromNode.id &&
            e.toNode.id == this.toNode.id) {
            return true;
        } else {
            return false;
        }
    }

    /*public void addTreeID(int times) {
        if (!existTreeID(times)) {
            treeIDs.add(String.valueOf(times));
        }
    }*/

    /*public boolean existTreeID(int times) {
        boolean exist = false;
        String stimes;
        for (int i = 0; i < treeIDs.size(); i++) {
            stimes = (String) treeIDs.elementAt(i);
            if (stimes.equalsIgnoreCase(String.valueOf(times))) {
                exist = true;
                break;
            }

        }
        return exist;
    }*/



    public edge reverseEdge(){
        edge redge=new edge();
        redge.fromNode=this.toNode;
        redge.toNode=this.fromNode;
        redge.distance=this.distance;
        return redge;
    }

    public String toString(){
        String content="";
        //content=" from="+this.fromNode.id + " to="+this.toNode.id +" distance="+ this.distance+ " sidetrack="+ this.sideCost;
        content=" from="+this.fromNode.id + " to="+this.toNode.id +" type="+ this.type ;
        return content;
    }
	public String getRevType() {
		// TODO Auto-generated method stub
		if(type.contains("-"))
			return type.substring(1);
		else
			return "-"+type;
	}


}

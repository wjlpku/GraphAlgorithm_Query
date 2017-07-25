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
    //���ǲ����ƻ���ʱ���õ���
    //ÿ���ڵ�����бߣ�Ҫô��incoming ��ʱ������Ϊhandle��Ҫô��outgoing��ʱ������Ϊhandled


    public boolean handled=false;

    public int beginClock;// �ߴ��ڵ�ʱ��
    
    public boolean covered=false;//�����Ϊ�˿������ǵı��Ƿ��Ѿ�������
    
    
    public edge() {
//        beginClock= GConstant.timeClock; //����������ﲻ̫�ã��ȷ��������

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

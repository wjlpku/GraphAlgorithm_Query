package Query.memory;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.util.*;

import Query.Utils.parameter;

public class EvaluationPath {
	int queryID=0;
	
    List<EvaluationNode> enodes = new ArrayList<EvaluationNode>();
    //the var that determin whether share 判定是否共享的两个参数
    int benefit;
    int cost;
    //this is for judge whether the edge of original graph is covered 这个是为了判定原图中的边是否被覆盖，计算benefit要以没有cover的为主
    List<edge> edges = new ArrayList<edge>();
    //each node should add action rule ,and the next node is rely the before node 每个节点附加的action rule，下一个节点需要依赖于上有一个节点的的规则
    List<ActionRule> rules = new ArrayList<ActionRule>();
    
    graph queryGraph;

	public double estimatedCost = -1; //estimated Cost 估值
    
    public EvaluationPath(int queryID1) {
    	queryID = queryID1;
    }
    
    public void setQueryGraph(graph queryGraph1){
    	queryGraph = queryGraph1;
    }
    
    public EvaluationPath() {
    	
    }

    public List<EvaluationNode> getENode(){
    	return enodes;
    }
    
    public List<edge> getEdge(){
    	return edges;
    }
    
    public void insertENode(EvaluationNode enode){
    	enodes.add(0, enode);
    }
    
    public List<EvaluationNode> getENodes(){
    	return enodes;
    }
    
    
    public void insertMapEdge(edge cedge){
    	edges.add(0, cedge);
    }
    
    public String getContent(){
    	String content="";
    	for (int i=0;i<enodes.size();i++){
    		content = content + "("+enodes.get(i).id+","+enodes.get(i).label+","+enodes.get(i).planNodeID+  ")"+"->";
    	}
    	content+="\t edge ";
    	for(int i = 0; i <edges.size();i++){
    		content += edges.get(i).type+" ";
    	}
    	return content;
    }
    
    //what is the function to do?
    public boolean validPath(){
    	boolean valid =true;
    	if (enodes.size()==3){
    		if (enodes.get(0).id == enodes.get(2).id){
    			valid =false;
    		}
    	}
    	return valid;
    }
    
    public int length(){
    	return enodes.size();
    }
    
    //the order and id of epath1 and this.enodes is same
    public boolean contains(EvaluationPath epath1){
    	boolean isContained= true;
    	if (this.length()<epath1.length()){
    		return false;
    	}
    	for (int i=0;i<epath1.length();i++){
    		if (this.enodes.get(i).label!=epath1.enodes.get(i).label){
    			return false;
    		}
    	}
    	return isContained;
    }
    
    public boolean equals(EvaluationPath epath1){
    	boolean isSame = true;
    	
    	if (this.length()!=epath1.length()){
    		return false;
    	}
//    	System.out.println(getContent()+"\n"+epath1.getContent());
    	for (int i=0;i<epath1.length();i++){
    		if(parameter.benefit==2){
    			//label need to equal and the carryDid(?) need to equal
    		if (!this.enodes.get(i).label.equals(epath1.enodes.get(i).label) || this.enodes.get(i).carryDid != epath1.enodes.get(i).carryDid){
    			return false;
    		}
    		}else{
//    			System.out.println(this.enodes.get(i).label+"\t"+epath1.enodes.get(i).label);
    			if(i<epath1.length()-1){  
//    				System.out.println(this.edges.get(i).type+"\t"+epath1.edges.get(i).type);
    				if (!this.enodes.get(i).label.equals(epath1.enodes.get(i).label) || !this.edges.get(i).type.equals(epath1.edges.get(i).type))
    					return false;
        		}else if (!this.enodes.get(i).label.equals(epath1.enodes.get(i).label))
        			return false;
    		}
    	}
    	return isSame;
    }
    
    //mark the path all the edge covered
    public void setCovered(){
    	edge cedge;
    	for (int i=0;i<edges.size();i++){
    		cedge = edges.get(i);
    		cedge.covered = true;
    	}
    }
 
    //this is to inform other candidate that some edges is covered 这个要通知其他的candidate, 某些边已经覆盖 了
    //if there is some edge has not be covered, return false, else return true(all edges have been covered)
    public boolean isCovered(){
    	boolean isCovered=true;
    	edge cedge;
    	for (int i=0;i<edges.size();i++){
    		cedge = edges.get(i);
    		if (!cedge.covered) {
    			return false;
    		}
    	}
    	return isCovered;
    }
    
    //if edges has no edge in remainEdges , return true, 
    //else return false(there exits one or more edge in edges also included by remainEdges)
    public boolean isCovered(HashSet remainEdges){
    	boolean isCovered=true;
    	edge cedge;
    	for (int i=0;i<edges.size();i++){
    		cedge = edges.get(i);
    		String s1=cedge.fromNode.id+","+cedge.toNode.id+":"+cedge.type;
    		if (remainEdges.contains(s1) ) {
    			return false;
    		}
    	}
    	return isCovered;
    }
    
    /*
     * 这个实际上实在select path运行，节点的id，映射到evaluation plan的节点空间
     * 还是在selectpath中保留一份
     * 目标是希望规则能够共享
     */
    
    public void generateActionRule(){
    	// root node need to be solved alone :: root node需要单独处理
    	
    	//construct hashmap , one path may have the repeated data :: 构建hashmap,一个路径中也许有重复的数据
    	//show the degree of the edges
    	HashMap<String, Integer> degrees = this.initDegree();
        	
    	EvaluationNode pathNode;
    	node queryNode;
    	int diff=1;
        int remainDegree;   
    	for (int i=0;i<enodes.size()-1;i++){
    	    pathNode = enodes.get(i);
    		//in the frequent path, the id is same :: 在frequent path中，这个id是一致的
    		queryNode = queryGraph.getNodeById(pathNode.id);
    		
    		if (i==0){
    			diff=1;
    		}else{
    			diff=2; //this is because the node in the path middle, and the pre edge and next edge has cover it :: 这个是因为某个节点在路径中间，前后两条边已经把它覆盖了
       		}
    		
    		//remainDegree show the degree of the current node after delete
    		remainDegree = this.descDegree(degrees, queryNode.id, diff);
    		
    		EvaluationNode preNode=null;
    		if (i>0){
    			preNode = enodes.get(i-1);
    		}
    		//initialize if prenode==null , or copy the prenode's message :: 初始化(preNode==null)或者 复制(preNode!=null)
    		System.out.println("pl "+pathNode.label);
    		pathNode.copyRuleFromBeforeNode(pathNode.planNodeID, preNode,pathNode.label);
    		//
    		ActionRule crule= pathNode.rules.get(0);
    		//firstPlanNode，就是这条路径上对应的plan node的节点编号
    		//这个节点和当前节点在原图是一个节点
    		int lastPlanNode = this.findLastPlanNode(pathNode,i);
    		//在当前节点形成一个圈，需处理
			if (lastPlanNode != pathNode.planNodeID) {
//				System.out.println("circle occurs");
				Condition cdt = new Condition();
				cdt.eid1 = -2;
				cdt.bid1 = -2;
				cdt.eid2 = preNode.planNodeID;
				cdt.bid2 = lastPlanNode;
				crule.conditions.add(cdt);
				if (remainDegree == 0)
					crule.removeBranchFromOutput(lastPlanNode);
			}
    		//需要产生相对复杂的规则，携带data id
    			//向这个规则追加输出信息
//			if(pathNode.carryDid)
			if(remainDegree>0)
                crule.addBranchIntoOutput(pathNode.planNodeID, pathNode.id);
    		
    		//下面，是对规则中neighbor的设置
    		EvaluationNode postNode=enodes.get(i+1);//无需考虑越界，因为root节点不在这个循环中
    		crule.addNeighborLabel(edges.get(i).type);
    		crule.queryId = queryID;
    		crule.nodeId = pathNode.id;
    		crule.label = pathNode.label;
//    		if(queryID==109)
    		System.out.println(crule.getContent());
    	}
    	
    }
    
    /**
     * 是否携带data node id是要根据 degree的情况
     * 返回一个degree的map, 对应的是nodeid , degree
     * here we find the node in query graph by enodes.nodeid
     * @return map which show the degree of node of the path
     */
    private HashMap<String, Integer> initDegree(){
    	HashMap degrees= new HashMap<String, Integer>();
    	EvaluationNode enode;
    	node cnode;
    	
    	for (int i=0;i<enodes.size()-1;i++){
    		enode = enodes.get(i);
    		cnode = queryGraph.getNodeById(enode.id);
            if (degrees.get(cnode.id+"")==null){
            	degrees.put(cnode.id+"", cnode.getOutEdgesInGraph().size());
            }
    	
    	}	
    	
    	return degrees;
    	
    }
    
    /**
     * 反馈的结果值ֵ  
     * descDegree is use to delete one edge of this node in the path(if the node is in the mid, then we delete 2 edges) 
     * @param degrees
     * @param nodeid
     * @param diff
     * @return  the node degree after delete edge
     */
    private int descDegree(HashMap<String, Integer> degrees, int nodeid, int diff){
    	Integer oneDegree = degrees.get(nodeid+"");
    	int result = oneDegree.intValue() - diff;
    	degrees.put(nodeid+"", result);
    	return result;
    }
    
    /**
     * ͬthere can be more than one node in the path, we should find the first 同一条路可能有多个同样的node，定位到第一个matched的node
     * such as path like a-b-a-c   
     * this is running in the query path
     * @param cnodeid
     * @return
     */
    private int findFirstPlanNode(EvaluationNode pathNode){
    	int planNodeID = pathNode.planNodeID;
    	EvaluationNode enode;
    	for (int i=0;i<enodes.size()-1;i++){
    		enode = enodes.get(i);
    		if (pathNode.id==enode.id){
    			planNodeID = enode.planNodeID;  //��Ҫ�ҵ�һ����ͬ��
    			break;
    		}
    	}
    	return planNodeID;
    }
    
    private int findLastPlanNode(EvaluationNode pathNode,int pos){
    	int planNodeID = pathNode.planNodeID;
    	EvaluationNode enode;
    	for (int i=pos-1;i>=0;i--){
    		enode = enodes.get(i);
    		if (pathNode.id==enode.id){
    			planNodeID = enode.planNodeID;  //
    			break;
    		}
    	}
    	return planNodeID;
    }

	public boolean includes(EvaluationPath cpath) {
		// TODO Auto-generated method stub
		for(edge e:cpath.edges){
			if(!edges.contains(e) && !edges.contains(e.reverseEdge()))
				return false;
		}
		return true;
//		boolean contains = true;
//    	if (this.length()<cpath.length()){
//    		return false;
//    	}
//    	int length = length();
//    	boolean flag = true;
//    	int clength = cpath.length();
//    	for (int i=0;i<clength;i++){  
//    		if( i > 0){
//    			if (this.enodes.get(length-i-1).id!=cpath.enodes.get(clength-i-1).id
//    					|| this.edges.get(length-i-1).type != cpath.edges.get(clength-i-1).type){
//    				flag = false;
//    				break;
//    			}
//    		}else if (this.enodes.get(length-i-1).id!=cpath.enodes.get(clength-i-1).id){
//				flag = false;
//				break;
//			}
//    	}
//    	if( !flag && enodes.get(0).id == enodes.get(length-1).id){
//    		flag = true;
//    		for (int i=0;i<clength;i++){		      		
//        		if( i > 0 ){
//        			if (this.enodes.get(i).id!=cpath.enodes.get(clength-i-1).id
//        					|| "-"+this.edges.get(i-1).type != cpath.edges.get(clength-i-1).type
//        					|| this.edges.get(i-1).type != "-"+cpath.edges.get(clength-i-1).type){
//        				flag = false;
//        				break;
//        			}
//        		}else if (this.enodes.get(i).id!=cpath.enodes.get(clength-i-1).id){
//    				flag = false;
//    				break;
//    			}
//        	}
//    	}    		
////    	System.out.println(cpath.getContent()+" "+getContent());
//    	return flag;
	}
    
   }

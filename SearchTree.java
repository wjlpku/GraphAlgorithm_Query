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

import Query.Utils.statistic;

public class SearchTree {
    node startNode=null, rootNode=null;
    graph graph;
    int queryId;
    int maxPath=6;
    List<EvaluationPath> epaths = new ArrayList<EvaluationPath>();
    HashMap<edge, edge> edgeMap = new HashMap<edge, edge>();
    
    public SearchTree(node startNode1, node rootNode1, graph graph1, int queryId1) {
        startNode = startNode1;
        rootNode = rootNode1;
        graph = graph1;
        queryId = queryId1;
    }

	public void buildSearchTree() {
		node treeRootNode = new node(startNode.id);
		treeRootNode.label = startNode.label;
		LinkedList wklist = new LinkedList();
		wklist.add(treeRootNode);
		node treeCnode, cnode;
		node treeChildNode, childNode;

		boolean first = true;
		int k = maxPath;
		while (!wklist.isEmpty()) {
			treeCnode = (node) wklist.removeFirst();
			cnode = graph.getNodeById(treeCnode.id);
			List<edge> edges = cnode.getOutEdgesInGraph();
			if (first && edges.size() > 1) {
				treeCnode.carryDid = true;
				first = false;
			}
			if (edges.size() > 2)
				treeCnode.carryDid = true;
			boolean selfCircle = false;
			if (!treeCnode.selfCircle) {
				for (int i = 0; i < edges.size(); i++) {
					edge cedge = edges.get(i);
					childNode = cedge.toNode;
					if (childNode.id == cnode.id) {
						System.out.println(childNode.id + " circle");
						selfCircle = true;
						treeChildNode = new node(childNode.id);
						treeChildNode.label = childNode.label;
						treeChildNode.selfCircle = true;
						edge newedge = treeCnode.addChildNode(treeChildNode);
						// 这个在产生候选路径时使用
						edgeMap.put(newedge, cedge);
						// 和前面边不重复，才加入, 找到root 也没有必要再找了
						if (!this.existInParents(treeCnode, cedge)) {
							if (childNode.id != rootNode.id) {
								wklist.add(treeChildNode);
							} else {

								EvaluationPath onePath = this
										.generateOneCandidatePath(treeChildNode);
								epaths.add(onePath);

								wklist.add(treeChildNode);
							}
						}
						break;
					}
				}
			}
			if (selfCircle)
				continue;
			for (int i = 0; i < edges.size(); i++) {
				edge cedge = edges.get(i);
				childNode = cedge.toNode;
				// 这个地方需要保持原有graph的id
				treeChildNode = new node(childNode.id);
				treeChildNode.label = childNode.label;

				edge newedge = treeCnode.addChildNode(treeChildNode);
				// 这个在产生候选路径时使用
				edgeMap.put(newedge, cedge);

				// 和前面边不重复，才加入, 找到root 也没有必要再找了
				if (!this.existInParents(treeCnode, cedge)) {
					if (childNode.id != rootNode.id) {
						wklist.add(treeChildNode);
					} else {
						cnode = graph.getNodeById(childNode.id);
						List<edge> rootEdges = cnode.getOutEdgesInGraph();
						for (int j = 0; j < rootEdges.size(); j++) {
							cedge = rootEdges.get(j);
							childNode = cedge.toNode;
							if (childNode.id == cnode.id) {
								selfCircle = true;
								System.out.println("root circle");
								break;
							}
						}
						if (!selfCircle) {
							EvaluationPath onePath = this
									.generateOneCandidatePath(treeChildNode);
							epaths.add(onePath);
						}
						wklist.add(treeChildNode);

					}
				}
			}
		}
	}
	
    boolean buildCandidatePath(node secondNode, LinkedList totallist){
//        node treeRootNode = new node(secondNode.id);
//        treeRootNode.label = secondNode.label;
        LinkedList wklist = new LinkedList();
        wklist.add(secondNode);
        node treeCnode, cnode;
        node treeChildNode, childNode;
        

        while (!wklist.isEmpty()) {
            treeCnode = (node)  wklist.removeFirst();
            cnode=graph.getNodeById(treeCnode.id);
            List<edge> edges = cnode.getOutEdgesInGraph();

            if( edges.size()>2)
            	treeCnode.carryDid = true;
            for (int i=0;i<edges.size();i++){
                   edge cedge = edges.get(i);
                   childNode = cedge.toNode;
                   //这个地方需要保持原有graph的id
                   treeChildNode = new node (childNode.id);
                   treeChildNode.label = childNode.label;
            	   edge newedge=treeCnode.addChildNode(treeChildNode);
            	   //这个在产生候选路径时使用
            	   edgeMap.put(newedge, cedge);
            	   
                   //和前面边不重复，才加入, 找到root 也没有必要再找了
                   if (!this.existInParents(treeCnode,  cedge)){
                		if(childNode.id!=rootNode.id){
                			wklist.add(treeChildNode);                	   
                		}
                		else{
                	   EvaluationPath onePath =this.generateOneCandidatePath(treeChildNode);
//                	   if (onePath.validPath()){
                		   epaths.add(onePath);
                		   wklist.add(treeChildNode);
//                		   if(onePath.enodes.size()>5)
//                			   System.out.println(queryId+" "+rootNode.id);
                		   for( int j = i+1; j < edges.size(); j++){
                			   edge remainedge = edges.get(j);
                               childNode = remainedge.toNode;
                               //这个地方需要保持原有graph的id
                               treeChildNode = new node (childNode.id);
                               treeChildNode.label = childNode.label;                               
                               newedge=treeCnode.addChildNode(treeChildNode);
                        	   //这个在产生候选路径时使用
                        	   edgeMap.put(newedge, remainedge);
                        	   if (!this.existInParents(treeCnode,  remainedge)
                               		&& childNode.id!=rootNode.id   ){
                               	   wklist.add(treeChildNode);                	   
                               }
                        	  	if(childNode.id == rootNode.id)
                        		   System.out.println("no possible");
                		   }
                		   totallist.addAll(wklist);
                           return true;   
//                	   }
                   }
                   }
            }
        }
        return false;
    }

    private boolean existInParents(node parent, node child){
        boolean exists=false;
        List<edge> inEdges;
        while (parent!=null){
            if (parent.id == child.id){
                return true;
            }
            inEdges = parent.getInEdgesInGraph();
            if (inEdges.size()==0){
            	parent = null;
            	
            }else{
            	parent = inEdges.get(0).fromNode;
            }
        }
        return exists;
    }
    
    /**
     * @param parent  这个是查询树的节点
     * @param cedge   这个是原图中的边, 主要不能有重复边
     * @return
     */
    private boolean existInParents(node parent, edge cedge){
        boolean exists=false;
        List<edge> inEdges;
        edge treeCedge;
        while (parent!=null){
            
            inEdges = parent.getInEdgesInGraph();
            if (inEdges.size()==0){
            	parent = null;
            	
            }else{
            	treeCedge = inEdges.get(0);
            	edge cedge1= edgeMap.get(treeCedge);
            	if (this.isSameEdge(cedge, cedge1)){ //前面这条边出现过
            		return true;
            	}
            	parent = inEdges.get(0).fromNode;
            }
        }
        return exists;
    }
    /**
     * 判定两个edge是否是一致的， 这个是为了解决边的方向 a->b和b->a在pattern中是一样的
     * @param edge1
     * @param edge2
     * @return
     */
    private boolean isSameEdge(edge edge1, edge edge2){
    	if (edge1.fromNode == edge2.fromNode && edge1.toNode==edge2.toNode && edge1.type.equals(edge2.type)){
    		return true;
    	}
    	
//    	if (edge1.fromNode == edge2.toNode && edge1.toNode==edge2.fromNode && edge1.type.equals(edge2.type)){
//    		return true;
//    	}
    	
    	return false;
    }
    
    private EvaluationPath generateOneCandidatePath(node startNode){
    	EvaluationPath epath= new EvaluationPath();
    	List<edge> inEdges;
    	EvaluationNode enode;
    	edge treeEdge, queryEdge;
        while (startNode!=null){
        	enode = new EvaluationNode(queryId, startNode.id, startNode.label,startNode.carryDid);
        	epath.insertENode(enode);
            inEdges = startNode.getInEdgesInGraph();
            if (inEdges.size()==0){
            	startNode = null;
            }else{
            	treeEdge = inEdges.get(0);
            	startNode = treeEdge.fromNode;
                queryEdge = edgeMap.get(treeEdge);
                epath.insertMapEdge(queryEdge);
            	
            }
        }
        double cost = 0, toNodeNum = 1;
        enode = epath.enodes.get(0);
        if(!epath.edges.get(0).type.contains("null") && !epath.edges.get(0).type.contains("?")){
        	if(enode.label.equals("?"))
        		toNodeNum = statistic.triples.get(epath.edges.get(0).type);
        	else
        		toNodeNum *= statistic.triples.get(epath.edges.get(0).type)/
    					(statistic.tri_nodes.get(epath.edges.get(0).type)+0.01);
        }else if(enode.label.equals("?"))
        	toNodeNum = 10000000; //all nodes
    	cost += toNodeNum;
    	boolean lastContains = false;
        for(int i = 2; i < epath.enodes.size(); i++){
        	enode = epath.enodes.get(i);
        	boolean contains = false;
        	for(int j = i-2; j>=0; j--){
        		if(epath.enodes.get(j).id == enode.id){
        			contains = true;
        			break;
        		}        		
        	}
        	//consider situation like:5->2->1->3->4->3->1->2, 
			//node 4 can send msg to node 2 directly(jump node 3/1) to save msg
        	if(lastContains && contains){
        		continue;
        	}
        	
        	lastContains = contains;
        	if(!contains && !epath.edges.get(i-1).type.contains("null")){
        		if(epath.edges.get(i-1).type.contains("?")){
        			int lenth=1;
        			if(epath.edges.get(i-1).type.length()>1)
        				lenth=Integer.parseInt(epath.edges.get(i-1).type.substring(1));
        			if(i==epath.enodes.size()-1 && lenth>1)
        				lenth /= 2;
        			while(lenth>0){
        			toNodeNum *= statistic.triples.get("?")/
    					(statistic.tri_nodes.get("?")+0.01);
        			lenth--;
        			}
        		}
        		else{
        			toNodeNum *= statistic.triples.get(epath.edges.get(i-1).type)/
					(statistic.tri_nodes.get(epath.edges.get(i-1).type)+0.01);
        		}
        	}
        	cost += toNodeNum;
        }
        epath.estimatedCost = cost;
        return epath;
        
    }
    
    public void printCandidatePaths(){
    	System.out.println("startNode="+startNode.id + " rootNode="+rootNode.id);
    	EvaluationPath epath;
    	for (int i=0;i<epaths.size();i++){
    		epath = epaths.get(i);
    		System.out.println(epath.getContent());
    	}
    }
}

package Query.memory;

//获取candidate path中的frequent path的机制

import java.util.*;

import Query.Utils.parameter;

public class FrequentTree {
	
	//为了减少代码，我们用graph结构代表树
	node rootNode = new node();
	
	int leafnum = 0;
	//把这个epath加载到tree中
	public void addCandidatePath(EvaluationPath epath){
		node cnode = rootNode;
		node childNode;
		List<EvaluationNode> eNodes=epath.getENodes();
		List<edge> edges;
		edge cedge;
		for (int i=0;i<eNodes.size();i++){
			edges=cnode.getOutEdgesInGraph();
			childNode=null;
			for (int j=0;j<edges.size();j++){
				cedge = edges.get(j);
				if(parameter.benefit==2){
				if (cedge.toNode.label.equals(eNodes.get(i).label) && cedge.toNode.carryDid == eNodes.get(i).carryDid){
					childNode = cedge.toNode;
					break;
				}
				}else{
					if( i == 0){
						if (cedge.toNode.label.equals(eNodes.get(i).label)){
							childNode = cedge.toNode;
							break;
						}							
					}
					else if (cedge.toNode.label.equals(eNodes.get(i).label) && cedge.type.equals(epath.edges.get(i-1).type)){
						childNode = cedge.toNode;
						break;
					}
				}
			}
			if (childNode == null){
				childNode= new node();
				childNode.level = i;
				childNode.label = eNodes.get(i).label;
				childNode.carryDid = eNodes.get(i).carryDid;
				if(i==0)
					cnode.addChildNode(childNode);
				else
					cnode.addChildNode(childNode,epath.edges.get(i-1).type);
//				 System.out.println("cc "+childNode.id+" "+childNode.label);
			}

			cnode=childNode;
		}
		//最后一个点，是queryRoot
		if(!cnode.queryRoot){
			leafnum++;
		}
		cnode.queryRoot = true;
		cnode.frequent++;
	}
	public int getLeafNum(){
		return leafnum;
	}
	public void addCandidatePathList(List<EvaluationPath> epaths){
		EvaluationPath epath;
		for (int i=0;i<epaths.size();i++){
			this.addCandidatePath(epaths.get(i));
		}
		
	}
	
	public EvaluationPath  LocateNextEvaluationPath(queryPatterns queries){
		//获得最大收益的节点
		LinkedList<node> wklist = new LinkedList<node>();
		rootNode.pathcost = -1;
		wklist.add(rootNode);
		node cnode;
		double maxBenefit=-1;
		node selectedNode=null;
		double benefit = -1;
		int common = 0,tmp=0;
		while (!wklist.isEmpty()){
			cnode = wklist.removeFirst();
//			List<edge> inedges = cnode.getInEdgesInGraph();
//			if(inedges.size() > 0){
//				node innode = inedges.get(0).fromNode;
//				if(innode.id == rootNode.id){
//					if(parameter.labelDistributed)
//						cnode.num= statistic.labelDistribution[cnode.label];
//					else
//						cnode.num=40000;
//					cnode.hopcost = 1;
//					cnode.pathcost = 0;
//					if(cnode.carryDid)
//						cnode.hopcost++;
//				}else{
//				if(parameter.labelDistributed)
//					cnode.num = (innode.num*statistic.degree[innode.label]
//						*statistic.labelDistribution[cnode.label])/statistic.totalNodes;
//				else
//					cnode.num = innode.num*15*innode.num/4000000;
//				cnode.pathcost =innode.pathcost+cnode.num*innode.hopcost;
//				
//				if(cnode.carryDid)
//					cnode.hopcost = innode.hopcost+1;
//				else
//					cnode.hopcost = innode.hopcost;
//				}
//			}
			
			List<edge> edges= cnode.getOutEdgesInGraph();
			for (int i=0;i<edges.size();i++)
				wklist.add(edges.get(i).toNode);

			benefit = -1;
			common = 0;
//			common = cnode.level*cnode.frequent;
//			if(parameter.benefit==2 && cnode.frequent>0){
//				EvaluationPath tmpepath= new EvaluationPath();
//				node selectedNode2=cnode;
//				while (selectedNode2.id!=rootNode.id){
//					EvaluationNode enode = new EvaluationNode();
//					enode.label = selectedNode2.label;
//					enode.carryDid = selectedNode2.carryDid;
//					tmpepath.insertENode(enode);
//					List<edge> tmpedges = selectedNode2.getInEdgesInGraph();
//					selectedNode2 = tmpedges.get(0).fromNode;
//				}
//				
//				for (int i=0;i<queries.queryPatterns.size();i++){
//		            QueryPattern query = queries.queryPatterns.get(i);
//		            common += query.countCovered(tmpepath);
//		        }
//			}
			if(parameter.benefit==2)
//				benefit=cnode.level* cnode.frequent/ (cnode.pathcost+0.0);
				benefit=common / (cnode.pathcost+0.01);
			else if(parameter.benefit == 1)
				benefit=cnode.level* cnode.frequent;
			else
			    	benefit = cnode.frequent;
			if (benefit>maxBenefit){
				maxBenefit = benefit;
			    selectedNode=cnode;	
			    tmp = common;
			}
		}
//		System.out.print(maxBenefit+" "+tmp+" "+selectedNode.level+" "+selectedNode.frequent+" "+selectedNode.pathcost+" "+selectedNode.getInEdgesInGraph().get(0).fromNode.id+" "+selectedNode.getInEdgesInGraph().get(0).fromNode.pathcost);
		//产生evaluationPath
		EvaluationPath epath= new EvaluationPath();
		while (selectedNode.id!=rootNode.id){
			EvaluationNode enode = new EvaluationNode();
			enode.label = selectedNode.label;
			enode.carryDid = selectedNode.carryDid;
			epath.insertENode(enode);
			List<edge> edges = selectedNode.getInEdgesInGraph();
			if(selectedNode.level>0){
				edge newedge = new edge(edges.get(0).type);
//			enode.addInEdgeIntoGraph(newedge);			
				epath.insertMapEdge(newedge);
			}
			selectedNode = edges.get(0).fromNode;
		}
//		System.out.println("s "+epath.getContent());
		return epath;
		
		
	}
	
	public boolean isEmpty(){
		if (rootNode.getOutEdgesInGraph().size()>0)
			return false;
		else
			return true;
	}

}

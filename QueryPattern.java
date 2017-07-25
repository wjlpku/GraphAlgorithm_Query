package Query.memory;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import Query.Utils.*;

public class QueryPattern {
	private static final Logger LOG = Logger.getLogger(QueryPattern.class);
	graph queryGraph;
	int qid;
	node startNode, endNode; // 就是一个模式的开始和终止，startNode之外都是不需要比较vid，只需要比较state即可

	List<EvaluationPath> candidatePaths = new ArrayList<EvaluationPath>();
	HashSet<String> remainEdges = new HashSet<String>();
	HashSet<String> remainEdgesCopy = new HashSet<String>();
	List<EvaluationPath> selectPaths = new ArrayList<EvaluationPath>();

	Map<Integer, Integer> condOccurTimes = new HashMap<Integer, Integer>();

	// HashMap<String, HashSet<Integer>> plan2graph
	// = new HashMap<String, HashSet<Integer>>();

	public QueryPattern(graph queryGraph1, int qid1) {
		queryGraph = queryGraph1;
		qid = qid1;
		this.buildEdgeSet();
	}

	private void buildEdgeSet() {
		List<node> nodes = queryGraph.getNodes();
		node cnode;
		edge cedge;
		for (int i = 0; i < nodes.size(); i++) {
			cnode = nodes.get(i);
			List<edge> edges = cnode.getOutEdgesInGraph();
			for (int j = 0; j < edges.size(); j++) {
				cedge = edges.get(j);
				remainEdges.add(cedge.fromNode.id + "," + cedge.toNode.id + ":"
						+ cedge.type);// 这个里面是不区分方向的
				remainEdges.add(cedge.toNode.id + "," + cedge.fromNode.id + ":"
						+ cedge.getRevType());
			}
		}
		remainEdgesCopy.addAll(remainEdges);
	}

	private node findRootNode(String type) {
		if (type.equalsIgnoreCase("degree")) {
			return this.findRootNodeByDegree();
		}
		if (type.equalsIgnoreCase("first")) {
			return this.findRootNodeByFirst();
		}
		return null;

	}

	node cnode, maxNode;

	private node findRootNodeByDegree() {
		maxNode = queryGraph.getNodeById(0);
		int degree = maxNode.getOutEdgesInGraph().size();
		for (int i = 1; i < queryGraph.nodes.size(); i++) {
			cnode = queryGraph.getNodeById(i);
			if (cnode.getOutEdgesInGraph().size() > degree) {
				degree = cnode.getOutEdgesInGraph().size();
				maxNode = cnode;
			}
		}
		return maxNode;
	}

	public graph getQueryGraph() {
		return queryGraph;
	}

	private node findRootNodeByFirst() {
		node firstNode;
		firstNode = queryGraph.getNodeById(0);
		return firstNode;

	}

	public void removeCovered(EvaluationPath epath) {
		EvaluationPath cpath;
		// 根据刚才收益最大的，将包含的候选路径剪掉
		for (int i = 0; i < candidatePaths.size(); i++) {
			cpath = candidatePaths.get(i);
			if (epath.equals(cpath)) {
				// System.out.println("same "+cpath.getContent());
				this.addIntoSelected(cpath);
				// this.setPlanNode(epath, cpath);
				this.markEdgeSet(cpath);
			}
		}
		// 去掉已经覆盖了的, 这样candidate path越来越少。
		for (int i = candidatePaths.size() - 1; i >= 0; i--) {
			cpath = candidatePaths.get(i);
			if (cpath.isCovered(remainEdges)) {
				candidatePaths.remove(i);
			}
		}

	}

	public int countCovered(EvaluationPath epath) {
		EvaluationPath cpath;
		// 根据刚才收益最大的，将包含的候选路径剪掉
		int count = 0;
		for (int i = 0; i < candidatePaths.size(); i++) {
			cpath = candidatePaths.get(i);
			if (epath.equals(cpath)) {
				List<edge> edges = cpath.edges;
				edge cedge;
				for (int j = 0; j < edges.size(); j++) {
					cedge = edges.get(j);
					if (remainEdges.contains(cedge.fromNode.id + ","
							+ cedge.toNode.id)
							|| remainEdges.contains(cedge.toNode.id + ","
									+ cedge.fromNode.id))
						count++;
				}
			}
		}
		return count;
	}

	/**
	 * 
	 * @param fpath
	 *            这个是frequent 树中的path，是包含到plan的映射
	 * @param spath
	 *            是每个查询selected 的path，不会比fpath要长，前缀是一样
	 */
	private void setPlanNode(EvaluationPath fpath, EvaluationPath spath) {
		EvaluationNode snode, fnode;
		for (int i = 0; i < spath.enodes.size(); i++) {
			snode = spath.enodes.get(i);
			fnode = fpath.enodes.get(i);
			snode.setPlanNodeID(fnode.getPlanNodeID());
		}
	}

	private void addIntoSelected(EvaluationPath cpath) {
		for (int i = selectPaths.size() - 1; i >= 0; i--) {
			if (selectPaths.get(i).includes(cpath))
				return;
			if (cpath.includes(selectPaths.get(i)))
				selectPaths.remove(i);
			if (cpath.enodes.size() == 1)
				return;
		}
		selectPaths.add(cpath);
	}

	private void markEdgeSet(EvaluationPath epath) {
		List<edge> edges = epath.edges;
		edge cedge;
		for (int i = 0; i < edges.size(); i++) {
			cedge = edges.get(i);
			remainEdges.remove(cedge.fromNode.id + "," + cedge.toNode.id + ":"
					+ cedge.type);
			remainEdges.remove(cedge.toNode.id + "," + cedge.fromNode.id + ":"
					+ cedge.getRevType());
		}
	}

	// public void chooseRootNodeId(int k){
	// int minPaths=Integer.MAX_VALUE;
	// ArrayList<structForSort> degrees = new ArrayList<structForSort>();
	// node cnode;
	// for (int i=0;i<queryGraph.nodes.size();i++){
	// cnode = queryGraph.getNodeById(i);
	// int paths = countCandidatePath(cnode.id).b;
	// degrees.add(new
	// structForSort(cnode.id,paths,cnode.label,cnode.getOutEdgesInGraph().size()));
	// }
	// Collections.sort(degrees,
	// new Comparator<structForSort>() {
	// public int compare(structForSort o1, structForSort o2) {
	// // TODO Auto-generated method stub
	// if( o1.b == o2.b)
	// return o2.d - o1.d;
	// return o1.b - o2.b;
	// }
	// });
	// int level = 1;
	// degrees.get(0).e = level;
	// for( int i = 1; i < degrees.size(); i++)
	// if( degrees.get(i).b == degrees.get(i-1).b && degrees.get(i).d ==
	// degrees.get(i-1).d)
	// degrees.get(i).e = level;
	// else
	// degrees.get(i).e = ++level;
	// if(degrees.size()<k)
	// k = degrees.size();
	// candidateRootNodes = new structForSort[k];
	// for( int i = 0; i < k; i++)
	// candidateRootNodes[i] = new structForSort();
	// for( int i = 0; i < k; i++){
	// candidateRootNodes[i].a = degrees.get(i).a; //id
	// candidateRootNodes[i].b = degrees.get(i).b; //paths
	// candidateRootNodes[i].c = degrees.get(i).c; //label
	// candidateRootNodes[i].d = degrees.get(i).d; //degree
	// candidateRootNodes[i].e = degrees.get(i).e; //level
	// //
	// System.out.println(degrees.get(i).a+" "+degrees.get(i).b+" "+degrees.get(i).c+" "+degrees.get(i).d);
	// }
	// }

	// Random ran = new Random(qid);

	public void generatePlan() {
		int finalRootId = -1;
		double finalMinCost = Double.MAX_VALUE;
		for (int i = 0; i < queryGraph.getNodes().size(); i++) {
			int rootId = queryGraph.getNodes().get(i).id;
			generateCandidatePath(qid, rootId);
			resetRemainEdge();
			double totalMinCost = estimateMinCost();
			if (totalMinCost < finalMinCost) {
				finalMinCost = totalMinCost;
				finalRootId = rootId;
			}
			LOG.info("try use node " + rootId
					+ " as sink vertex, its min cost is " + totalMinCost);
		}
		generateCandidatePath(qid, finalRootId);
		resetRemainEdge();
		double totalMinCost = estimateMinCost();
		LOG.info("finally select node " + finalRootId
				+ " as sink vertex, its min cost is" + totalMinCost);
		for (int j = 0; j < selectPaths.size(); j++) {
			addOnePath(selectPaths.get(j));
			LOG.info("selected path: "+selectPaths.get(j).getContent());
		}
		List<ActionRule> rules = actionMaps.get(queryGraph
				.getNodeById(finalRootId).label);
		if (rules == null) {
			rules = new ArrayList<ActionRule>();
			actionMaps.put(queryGraph.getNodeById(finalRootId).label, rules);
		}
		rootRule.isRootRule = true;
		rootRule.queryId = qid;
		rootRule.nodeId = finalRootId;
		rootRule.label = queryGraph.getNodeById(finalRootId).label;
		rules.add(rootRule);

		// generate rule
		EvaluationNode pNode;
		for (int i = 0; i < selectPaths.size(); i++) {
			List<EvaluationNode> pathNodes = selectPaths.get(i).enodes;
			List<edge> pathEdges = selectPaths.get(i).edges;
			ActionRule beforeRule = null;
			int circlePreNode = -1;
			boolean hasCircle = false;
			for (int j = 0; j < pathNodes.size() - 1; j++) {
				pNode = pathNodes.get(j);
				ActionRule crule = null;
				if (pNode.rules.size() == 0) {
					crule = new ActionRule();
					pNode.rules.add(crule);
				} else {
					crule = pNode.rules.get(0);
				}
				if (j == 0) {
					// start node
					Message msg;
					msg = new Message();
					msg.eid = pNode.planNodeID;
					crule.outputs.add(msg);
					MessageInfo info = new MessageInfo();
					info.bid = pNode.id;
					info.obid = pNode.id;
					msg.msgInfos.add(info);

					crule.clock = 0;
				} else {
					Message msg, newMsg = null;
					// beforeRule = path.get(j-1).rules.get(0);
					// should be one inputs and one outputs
					if (hasCircle) {
						crule.circleStartNodes.add(circlePreNode);
						if (pNode.id != pathNodes.get(j - 1).id) {
							hasCircle = false;
							crule.reached = true;
						} else
							crule.circleNext = true;
					}
					for (int m = 0; m < beforeRule.outputs.size(); m++) {
						msg = beforeRule.outputs.get(m);
						newMsg = msg.cloneMessage();
						crule.inputs.add(newMsg);
						newMsg = msg.cloneMessage();
						if (pNode.id == pathNodes.get(j + 1).id) {
							// if(pNode.id == finalRootId)
							newMsg.eid = pathNodes.get(j - 1).planNodeID;
							crule.hasCircle = true;
							circlePreNode = pathNodes.get(j - 1).id;
							crule.circleStartNodes.add(circlePreNode);
							hasCircle = true;
							// else
							// newMsg.eid = pathNodes.get(j+1).planNodeID;
						} else
							newMsg.eid = pNode.planNodeID; // 就是说，这个message的状态和input的时候不一样
						crule.outputs.add(newMsg);
					}
					if (newMsg == null) {
						System.out.println("newmsg is null!!!");
					}
					MessageInfo info = new MessageInfo();
					info.bid = pNode.id;
					info.obid = pNode.id;
					newMsg.msgInfos.add(info);

				}
				
				crule.outNeighbors.add(pathEdges.get(j).type);
				
				//deal situation like:5->2->1->3->4->3->1->2, 
				//node 4 can send msg to node 2 directly(jump node 3/1) to save msg
				boolean contains;
				int pos;
				int outputNum=crule.outputs.get(0).msgInfos.size()-1;
				for (pos = j + 1; pos < pathNodes.size(); pos++) {
					if (pos == j + 1 && pNode.id == pathNodes.get(pos).id)
						break;
					contains = false;
					if (outputNum == 0)
						break;
					int m = outputNum - 1; // m <
											// crule.outputs.get(0).msgInfos.size();
											// m++) {
					int bid = crule.outputs.get(0).msgInfos.get(m).bid;
					if (bid == pathNodes.get(pos).id) {
						contains = true;
						crule.collectedNodeId = bid;
						outputNum--;
						// break;
					}
					// }
					if (!contains)
						break;

				}
				if (pos == j + 1)
					j = pos - 1;
				else
					j = pos - 2;
				crule.nodeId = pNode.id;
				crule.queryId = qid;
				crule.label = pNode.label;

				rules = actionMaps.get(pNode.label);
				if (rules == null) {
					rules = new ArrayList<ActionRule>();
					actionMaps.put(pNode.label, rules);
				}

				// distinct
				ActionRule newRule, tmpRule;
				for (int m = 0; m < pNode.rules.size(); m++) {
					newRule = pNode.rules.get(m);
					boolean repeated = false;
					for (int r = 0; r < rules.size(); r++) {
						tmpRule = rules.get(r);
						if (tmpRule.isSameAs(newRule)) {
							repeated = true;
							break;
						}
					}
					if (!repeated)
						rules.add(newRule);
				}
				LOG.info("buildrule " + rules.size() + " "
						+ crule.getContent());
				beforeRule = crule;
			}
			ActionRule rule = beforeRule;
			if (hasCircle)
				rootRule.circleStartNodes.add(circlePreNode);
			rootRule.inputs.add(new Message(rule.outputs.get(0).eid));

		}
		System.out.println("actionmap size: " + actionMaps.size());
		LOG.info("root rule: "+rootRule.getContent());
		//root上对中间结果的合并及是否匹配的判断在vertex中直接进行
	}

	public void addOnePath(EvaluationPath epath) {
		EvaluationNode startNode = null;
		// 首先处理第一个节点
		for (int i = 0; i < leafNodes.size(); i++) {
			String label = epath.enodes.get(0).label;
			if (leafNodes.get(i).label == label) {
				int tmpObid = leafNodes.get(i).tmpObid;
				if (tmpObid == -1) {
					startNode = leafNodes.get(i);
					leafNodes.get(i).tmpObid = epath.enodes.get(0).id;
					break;
				} else if (tmpObid == epath.enodes.get(0).id) {
					startNode = leafNodes.get(i);
					break;
				}
			}
		}
		if (startNode == null) {
			startNode = new EvaluationNode();
			leafNodes.add(startNode);
			startNode.label = epath.enodes.get(0).label;
			startNode.tmpObid = epath.enodes.get(0).id;
		}

		// 建立从epath到Plan节点的映射
		epath.enodes.get(0).setPlanNodeID(startNode.id);

		EvaluationNode childNode;
		List<EvaluationNode> eNodes = epath.getENodes();
		List<edge> edges;
		edge cedge;

		// 处理第二个到root节点，这个要合并的。

		EvaluationNode pathNode;
		for (int i = 1; i < eNodes.size() - 1; i++) {
			pathNode = eNodes.get(i);
			int lastSamePos = -1;
			for (int j = i - 1; j >= 0; j--)
				if (eNodes.get(j).id == pathNode.id) {
					lastSamePos = j;
					// System.out.println("same pos: "+j);
					break;
				}
			edges = startNode.getOutEdgesInGraph();
			childNode = null;
			for (int j = 0; j < edges.size(); j++) {
				cedge = edges.get(j);
				// label相同，且cedge.toNode不为根节点
				if (cedge.toNode.label == pathNode.label
						&& cedge.type == epath.edges.get(i - 1).type
						&& cedge.toNode.getOutEdgesInGraph().size() != 0) {
					if (lastSamePos != cedge.toNode.lasteid)
						continue;
					childNode = (EvaluationNode) cedge.toNode;
					break;
				}
			}
			// 当前没有合适的
			if (childNode == null) {
				childNode = new EvaluationNode();
				childNode.level = i;
				childNode.label = eNodes.get(i).label;
				if (lastSamePos != -1)
					childNode.lasteid = lastSamePos;
				startNode.addChildNode(childNode, epath.edges.get(i - 1).type);
			}
			pathNode.setPlanNodeID(childNode.id);
			startNode = childNode;
		}

	}

	public double estimateMinCost() {
		Collections.sort(candidatePaths, new Comparator<EvaluationPath>() {
			public int compare(EvaluationPath path1, EvaluationPath path2) {
				if (path1.estimatedCost > path2.estimatedCost)
					return 1;
				if (path1.estimatedCost < path2.estimatedCost)
					return -1;
				return 0;
			}
		});

		EvaluationPath selectedPath = candidatePaths.get(0);
		System.out.println("first path " + candidatePaths.get(0).estimatedCost
				+ " " + candidatePaths.get(0).getContent() + " "
				+ remainEdges.size());

		while (remainEdges.size() > 0) {

			removeCovered(selectedPath);
			if (remainEdges.size() > 0){
				selectedPath = candidatePaths.get(0);
			System.out.println("then path " + candidatePaths.get(0).estimatedCost
					+ " " + candidatePaths.get(0).getContent() + " "
					+ remainEdges.size());
			}
		}
		double totalMinCost = 0;
		for (int j = 0; j < selectPaths.size(); j++) {
			totalMinCost += selectPaths.get(j).estimatedCost;
//			LOG.info(selectPaths.get(j).getContent() + " "
//					+ selectPaths.get(j).estimatedCost);
		}
		return totalMinCost;
	}

	public void resetRemainEdge() {
		// TODO Auto-generated method stub
		remainEdges.addAll(remainEdgesCopy);
	}

	private void buildLeafPath(EvaluationNode leaf, int rootId,
			List<edge> remainsEdges) {
		Set<String> visited = new HashSet<String>();
		node firstNode = queryGraph.getNodeById(leaf.originId);
		EvaluationNode fromNode, toNode, pathStartNode = new EvaluationNode(
				firstNode.id, firstNode.label);

		List<edge> pathEdges = new ArrayList<edge>();
		leaf_dfs(firstNode, rootId, visited, pathEdges, 0, 1, false);

		List<EvaluationNode> path = new ArrayList<EvaluationNode>();
		path.add(pathStartNode);
		fromNode = pathStartNode;
		for (int i = 0; i < finalPathEdges.size(); i++) {
			edge e = finalPathEdges.get(i);
			toNode = new EvaluationNode(e.toNode.id, e.toNode.label);
			path.add(toNode);
			fromNode.addChildNode(toNode, e.type);
			fromNode = toNode;
			remainsEdges.remove(e);
			remainsEdges.remove(e.reverseEdge());
		}
		ActionRule leafRule = leaf.rules.get(0);
		int pos = 1;
		for (int i = 1; i < path.size(); i++) {
			boolean contains = false;
			for (int j = 0; j < leafRule.outputs.get(0).msgInfos.size(); j++) {
				int bid = leafRule.outputs.get(0).msgInfos.get(j).bid;
				if (bid == path.get(i).originId) {
					contains = true;
					leafRule.collectedNodeId = bid;
					pos = i;
					break;
				}
			}
			if (!contains)
				break;
		}
		leafRule.outNeighbors.add(finalPathEdges.get(0).type);
		System.out.println("leaf path " + leafRule.getContent());

		EvaluationNode preNode = leaf, pNode = null;
		// generate rule
		for (int i = pos; i < path.size() - 1; i++) {
			pNode = path.get(i);
			ActionRule crule = null;
			if (pNode.rules.size() == 0) {
				crule = new ActionRule();
				pNode.rules.add(crule);
			} else {
				crule = pNode.rules.get(0);
			}
			Message msg, newMsg = null;
			ActionRule beforeRule = preNode.rules.get(0);
			// should be one inputs and one outputs
			for (int j = 0; j < beforeRule.outputs.size(); j++) {
				msg = beforeRule.outputs.get(j);
				newMsg = msg.cloneMessage();
				crule.inputs.add(newMsg);
				newMsg = msg.cloneMessage();
				newMsg.eid = pNode.id; // 就是说，这个message的状态和input的时候不一样
				crule.outputs.add(newMsg);
			}
			if (newMsg == null) {
				System.out.println("newmsg is null!!!");
			}
			MessageInfo info = new MessageInfo();
			info.bid = pNode.originId;
			info.obid = pNode.originId;
			newMsg.msgInfos.add(info);

			crule.outNeighbors.add(finalPathEdges.get(i).type);

			crule.nodeId = pNode.originId;
			crule.queryId = qid;
			crule.label = pNode.label;

			List<ActionRule> rules = actionMaps.get(pNode.label);
			if (rules == null) {
				rules = new ArrayList<ActionRule>();
				actionMaps.put(pNode.label, rules);
			}

			ActionRule newRule;
			for (int j = 0; j < pNode.rules.size(); j++) {
				newRule = pNode.rules.get(j);
				rules.add(newRule);
			}

			System.out.println("leaf path " + crule.getContent());
			preNode = pNode;
		}

		ActionRule rule = preNode.rules.get(0);
		rootRule.inputs.add(new Message(rule.outputs.get(0).eid));
	}

	double minPathCost = Double.MAX_VALUE;
	double minNodeNum = Double.MAX_VALUE;
	List<edge> finalPathEdges = new ArrayList<edge>();

	private void leaf_dfs(node fromNode, int rootId, Set<String> visited,
			List<edge> pathEdges, double cost, double toNodeNums,
			boolean isContained) {
		// TODO Auto-generated method stub
		// System.out.println("dfs " +fromNode.id+" "+cost+" "+toNodeNums);
		if (fromNode.id == rootId && cost < minPathCost) {
			minPathCost = cost;
			minNodeNum = toNodeNums;
			finalPathEdges.clear();
			for (edge te : pathEdges)
				System.out.println("one " + te.toString());
			System.out.println(cost);
			finalPathEdges.addAll(pathEdges);
			return;
		}
		for (int i = 0; i < fromNode.getTotalEdge(); i++) {
			edge e = fromNode.getOutEdgesInGraph().get(i);
			// System.out.println("e " +e.toString());
			if (visited
					.contains(fromNode.id + "_" + e.type + "_" + e.toNode.id))
				continue;
			double curCost = toNodeNums
					* (statistic.triples.get(e.type) / (0.01 + statistic.tri_nodes
							.get(e.type)));

			visited.add(fromNode.id + "_" + e.type + "_" + e.toNode.id);
			boolean contains = false;
			for (int j = 0; j < pathEdges.size(); j++) {
				edge preEdge = pathEdges.get(j);
				if (preEdge.fromNode.id == e.toNode.id
						|| preEdge.toNode.id == e.toNode.id) {
					contains = true;
					break;
				}
			}
			pathEdges.add(e);
			if (contains)
				curCost = toNodeNums;
			if (isContained && contains)
				leaf_dfs(e.toNode, rootId, visited, pathEdges, cost, curCost,
						contains);
			else
				leaf_dfs(e.toNode, rootId, visited, pathEdges, cost + curCost,
						curCost, contains);
			visited.remove(fromNode.id + "_" + e.type + "_" + e.toNode.id);
			pathEdges.remove(pathEdges.size() - 1);
		}
	}

	private void buildPath(edge firstEdge, int rootId, List<edge> remainsEdges) {
		// TODO Auto-generated method stub
		Queue<node> q = new LinkedList<node>();
		node firstNode = firstEdge.fromNode;
		node fromNode;
		EvaluationNode pathNode, pathEndNode = null;
		EvaluationNode pathStartNode = new EvaluationNode(firstNode.id,
				firstNode.label);
		EvaluationNode pathSecondNode = new EvaluationNode(firstEdge.toNode.id,
				firstEdge.toNode.label);
		pathStartNode.addChildNode(pathSecondNode, firstEdge.type);
		if (firstEdge.toNode.id == rootId)
			pathEndNode = pathSecondNode;
		else {
			q.add(firstEdge.toNode);
			q.add(pathSecondNode);
		}
		boolean first = true;
		boolean reached = false;
		while (!q.isEmpty()) {
			fromNode = q.poll();
			pathNode = (EvaluationNode) q.poll();
			for (int i = 0; i < fromNode.getTotalEdge(); i++) {
				node toNode = fromNode.getOutEdgesInGraph().get(i).toNode;
				if (first && toNode.id == firstNode.id) {
					first = false;
					continue;
				}
				EvaluationNode pathToNode = new EvaluationNode(toNode.id,
						toNode.label);
				pathNode.addChildNode(pathToNode, fromNode.getOutEdgesInGraph()
						.get(i).type);

				if (toNode.id == rootId) {
					pathEndNode = pathToNode;
					reached = true;
					break;
				} else {
					q.add(toNode);
					q.add(pathToNode);
				}

			}
			if (reached)
				break;
		}
		if (pathEndNode != null) {
			List<EvaluationNode> path = new ArrayList<EvaluationNode>();
			List<edge> pathEdge = new ArrayList<edge>();
			path.add(pathEndNode);
			EvaluationNode pNode = pathEndNode;
			while (pNode.getInEdgesInGraph().size() > 0) {
				edge e = pNode.getInEdgesInGraph().get(0);
				path.add(0, (EvaluationNode) e.fromNode);
				pathEdge.add(0, e);
				node tmp2 = queryGraph.getNodeById(pNode.originId);
				pNode = (EvaluationNode) e.fromNode;
				node tmp = queryGraph.getNodeById(pNode.originId);
				edge rEdge = new edge();
				rEdge.fromNode = tmp;
				rEdge.toNode = tmp2;
				remainsEdges.remove(rEdge);
				remainsEdges.remove(rEdge.reverseEdge());
			}

			// generate rule
			for (int i = 0; i < path.size() - 1; i++) {
				pNode = path.get(i);
				ActionRule crule = null;
				if (pNode.rules.size() == 0) {
					crule = new ActionRule();
					pNode.rules.add(crule);
				} else {
					crule = pNode.rules.get(0);
				}
				if (i == 0) {
					// start node
					Message msg;
					msg = new Message();
					msg.eid = pNode.id;
					crule.outputs.add(msg);
					MessageInfo info = new MessageInfo();
					info.bid = pNode.originId;
					info.obid = pNode.originId;
					msg.msgInfos.add(info);

					crule.clock = 0;
				} else {
					Message msg, newMsg = null;
					ActionRule beforeRule = path.get(i - 1).rules.get(0);
					// should be one inputs and one outputs
					for (int j = 0; j < beforeRule.outputs.size(); j++) {
						msg = beforeRule.outputs.get(j);
						newMsg = msg.cloneMessage();
						crule.inputs.add(newMsg);
						newMsg = msg.cloneMessage();
						newMsg.eid = pNode.id; // 就是说，这个message的状态和input的时候不一样
						crule.outputs.add(newMsg);
					}
					if (newMsg == null) {
						System.out.println("newmsg is null!!!");
					}
					MessageInfo info = new MessageInfo();
					info.bid = pNode.originId;
					info.obid = pNode.originId;
					newMsg.msgInfos.add(info);
				}
				crule.outNeighbors.add(pathEdge.get(i).type);

				crule.nodeId = pNode.originId;
				crule.queryId = qid;
				crule.label = pNode.label;

				List<ActionRule> rules = actionMaps.get(pNode.label);
				if (rules == null) {
					rules = new ArrayList<ActionRule>();
					actionMaps.put(pNode.label, rules);
				}

				ActionRule newRule;
				for (int j = 0; j < pNode.rules.size(); j++) {
					newRule = pNode.rules.get(j);
					rules.add(newRule);
				}
				System.out.println("buildpath " + crule.getContent());
			}
			ActionRule rule = path.get(path.size() - 2).rules.get(0);
			rootRule.inputs.add(new Message(rule.outputs.get(0).eid));
		}
	}

	private int countLenth_bfs(edge e, int rootId) {
		// TODO Auto-generated method stub
		if (e.toNode.id == rootId)
			return 0;
		Queue<node> q = new LinkedList<node>();
		node firstNode = e.fromNode;
		node fromNode;
		q.add(e.toNode);
		boolean reached = false;
		boolean first = true;
		int lenth = 0;
		while (!q.isEmpty()) {
			lenth++;
			fromNode = q.poll();
			for (int i = 0; i < fromNode.getTotalEdge(); i++) {
				node toNode = fromNode.getOutEdgesInGraph().get(i).toNode;
				if (first && toNode.id == firstNode.id) {
					first = false;
					continue;
				}
				if (toNode.id == rootId) {
					reached = true;
					break;
				} else {
					q.add(toNode);
				}

			}
			if (reached)
				break;
		}
		if (reached)
			return lenth;
		else
			return Integer.MIN_VALUE;
	}

	ActionRule rootRule = new ActionRule();

	private void leaf_bfs(EvaluationNode leaf, int rootId,
			List<edge> remainsEdges) {
		// TODO Auto-generated method stub
		Queue<node> q = new LinkedList<node>();
		node firstNode = queryGraph.getNodeById(leaf.originId);
		node fromNode;
		EvaluationNode pathNode, pathEndNode = null, pathStartNode = new EvaluationNode(
				firstNode.id, firstNode.label);
		q.add(firstNode);
		q.add(pathStartNode);
		boolean reached = false;
		while (!q.isEmpty()) {
			fromNode = q.poll();
			pathNode = (EvaluationNode) q.poll();
			for (int i = 0; i < fromNode.getTotalEdge(); i++) {
				node toNode = fromNode.getOutEdgesInGraph().get(i).toNode;
				EvaluationNode pathToNode = new EvaluationNode(toNode.id,
						toNode.label);
				pathNode.addChildNode(pathToNode, fromNode.getOutEdgesInGraph()
						.get(i).type);
				if (toNode.id == rootId) {
					reached = true;
					pathEndNode = pathToNode;
					break;
				} else {
					q.add(toNode);
					q.add(pathToNode);
				}

			}
			if (reached)
				break;
		}
		if (pathEndNode != null) {
			List<EvaluationNode> path = new ArrayList<EvaluationNode>();
			List<edge> pathEdge = new ArrayList<edge>();
			path.add(pathEndNode);
			EvaluationNode pNode = pathEndNode;
			while (pNode.getInEdgesInGraph().size() > 0) {
				edge e = pNode.getInEdgesInGraph().get(0);
				path.add(0, (EvaluationNode) e.fromNode);
				pathEdge.add(0, e);
				node tmp2 = queryGraph.getNodeById(pNode.originId);
				pNode = (EvaluationNode) e.fromNode;
				node tmp = queryGraph.getNodeById(pNode.originId);
				edge rEdge = new edge();
				rEdge.fromNode = tmp;
				rEdge.toNode = tmp2;
				remainsEdges.remove(rEdge);
				remainsEdges.remove(rEdge.reverseEdge());
			}
			ActionRule leafRule = leaf.rules.get(0);
			int pos = 1;
			for (int i = 1; i < path.size(); i++) {
				boolean contains = false;
				for (int j = 0; j < leafRule.outputs.get(0).msgInfos.size(); j++) {
					int bid = leafRule.outputs.get(0).msgInfos.get(j).bid;
					if (bid == path.get(i).originId) {
						contains = true;
						leafRule.collectedNodeId = bid;
						pos = i;
						break;
					}
				}
				if (!contains)
					break;
			}
			leafRule.outNeighbors.add(pathEdge.get(0).type);
			System.out.println("leaf path " + leafRule.getContent());

			EvaluationNode preNode = leaf;
			// generate rule
			for (int i = pos; i < path.size() - 1; i++) {
				pNode = path.get(i);
				ActionRule crule = null;
				if (pNode.rules.size() == 0) {
					crule = new ActionRule();
					pNode.rules.add(crule);
				} else {
					crule = pNode.rules.get(0);
				}
				Message msg, newMsg = null;
				ActionRule beforeRule = preNode.rules.get(0);
				// should be one inputs and one outputs
				for (int j = 0; j < beforeRule.outputs.size(); j++) {
					msg = beforeRule.outputs.get(j);
					newMsg = msg.cloneMessage();
					crule.inputs.add(newMsg);
					newMsg = msg.cloneMessage();
					newMsg.eid = pNode.id; // 就是说，这个message的状态和input的时候不一样
					crule.outputs.add(newMsg);
				}
				if (newMsg == null) {
					System.out.println("newmsg is null!!!");
				}
				MessageInfo info = new MessageInfo();
				info.bid = pNode.originId;
				info.obid = pNode.originId;
				newMsg.msgInfos.add(info);

				crule.outNeighbors.add(pathEdge.get(i).type);

				crule.nodeId = pNode.originId;
				crule.queryId = qid;
				crule.label = pNode.label;

				List<ActionRule> rules = actionMaps.get(pNode.label);
				if (rules == null) {
					rules = new ArrayList<ActionRule>();
					actionMaps.put(pNode.label, rules);
				}

				ActionRule newRule;
				for (int j = 0; j < pNode.rules.size(); j++) {
					newRule = pNode.rules.get(j);
					rules.add(newRule);
				}

				System.out.println("leaf path " + crule.getContent());
				preNode = pNode;
			}

			ActionRule rule = preNode.rules.get(0);
			rootRule.inputs.add(new Message(rule.outputs.get(0).eid));
		}
	}

	private edge selectMinCostTriple() {
		double cost = Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		edge minEdge = null;
		node cnode;
		for (int i = 0; i < queryGraph.nodes.size(); i++) {
			cnode = queryGraph.getNodeById(i);
			for (int j = 0; j < cnode.getTotalEdge(); j++) {
				edge e = cnode.getOutEdgesInGraph().get(j);
				if (e.selected)
					continue;
				if (cnode.predictedNums == Double.MAX_VALUE)
					cost = statistic.triples.get(e.type);
				else {
					cost = cnode.predictedNums
							* (statistic.triples.get(e.type) / (statistic.tri_nodes
									.get(e.type) + 0.01));
					// if(cost > triples.get(e.type)){
					// cost = triples.get(e.type);
					// System.out.println("!!!I think it's impossible that triples.get(e.type) is less");
					// }
				}

				if (cost < min) {
					min = cost;
					minEdge = e;
				}
			}
		}

		if (min > 10000)
			return null;
		if (minEdge != null) {
			System.out.println(minEdge.toString());
			if (min < minEdge.toNode.predictedNums)
				minEdge.toNode.predictedNums = min;
			// if( min == statistic.triples.get(minEdge.type)
			// && minEdge.fromNode.predictedNums >
			// statistic.tri_nodes.get(minEdge.type))
			// minEdge.fromNode.predictedNums =
			// statistic.tri_nodes.get(minEdge.type);
		}
		return minEdge;
	}

	EvaluationNode planRoot = new EvaluationNode();

	private void addToPlan(edge e) {
		// TODO Auto-generated method stub
		if (!dfs(planRoot, e)) {
			EvaluationNode child = new EvaluationNode(e.fromNode.id,
					e.fromNode.label);
			planRoot.addChildNode(child, "null");
			child.addChildNode(new EvaluationNode(e.toNode.id, e.toNode.label),
					e.type);
		}

	}

	private boolean dfs(EvaluationNode v, edge e) {
		if (v.originId == e.fromNode.id) {
			v.addChildNode(new EvaluationNode(e.toNode.id, e.toNode.label),
					e.type);
			return true;
		}
		EvaluationNode child = null;
		edge cedge = null;
		for (int i = 0; i < v.getTotalEdge(); i++) {
			cedge = v.getOutEdgesInGraph().get(i);
			child = (EvaluationNode) cedge.toNode;
			if (dfs(child, e))
				return true;
		}
		return false;
	}

	HashMap<String, List<ActionRule>> actionMaps = new HashMap<String, List<ActionRule>>();

	List<Integer> leafNodeId = new ArrayList<Integer>();
	List<EvaluationNode> leafNodes = new ArrayList<EvaluationNode>();

	public HashMap<String, List<ActionRule>> getActionMaps() {
		return actionMaps;
	}

	public List<Integer> getLeafNodeId() {
		return leafNodeId;
	}

	private void generateSelectedRules(Set<Integer> remains) {
		generateSelectedRules_dfs(planRoot, remains);

	}

	public Map<Integer, List<ActionRule>> preRules = new HashMap<Integer, List<ActionRule>>();

	private void generateSelectedRules_dfs(EvaluationNode v,
			Set<Integer> remains) {
		if (v.getTotalEdge() == 0) {
			if (v.rules.size() == 0)
				return;
			leafNodeId.add(v.id);
			leafNodes.add(v);
			ActionRule rule = v.rules.get(0);
			rule.isLeafRule = true;

		}
		EvaluationNode child = null;
		edge cedge = null;
		for (int i = 0; i < v.getTotalEdge(); i++) {
			cedge = v.getOutEdgesInGraph().get(i);
			child = (EvaluationNode) cedge.toNode;
			ActionRule crule = null;
			if (child.rules.size() == 0) {
				crule = new ActionRule();
				child.rules.add(crule);
			} else {
				crule = child.rules.get(0);
			}
			if (v == planRoot) {
				// start node
				Message msg;
				msg = new Message();
				msg.eid = child.id;
				crule.outputs.add(msg);
				MessageInfo info = new MessageInfo();
				info.bid = child.originId;
				info.obid = child.originId;
				msg.msgInfos.add(info);

				crule.clock = 0;
			} else {
				Message msg, newMsg = null;
				ActionRule beforeRule = v.rules.get(0);
				// should be one inputs and one outputs
				for (int j = 0; j < beforeRule.outputs.size(); j++) {
					msg = beforeRule.outputs.get(j);
					newMsg = msg.cloneMessage();
					crule.inputs.add(newMsg);
					newMsg = msg.cloneMessage();
					newMsg.eid = child.id; // 就是说，这个message的状态和input的时候不一样
					crule.outputs.add(newMsg);
				}
				if (newMsg == null) {
					System.out.println("newmsg is null!!!");
				}
				MessageInfo info = new MessageInfo();
				info.bid = child.originId;
				info.obid = child.originId;
				newMsg.msgInfos.add(info);
			}
			for (int j = 0; j < child.getTotalEdge(); j++) {
				edge e = child.getOutEdgesInGraph().get(j);
				crule.outNeighbors.add(e.type);
			}
			crule.nodeId = child.originId;
			crule.queryId = qid;
			crule.label = child.label;

			List<ActionRule> rules = actionMaps.get(child.label);
			if (rules == null) {
				rules = new ArrayList<ActionRule>();
				actionMaps.put(child.label, rules);
			}

			// if(child.getTotalEdge()==0){
			// for(int j=0; j<rules.size();j++){
			// ActionRule preRule = rules.get(j);
			// //end with the same node,combine the rule
			// if(preRule.isLeafRule && preRule.nodeId == crule.nodeId){
			// preRule.inputs.add(new Message(crule.inputs.get(0).eid));
			// crule.inputs.add(new Message(preRule.inputs.get(0).eid));
			// // return;
			// }
			// }
			// }

			ActionRule newRule;
			for (int j = 0; j < child.rules.size(); j++) {
				newRule = child.rules.get(j);
				rules.add(newRule);
			}

			System.out.println(qid + " " + crule.getContent());
			generateSelectedRules_dfs(child, remains);
		}
	}

	/*
	 * generate candidate paths that ends at node rootId
	 */
	public void generateCandidatePath(int queryId, int rootId) {
		node rootNode;
		// if(parameter.RootChosen==2)
		// rootNode = queryGraph.getNodeById(rootNodeId);
		// else if( parameter.RootChosen == 1)
		// rootNode = this.findRootNodeByDegree();
		// else{
		// Random ran = new Random(queryId);
		// int id = ran.nextInt(queryGraph.nodes.size());
		// rootNode=new node(id);
		// }
		// if(queryId==1 ||queryId==4||queryId==5)
		// rootNode = new node(2);
		// else
		// rootNode=new node(0);

		rootNode = new node(rootId);
		SearchTree tree;
		node cnode;
		candidatePaths.clear();
		selectPaths.clear();
		for (int i = 0; i < queryGraph.nodes.size(); i++) {
			cnode = queryGraph.getNodeById(i);
			tree = new SearchTree(cnode, rootNode, queryGraph, queryId);
			tree.buildSearchTree();
			// 追加到candidatePath中
			candidatePaths.addAll(tree.epaths);
		}
		this.setQueryGraph(queryId);
	}

	private void setQueryGraph(int queryId) {
		EvaluationPath epath;
		for (int i = 0; i < candidatePaths.size(); i++) {
			epath = candidatePaths.get(i);
			epath.setQueryGraph(queryGraph);
			epath.queryID = queryId;
		}
	}

	// 判定节点是否已经标记完毕了
	private boolean isProcessed(node cnode) {
		boolean checked = false;
		int handled = 0;
		edge cedge;
		List<edge> edges;
		edges = cnode.getInEdgesInGraph();
		int tot = edges.size();
		for (int i = 0; i < edges.size(); i++) {
			cedge = edges.get(i);
			if (cedge.handled) {
				handled++;
			}
		}

		edges = cnode.getOutEdgesInGraph();
		for (int i = 0; i < edges.size(); i++) {
			cedge = edges.get(i);
			if (cedge.handled) {
				handled++;
			}
		}

		return (handled == tot);

	}

	public void generateRule() {
		EvaluationPath cpath;
		for (int i = 0; i < selectPaths.size(); i++) {
			cpath = selectPaths.get(i);
			cpath.generateActionRule();
		}
	}

	/**
	 * 这个是在plan空间做的
	 * 
	 * @param eplan
	 */
	public void generateRootRule(EvaluationPlan eplan) {
		if (selectPaths.size() == 0)
			return;
		EvaluationPath cpath = selectPaths.get(0);
		EvaluationNode pathRoot = cpath.enodes.get(cpath.enodes.size() - 1);
		EvaluationNode planRoot = eplan.rootMap.get(this.qid);
		if (planRoot == null) {
			System.out.println("Root Plan should be initialized earlier");
		}

		ActionRule rootRule = new ActionRule();
		planRoot.AddRule(rootRule);

		ActionRule beforeRule;
		EvaluationNode beforeRoot;
		// 处理消息来源，其他节点output的组合
		for (int i = 0; i < selectPaths.size(); i++) {
			cpath = selectPaths.get(i);
			if (cpath.enodes.size() == 1) {
				continue; // 不知道这种情况是否会发生，一般不会把
			}
			beforeRoot = cpath.enodes.get(cpath.enodes.size() - 2);
			beforeRule = beforeRoot.rules.get(0); // 路径中只有一个规则
			// 增加一个消息来源
			for (int j = 0; j < beforeRule.outputs.size(); j++)
				rootRule.inputs.add(beforeRule.outputs.get(j).cloneMessage());
		}
		Message cmsg1, cmsg2;
		// 处理condition条件，主要是当前节点和不同来源的数据
		for (int i = 0; i < rootRule.inputs.size(); i++) {
			cmsg1 = rootRule.inputs.get(i);
			this.buildConditionforRoot(planRoot, cmsg1);
		}
		// 处理condition条件, 这个是两个来源
		for (int i = 0; i < rootRule.inputs.size(); i++) {
			cmsg1 = rootRule.inputs.get(i);
			for (int j = i + 1; j < rootRule.inputs.size(); j++) {
				cmsg2 = rootRule.inputs.get(j);
				this.buildConditionforRoot(planRoot, cmsg1, cmsg2);
			}
		}

		for (int i = 0; i < rootRule.conditions.size(); i++) {
			int bid1 = rootRule.conditions.get(i).bid1;
			if (bid1 != -2)
				if (condOccurTimes.get(bid1) > 1)
					rootRule.conditions.get(i).unique = false;
			int bid2 = rootRule.conditions.get(i).bid1;
			if (bid2 != -2)
				if (condOccurTimes.get(bid2) > 1)
					rootRule.conditions.get(i).unique = false;
		}
		rootRule.isRootRule = true;
		rootRule.queryId = qid;
		rootRule.nodeId = pathRoot.id;
		rootRule.addSatisfiedQuery(cpath.queryID);
		// if(qid == 109)
		System.out.println("!!!" + rootRule.getContent());
	}

	private void buildConditionforRoot(EvaluationNode root, Message msg1,
			Message msg2) {
		MessageInfo info1, info2;
		int qid1, qid2; // 要判定qid1和qid2在原始的图中是否是一个
		ActionRule rootRule = root.rules.get(0);
		for (int i = 0; i < msg1.msgInfos.size(); i++) {
			info1 = msg1.msgInfos.get(i);
			for (int j = 0; j < msg2.msgInfos.size(); j++) {
				info2 = msg2.msgInfos.get(j);
				if (info1.obid == info2.obid) {
					Condition cdt = new Condition();
					cdt.eid1 = msg1.eid;
					cdt.bid1 = info1.bid;
					cdt.eid2 = msg2.eid;
					cdt.bid2 = info2.bid;
					if (condOccurTimes.containsKey(info1.bid))
						condOccurTimes.put(info1.bid,
								condOccurTimes.get(info1.bid) + 1);
					else
						condOccurTimes.put(info1.bid, 1);
					if (info2.bid != info1.bid) {
						if (condOccurTimes.containsKey(info2.bid))
							condOccurTimes.put(info2.bid,
									condOccurTimes.get(info2.bid) + 1);
						else
							condOccurTimes.put(info2.bid, 1);
					}
					rootRule.conditions.add(cdt);
				}
			}
		}
	}

	/**
	 * 这个是考虑一个来源和当前节点自身关联关系
	 * 
	 * @param root
	 * @param msg1
	 */
	private void buildConditionforRoot(EvaluationNode root, Message msg1) {
		MessageInfo info1, info2;
		int qid1, qid2; // 要判定qid1和qid2在原始的图中是否是一个
		ActionRule rootRule = root.rules.get(0);
		for (int i = 0; i < msg1.msgInfos.size(); i++) {
			info1 = msg1.msgInfos.get(i);
			if (this.isSameGraphNode(root.id, info1.bid)) {
				Condition cdt = new Condition();
				cdt.eid1 = -2;
				cdt.bid1 = -2; // 标记一下，这个等式的含义
				cdt.eid2 = msg1.eid;
				cdt.bid2 = info1.bid;
				if (condOccurTimes.containsKey(info1.bid))
					condOccurTimes.put(info1.bid,
							condOccurTimes.get(info1.bid) + 1);
				else
					condOccurTimes.put(info1.bid, 1);
				rootRule.conditions.add(cdt);
			}
		}

	}

	private boolean isSameGraphNode(int planNode1, int planNode2) {
		HashSet<Integer> set1 = new HashSet<Integer>();
		HashSet<Integer> set2 = new HashSet<Integer>();
		EvaluationPath cpath;
		EvaluationNode cnode;
		for (int i = 0; i < selectPaths.size(); i++) {
			cpath = selectPaths.get(i);
			for (int j = 0; j < cpath.enodes.size(); j++) {
				cnode = cpath.enodes.get(j);
				if (cnode.planNodeID == planNode1) {
					set1.add(cnode.id);
				}
				if (cnode.planNodeID == planNode2) {
					set2.add(cnode.id);
				}
			}
		}

		Iterator itr = set1.iterator();
		while (itr.hasNext()) {
			if (set2.contains(itr.next())) {
				return true;
			}
		}

		return false;
	}


	int rootNodeId;

	void setRootNode(int id) {
		// TODO Auto-generated method stub
		rootNodeId = id;
	}

	public void judgeSimpleNode() {
		// TODO Auto-generated method stub
		EvaluationPath cpath;
		Map<Integer, Integer> occurTimes = new HashMap<Integer, Integer>();
		for (int i = 0; i < selectPaths.size(); i++) {
			cpath = selectPaths.get(i);
			for (int j = 0; j < cpath.enodes.size() - 1; j++) {
				EvaluationNode node = cpath.enodes.get(j);
				if (occurTimes.containsKey(node.id))
					occurTimes.put(node.id, occurTimes.get(node.id) + 1);
				else
					occurTimes.put(node.id, 1);
			}
		}
		for (int i = 0; i < selectPaths.size(); i++) {
			cpath = selectPaths.get(i);
			for (int j = 0; j < cpath.enodes.size() - 1; j++) {
				EvaluationNode node = cpath.enodes.get(j);
				if (occurTimes.get(node.id) > 1)
					node.carryDid = true;
			}
		}
	}
}

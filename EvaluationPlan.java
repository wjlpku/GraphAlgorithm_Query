package Query.memory;

//�������������ִ�мƻ���
//��Ҫ��������ѡ�е�·��

import java.util.*;

import org.apache.log4j.Logger;

import Query.Utils.Debug;
import Query.Utils.parameter;

public class EvaluationPlan {
	private static final Logger LOG = Logger.getLogger(EvaluationPlan.class);
    
	public queryPatterns QueryPatterns = null;
	List<EvaluationNode>  leafNodes = new ArrayList<EvaluationNode>();	
    HashMap<Integer, EvaluationNode>  allNodes = 
    		new HashMap<Integer, EvaluationNode>();
    
    //�����type��������ӳ��
    //�����Ƕ�������붯��ʱ����Ҫ����ظ�
    HashMap<String, List<ActionRule>> actionMaps
                   = new HashMap <String, List<ActionRule>>();
    List<Integer> leafNodesId = new ArrayList<Integer>();
//    HashMap<Integer, JoinShared> rootJoinMaps = new HashMap<Integer,JoinShared>();
    //����ÿ��query id��root��ӳ��    
    HashMap<Integer, EvaluationNode> rootMap
            = new HashMap <Integer, EvaluationNode>();
    
    int maxClock=0;
    boolean intraQuery=false;
    
    public static int totalRules=0;
    public static int selectRules=0;
    public static int totalCanPaths=0;
    public static int totalSelectPaths=0;
    public EvaluationPlan(){
    	
    }
    /** �����׷�Ӻϲ�����˼
     * ���path��ÿ��query pattern��Ӧ�ĺ�ѡ�ڵ�
     * ÿ��query pattern��root�ڵ���ͬ
     * @param epath
     */
    public void addOnePath(EvaluationPath epath){
    	EvaluationNode startNode=null;
//    	System.out.println(epath.enodes.get(0).id);
    	//���ȴ����һ���ڵ�
    	for (int i=0;i<leafNodes.size();i++){
    		String label = epath.enodes.get(0).label;
    		if (leafNodes.get(i).label ==  label){
    			int tmpObid = leafNodes.get(i).tmpObid;
    			if(tmpObid == -1){
    				startNode = leafNodes.get(i); 
    				leafNodes.get(i).tmpObid = epath.enodes.get(0).id ;
    				break;
    			}else if( tmpObid == epath.enodes.get(0).id){
    				startNode = leafNodes.get(i);
    				break;
    			}
    		}
    	}
    	if (startNode==null){
    		startNode = new EvaluationNode();
    	    leafNodes.add(startNode);
    	    startNode.label = epath.enodes.get(0).label;
    	    startNode.tmpObid = epath.enodes.get(0).id;
    	    allNodes.put(startNode.id,startNode);
    	}
    	
    	//������epath��Plan�ڵ��ӳ��
		epath.enodes.get(0).setPlanNodeID(startNode.id); 
    	
    	EvaluationNode childNode;
		List<EvaluationNode> eNodes=epath.getENodes();
		List<edge> edges;
		edge cedge;
		
		//����ڶ�����root�ڵ㣬���Ҫ�ϲ��ġ�	
		
		EvaluationNode pathNode;		
		for (int i=1;i<eNodes.size()-1;i++){
		    pathNode = eNodes.get(i);
		    int lastSamePos=-1;
		    for(int j = i-1; j>=0; j--)
		    	if( eNodes.get(j).id == pathNode.id){
		    		lastSamePos=j;
//		    		System.out.println("same pos: "+j);
		    		break;
		    	}
			edges=startNode.getOutEdgesInGraph();
			childNode=null;
			for (int j=0;j<edges.size();j++){
				cedge = edges.get(j);
				//label��ͬ����cedge.toNode��Ϊ���ڵ�
				if (cedge.toNode.label == pathNode.label && 
						cedge.type == epath.edges.get(i-1).type
						&& cedge.toNode.getOutEdgesInGraph().size()!=0){
					if(lastSamePos!=cedge.toNode.lasteid)
						continue;					
					childNode = (EvaluationNode) cedge.toNode;
					break;
				}
			}
			//��ǰû�к��ʵ�
			if (childNode == null){
				childNode= new EvaluationNode();
				childNode.level = i;
				childNode.label = eNodes.get(i).label;
				if( lastSamePos != -1)
					childNode.lasteid = lastSamePos;
				startNode.addChildNode(childNode,epath.edges.get(i-1).type);
				allNodes.put(childNode.id, childNode);
			}
			pathNode.setPlanNodeID(childNode.id);
			startNode=childNode;
		}
		
		//����root�ڵ�
		EvaluationNode planRoot, pathRoot;

		planRoot = rootMap.get(epath.queryID);
		pathRoot = epath.enodes.get(epath.enodes.size() -1);
		if (planRoot==null){
			planRoot = new EvaluationNode();
			planRoot.label = pathRoot.label;
			rootMap.put(epath.queryID, planRoot);
			allNodes.put(planRoot.id, planRoot);
		}
		
		startNode.addChildNode(planRoot,epath.edges.get(eNodes.size()-2).type);
		pathRoot.setPlanNodeID(planRoot.id);
//		System.out.println(eNodes.size()+" "+startNode.id+" "+planRoot.id+" "+planRoot.getOutEdgesInGraph().size());
		
    }
    
    public void generatInitAction_separated(queryPatterns queries){
    	QueryPattern query;    	
        for (int i=0;i<queries.queryPatterns.size();i++){        	
            query = queries.queryPatterns.get(i);
            queryPatterns tmpPatterns = new queryPatterns();
            tmpPatterns.queryPatterns.add(query);
            leafNodes.clear();
            generatInitAction(tmpPatterns);
        }	
    }
    
    public void generateRules(queryPatterns queries){
    	for (int i=0;i<queries.getAllPatterns().size();i++){
        	QueryPattern cquery = queries.getAllPatterns().get(i);
        	cquery.generatePlan();
        	actionMaps = cquery.getActionMaps();
    	}
    }
    public List<Integer> getLeafNodesId(){
    	return leafNodesId;
    }
    
    /**
     * ����ǲ���ÿ����ѯ�Լ��Ķ���
     * @param queries
     */
    public void generatInitAction(queryPatterns queries){
    	
        FrequentTree ftree = queries.buildNextFrequentTree();
        EvaluationPath epath;
        totalCanPaths=queries.getAllPaths();
        LOG.info("The total candidate paths is "+queries.getAllPaths());
        //�����Ǹ���Ƶ��������������ѡ�е�·��
        while (!ftree.isEmpty()){
        	epath = ftree.LocateNextEvaluationPath(queries);
        	
//        	System.out.println("Seleted path is "+ epath.getContent());
        	
        	//this.addOnePath(epath);
        	
        	queries.removeCovered(epath);
        	ftree = queries.buildNextFrequentTree();
//        	LOG.info("The total candidate paths is "+queries.getAllPaths());
        }
        LOG.info("select paths end");
        //����ѡ�е�·����������ѯִ����
        int selectpaths=0;
        QueryPattern cquery;        
        for (int i=0;i<queries.getAllPatterns().size();i++){
        	cquery = queries.getAllPatterns().get(i);
        	intraQuery=false;
        	for (int j=0;j<leafNodes.size();j++){
        		leafNodes.get(j).tmpObid = -1;
        	}
        	
        	for (int j=0;j<cquery.selectPaths.size();j++){        		
        		this.addOnePath(cquery.selectPaths.get(j));
//        		if(i==20)
//        			System.out.println("s "+cquery.selectPaths.get(j).getContent());
        	}
        	selectpaths+=cquery.selectPaths.size();
        }
        totalSelectPaths=selectpaths;
        LOG.info("The total select paths is "+selectpaths);
        //ǰ����ɵĹ���  1��������evaluationPlan, 2 ������ÿ���Ӳ�ѯ��selectedpath
        //�������ÿ��query�е�ѡ�е�·���Ľ�����Ӧ�Ĺ���
        for (int i=0;i<queries.getAllPatterns().size();i++){
 		    cquery= queries.getAllPatterns().get(i);
 		    if (i==3){
 		    	i=3;
 		    }
// 		    cquery.judgeSimpleNode();
        	cquery.generateRule();
 	    }
 	    
        //ÿ��query��root�ڵ���Ҫ��������
        
        for (int i=0;i<queries.getAllPatterns().size();i++){
 		    cquery= queries.getAllPatterns().get(i);
        	cquery.generateRootRule(this);
 	    }
 	   
    	
    	
    	
    }
    /**
     * ǰ��Ϊ�˸ɾ��ô�����path�ռ������plan�ռ��ŵĹ�������ת��plan�ռ�
     * @param queries
     */
    public void copyFromPathtoPlan(queryPatterns queries){
    	QueryPattern cquery;
    	EvaluationPath cpath;
    	EvaluationNode enode;
    	int totalRule =0;
    	int selectRule=0;
    	for (int i=0;i<queries.getAllPatterns().size();i++){
 		    cquery= queries.getAllPatterns().get(i);
        	for (int j=0;j<cquery.selectPaths.size();j++){
        		cpath = cquery.selectPaths.get(j);
        		//���һ��root��ҪǨ�ƣ�����ǹ�����ʱ������
        		for (int k=0;k<cpath.enodes.size()-1;k++){
        			enode = cpath.enodes.get(k);
        			totalRule++;
        			boolean added = this.copyNodeFromPathtoPlan(enode);
        			if (added){
        				selectRule++;
        			}
//        			if( i<2)
//        			System.out.println("q"+i+" :"+enode.rules.get(0).getContent());
        		}
        	}
 	    }
    	totalRules = totalRule;
    	selectRules = selectRule;
    	LOG.info("Total rules="+totalRule + "; select rules ="+selectRule);
    }
    /**
     * ��path node������
     * @param pathNode
     */
    private boolean copyNodeFromPathtoPlan(EvaluationNode pathNode){
    	EvaluationNode planNode = allNodes.get(pathNode.planNodeID);
    	ActionRule pathRule = pathNode.rules.get(0);
    	return planNode.AddRule(pathRule);
    }
    
    /**
     * ���������,��ϲ�����ͬ���͵Ĺ���
     * ֱ�ӱ���allNodes���У�����label������ͬһ��label��Ӧ��action����
     */
    public void buildActionMap(queryPatterns queries){
        Iterator<Integer> itr = allNodes.keySet().iterator();
        int key;
        EvaluationNode enode;
        while (itr.hasNext()){
        	key = itr.next();
        	enode = allNodes.get(key);
        	this.addIntoMap(enode);
        }
        int sum=0;
        for(String label:actionMaps.keySet()){
        	List<ActionRule> rules = actionMaps.get(label);
//        	if(label == 83)
        		for( int i = 0; i <rules.size(); i++)
        			System.out.println(label+" "+rules.get(i).getContent());
        	sum += rules.size();
        }
        selectRules = sum;
//        for(int label:rootJoinMaps.keySet()){
//        	System.out.println(label+" "+rootJoinMaps.get(label).shareCondition.size());
//        }
    }
    /**
     * ��enode�������action ���ӵ�
     * @param enode
     */
    private void addIntoMap(EvaluationNode enode){
    	List<ActionRule> rules = actionMaps.get(enode.label);
    	if (rules == null){
    		rules = new ArrayList<ActionRule>();
    		actionMaps.put(enode.label, rules);
    	}
    	
//    	JoinShared joinShared = rootJoinMaps.get(enode.label);
//    	if (joinShared == null){
//    		joinShared = new JoinShared();
//    		rootJoinMaps.put(enode.label, joinShared);
//    	}
    	ActionRule existRule, newRule;
    	boolean found;
    	//׷��enode�Ĺ��򵽱���
    	for (int i=0;i<enode.rules.size();i++){
    		newRule = enode.rules.get(i);
//    		found = false;
//    		for (int j=0;j<rules.size();j++){
//    			existRule = rules.get(j);
//    			
//    			if (newRule.isSameAs(existRule)){
//    				found = true;
//    				if (newRule.satisfiedQueries.size()!=0){ 
//        				existRule.satisfiedQueries.addAll(newRule.satisfiedQueries);
//        			}
////        				System.out.println("???same rule?");
//    			}else if(parameter.shareRootJoin && newRule.isRootRule && existRule.isRootRule){
//    				
//    				for( int m=0; m < newRule.conditions.size(); m++){
//    					Condition cond1 = newRule.conditions.get(m);
////    					System.out.println(cond1.getContent());
//    					if(!cond1.unique)
//    						continue;
//    					cond1.clock = newRule.clock;
//    					if(joinShared.contains(cond1,newRule))
//    						continue;
//    					for(int n=0; n<existRule.conditions.size();n++){
//    						Condition cond2 = existRule.conditions.get(n);
////    						System.out.println(cond2.getContent());
//    						if(!cond2.unique)
//    							continue;
//    						if(cond1.isSameAs(cond2)){
//    							Condition cond = new Condition();
//    							cond.set(cond1);
////    							cond.shareRules.add(newRule);
////    							cond.shareRules.add(existRule);
//    							cond.clock = cond1.clock;
//    							if( newRule.clock>existRule.clock)
//    								cond.clock = existRule.clock;
//    							joinShared.shareCondition.add(cond);
////    							System.out.println(cond.getContent());
//    						}
//    					}
//    				}
//    			}
//    		}
//    		if (!found){
    			rules.add(newRule);
//    		}
    	}
    	
    }
    
    public List<ActionRule> getRulesViaLabel(String label){
    	List<ActionRule> rules = actionMaps.get(label);
    	if (rules == null)
    		rules = new ArrayList<ActionRule>();
    	return rules;
    }
    public List<ActionRule> getAllRules(){
    	List<ActionRule> allRules = new ArrayList<ActionRule>();
    	for(String key:actionMaps.keySet()){
    		allRules.addAll(actionMaps.get(key));
    	}
    	return allRules;
    }
//    public JoinShared getRootJoinViaLabel(int label){
//    	JoinShared joinShared = rootJoinMaps.get(label);
//    	if (joinShared == null)
//    		joinShared = new JoinShared();
//    	return joinShared;
//    }
    
    /**
     * ������õ���Ҫ����
     * @param queries
     */
    public void buildPlan(queryPatterns queries){
    	QueryPatterns = queries;
    	
    	//�������� 
    	this.generateRules(queries);

    }
    
    /**
     * ����ִ�е�ʱ�ӣ�ÿ��evaluation Node Ҫ�յ����е���Ϣ�������ƽ�
     * 
     */
    public void setClock(){
    	Debug.print(2, "set clock starts");
    	EvaluationNode lnode;
    	HashSet<EvaluationNode> activeNodes = new HashSet<EvaluationNode>();
    	//leaf�ڵ��clock����Ϊ0
    	//active�ǻ��д���δȷ���㼶�Ľڵ�ļ���
    	for (int i=0;i<leafNodes.size();i++){
    		lnode = leafNodes.get(i);
    		lnode.clock =0;
    		this.assignRuleClock(lnode, 0);
    		activeNodes.add(lnode);
    	}
    	
    	Iterator<EvaluationNode> itr;
    	HashSet<EvaluationNode> tmpNodes=new HashSet<EvaluationNode>();
    	EvaluationNode cnode, right, left;
    	int count=0;
    	while (!activeNodes.isEmpty()){
    		tmpNodes.clear();
    		itr = activeNodes.iterator();
//    		System.out.println(count);
    		while (itr.hasNext()){
    			cnode = itr.next();
    			boolean finish=true;
    	    	for (int i=0;i<cnode.getOutEdgesInGraph().size();i++){
    				right = (EvaluationNode) cnode.getOutEdgesInGraph().get(i).toNode;
    				this.assignClock(right);
    				if (right.clock!=-1){
    					tmpNodes.add(right);
    					count++;
    				}else{
    					finish = false;
    				}
    			}
    	    	if (!finish){
    	    		tmpNodes.add(cnode);
    	    	}
    		}
    		activeNodes.clear();
    		activeNodes.addAll(tmpNodes);
    	}
    	LOG.info("maxClock "+maxClock);
    	Debug.print(2,"set clock finishes");
    }
    
    /**
     * ��ͼΪһ���ڵ���ϱ��
     * ����ڵ���ǣ����ҽ�������ڵ����߽ڵ�ȫ�������
     * ��һ��û�б�ǣ�������clock����Ϊ-1    
     * @param right
     * @return
     */
    private void assignClock(EvaluationNode right){
    	EvaluationNode left;
    	
    	boolean leftLabeled = true;
		int maxClock =-1;
//		if( right.getInEdgesInGraph().size()>1 && right.getOutEdgesInGraph().size()>0)
//			System.out.println("!!!let me think "+right.getInEdgesInGraph().size()+" "+right.getOutEdgesInGraph().size());
		for (int j=0;j<right.getInEdgesInGraph().size();j++){
			left = (EvaluationNode) right.getInEdgesInGraph().get(j).fromNode;
			if (left.clock == -1){
				leftLabeled = false;
		    }
			if (left.clock>maxClock){
				maxClock = left.clock;
			}
		}
		if (leftLabeled){
			right.clock = maxClock+1;
			this.assignRuleClock(right, right.clock);
			if (right.clock>this.maxClock){
				this.maxClock = right.clock;
			}
		}
	}
    
    public int getMaxClock(){
    	return this.maxClock;
    }
    
    private void assignRuleClock(EvaluationNode enode, int clock){
    	ActionRule crule;
    	for (int i=0;i<enode.rules.size();i++){
    		crule = enode.rules.get(i);
    		crule.clock = clock;
    	}
    	
    }
    public void printPlan(){
    	Iterator<Integer> itr = allNodes.keySet().iterator();
    	int nodeID;
    	EvaluationNode enode;
    	String content;
    	while (itr.hasNext()){
    		nodeID = itr.next();
    		enode = allNodes.get(nodeID);
    		content = enode.getContent();
    		System.out.println(content);
      	}
    }
    
}

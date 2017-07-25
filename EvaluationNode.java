package Query.memory;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 * 是具有同一前提条件的  一组行为的节点，可以支持不同的label的响应
 * have the same prefix condition, the same action
 * @author not attributable
 * @version 1.0
 */

import java.util.*;

import Query.Utils.*;

public class EvaluationNode extends node{
    
    int queryId;
    
    int planNodeID;// node id in the whole plan 整个plan中的node所对应的id
    
    //ActionRule rule;//在node级别上的规则, 这个是frequent  
    
    List<ActionRule> rules = new ArrayList<ActionRule>();
    
    int clock=-1;

    boolean status=false;//运行状态是否满足  whether the running status is satisfied

	int tmpObid=-1;
	
	int originId=-1;
	
	public EvaluationNode(int _originId, String _label){
		originId = _originId;
		label = _label;
	}
    public EvaluationNode() {
        status = false;
        //ActionRule oneRule = new ActionRule();
        //rules.add(oneRule);
        
    }
    //应该是nodetype
    public EvaluationNode(int queryId1, int nodeId1){
    	id = nodeId1;
    	queryId = queryId1;
//    	label = nodeId1;//����Ҫ�滻��
    	//ActionRule oneRule = new ActionRule();
    	//oneRule.label = this.label;
        //rules.add(oneRule);
    }
    
    public EvaluationNode(int queryId2, int nodeId2, String nodeLabel) {
		// TODO Auto-generated constructor stub
    	id = nodeId2;
    	queryId = queryId2;
    	label = nodeLabel;//need to replace in the future 将来要替换的
	}
	public EvaluationNode(int queryId2, int nodeId2, String label, boolean carryDid2) {
		// TODO Auto-generated constructor stub
		id = nodeId2;
    	queryId = queryId2;
    	this.label = label;//need to replace in the future 将来要替换的
    	carryDid = carryDid2;
	}
	/**
     * 追加规则到当前节点
     * add the rule to the current node
     * @param oneRule
     */
    public boolean AddRule(ActionRule oneRule){
    	ActionRule crule;
//    	if(parameter.ruleMerged){
//    	for (int i=0;i<rules.size();i++){
//    		crule = rules.get(i);
//    		
//    		if (crule.isSameAs(oneRule)){
//    			//��ʹ��ȣ����Ǵ�ҿ���ָ��ͬ�Ĺ������Ҫ�ϲ�
//    			if (oneRule.satisfiedQueries.size()!=0){
//    				System.out.println("i think impossible");
//    				crule.satisfiedQueries.addAll(oneRule.satisfiedQueries);
//    			}
//    			return false;
//    		}else if(crule.isSameAs2(oneRule)){
//    			if(oneRule.satisfiedQueries.size()==0&& crule.satisfiedQueries.size()==0){    				
//    				for( int j=0; j < oneRule.outNeighbors.size(); j++){
//    					int label = oneRule.outNeighbors.get(j);
//    					if(!crule.outNeighbors.contains(label))
//    						crule.outNeighbors.add(label);
//    				}
//    				return false;
//    			}
//    			else
//    				System.out.println("!!!root2???");
//    		}else if(crule.isSameAs3(oneRule)){			//outneighbor��ͬ��rule�ϲ�
//    			int j,l;
//    			Message oneMsg,msg;
//    			if(oneRule.inputs.size()>0 && crule.inputs.size()>0){
//    				oneMsg = oneRule.inputs.get(0);
//    				msg = crule.inputs.get(0);
//    			for( j = 0; j < oneMsg.msgInfos.size(); j++){
//    				for( l = 0; l < msg.msgInfos.size(); l++)
//    					if(msg.msgInfos.get(l).isSameAs(oneMsg.msgInfos.get(j)))
//    						break;
//    				if( l == msg.msgInfos.size())
//    					msg.msgInfos.add(oneMsg.msgInfos.get(j));
//    			}
//    			}
//    			oneMsg = oneRule.outputs.get(0);
//    			msg = crule.outputs.get(0);
//    			for( j = 0; j < oneMsg.msgInfos.size(); j++){
//    				for( l = 0; l < msg.msgInfos.size(); l++)
//    					if(msg.msgInfos.get(l).isSameAs(oneMsg.msgInfos.get(j)))
//    						break;
//    				if( l == msg.msgInfos.size())
//    					msg.msgInfos.add(oneMsg.msgInfos.get(j));
//    			}
////    			if(oneRule.satisfiedQueries.size()==0&& crule.satisfiedQueries.size()==0){    				
////    				for( j=0; j < oneRule.outNeighbors.size(); j++){
////    					int label = oneRule.outNeighbors.get(j);
////    					if(!crule.outNeighbors.contains(label))
////    						crule.outNeighbors.add(label);
////    				}
//////    				return false;
////    			}
////    			else
////    				System.out.println("!!!root3???");
//    			return false;
//    		}
//    	}
//    	}
    	rules.add(oneRule);
    	//we add the new rule, and return true
    	return true;
    	
    }
    
    
    public boolean getSatisfied(){
        return status;
    }
    
    public void setPlanNodeID(int id){
    	planNodeID = id;
    }
    
    public void setOriginID(int id){
    	originId = id;
    }
    
    public int getPlanNodeID(){
    	return planNodeID;
    }
    /**
     * we copy the previous node output into current node input and output 这个是将上一个节点的output复制到当前节点的input 和 output
     * we can change the current node output by add and remove function
     * @param label 
     * @param enode1
     */
    public void copyRuleFromBeforeNode(int planNodeID, EvaluationNode beforeNode, String label){
    	ActionRule crule;
    	if (this.rules.size()==0){
    		crule = new ActionRule();
    		this.rules.add(crule);
    	}else{
    		crule = this.rules.get(0); //why get(0) ?
    	}
    	//when this function is call , the leafnode's before node is null 这个函数调用的时候，叶子节点的before node就为空
    	Message msg, newMsg;
    	if (beforeNode !=null){
    		ActionRule beforeRule = beforeNode.rules.get(0);
        	for (int i=0;i<beforeRule.outputs.size();i++){
        		msg = beforeRule.outputs.get(i);
        		newMsg = msg.cloneMessage();
        		crule.inputs.add(newMsg);
        		newMsg = msg.cloneMessage();
        		newMsg.eid =planNodeID; //it means this message status is not same as the input 就是说，这个message的状态和input的时候不一样
        		if(label.equals("?"))
        			newMsg.label=label;
        		crule.outputs.add(newMsg); 
          	}
    	}else{
    		msg = new Message();
    		msg.eid = planNodeID;
    		if(label.equals("?"))
    			msg.label=label;
    		crule.outputs.add(msg);
    	}
    	
    	
    }
    
    public String getContent(){
    	String content="";
    	content = content +"id="+this.id+" label="+this.label+"\r\n";
    	if (rules.size()>0){
    		content = content + " "+ rules.size()+" rules \r\n";
    		ActionRule crule;
    		for (int i=0;i<rules.size();i++){
    			crule = rules.get(i);
    			content = content + crule.getContent() +"\r\n";
    		}
    	}
    	return content;
    }
    
 }

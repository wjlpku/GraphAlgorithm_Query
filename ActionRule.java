package Query.memory;

/**
 * �����evaluation plan������˵��
 * @author Administrator
 *
 */

import java.util.*;

public class ActionRule {
	
//	int label;//�����˵�����ʲô�ط�
	public boolean isLeafRule = false;
	public boolean isRootRule = false;
	public int clock=-1; //ʲôʱ��ִ���������
	
	public List<Message> inputs = new ArrayList<Message>();
	
	//�������ʽ������ ��Դ��eid��qid=��Դ2.qid����������Ƕ��
	
	public List<Condition> conditions = new ArrayList<Condition>();
    
	//�����output�Ĺ���ǰ���Ǹ���neigbor node�ı��
	
	//������ ����outNeighbors��ÿ���ڵ㣬����target����Ϣ
	//���target����Ϣ���Ƕ������һ���ڵ㿽�����Ǵ�����ڵ����������
	public List<Message> outputs = new ArrayList<Message>();
    //�����output�� ����ȡһ�������û��outputmessage,���������
	public List<Integer> satisfiedQueries = new ArrayList<Integer>();
	
	public List<String> outNeighbors = new ArrayList<String>();
	public int queryId;
	public int nodeId;
	public int collectedNodeId=-1;
	public String label="";
	public boolean isSolved=false;
	public boolean hasCircle=false;
	public boolean circleNext=false;
	public List<Integer> circleStartNodes = new ArrayList<Integer>();
	//used when reachability
	public boolean reached=false;
	
	public ActionRule(){
		
	}
	
	public void addNeighborLabel(String label){
		outNeighbors.add(label);
	}
	
	public void copySource(ActionRule preRule){
		
		this.inputs.addAll(preRule.inputs);
		
	}
	
	public void addSatisfiedQuery(int qid){
		for (int i=0;i<satisfiedQueries.size();i++){
			if (satisfiedQueries.get(i).intValue()==qid){
				return;
			}
		}
		satisfiedQueries.add(qid);
	}
	/**
	 * �����ǰ���eid,Я����ǰ��Ҫ��¼�Ĳ�ѯ�ڵ�����
	 * ����������õ�ʱ��output messgae �����һ������Ȼ��message�е�messageinfo���Զ��
	 * @param preRule
	 * @param eid
	 */
	public void removeBranchFromOutput(int bid){
		if (outputs.size()==0)
			return;
		Message msg=outputs.get(0);
		MessageInfo info;
		for (int i=0;i<msg.msgInfos.size();i++){
			info = msg.msgInfos.get(i);
			if (info.bid == bid ){
				msg.msgInfos.remove(info);
				break;
			}
		}
	}
	
	/**
	 * �����ǰ���eid
	 * @param preRule
	 * @param eid
	 */
	public void addBranchIntoOutput(int eid, int obid1){
		Message oneMsg;
		if (this.outputs.size()==0){
			oneMsg= new Message();
			this.outputs.add(oneMsg);
		}else{
			oneMsg = this.outputs.get(0);
		}
			
		MessageInfo info = new MessageInfo();
		info.bid = eid;
		info.obid = obid1;
		oneMsg.msgInfos.add(info);
	}
	/**
	 * ����������ͬ����˵������ͬ��������ͬ�������ͬ��neighbour��ͬ
	 * �ƺ��е�ǿ��
	 * 
	 * ��ͬ�����ڲ����ظ���������Ƴ�
	 * 
	 * @param another
	 * @return
	 */
	public boolean isSameAs(ActionRule second){
		if (! this.isSameMessageList(this.inputs, second.inputs)){
			return false;
		}
		if (! this.isSameMessageList(this.outputs, second.outputs)){
			return false;
		}
		if (! this.isSameCondition(second.conditions)){
			return false;
		}
		if (! this.isSameNgbs(second.outNeighbors)){
			return false;
		}
		
		return true;
		
	}
	
	public boolean isSameAs2(ActionRule second){
		if (! this.isSameMessageList(this.inputs, second.inputs)){
			return false;
		}
		if (! this.isSameMessageList(this.outputs, second.outputs)){
			return false;
		}
		if (! this.isSameCondition(second.conditions)){
			return false;
		}
//		if (! this.isSameNgbs(second.outNeighbors)){
//			return false;
//		}
		
		return true;
		
	}
	
	public boolean isSameAs3(ActionRule second){
//		if (! this.isSameMessageList(this.inputs, second.inputs)){
//			return false;
//		}
//		if (! this.isSameMessageList(this.outputs, second.outputs)){
//			return false;
//		}
		if(second.isRootRule || isRootRule)
			return false;
		if(outputs.get(0).eid != second.outputs.get(0).eid)
			return false;
		if (! this.isSameCondition(second.conditions)){
			System.out.println("i think impossible");
			return false;
		}
			
		if (! this.isSameNgbs(second.outNeighbors)){
			return false;
		}
		
		return true;
		
	}
	
	private boolean isSameMessageList(List<Message> msgs1, List<Message> msgs2){
		if (msgs1.size() != msgs2.size())
			return false;
		Message msg1, msg2;
		boolean found;
		for (int i=0;i<msgs1.size();i++){
			msg1 = msgs1.get(i);
			found = false;
			for (int j=0;j<msgs2.size();j++){
				msg2 = msgs2.get(j);
				if (msg1.isSameAs(msg2)){
					found = true;
					break;
				}
			}
			if (!found){
				return false;
			}
		}
		return true;
	}
	
	
	public boolean isSameCondition(List<Condition> seconds){
		boolean found;
		if (this.conditions.size()!= seconds.size())
			return false;
		found = false;
		Condition con1, con2;
		for (int i=0;i<conditions.size();i++){
			con1 = conditions.get(i);
			found =false;
			for (int j=0; j<seconds.size();j++){
				con2 = seconds.get(j);
				if (con1.isSameAs(con2)){
					found= true;
					break;
				}
			}
			if (!found){
				return false;
			}
		}
		return true;
	}
	
	public boolean isSameNgbs(List<String> seconds){
		boolean found;
		if (this.outNeighbors.size()!= seconds.size())
			return false;
		found = false;
		String n1, n2;
		for (int i=0;i<outNeighbors.size();i++){
			n1 = outNeighbors.get(i);
			found =false;
			for (int j=0; j<seconds.size();j++){
				n2 = seconds.get(j);
				if (n1 == n2){
					found= true;
					break;
				}
			}
			if (!found){
				return false;
			}
		}
		return true;
	}
	
	public String getContent(){
		String content=isRootRule+" "+label+" "+collectedNodeId+" ";
		Message msg;
		for(int i =0; i < circleStartNodes.size(); i++)
			content += "clist "+circleStartNodes.get(i);
		if (inputs.size()>0){
			content = content +" IM["; //input message
			for (int i=0;i<inputs.size();i++){
				msg = inputs.get(i);
				content = content+msg.getContent();
			}
			content = content +"]";
		}
		
		if (conditions.size()>0){
			content = content +" CO["; //condition
			Condition cond;
			for (int i=0;i<conditions.size();i++){
				cond = conditions.get(i);
				content = content+cond.getContent();
				if (i!=satisfiedQueries.size()-1){
					content = content+" && ";
				}
			}
			content = content +"]";
		}
		
		if (outputs.size()>0){
			content = content +" OM["; //output message
			for (int i=0;i<outputs.size();i++){
				msg = outputs.get(i);
				content = content+msg.getContent();
				if (i!=outputs.size()-1){
					content = content+",";
				}
			}
			content = content +"]";
		}
		
		if (outNeighbors.size()>0){
			content = content +" TL["; //to labels
			for (int i=0;i<outNeighbors.size();i++){
				content = content+outNeighbors.get(i);
				if (i!=outNeighbors.size()-1){
					content = content+",";
				}
			}
			content = content +"]";
		}
		
		if (this.satisfiedQueries.size()>0){
			content = content +" SQ["; //satisfied query
			for (int i=0;i<satisfiedQueries.size();i++){
				content = content+ satisfiedQueries.get(i).intValue();
				if (i!=satisfiedQueries.size()-1){
					content = content+",";
				}
			}
			content = content +"]";
		}
		content = content+"\r\n";
		return content;
		
	}

	public ActionRule cloneActionRule() {
		// TODO Auto-generated method stub
		ActionRule rule = new ActionRule();
		for( int i = 0; i < inputs.size(); i++)
			rule.inputs.add(inputs.get(i).cloneMessage());
		for( int i = 0; i < outputs.size(); i++)
			rule.outputs.add(outputs.get(i).cloneMessage());
		for( int i = 0; i < outNeighbors.size(); i++)
			rule.outNeighbors.add(outNeighbors.get(i));
		for( int i = 0; i < satisfiedQueries.size(); i++)
			rule.satisfiedQueries.add(satisfiedQueries.get(i));
		rule.clock = clock;
		rule.isRootRule = isRootRule;
		rule.isLeafRule = isLeafRule;
		rule.collectedNodeId = collectedNodeId;
		rule.label = label;
		for( int i = 0; i < circleStartNodes.size(); i++)
			rule.circleStartNodes.add(circleStartNodes.get(i));
		for( int i=0; i < conditions.size(); i++){
			Condition cond = new Condition();
			cond.set(conditions.get(i));
			rule.conditions.add(cond);
		}
		return rule;
	}
	
	

}

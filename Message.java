package Query.memory;

/**
 * 
 * @author Administrator
 *
 */

import java.util.*;

public class Message {
    public int eid;//the id from queryplan  , the id is sole(唯一的)
            //it is just the last evaluation node id 就是上一个evaluation node的id
    public String label="";
    public List<MessageInfo> msgInfos = new ArrayList<MessageInfo>();
    public List<String> midResult = new ArrayList<String>();
    
    public Message(int _eid){
    	eid=_eid;
    }
    public Message() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * sometimes we cannot directly reference, because it is different, we need clone
     * 有的时候不能直接引用，大家还是有不同，可能需要实体化
     * @return
     */
    public Message cloneMessage(){
    	Message oneMsg= new Message();
    	oneMsg.eid = this.eid;
    	MessageInfo oneInfo, newInfo;
    	for (int i=0;i<msgInfos.size();i++){
    		oneInfo = msgInfos.get(i);
    		newInfo = oneInfo.cloneInfo();
    		oneMsg.msgInfos.add(newInfo);
    	}
    	oneMsg.label = label;
    	oneMsg.midResult.addAll(midResult);
//    	for (int i=0;i<midResult.size();i++){    		
//    		oneMsg.midResult.add(midResult.get(i));
//    	}
    	return oneMsg;
    	
    }
    
    
    public boolean isSameAs(Message second){
    	if (this.eid != second.eid)
    		return false;
    	
    	if (this.msgInfos.size()!= second.msgInfos.size())
    		return false;
    	
    	boolean found;
    	MessageInfo info1, info2;
    	//each msgInfos in info1 should have the same one in info2
    	for (int i=0;i<msgInfos.size();i++){
    		info1 = msgInfos.get(i);
    		found=false;
    		for (int j=0;j<second.msgInfos.size();j++){
    			info2 = second.msgInfos.get(j);
    			if (info1.isSameAs(info2)){
    				found = true;
    				break;
    			}
    		}
    		if (! found){
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    public String getContent(){
    	String content="("+ this.eid+","+this.label;
    	if (msgInfos.size()>0){
    		content = content +"(";
    		for (int i=0;i<msgInfos.size();i++){
    	       content = content+ msgInfos.get(i).bid+" "+msgInfos.get(i).obid;	
    	       if (i!=msgInfos.size()-1){
    	    	   content = content +",";
    	       }
    		}
    		content = content +")";
    	}
    	content = content +")";
    	return content;
    }
	public String getContent2() {
		// TODO Auto-generated method stub
		String content=this.eid+": ";
		for( int i = 0; i < msgInfos.size(); i++)
			content += msgInfos.get(i).bid+" "+msgInfos.get(i).did+" ";
		content += "\tmidResult: ";
		for( int i = 0; i < midResult.size(); i++)
			content += midResult.get(i)+",";
		return content;
	}
	public String getResult(){
		String content="(";
    	if (msgInfos.size()>0){;
    		for (int i=0;i<msgInfos.size();i++){
    	       content += msgInfos.get(i).bid+":"+msgInfos.get(i).did;	
    	       if (i!=msgInfos.size()-1){
    	    	   content = content +",";
    	       }
    		}
    	}
    	content = content +")";
    	return content;
	}
    public String locateDataBranchingId(int bid){
    	MessageInfo info;
    	for (int i=0;i<msgInfos.size();i++){
    		info = msgInfos.get(i);
    		if (info.bid == bid){
    			return info.did;
    		}
    	}
    	return "-1";
    }
}

package Query.memory;

public class MessageInfo {
	public int bid;// it is the branching node id for the plan
	int obid;//it is the original branching node id , it is single??
	public String did;//it is  the branching node id for the data graph 
	
	public MessageInfo cloneInfo(){
		MessageInfo info = new MessageInfo();
		info.bid= this.bid;
		info.obid = this.obid;
		info.did = this.did;
		return info;
	}
	/**
	 * we do not use did, did is use in the running
	 * we do not use obid , because it is use in the single query, now is the many query 
	 * 
	 * @param second
	 * @return
	 */
	public boolean isSameAs(MessageInfo second){
		if (this.bid==second.bid){
			return true;
		}
		return false;
	}
}

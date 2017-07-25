package Query.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Administrator
 *
 */

public class Condition {
	
	 // eid  from which son node
	 // bid  the branching node we need to think 
     public int eid1 = -1, bid1 = -1;
     public int eid2 = -1, bid2 = -1;

//     public List<ActionRule> shareRules = new ArrayList<ActionRule>();
     public int clock = -1;
     public boolean unique = true;
     public Condition(){
    	 
     }
     
     public void setFirst(int eid, int bid){
    	 eid1 = eid;
    	 bid1 = bid;
     }
     
     public void setConditions(int eid, int bid){
    	 eid2 = eid;
    	 bid2 = bid;
     }
     
     public void set(Condition cond){
    	 eid1 = cond.eid1;
    	 eid2 = cond.eid2;
    	 bid1 = cond.bid1;
    	 bid2 = cond.bid2;
     }
     
     public boolean isSameAs(Condition second){
    	
    	 return (this.eid1 == second.eid1 &&
    			this.eid2 == second.eid2 &&
    			this.bid1 == second.bid1 &&
    			this.bid2 == second.bid2) || (
    			this.eid1 == second.eid2 &&
    	    	this.eid2 == second.eid1 &&
    	    	this.bid1 == second.bid2 &&
    	    	this.bid2 == second.bid1);
     }
     
     public String getContent(){
    	 String content=""+clock+" "+unique;
    	 if (this.eid1!= -1){
    		 content += eid1+"."+bid1+"="+eid2+"."+bid2;
    	 }
    	 return content;
     }
    
     
}

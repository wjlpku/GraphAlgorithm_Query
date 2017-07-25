package Query.memory;

import java.util.ArrayList;
import java.util.List;

public class JoinShared {
	public List<Condition> shareCondition = new ArrayList<Condition>();	
	public boolean contains(Condition cond, ActionRule newRule) {
		// TODO Auto-generated method stub
		for( int i=0; i < shareCondition.size(); i++)
			if(shareCondition.get(i).isSameAs(cond)){
//				shareCondition.get(i).shareRules.add(newRule);
				if(cond.clock<shareCondition.get(i).clock)
					shareCondition.get(i).clock = cond.clock;
				return true;
			}
		return false;
	}
	public JoinShared clone(){
		JoinShared joinShared = new JoinShared();
		for( int i=0; i < shareCondition.size(); i++){
			Condition cond = new Condition();
			cond.set(shareCondition.get(i));
			joinShared.shareCondition.add(cond);
		}
		return joinShared;
	}
}

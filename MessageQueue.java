package Query.memory;

/**
 * 在数据节点中，同一状态的消息可能有多分，如不同的data id
 * 这些数据保存在Message queue中
 * 这个message queue中的message的queryid都是一样的
 * 对于仅仅有queryid的消息来说，这个queue可能只有一个数据
 * @author Administrator
 *
 */

import java.util.*;

public class MessageQueue {
	int eid;// 这个是evaluation plan 中的上一个节点的id
	public List<Message> msgs = new ArrayList<Message>();
	
	public MessageQueue(int eid1){
		eid = eid1;
	}
	
	public void addMessage(Message msg){
		msgs.add(msg);
	}

	public MessageQueue cloneMessageQueue() {
		// TODO Auto-generated method stub
		MessageQueue queue = new MessageQueue(this.eid);
		Message oneMsg;
		for( int i = 0; i < msgs.size(); i++){
			oneMsg = msgs.get(i).cloneMessage();
			queue.addMessage(oneMsg);
		}
		return queue;
	}
	
}

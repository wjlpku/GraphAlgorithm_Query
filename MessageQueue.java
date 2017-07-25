package Query.memory;

/**
 * �����ݽڵ��У�ͬһ״̬����Ϣ�����ж�֣��粻ͬ��data id
 * ��Щ���ݱ�����Message queue��
 * ���message queue�е�message��queryid����һ����
 * ���ڽ�����queryid����Ϣ��˵�����queue����ֻ��һ������
 * @author Administrator
 *
 */

import java.util.*;

public class MessageQueue {
	int eid;// �����evaluation plan �е���һ���ڵ��id
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

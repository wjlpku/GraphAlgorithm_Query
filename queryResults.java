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

public class queryResults {
    static HashSet<String> results = new HashSet<String>();  //String的形式就是 nodeid<->queryid
    static int totalMessages;
    public queryResults() {
    }
    public static void clearAll(){
        results.clear();
        totalMessages =0;
    }

    public void printResults(){
        String content ="";
        content = content + "Total Messages ="+totalMessages+"\r\n";
        content = content + "Total Satisifed Query Node Pairs="+ results.size()+"\r\n";
        Iterator itr = results.iterator();
        String result;
        while (itr.hasNext()){
            result = (String) itr.next();
            content = content + result +"\r\n";
        }
        System.out.println(content);
    }

    public static void addOneResult(String result){
        results.add(result);
    }
}

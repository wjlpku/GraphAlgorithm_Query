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
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import Query.Utils.*;
import Query.database.*;


public class queryPatterns {
	private static final Logger LOG = Logger.getLogger(queryPatterns.class);
    List<QueryPattern> queryPatterns = new ArrayList<QueryPattern>();
   

    public queryPatterns() {
        
    }
    
    public List<QueryPattern> getAllPatterns(){
    	return queryPatterns;
    }

    public void initAllQueries(String prefix){
        int i=0;
        boolean finish=false;
        while (! finish){
            graph dgraph= new graph();
            dgraph.constructNodeFromDB(database.sta, prefix,"qid="+i);
            dgraph.constructEdgsFromDB(database.sta, prefix,"qid="+i);
            if (dgraph.nodes.size()!=0){
                QueryPattern query = new QueryPattern(dgraph, i);
                queryPatterns.add(query);
            }else{
                finish = true;
            }
            i=i+1;
//            if(i>2)break;
        }
    }
	public void initAllQueriesByFile(String path) {
		// TODO Auto-generated method stub
		 int i=0;
	        boolean finish=false;
	        int t=0;
	        while(!finish){
	            graph dgraph= new graph();
	            dgraph.constructNodeFromFile(path+"/node",i);
	            dgraph.constructEdgeFromFile(path+"/edge",i);
	            	t=i;
	            if (dgraph.nodes.size()!=0){
	                QueryPattern query = new QueryPattern(dgraph, t);
	                queryPatterns.add(query);
	            }else{
	                finish = true;
	            }
	            i=i+1;
	        }
	}

	public void initAllQueriesByFile_QueryId(String path, int queryId) {
		// TODO Auto-generated method stub
		graph dgraph = new graph();
		dgraph.constructNodeFromFile(path + "/node", queryId);
		dgraph.constructEdgeFromFile(path + "/edge", queryId);
		if (dgraph.nodes.size() != 0) {
			QueryPattern query = new QueryPattern(dgraph, 0);
			queryPatterns.add(query);
		} else {
			LOG.info("query input error");
		}

	}
    public void initAllQueries1(String prefix){
        int i=0;
        graph dgraph= new graph();
        dgraph.constructNodeFromDB(database.sta, prefix,"qid="+i);
        dgraph.constructEdgsFromDB(database.sta, prefix,"qid="+i);
        if (dgraph.nodes.size()!=0){
                QueryPattern query = new QueryPattern(dgraph, i);
                queryPatterns.add(query);
        }
    }
 
    public FrequentTree buildNextFrequentTree(){
    	QueryPattern query;
    	FrequentTree ftree= new FrequentTree();
        for (int i=0;i<queryPatterns.size();i++){
            query = queryPatterns.get(i);
            ftree.addCandidatePathList(query.candidatePaths);
        }
        return ftree;
    }
    /**
     * epath 就是从frequent path tree选中的路径，和节点所附属的路径不一致
     * epath 中带有和plan node的关系
     * @param epath
     */
    public void removeCovered(EvaluationPath epath){
    	QueryPattern query;
    	for (int i=0;i<queryPatterns.size();i++){
            query = queryPatterns.get(i);
            query.removeCovered(epath);
        }
        
    }
    
    public int getAllPaths(){
    	int cnt=0;
    	QueryPattern query;
    	for (int i=0;i<queryPatterns.size();i++){
            query = queryPatterns.get(i);
            cnt=cnt+query.candidatePaths.size();
        }
    	return cnt;
    }
    
 
}

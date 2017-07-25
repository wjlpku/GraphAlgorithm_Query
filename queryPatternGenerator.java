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

import Query.memory.*;
import Query.database.*;

public class queryPatternGenerator {

    int nodeSize=4;
    int querySize =2;
    List<node> queryNodes;
    List<edge> queryEdges;
    public queryPatternGenerator() {


    }
    public void generateQueryNodeDFS(graph dgraph){
        queryNodes = new ArrayList<node>(querySize);
        Random rnd = new Random();
        int nodeID = Math.abs(rnd.nextInt()) % dgraph.nodes.size();
        node cnode = dgraph.getNodeById(nodeID);
        queryNodes.add(cnode);
        cnode.queryID=0;
        int idx;
        int idle =0;
        for (int i=0;i<nodeSize && idle<100;i++){
            idx = Math.abs(rnd.nextInt()) % cnode.getToNodesList().size();
            cnode=cnode.getToNodesList().get(idx);
            if (!queryNodes.contains(cnode)){
                queryNodes.add(cnode);
                cnode.queryID=i+1;
            }else{
                i--;
                idle++;
            }

        }
    }

    public void generateQueryNodeRandom(graph dgraph){
        queryNodes = new ArrayList<node>(querySize);
        Random rnd = new Random();
        int nodeID;
        node cnode;
        for (int i=0;i<nodeSize;i++){
            nodeID = Math.abs(rnd.nextInt()) % dgraph.nodes.size();
            cnode = dgraph.getNodeById(nodeID);
            cnode.queryID=0;
            queryNodes.add(cnode);
        }
    }

    public void buildEdges(){
        node first, second;
        edge newEdge;
        queryEdges = new ArrayList<edge>();
        for (int i=0; i<queryNodes.size();i++){
            first = queryNodes.get(i);
            for (int j=i+1;j<queryNodes.size();j++){
                second = queryNodes.get(j);
                newEdge = first.getOutEdgeToNodeInGraph(second.id);
                if (newEdge!=null){
                    queryEdges.add(newEdge);
                }
            }

        }
    }

    public void saveIntoDB(int queryID){
        databaseNode.insertQueryNodes(database.sta,"hdel2kq",queryID,queryNodes);
        databaseEdge.insertQueryEdges(database.sta,"hdel2kq",queryID,queryEdges);
    }
    //外部调用
    public void generateQueryPattern(graph dgraph){
        for (int i=0;i<querySize;i++){
            this.generateQueryNodeDFS(dgraph);
            this.buildEdges();
            this.saveIntoDB(i);
        }

    }





}

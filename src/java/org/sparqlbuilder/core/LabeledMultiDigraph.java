/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sparqlbuilder.core;

/**
 *
 * @author atsuko
 */
import java.util.*;


public class LabeledMultiDigraph {
    List<List<LabeledEdge>> adjlist;
    List<String> labels;
    HashMap<String,Integer> labelednodes;
    List<Map<Integer,List<LabeledEdge>>> gadjlist; // grouped adj list: node, node-edge list
    
    public class LabeledEdge{
        Integer node;
        Object label;
        
        public LabeledEdge(Integer node, Object label){
            this.node = node;
            this.label = label;
        }
        
        public Object getLabel(){
            return label;
        }
    }
    
    public LabeledMultiDigraph(){
        adjlist = new ArrayList<List<LabeledEdge>>();
        labels = new LinkedList<String>();
        labelednodes = new HashMap<String, Integer>();
        gadjlist = new ArrayList<Map<Integer,List<LabeledEdge>>>();
    }
    
    public void addNode(String label){
        labelednodes.put(label, labels.size());
        labels.add(label);
        adjlist.add(new LinkedList<LabeledEdge>());
        gadjlist.add(new HashMap<Integer, List<LabeledEdge>>());
    }
    
    public void addEdge(Integer node1, Integer node2, Object elabel){
        if ( labels.size() < node1 || labels.size() < node2 ){
            System.err.println("Error for Edge Addition: No Node for the Edge");
            return;
        }
        LabeledEdge edge = new LabeledEdge(node2, elabel);
        adjlist.get(node1).add(edge);
        
        Map<Integer, List<LabeledEdge>> edges = gadjlist.get(node1);
        List<LabeledEdge> sedge = edges.get(node2);
        if ( sedge == null ){
            sedge = new LinkedList<LabeledEdge>();
            edges.put(node2, sedge);
        }
        sedge.add(edge);
    }
}

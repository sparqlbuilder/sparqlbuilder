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
import java.io.Serializable;

public class ClassGraph extends LabeledMultiDigraph implements Serializable{
    int nsteps = 3;
    //int limit = 100;
    
    List<String> nodeType;
    String sparqlEndpoint;
    Set<Integer> visited;
    List<Map<Integer, Integer>> edgeweight;
    List<Integer> nodeweight;
    Map<String, Boolean> checkedpaths;
    
    public class LinkAndPath{
        String originalClassURI; // originalClasssURI -classLink.propertyURI-> classLink.linkedClassURL
        ClassLink classLink;
        List<ClassLink> path;
        Set<String> classURIs; // apearing class URIs in the path
        
        
        public LinkAndPath(ClassLink classLink, List<ClassLink> path){
           this.classLink = classLink;
           this.path = path;
        }
        
        public LinkAndPath(ClassLink classLink, List<ClassLink> path, String originalClassURI){
           this.classLink = classLink;
           this.path = path;
           this.originalClassURI = originalClassURI;
        }

        public LinkAndPath(ClassLink classLink, List<ClassLink> path, String originalClassURI, Set<String> classURIs){
           this.classLink = classLink;
           this.path = path;
           this.originalClassURI = originalClassURI;
           this.classURIs = classURIs;
        }
    }

    public ClassGraph(){ // not used -> For new version
        super();
        nodeType = new LinkedList<String>();
    }
    
/*    public ClassGraph(RDFSchemaAnalyzer rdfsa){ // for experiment
        super();
        nodeType = new LinkedList<String>();
        setClassGraph(rdfsa);
    }
    
    public ClassGraph(RDFSchemaAnalyzer rdfsa, String sparqlEndpoint, String startClass){ // used
        super();
        nodeType = new LinkedList<String>();
        setPartClassGraph(rdfsa, sparqlEndpoint, startClass);
    }/*    public OWLClassGraph(RDFSchemaAnalyzer rdfsa){ // for experiment
        super();
        nodeType = new LinkedList<String>();
        setClassGraph(rdfsa);
    }
    
    public OWLClassGraph(RDFSchemaAnalyzer rdfsa, String sparqlEndpoint, String startClass){ // used
        super();
        nodeType = new LinkedList<String>();
        setPartClassGraph(rdfsa, sparqlEndpoint, startClass);
    }
  */ // For old version
    
    public ClassGraph(String ep, Set<String> cl, Set<String> cr, Map<String, Integer> cent){ // used
        super();
        sparqlEndpoint = ep;
        setClassGraph(cl, cr, cent);
    }

    public ClassGraph(String ep, Set<String> cl, Set<String> cr){ // used
        super();
        sparqlEndpoint = ep;
        setClassGraph(cl, cr);
    }

    public int getNumberOfEdge(String url){
        Integer node = labelednodes.get(url);
        if (node == null){
            return 0;
        }
        return adjlist.get(node).size();
    }
    
    public boolean visitedNode(String classURI){
        if ( visited.contains(labelednodes.get(classURI)) ){
            return true;
        }
        return false;
    }
    
    public Path[] getPaths(String startClass, String endClass){
        List<List<ClassLink>> paths = searchPaths(startClass, endClass);

        List<Path> sortedpaths = new LinkedList<Path>();
        ListIterator<List<ClassLink>> pit = paths.listIterator();
        int j = 0;
        while ( pit.hasNext() ){
            Path path = new Path();
            path.setStartClass(startClass);
            List<ClassLink> crrpath = pit.next();
            path.setClassLinks(crrpath);
            ListIterator<ClassLink> cit = crrpath.listIterator();
            int min = Integer.MAX_VALUE;
            while ( cit.hasNext() ){
                ClassLink cl = cit.next();
                if ( cl.getNumOfLinks() < min ){
                    min = cl.getNumOfLinks();
                }
            }
            path.setMin(min);
            // using length of path
            //int rankwidth = (int) ( ( min * nsteps )/ crrpath.size() );
            //path.setWidth(500000 - crrpath.size()*100000 - min);
            double prob = computePrOfPath(path);
            path.setWidth(prob);
            sortedpaths.add(path);
            j++;
        }
        Path[] patharray = new Path[paths.size()];
        Collections.sort(sortedpaths);
        Iterator<Path> pait = sortedpaths.listIterator();
        int i = 0;
        while ( pait.hasNext() ){
            patharray[paths.size()-i-1] = pait.next();
            i++;
        }
        return patharray;
    }
    
    private List<List<ClassLink>> searchPaths(String startClass, String endClass){
        //int asked = 0;
        checkedpaths = new HashMap<String, Boolean>();
        List<List<ClassLink>> paths = new ArrayList<>();
        Integer snode = labelednodes.get(startClass);
        Integer enode = labelednodes.get(endClass);
        List<List<Integer>> simplePaths = searchSimplePaths(snode, enode);
        
        ListIterator<List<Integer>> pit = simplePaths.listIterator();
        //System.out.println("SPATH:");
        //System.out.println(simplePaths.size());
        while( pit.hasNext()){
            List<Integer> spath = pit.next();
            List<List<ClassLink>> convertedPaths = convertSimplePathToPaths(spath);
            paths.addAll(convertedPaths);
        }
        //System.out.println("PATH:");
        //System.out.println(paths.size());
        return paths;
    }

    private List<List<Integer>> searchSimplePaths(Integer snode, Integer enode){
        List<List<Integer>> simplePaths = new LinkedList<>();
        List<List<Integer>> lp = new LinkedList<>();
        List<Integer> ini = new LinkedList<Integer>(); // initial path
        ini.add(snode);
        lp.add(ini);
        for (int i = 0; i < nsteps; i++ ){
            ListIterator<List<Integer>> lit = lp.listIterator();
            List<List<Integer>> nextlp = new LinkedList<>();
            while ( lit.hasNext() ){ 
                List<Integer> crrpath = lit.next();
                Integer crrnode = crrpath.get(crrpath.size()-1);
                Set<Integer> nexts = gadjlist.get(crrnode).keySet();
                Iterator<Integer> nit = nexts.iterator();
                while( nit.hasNext() ){
                    Integer nextnode = nit.next();
                    if ( crrpath.contains(nextnode) ){ continue; }
                    List<Integer> nextpath = new LinkedList<Integer>(crrpath); // copy
                    nextpath.add(nextnode);
                    if ( nextnode.equals(enode) ){
                        simplePaths.add(nextpath);
                        continue;
                    }
                    nextlp.add(nextpath);
                }
	    }
            lp = nextlp;
        }        
        return simplePaths;
    }
    
    
    private List<List<ClassLink>> convertSimplePathToPaths(List<Integer> simplePath){
        List<List<ClassLink>> paths = new LinkedList<List<ClassLink>>();
        ListIterator<Integer> spit = simplePath.listIterator();
        Integer start = spit.next();
        String startClass = this.labels.get(start);
        Integer end = spit.next();
        List<LabeledEdge> edges = gadjlist.get(start).get(end);
        ListIterator<LabeledEdge> eit = edges.listIterator();
        while ( eit.hasNext() ){
            List<ClassLink> cl = new LinkedList<ClassLink>();
            cl.add((ClassLink)eit.next().getLabel());
            paths.add(cl);
        }
        start = end;
        while( spit.hasNext() ){
            end = spit.next();
            // start-end
            edges = gadjlist.get(start).get(end);
            List<List<ClassLink>> tmppaths = new LinkedList<List<ClassLink>>();            
            // current path
            ListIterator<List<ClassLink>> pit = paths.listIterator();
            while ( pit.hasNext() ){
                List<ClassLink> basepath = pit.next();
                eit = edges.listIterator();
                while ( eit.hasNext() ){
                    ClassLink cl = (ClassLink) eit.next().label;
                    List<ClassLink> addedpath = new LinkedList<ClassLink>(basepath);
                    addedpath.add(cl);
                    tmppaths.add(addedpath);
                }
            }
            paths = tmppaths;
            start = end;
        }        
        return paths;
    }
    
    /*
    private void setClassGraph(RDFSchemaAnalyzer rdfsa){
        // setNodes
        SClass[] classes = null;
        try{
            classes = rdfsa.getOWLClasses(null, null, null, true);
        }catch(Exception e){
            System.err.println(e); return;
        }
        for (int i = 0 ; i < classes.length; i++){
            addNode(classes[i].getClassURI());
            nodeType.add("class");
            nodeweight.add(classes[i].getNumOfInstances());           
        }
        // setEdges
        for (int i = 0 ; i < classes.length; i++ ){
            try{
                ClassLink[] classLinks = rdfsa.getNextClass(null, classes[i].getClassURI(), 10000, true);
                for (int j = 0 ; j < classLinks.length; j++){
                    Integer n = labelednodes.get(classLinks[j].getLinkedClassURI());
                    if ( n != null ){
                        addEdge(i, n, classLinks[j]);
                    }else{
                        n = labelednodes.get(classLinks[j].getLinkedLiteralDatatypeURI());
                        if ( n == null ){
                           addNode(classLinks[j].getLinkedLiteralDatatypeURI());
                           n = nodeType.size();
                           nodeType.add("literal");
                        }
                        addEdge(i, n, classLinks[j]);
                    }
                }
            }catch(Exception e){
                System.err.println(e);
            }
        }
    } 

    public void setPartClassGraph(RDFSchemaAnalyzer rdfsa, String sparqlEndpoint, String startClass){
        // set endpoint
        this.sparqlEndpoint = sparqlEndpoint;
        
        visited = new HashSet<Integer>();
        nodeweight = new LinkedList<Integer>();
        // setNodes for all classes
        SClass[] classes = null;
        try{
           classes = rdfsa.getOWLClasses(null, null, null, true);
        }catch(Exception e){
           System.err.println(e); return;
        }
        for (int i = 0 ; i < classes.length; i++){
           addNode(classes[i].getClassURI());
           nodeType.add("class");
           nodeweight.add(classes[i].getNumOfInstances());
        }
        // setEdges
        Integer snode = labelednodes.get(startClass);
        Set<Integer> nodes = new HashSet<Integer>();
        nodes.add(snode);
        visited.add(snode);
        for (int i = 0 ; i < nsteps; i++ ){
            Iterator<Integer> nit = nodes.iterator();
            Set<Integer> nextnodes = new HashSet<Integer>();
            while ( nit.hasNext() ){
                Integer crr = nit.next();
                try{
                    ClassLink[] classLinks = rdfsa.getNextClass(null, labels.get(crr), 10000, true);
                    for (int j = 0 ; j < classLinks.length; j++){
                        Integer nn = labelednodes.get(classLinks[j].getLinkedClassURI());
                        if ( nn == null ){
                            continue;
                        }
                        if ( !visited.contains(nn) ){
                            nextnodes.add(nn);
                            //visited.add(nn);
                        }
                        addEdge(crr, nn, classLinks[j]);
                        //updateWeight(crr, nn, classLinks[j]);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            nodes = nextnodes;
            visited.addAll(nodes);
            if ( visited.size() > labelednodes.size()){
                System.out.println();
            }
        }
    }
*/
  
    public List<String> getReachableClasses(String st){
        List<String> clURIs = new LinkedList<String>();
        Integer snode = labelednodes.get(st);
        Set<Integer> vnodes = new HashSet<Integer>();
        Set<Integer> cnodes = new HashSet<Integer>();
        vnodes.add(snode);
        cnodes.add(snode);
        for (int i = 0 ; i < nsteps; i++ ){
            Iterator<Integer> nit = cnodes.iterator();
            Set<Integer> nextnodes = new HashSet<Integer>();
            while ( nit.hasNext() ){
                Integer crr = nit.next();
                nextnodes = this.gadjlist.get(crr).keySet();
            }
            cnodes = nextnodes;
            vnodes.addAll(cnodes);
        }
        // vnode->urls
        Iterator<Integer> vit = vnodes.iterator();
        while(vit.hasNext()){
            Integer vn = vit.next();
            if (vn.equals(snode)){continue;}
            clURIs.add(this.labels.get(vn));
        }
        return clURIs;
    }
    
    public double computePrOfPath(Path path){
        ListIterator<ClassLink> lit = path.getClassLinks().listIterator();
        ClassLink prev = lit.next();
        double prob = 1.0;
        boolean chk = true;
        double c1, c2; 
        while( lit.hasNext() ){
            ClassLink crr = lit.next();
            c1 = (double) ( crr.getNumOfOriginClassInstances()) / 
                    (double) (  nodeweight.get(labelednodes.get(prev.getLinkedClassURI())));
            c2 = (double) ( prev.getNumOfLinkedClassInstances()) / 
                    (double) (  nodeweight.get(labelednodes.get(prev.getLinkedClassURI())));
            if ( c1 < 0.5 && c2 < 0.5 ){ chk = false;}
            double prob2 = 1.0 - c1;
            double prob3 = 1.0 - Math.pow(prob2, (double) prev.getNumOfLinkedClassInstances());
            prob = prob * prob3 ;
            prev = crr;
        }
        path.setChk(chk);
        return prob;
    }
    
    private void setClassGraph(Set<String> cl, Set<String> crel){
        Iterator<String> cit = cl.iterator();
        while( cit.hasNext()){
            String c = cit.next();
            addNode(c);
        }
        Iterator<String> crit = crel.iterator();
        while( crit.hasNext() ){
            String cr = crit.next();
            String[] data = cr.split("\t");
            if (data.length < 3 ){ 
                continue;
            }
            Integer n1 = labelednodes.get(data[0]);
            Integer n2 = labelednodes.get(data[1]);
            addEdge(n1, n2, new ClassLink(data[2], data[1], null, Direction.forward, 
				10, 10, 10,
				10, 10,
				false, false));
            addEdge(n2, n1, new ClassLink(data[2], data[0], null, Direction.reverse, 
				10, 10, 10,
				10, 10,
				false, false));
        }
    }

    
    private void setClassGraph(Set<String> cl, Set<String> crel, Map<String, Integer> cent){
        //visited = new HashSet<Integer>();
        nodeweight = new LinkedList<Integer>();
        Iterator<String> cit = cl.iterator();
        while( cit.hasNext()){
            String c = cit.next();
            addNode(c);
            nodeweight.add(cent.get(c));
        }
        Iterator<String> crit = crel.iterator();
        while( crit.hasNext() ){
            String cr = crit.next();
            String[] data = cr.split("\t");
            if (data.length < 6 ){ // 0 sc \t 1 oc \t 2 prurl \t 3 dsn \t 4 don \t 5 trn
                continue;
            }
            Integer n1 = labelednodes.get(data[0]);
            Integer n2 = labelednodes.get(data[1]);
            if (n1 == null || n2 == null ){
                continue;
            }
            addEdge(n1, n2, new ClassLink(data[2], data[1], null, Direction.forward, 
				Integer.parseInt(data[5]), Integer.parseInt(data[3]), Integer.parseInt(data[4]),
				Integer.parseInt(data[3]), Integer.parseInt(data[4]),
				false, false));
            addEdge(n2, n1, new ClassLink(data[2], data[0], null, Direction.reverse, 
				Integer.parseInt(data[5]), Integer.parseInt(data[4]), Integer.parseInt(data[3]),
				Integer.parseInt(data[4]), Integer.parseInt(data[3]),
				false, false));
        }
    }

}

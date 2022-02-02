package graph.sub.system;

import java.io.StringWriter;
import java.io.Writer;
import java.rmi.server.ExportException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.alg.clique.CliqueMinimalSeparatorDecomposition;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import csv.utilities.CsvReaders;
import csvBeans.AccessesBean;

public class GraphTries {
	
	private Graph<String, DefaultWeightedEdge> myGraph;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphTries gt = new GraphTries();
		gt.graphCreation();
		gt.loadgraph();
//		try {
//		gt.renderHrefGraph(gt.myGraph);
//		} catch (ExportException ee) {
//			System.out.println(ee.getLocalizedMessage());
//		}
		gt.graphAlgos();
	}

	public void graphCreation() {
		myGraph = GraphTypeBuilder
				.<String, DefaultWeightedEdge> undirected()
				.allowingMultipleEdges(true)
				.allowingSelfLoops(true)
				.edgeClass(DefaultWeightedEdge.class)
				.weighted(true).buildGraph();
	}

	public void loadgraph() {
		CsvReaders cr = new CsvReaders();
		cr.loadData();
		for (AccessesBean ab : cr.getTheAccessesData()){
			if (!myGraph.containsVertex(ab.getSource()))
				myGraph.addVertex(ab.getSource());
			if (!myGraph.containsVertex(ab.getTarget()))
				myGraph.addVertex(ab.getTarget());
			myGraph.addEdge(ab.getSource(), ab.getTarget());
		}
	}
	
    private void renderHrefGraph(Graph<String, DefaultWeightedEdge> hrefGraph)
            throws ExportException
        {
//    		DOTExporter<String, DefaultEdge> exporter=new DOTExporter<>();
            DOTExporter<String, DefaultWeightedEdge> exporter =
                new DOTExporter<>(v -> v.toString().replace(".", "_").replace("/", "__"));
            exporter.setVertexAttributeProvider((v) -> {
                Map<String, Attribute> map = new LinkedHashMap<>();
                map.put("label", DefaultAttribute.createAttribute(v.toString()));
                return map;
            });
            Writer writer = new StringWriter();
            exporter.exportGraph(hrefGraph, writer);
            System.out.println(writer.toString());
        }
	
    private void graphAlgos() {
    	CliqueMinimalSeparatorDecomposition<String, DefaultWeightedEdge> cmsd = 
    			new CliqueMinimalSeparatorDecomposition<String, DefaultWeightedEdge>(myGraph);
    	for (String s : cmsd.getMeo()) {
    		System.out.println(s);
    	}

    }
    
}


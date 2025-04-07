import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SAXGraphParser {
    static Map<Integer, Vector<Edge>> arcList = new HashMap<>();

    public static Graph parseGraph(String filePath) {
        Graph graph = new Graph();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                int currentNodeId = -1;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equalsIgnoreCase("node")) {
                        int id = Integer.parseInt(attributes.getValue("id"));
                        double longitude = Double.parseDouble(attributes.getValue("longitude"));
                        double latitude = Double.parseDouble(attributes.getValue("latitude"));
                        graph.addNode(id, longitude, latitude);
                    } else if (qName.equalsIgnoreCase("arc")) {
                        int from = Integer.parseInt(attributes.getValue("from"));
                        int to = Integer.parseInt(attributes.getValue("to"));
                        int length = Integer.parseInt(attributes.getValue("length"));

                        graph.addEdge(from, to, length);
                        arcList.putIfAbsent(from, new Vector<>());
                        arcList.get(from).add(new Edge(from, to, length));
                    }
                }
            };

            saxParser.parse(filePath, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return graph;
    }

    public static Map<Integer, Vector<Edge>> getArcList() {
        return arcList;
    }
}


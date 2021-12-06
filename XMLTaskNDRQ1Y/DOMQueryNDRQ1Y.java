package XMLFeladatNDRQ1Y;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

public class DOMQueryNDRQ1Y {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
    XPathExpressionException, DOMException, ParseException {
// XML fájl beolvasása
File xml = new File("XMLNDRQ1Y.xml");

// XML fájl DOM document való formába való alakítása
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = factory.newDocumentBuilder();
Document document = builder.parse(xml);


// DOM document átalakítása DOM DocumentTraversal formába
DocumentTraversal traversal = (DocumentTraversal) document;


NodeList vehicles = document.getElementsByTagName("vehicle");

//Lekérdezzük azokat a jármûveket amelyeknek a gyártója Skoda.
for (int i = 0; i < vehicles.getLength(); i++) {
	Node temp = vehicles.item(i);
	NodeList tempchildren = temp.getChildNodes();
	for (int j = 0; j < tempchildren.getLength(); j++) {
		Node temp2 = tempchildren.item(j);
			if (temp2.getNodeName().equals("manufacturer") && temp2.getTextContent().equals("Skoda")) {
				TreeWalker walker = traversal.createTreeWalker(temp,
				        NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);
				DomTraverser.traverseLevel(walker, "");
			}
	}
}

//Lekérdezzük azokat a sofõröket akiknek van jogosítványuk villamosra.
NodeList drivers = document.getElementsByTagName("driver");

for (int i = 0; i < drivers.getLength(); i++) {
	Node temp = drivers.item(i);
	NodeList tempchildren = temp.getChildNodes();
	for (int j = 0; j < tempchildren.getLength(); j++) {
		Node temp2 = tempchildren.item(j);
			if (temp2.getNodeName().equals("license")) {
				NodeList temp2children = temp2.getChildNodes();
				for (int k = 0; k < temp2children.getLength(); k++) {
					Node temp3 = temp2children.item(k);
					
					if (temp3.getNodeName().equals("tram") && temp3.getTextContent().equals("true")) {
						
						TreeWalker walker = traversal.createTreeWalker(temp,
						        NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);
						DomTraverser.traverseLevel(walker, "");
					}
				}
			}
			
			
	}
}




}
    private static class DomTraverser {
        public static void traverseLevel(TreeWalker walker, String indent) {
            // kimentjük az aktuális csomópontot
            Node node = walker.getCurrentNode();

            // kiíratjuk a megfelelõ metódussal
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                printElementNode(node, indent);
            } else {
                printTextNode(node, indent);
            }

            // rekurzívan meghívjuk a bejárást a DOM fa eggyel mélyebben lévõ csomópontjára,
            // majd azok testvér csomópontjaira
            for (Node n = walker.firstChild(); n != null; n = walker.nextSibling()) {
                traverseLevel(walker, indent + "    ");
            }

            walker.setCurrentNode(node);
        }

        private static void printElementNode(Node node, String indent) {
            System.out.print(indent + node.getNodeName());

            printElementAttributes(node.getAttributes());
        }

        private static void printElementAttributes(NamedNodeMap attributes) {
            int length = attributes.getLength();

            if (length > 0) {
                System.out.print(" (");

                for (int i = 0; i < length; i++) {
                    Node attribute = attributes.item(i);

                    System.out.printf("%s=%s%s", attribute.getNodeName(), attribute.getNodeValue(),
                            i != length - 1 ? ", " : "");
                }

                System.out.println(")");
            } else {
                System.out.println();
            }
        }

        private static void printTextNode(Node node, String indent) {
            String content_trimmed = node.getTextContent().trim();

            if (content_trimmed.length() > 0) {
                System.out.print(indent);
                System.out.printf("%s%n", content_trimmed);
            }
        }
    }
}

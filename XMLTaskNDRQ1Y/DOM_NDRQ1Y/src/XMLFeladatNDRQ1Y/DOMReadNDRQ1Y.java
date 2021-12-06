package XMLFeladatNDRQ1Y;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;

public class DOMReadNDRQ1Y {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        // XML f�jl beolvas�sa
        File xml = new File("XMLNDRQ1Y.xml");

        // XML f�jl DOM document val� form�ba val� alak�t�sa
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

        // DOM document �talak�t�sa DOM DocumentTraversal form�ba
        DocumentTraversal traversal = (DocumentTraversal) document;

        // DOM TreeWalker inicializ�l�sa
        // a gy�k�relemt�l kezdve bej�rhatjuk az �sszes elemet �s sz�veget tartalmaz�
        // csom�pontot
        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        // a DOM bej�r�sa �s ki�rat�sa rekurz�van
        DomTraverser.traverseLevel(walker, "");
    }

    private static class DomTraverser {
        public static void traverseLevel(TreeWalker walker, String indent) {
            // kimentj�k az aktu�lis csom�pontot
            Node node = walker.getCurrentNode();

            // ki�ratjuk a megfelel� met�dussal
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                printElementNode(node, indent);
            } else {
                printTextNode(node, indent);
            }

            // rekurz�van megh�vjuk a bej�r�st a DOM fa eggyel m�lyebben l�v� csom�pontj�ra,
            // majd azok testv�r csom�pontjaira
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

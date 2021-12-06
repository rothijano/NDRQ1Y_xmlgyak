package XMLFeladatNDRQ1Y;

import java.io.*;
import java.text.ParseException;
import java.time.*;
import java.time.format.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;

public class DOMModifyNDRQ1Y {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, DOMException, ParseException {
        // XML f�jl beolvas�sa
        File xml = new File("XMLNDRQ1Y.xml");

        // XML f�jl DOM document val� form�ba val� alak�t�sa
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

        // a DOM document l�v� adatok m�dos�t�sa
        DomModifier.modifyDom(document);

        // DOM document �talak�t�sa DOM DocumentTraversal form�ba
        DocumentTraversal traversal = (DocumentTraversal) document;

        // DOM TreeWalker inicializ�l�sa
        // a gy�k�relemt�l kezdve bej�rhatjuk az �sszes elemet �s sz�veget tartalmaz�
        // csom�pontot
        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        // a DOM bej�r�sa rekurz�van
        DomTraverser.traverseLevel(walker, "");
    }

    private static class DomModifier {
        public static void modifyDom(Document document) throws XPathExpressionException, DOMException, ParseException {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            // 1.)  Michael Schumacher  megszervezte a villamoshoz a jogos�tv�nyt,
            // �s ezt kellene hozz�adnunk az adatb�zisunkhoz
            // XPath seg�ts�g�vel lek�rdezz�k a megfelel� elemet/csom�pontot a DOM documentb�l
            Node driver = (Node) xpath.evaluate("//driver[./lastName='Michael' and ./firstName='Schumacher']/license/bus",
                    document, XPathConstants.NODE);

            driver.setTextContent("true");

            // 2.) Egy v�rosi rendelet szerint a 15 km-n�l r�videbb �tvonalaknak 20%-kal n�
            // a kilom�ter�ra
            NodeList routes = (NodeList) xpath.evaluate("//route[./distance<15]/pricePerKm", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < routes.getLength(); i++) {
                Node route = routes.item(i);

                double pricePerKm = Double.parseDouble(route.getTextContent());
                route.setTextContent(Double.toString(pricePerKm * 1.20));
            }

            // 3.) A 08:35-kor be�rkez� j�ratnak 15 perccel n�tt a menetideje, ez�rt k�s�bb fog be�rkezni
            Node schedule = (Node) xpath.evaluate("//schedule[./departureTime='08:35:00']/departureTime", document,
                    XPathConstants.NODE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime departureTime = LocalTime.parse(schedule.getTextContent(), formatter);
            departureTime = departureTime.plusMinutes(15);
            schedule.setTextContent(formatter.format(departureTime));

            // 4.) A 43-es j�rat �t lett nevezve, mivel ez egy ID ez�rt az ehhez kapcsol�d�
            // elemekben is m�dos�tanunk kell a kapcsolatot
            NodeList nodes = (NodeList) xpath.evaluate("//route[@id='43']/@id | //*[@routeId='43']/@routeId", document,
                    XPathConstants.NODESET);
            String newId = "666";

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                node.setTextContent(newId);
            }
        }
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

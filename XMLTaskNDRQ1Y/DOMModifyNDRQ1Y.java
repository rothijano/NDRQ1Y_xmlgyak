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
        // XML fájl beolvasása
        File xml = new File("XMLNDRQ1Y.xml");

        // XML fájl DOM document való formába való alakítása
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

        // a DOM document lévõ adatok módosítása
        DomModifier.modifyDom(document);

        // DOM document átalakítása DOM DocumentTraversal formába
        DocumentTraversal traversal = (DocumentTraversal) document;

        // DOM TreeWalker inicializálása
        // a gyökérelemtõl kezdve bejárhatjuk az összes elemet és szöveget tartalmazó
        // csomópontot
        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        // a DOM bejárása rekurzívan
        DomTraverser.traverseLevel(walker, "");
    }

    private static class DomModifier {
        public static void modifyDom(Document document) throws XPathExpressionException, DOMException, ParseException {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            // 1.)  Michael Schumacher  megszervezte a villamoshoz a jogosítványt,
            // és ezt kellene hozzáadnunk az adatbázisunkhoz
            // XPath segítségével lekérdezzük a megfelelõ elemet/csomópontot a DOM documentból
            Node driver = (Node) xpath.evaluate("//driver[./lastName='Michael' and ./firstName='Schumacher']/license/bus",
                    document, XPathConstants.NODE);

            driver.setTextContent("true");

            // 2.) Egy városi rendelet szerint a 15 km-nél rövidebb útvonalaknak 20%-kal nõ
            // a kilométerára
            NodeList routes = (NodeList) xpath.evaluate("//route[./distance<15]/pricePerKm", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < routes.getLength(); i++) {
                Node route = routes.item(i);

                double pricePerKm = Double.parseDouble(route.getTextContent());
                route.setTextContent(Double.toString(pricePerKm * 1.20));
            }

            // 3.) A 08:35-kor beérkezõ járatnak 15 perccel nõtt a menetideje, ezért késõbb fog beérkezni
            Node schedule = (Node) xpath.evaluate("//schedule[./departureTime='08:35:00']/departureTime", document,
                    XPathConstants.NODE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime departureTime = LocalTime.parse(schedule.getTextContent(), formatter);
            departureTime = departureTime.plusMinutes(15);
            schedule.setTextContent(formatter.format(departureTime));

            // 4.) A 43-es járat át lett nevezve, mivel ez egy ID ezért az ehhez kapcsolódó
            // elemekben is módosítanunk kell a kapcsolatot
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

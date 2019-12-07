package Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class SettingsParser {

    private String businessName;
    private String location;
    private String phone1;
    private String phone2;
    private String tin;
    private int till_no;
    private Document doc;
    private File inputFile;
    private String serverIp;
    private int default_stock_destination; //where stock received is initially stored
    private boolean checkStock;
    private boolean printer; //enable or disbable printer

    public SettingsParser(String settingsFilePath) {
        try {
            inputFile = new File(settingsFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            //parse header settings
            NodeList nList = doc.getElementsByTagName("header");

            //System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                //System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    businessName = eElement
                            .getElementsByTagName("businessname")
                            .item(0)
                            .getTextContent();
                    location = eElement
                            .getElementsByTagName("location")
                            .item(0)
                            .getTextContent();
                    phone1 = eElement
                            .getElementsByTagName("phone1")
                            .item(0)
                            .getTextContent();
                    phone2 = eElement
                            .getElementsByTagName("phone2")
                            .item(0)
                            .getTextContent();
                    tin = eElement
                            .getElementsByTagName("tin")
                            .item(0)
                            .getTextContent();
                    till_no = Integer.parseInt (eElement
                            .getElementsByTagName("till_no")
                            .item(0)
                            .getTextContent());
                    checkStock = Boolean.parseBoolean (eElement
                            .getElementsByTagName("check_stock")
                            .item(0)
                            .getTextContent());
                    printer = Boolean.parseBoolean (eElement
                            .getElementsByTagName("printer")
                            .item(0)
                            .getTextContent());
                }
            }

            //parse server settings
            NodeList nList_server = doc.getElementsByTagName("server");

            for (int temp = 0; temp < nList_server.getLength(); temp++) {
                Node nNode = nList_server.item(temp);
                //System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    serverIp = eElement
                            .getElementsByTagName("server_ip")
                            .item(0)
                            .getTextContent();

                    default_stock_destination = Integer.parseInt(eElement
                            .getElementsByTagName("default_stock_destination")
                            .item(0)
                            .getTextContent());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getServerIp(){
        return serverIp;
    }

    public String getTin() {
        return tin;
    }

    public int getTillNumber() {
        return till_no;
    }

    public boolean isCheckStock() {
        return checkStock;
    }

    public void setCheckStock(boolean checkStock) {
        updateSettings("header","check_stock", Boolean.toString(checkStock));
        this.checkStock = checkStock;

    }

    public boolean isPrinter() {
        return printer;
    }

    public void setPrinter(boolean printer) {
        updateSettings("header","printer", Boolean.toString(printer));
        this.printer = printer;
    }

    public void setBusinessName(String businessName) {
        updateSettings("header", "businessname", businessName);
    }

    public void updateSettings(String tagName, String nodeName, String textContent) {
        try {
            Node header = doc.getElementsByTagName(tagName).item(0);
            NodeList list = header.getChildNodes();
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    if (nodeName.equals(eElement.getNodeName())) {
                        eElement.setTextContent(textContent);
                    }
                }
            }

            // write the content to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(inputFile);
            transformer.transform(source, result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setLocation(String location) {
        updateSettings("header","location", location);
    }

    public void setPhone1(String phone1) {

        updateSettings("header","phone1", phone1);
    }

    public void setPhone2(String phone2) {

        updateSettings("header","phone2", phone2);
    }

    public void setServerIp(String serverIp){
        updateSettings("server","server_ip",serverIp);
    }

    public void setTin(String tin) {
        updateSettings("header","tin", tin);
    }

    public void setTillNumber(int till_no) {
        updateSettings("header","till_no", Integer.toString(till_no));
    }

    public int getDefaultStockDestination() {
        return default_stock_destination;
    }

    public void setDefaultStockDestination(int default_stock_destination) {
        updateSettings("server","default_stock_destination", Integer.toString(default_stock_destination));
    }

}


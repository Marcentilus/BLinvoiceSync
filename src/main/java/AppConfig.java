import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class AppConfig {

    private final Configuration configuration;

    public AppConfig(String xmlFilePath) {
        String url = "";
        String login = "";
        String password = "";
        String token = "";

        try {
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList databaseList = doc.getElementsByTagName("database");
            Node databaseNode = databaseList.item(0);
            if (databaseNode.getNodeType() == Node.ELEMENT_NODE) {
                Element databaseElement = (Element) databaseNode;
                url = databaseElement.getElementsByTagName("url").item(0).getTextContent();
                login = databaseElement.getElementsByTagName("login").item(0).getTextContent();
                password = databaseElement.getElementsByTagName("password").item(0).getTextContent();
            }

            NodeList apiList = doc.getElementsByTagName("api");
            Node apiNode = apiList.item(0);
            if (apiNode.getNodeType() == Node.ELEMENT_NODE) {
                Element apiElement = (Element) apiNode;
                token = apiElement.getElementsByTagName("token").item(0).getTextContent();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.configuration = new Configuration(url, login, password, token);
    }

    public Configuration getConfiguration() throws NullPointerException {
        return configuration;
    }
}
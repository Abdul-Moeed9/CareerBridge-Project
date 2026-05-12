package utility;


import domain.Offering;
import interfaces.IFeedFetchable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
// import java.net.URL; -- >> NOT USED
import java.util.ArrayList;
import java.util.List;
import java.net.URI;


public class RSSParser implements IFeedFetchable {


    private String feedURL;


    public RSSParser(String feedURL) {
        this.feedURL = feedURL;
    }


    @Override
    public List<Offering> fetch() {
        String rawXML = fetchXML(feedURL);
        if (!validate(rawXML)) return new ArrayList<>();
        return parse(rawXML);
    }


    @Override
    public boolean validate(String data) {
        return data != null && !data.trim().isEmpty() && data.contains("<item>");
    }


    @Override
    public List<Offering> parse(String rawData) {
        List<Offering> offerings = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(rawData.getBytes()));
            doc.getDocumentElement().normalize();


            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                Offering offering = new Offering();
                offering.setTitle(extractField(item, "title"));
                offering.setLocation(extractField(item, "location"));
                offering.setJobDescription(extractField(item, "description"));
                offering.setRole(extractField(item, "category"));
                offering.setStatus("open");
                offering.setOfferingType("job");
                offerings.add(offering);
            }
        } catch (Exception e) {
            System.err.println("RSSParser parse error: " + e.getMessage());
        }
        return offerings;
    }


    public String fetchXML(String url) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
            }
        } catch (Exception e) {
            System.err.println("RSSParser fetchXML error: " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
        return result.toString();
    }


    private String extractField(Element element, String tag) {
        NodeList nodes = element.getElementsByTagName(tag);
        if (nodes.getLength() > 0 && nodes.item(0).getTextContent() != null) {
            return nodes.item(0).getTextContent().trim();
        }
        return "";
    }
}

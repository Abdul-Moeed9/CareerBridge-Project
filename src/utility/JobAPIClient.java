package utility;


import domain.Offering;
import interfaces.IFeedFetchable;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
// import java.net.URL;   -- >> NOT USED
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.net.URI;


public class JobAPIClient implements IFeedFetchable {


    private String apiBaseURL;
    private String apiKey;


    public JobAPIClient(String apiBaseURL, String apiKey) {
        this.apiBaseURL = apiBaseURL;
        this.apiKey = apiKey;
    }


    @Override
    public List<Offering> fetch() {
        String requestURL = buildRequestURL(Map.of("country", "pakistan", "limit", "50"));
        String rawData = fetchResponse(requestURL);
        if (!validate(rawData)) return new ArrayList<>();
        return parse(rawData);
    }


    @Override
    public boolean validate(String data) {
        return data != null && !data.trim().isEmpty() && data.contains("title");
    }


    @Override
    public List<Offering> parse(String rawData) {
        List<Offering> offerings = new ArrayList<>();
        try {
            String[] entries = rawData.split("\\},\\s*\\{");
            for (String entry : entries) {
                Offering offering = new Offering();
                offering.setTitle(extractJSONField(entry, "title"));
                offering.setLocation(extractJSONField(entry, "location"));
                offering.setRole(extractJSONField(entry, "category"));
                offering.setJobDescription(extractJSONField(entry, "description"));
                offering.setStatus("open");
                offering.setOfferingType("job");


                String stipendStr = extractJSONField(entry, "salary");
                if (!stipendStr.isEmpty()) {
                    offering.setSalaryText(stipendStr);
                    try { offering.setStipend(Double.parseDouble(stipendStr.replaceAll("[^0-9.]", ""))); }
                    catch (NumberFormatException ignored) {}
                }
                offerings.add(offering);
            }
        } catch (Exception e) {
            System.err.println("JobAPIClient parse error: " + e.getMessage());
        }
        return offerings;
    }


    private String buildRequestURL(Map<String, String> params) {
        StringBuilder url = new StringBuilder(apiBaseURL).append("?api_key=").append(apiKey);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return url.toString();
    }


    private String fetchResponse(String urlString) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
            }
        } catch (Exception e) {
            System.err.println("JobAPIClient fetch error: " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
        return result.toString();
    }


    private String extractJSONField(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) return "";
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return "";
        int valueStart = json.indexOf("\"", colonIndex);
        int valueEnd = json.indexOf("\"", valueStart + 1);
        if (valueStart == -1 || valueEnd == -1) return "";
        return json.substring(valueStart + 1, valueEnd).trim();
    }
}

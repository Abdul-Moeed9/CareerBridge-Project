package utility;

import dao.OfferingDAO;
import domain.Offering;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class AbstractDatasetLoader {

    protected String datasetPath;

    public AbstractDatasetLoader(String datasetPath) {
        this.datasetPath = datasetPath;
    }

    public abstract String extractTitle(JSONObject job);
    public abstract String extractCompanyName(JSONObject job);
    public abstract String extractLocation(JSONObject job);
    public abstract String extractDescription(JSONObject job);
    public abstract String extractExperience(JSONObject job);
    public abstract String extractRole(JSONObject job);
    public abstract double extractSalary(JSONObject job);
    public abstract String extractSalaryText(JSONObject job);
    public abstract Date extractPostedDate(JSONObject job);
    public abstract String extractURL(JSONObject job);
    public abstract String extractPostedAt(JSONObject job);

    public void load() {
        try {
            String raw = new String(Files.readAllBytes(Paths.get(datasetPath)));
            JSONArray jobs = new JSONArray(raw);

            OfferingDAO offeringDAO = new OfferingDAO();

            int loaded = 0;
            int skipped = 0;

            for (int i = 0; i < jobs.length(); i++) {
                JSONObject job = jobs.getJSONObject(i);

                String title = extractTitle(job);
                if (title.isEmpty()) {
                    skipped++;
                    continue;
                }

                String companyName = extractCompanyName(job);
                if (companyName.isEmpty()) companyName = "Unknown";

                Offering offering = new Offering();
                offering.setTitle(title);
                offering.setCompanyID(null);
                offering.setCompanyName(companyName);
                offering.setLocation(extractLocation(job));
                offering.setJobDescription(extractDescription(job));
                offering.setRequiredExperience(extractExperience(job));
                offering.setRole(extractRole(job));
                offering.setStipend(extractSalary(job));
                offering.setSalaryText(extractSalaryText(job));
                offering.setOfferingType("job");
                offering.setStatus("open");
                Date postedDate = extractPostedDate(job);
                offering.setPostedDate(postedDate != null ? postedDate : new Date());
                offering.setRequiredCGPA(null);
                offering.setUrl(extractURL(job));
                offering.setPostedAtText(extractPostedAt(job));
                offering.setSource("scraped");
                offeringDAO.save(offering);
                loaded++;

                if (loaded % 100 == 0) {
                    System.out.println("Loaded " + loaded + " offerings...");
                }
            }

            System.out.println("Done. Loaded: " + loaded + " | Skipped: " + skipped);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected double parseSalary(String salary) {
        if (salary == null || salary.isEmpty()) return 0.0;
        try {
            String cleaned = salary.replaceAll("[^0-9,\\-]", "").trim();
            if (cleaned.contains("-")) {
                String[] parts = cleaned.split("-");
                double low = Double.parseDouble(parts[0].replace(",", "").trim());
                double high = Double.parseDouble(parts[1].replace(",", "").trim());
                return (low + high) / 2.0;
            }
            return Double.parseDouble(cleaned.replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    protected Date parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    protected String getString(JSONObject obj, String key) {
        try {
            if (obj.has(key) && !obj.isNull(key)) {
                return obj.getString(key).trim();
            }
        } catch (Exception ignored) {}
        return "";
    }
}
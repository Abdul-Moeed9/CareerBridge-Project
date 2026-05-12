package utility;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Date;

public class IndeedDatasetLoader extends AbstractDatasetLoader {

    public IndeedDatasetLoader(String datasetPath) {
        super(datasetPath);
    }

    @Override
    public String extractTitle(JSONObject job) {
        return getString(job, "positionName");
    }

    @Override
    public String extractCompanyName(JSONObject job) {
        return getString(job, "company");
    }

    @Override
    public String extractLocation(JSONObject job) {
        return getString(job, "location");
    }

    @Override
    public String extractDescription(JSONObject job) {
        return getString(job, "description");
    }

    @Override
    public String extractExperience(JSONObject job) {
        return "";
    }

    @Override
    public String extractRole(JSONObject job) {
        try {
            if (job.has("jobType") && !job.isNull("jobType")) {
                JSONArray types = job.getJSONArray("jobType");
                if (types.length() > 0) {
                    return types.getString(0).trim();
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    @Override
    public double extractSalary(JSONObject job) {
        return parseSalary(getString(job, "salary"));
    }

    @Override
    public String extractSalaryText(JSONObject job) {
        String s = getString(job, "salary");
        return s.isEmpty() ? null : s;
    }

    @Override
    public Date extractPostedDate(JSONObject job) {
        String dateStr = getString(job, "postingDateParsed");
        if (!dateStr.isEmpty()) {
            return parseDate(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        }
        return null;
    }

    @Override
    public String extractURL(JSONObject job) {
        return getString(job, "url");
    }

    @Override
    public String extractPostedAt(JSONObject job) {
        return getString(job, "postedAt");
    }
}
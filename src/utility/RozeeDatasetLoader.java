package utility;

import org.json.JSONObject;
import java.util.Date;

public class RozeeDatasetLoader extends AbstractDatasetLoader {

    public RozeeDatasetLoader(String datasetPath) {
        super(datasetPath);
    }

    @Override
    public String extractTitle(JSONObject job) {
        return getString(job, "title");
    }

    @Override
    public String extractCompanyName(JSONObject job) {
        String name = getString(job, "company_name");
        if (name.isEmpty()) name = getString(job, "company");
        return name;
    }

    @Override
    public String extractLocation(JSONObject job) {
        String location = getString(job, "city");
        if (location.isEmpty()) location = getString(job, "location");
        return location;
    }

    @Override
    public String extractDescription(JSONObject job) {
        String desc = getString(job, "description_text");
        if (desc.isEmpty()) desc = getString(job, "description_raw");
        return desc;
    }

    @Override
    public String extractExperience(JSONObject job) {
        return getString(job, "experience_text");
    }

    @Override
    public String extractRole(JSONObject job) {
        String role = getString(job, "type");
        if (role.isEmpty()) role = getString(job, "contract_type");
        return role;
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
        return parseDate(getString(job, "date_posted"), "MMM dd, yyyy");
    }

    @Override
    public String extractURL(JSONObject job) {
        return getString(job, "url");
    }

    @Override
    public String extractPostedAt(JSONObject job) {
        return getString(job, "date_posted");
    }
}


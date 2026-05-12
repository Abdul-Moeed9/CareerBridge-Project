package service;

import domain.FilterCandidate;
import domain.Offering;
import interfaces.IFilterable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterService implements IFilterable {

    @Override
    public List<Object> applyFilter(List<Object> items, Object criteria) {
        if (!(criteria instanceof FilterCandidate)) return items;
        FilterCandidate filter = (FilterCandidate) criteria;

        List<Offering> offerings = new ArrayList<>();
        for (Object obj : items) {
            if (obj instanceof Offering) offerings.add((Offering) obj);
        }

        if (filter.getLocation() != null && !filter.getLocation().isEmpty()) {
            offerings = filterByLocation(offerings, filter.getLocation());
        }
        if (filter.getCGPA() > 0) {
            offerings = filterByCGPA(offerings, filter.getCGPA());
        }
        if (filter.getStipend() > 0) {
            offerings = filterByStipend(offerings, filter.getStipend());
        }
        if (filter.getExperience() != null && !filter.getExperience().isEmpty()) {
            offerings = filterByExperience(offerings, filter.getExperience());
        }
        if (filter.getRole() != null && !filter.getRole().isEmpty()) {
            offerings = filterByRole(offerings, filter.getRole());
        }

        return new ArrayList<>(offerings);
    }

    private List<Offering> filterByLocation(List<Offering> offerings, String location) {
        return offerings.stream()
                .filter(o -> o.getLocation() != null &&
                        o.getLocation().toLowerCase().contains(location.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<Offering> filterByCGPA(List<Offering> offerings, double seekerCGPA) {
        return offerings.stream()
                .filter(o -> o.getRequiredCGPA() == null || o.getRequiredCGPA() <= seekerCGPA)
                .collect(Collectors.toList());
    }

    private List<Offering> filterByStipend(List<Offering> offerings, double minStipend) {
        return offerings.stream()
                .filter(o -> o.getStipend() >= minStipend)
                .collect(Collectors.toList());
    }

    private List<Offering> filterByExperience(List<Offering> offerings, String experience) {
        int maxYears = parseExperienceToMaxYears(experience);
        return offerings.stream()
                .filter(o -> {
                    if (o.getRequiredExperience() == null || o.getRequiredExperience().trim().isEmpty()) {
                        return true;
                    }
                    int requiredYears = extractYearsFromExperience(o.getRequiredExperience());
                    return requiredYears <= maxYears;
                })
                .collect(Collectors.toList());
    }

    private int parseExperienceToMaxYears(String filterValue) {
        if (filterValue == null) return Integer.MAX_VALUE;
        String lower = filterValue.toLowerCase().trim();
        if (lower.contains("fresh") || lower.contains("0 year")) return 0;
        if (lower.startsWith("1-2") || lower.contains("1-2")) return 2;
        if (lower.startsWith("3-5") || lower.contains("3-5")) return 5;
        if (lower.startsWith("5+") || lower.contains("5+")) return Integer.MAX_VALUE;
        try {
            return Integer.parseInt(lower.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private int extractYearsFromExperience(String dbValue) {
        if (dbValue == null) return 0;
        String lower = dbValue.toLowerCase().trim();
        if (lower.contains("fresh") || lower.contains("entry") || lower.contains("none")
                || lower.equals("0") || lower.contains("no experience")) {
            return 0;
        }
        try {
            String digits = lower.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                return Integer.parseInt(digits.substring(0, Math.min(digits.length(), 2)));
            }
        } catch (NumberFormatException ignored) {}
        return 0;
    }

    private List<Offering> filterByRole(List<Offering> offerings, String role) {
        return offerings.stream()
                .filter(o -> {
                    if (o.getRole() != null && o.getRole().toLowerCase().contains(role.toLowerCase())) {
                        return true;
                    }
                    return o.getTitle() != null && o.getTitle().toLowerCase().contains(role.toLowerCase());
                })
                .collect(Collectors.toList());
    }
}
package com.agileoracles.leave_portal_app.service;

import com.agileoracles.leave_portal_app.model.LeaveCategorizationResult;
import com.agileoracles.leave_portal_app.model.LeaveCategory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LeaveCategorizationService {

    private final Map<LeaveCategory, List<String>> categoryKeywords =
            new LinkedHashMap<>();

    public LeaveCategorizationService() {

        categoryKeywords.put(
                LeaveCategory.SICK_LEAVE,
                List.of(
                        "sick",
                        "illness",
                        "doctor",
                        "medical",
                        "hospital",
                        "fever",
                        "clinic",
                        "unwell"
                )
        );

        categoryKeywords.put(
                LeaveCategory.ANNUAL_LEAVE,
                List.of(
                        "annual leave",
                        "annual",
                        "vacation",
                        "holiday",
                        "travel"
                )
        );

        categoryKeywords.put(
                LeaveCategory.EMERGENCY_LEAVE,
                List.of(
                        "emergency",
                        "urgent",
                        "accident",
                        "bereavement",
                        "death"
                )
        );

        categoryKeywords.put(
                LeaveCategory.MATERNITY_LEAVE,
                List.of(
                        "maternity",
                        "pregnancy",
                        "pregnant",
                        "childbirth",
                        "delivery"
                )
        );

        categoryKeywords.put(
                LeaveCategory.UNPAID_LEAVE,
                List.of(
                        "unpaid",
                        "without pay",
                        "no salary",
                        "salary deduction"
                )
        );
    }

    public LeaveCategorizationResult categorize(String content) {

        if (content == null || content.isBlank()) {
            return new LeaveCategorizationResult(
                    LeaveCategory.OTHER,
                    List.of(),
                    "The uploaded file contains no readable text."
            );
        }

        String normalizedContent =
                content.toLowerCase(Locale.ROOT);

        for (Map.Entry<LeaveCategory, List<String>> entry
                : categoryKeywords.entrySet()) {

            List<String> matches = entry.getValue()
                    .stream()
                    .filter(normalizedContent::contains)
                    .toList();

            if (!matches.isEmpty()) {
                return new LeaveCategorizationResult(
                        entry.getKey(),
                        matches,
                        "Category selected using matched keywords."
                );
            }
        }

        return new LeaveCategorizationResult(
                LeaveCategory.OTHER,
                List.of(),
                "No supported leave keywords were found."
        );
    }
}
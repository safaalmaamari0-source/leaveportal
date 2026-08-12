package com.agileoracles.leave_portal_app.service;

import com.agileoracles.leave_portal_app.model.LeaveRequestEntity;
import com.agileoracles.leave_portal_app.repository.LeaveRequestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveCategoryJob {

    private final LeaveRequestRepository repository;
    private final LlmLeaveCategorizationService llmService;

    public LeaveCategoryJob(
            LeaveRequestRepository repository,
            LlmLeaveCategorizationService llmService
    ) {
        this.repository = repository;
        this.llmService = llmService;
    }

    @Scheduled(fixedDelay = 60000)
    public void categorizePendingRequests() {

        List<LeaveRequestEntity> pendingRequests =
                repository.findByLeaveCategoryIsNull();

        for (LeaveRequestEntity request : pendingRequests) {

            try {

                String category =
                        llmService.categorize(
                                request.getReasonForLeave()
                        );

                request.setLeaveCategory(category);

                repository.save(request);

                System.out.println(
                        "Categorized request "
                                + request.getId()
                                + " as "
                                + category
                );

            } catch (Exception e) {

                System.out.println(
                        "Failed to categorize request "
                                + request.getId()
                );

                e.printStackTrace();
            }
        }
    }
}
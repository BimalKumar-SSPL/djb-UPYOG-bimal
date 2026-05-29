package org.upyog.rs.wt.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.rs.repository.RequestServiceRepository;
import org.upyog.rs.service.WaterTankerInternalBookingService;
import org.upyog.rs.service.WaterTankerService;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingSearchCriteria;
import org.upyog.rs.web.models.Workflow;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class WaterTankerCancellationService {

    @Autowired
    private WaterTankerService waterTankerService;

    @Autowired
    private RequestServiceRepository requestServiceRepository;

    @Autowired
    private WaterTankerInternalBookingService internalBookingService;

    @Value("${wt.fixedpoint.tenant-id}")
    private String tenantId;

    public void cancelScheduledBookings(RequestInfo requestInfo) {
        log.info("Starting automated cancellation of SCHEDULED water tanker bookings...");

        // 1. If RequestInfo is null (called by Cron), generate a System RequestInfo
        if (requestInfo == null) {
            requestInfo = internalBookingService.buildSystemRequestInfo(tenantId);
        }

        // 2. Search for all bookings currently in 'SCHEDULED' status
        WaterTankerBookingSearchCriteria criteria = WaterTankerBookingSearchCriteria.builder()
                .tenantId(tenantId)
                .status("SCHEDULED")
                .applicationType("watertanker-fixedpoint")
                .build();

        List<WaterTankerBookingDetail> pendingBookings = requestServiceRepository.getWaterTankerBookingDetails(criteria);

        if (pendingBookings == null || pendingBookings.isEmpty()) {
            log.info("No SCHEDULED bookings found to cancel at this time.");
            return;
        }

        log.info("Found {} SCHEDULED bookings. Initiating cancellation.", pendingBookings.size());

        // 3. Iterate and apply the CANCEL workflow (matching your curl payload)
        for (WaterTankerBookingDetail booking : pendingBookings) {
            try {
                Workflow workflow = Workflow.builder()
                        .action("CANCEL")
                        .comments("Automatically cancelled by system scheduler at 11:30 PM")
                        .moduleName("request-service.water_tanker")
                        // If you use 'watertanker' for regular and 'watertanker-fixedpoint' for fixed, dynamically set it here:
                        .businessService("watertanker-fixedpoint")
                        .build();

                booking.setWorkflow(workflow);

                WaterTankerBookingRequest updateRequest = WaterTankerBookingRequest.builder()
                        .requestInfo(requestInfo)
                        .waterTankerBookingDetail(booking)
                        .build();

                // 4. Send to existing update lifecycle to process workflow state change
                waterTankerService.updateBookingLifecycle(updateRequest);

                log.info("Successfully cancelled booking: {}", booking.getBookingNo());
            } catch (Exception e) {
                log.error("Failed to auto-cancel booking: {}", booking.getBookingNo(), e);
            }
        }
    }
}
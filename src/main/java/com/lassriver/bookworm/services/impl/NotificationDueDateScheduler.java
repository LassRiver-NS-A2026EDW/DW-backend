package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.enums.LoanStatus;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationDueDateScheduler {

    private static final List<LoanStatus> OPEN_STATUSES = List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE);

    private final LoanRepository loanRepository;
    private final NotificationService notificationService;

    @Scheduled(
            initialDelayString = "${bookworm.notifications.due-scan-initial-delay-ms:60000}",
            fixedDelayString = "${bookworm.notifications.due-scan-interval-ms:60000}")
    @Transactional
    public void notifyDueLoans() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueSoonLimit = now.plusHours(24);

        loanRepository.findAllByStatusInAndDueDateBetween(OPEN_STATUSES, now, dueSoonLimit)
                .forEach(notificationService::notifyLoanDueSoon);

        for (Loan loan : loanRepository.findAllByStatusInAndDueDateBefore(OPEN_STATUSES, now)) {
            if (LoanStatus.ACTIVE.equals(loan.getStatus())) {
                loan.setStatus(LoanStatus.OVERDUE);
            }
            notificationService.notifyLoanOverdue(loan);
        }
    }
}

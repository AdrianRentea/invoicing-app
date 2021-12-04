package com.smartwecode.generateinvoice.job;

import com.smartwecode.generateinvoice.service.GenerateInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
@Configuration
@EnableScheduling
public class GenerateInvoice {
    @Autowired
    GenerateInvoiceService generateInvoiceService;

    @Scheduled(cron = "${cron.frequency}")
    public void runJob() throws InterruptedException {
        generateInvoiceService.generateInvoices();
    }
}

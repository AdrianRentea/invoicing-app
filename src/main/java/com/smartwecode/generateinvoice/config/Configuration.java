package com.smartwecode.generateinvoice.config;

import com.smartwecode.generateinvoice.utils.Mailer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public Mailer loadMailer(@Value("${email.from.user}") String smtpUser, @Value("${email.from.password}") String smtpPassword) {
        return new Mailer(smtpUser, smtpPassword);
    }

}

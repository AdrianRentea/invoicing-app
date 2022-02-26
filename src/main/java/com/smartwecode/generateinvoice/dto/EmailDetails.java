package com.smartwecode.generateinvoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    private String emailFrom;
    private String emailTo;
    private String emailSubject;
    private String emailBody;
    private String attachmentPath;
}

package com.smartwecode.generateinvoice.dto;

import com.smartwecode.generateinvoice.utils.excel.annotation.ExcelCellInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Collaborator {
    @ExcelCellInfo(index = 0)
    private String name;
    @ExcelCellInfo(index = 1)
    private String registrationNumber;
    @ExcelCellInfo(index = 2)
    private String CIF;
    @ExcelCellInfo(index = 3)
    private String address;
    @ExcelCellInfo(index = 4)
    private String bank;
    @ExcelCellInfo(index = 5)
    private String IBAN;
}

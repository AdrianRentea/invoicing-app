package com.smartwecode.generateinvoice.dto;

import com.smartwecode.generateinvoice.utils.excel.annotation.ExcelCellInfo;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Customer extends Collaborator{
    @ExcelCellInfo(index = 6)
    private Integer amount;
}

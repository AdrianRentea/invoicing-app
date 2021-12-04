package com.smartwecode.generateinvoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    private String name;
    private Supplier supplier;
    private List<Customer> customerList;
}

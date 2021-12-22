package com.smartwecode.generateinvoice.service;

import com.aspose.cells.PdfSaveOptions;
import com.smartwecode.generateinvoice.dto.Company;
import com.smartwecode.generateinvoice.dto.Customer;
import com.smartwecode.generateinvoice.dto.Supplier;
import com.smartwecode.generateinvoice.utils.excel.ExcelSheetDescriptor;
import com.smartwecode.generateinvoice.utils.excel.ExcelUtils;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GenerateInvoiceService {
    @Value("${directoryPath}")
    private String directoryPath;
    @Value("${invoiceTemplate}")
    private String invoiceTemplatePath;
    @Value("${invoiceControllerFileName}")
    private String invoiceControllerFileName;
    private List<Company> dataSet;
    private Workbook companyInvoiceController;

    private Boolean shouldUpdateInvoiceController = false;

    @SneakyThrows
    public void generateInvoices() {

        this.loadDataSet();
        for (Company company : dataSet) {
            loadInvoiceControllerForCompany(company.getName());
            generateCompanyInvoices(company);
            saveInvoiceControllerForCompany(company.getName());
        }
    }

    private void loadDataSet() throws IOException, InstantiationException, IllegalAccessException {
        dataSet = new ArrayList<>();
        List<String> directoryList = listDirectories(directoryPath);
        for (String company : directoryList) {
            List<String> companyFiles = listFilesUsingDirectoryStream(directoryPath + company + "/");
            dataSet.add(
                    new Company(
                            company,
                            loadSupplier(
                                    directoryPath + company + "/" + filterSuppliers(companyFiles).get(0)
                            ),
                            filterCustomers(companyFiles).stream().map(customer -> loadCustomer(directoryPath + company + "/" + customer)).collect(Collectors.toList()))
            );
        }
    }

    private void generateCompanyInvoices(Company companyData) {
        for (Customer customer : companyData.getCustomerList()) {
            generateInvoiceForCompanyCustomer(companyData.getName(), companyData.getSupplier(), customer);
        }
    }

    private void generateInvoiceForCompanyCustomer(String companyName, Supplier supplier, Customer customer) {
        if (!invoiceAlreadyGenerated(companyName, customer.getName())) {
            try {
                Workbook wb = updateInvoice(supplier, customer);
                saveInvoice(companyName, customer, wb);
            } catch (IOException e) {
                removeLastRowFromCompanyInvoiceController();
            }
        }
    }

    private Workbook updateInvoice(Supplier supplier, Customer customer) throws IOException {
        Workbook wb = getSheetFromInvoiceTemplate(invoiceTemplatePath);
        Sheet sheet = wb.getSheetAt(0);
        updateSupplier(sheet, supplier);
        updateCustomer(sheet, customer);
        updateInvoiceSeriesAndNumber(sheet, getNextCompanyInvoiceSerialAndNumber());
        updateInvoiceDate(sheet);
        return wb;
    }

    private void saveInvoice(String companyName, Customer customer, Workbook wb) throws IOException {
        String filePath = getInvoicePathAndName(companyName, customer.getName());
        createDirectoriesInPathIfNotExists(getInvoicePath(companyName));
        FileOutputStream outputStream = new FileOutputStream(filePath + ".xlsx");
        wb.setForceFormulaRecalculation(true);
        wb.write(outputStream);

        wb.close();

        saveInvoiceAsPDF(filePath);


    }

    private void saveInvoiceAsPDF(String filePath){
        try {
            com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook(filePath + ".xlsx");
            PdfSaveOptions options = new PdfSaveOptions();
            options.setOnePagePerSheet(true);
            options.setCalculateFormula(true);
            workbook.save(filePath+".pdf", options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeLastRowFromCompanyInvoiceController() {
        Sheet invoiceControllerSheet = companyInvoiceController.getSheetAt(0);
        invoiceControllerSheet.removeRow(invoiceControllerSheet.getRow(getLastIndexWithNotEmptyData(invoiceControllerSheet)));
    }

    private Boolean invoiceAlreadyGenerated(String companyName, String customerName) {
        return Files.exists(Paths.get(getInvoicePathAndName(companyName, customerName)+ ".xlsx"));
    }

    private String getInvoicePathAndName(String companyName, String customerName) {

        String fileName = "invoice_" + customerName;
        return getInvoicePath(companyName) + fileName;
    }

    private String getInvoicePath(String companyName) {
        LocalDate date = LocalDate.now();
        String monthYear = date.format(DateTimeFormatter.ofPattern("MMYYYY"));
        return directoryPath + companyName + "/generatedInvoices/" + monthYear + "/";
    }

    private List<String> listFilesUsingDirectoryStream(String dir) throws IOException {
        List<String> fileList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir), "*.{xlsx}")) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.getFileName()
                            .toString());
                }
            }
        }
        return fileList;
    }

    private List<String> listDirectories(String dir) throws IOException {
        List<String> directoriesList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    directoriesList.add(path.getFileName()
                            .toString());
                }
            }
        }
        return directoriesList;
    }

    private List<String> filterSuppliers(List<String> list) {
        Pattern pattern = Pattern.compile("supplier");
        return filterListByPattern(pattern, list);
    }

    private List<String> filterCustomers(List<String> list) {
        Pattern pattern = Pattern.compile("customer");
        return filterListByPattern(pattern, list);
    }

    private List<String> filterListByPattern(Pattern pattern, List<String> list) {
        return list.stream()
                .filter(pattern.asPredicate())
                .collect(Collectors.toList());
    }

    public Supplier loadSupplier(String path) throws IOException, InstantiationException, IllegalAccessException {
        ExcelSheetDescriptor<Supplier> sheetDescriptor = new ExcelSheetDescriptor<>(Supplier.class).setHasHeader();
        List<Supplier> rows = ExcelUtils.readFirstSheet(path, sheetDescriptor);
        Supplier supplier = rows.get(0);
        return supplier;
    }

    @SneakyThrows
    public Customer loadCustomer(String path) {
        ExcelSheetDescriptor<Customer> sheetDescriptor = new ExcelSheetDescriptor<>(Customer.class).setHasHeader();
        List<Customer> rows = ExcelUtils.readFirstSheet(path, sheetDescriptor);
        Customer customer = rows.get(0);
        return customer;
    }

    private XSSFWorkbook getSheetFromInvoiceTemplate(String invoiceTemplatePath) throws IOException {
        FileInputStream file = new FileInputStream(invoiceTemplatePath);
        return new XSSFWorkbook(file);
    }

    private Sheet updateSupplier(Sheet sheet, Supplier supplier) {
        sheet.getRow(1).getCell(0).setCellValue(supplier.getName());
        sheet.getRow(2).getCell(0).setCellValue("Nr.Reg.Com: " + supplier.getRegistrationNumber());
        sheet.getRow(3).getCell(0).setCellValue("CIF: " + supplier.getCIF());
        sheet.getRow(4).getCell(0).setCellValue("Sediu: " + supplier.getAddress());
        sheet.getRow(5).getCell(0).setCellValue("Banca: " + supplier.getBank());
        sheet.getRow(6).getCell(0).setCellValue("IBAN(RON): " + supplier.getIBAN());
        return sheet;
    }

    private void updateCustomer(Sheet sheet, Customer customer) {
        sheet.getRow(1).getCell(6).setCellValue(customer.getName());
        sheet.getRow(2).getCell(6).setCellValue("Nr.Reg.Com: " + customer.getRegistrationNumber());
        sheet.getRow(3).getCell(6).setCellValue("CIF: " + customer.getCIF());
        sheet.getRow(4).getCell(6).setCellValue("Sediu: " + customer.getAddress());
        sheet.getRow(5).getCell(6).setCellValue("Banca: " + customer.getBank());
        sheet.getRow(6).getCell(6).setCellValue("IBAN(RON): " + customer.getIBAN());
        sheet.getRow(17).getCell(5).setCellValue(customer.getAmount());
    }

    private void updateInvoiceSeriesAndNumber(Sheet sheet, String seriesAndNumber) {
        sheet.getRow(11).getCell(3).setCellValue(seriesAndNumber);
    }

    private void updateInvoiceDate(Sheet sheet) {
        sheet.getRow(12).getCell(3).setCellValue(LocalDate.now());
    }

    @SneakyThrows
    private void loadInvoiceControllerForCompany(String companyName) {
        companyInvoiceController = getSheetFromInvoiceTemplate(directoryPath + companyName + "/" + invoiceControllerFileName);
    }

    @SneakyThrows
    private void saveInvoiceControllerForCompany(String companyName) {

        if (shouldUpdateInvoiceController) {
            FileOutputStream outputStream = new FileOutputStream(directoryPath + companyName + "/" + invoiceControllerFileName);
            companyInvoiceController.setForceFormulaRecalculation(true);
            companyInvoiceController.write(outputStream);
            companyInvoiceController.close();
            System.out.println("invoice controller updated for " + companyName);
            shouldUpdateInvoiceController = false;
        }
    }

    private String getNextCompanyInvoiceSerialAndNumber() {
        LocalDate currentDate = LocalDate.now();
        Sheet sheet = companyInvoiceController.getSheetAt(0);
        String currentYear2Digits = String.valueOf((currentDate.getYear() % 100));

        int rowIndex = getLastIndexWithNotEmptyData(sheet);

        String serial = sheet.getRow(rowIndex).getCell(0).getStringCellValue();
        int number = (int) sheet.getRow(rowIndex).getCell(2).getNumericCellValue();
        sheet.createRow(++rowIndex).createCell(0).setCellValue(serial);
        sheet.getRow(rowIndex).createCell(1).setCellValue(currentYear2Digits);
        sheet.getRow(rowIndex).createCell(2).setCellValue(++number);
        sheet.getRow(rowIndex).createCell(3).setCellValue(currentDate.getDayOfMonth() + "." + currentDate.getMonth() + "." + currentDate.getYear() % 100);
        this.shouldUpdateInvoiceController = true;
        return serial + "-" + currentYear2Digits + "-" + number;
    }

    private int getLastIndexWithNotEmptyData(Sheet sheet) {
        int rowIndex = sheet.getLastRowNum();
        for (int rowNum = sheet.getLastRowNum(); rowNum >= 0; rowNum--) {
            final Row row = sheet.getRow(rowNum);
            if (row != null && row.getCell(0) != null && row.getCell(0).getStringCellValue().length() != 0) {
                rowIndex = rowNum;
                break;
            }
        }
        return rowIndex;
    }

    private void createDirectoriesInPathIfNotExists(String path) {
        File pathAsFile = new File(path);
        if (!Files.exists(Paths.get(path))) {
            pathAsFile.mkdirs();
        }
    }

}

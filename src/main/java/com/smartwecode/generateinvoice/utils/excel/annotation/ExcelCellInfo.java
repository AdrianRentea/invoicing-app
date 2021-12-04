package com.smartwecode.generateinvoice.utils.excel.annotation;


import com.smartwecode.generateinvoice.utils.excel.cellParser.CellParser;
import com.smartwecode.generateinvoice.utils.excel.cellParser.DefaultCellParser;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface ExcelCellInfo {
	int index();
	Class<? extends CellParser> cellParser() default DefaultCellParser.class;
}

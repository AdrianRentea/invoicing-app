package com.smartwecode.generateinvoice.utils.excel.cellParser;

import com.smartwecode.generateinvoice.utils.reflection.ReflectionUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;

public class CellPercentageParser extends CellParser {
			
	public void parse(Cell cell, Object object, Field field) {
		try {
			ReflectionUtils.setField(object, field,cell.getNumericCellValue() * 100);
		} catch (NumberFormatException | IllegalAccessException e) {
			logDefaultFailure(e, cell, field);
		}
	}

}

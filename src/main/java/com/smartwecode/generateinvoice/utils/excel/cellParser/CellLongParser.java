package com.smartwecode.generateinvoice.utils.excel.cellParser;

import com.smartwecode.generateinvoice.utils.reflection.ReflectionUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;

public class CellLongParser extends CellParser {

	public void parse(Cell cell, Object object, Field field) {
		String stringValue = cell.getStringCellValue().trim();
		try {
			ReflectionUtils.setField(object, field, Long.parseLong(stringValue));
		} catch (NumberFormatException | IllegalAccessException e) {
			logDefaultFailure(e, cell, field);
		}
	}

}

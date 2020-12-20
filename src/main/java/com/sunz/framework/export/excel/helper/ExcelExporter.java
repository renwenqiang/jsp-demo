//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel.helper;

import com.sunz.framework.core.Config;
import com.sunz.framework.util.StringUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelExporter {
    private String boolTrueValue = "是";
    private String boolFalseValue = "否";
    private boolean binaryAsImage = true;
    private String sheetName = "导出";
    private Map<String, String> nameMapping;
    private List<String> columnNames;
    private boolean onlyAssignedColumn = false;
    private IMetaHelper metaHelper = null;
    private HSSFWorkbook m_Workbook;
    private HSSFSheet m_Sheet;
    private int currentSheetRowIndex = 0;
    private short dateFormatIndex;
    private String dateFormat = StringUtil.ifEmpty(Config.get("poi.exportDateFormat"), "yyyy年M月d日");
    private DateFormat dateFormatter;
    private short numberFormatIndex;
    private String numberFormat = StringUtil.ifEmpty(Config.get("poi.exportNumberFormat"), "0");
    private int dateColumnWidth = 0;
    private List<String> dictFields;
    private List<Integer> ignoreColumns;
    private List<Integer> ignoreRows;
    private int startColumn = 0;
    private int startRow = 0;

    public ExcelExporter() {
    }

    public void setBoolTrueValue(String boolTrueValue) {
        this.boolTrueValue = boolTrueValue;
    }

    public void setBoolFalseValue(String boolFalseValue) {
        this.boolFalseValue = boolFalseValue;
    }

    public void setBinaryAsImage(boolean binaryAsImage) {
        this.binaryAsImage = binaryAsImage;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
        if (this.m_Workbook != null && this.m_Sheet != null) {
            this.m_Workbook.setSheetName(this.m_Workbook.getSheetIndex(this.m_Sheet), sheetName);
        }

    }

    public void setNameMapping(Map<String, String> nameMapping) {
        this.nameMapping = nameMapping;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public void setOnlyAssignedColumn(boolean onlyAssigned) {
        this.onlyAssignedColumn = onlyAssigned;
    }

    private void initParams() {
        this.dateFormatter = new SimpleDateFormat(this.dateFormat);
        if (this.ignoreRows == null) {
            this.ignoreRows = new ArrayList();
        }

    }

    private void createHead() {
        if (this.startRow == 0 && this.columnNames != null) {
            HSSFRow row = this.m_Sheet.createRow(0);
            int cIndex = 0;
            Iterator var3 = this.columnNames.iterator();

            while(var3.hasNext()) {
                String cName = (String)var3.next();
                HSSFCell cell = row.createCell(cIndex++);
                cell.setCellValue(cName);
            }
        }

    }

    public void setTemplate(byte[] template) {
        this.m_Workbook = this.getStructure(template);
        this.initParams();
    }

    public void setCurrentSheet(int sheetIndex) {
        if (this.m_Workbook == null) {
            this.setTemplate((byte[])null);
        }

        if (this.m_Sheet != null) {
            this.createHead();
        }

        while((this.m_Sheet = this.m_Workbook.getSheetAt(sheetIndex)) == null) {
            this.m_Workbook.createSheet(this.sheetName);
        }

        this.currentSheetRowIndex = 0;
    }

    public void input(Object data) {
        if (this.metaHelper == null) {
            this.metaHelper = this.getHelper(data);
            this.metaHelper.Sampling(data, this.dictFields, this.nameMapping, this.startColumn, this.ignoreColumns);
            this.columnNames = this.metaHelper.getCaptions();
        }

        if (this.m_Sheet == null) {
            this.setCurrentSheet(0);
        }

        if (this.currentSheetRowIndex == 0) {
            this.createHead();
            this.currentSheetRowIndex = this.startRow < 1 ? 0 : this.startRow - 1;
        }

        while(this.ignoreRows.contains(++this.currentSheetRowIndex)) {
        }

        HSSFRow row = this.m_Sheet.createRow(this.currentSheetRowIndex);
        this.recordForObject(this.m_Workbook, this.m_Sheet, row, data, this.metaHelper);
    }

    public void write(OutputStream stream) throws IOException {
        this.m_Workbook.write(stream);
    }

    public void dispose() {
        this.m_Sheet = null;
        this.m_Workbook = null;
        this.columnNames = null;
        this.dateFormatter = null;
        this.dictFields = null;
        this.ignoreColumns = null;
        this.ignoreRows = null;
        this.metaHelper = null;
        this.nameMapping = null;
    }

    private HSSFWorkbook getWorkbook(IGradualReader<Object> dataReader, byte[] template) {
        if (dataReader == null) {
            return null;
        } else {
            this.setTemplate(template);
            this.setCurrentSheet(0);

            while(dataReader.hasMore()) {
                this.input(dataReader.read());
            }

            return this.m_Workbook;
        }
    }

    /** @deprecated */
    @Deprecated
    public byte[] toExcel(Object[] datas, byte[] template) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            this.getWorkbook(IGradualReader.getArrayReader(datas), template).write(os);
        } catch (IOException var8) {
            var8.printStackTrace();
        } finally {
            this.dispose();
        }

        return os.toByteArray();
    }

    public void toExcel(Object[] datas, byte[] template, OutputStream stream) throws IOException {
        this.toExcel(IGradualReader.getArrayReader(datas), template, stream);
    }

    public void toExcel(IGradualReader<Object> reader, byte[] template, OutputStream stream) throws IOException {
        this.getWorkbook(reader, template);
        this.m_Workbook.write(stream);
        this.dispose();
    }

    public void setDateFormat(String format) {
        this.dateFormat = format;
    }

    public void setNumberFormat(String format) {
        this.numberFormat = format;
    }

    public void setDateColumnWidth(int width) {
        this.dateColumnWidth = width;
    }

    private IMetaHelper getHelper(Object o) {
        if (o == null) {
            return null;
        } else {
            return (IMetaHelper)(o instanceof Map ? new MapHelper() : new BeanHelper());
        }
    }

    private void recordForObject(HSSFWorkbook workBook, HSSFSheet sheet, HSSFRow row, Object o, IMetaHelper helper) {
        if (row != null && o != null) {
            if (helper == null) {
                helper = this.getHelper(o);
            }

            int fieldCount = helper.getCount();

            for(int i = 0; i < fieldCount; ++i) {
                HSSFCell cell = row.createCell(i);
                Object value = helper.getValue(o, i);
                if (value != null) {
                    if (value instanceof Boolean) {
                        cell.setCellValue((Boolean)value ? this.boolTrueValue : this.boolFalseValue);
                    } else if (value instanceof Date) {
                        cell.setCellValue(this.dateFormatter.format(value));
                    } else if (!(value instanceof Integer) && !(value instanceof Short) && !(value instanceof Float) && !(value instanceof Long) && !(value instanceof Byte) && !(value instanceof Double)) {
                        if (value instanceof byte[] && this.binaryAsImage) {
                            byte[] bsValue = (byte[])((byte[])value);
                            int rowNum = row.getRowNum();
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short)i, rowNum, (short)(i + 1), rowNum + 1);
                            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                            patriarch.createPicture(anchor, workBook.addPicture(bsValue, 5));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }

        }
    }

    public void setDictFields(List<String> dictFields) {
        this.dictFields = dictFields;
    }

    public void setIgnoreColumns(List<Integer> ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
        if (this.ignoreColumns == null) {
            this.ignoreColumns = new ArrayList(0);
        }

    }

    public void setIgnoreRows(List<Integer> ignoreRows) {
        this.ignoreRows = ignoreRows;
        if (this.ignoreRows == null) {
            this.ignoreRows = new ArrayList(0);
        }

    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    private HSSFWorkbook createStructure() {
        HSSFWorkbook workBook = new HSSFWorkbook();
        workBook.createSheet(this.sheetName);
        return workBook;
    }

    private HSSFWorkbook getStructure(byte[] template) {
        if (template == null) {
            return this.createStructure();
        } else {
            try {
                HSSFWorkbook workBook = new HSSFWorkbook(new ByteArrayInputStream(template));
                if (workBook.getSheetAt(0) == null) {
                    workBook.createSheet(this.sheetName);
                }

                return workBook;
            } catch (Exception var3) {
                return this.createStructure();
            }
        }
    }
}

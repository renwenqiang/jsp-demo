//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel.helper;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
    private static Logger logger = Logger.getLogger(ExcelReader.class);
    private Map<Integer, String> fieldMapping;
    private List<Integer> ignoreColumns;
    private List<Integer> ignoreRows;
    public static final String Excel_Type_XLS = "xls";
    public static final String Excel_Type_XLSX = "xlsx";
    private Workbook m_WorkBook;
    private Sheet m_Sheet;
    public static int Limit_Warn = 100;
    private int[] m_Indexs;
    private String[] m_Fields;
    private String nullHolder;

    public void setIgnoreColumns(List<Integer> ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
    }

    public void setIgnoreRows(List<Integer> ignoreRows) {
        this.ignoreRows = ignoreRows;
    }

    public void setFieldMapping(Map<Integer, String> fieldMapping) {
        this.fieldMapping = fieldMapping;
    }

    public static ExcelReader.excelType getExcelType(String fileName) {
        if (fileName == null) {
            return ExcelReader.excelType.UNKNOWN;
        } else {
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            return "xls".equals(ext) ? ExcelReader.excelType.XLS : ("xlsx".equals(ext) ? ExcelReader.excelType.XLSX : ExcelReader.excelType.UNKNOWN);
        }
    }

    private Workbook input(InputStream stream, ExcelReader.excelType type) {
        try {
            switch(type) {
            case XLS:
                return this.m_WorkBook = new HSSFWorkbook(stream);
            case XLSX:
                return this.m_WorkBook = new XSSFWorkbook(stream);
            default:
                try {
                    return this.m_WorkBook = new HSSFWorkbook(stream);
                } catch (IOException var4) {
                    return this.m_WorkBook = new XSSFWorkbook(stream);
                }
            }
        } catch (Exception var5) {
            throw new RuntimeException("非正确的Excel文件", var5);
        }
    }

    public ExcelReader(InputStream stream, ExcelReader.excelType type) {
        this.input(stream, type);
    }

    public ExcelReader(String fileName) {
        try {
            this.input(new FileInputStream(fileName), getExcelType(fileName));
        } catch (FileNotFoundException var3) {
            throw new RuntimeException("输入的文件不存在，路径：" + fileName, var3);
        }
    }

    public ExcelReader(byte[] bdata, ExcelReader.excelType type) {
        this.input(new ByteArrayInputStream(bdata), type);
    }

    public Sheet activeSheet(String name) {
        return this.m_Sheet = this.m_WorkBook.getSheet(name);
    }

    public Sheet activeSheet(int index) {
        return this.m_Sheet = this.m_WorkBook.getSheetAt(index);
    }

    public int getSheetCount() {
        return this.m_WorkBook.getNumberOfSheets();
    }

    public String getSheetName(int index) {
        return this.m_WorkBook.getSheetName(index);
    }

    public int getRowCount() {
        if (this.m_Sheet == null) {
            this.activeSheet(0);
        }

        return this.m_Sheet.getPhysicalNumberOfRows();
    }

    public int getColumnCount() {
        if (this.m_Sheet == null) {
            this.activeSheet(0);
        }

        return this.m_Sheet.getRow(0).getLastCellNum();
    }

    public List<Map<String, Object>> readToMaps(int start, int limit) {
        List<Map<String, Object>> result = new ArrayList();
        int dataCount = this.getRowCount();
        if (dataCount == 0) {
            return result;
        } else {
            if (limit < 0) {
                limit = this.getRowCount();
            }

            if (limit > Limit_Warn) {
                logger.warn("代码正在通过ExcelReader一次性读取【" + limit + "】条数据入内存");
            }

            if (this.m_Sheet == null) {
                this.activeSheet(0);
            }

            if (start < 0) {
                start = 0;
            }

            if (start >= dataCount) {
                return result;
            } else {
                if (this.m_Indexs == null) {
                    if (this.fieldMapping == null) {
                        this.fieldMapping = new HashMap();
                        Row rSample = this.m_Sheet.getRow(0);
                        int cCount = rSample.getLastCellNum();

                        for(Integer i = Integer.valueOf(rSample.getFirstCellNum()); i < cCount; i = i + 1) {
                            if (this.ignoreColumns == null || !this.ignoreColumns.contains(i)) {
                                String cv = rSample.getCell(i).getStringCellValue();
                                this.fieldMapping.put(i, cv == null ? i.toString() : cv);
                            }
                        }
                    }

                    int[] indexs = new int[this.fieldMapping.size()];
                    String[] fields = new String[indexs.length];
                    Iterator<Entry<Integer, String>> iter = this.fieldMapping.entrySet().iterator();

                    Entry en;
                    for(int index = 0; iter.hasNext(); fields[index++] = (String)en.getValue()) {
                        en = (Entry)iter.next();
                        indexs[index] = (Integer)en.getKey();
                    }

                    this.m_Indexs = indexs;
                    this.m_Fields = fields;
                }

                if (this.ignoreRows == null) {
                    this.ignoreRows = new ArrayList();
                }

                int i = start;

                for(int end = start + (limit > 0 ? limit : dataCount); i < end && i < dataCount; ++i) {
                    if (!this.ignoreRows.contains(i)) {
                        Map<String, Object> mRow = new HashMap();
                        Row row = this.m_Sheet.getRow(i);

                        for(int j = 0; j < this.m_Indexs.length; ++j) {
                            Cell cell = row.getCell(this.m_Indexs[j] - 1);
                            mRow.put(this.m_Fields[j], cell == null ? null : this.getCellValue(cell));
                        }

                        result.add(mRow);
                    }
                }

                return result;
            }
        }
    }

    public List<Object[]> readToArrays(int start, int limit) {
        List<Object[]> result = new ArrayList();
        int dataCount = this.getRowCount();
        if (dataCount == 0) {
            return result;
        } else {
            if (limit < 0) {
                limit = this.getRowCount();
            }

            if (limit > Limit_Warn) {
                logger.warn("代码正在通过ExcelReader一次性读取【" + limit + "】条数据入内存");
            }

            if (this.m_Sheet == null) {
                this.activeSheet(0);
            }

            if (start < 0) {
                start = 0;
            }

            if (start >= dataCount) {
                return result;
            } else {
                if (this.m_Indexs == null) {
                    List<Integer> indexs = new ArrayList();
                    Row rSample = this.m_Sheet.getRow(0);
                    int cCount = rSample.getLastCellNum();

                    for(Integer i = Integer.valueOf(rSample.getFirstCellNum()); i < cCount; i = i + 1) {
                        if (this.ignoreColumns == null || !this.ignoreColumns.contains(i)) {
                            indexs.add(i);
                        }
                    }

                    this.m_Indexs = new int[indexs.size()];

                    for(int i = 0; i < indexs.size(); ++i) {
                        this.m_Indexs[i] = (Integer)indexs.get(i);
                    }
                }

                if (this.ignoreRows == null) {
                    this.ignoreRows = new ArrayList();
                }

                int i = start;

                for(int end = start + (limit > 0 ? limit : dataCount); i < end && i < dataCount; ++i) {
                    if (!this.ignoreRows.contains(i)) {
                        Row row = this.m_Sheet.getRow(i);
                        Object[] aRow = new Object[this.m_Indexs.length];

                        for(int j = 0; j < this.m_Indexs.length; ++j) {
                            aRow[j] = this.getCellValue(row.getCell(this.m_Indexs[j]));
                        }

                        result.add(aRow);
                    }
                }

                return result;
            }
        }
    }

    public List<Map<String, Object>> readToMaps() {
        return this.readToMaps(0, this.getRowCount());
    }

    public List<Object[]> readToArrays() {
        return this.readToArrays(0, this.getRowCount());
    }

    public void setNullHolder(String nullHolder) {
        this.nullHolder = nullHolder;
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        } else {
            Object cellValue = null;
            int cellType = cell.getCellType();
            switch(cellType) {
            case 0:
                if (ExcelReader.DateUtil.isCellDateFormatted(cell)) {
                    cellValue = cell.getDateCellValue();
                } else {
                    cellValue = cell.getNumericCellValue();
                }
                break;
            case 1:
                cellValue = cell.getStringCellValue();
                if (this.nullHolder != null && this.nullHolder.equals(cellValue)) {
                    return null;
                }
                break;
            case 2:
                cellValue = cell.getCellFormula();
                break;
            case 3:
                cellValue = null;
                break;
            case 4:
                cellValue = cell.getBooleanCellValue();
                break;
            case 5:
                cellValue = "错误";
                break;
            default:
                cellValue = null;
            }

            return cellValue;
        }
    }

    static class DateUtil {
        private static final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
        private static final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
        private static final Pattern date_ptrn3a = Pattern.compile("[yYmMdDhHsS]");
        private static final Pattern date_ptrn3b = Pattern.compile("^[\\[\\]yYmMdDhHsS\\-/,. :\"\\\\]+0*[ampAMP/]*$");
        private static final Pattern date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
        private static int lastFormatIndex = -1;
        private static String lastFormatString = null;
        private static boolean cached = false;

        DateUtil() {
        }

        public static boolean isValidExcelDate(double value) {
            return value > -4.9E-324D;
        }

        public static boolean isCellDateFormatted(Cell cell) {
            if (cell == null) {
                return false;
            } else {
                boolean bDate = false;
                double d = cell.getNumericCellValue();
                if (isValidExcelDate(d)) {
                    CellStyle style = cell.getCellStyle();
                    if (style == null) {
                        return false;
                    }

                    int i = style.getDataFormat();
                    String f = style.getDataFormatString();
                    bDate = isADateFormat(i, f);
                }

                return bDate;
            }
        }

        public static boolean isInternalDateFormat(int format) {
            switch(format) {
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 45:
            case 46:
            case 47:
                return true;
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            default:
                return false;
            }
        }

        public static synchronized boolean isADateFormat(int formatIndex, String formatString) {
            if (formatString != null && formatIndex == lastFormatIndex && formatString.equals(lastFormatString)) {
                return cached;
            } else if (isInternalDateFormat(formatIndex)) {
                lastFormatIndex = formatIndex;
                lastFormatString = formatString;
                cached = true;
                return true;
            } else if (formatString != null && formatString.length() != 0) {
                String fs = formatString;
                StringBuilder sb = new StringBuilder(formatString.length());

                for(int i = 0; i < fs.length(); ++i) {
                    char c = fs.charAt(i);
                    if (i < fs.length() - 1) {
                        char nc = fs.charAt(i + 1);
                        if (c == '\\') {
                            switch(nc) {
                            case ' ':
                            case ',':
                            case '-':
                            case '.':
                            case '\\':
                                continue;
                            }
                        } else if (c == ';' && nc == '@') {
                            ++i;
                            continue;
                        }
                    }

                    sb.append(c);
                }

                fs = sb.toString();
                if (date_ptrn4.matcher(fs).matches()) {
                    lastFormatIndex = formatIndex;
                    lastFormatString = formatString;
                    cached = true;
                    return true;
                } else {
                    fs = fs.replaceAll("[\"|']", "").replaceAll("[年|月|日|时|分|秒|毫秒|微秒]", "");
                    fs = date_ptrn1.matcher(fs).replaceAll("");
                    fs = date_ptrn2.matcher(fs).replaceAll("");
                    if (fs.indexOf(59) > 0 && fs.indexOf(59) < fs.length() - 1) {
                        fs = fs.substring(0, fs.indexOf(59));
                    }

                    if (!date_ptrn3a.matcher(fs).find()) {
                        return false;
                    } else {
                        boolean result = date_ptrn3b.matcher(fs).matches();
                        lastFormatIndex = formatIndex;
                        lastFormatString = formatString;
                        cached = result;
                        return result;
                    }
                }
            } else {
                lastFormatIndex = formatIndex;
                lastFormatString = formatString;
                cached = false;
                return false;
            }
        }
    }

    public static enum excelType {
        XLS("xls", 1),
        XLSX("xlsx", 2),
        UNKNOWN("", 0);

        private excelType(String name, int value) {
        }
    }
}

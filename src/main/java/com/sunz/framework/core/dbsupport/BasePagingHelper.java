//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport;

import com.sunz.framework.core.util.IPagingHelper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BasePagingHelper implements IPagingHelper {
    protected static Pattern patternOrderbyCaseSensitive = Pattern.compile("ORDER\\s+BY");
    protected static Pattern patternGroupby = Pattern.compile("GROUP\\s+BY");
    protected static Pattern patternFrom = Pattern.compile("\\bFROM\\b");
    protected static Pattern patternUnion = Pattern.compile("\\bUNION\\b");

    public BasePagingHelper() {
    }

    public String getCountSql(String sql) {
        if (sql == null) {
            return null;
        } else {
            String sqlTest = sql.toUpperCase();
            Matcher m = patternOrderbyCaseSensitive.matcher(sqlTest);
            int obStart = -1;

            int obEnd;
            for(obEnd = -1; m.find(); obEnd = m.end()) {
                obStart = m.start();
            }

            int from;
            int sIndex;
            if (obEnd > 0) {
                int bracketStart = 0;
                from = obEnd;

                for(sIndex = sqlTest.length(); from < sIndex; ++from) {
                    char cur = sqlTest.charAt(from);
                    if (cur == '(') {
                        ++bracketStart;
                    }

                    if (cur == ')') {
                        --bracketStart;
                    }
                }

                if (0 == bracketStart) {
                    sql = sql.substring(0, obStart);
                }
            }

            Matcher mFrom = patternFrom.matcher(sqlTest);
            from = mFrom.find() ? mFrom.start() : 0;
            if (from > -1) {
                sIndex = sqlTest.indexOf("SELECT") + 6;
                int firstBracket = sqlTest.indexOf("(");
                if (firstBracket < 0 || firstBracket > sIndex) {
                    while(firstBracket > 0 && firstBracket < from) {
                        int count = 1;

                        while(count > 0) {
                            ++firstBracket;
                            char cur = sqlTest.charAt(firstBracket);
                            if (cur == '(') {
                                ++count;
                            }

                            if (cur == ')') {
                                --count;
                            }
                        }

                        while(from < firstBracket) {
                            mFrom.find();
                            from = mFrom.start();
                        }

                        firstBracket = sqlTest.indexOf("(", firstBracket);
                    }

                    while(true) {
                        ++sIndex;
                        if (sqlTest.charAt(sIndex) > ' ') {
                            boolean groupby = true;
                            boolean union = true;
                            if (!"TOP".equals(sqlTest.substring(sIndex, sIndex + 3)) && !"DISTINCT".equals(sqlTest.substring(sIndex, sIndex + 8)) && !patternGroupby.matcher(sqlTest).find(from) && !patternUnion.matcher(sqlTest).find(from)) {
                                return "select count(0) " + sql.substring(from);
                            }
                            break;
                        }
                    }
                }
            }

            return "select count(0) from (" + sql + ") t_temp";
        }
    }
}

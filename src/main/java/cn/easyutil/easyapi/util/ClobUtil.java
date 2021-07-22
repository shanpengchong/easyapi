package cn.easyutil.easyapi.util;

import java.sql.Clob;
import java.sql.SQLException;

public class ClobUtil {

    /**
     * 获取字符串
     * @param clob
     * @return
     */
    public static String getStr(Clob clob){
        if(clob == null){
            return null;
        }
        try {
            Long length = clob.length();
            return clob.getSubString(1,length.intValue());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }
}

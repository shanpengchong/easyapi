package cn.easyutil.easyapi.javadoc;

import cn.easyutil.easyapi.entity.read.MethodAlias;
import cn.easyutil.easyapi.util.StringUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 读取java源文件
 */
public class JavaFileReader {

    /**
     * 读取类注释
     *
     * @param file
     * @return
     */
    public static String readClassComment(File file) {
        // 读取源文件，返回key:属性名 val:属性说明
        BufferedReader reader = null;
        String result = "";
        if(!file.exists()){
            return result;
        }
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuffer commentText = new StringBuffer();
            boolean lastLineIsComment = false;
            while ((line = reader.readLine()) != null) {
                boolean thisLineIsComment = false;
                if (line.trim().startsWith("/**")) {
                    commentText = new StringBuffer();
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().startsWith("*") && !line.contains("@")) {
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().endsWith("*/")) {
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().startsWith("/**") && line.trim().endsWith("*/")) {
                    commentText = new StringBuffer();
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.contains("//")) {
                    commentText = new StringBuffer();
                    commentText.append(line.substring(line.indexOf("//") + 2));
                    thisLineIsComment = true;
                }else if(line.startsWith("@") && lastLineIsComment){
                    thisLineIsComment = true;
                }else{
                    thisLineIsComment = false;
                }
                if (line.contains("public") && line.contains("class")) {
                    result = commentText.toString().replaceAll("/|\\*", "").trim();
                    break;
                }
                if(!thisLineIsComment){
                    commentText = new StringBuffer();
                }
                lastLineIsComment = thisLineIsComment;
            }
        } catch (Exception e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 读取方法别名
     *
     * @param file
     * @return
     */
    public static List<MethodAlias> readMethodAliasName(File file) {
        // 读取源文件，返回key:属性名 val:属性说明
        BufferedReader reader = null;
        List<MethodAlias> result = new ArrayList<>();
        if(!file.exists()){
            return result;
        }
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuffer commentText = new StringBuffer();
            boolean lastLineIsComment = false;
            // 属性正则表达式
            while ((line = reader.readLine()) != null) {
                boolean thisLineIsComment = false;
                if (line.trim().startsWith("/**")) {
                    commentText = new StringBuffer();
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().startsWith("*") && !line.contains("@")) {
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().endsWith("*/")) {
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().startsWith("/**") && line.trim().endsWith("*/")) {
                    commentText = new StringBuffer();
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.contains("//")) {
                    commentText = new StringBuffer();
                    commentText.append(line.substring(line.indexOf("//") + 2));
                }else if(line.contains("}") && !line.contains("{")){
                    commentText = new StringBuffer();
                }else if(line.startsWith("@") && lastLineIsComment){
                    thisLineIsComment = true;
                }else{
                    thisLineIsComment = false;
                }
                // 读到方法行
                if (line.trim().startsWith("public") && line.contains("(")) {
                    // 找到方法名
                    line = line.substring(0, line.indexOf("("));
                    String[] keys = line.split("\\s");
                    String methodName = keys[keys.length - 1];
                    MethodAlias mc = new MethodAlias(methodName, commentText.toString().replaceAll("/|\\*", "").trim());
                    result.add(mc);
                    commentText = new StringBuffer();
                }
                if(!thisLineIsComment){
                    commentText = new StringBuffer();
                }
                lastLineIsComment = thisLineIsComment;
            }
        } catch (Exception e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 读取方法参数注释
     *
     * @param file
     * @return
     */
    public static Map<String, Map<String, String>> readMethodParamComment(File file) {
        // 读取源文件，返回key:方法名 val-key:参数名  val-val:参数注释
        BufferedReader reader = null;
        Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>();
        if(!file.exists()){
            return map;
        }
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            //注释内容
            StringBuffer commentText = null;
            //用来存放参数注释的map
            Map<String, String> param = null;
            //本行是否含有注释
            boolean hasText = false;
            //本行的参数名称
            String keyName = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("/**")) {
                    commentText = new StringBuffer();
                    commentText.append(line);
                    param = new HashMap<String, String>();
                }
                if (line.trim().startsWith("*") && line.contains("@param")) {
                    if(commentText != null){
                        String text = line.substring(line.indexOf("@param")+6, line.length());
                        String[] split = text.trim().split("\\s");
                        String key ="";
                        StringBuffer val = new StringBuffer();
                        for (int i = 0; i < split.length; i++) {
                            if(i == 0){
                                key = split[i];
                                continue;
                            }
                            val.append(split[i]);
                        }
                        if(!StringUtil.isEmpty(val.toString().trim())){
                            param.put(key, val.toString());
                            hasText = true;
                        }
                        keyName = key;
                    }
                }
                //注释可能在参数名的下一行
                if(line.trim().startsWith("*") && !line.contains("@") && commentText!=null && !hasText && !line.trim().endsWith("*/")){
                    param.put(keyName, line.replace("*", "").trim());
                }
                if(line.contains("}") && !line.contains("{")){
                    commentText = null;
                    param = null;
                }
                // 读到方法行
                if (line.trim().startsWith("public") && line.contains("(")) {
                    // 找到方法名
                    line = line.substring(0, line.indexOf("("));
                    String[] keys = line.split("\\s");
                    String methodName = keys[keys.length - 1];
                    if(!StringUtil.isEmpty(commentText)){
                        map.put(methodName, param);
                    }
                    commentText = null;
                    param = null;
                }
            }
        } catch (Exception e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * 读取源文件获取java字段名和注释
     *
     * @param beanFile
     * @return
     */
    public static Map<String, String> readFieldComment(File beanFile) {
        Map<String, String> map = new HashMap<String, String>();
        if(!beanFile.exists()){
            return map;
        }
        // 读取源文件，返回key:属性名 val:属性说明
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(beanFile)));
            String line;
            StringBuffer commentText = new StringBuffer();
            // 属性正则表达式
            Pattern p = Pattern.compile("private.+?;");
            boolean lastLineIsComment = false;
            while ((line = reader.readLine()) != null) {
                boolean thisLineIsComment = false;
                if (line.trim().startsWith("/**")) {
                    commentText = new StringBuffer();
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().startsWith("*")) {
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().endsWith("*/")) {
                    commentText.append(line);
                    thisLineIsComment = true;
                }else if (line.trim().startsWith("/**") && line.trim().endsWith("*/")) {
                    commentText = new StringBuffer();
                    thisLineIsComment = true;
                    commentText.append(line);
                }else if (line.contains("//")) {
                    commentText = new StringBuffer();
                    commentText.append(line.substring(line.indexOf("//") + 2));
                    thisLineIsComment = true;
                }else if(line.startsWith("@") && lastLineIsComment){
                    thisLineIsComment = true;
                }else{
                    thisLineIsComment = false;
                }                Matcher m = p.matcher(line);
                while (m.find()) {
                    if (line.trim().contains("publicclass") || line.contains("serialVersionUID")) {
                        commentText = new StringBuffer();
                        continue;
                    }
                    if (line.contains("=")) {
                        line = line.substring(0, line.indexOf("="));
                    }
                    if (line.contains(";")) {
                        line = line.substring(0, line.indexOf(";"));
                    }
                    String[] keys = line.split("\\s");
                    String fieldName = keys[keys.length - 1].replaceAll(";", "");
                    map.put(fieldName, commentText.toString().replaceAll("/|\\*", "").trim());
                    commentText = new StringBuffer();
                }
                if(!thisLineIsComment){
                    commentText = new StringBuffer();
                }
                lastLineIsComment = thisLineIsComment;
            }
        } catch (Exception e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}

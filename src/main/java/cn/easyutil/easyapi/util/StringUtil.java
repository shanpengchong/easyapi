package cn.easyutil.easyapi.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 字符串处理类 */
@SuppressWarnings("restriction")
public class StringUtil {
	/**
	 * 汉语中数字大写
	 */
	private static final String[] CN_UPPER_NUMBER = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	/**
	 * 汉语中货币单位大写，这样的设计类似于占位符
	 */
	private static final String[] CN_UPPER_MONETRAY_UNIT = { "分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾",
			"佰", "仟", "兆", "拾", "佰", "仟" };
	/**
	 * 特殊字符：整
	 */
	private static final String CN_FULL = "整";
	/**
	 * 特殊字符：负
	 */
	private static final String CN_NEGATIVE = "负";
	/**
	 * 金额的精度，默认值为2
	 */
	private static final int MONEY_PRECISION = 2;
	/**
	 * 特殊字符：零元整
	 */
	private static final String CN_ZEOR_FULL = "零元" + CN_FULL;

	/** 常规字符 */
	private static final char[] chs = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
			'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z' };


	/**
	 * 正则校验是否正确手机号
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		if (isEmpty(mobile)) {
			return false;
		}
		Pattern p = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$"); // 验证手机号
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	/**
	 * 验证是否正确邮箱
	 * 
	 * @param mail
	 * @return
	 */
	public static boolean isMail(String mail) {
		if (isEmpty(mail)) {
			return false;
		}
		String reg = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(mail);
		return m.matches();
	}

	/**
	 * 将中文转成拼音，英文不变
	 * 
	 * @param chinese
	 *            汉字串
	 * @return 汉语拼音
	 */
	public static String toPinYin(String chinese) {
		try {
			StringBuffer pybf = new StringBuffer();
			char[] arr = chinese.toCharArray();
			HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
			defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] > 128) {
					try {
						pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						throw new RuntimeException(e);
					}
				} else {
					pybf.append(arr[i]);
				}
			}
			return pybf.toString();
		} catch (Exception e) {
			return chinese;
		}
	}
	
	
	/**
	 * 验证是否是中文
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isChineseChar(String str) {
		if (isEmpty(str)) {
			return false;
		}
		String reg = "^[\u4e00-\u9fa5],{0,}$";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 验证是否是正确身份证号
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean isIdCard(String idCard) {
		if (isEmpty(idCard)) {
			return false;
		}
		String reg = "\\d{17}[\\d|x]|\\d{15}";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(idCard);
		return m.matches();
	}

	/**
	 * 验证是否是http或https路径
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isUrlPath(String url) {
		if (isEmpty(url)) {
			return false;
		}
		if (url.trim().toUpperCase().startsWith("HTTP://") || url.trim().toUpperCase().startsWith("HTTPS://")) {
			return true;
		}
		return false;
	}

	/**
	 * 验证是否正确ip写法
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isIp(String ip) {
		if (isEmpty(ip)) {
			return false;
		}
		String reg = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(ip);
		return m.matches();
	}

	/**
	 * 校验是否是数字
	 * 
	 * @return
	 */
	public static boolean isNumber(String number) {
		try {
			Double.valueOf(number);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 校验是否是整数
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isInteger(String number) {
		try {
			Long.valueOf(number);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
     * 驼峰转下划线格式
     */
    public static  String conversionMapUnderscore(String v) {
        String str = "";
        for (int i = 0; i < v.toCharArray().length; i++) {
            char c = v.toCharArray()[i];
            if (i == 0) {
                c = Character.toLowerCase(c);
            } else if (Character.isUpperCase(c)) {
                str += "_" + Character.toLowerCase(c);
                continue;
            }
            str += c;
        }

        return str;
    }

    /**
     * 下划线转驼峰格式
     */
    public static  String conversionCamelCase(String v) {
        int index = v.indexOf("_");
        while (index != -1) {
            v = v.replace(v.substring(index, index + 2), v.substring(index + 1, index + 2).toUpperCase());
            index = v.indexOf("_");
        }
        return v;
    }
	
	/**
	 * 把输入的金额转换为汉语中人民币的大写(默认精度取小数点两位四舍五入)
	 * 
	 * @param money
	 *            需要转换的金额
	 * @return
	 */
	public static String toChineseMoney(BigDecimal money) {
		return toChineseMoney(money, MONEY_PRECISION);
	}

	/**
	 * 把输入的金额转换为汉语中人民币的大写
	 * 
	 * @param money
	 *            需要转换的金额
	 * @param money_precision
	 *            取金额的精度,小数点后的位数
	 * @return
	 */
	private static String toChineseMoney(BigDecimal money, int money_precision) {
		StringBuffer sb = new StringBuffer();
		// -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
		// positive.
		// BigDecimal numberOfMoney = new BigDecimal(money);
		int signum = money.signum();
		// 零元整的情况
		if (signum == 0) {
			return CN_ZEOR_FULL;
		}
		// 这里会进行金额的四舍五入
		long number = money.movePointRight(money_precision).setScale(0, 4).abs().longValue();
		// 得到小数点后两位值
		long scale = number % 100;
		int numUnit = 0;
		int numIndex = 0;
		boolean getZero = false;
		// 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
		if (!(scale > 0)) {
			numIndex = 2;
			number = number / 100;
			getZero = true;
		}
		if ((scale > 0) && (!(scale % 10 > 0))) {
			numIndex = 1;
			number = number / 10;
			getZero = true;
		}
		int zeroSize = 0;
		while (true) {
			if (number <= 0) {
				break;
			}
			// 每次获取到最后一个数
			numUnit = (int) (number % 10);
			if (numUnit > 0) {
				if ((numIndex == 9) && (zeroSize >= 3)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
				}
				if ((numIndex == 13) && (zeroSize >= 3)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
				}
				sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
				sb.insert(0, CN_UPPER_NUMBER[numUnit]);
				getZero = false;
				zeroSize = 0;
			} else {
				++zeroSize;
				if (!(getZero)) {
					sb.insert(0, CN_UPPER_NUMBER[numUnit]);
				}
				if (numIndex == 2) {
					if (number > 0) {
						sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
					}
				} else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
				}
				getZero = true;
			}
			// 让number每次都去掉最后一个数
			number = number / 10;
			++numIndex;
		}
		// 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
		if (signum == -1) {
			sb.insert(0, CN_NEGATIVE);
		}
		// 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
		if (!(scale > 0)) {
			sb.append(CN_FULL);
		}
		return sb.toString();
	}

	/**
	 * 长字符串转短字符串
	 * 
	 * @param url
	 *            原链接
	 * @return
	 */
	public static String toShortString(String url, int in) {
		if (in < 0 || in > 4) {
			in = 0;
		}
		// 对传入网址进行 MD5 加密
		String sMD5EncryptResult = toMD5(url);
		String hex = sMD5EncryptResult;
		String[] resUrl = new String[4];
		for (int i = 0; i < 4; i++) {
			// 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
			String sTempSubString = hex.substring(i * 8, i * 8 + 8);
			// 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用
			// long ，则会越界
			long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
			String outChars = "";
			for (int j = 0; j < 6; j++) {
				// 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
				long index = 0x0000003D & lHexLong;
				// 把取得的字符相加
				outChars += chs[(int) index];
				// 每次循环按位右移 5 位
				lHexLong = lHexLong >> 5;
			}
			// 把字符串存入对应索引的输出数组
			resUrl[i] = outChars;
		}
		return resUrl[in];
	}

	/**
	 * 长字符串转短字符串
	 * 
	 * @param url
	 * @return
	 */
	public static String toShortString(String url) {
		return toShortString(url, 0);
	}

	/**
	 * 校验对象是不是为null或者内容为空
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (ObjectUtil.isBaseObject(obj)) {
			return obj.toString().equals("");
		}
		if (obj instanceof Collection) {
			Collection list = (Collection) obj;
			return list.size() == 0;
		}
		if (obj instanceof Map) {
			Map map = (Map) obj;
			return map.size() == 0;
		}
		if(obj.getClass().isArray()){
			return Arrays.asList(obj).size() == 0;
		}
		Map<String, Object> map = ObjectUtil.getNotNullAttributes(obj);
		return map.size() == 0;
	}

	/**
	 * 将base64字符串处理成String字节<br/>
	 * 
	 * @param str
	 *            base64的字符串
	 * @return 原字节数据
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String str) {
		try {
			if (str == null) {
				return null;
			}
			return new BASE64Decoder().decodeBuffer(str);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将base64字符串处理成String<br/>
	 * (用默认的String编码集)
	 * 
	 * @param str
	 *            base64的字符串
	 * @return 可显示的字符串
	 * @throws IOException
	 */
	public static String base64Decode(String str) {
		try {
			if (str == null) {
				return null;
			}
			return new String(base64ToByte(str), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将base64字符串处理成String<br/>
	 * (用默认的String编码集)
	 * 
	 * @param str
	 *            base64的字符串
	 * @param charset
	 *            编码格式(UTF-8/GBK)
	 * @return 可显示的字符串
	 * @throws IOException
	 */
	public static String base64Decode(String str, String charset) {
		try {
			if (str == null) {
				return null;
			}
			return new String(base64ToByte(str), charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将字节数据处理成base64字符串<br/>
	 * 
	 * @param bts
	 *            字节数据
	 * @return base64编码后的字符串(用于传输)
	 * @throws IOException
	 */
	public static String base64Encode(byte[] bts) {
		if (bts == null || bts.length == 0) {
			return null;
		}
		String base64Str = new BASE64Encoder().encode(bts);
		base64Str = base64Str.replace("\r", "").replace("\n", "");
		return base64Str;
	}

	/**
	 * 将String处理成base64字符串<br/>
	 * (用默认的String编码集) @param oldStr 原字符串 @return base64编码后的字符串(用于传输) @throws
	 */
	public static String base64Encode(String oldStr) {
		if (oldStr == null) {
			return null;
		}
		try {
			byte[] bts = oldStr.getBytes("UTF-8");
			String base64Str = new BASE64Encoder().encode(bts);
			base64Str = base64Str.replace("\r", "").replace("\n", "");
			return base64Str;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将String处理成base64字符串<br/>
	 * (用默认的String编码集)
	 * 
	 * @param oldStr
	 *            原字符串
	 * @return base64编码后的字符串(用于传输)
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Encode(String oldStr, String charset) {
		try {
			if (oldStr == null) {
				return null;
			}
			byte[] bts = oldStr.getBytes(charset);
			return new BASE64Encoder().encode(bts);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 下面这个函数用于将字节数组换成成16进制的字符串 */
	public static String byteArrayToHex(byte[] byteArray) {
		// 首先初始化一个字符数组，用来存放每个16进制字符
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
		char[] resultCharArray = new char[byteArray.length * 2];
		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		// 字符数组组合成字符串返回
		return new String(resultCharArray);
	}

	/** 将byte转换为MD5 */
	public static String toMD5(byte[] sourceData) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(sourceData);
			return byteArrayToHex(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 将字符串转换为MD5 */
	public static String toMD5(String sourceData) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(sourceData.getBytes());
			return byteArrayToHex(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 将字符串转换为MD5 */
	public static String toMD5(String sourceData, String sourceCharset) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(sourceData.getBytes(sourceCharset));
			return byteArrayToHex(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 将byte转换为SHA-1 */
	public static String toSHA1(byte[] sourceData) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(sourceData);
			return byteArrayToHex(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 将字符串转换为SHA-1 */
	public static String toSHA1(String sourceData) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(sourceData.getBytes());
			return byteArrayToHex(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 将字符串转换为SHA-1 */
	public static String toSHA1(String sourceData, String sourceCharset) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(sourceData.getBytes(sourceCharset));
			return byteArrayToHex(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 转换为全角
	 * 
	 * @param input
	 *            需要转换的字符串
	 * @return 全角字符串.
	 */
	public static String toFull(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// 空格单独处理, 其余的偏移量为65248
			if (c[i] == ' ') {
				c[i] = '\u3000'; // 中文空格
			} else if (c[i] < 128) {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 转换为半角
	 * 
	 * @param input
	 *            需要转换的字符串
	 * @return 半角字符串
	 */
	public static String toHalf(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// 是否是中文空格， 单独处理
			if (c[i] == '\u3000') {
				c[i] = ' ';
			}
			// 校验是否字符值是否在此数值之间
			else if (c[i] > 65248 && c[i] < (128 + 65248)) {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 将字符串转换为unicode编码
	 * 
	 * @param input
	 *            要转换的字符串(主要是包含中文的字符串)
	 * @return 转换后的unicode编码
	 */
	public static String unicodEncode(String input) {
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			// 取出每一个字符
			char c = input.charAt(i);
			String hexStr = Integer.toHexString(c);
			while (hexStr.length() < 4) {
				hexStr = "0" + hexStr;
			}
			// 转换为unicode
			unicode.append("\\u" + hexStr);
		}
		return unicode.toString();
	}

	/**
	 * 将unicode编码还原为字符串
	 * 
	 * @param input
	 *            unicode编码的字符串
	 * @return 原始字符串
	 */
	public static String unicodDecode(String input) {
		StringBuffer string = new StringBuffer();
		String[] hex = input.split("\\\\u");
		for (int i = 1; i < hex.length; i++) {
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);
			// 追加成string
			string.append((char) data);
		}
		return string.toString();
	}

	/** 将字符串转换为url参数形式(中文和特殊字符会以%xx表示) */
	public static String UrlEncode(String input) {
		return UrlEncode(input, "UTF-8");
	}

	/** 将字符串转换为url参数形式(中文和特殊字符会以%xx表示) */
	public static String UrlEncode(String input, String charset) {
		try {
			return URLEncoder.encode(input, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 将url参数形式的字符串转换为原始字符串(中文和特殊字符会以%xx表示) */
	public static String UrlDecode(String input) {
		return UrlDecode(input, "UTF-8");
	}

	/** 将url参数形式的字符串转换为原始字符串(中文和特殊字符会以%xx表示) */
	public static String UrlDecode(String input, String charset) {
		try {
			return URLDecoder.decode(input, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * DES加密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @param charset
	 *            原文的编码集
	 * @return 加密后的密文
	 */
	public static String DESEncode(String text, String token, String charset) {
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			byte[] btToken = charset == null ? token.getBytes() : token.getBytes(charset);
			byte[] btText = charset == null ? text.getBytes() : text.getBytes(charset);
			DESKeySpec desKeySpec = new DESKeySpec(btToken);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(btToken);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] bts = cipher.doFinal(btText);
			return base64Encode(bts);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * DES加密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @param charset
	 *            原文的编码集
	 * @return 加密后的密文
	 */
	public static String DESEncode(String text, String token) {
		return DESEncode(text, token, null);
	}

	/**
	 * DES解密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @param charset
	 *            原文的编码集
	 * @return 原文字符串
	 */
	public static String DESDecode(String text, String token, String charset) {
		try {
			byte[] bytesrc = base64ToByte(text);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			byte[] btToken = charset == null ? token.getBytes() : token.getBytes(charset);
			DESKeySpec desKeySpec = new DESKeySpec(btToken);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(btToken);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			byte[] retByte = cipher.doFinal(bytesrc);
			return charset == null ? new String(retByte) : new String(retByte, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * DES解密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @param charset
	 *            原文的编码集
	 * @return 原文字符串
	 */
	public static String DESDecode(String text, String token) {
		return DESDecode(text, token, null);
	}

	/**
	 * AES加密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @param charset
	 *            原数据字符集
	 * @return 加密后的密文
	 */
	private static String toAES(String text, String token, String charset) {
		if (token.length() % 16 != 0) {
			throw new RuntimeException("the key size not 16");
		}
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(token.getBytes(), "AES"));
			byte[] tempBts = cipher.doFinal(charset == null ? text.getBytes() : text.getBytes(charset));
			return base64Encode(tempBts).replace("\r", "").replace("\n", "");
		} catch (Exception e) {
			throw new RuntimeException("encryption or Decrypt exception", e);
		}
	}

	/**
	 * AES加密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @return 加密后的密文
	 */
	public static String AESEncode(String text, String token) {
		return toAES(text, token, "utf-8");
	}

	/**
	 * AES加密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @param charset
	 *            字符集
	 * @return 加密后的密文
	 */
	public static String AESEncode(String text, String token, String charset) {
		return toAES(text, token, charset);
	}

	/**
	 * AES解密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @param charset
	 *            原数据字符集
	 * @return 解密后的原文
	 */
	private static String AESToString(String text, String token, String charset) {
		if (token.length() % 16 != 0) {
			throw new RuntimeException("the key size not 16");
		}
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(token.getBytes(), "AES"));
			byte[] bts = cipher.doFinal(base64ToByte(text));
			return charset == null ? new String(bts) : new String(bts, charset);
		} catch (Exception e) {
			throw new RuntimeException("encryption or Decrypt exception", e);
		}
	}

	/**
	 * AES解密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @return 解密后的原文
	 */
	public static String AESDecode(String text, String token) {
		return AESToString(text, token, "utf-8");
	}

	/**
	 * AES解密
	 * 
	 * @param text
	 *            要加密的数据
	 * @param token
	 *            约定密串
	 * @param charset
	 *            原数据字符集
	 * @return 解密后的原文
	 */
	public static String AESDecode(String text, String token, String charset) {
		return AESToString(text, token, charset);
	}

	/** 字符转换为16进制码 */
	public static String hexEncode(String src) {
		return hexEncode(src.getBytes());
	}

	/** 字节数据转换为16进制码 */
	public static String hexEncode(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/** 16进制码还原字节数据 */
	public static byte[] hexDecode(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/** 字符转换16进制 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String getRandomString(int length, String text) {
		if (isEmpty(text)) {
			throw new NullPointerException("the parameter must not be null");
		}
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(text.length());
			sb.append(text.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 获取uuid随机串,替换特殊字符
	 * 
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String getRandomString(int length) {
		return getRandomString(length, new String(chs));
	}

	/** 自定义解密 */
	public static String unSecret(String val, String token) {
		char[] valBts = val.toCharArray();
		char[] tokenBts = token.toCharArray();
		return secret(valBts, tokenBts, false);
	}

	/** 自定义加密(不增长加密) */
	public static String toSecret(Object val, String token) {
		char[] valBts = val.toString().toCharArray();
		char[] tokenBts = token.toString().toCharArray();
		return secret(valBts, tokenBts, true);
	}

	/**
	 * 自定义加解密
	 * 
	 * @param val
	 *            原数据
	 * @param token
	 *            token数据
	 * @param isSecret
	 *            是否是加密（true:加密 , false:解密）
	 * @return 返回处理后的数据
	 */
	private static String secret(char[] val, char[] token, boolean isSecret) {
		char[] old = Arrays.copyOf(val, val.length);
		int length = old.length < token.length ? token.length : old.length;
		for (int i = 0, vm = 0, tm = 0; i < length; i++, vm++, tm++) {
			vm = vm >= old.length ? 0 : vm;
			tm = tm >= token.length ? 0 : tm;
			int a = find(old[vm]);
			int b = token[tm];
			if (isSecret) {
				old[vm] = chs[(a + b) % chs.length];
			} else {
				while (a < b) {
					a += chs.length;
				}
				old[vm] = chs[(a - b) % chs.length];
			}
		}
		return new String(old);
	}

	/** 从字符集合中查找某一个字符的位置 */
	private static int find(char ch) {
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] == ch) {
				return i;
			}
		}
		throw new RuntimeException("parameter must be english or number");
	}

	/**
	 * 自定义加解密
	 * 
	 * @param val
	 *            原数据
	 * @param token
	 *            token数据
	 * @param isSecret
	 *            是否是加密（true:加密 , false:解密）
	 * @return 返回处理后的数据
	 */
	public static byte[] secret(byte[] val, byte[] token, boolean isSecret) {
		byte[] old = Arrays.copyOf(val, val.length);
		int length = old.length < token.length ? token.length : old.length;
		for (int i = 0, vm = 0, tm = 0; i < length; i++, vm++, tm++) {
			vm = vm >= old.length ? 0 : vm;
			tm = tm >= token.length ? 0 : tm;
			if (isSecret) {
				old[vm] = (byte) (old[vm] + token[tm]);
			} else {
				old[vm] = (byte) (old[vm] - token[tm]);
			}
		}
		return old;
	}


	/**
	 * 查找分割字符，返回距离最近的一个分割字符的距离和分隔符
	 * 
	 * @param val
	 *            带有分割符的字符串
	 * @param splits
	 *            分隔符集合
	 * @return 返回距离最近的一个分割字符的距离和分隔符 Object[距离, 分割符]
	 */
	public static MinIndex minIndexOf(String val, String... splits) {
		MinIndex mi = new MinIndex();
		for (String split : splits) {
			int temp = val.indexOf(split);
			if (temp == -1) {
				continue;
			} else if (mi.distance == -1 || mi.distance > temp) {
				mi.distance = temp;
				mi.splitStr = split;
			}
		}
		return mi;
	}

	public static class MinIndex {
		// 距离分割符相差几个字符
		public int distance = -1;
		// 距离的符号
		public String splitStr = "+";
	}

	/**
	 * 随机生成常见汉字
	 * 
	 * @param length
	 *            生成汉字个数
	 * @return 生成的汉字
	 */
	public static String getRandomUnicodeChar(int length) {
		String str = "";
		while (length-- > 0) {
			str += getRandomUnicodeChar();
		}
		return str;
	}

	/** 随机生成一个汉字字符 */
	public static String getRandomUnicodeChar() {
		String str = "";
		int highCode;
		int lowCode;
		Random random = new Random();
		highCode = (176 + Math.abs(random.nextInt(39))); // B0 + 0~39(16~55)
															// 一级汉字所占区
		lowCode = (161 + Math.abs(random.nextInt(93))); // A1 + 0~93 每区有94个汉字

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(highCode)).byteValue();
		b[1] = (Integer.valueOf(lowCode)).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return str;
	}

	/**
	 * 清除非utf8mb4的字符
	 * @param text
	 * @return
	 */
	public static String clearEmoji(String text) {
		byte[] bytes = null;
		try {
			bytes = text.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		int i = 0;
		while (i < bytes.length) {
			short b = bytes[i];
			if (b > 0) {
				buffer.put(bytes[i++]);
				continue;
			}
			b += 256;
			if ((b ^ 0xC0) >> 4 == 0) {
				buffer.put(bytes, i, 2);
				i += 2;
			} else if ((b ^ 0xE0) >> 4 == 0) {
				buffer.put(bytes, i, 3);
				i += 3;
			} else if ((b ^ 0xF0) >> 4 == 0) {
				i += 4;
			}
		}
		buffer.flip();
		try {
			return new String(buffer.array(), "utf-8").trim();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		System.out.println(toPinYin(null));
	}
}

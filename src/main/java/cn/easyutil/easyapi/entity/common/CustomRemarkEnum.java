package cn.easyutil.easyapi.entity.common;

import cn.easyutil.easyapi.util.StringUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * 自定义示例值
 * @author spc
 *
 */
public enum CustomRemarkEnum {

//	/**
//	 * 随机名字,默认用户昵称
//	 */
//	public static final String name = "${name(user)},${name(title)}";
	userName(1,"${char(name)}","用户昵称"),
	titleName(1,"${char(text)}","中文内容"),
	hName(1,"${char(ho)}","公司名称"),
	addressName(1,"${char(address)}","地址"),
	mobile(1,"${char(mobile)}","手机号码"),
	md5(1,"${char(md5)}","随机一个md5串"),
	aes(1,"${char(aes)}","随机一个aes串"),
//	/**
//	 * 随机字符串,默认4个随机数字+字母
//	 */
//	public static final String randomStr = "${char(3)},${char}";
	randomStr(1,"${char(","随机char"),
	rStr(1,"${char}","默认4个随机数字+字母"),
//	
//	/**
//	 * 随机int值,默认0或1
//	 */
//	public static final String randomInt = "${int(0-3)},${int(100)},${int}";
	randomInt(2,"${int(","随机int"),
	rint(2,"${int}","随机默认int"),
//	
//	/**
//	 * 随机double值,默认0-1
//	 */
//	public static final String randomDouble = "${double(0-3)},${double(3.5)},${double}";
	randomDouble(3,"${double(","随机int"),
	rdouble(3,"${double}","随机默认int"),
//	

//	
//	/**
//	 * 随机时间,默认时间戳
//	 */
//	public static final String time = "${time},${time(yyyy-MM-dd HH:mm:ss)}";
	randomTime(5,"${time(","时间转换字符串"),
	rTime(5,"${time}","当前时间戳"),
//	
//	/**
//	 * 随机网络地址,默认图片地址
//	 */
//	public static final String pic = "${url(pic)},${url(vod)},${url}";
	randomUrl(6,"${url(","随机网络地址"),
	rUrl(6,"${url}","随机图片地址"),
	unknow(999,"null","未知");
	
	private Integer type;
	private String rule;
	private String remark;
	
	private CustomRemarkEnum(Integer type, String rule, String remark){
		this.type = type;
		this.rule = rule;
		this.remark = remark;
	}
	
	/** 人名*/
	private static final String[] unames = new String[]{
			"石榴",
			"华安",
			"如花",
			"伯虎",
			"旺财",
			"莲花",
			"八两金"
	};
	
	/** 公司名*/
	private static final String[] hnames = new String[]{
			"杭州驿飞科技有限公司",
			"浙江阿里巴巴集团",
			"深圳腾讯集团",
			"万达企业",
			"开心麻花影视",
			"杭州嘉年华房地产",
			"美国苹果集团"
	};
	
	/** 中文内容*/
	private static final String[] tnames = new String[]{
			"流行蝴蝶剑",
			"LOL之提莫队长",
			"王者荣耀元哥教学",
			"java-从入门到删库",
			"韭菜鸡蛋有大用处",
			"盘点你不知道的陈老师",
			"锄禾日当午，汗滴禾下土"
	};
	
	/** 地区名*/
	private static final String[] anames = new String[]{
			"浙江杭州",
			"苏州",
			"美国加利福尼亚州",
			"创伟科技园",
			"江苏",
			"西安",
			"滨江区"
	};
	
	/** 图片地址*/
	private static final String[] purl = new String[]{
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/2721a0fb957b4949a2c4ef3505e70d97.jpg",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/eaf80ccb4e754f6fada1af42ea737248.jpg",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/778b813fc28048f6848ec7aadc691a13.jpg",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/3ac939b7dc9e422abf4fa981edfb8ca5.jpg",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/104e6044c738429f8679e5bb88e665e4.jpg",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/39addca3d1794be69b57d8ec86dd622b.jpg",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/b6f009df8ff7452e9ae9a96d7312f573.jpg"
	};
	
	/** 视频地址*/
	private static final String[] vurl = new String[]{
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/c74c5c50a7d04d559584867810afd327.mp4",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/3316bf5183bc414982731088f9daf550.mp4",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/184355b287144a54b9c0e9bf265f5b08.mp4",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/1566619e084f4e4b863ca22ffb072c3d.mp4",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/6fbea57381e14afcb5c3aafc85ed7eda.mp4",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/8688586e408f44499c3b7bb30b641579.mp4",
			"https://ssyerv1.oss-cn-hangzhou.aliyuncs.com/picture/c969b347bd634e97b7a262a446a3f09f.mp4"
	};
	

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public static Integer getRule(String rule){
		if(StringUtil.isEmpty(rule)){
			return unknow.getType();
		}
		CustomRemarkEnum[] values = CustomRemarkEnum.values();
		rule = rule.trim().toLowerCase();
		for (CustomRemarkEnum custom : values) {
			if(rule.startsWith(custom.getRule())){
				return custom.getType();
			}
		}
		return unknow.getType();
	}
	
	/**
	 * 获取随机示例
	 * @param rule
	 * @return
	 */
	public static String getExample(String rule){
		if(StringUtil.isEmpty(rule)){
			return "";
		}
		Integer rule2 = getRule(rule);
		if(rule2.equals(unknow.getType())){
			return rule;
		}
		if(!rule.endsWith("}")){
			return rule;
		}
		//需要解析的字符串
		String ruleStr = rule.substring(rule.indexOf("${")+2, rule.length()-1).toLowerCase();
		switch (rule2) {
			case 1:{
				//名称相关
				if(ruleStr.equals("char")){
					return StringUtil.getRandomString(4);
				}else if(ruleStr.equals("char(name)")){
					return unames[new Random().nextInt(unames.length-1)];
				}else if(ruleStr.equals("char(ho)")){
					return hnames[new Random().nextInt(hnames.length-1)];
				}else if(ruleStr.equals("char(address)")){
					return anames[new Random().nextInt(anames.length-1)];
				}else if(ruleStr.equals("char(text)")){
					return tnames[new Random().nextInt(tnames.length-1)];
				}else if(ruleStr.equals("char(mobile)")){
					return "188"+StringUtil.getRandomString(8,"0123456789");
				}else if(ruleStr.equals("char(md5)")){
					return StringUtil.toMD5("123456");
				}else if(ruleStr.equals("char(aes)")){
					return StringUtil.getRandomString(16);
				}
				break;
			}
			case 2:{
				//int
				if(ruleStr.equals("int")){
					return new Random().nextInt(2)+"";
				}
				if(!ruleStr.startsWith("int(") && !ruleStr.endsWith(")")){
					break;
				}
				String intStr = ruleStr.substring(ruleStr.indexOf("int(")+4, ruleStr.length()-1);
				if(!intStr.contains(",")){
					try {
						return new Random().nextInt(Integer.valueOf(intStr))+"";
					} catch (Exception e) {
						break;
					}
				}
				String[] split = intStr.split(",");
				if(split.length <= 1 ){
					try {
						return new Random().nextInt(Integer.valueOf(split[0]))+"";
					} catch (Exception e) {
						break;
					}
				}
				try {
					int s1 = Integer.valueOf(split[0]);
					int s2 = Integer.valueOf(split[1]);
					if(s1 < 0 || s2 < 0){
						return -1+"";
					}
					if(s1 == s2){
						return split[0];
					}else if(s1 > s2){
						while(true){
							int val = new Random().nextInt(s1);
							if(val >= s2){
								return val+"";
							}
						}
					}else{
						while(true){
							int val = new Random().nextInt(s2);
							if(val >= s1){
								return val+"";
							}
						}
					}
				} catch (Exception e) {
					break;
				}
			}
			case 3:{
				//double
				if(ruleStr.equals("double")){
					return 0.25+"";
				}
				if(!ruleStr.startsWith("double(") && !ruleStr.endsWith(")")){
					break;
				}
				String doubleStr = ruleStr.substring(ruleStr.indexOf("double(")+7, ruleStr.length()-1);
				DecimalFormat df = new DecimalFormat("#.00");
				if(!doubleStr.contains(",")){
					try {
						int intValue = Double.valueOf(doubleStr).intValue();
						return new Random().nextInt(intValue)+""+df.format(new Random().nextDouble());
					} catch (Exception e) {
						break;
					}
				}
				String[] split = doubleStr.split(",");
				if(split.length <= 1 ){
					try {
						int intValue = Double.valueOf(doubleStr).intValue();
						return new Random().nextInt(intValue)+""+df.format(new Random().nextDouble());
					} catch (Exception e) {
						break;
					}
				}
				try {
					int s1 = Double.valueOf(split[0]).intValue();
					int s2 = Double.valueOf(split[1]).intValue();
					if(s1 < 0 || s2 < 0){
						return -1+""+df.format(new Random().nextDouble());
					}
					if(s1 == s2){
						return s1+df.format(new Random().nextDouble());
					}else if(s1 > s2){
						while(true){
							int val = new Random().nextInt(s1);
							if(val >= s2){
								return val+""+df.format(new Random().nextDouble());
							}
						}
					}else{
						while(true){
							int val = new Random().nextInt(s2);
							if(val >= s1){
								return val+""+df.format(new Random().nextDouble());
							}
						}
					}
				} catch (Exception e) {
					break;
				}
			}
			case 4:{
				//字符串
				if(ruleStr.equals("char")){
					return StringUtil.getRandomString(4);
				}
				if(!ruleStr.startsWith("char(") && !ruleStr.endsWith(")")){
					break;
				}
				String charStr = ruleStr.substring(ruleStr.indexOf("char(")+5, ruleStr.length()-1);
				try {
					Integer val = Integer.valueOf(charStr);
					return StringUtil.getRandomString(val);
				} catch (Exception e) {
					break;
				}
			}
			case 5:{
				//时间
				if(ruleStr.equals("time")){
					return System.currentTimeMillis()+"";
				}
				if(ruleStr.equals("time()")){
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
				}
				if(!ruleStr.startsWith("time(") && !ruleStr.endsWith(")")){
					break;
				}
				String timeStr = ruleStr.substring(ruleStr.indexOf("time(")+5, ruleStr.length()-1);
				try {
					SimpleDateFormat format = new SimpleDateFormat(timeStr);
					return format.format(System.currentTimeMillis());
				} catch (Exception e) {
					break;
				}
			}
			case 6:{
				//url
				if(ruleStr.equals("url")){
					return purl[new Random().nextInt(purl.length-1)];
				}
				if(!ruleStr.startsWith("url(") && !ruleStr.endsWith(")")){
					break;
				}
				String urlStr = ruleStr.substring(ruleStr.indexOf("url(")+4, ruleStr.length()-1);
				if(urlStr.equals("pic")){
					return purl[new Random().nextInt(purl.length-1)];
				}else if(urlStr.equals("vod")){
					return vurl[new Random().nextInt(vurl.length-1)];
				}else{
					break;
				}
			}
	
			default:
				return rule;
			}
		return rule;
	}
}

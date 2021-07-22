package cn.easyutil.easyapi.entity.doc;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tie;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;
import cn.easyutil.easyapi.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@Tne("easyapi_controller")
public class ControllerBean extends BaseBean {

	/**
	 * 项目id
	 */
	@Tfd("PROJECT_ID")
 	@ApiModelProperty("项目id") 
 	private Long project_id;


	/**
	 * 控制器名称
	 */
	@Tfd("JAVA_NAME")
 	@ApiModelProperty("控制器包名+类名") 
 	private String controller_java_name;

	/**
	 * 控制器别名
	 */
	@Tfd("COMMENT")
	@ApiModelProperty("控制器名称")
 	private String controller_comment;

	@Tfd("COMMENT_PINYIN")
 	private String controller_comment_pinyin;

	@Tfd("API_PATH")
 	@ApiModelProperty("统一请求的父路径")
 	private String api_path;

	@Tfd("SORT")
 	@ApiModelProperty("排序值") 
 	private Integer sort = 0;

	@Tie
 	@ApiModelProperty("接口集合") 
 	private List<InterfaceBean> children;

	public Long getProject_id() {
		return project_id;
	}

	public String getController_java_name() {
		return controller_java_name;
	}

	public String getController_comment() {
		return controller_comment;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public void setController_java_name(String controller_java_name) {
		this.controller_java_name = controller_java_name;
	}

	public void setController_comment(String controller_comment) {
		this.controller_comment = controller_comment;
		this.controller_comment_pinyin = StringUtil.toPinYin(controller_comment);
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public List<InterfaceBean> getChildren() {
		return children;
	}

	public String getApi_path() {
		return api_path;
	}

	public void setApi_path(String api_path) {
		this.api_path = api_path;
	}

	public void setChildren(InterfaceBean children) {
		if(this.children == null){
			this.children = new ArrayList<InterfaceBean>();
		}
		this.children.add(children);
	}

	public String getController_comment_pinyin() {
		return controller_comment_pinyin;
	}

	public void setController_comment_pinyin(String controller_comment_pinyin) {
		this.controller_comment_pinyin = controller_comment_pinyin;
	}
}
package cn.easyutil.easyapi.datasource.bean;

public class EasyapiBindPage {

	private Integer showCount = 5; // 每页显示记录数
    private Integer totalPage = 0; // 总页数
    private Integer totalResult = 0; // 总记录数
    private Integer currentPage = 0; // 当前页
    
    /** 获取总页数*/
    public Integer getTotalPage(){
    	if (showCount == 0 && totalResult == 0) {
    		return 0;
    	}
    	
        if (totalResult % showCount == 0){
        	totalPage = totalResult / showCount;
        }else{
        	totalPage = totalResult / showCount + 1;
        }
        return totalPage;
    }

    public void setTotalPage(Integer totalPage){
        this.totalPage = totalPage;
    }

    /** 获取总记录条数*/
    public Integer getTotalResult(){
        return totalResult;
    }

    public void setTotalResult(Integer totalResult){
    	if (showCount == 0) {
    		this.showCount = totalResult;
    	}
        this.totalResult = totalResult;
    }

    /** 获取当前页*/
    public Integer getCurrentPage(){
        if (getTotalPage()!=0 && currentPage>getTotalPage()) {
        	currentPage = getTotalPage();
        }
        if (currentPage <= 0) {
        	currentPage = 1;
        }
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage){
        this.currentPage = currentPage;
    }


    /** 获取每页显示条数*/
    public Integer getShowCount(){
        return showCount;
    }

    public void setShowCount(Integer showCount){
        this.showCount = showCount;
    }

}

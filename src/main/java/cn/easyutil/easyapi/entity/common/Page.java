package cn.easyutil.easyapi.entity.common;

/**
 * 分页信息
 */
public class Page extends BaseBean {
    private static final long serialVersionUID = 1L;

    private Integer showCount = 5; // 每页显示记录数

    private Integer totalPage = 0; // 总页数

    private Integer totalResult = 0; // 总记录数

    private Integer currentPage = 1; // 当前页

    private Integer currentResult = 0; // 当前记录起始索引

    public Integer getTotalPage() {
        if (showCount == 0 && totalResult == 0) {
            return 0;
        }

        if (totalResult == null) {
            totalResult = 0;
        }

        if (totalResult % showCount == 0) {
            totalPage = totalResult / showCount;
        } else {
            totalPage = totalResult / showCount + 1;
        }
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(Integer totalResult) {
        if (showCount == 0) {
            this.showCount = totalResult;
        }
        this.totalResult = totalResult;
    }

    public Integer getCurrentPage() {
        if (getTotalPage() != 0 && currentPage > getTotalPage()) {
            currentPage = getTotalPage();
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
    public Integer getShowCount() {
        return showCount;
    }

    public void setShowCount(Integer showCount) {
        this.showCount = showCount;
    }

    public Integer getCurrentResult() {
        currentResult = (getCurrentPage() - 1) * getShowCount();
        if (currentResult < 0) {
            currentResult = 0;
        }
        return currentResult;
    }

    public void setCurrentResult(Integer currentResult) {
        this.currentResult = currentResult;
    }


}

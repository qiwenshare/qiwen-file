package com.qiwenshare.common.cbb;


import com.qiwenshare.common.domain.TableQueryBean;

public class MiniuiUtil {

    public static TableQueryBean getMiniuiTablePageQuery(TableQueryBean tableQueryBean) {
        int pageIndex = tableQueryBean.getPage();
        int pageSize = tableQueryBean.getLimit();
        int beginCount = (pageIndex - 1) * pageSize;
        tableQueryBean.setBeginCount(beginCount);
        return tableQueryBean;
    }
}

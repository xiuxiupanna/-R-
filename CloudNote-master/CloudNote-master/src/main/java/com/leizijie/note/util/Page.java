package com.leizijie.note.util;

/**
 * Created by HMF on 2021/07/15 15:36
 */

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页工具类
 */
@Getter
@Setter
public class Page<T> {

    // 这三个值是能拿到的
    private Integer pageNum; // 当前页     （前台传递的参数：如果前台未传递，则默认第一页）
    private Integer pageSize; // 每页显示的数量    （前台传递或后台设定）
    private long totalCount; // 总记录数    （后台数据库中查询得到：count()函数）

    // 这五个我们拿不到，我们要做判断设置的
    private Integer totalPages; // 总页数  （总记录数/每页显示的数量：将参数转换为浮点型，执行除法操作，向上取整，即取大值）
    private Integer prePage; // 上一页   （当前页-1；如果当前页-1 小于1，则上一页为1）
    private Integer nextPage; // 下一页   （当前页+1；如果当前页+1 大于 总页数，则下一页为总页数的值）

    private Integer startNavPage; // 导航开始页  （当前页-5；如果当前页-5 小于 1，则导航开始页为1，此时导航结束页为导航开始数+9，如果导航开始数+9 大于 总页数，则导航结束页为总页数）
    private Integer endNavPage; // 导航结束页  （当前页+4；如果当前页+4 大于 总页数，则导航结束页为总页数；此时导航开始页为导航结束页-9；如果导航结束页-9 小于 1，则导航开始数为1）

    //
    private List<T> dataList; // 当前页的数据集合（查询数据库中指定页的数据列表）

    /**
     * 带参构造：
     * --通过指定参数，得到其他五个分页参数的值
     *
     * @param pageNum
     * @param pageSize
     * @param totalCount
     */
    public Page(Integer pageNum, Integer pageSize, long totalCount) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;

        // 总页数 totalPage
        this.totalPages = (int) Math.ceil(totalCount / (pageSize * 1.0)); // ceil()方法：向上取整

        // 上一页 prePage  （当前页-1；如果当前页-1 小于1，则上一页为1）
        this.prePage = (pageNum - 1 < 1) ? 1 : pageNum - 1;

        // 下一页 nextPage   （当前页+1；如果当前页+1 大于 总页数，则下一页为总页数的值）
        this.nextPage = (pageNum + 1 > totalPages) ? totalPages : pageNum + 1;

        this.startNavPage = pageNum - 5; // 导航开始页 （默认当前页-5）
        this.endNavPage = pageNum + 4; // 导航结束页（默认当前页+4）

        // 导航开始页  （当前页-5；如果当前页-5 小于 1，则导航开始页为1，此时导航结束页为导航开始数+9，如果导航开始数+9 大于 总页数，则导航结束页为总页数）
        if (this.startNavPage < 1) {
            // 如果当前页-5 小于 1，则导航开始页为1
            this.startNavPage = 1;
            // 此时导航结束页为导航开始数+9，如果导航开始数+9 大于 总页数，则导航结束页为总页数
            this.endNavPage = (this.startNavPage + 9 > totalPages) ? totalPages : this.startNavPage + 9;
        }

        // 导航结束页  （当前页+4；如果当前页+4 大于 总页数，则导航结束页为总页数；此时导航开始页为导航结束页-9；如果导航结束页-9 小于 1，则导航开始数为1）
        if (this.endNavPage > totalPages) {
            // 如果当前页+4 大于 总页数，则导航结束页为总页数
            this.endNavPage = totalPages;
            // 此时导航开始页为导航结束页-9；如果导航结束页-9 小于 1，则导航开始数为1
            this.startNavPage = (this.endNavPage - 9 < 1) ? 1 : this.endNavPage - 9;
        }
    }
}

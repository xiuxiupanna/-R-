<%--
  Created by IntelliJ IDEA.
  User: HMF
  Date: 2021/7/16
  Time: 14:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<div class="col-md-9">
    <div class="data_list">
        <div class="data_list_title"><span class="glyphicon glyphicon-signal"></span>&nbsp;数据报表</div>
        <div class="container-fluid">
            <div class="row" style="padding-top: 20px;">
                <div class="col-md-12">
                    <%-- 3.为 ECharts 准备一个具备高宽的 DOM 容器 --%>
                    <%-- 柱状图的容器 --%>
                    <div id="monthChart" style="height: 500px"></div>

                    <%-- 百度地图的加载 --%>
                    <h3 align="center">用户地区分布图</h3>
                    <%-- 百度地图的容器 --%>
                    <div id="baiduMap" style="height: 600px; width: 100%;"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<%--
  关于Echarts报表的使用：
    1.下载Echarts的依赖（JS文件）echarts.min.js
    2.在需要使用的页面引入Echarts的JS文件
    3.为 ECharts 准备一个具备高宽的 DOM 容器
    4.通过 echarts.init 方法初始化一个 echarts 实例并通过 setOption 方法生成一个报表
 --%>
<script type="text/javascript" src="static/echarts/echarts.min.js"></script>

<%-- 引用百度地图API文件，还需要申请百度地图对应的ak密钥，我使用的是课程老师的ak密钥 --%>
<script type="text/javascript"
        src="https://api.map.baidu.com/api?v=1.0&&type=webgl&ak=yrxymYTyuefnxNtXbZcMU8phABXtu6TG"></script>

<script type="text/javascript">
    /**
     * 发送ajax请求获取用于柱状图显示的数据:
     * 通过月份查询对应的云记数量
     */
    $.ajax({
        type: 'get',
        url: 'report',
        data: {
            actionName: "month"
        },
        success: function (result) {
            console.log(result); // 打印查看效果
            if (result.code == 1) {
                // 得到月份（得到X轴的数据）
                var monthArray = result.result.monthArray;
                // 得到月份对应的云记数量（得到Y轴的数据）
                var dataArray = result.result.dataArray;

                // 加载柱状图
                loadMonthChart(monthArray, dataArray);
            }
        }
    });

    /**
     * 把加载显示数据图放到了方法里
     * 加载柱状图
     */
    function loadMonthChart(monthArray, dataArray) {
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('monthChart'));

        // 指定图表的配置项和数据
        // X轴显示的名称
        // var dataAxis = ['点', '击', '柱', '子', '或', '者', '两', '指', '在', '触'];
        var dataAxis = monthArray;

        // Y轴数据
        // var data = [220, 182, 191, 234, 290, 330, 310, 123, 442, 321, 90];
        var data = dataArray;
        var yMax = 10; // 就是这么智能，他会自动帮你调整大小
        var dataShadow = [];

        for (var i = 0; i < data.length; i++) {
            dataShadow.push(yMax);
        }

        var option = {
            // 标题
            title: {
                text: '按月统计', // 主标题
                subtext: '通过月份查询对应的云记数量', // 副标题
                left: 'center' // 标题的对齐方式，center表示居中
            },
            // 提示框
            tooltip: {},
            //
            // legend: {
            //     data: ['月份'],
            // },
            xAxis: { // X轴
                data: dataAxis,
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                }
            },
            yAxis: { // Y轴
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    textStyle: {
                        color: '#999'
                    }
                }
            },
            dataZoom: [
                {
                    type: 'inside'
                }
            ],
            // 系列
            series: [
                {
                    type: 'bar', // bar 柱状图
                    data: data, // Y轴数据
                    // name: '月份',
                    showBackground: true,
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(
                            0, 0, 0, 1,
                            [
                                {offset: 0, color: '#83bff6'},
                                {offset: 0.5, color: '#188df0'},
                                {offset: 1, color: '#188df0'}
                            ]
                        )
                    },
                    emphasis: {
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1,
                                [
                                    {offset: 0, color: '#2378f7'},
                                    {offset: 0.7, color: '#2378f7'},
                                    {offset: 1, color: '#83bff6'}
                                ]
                            )
                        }
                    },
                }
            ]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    }

    // loadBaiduMap(); // 调用下面定义的方法，加载百度地图，这是用来测试用的
    /**
     * 通过用户发布的云记查找坐标
     */
    $.ajax({
        type: "get",
        url: "report",
        data: {
            actionName: "location"
        },
        success: function (result) {
            console.log(result);
            if (result.code == 1) {
                // 加载百度地图
                loadBaiduMap(result.result);
            }
        }
    });


    /**
     * 加载百度地图
     */
    function loadBaiduMap(markers) {
        // 创建地图实例
        var map = new BMapGL.Map("baiduMap");
        // 设置中心点坐标
        var point = new BMapGL.Point(116.404, 39.915);
        // 地图初始化，同时设置地图展示级别
        map.centerAndZoom(point, 15);

        map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
        var scaleCtrl = new BMapGL.ScaleControl();  // 添加比例尺控件
        map.addControl(scaleCtrl);
        var zoomCtrl = new BMapGL.ZoomControl();  // 添加比例尺控件
        map.addControl(zoomCtrl);

        // 判断是否有坐标点标记
        if (markers != null && markers.length > 0) { // 集合中第一个坐标是用户当前所在的位置，其他的是云记记录中对应的经纬度
            // 将查询到的第一条记录位置坐标设置为中心点
            map.centerAndZoom(new BMapGL.Point(markers[0].lon, markers[0].lat), 10);

            // 循环在地图上添加坐标点标记
            for (var i = 0; i < markers.length; i++) { // 从0开始，确保第一条记录也有坐标点标记
                // 创建坐标点标记
                var marker = new BMapGL.Marker(new BMapGL.Point(markers[i].lon, markers[i].lat));
                // 在地图上添加点标记
                map.addOverlay(marker);
            }
        }

        // // 创建点标记
        // var marker1 = new BMapGL.Marker(new BMapGL.Point(116.404, 39.925));
        // var marker2 = new BMapGL.Marker(new BMapGL.Point(116.404, 39.915));
        // var marker3 = new BMapGL.Marker(new BMapGL.Point(116.395, 39.935));
        // var marker4 = new BMapGL.Marker(new BMapGL.Point(116.415, 39.931));
        // // 在地图上添加点标记
        // map.addOverlay(marker1);
        // map.addOverlay(marker2);
        // map.addOverlay(marker3);
        // map.addOverlay(marker4);
    }
</script>


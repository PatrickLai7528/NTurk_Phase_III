<template>
    <el-container class="user-charts">
        <el-main>
            <el-tabs type="border-card" class="tab">
                <el-tab-pane label="我的成长">
                    <div id="pointChart" class="charts"></div>
                </el-tab-pane>
                <el-tab-pane label="我的活跃度">
                    <div id="heat" class="charts"></div>
                </el-tab-pane>
            </el-tabs>
        </el-main>
    </el-container>
</template>

<script>
    import UserUtils from '../../js/utils/UserUtils.js'

    export default {
        data() {
            return {
                isWorker: false
            }
        },
        mounted: function () {
            this.isWorker = UserUtils.isWorker(this);
            this.$nextTick(()=> {
                if(this.isWorker){
                    this.setLineGraph();
                    this.setHeatMap();
                }
            })
        },
        methods: {
            setHeatMap(){
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('heat'));
                let option = {
                    title: [{
                        left: 'center',
                        text: '任务参与次数',
                        textStyle: {
                            fontSize: 20
                        }
                    }],
                    tooltip: {
                        formatter: '{c}次',
                        textStyle: {
                            fontSize: 16
                        }
                    },
                    visualMap: {
                        type: 'piecewise',
                        pieces: [
                            {min: 775, color: '#196127'},
                            {min: 550, max: 775, color: '#239a3b'},
                            {min: 325, max: 550, color: '#7bc96f'},
                            {min: 100, max: 325, color: '#c6e48b'},
                            {max: 100, color: '#DEDEDE'}
                        ],
                        orient: 'horizontal',
                        left: 'right',
                        top: 'middle',
                        textStyle: {
                            fontSize: 14
                        }
                    },

                    calendar: [{
                        //range: ['2017-06-16', '2018-06-16'],
                        range: '2017',
                        cellSize: ['auto', 20],
                        splitLine: {
                            show: false
                        },
                        itemStyle: {
                            borderColor: '#FFFFFF',
                            borderWidth: 2
                        }
                    }],
                    series: [{
                        type: 'heatmap',
                        coordinateSystem: 'calendar',
                        calendarIndex: 0,
                        data: this.getVirtualData(2017),
                        itemStyle: {
                            // borderColor:"red",         //边框颜色
                            // borderWidth: 1,              //柱条的描边宽度，默认不描边。
                            borderType:"solid",         //柱条的描边类型，默认为实线，支持 'dashed', 'dotted'。
                            barBorderRadius: 4,          //柱形边框圆角半径，单位px，支持传入数组分别指定柱形4个圆角半径。
                            opacity: 1,                  //图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。
                        }
                    }]

                };
                alert("I'm here");
                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);
            },
            getVirtualData(year) {
                let echarts = require('echarts');
                year = year || '2017';
                let date = +echarts.number.parseDate(year + '-01-01');
                let end = +echarts.number.parseDate((+year + 1) + '-01-01');
                let dayTime = 3600 * 24 * 1000;
                let data = [];
                for (let time = date; time < end; time += dayTime) {
                    data.push([
                        echarts.format.formatTime('yyyy-MM-dd', time),
                        Math.floor(Math.random() * 1000)
                    ]);
                }
                return data;
            },
            setLineGraph() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('pointChart'));
                let max = 0;

                let data = [
                    {date: "2018-05-13", userPoint: 3, average: 5},
                    {date: "2018-05-14", userPoint: 4, average: 5},
                    {date: "2018-05-16", userPoint: 6, average: 5},
                    {date: "2018-05-17", userPoint: 7, average: 5},
                    {date: "2018-05-18", userPoint: 5, average: 5},
                    {date: "2018-05-20", userPoint: 9, average: 5},
                    {date: "2018-05-23", userPoint: 4, average: 5},
                    {date: "2018-05-25", userPoint: 6, average: 5},
                    {date: "2018-05-26", userPoint: 3, average: 5},
                    {date: "2018-05-27", userPoint: 3, average: 5},
                    {date: "2018-05-28", userPoint: 3, average: 5},
                    {date: "2018-05-31", userPoint: 2, average: 5},
                    {date: "2018-06-01", userPoint: 5, average: 5},
                    {date: "2018-06-04", userPoint: 8, average: 5},
                    {date: "2018-06-07", userPoint: 7, average: 5},
                    {date: "2018-06-09", userPoint: 4, average: 5},
                    {date: "2018-06-10", userPoint: 6, average: 5},
                ];
                let dateList = data.map((item)=> {
                    return item.date;
                });
                let userPointList = data.map((item)=> {
                    return item.userPoint;
                });
                let average = data.map((item)=> {
                    return item.average;
                });


                // 指定图表的配置项和数据
                let option = {

                    textStyle: {
                        fontSize: 20
                    },
                    title: [{
                        left: '10%',
                        text: "得分情况一览",
                        textStyle: {
                            fontSize: 20
                        }
                    }],
                    tooltip: {
                        trigger: 'axis',
                        textStyle: {
                            fontSize: 20
                        }
                    },
                    legend: {
                        right: '5%',
                        data: ['您的得分', '系统均分']
                    },
                    xAxis: {
                        data: dateList
                    },
                    yAxis: {
                        width: 5
                    },
                    series: [
                        {
                            name: '您的得分',
                            type: 'line',
                            lineStyle: {
                                width: 3
                            },
                            z: 2,
                            smooth: true,  //这句就是让曲线变平滑的
                            smoothMonotone: 'none',
                            data: userPointList
                        },
                        {
                            name: '系统均分',
                            type: 'line',
                            lineStyle: {
                                width: 3
                            },
                            z: 1,
                            symbol: 'none',
                            smooth: true,  //这句就是让曲线变平滑的
                            smoothMonotone: 'none',
                            data: average
                        }
                    ]
                };
                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);

                //let route = 'http://localhost:8086/admin/requester/requesterGrowth';
                // this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                //
                // }).catch(function (error) {
                //     console.log(error);
                // });
            },
        }
    }
</script>

<style scoped>
    .tab {
        font-size: 20px;
    }

    .user-charts {
        font-family: Microsoft YaHei;
        text-align: left;
        font-size: 20px;
    }

    .charts {
        margin: 0;
        overflow: hidden;
        width: 1000px;
        height: 500px;
    }
</style>
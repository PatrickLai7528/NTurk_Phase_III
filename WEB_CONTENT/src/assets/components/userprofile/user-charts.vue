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
    import DateUtils from "~/assets/js/utils/DateUtils";

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
            setHeatMap: function () {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('heat'));

                let now = new Date();
                let year = now.getFullYear();
                let month = now.getMonth() + 1;
                let startDay1 = DateUtils.monthFirstDay(year, month, -11);
                let endDay1 = DateUtils.monthLastDay(year, month, -6);
                let startDay2 = DateUtils.monthFirstDay(year, month, -5);
                let endDay2 = DateUtils.monthLastDay(year, month, 0);
                let route = "http://localhost:8086/userProfile/worker/charts/active";

                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response) => {
                    // let data = [];
                    // response.data.forEach((item) => {
                    //     data.push([
                    //         echarts.format.formatTime('yyyy-MM-dd', item.date),
                    //         item.activity
                    //     ])
                    // })
                    let data = this.getVirtualHeatData();
                    let option = {
                        title: [{
                            left: '30%',
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
                                {gte: 4, color: '#196127'},
                                {value: 3, color: '#239a3b'},
                                {value: 2, color: '#7bc96f'},
                                {value: 1, color: '#c6e48b'},
                                {value: 0, color: '#DEDEDE'}
                            ],
                            orient: 'vertical',
                            right: '50px',
                            top: 'middle',
                            textStyle: {
                                fontSize: 18
                            }
                        },

                        calendar: [
                            {
                                //range: ['2017-06-16', '2018-06-16'],
                                range: [startDay1, endDay1],
                                cellSize: [25, 25],
                                splitLine: {
                                    show: false
                                },
                                itemStyle: {
                                    borderColor: '#FFFFFF',
                                    borderWidth: 2
                                }
                            },
                            {
                                //range: ['2017-06-16', '2018-06-16'],
                                top: 300,
                                range: [startDay2, endDay2],
                                cellSize: [25, 25],
                                splitLine: {
                                    show: false
                                },
                                itemStyle: {
                                    borderColor: '#FFFFFF',
                                    borderWidth: 2
                                }
                            }
                        ],
                        series: [
                            {
                                type: 'heatmap',
                                coordinateSystem: 'calendar',
                                calendarIndex: 0,
                                data: data,
                                itemStyle: {
                                    // borderColor:"red",         //边框颜色
                                    // borderWidth: 1,              //柱条的描边宽度，默认不描边。
                                    borderType: "solid",         //柱条的描边类型，默认为实线，支持 'dashed', 'dotted'。
                                    barBorderRadius: 4,          //柱形边框圆角半径，单位px，支持传入数组分别指定柱形4个圆角半径。
                                    opacity: 1,                  //图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。
                                }
                            },
                            {
                                type: 'heatmap',
                                coordinateSystem: 'calendar',
                                calendarIndex: 1,
                                data: data,
                                itemStyle: {
                                    // borderColor:"red",         //边框颜色
                                    // borderWidth: 1,              //柱条的描边宽度，默认不描边。
                                    borderType: "solid",         //柱条的描边类型，默认为实线，支持 'dashed', 'dotted'。
                                    barBorderRadius: 4,          //柱形边框圆角半径，单位px，支持传入数组分别指定柱形4个圆角半径。
                                    opacity: 1,                  //图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。
                                }
                            }
                        ]

                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }).catch(function (error) {
                    console.log(error);
                });
            },
            setLineGraph() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('pointChart'));

                let route = 'http://localhost:8086/userProfile/worker/charts/point';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {

                    // let data = response.data;
                    let data = this.getVirtualLineData();
                    let average = [];
                    let dateList = data.items.map((item)=> {
                        average.push(data.average);
                        return item.date;
                    });
                    let userPointList = data.items.map((item)=> {
                        return item.userPoint;
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
                }).catch(function (error) {
                    console.log(error);
                });
            },
            getVirtualHeatData() {
                let echarts = require('echarts');
                let now = new Date();
                let year = now.getFullYear();
                let month = now.getMonth()+1;

                let date = new Date(DateUtils.monthFirstDay(year, month, -11)).getTime();
                let end = new Date(DateUtils.monthLastDay(year, month, 0)).getTime();
                let dayTime = 3600 * 24 * 1000;
                let data = [];
                for (let time = date; time <= end; time += dayTime) {
                    let v = Math.floor(Math.random() * 1000);
                    if(v < 800){
                        v = 0;
                    }
                    else if(v < 870){
                        v = 1;
                    }
                    else if(v < 930){
                        v = 2;
                    }
                    else if(v < 980){
                        v = 3;
                    }
                    else{
                        v = 4;
                    }

                    if(time>=now.getTime()){
                        v = 0;
                    }
                    data.push([
                        echarts.format.formatTime('yyyy-MM-dd', time),
                        v
                    ]);
                }
                return data;
            },
            getVirtualLineData(){
                let data = {
                    average: 0.6,
                    items: [
                        {date: "2018-05-13", userPoint: 0.7},
                        {date: "2018-05-14", userPoint: 0.59},
                        {date: "2018-05-16", userPoint: 0.4},
                        {date: "2018-05-17", userPoint: 0.62},
                        {date: "2018-05-18", userPoint: 0.55},
                        {date: "2018-05-20", userPoint: 0.65},
                        {date: "2018-05-23", userPoint: 0.72},
                        {date: "2018-05-25", userPoint: 0.70},
                        {date: "2018-05-26", userPoint: 0.73},
                        {date: "2018-05-27", userPoint: 0.8},
                        {date: "2018-05-28", userPoint: 0.75},
                        {date: "2018-05-31", userPoint: 0.83},
                        {date: "2018-06-01", userPoint: 0.83},
                        {date: "2018-06-04", userPoint: 0.83},
                        {date: "2018-06-07", userPoint: 0.81},
                        {date: "2018-06-09", userPoint: 0.85},
                        {date: "2018-06-10", userPoint: 0.84}
                    ]
                };
                return data;
            }
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
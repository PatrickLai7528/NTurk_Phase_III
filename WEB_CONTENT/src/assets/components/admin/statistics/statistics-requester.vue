<template>
    <el-container class="statistics-requester">
        <el-main>
            <el-tabs style="width: 100%; overflow-y: hidden" class="admin-tabs">
                <el-tab-pane label="发起者增长">
                    <div id="requesterGrowth" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
                <el-tab-pane label="发起者活跃度">
                    <div id="requesterActive" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
            </el-tabs>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        mounted: function () {
            this.$nextTick(()=> {
                this.setRequesterGrowth();
                this.setRequesterActive();
            })
        },
        methods: {
            setRequesterGrowth: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('requesterGrowth'));


                let route = 'http://localhost:8086/admin/requester/requesterGrowth';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    // let data = response.data;
                    let data = [
                        {date: "2018-06-01", value: 30},
                        {date: "2018-06-02", value: 38},
                        {date: "2018-06-03", value: 40},
                        {date: "2018-06-04", value: 34},
                        {date: "2018-06-05", value: 52},
                        {date: "2018-06-06", value: 43},
                        {date: "2018-06-07", value: 38},
                        {date: "2018-06-08", value: 36},
                        {date: "2018-06-09", value: 40},
                        {date: "2018-06-10", value: 39},
                        {date: "2018-06-11", value: 42},
                        {date: "2018-06-12", value: 44},
                        {date: "2018-06-13", value: 45},
                        {date: "2018-06-14", value: 37},
                        {date: "2018-06-15", value: 35},
                        {date: "2018-06-16", value: 46},
                        {date: "2018-06-17", value: 39},
                    ];

                    let dateList = data.map((item)=> {
                        return item.date;
                    });
                    let valueList = data.map((item)=> {
                        return item.value;
                    });

                    let option = {
                        textStyle: {
                            fontSize: 20
                        },
                        visualMap: [{
                            show: false,
                            type: 'continuous',
                            seriesIndex: 0,
                        }],
                        title: [{
                            left: 'center',
                            text: '发起者注册数据',
                            textStyle: {
                                fontSize: 20
                            }
                        }],
                        tooltip: {
                            trigger: 'axis',
                            formatter: "{b} <br/>注册人数: {c}人",
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        xAxis: [{
                            data: dateList,
                            axisLabel: {
                                textStyle: {
                                    fontSize: 16
                                }
                            },
                        }],
                        yAxis: [{
                            splitLine: {show: true},
                            axisLabel: {
                                textStyle: {
                                    fontSize: 16
                                }
                            },
                        }],
                        grid: [{
                            bottom: '15%'
                        }],
                        series: [{
                            type: 'line',
                            showSymbol: false,
                            center: ['40%', '50%'],
                            smooth: true,
                            data: valueList,
                            lineStyle: {
                                width: 5
                            }
                        }]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }).catch(function (error) {
                    console.log(error);
                });
            },
            setRequesterActive: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('requesterActive'));

                let route = 'http://localhost:8086/admin/requester/requesterActive';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    // let data = response.data;
                    let data = [
                        {name: 0, value: 1278},
                        {name: 1, value: 2268},
                        {name: 2, value: 2587},
                        {name: 3, value: 2392}
                    ];
                    for(let i=0;i<data.length;i++){
                        data[i].name = '发布'+data[i].name+'种任务';
                    }
                    let taskList = data.map((item)=> {
                        return item.name;
                    });
                    let numberList = data.map((item)=> {
                        return item.value;
                    });

                    let option = {
                        textStyle: {
                            fontSize: 20
                        },
                        title: {
                            text: '发起者活跃度',
                            subtext: '柱状图展示',
                            x:'center',
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: "{b}: {c}人",
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        xAxis: {
                            type: 'category',
                            data: taskList,
                            axisLabel: {
                                textStyle: {
                                    fontSize: 16
                                }
                            },
                        },
                        yAxis: {
                            type: 'value',
                            axisLabel: {
                                textStyle: {
                                    fontSize: 16
                                }
                            },
                        },
                        series: [{
                            data: numberList,
                            type: 'bar',
                        }]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }).catch(function (error) {
                    console.log(error);
                });
            }
        }
    }
</script>

<style scoped>
    .el-main {
        padding: 0;
    }

    .statistics-requester {
        font-family: Microsoft YaHei;
        text-align: left;
        font-size: 20px;
    }
</style>
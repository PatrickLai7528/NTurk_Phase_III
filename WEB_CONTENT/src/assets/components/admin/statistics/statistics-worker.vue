<template>
    <el-container class="statistics-worker">
        <el-main>
            <el-tabs style="width: 100%; overflow-y: hidden" class="admin-tabs">
                <el-tab-pane label="工人增长">
                    <div id="workerGrowth" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
                <el-tab-pane label="工人活跃度">
                    <div id="workerActive" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
            </el-tabs>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        mounted: function () {
            this.$nextTick(()=> {
                this.setWorkerGrowth();
                this.setWorkerActive();
            })
        },
        methods: {
            setWorkerGrowth: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('workerGrowth'));

                let route = 'http://localhost:8086/admin/worker/workerGrowth';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    let data = response.data;
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
                            text: '工人注册数据',
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
            setWorkerActive: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('workerActive'));

                let route = 'http://localhost:8086/admin/worker/workerActive';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    let data = response.data;
                    for(let i=0;i<data.length;i++){
                        data[i].name = '参与'+data[i].name+'种任务';
                    }
                    let taskList = data.map((item)=> {
                        return item.name;
                    });
                    let numberList = data.map((item)=> {
                        return item.value;
                    });;

                    let option = {
                        textStyle: {
                            fontSize: 20
                        },
                        title: {
                            text: '工人活跃度',
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

    .statistics-worker {
        font-family: Microsoft YaHei;
        text-align: left;
        font-size: 20px;
    }
</style>
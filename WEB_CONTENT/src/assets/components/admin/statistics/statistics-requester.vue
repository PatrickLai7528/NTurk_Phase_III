<template>
    <el-container class="statistics-requester">
        <el-main>
            <el-tabs style="width: 100%; overflow-y: hidden" class="admin-tabs">
                <el-tab-pane label="发起者增长">
                    <div id="requesterGrowth" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
                <el-tab-pane label="发起者分布-饼状图">
                    <div id="requesterDisPie" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
                <el-tab-pane label="发起者分布-地图">
                    <div id="requesterDisMap" style="height: 600px; width: 900px"></div>
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
                this.setRequesterDisPie();
                this.setRequesterDisMap();
                this.setRequesterActive();
            })
        },
        methods: {
            setRequesterGrowth: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('requesterGrowth'));


                let route = 'http://localhost:8086/admin/requester/requesterGrowth';
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
            setRequesterDisPie: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('requesterDisPie'));

                let route = 'http://localhost:8086/admin/requester/requesterDis';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    let data = response.data;
                    let provinceList = data.map((item)=> {
                        return item.name;
                    });;

                    let option = {
                        textStyle: {
                            fontSize: 20
                        },
                        title: {
                            text: '发起者数量统计',
                            subtext: '饼图展示',
                            x:'center',
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: "{a} <br/>{b}: {c}人 ({d}%)",
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        legend: {
                            orient: 'vertical',
                            left: 'left',
                            data: provinceList
                        },
                        series: [
                            {
                                name: '发起者数量',
                                type: 'pie',
                                radius: '70%',
                                center: ['60%', '50%'],
                                data: data
                            }
                        ],
                        color: [
                            '#32C5E9','#9FE6B8',
                            '#FFDB5C','#FF9F7F',
                            '#FB7293','#E7BCF3',
                            '#8378EA','#37A2DA'
                        ]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }).catch(function (error) {
                    console.log(error);
                });
            },
            setRequesterDisMap: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('requesterDisMap'));
                require('echarts/map/js/china.js');

                let route = 'http://localhost:8086/admin/requester/requesterDis';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    let data = response.data;

                    let option = {
                        textStyle: {
                            fontSize: 20
                        },
                        title: {
                            text: '发起者数量统计',
                            subtext: '地图展示',
                            x:'center',
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: function(data){
                                if( !isNaN(data.value) ){
                                    return '发起者数量'+'<br/>'+data.name+': '+data.value+'人';
                                }
                            },
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        visualMap: {
                            min: 0,
                            max: 1000,
                            left: 'left',
                            top: 'bottom',
                            text: ['High','Low'],
                            seriesIndex: [1],
                            inRange: {
                                color: ['#e0ffff', '#73cefc', '#2ea8ff', '#006edd']
                            },
                            calculable: true
                        },
                        geo: {
                            map: 'china',
                            roam: true,
                            label: {
                                normal: {
                                    show: true,
                                    textStyle: {
                                        color: 'rgba(0,0,0,0.4)'
                                    }
                                }
                            },
                            itemStyle: {
                                normal:{
                                    borderColor: 'rgba(0, 0, 0, 0.2)'
                                },
                                emphasis:{
                                    areaColor: null,
                                    shadowOffsetX: 0,
                                    shadowOffsetY: 0,
                                    shadowBlur: 20,
                                    borderWidth: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        },
                        series: [
                            {
                                type: 'scatter',
                                coordinateSystem: 'geo',
                            },
                            {
                                name: '发起者数量',
                                type: 'map',
                                geoIndex: 0,
                                // tooltip: {show: false},
                                label: {
                                    normal: {
                                        formatter: '{b}',
                                        position: 'right',
                                        show: false
                                    },
                                    emphasis: {
                                        show: true
                                    }
                                },
                                data: data
                            }
                        ]
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
                    let data = response.data;
                    for(let i=0;i<data.length;i++){
                        data[i].name = '发布'+data[i].name+'种任务';
                    }
                    let taskList = data.map((item)=> {
                        return item.name;
                    });;
                    let numberList = data.map((item)=> {
                        return item.value;
                    });;

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
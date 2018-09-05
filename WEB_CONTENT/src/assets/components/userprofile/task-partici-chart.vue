<template>
    <el-container class="user-charts">
        <el-main>
            <el-tabs type="border-card" class="tab">
                <el-tab-pane label="每日标注数量">
                    <div id="draw" class="charts"></div>
                </el-tab-pane>
                <el-tab-pane label="每日评审数量">
                    <div id="quality" class="charts"></div>
                </el-tab-pane>
                <el-tab-pane label="每日审核数量" v-if="notGeneral">
                    <div id="coverage" class="charts"></div>
                </el-tab-pane>
            </el-tabs>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        props: ["taskId","taskName","taskCategory"],
        data() {
            return{
                notGeneral: false
            }
        },
        mounted: function () {
            this.notGeneral = (this.taskCategory !== "GENERAL");
            this.$nextTick(()=> {
                this.$http({
                    url: "http://localhost:8086/userProfile/requester/charts/"+this.taskId,
                    method: "GET",
                    headers: {Authorization: this.$store.getters.getToken},
                }).then((response)=>{
                    // let data = response.data;
                    let data = this.getVirtualData();
                    let drawMax = 0;
                    let qualityMax = 0;
                    let coverageMax = 0;

                    let dateList = data.map((item)=> {
                        return item.date;
                    });
                    let drawList = data.map((item)=> {
                        drawMax = item.draw>drawMax ? item.draw : drawMax;
                        return item.draw;
                    });
                    let qualityList = data.map((item)=> {
                        qualityMax = item.quality>qualityMax ? item.quality : qualityMax;
                        return item.quality;
                    });
                    let coverageList = data.map((item)=> {
                        coverageMax = item.coverage>coverageMax ? item.coverage : coverageMax;
                        return item.coverage;
                    });

                    this.setWaveGraph("draw", dateList, drawList, drawMax, 'rgb(135, 224, 166)', "每日标注数量");
                    this.setWaveGraph("quality", dateList, qualityList, qualityMax, 'rgb(245, 105, 57)', "每日评审数量");
                    if(this.notGeneral){
                        this.setWaveGraph("coverage", dateList, coverageList, coverageMax, 'rgb(198, 38, 47)', "每日审核数量");
                    }
                }).catch((error)=> {
                    console.log(error);
                });
            })
        },
        methods: {
            setWaveGraph(elementID, dateList, valueList, max, color, message) {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById(elementID));

                let option = {
                    textStyle: {
                        fontSize: 20
                    },
                    title: [{
                        left: 'center',
                        text: "任务："+this.taskName,
                        textStyle: {
                            fontSize: 20
                        }
                    }],
                    tooltip: {
                        trigger: 'axis',
                        formatter: "{b} <br/>"+message+": {c}",
                        textStyle: {
                            fontSize: 20
                        }
                    },
                    xAxis: [{
                        type: 'category',
                        boundaryGap: false,
                        data: dateList,
                        axisLabel: {
                            textStyle: {
                                fontSize: 16
                            }
                        }
                    }],
                    yAxis: [{
                        type: 'value',
                        splitLine: {show: false},
                        axisLabel: {
                            textStyle: {
                                fontSize: 16
                            }
                        },
                        max: 50
                    }],
                    grid: [{
                        bottom: '10%'
                    }],
                    series: [{
                        type: 'line',
                        showSymbol: false,
                        data: valueList,
                        lineStyle: {
                            width: 0
                        },
                        symbol:'none',  //这句就是去掉点的
                        smooth: true,  //这句就是让曲线变平滑的
                        areaStyle: {
                            color: {
                                type: 'linear',
                                x: 0,
                                y: 0,
                                x2: 0,
                                y2: 1,
                                colorStops: [{
                                    offset: 0, color: color // 0% 处的颜色
                                }, {
                                    offset: 1, color: color// 100% 处的颜色
                                }],
                                globalCoord: false // 缺省为 false
                            }
                        }
                    }]
                };
                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);
            },
            getVirtualData(){
                let data = [
                    {date: "2018/05/25", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/05/26", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/05/27", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/05/28", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/05/29", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/05/30", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/06/01", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/06/02", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/06/03", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/06/04", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/06/05", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/06/06", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/06/07", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 1, coverage: Math.floor(Math.random() * 30) + 1},
                    {date: "2018/06/08", draw: Math.floor(Math.random() * 40) + 1, quality: Math.floor(Math.random() * 30) + 2, coverage: Math.floor(Math.random() * 30) + 1},
                ];
                return data;
            }
        }
    }
</script>

<style scoped>
    .user-charts {
        font-family: Microsoft YaHei;
        text-align: left;
        font-size: 20px;
    }

    .charts {
        margin: 0;
        overflow: hidden;
        width: 900px;
        height: 450px;
    }
</style>
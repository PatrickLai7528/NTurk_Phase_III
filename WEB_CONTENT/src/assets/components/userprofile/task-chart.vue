<template>
    <el-container class="user-charts">
        <el-main>
            <div id="taskInfo" class="charts"></div>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        name: "task-state",
        mounted: function () {
            this.isWorker = UserUtils.isWorker(this);
            this.$nextTick(()=> {
                if(this.isWorker){
                    this.setWaveGraph('general', '整体标注参与数', null, 'rgb(135, 224, 166)');
                    this.setWaveGraph('frame', '画框标注参与数', null, 'rgb(245, 105, 57)');
                    this.setWaveGraph('segment', '区域标注参与数', null, 'rgb(198, 38, 47)');
                } else {
                    this.setWaveGraph('general', '整体标注发布数', null, 'rgb(135, 224, 166)');
                    this.setWaveGraph('frame', '画框标注发布数', null, 'rgb(245, 105, 57)');
                    this.setWaveGraph('segment', '区域标注发布数', null, 'rgb(198, 38, 47)');
                }
            })
        },
        methods: {
            setWaveGraph(elementID, title, route, color) {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById(elementID));

                let data = [
                    {date: "2018-05-13", ungoing: 10, finished: 3, judge: 7},
                    {date: "2018-05-14", ungoing: 9, finished: 5, judge: 6},
                    {date: "2018-05-16", ungoing: 8, finished: 6, judge: 6},
                    {date: "2018-05-17", ungoing: 7, finished: 7, judge: 6},
                    {date: "2018-05-20", ungoing: 6, finished: 9, judge: 5},
                    {date: "2018-05-23", ungoing: 5, finished: 11, judge: 4},
                    {date: "2018-05-25", ungoing: 3, finished: 14, judge: 3},
                    {date: "2018-05-26", ungoing: 2, finished: 17, judge: 1},
                    {date: "2018-05-30", ungoing: 0, finished: 20, judge: 0},

                ];
                let dateList = data.map((item)=> {
                    return item.date;
                });

                let ungoingList = data.map((item)=> {
                    return item.ungoing;
                });

                let finishedList = data.map((item)=> {
                    return item.finished;
                });

                let judgeList = data.map((item)=> {
                    return item.judge;
                });

                // 指定图表的配置项和数据
                let option = {

                    textStyle: {
                        fontSize: 20
                    },
                    title: [{
                        left: '20%',
                        text: "一个标题",
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
                        right: 0,
                        data: ['待标注图片数', '已标注图片数', '待评审图片数']
                    },
                    xAxis: {
                        data: dateList
                    },
                    yAxis: {
                        width: 5
                    },
                    series: [
                        {
                            name: '待标注图片数',
                            type: 'line',
                            lineStyle: {
                                width: 3
                            },
                            symbol: 'none',  //这句就是去掉点的
                            smooth: true,  //这句就是让曲线变平滑的
                            data: ungoingList
                        },
                        {
                            name: '已标注图片数',
                            type: 'line',
                            lineStyle: {
                                width: 3
                            },
                            symbol: 'none',  //这句就是去掉点的
                            smooth: true,  //这句就是让曲线变平滑的
                            data: finishedList
                        },
                        {
                            name: '待评审图片数',
                            type: 'line',
                            lineStyle: {
                                width: 3
                            },
                            symbol: 'none',  //这句就是去掉点的
                            smooth: true,  //这句就是让曲线变平滑的
                            data: judgeList
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

    .user-charts {
        font-family: Microsoft YaHei;
        text-align: left;
        font-size: 20px;
    }

    .charts {
        margin: 0;
        overflow: hidden;
        width: 9000px;
        height: 500px;
    }
</style>
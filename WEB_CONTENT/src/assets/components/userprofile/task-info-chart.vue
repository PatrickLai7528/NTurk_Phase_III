<template>
    <el-container class="user-charts">
        <el-main>
            <div id="taskInfo" class="charts"></div>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        props: ["taskId","taskName"],
        data() {
            return {

            }
        },
        mounted: function () {
            this.$nextTick(()=> {
                this.setLineChart();
            })
        },
        methods: {
            setLineChart() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('taskInfo'));

                let data = [
                    {date: "2018/05/13", ongoing: 10, finished: 3, underReview: 7},
                    {date: "2018/05/14", ongoing: 9, finished: 5, underReview: 6},
                    {date: "2018/05/16", ongoing: 8, finished: 6, underReview: 6},
                    {date: "2018/05/17", ongoing: 7, finished: 7, underReview: 6},
                    {date: "2018/05/20", ongoing: 6, finished: 9, underReview: 5},
                    {date: "2018/05/23", ongoing: 5, finished: 11, underReview: 4},
                    {date: "2018/05/25", ongoing: 3, finished: 14, underReview: 3},
                    {date: "2018/05/26", ongoing: 2, finished: 17, underReview: 1},
                    {date: "2018/05/30", ongoing: 0, finished: 20, underReview: 0},

                ];
                let dateList = data.map((item)=> {
                    return item.date;
                });

                let ongoingList = data.map((item)=> {
                    return item.ongoing;
                });

                let finishedList = data.map((item)=> {
                    return item.finished;
                });

                let underReviewList = data.map((item)=> {
                    return item.underReview;
                });

                // 指定图表的配置项和数据
                let option = {

                    textStyle: {
                        fontSize: 20
                    },
                    title: [{
                        left: '10%',
                        text: "任务："+this.taskName,
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
                            smooth: true,  //这句就是让曲线变平滑的
                            data: ongoingList
                        },
                        {
                            name: '已标注图片数',
                            type: 'line',
                            lineStyle: {
                                width: 3
                            },
                            smooth: true,  //这句就是让曲线变平滑的
                            data: finishedList
                        },
                        {
                            name: '待评审图片数',
                            type: 'line',
                            lineStyle: {
                                width: 3
                            },
                            smooth: true,  //这句就是让曲线变平滑的
                            data: underReviewList
                        }
                    ]
                };
                
                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);
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
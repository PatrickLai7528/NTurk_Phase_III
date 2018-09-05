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

                this.$http({
                    url: "http://localhost:8086/requesterTasks/taskChart/"+this.taskId,
                    method: "GET",
                    headers: {Authorization: this.$store.getters.getToken}
                }).then((response)=>{
                    // let data = response.data;
                    let data = this.getVirtualData();
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
                }).catch((error)=> {
                    console.log(error);
                });
            },
            getVirtualData(){
                let data = [
                    {date: "2018/05/25", ongoing: 30, finished: 0, underReview: 0},
                    {date: "2018/05/26", ongoing: 26, finished: 3, underReview: 1},
                    {date: "2018/05/27", ongoing: 24, finished: 4, underReview: 2},
                    {date: "2018/05/28", ongoing: 23, finished: 5, underReview: 2},
                    {date: "2018/05/29", ongoing: 19, finished: 6, underReview: 5},
                    {date: "2018/05/30", ongoing: 16, finished: 7, underReview: 7},
                    {date: "2018/06/01", ongoing: 14, finished: 7, underReview: 9},
                    {date: "2018/06/02", ongoing: 13, finished: 10, underReview: 7},
                    {date: "2018/06/03", ongoing: 10, finished: 15, underReview: 5},
                    {date: "2018/06/04", ongoing: 8, finished: 16, underReview: 6},
                    {date: "2018/06/05", ongoing: 5, finished: 20, underReview: 5},
                    {date: "2018/06/06", ongoing: 3, finished: 24, underReview: 3},
                    {date: "2018/06/07", ongoing: 1, finished: 27, underReview: 2},
                    {date: "2018/06/08", ongoing: 0, finished: 30, underReview: 0},
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
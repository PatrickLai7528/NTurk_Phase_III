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
                let root = "http://localhost:8086/userProfile/requester/charts/";
                this.setWaveGraph("draw", root+"general/", 'rgb(135, 224, 166)', "每日标注数量");
                this.setWaveGraph("quality", root+"general/", 'rgb(245, 105, 57)', "每日评审数量");
                if(this.notGeneral){
                    this.setWaveGraph("coverage", null, 'rgb(198, 38, 47)', "每日审核数量");
                }
            })
        },
        methods: {
            setWaveGraph(elementID, route, color, message) {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById(elementID));
                let max = 0;

                let data = [
                    {date: "2018-05-13", value: 2},
                    {date: "2018-05-14", value: 4},
                    {date: "2018-05-16", value: 6},
                    {date: "2018-05-17", value: 4},
                    {date: "2018-05-18", value: 8},
                    {date: "2018-05-20", value: 7},
                    {date: "2018-05-23", value: 5},
                    {date: "2018-05-25", value: 9},
                    {date: "2018-05-28", value: 6},
                    {date: "2018-05-31", value: 10},
                    {date: "2018-06-01", value: 7},
                    {date: "2018-06-04", value: 5},
                    {date: "2018-06-07", value: 8},
                    {date: "2018-06-09", value: 9},
                    {date: "2018-06-10", value: 3},
                ];
                let dateList = data.map((item)=> {
                    // let date = item.date.split("/");
                    // let year = parseInt(date[0]);
                    // let month = parseInt(date[1]);
                    // let day = parseInt(date[2]);
                    // alert(new Date(year, month, day));
                    // return new Date(year, month, day);
                    return item.date;
                });
                let valueList = data.map((item)=> {
                    if(max<item.value){
                        max = item.value;
                    }
                    return item.value;
                });

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
                        max: max*1.5
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
                        smoothMonotone: 'none',
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

                //let route = 'http://localhost:8086/admin/requester/requesterGrowth';
                // this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                //
                // }).catch(function (error) {
                //     console.log(error);
                // });
            },
            addDateAndValue(dateList, valueList){
                let interval = 3600 * 24 * 1000;
                let firstDay = new Date(dateList[0]).getTime();
                let theDayBefore = firstDay - interval;

                let saveDate = dateList;

                for(let i=0;i<saveDate.length;i++){
                    let index = i;
                    let stamp_i = new Date(saveDate[i]).getTime();
                    if(stamp_i-theDayBefore>interval){
                        let currentDay = theDayBefore + interval;
                        while(currentDay < stamp_i){
                            dateList.splice(index, 0, this.getLocalTime(currentDay));
                            valueList.splice(index, 0, 0);
                            theDayBefore = currentDay;
                            currentDay = currentDay + interval;
                            index++;
                        }
                    }
                    theDayBefore = stamp_i;
                }
            },
            //时间戳转时间字符串
            getLocalTime(timeStamp) {
                let date = new Date(timeStamp);//毫秒
                let year = date.getFullYear();
                let month = date.getMonth() + 1;
                month = month > 9 ? month : ("0" + month);
                let day = date.getDate();
                day = day > 9 ? day : ("0" + day);
                return year + "-" + month + "-" + day;
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
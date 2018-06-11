<template>
    <el-container class="user-charts">
        <el-main>
            <el-tabs type="border-card" class="tab">
                <el-tab-pane label="整体标注" style="width: 100%">
                    <div id="general" class="charts"></div>
                </el-tab-pane>
                <el-tab-pane label="画框标注" style="width: 100%">
                    <div id="frame" class="charts"></div>
                </el-tab-pane>
                <el-tab-pane label="区域标注" style="width: 100%">
                    <div id="segment" class="charts"></div>
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
                let max = 0;

                let data = [
                    {date: "2018/05/13", value: 2},
                    {date: "2018/05/14", value: 4},
                    {date: "2018/05/16", value: 6},
                    {date: "2018/05/17", value: 4},
                    {date: "2018/05/18", value: 8},
                    {date: "2018/05/20", value: 7},
                    {date: "2018/05/23", value: 5},
                    {date: "2018/05/25", value: 9},
                    {date: "2018/05/28", value: 6},
                    {date: "2018/05/31", value: 10},
                    {date: "2018/06/01", value: 7},
                    {date: "2018/06/04", value: 5},
                    {date: "2018/06/07", value: 8},
                    {date: "2018/06/09", value: 9},
                    {date: "2018/06/10", value: 3},
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

                this.addDateAndValue(dateList, valueList);

                let option = {
                    textStyle: {
                        fontSize: 20
                    },
                    title: [{
                        left: 'center',
                        text: title,
                        textStyle: {
                            fontSize: 20
                        }
                    }],
                    tooltip: {
                        trigger: 'axis',
                        formatter: "{b} <br/>数量: {c}",
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
                        bottom: '15%'
                    }],
                    series: [{
                        type: 'line',
                        showSymbol: false,
                        center: ['40%', '50%'],
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
                                    offset: 1, color: color // 100% 处的颜色
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
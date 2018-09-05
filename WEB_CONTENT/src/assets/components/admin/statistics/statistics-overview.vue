<template>
    <el-container class="statistics-overview">
        <el-main>
            <el-tabs style="width: 100%; overflow-y: hidden" class="admin-tabs">
                <el-tab-pane label="用户数量">
                    <div id="userNumGraph" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
                <el-tab-pane label="任务数量">
                    <div id="taskNumGraph" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
            </el-tabs>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        mounted: function () {
            this.$nextTick(()=> {
                this.setUserNumGraph();
                this.setTaskNumGraph();
            })
        },
        methods: {
            setUserNumGraph: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('userNumGraph'));

                let route = 'http://localhost:8086/admin/overview/userNum';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    // let data = this.translate(response.data);
                    let data = [
                        {name: '工人', value: 5736},
                        {name: '发起者', value: 4389},
                    ];
                    let userList = data.map((item)=> {
                        return item.name;
                    });

                    let option = {
                        textStyle: {
                            fontSize: 20
                        },
                        title: {
                            text: '用户数量统计',
                            subtext: '发起者/工人',
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
                            data: userList,
                        },
                        series: [
                            {
                                name: '用户数量统计',
                                type: 'pie',
                                radius: '70%',
                                center: ['40%', '50%'],
                                data: data
                            }
                        ],
                        color: [
                            '#32C5E9', '#FFDB5C',
                            '#9FE6B8', '#FB7293',
                            '#37A2DA', '#8378EA',
                            '#FF9F7F', '#FB7293',
                            '#E7BCF3',
                        ]
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }).catch(function (error) {
                    console.log(error);
                });
            },
            setTaskNumGraph: function() {
                let echarts = require('echarts');
                let myChart = echarts.init(document.getElementById('taskNumGraph'));

                let route = 'http://localhost:8086/admin/overview/taskNum';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    // let data = this.translate(response.data);
                    let data = [
                        {name: '整体标注', value: 432},
                        {name: '画框标注', value: 378},
                        {name: '区域标注', value: 407},
                    ];
                    let taskList = data.map((item)=> {
                        return item.name;
                    });

                    let option = {
                        textStyle: {
                            fontSize: 20
                        },
                        title: {
                            text: '任务数量统计',
                            subtext: '整体标注/画框标注/区域标注',
                            x:'center',
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: "{a} <br/>{b}: {c}件 ({d}%)",
                            textStyle: {
                                fontSize: 20
                            }
                        },
                        legend: {
                            orient: 'vertical',
                            left: 'left',
                            data: taskList
                        },
                        series: [
                            {
                                name: '任务数量统计',
                                type: 'pie',
                                radius: '70%',
                                center: ['40%', '50%'],
                                data: data
                            }
                        ],
                        color: [
                            '#9FE6B8', '#FB7293',
                            '#37A2DA', '#FFDB5C',
                            '#FF9F7F', '#FB7293',
                            '#E7BCF3', '#8378EA',
                            '#32C5E9',
                        ]
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }).catch(function (error) {
                    console.log(error);
                });
            },
            translate: function (data) {
                for (let i = 0; i < data.length; i++) {
                    if (data[i].name === "WORKER") {
                        data[i].name = "工人";
                    } else if (data[i].name === "REQUESTER") {
                        data[i].name = "发起者";
                    }
                }
                for (let j = 0; j < data.length; j++) {
                    if (data[j].name === "GENERAL") {
                        data[j].name = "整体标注";
                    } else if (data[j].name === "FRAME") {
                        data[j].name = "画框标注";
                    } else if (data[j].name === "SEGMENT") {
                        data[j].name = "区域标注";
                    }
                }
                return data;
            }
        }
    }
</script>

<style scoped>
    .el-main {
        padding: 0;
    }

    .statistics-overview {
        font-family: Microsoft YaHei;
        text-align: left;
        font-size: 20px;
    }
</style>
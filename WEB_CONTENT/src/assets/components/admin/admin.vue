<template>
    <el-row class="admin">
        <el-col :span="24">
            <el-card>
                <div slot="header" class="title">
                    <span>管理員界面</span>
                </div>
                <el-tabs tab-position="left" type="border-card" class="admin-navigation" @tab-click="loadGraph">
                    <el-tab-pane label="概述">
                        <statistics-overview v-if="tagNumber===0"></statistics-overview>
                    </el-tab-pane>
                    <el-tab-pane label="工人统计">
                        <statistics-worker v-if="tagNumber===1"></statistics-worker>
                    </el-tab-pane>
                    <el-tab-pane label="发起者统计">
                        <statistics-requester v-if="tagNumber===2"></statistics-requester>
                    </el-tab-pane>
                    <el-tab-pane label="任务统计">
                        <statistics-task v-if="tagNumber===3"></statistics-task>
                    </el-tab-pane>
                </el-tabs>
            </el-card>
        </el-col>
    </el-row>
</template>

<script>
    import statisticsOverview from './statistics/statistics-overview.vue'
    import statisticsWorker from './statistics/statistics-worker.vue'
    import statisticsRequester from './statistics/statistics-requester.vue'
    import statisticsTask from './statistics/statistics-task.vue'

    export default {
        components: {
            statisticsOverview,
            statisticsWorker,
            statisticsRequester,
            statisticsTask
        },
        data() {
            return {
                tagNumber: 0
            }
        },
        methods: {
            loadGraph(tab)  {
                if(tab.label==="概述"){
                    this.tagNumber = 0;
                }
                else if(tab.label==="工人统计"){
                    this.tagNumber = 1;
                }
                else if(tab.label==="发起者统计"){
                    this.tagNumber = 2;
                }
                else if(tab.label==="任务统计"){
                    this.tagNumber = 3;
                }
            }
        }
    }
</script>

<style>
    .title {
        font-size: 1.5em;
        background: rgba(202, 235, 242, 0.5);
        font-weight: lighter;
    }

    .admin {
        margin-top: 3em;
        margin-left: 3em;
        margin-right: 3em;
        font-family: Microsoft YaHei;
    }

    .admin-navigation {
        height: 700px;
        font-size: 20px;
    }

    .admin .el-tabs__item {
        margin: 10px 0px 10px 0px;
        height: 60px;
        line-height: 60px;
        box-sizing: border-box;
        display: inline-block;
        list-style: none;
        font-size: 20px;
        font-weight: 500;
        color: #303133;
        position: relative;
    }

    .admin-tabs .el-tabs__item {
        margin: 0px 0px 10px 0px;
        height: 40px;
        line-height: 40px;
    }
</style>
<template>
    <div class="main">
        <el-table :data="taskData"
                  class="table" :row-class-name="tableRowClassName" align="center">
            <el-table-column label="任务ID" prop="taskId" sortable>
            </el-table-column>
            <el-table-column label="任务名称" prop="taskName">
            </el-table-column>
            <el-table-column label="参与人数" prop="attendance">
            </el-table-column>
            <el-table-column label="任务状态" prop="taskStatus"
                             :filters="[{text:'标注阶段',value:'标注阶段'},{text:'评审阶段',value:'评审阶段'},{text:'已完成',value:'已完成'}]"
                             :filter-method="filterStatus">
                <template slot-scope="scope">
                    <el-tag
                            :type="scope.row.taskStatus === '标注阶段' ? 'primary' : (scope.row.taskStatus === '已完成') ? 'success':'waring'"
                            close-transition>{{scope.row.taskStatus}}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="截止时间" prop="endTime" sortable width="250">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button @click="handleClick(scope.row)" type="text" size="small" align="left">查看详情</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
    import DateUtils from '../../js/utils/DateUtils.js'
    export default {
        props: ['message'],
        data() {
            return {
                /*
                {        //从后端先得到task的具体内容，然后再得到contract进行二次封装获得tableData
                    taskId:'',
                    taskName:'',
                    endTime:'', //项目的截止日期
                    ddl:''   //标注的截止日期
                    attendance:''   //标注的参与人数
                    taskStatus:''     //标注的状态


                }
                 */
                taskData: [],
                temPath: ''
            }
        },
        mounted: function () {
            let _this = this;
            this.$nextTick(function () {
                _this.setUpBusEvent();
                _this.getAll();
            })
        },

        methods: {
            setUpBusEvent(){
                this.$bus.$on("handleClick",() => {
                    this.handleClick(row);
                })
            },
            handleClick(row){
                //点击任务进入该任务的合同列表
                ///user/${userId}
                this.$router.push({name: 'requesterlobby',params:{taskId:row.taskId}});
            },
            filterStatus(value, row) {    //根据合同状态筛选
                return row.taskStatus === value;
            },
            translateContractStatus: function (status) {        //翻译状态
                if (status === "ONGOING") {
                    return "标注阶段";
                }
                else if (status === "UNDER_REVIEW") {
                    return "评审阶段";
                }
                else if (status === "FINISHED") {
                    return "已完成";
                }
            },
            getTableData() {          //获得tabledata的数据
                let _this = this;
                //  this.$http.get("http://localhost:8086/newTasks", {headers: {Authorization: store.state.token}}).then(function (response)
                _this.$http.get("http://localhost:8086/requesterTasks", {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    _this.taskData = response.data;     //只需要获得taskData的数据就可以了
                    for (let item of _this.taskData){
                        item.taskStatus = _this.translateContractStatus(item.taskStatus);
                        item.endTime = DateUtils.dateFormat(item.endTime);
                    }
                })
            },
            getAll() {
                this.getTableData();
            },
            tableRowClassName({row, rowIndex}) {
                // console.log("here" + rowIndex);
                if (rowIndex % 2 === 1) {
                    return 'odd-row';
                } else {
                    return 'even-row';
                }
            },
        }
    }
</script>

<style>
    .main {
        padding-left: 3em;
        padding-right: 3em;
        padding-top: 5em;
        font-family: Microsoft YaHei;
        /*background-image: url(../../images/mainbg.jpg);*/
        /*background-size: auto 100%;*/
        height: 650px;
        width: 100%;
    }

    .table {
        width: 100%;
        text-align: left;
        font-size: 1em;
    }

    .tableSlot {
        margin-bottom: 0;
    }

    .tableSlotSpan {
        font-weight: lighter;
        color: #FF3B3F;
    }

    .el-table .even-row {
        /*background: #CAFBF2;*/
        /*background: rgba(202, 235, 242, 0.5);*/
        background: #EFEFEF;
    }

    .el-table .odd-row {
        background: #FFFFFF;
        /*background: rgba(169, 169, 169, 0.5);*/
    }

    .submitButton {
        font-family: Microsoft YaHei;
        background: #A9A9A9;
        border-color: #A9A9A9;
        color: #FFFFFF;
        font-size: 13px;
    }

    .cell {
        display: table-cell;
        vertical-align: middle;
    }
</style>
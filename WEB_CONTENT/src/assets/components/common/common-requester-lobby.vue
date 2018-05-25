<template>
    <div class="main">
        <el-table :data="tableData"
                  class="table" :row-class-name="tableRowClassName" align="center">
            <el-table-column label="任务ID" prop="taskId" sortable
                             :filters="[{text:'已截止',value:'已截止'},{text:'正在进行',value:'正在进行'}]"
                             :filter-method="filterTaskStatus">
            </el-table-column>
            <el-table-column label="任务名称" prop="taskName">
            </el-table-column>
            <el-table-column label="工人ID" prop="workerId" sortable>
            </el-table-column>
            <el-table-column label="合同ID" prop="contractId" sortable>
            </el-table-column>
            <el-table-column label="合同状态" prop="contractStatus"
                             :filters="[{text:'正在进行',value:'正在进行'},{text:'已完成',value:'已完成'},{text:'逾期',value:'逾期'}]"
                             :filter-method="filterStatus">
                <template slot-scope="scope">
                    <el-tag
                            :type="scope.row.contractStatus === '正在进行' ? 'primary' : (scope.row.contractStatus === '已完成') ? 'success':'danger'"
                            close-transition>{{scope.row.contractStatus}}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="最后修改时间" prop="lastEditTime" sortable width="250">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button @click="handleClick(scope.row)" type="text" size="small" align="left">查看标注</el-button>
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
            return {             //好的实现：根据taskId是否到期来看到期的或者没有到期的task，根据工人的完成情况来筛选完成的和没有完成的contract，能够进入查看标注的结果
                /*
                {
                    taskId:'',
                    taskName:'',
                    contractId:'',
                    contractStatus:'',
                    workerId:'',
                    lastEditTime:'',
                    taskType:''   新增
                }
                 */
                tableData: [],
                /*
                {        //从后端先得到task的具体内容，然后再得到contract进行二次封装获得tableData
                    taskId:'',
                    taskName:'',
                    endTime:'',
                }
                 */
                taskData: []
            }
        },
        mounted: function () {
            let _this = this;
            this.$nextTick(function () {
                _this.getAll();
            })
        },

        methods: {
            handleClick(row){
                if(row.taskCategory === "GENERAL"){
                    this.$router.push({name: 'viewgeneral',params:{taskId:row.taskId,contractId:row.contractId}});
                }
                else if(row.taskCategory === "SEGMENT"){
                    this.$router.push({name: 'viewsegment',params:{taskId:row.taskId,contractId:row.contractId}});
                }
                else if(row.taskCategory === "FRAME"){
                    this.$router.push({name: 'viewframe',params:{taskId:row.taskId,contractId:row.contractId}});
                }
            },
            filterStatus(value, row) {    //根据合同状态筛选
                return row.contractStatus === value;
            },
            filterTaskStatus(value, row) {
                let flag = true;
                if (value === '已截止') {
                    flag = false;    //如果查找已截止的task的话，flag=false
                }
                for (let e of this.taskData) {
                    if (row.taskId === e.taskId) {
                        if (flag === false) {
                            return new Date() > e.endTime;
                        }
                        else {
                            return DateUtils.dateFormat(new Date()) <= e.endTime;
                        }

                    }
                }
            },
            translateContractStatus: function (status) {        //翻译状态
                if (status === "IN_PROGRESS") {
                    return "正在进行";
                }
                else if (status === "COMPLETED") {
                    return "已完成";
                }
                else if (status === "ABORT") {
                    return "逾期";
                }
            },
            getTableData() {          //获得tabledata的数据
                let _this = this;
                //  this.$http.get("http://localhost:8086/newTasks", {headers: {Authorization: store.state.token}}).then(function (response)
                _this.$http.get("http://localhost:8086/requesterTasks", {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    _this.taskData = response.data;
                    let that = _this;   //进行第二次调用
                    for (let e of that.taskData) {
                        that.$http.get("http://localhost:8086/contracts/taskId/" + e.taskId, {headers: {Authorization: that.$store.getters.getToken}}).then(function (res) {
                            let temp = res.data;

                            let haha = that;

                            function Contract(contractId, contractStatus, workerId, lastEditTime) {
                                this.contractId = contractId;
                                let status = haha.translateContractStatus(contractStatus);
                                this.contractStatus = status;
                                this.workerId = workerId;
                                this.lastEditTime = lastEditTime;
                            }

                            for (let con of temp) {
                                console.log(response.data);
                                let theContract = new Contract(con.contractId, con.contractStatus, con.workerId, DateUtils.dateFormat(con.lastEditTime));
                                theContract.taskId = e.taskId;
                                theContract.taskName = e.taskName;
                                theContract.endTime = e.endTime;    //加入时间来共筛选
                                theContract.taskCategory = e.taskCategory;   //加入任务类型来进行路由判断
                                that.tableData.push(theContract);
                            }
                        })
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
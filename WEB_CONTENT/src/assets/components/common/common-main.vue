<template>
    <div class="main">
        <el-table :data="tableData" center="all"
                  class="table" :row-class-name="tableRowClassName">
            <el-table-column type="expand" style="margin-bottom: 0px">
                <template slot-scope="props">
                    <el-form label-position="left" class="tableSlot">
                        <el-row>
                            <el-col :span="6">
                                <el-form-item label="發起者">
                                    <span class="tableSlotSpan">{{props.row.requesterName}}</span>
                                </el-form-item>
                            </el-col>

                            <el-col :span="6">
                                <el-form-item label="已加入人数">
                                    <span class="tableSlotSpan">{{props.row.attendance}}</span>
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                                <el-form-item label="限制人数">
                                    <span class="tableSlotSpan">{{props.row.capacity}}</span>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="24">
                                <el-form-item label="任务描述">
                                    <span class="tableSlotSpan">{{ props.row.taskDescription }}</span>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>
                </template>
            </el-table-column>
            <el-table-column prop="taskStatus" label="任务类型" sortable :filters="[
                                {text: '评审任务', value: '评审任务'},
                                {text: '标注任务', value: '标注任务'}]"
                             :filter-method="filterStateHandler"></el-table-column>
            <el-table-column prop="taskCategoryChi" label="类别" sortable
                             :filters="[
                                {text: '整體標註', value: 'GENERAL'},
                                {text: '區域標註', value: 'FRAME'},
                                {text: '區域劃分', value: 'SEGMENT'}]"
                             :filter-method="filterCategoryHandler"></el-table-column>
            <el-table-column prop="taskName" label="任务名称"></el-table-column>
            <!--<el-table-column prop="taskDescription" label="任务描述"></el-table-column>-->
            <el-table-column prop="totalReward" label="總積分" sortable></el-table-column>
            <el-table-column prop="formatEndTime" label="截止日期" sortable></el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <!--<router-link v-blind:to="'pagesSelector(taskCategory)'">-->
                    <!--<router-link>-->
                    <el-button plain size="mini" class="submitButton"
                               @click="handleClick(scope.row,scope.row.taskStatus,scope.row.taskId,scope.row.taskCategory)">
                        进入
                    </el-button>
                    <!--</router-link>-->
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
                    taskId: '',
                    taskName: '',
                    requesterId: '',
                    requesterName: '',
                    taskCategory: '',
                    taskCategoryChi: '',
                    requester: '',
                    reward: '',
                    endTime: '',
                    taskStatus: '',
                    totalReward: '',    //任务的总积分奖励
                    formatEndTime: '',   //将读出来的时间进行格式化再显示
                    attendance: 0,      //表示有多少人参加了这个标注任务
                    capacity: 0,          //表示这个标注任务共限制多少人参加
                 */
                tableData: [],
                contractData: '',
                routerDictionary: [],     //进一步优化代码，用一个字典来装router要跳转的名称
            }
        }
        ,
        mounted: function () {
            let _this = this;
            this.$nextTick(function () {
                console.log(_this.message);
                this.routerDictionary = new Array();
                this.routerDictionary['GENERAL'] = 'viewgeneral';
                this.routerDictionary['SEGMENT'] = 'viewsegment';
                this.routerDictionary['FRAME'] = 'viewframe';
                _this.getAll();

            })
        },
        methods: {
            filterCategoryHandler(value, row, column) {      //对任务的类别进行筛选
                return row.taskCategory === value;
            },
            filterStateHandler(value,row,column){
                return row.taskStatus === value;
            },
            doWhileGetTableDataSuccess(response) {        //赖总的编程风格很友好啊，将代码都优化了
                console.log(response.data);
                if(response.data.length != 0){
                    this.tableData = this.tableData.concat(response.data);     //现在是多个response.data  用数组连接
                }
                for (let e of this.tableData) {
                    e.formatEndTime = DateUtils.dateFormat(e.endTime); //将日期进行格式化
                    if (e.capacity === 2147483647) {
                        e.capacity = "无限制";
                    }
                }
                this.translate();
            },
            decideGetTableDataUrl() {//现在的返回值是一个数组
                if (this.message === "user")
                    return ["http://localhost:8086/workerTasks","http://localhost:8086/tasks/workerInspectionTasks"]; //用户中心得到的是以前存在的任务
                else
                    return ["http://localhost:8086/newTasks","http://localhost:8086/tasks/newInspectionTasks"];
            },
            getTableData() {
                let header = {Authorization: this.$store.getters.getToken};
                let getUrl = this.decideGetTableDataUrl();
                for(let url of getUrl){
                    this.$http.get(
                        url,
                        {headers: header}
                    ).then(this.doWhileGetTableDataSuccess).catch(function (error) {
                        console.log(error);
                    });
                }
            },

            translate: function () {
                for (let i = 0; i < this.tableData.length; i++) {
                    if (this.tableData[i].taskCategory === "GENERAL") {
                        this.tableData[i].taskCategoryChi = "整體標註";
                    } else if (this.tableData[i].taskCategory === "FRAME") {
                        this.tableData[i].taskCategoryChi = "區域標註";
                    } else if (this.tableData[i].taskCategory === "SEGMENT") {
                        this.tableData[i].taskCategoryChi = "區域劃分";
                    }

                    //这里有bug
                    if(this.message === 'user'){
                        if(this.tableData[i].mandatoryTime !== undefined){
                            this.tableData[i].taskStatus = '评审任务';
                        }
                        else{
                            this.tableData[i].taskStatus = '标注任务';   //这是原来完成的任务，不是评审任务，虽然taskStatus不一样
                        }
                    }
                    else{
                        if(this.tableData[i].taskStatus === 'ONGOING'){
                            this.tableData[i].taskStatus = '标注任务';
                        }
                        else if(this.tableData[i].taskStatus === 'UNDER_REVIEW'){
                            this.tableData[i].taskStatus = '评审任务';
                        }
                    }
                }
            },
            handleReviewJump(row,taskCategory,taskId){     //处理是review的jump
                let contractId = '';
                let _this = this;
                _this.$http.get('http://localhost:8086/contract/review/' + taskId, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    contractId = response.data.contractId;   //得到contractId
                    console.log(contractId);
                    let mandatory = 0
                    if(row.mandatoryTime === undefined){
                        mandatory = 1;
                    }
                    else{
                        if(row.mandatoryTime !== 0){
                            mandatory = row.mandatoryTime;
                        }
                    }

                    _this.$router.push({name: _this.routerDictionary[taskCategory],params:{taskId:taskId,contractId:contractId,mandatoryTime:mandatory}});
                }).catch(function (error) {
                    //如果在这里出现问题说明将所有标注都评分过了
                    _this.successMessage();
                    console.log(error);
                })
            },
            successMessage(){
                this.$notify({
                    title: '系统提示',
                    message: '您已经完成了这个项目的所有评审任务，换一个任务试试呦^_^',
                    type: 'success'
                });
            },
            handleNewJump(taskId,path){      //处理任务中心的jump标注
                function Contract(taskId, workerId) {
                    this.taskId = taskId;
                    this.workerId = workerId;
                }

                let _this = this;
                let contract = new Contract(taskId, _this.$store.getters.getUserId);
                _this.$http.post('http://localhost:8086/contract', JSON.stringify(contract), {
                    headers: {
                        Authorization: _this.$store.getters.getToken,
                        'Content-Type': 'application/json'
                    }
                }).then(function (response) {
                    _this.$router.push({name: path.toLowerCase(), params: {taskId: taskId}});
                }).catch(function (error) {
                    console.log(error);
                })
            },
            handleUserJump(taskId,path){     //处理个人中心的jump标注
                //在这里判断这个任务是不是已经提交了，如果已经提交，不能跳转到修改界面
                let _this = this;
                _this.$http.get('http://localhost:8086/contract/taskId/' + taskId, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    _this.contractData = response.data;
                    if (_this.contractData.contractStatus === 'COMPLETED') {
                        _this.messageHandler();
                    }
                    else if (_this.contractData.contractStatus === 'ABORT') {
                        _this.badMessage();
                    }
                    else {
                        _this.$router.push({name: path.toLowerCase(), params: {taskId: taskId}});
                    }
                }).catch(
                    function (error) {
                        console.log(error)
                    }
                );
            },
            handleClick(row,taskStatus, Id, taskCategory) {
                let _this = this;
                const h = this.$createElement;

                //优化跳转判断逻辑,分成好几个方法，减少重复代码
                if(taskStatus === '评审任务'){
                    this.handleReviewJump(row,taskCategory,Id);
                }
                else if(taskStatus === '标注任务'){
                    if(_this.message === 'user'){
                        this.handleUserJump(Id,taskCategory);
                    }
                    else{
                        this.handleNewJump(Id,taskCategory);
                    }
                }
                else{
                    console.log("Oops");        //调试使用
                }
            }
            ,
            messageHandler() {
                this.$message({
                    message: '您已经提交了该任务，不能对结果进行修改，请安心等待结果和奖励^_^',
                    type: 'success'
                })
            },
            badMessage() {
                this.$alert('这个任务已经过期了，不能对结果进行修改', '系统警告', {
                    confirmButtonText: '确定'
                });
            },
            getAll() {
                this.getTableData();
            }
            ,
            tableRowClassName({row, rowIndex}) {
                console.log("here" + rowIndex);
                if (rowIndex % 2 === 1) {
                    return 'odd-row';
                } else {
                    return 'even-row';
                }
            }
            ,
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

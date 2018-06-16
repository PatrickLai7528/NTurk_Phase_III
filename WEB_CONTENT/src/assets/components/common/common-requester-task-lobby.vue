<template >
    <div class = "main" >
        <el-table :data = "taskData"
                  class = "table" :row-class-name = "tableRowClassName" align = "center" >
            <el-table-column label = "任务ID" prop = "taskId" sortable >
            </el-table-column >
            <el-table-column label = "任务名称" prop = "taskName" >
            </el-table-column >
            <el-table-column label = "创建时间" prop = "createTime" sortable >
            </el-table-column >
            <el-table-column label = "任务状态" prop = "taskStatus"
                             :filters = "[{text:'正在进行',value:'正在进行'},{text:'已完成',value:'已完成'}]"
                             :filter-method = "filterStatus" >
                <template slot-scope = "scope" >
                    <el-tag
		                    :type = "scope.row.taskStatus === '正在进行' ? 'primary' : (scope.row.taskStatus === '已完成') ? 'success':'waring'"
		                    close-transition >{{scope.row.taskStatus}}
                    </el-tag >
                </template >
            </el-table-column >
            <el-table-column label = "操作" >
                <template slot-scope = "scope" >
                    <!--<el-button @click="handleClick(scope.row)" type="primary" size="small" align="left" round>查看详情</el-button>-->
                    <el-popover placement = "left" width = "300px" trigger = "hover" >
                        <p style = "font-size: 16px; font-weight: bold; text-align: center;"
                           class = "default-font-style" >请选择要查看的任务信息</p >
                        <div style = "text-align: center;" >
                            <el-button type = "success" size = "medium" @click = "showChart(scope.row)"
                                       round class = "default-font-style" > 统计信息 </el-button >
                            <el-button type = "error" size = "medium" @click = "handleClick(scope.row)"
                                       round class = "default-font-style" > 标注结果 </el-button >
                        </div >

                        <el-button slot = "reference" type = "primary" size = "medium" align = "left"
                                   round class = "default-font-style" > 查 看 </el-button >
                    </el-popover >
                </template >
            </el-table-column >
        </el-table >

        <el-dialog :visible.sync = "dialogFormVisible" :modal-append-to-body = "false" width = "1000px" >
            <div v-if = "dialogFormVisible" >
                <task-info-chart :taskId = "taskIdOfChart" :taskName = "taskNameOfChart" ></task-info-chart >
            </div >
        </el-dialog >
    </div >
</template >

<script >
    import DateUtils from '../../js/utils/DateUtils.js'
    import userCharts from '../userprofile/user-charts.vue'
    import taskInfoChart from '../userprofile/task-info-chart.vue'

    export default {
        components: {
            userCharts,
            taskInfoChart
        },
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
                temPath: '',
                routerPath: [],
                dialogFormVisible: false,
                taskIdOfChart: null,
                taskNameOfChart: null
            }
        },
        mounted: function () {
            let _this = this;
            this.$nextTick(function () {
                _this.routerPath['GENERAL'] = 'viewgeneral';
                _this.routerPath['FRAME'] = 'viewframe';
                _this.routerPath['SEGMENT'] = 'viewsegment';         //现在不存在两级目录了，直接通过task-lobby看标注结果
                _this.setUpBusEvent();
                _this.getAll();
            })
        },

        methods: {
            setUpBusEvent() {
                this.$bus.$on("handleClick", () => {
                    this.handleClick(row);
                })
            },
            dateFormat(oldDate) {
                Date.prototype.format = function (fmt) {
                    let o = {
                        "M+": this.getMonth() + 1,                 //月份
                        "d+": this.getDate(),                    //日
                        "h+": this.getHours(),                   //小时
                        "m+": this.getMinutes(),                 //分
                        "s+": this.getSeconds(),                 //秒
                        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
                        "S": this.getMilliseconds()             //毫秒
                    };
                    if (/(y+)/.test(fmt)) {
                        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
                    }
                    for (let k in o) {
                        if (new RegExp("(" + k + ")").test(fmt)) {
                            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
                        }
                    }
                    return fmt;
                };

                let tem = new Date(oldDate).getTime();
                let ans = new Date(tem).format("yyyy-MM-dd hh:mm:ss");
                return ans;
            },
            handleClick(row) {
                //点击任务进入该任务的合同列表
                let imgNames = row.imgNames;     //TODO：听说是task里面有imgNames，所以查看标注方法应该一样
                console.log(imgNames);
                this.$store.commit('changeImgNames', imgNames);
                console.log("haha");
                console.log(this.$store.getters.getImgNames);
                this.$router.push({
                    name: this.routerPath[row.taskCategory],
                    params: {taskId: row.taskId, taskType: 'requester'}
                });
            },
            showChart(row) {
                this.taskIdOfChart = row.taskId;
                this.taskNameOfChart = row.taskName;
                this.dialogFormVisible = true;
            },
            filterStatus(value, row) {    //根据合同状态筛选
                return row.taskStatus === value;
            },
            translateContractStatus: function (status) {        //TODO:翻译状态        这个状态的翻译看后端怎么判断已完成
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
                //  this.$http.get("http://localhost:8086/newTasks", {headers: {Authorization: store.state.token}}).then(function (response)
                let _this = this;
                this.$http.get("http://localhost:8086/requesterTasks", {headers: {Authorization: this.$store.getters.getToken}}).then(function (response) {

                    _this.taskData = response.data;     //只需要获得taskData的数据就可以了
                    for (let item of _this.taskData) {
                        item.taskStatus = _this.translateContractStatus(item.taskStatus);
                        item.createTime = _this.dateFormat(item.createTime);
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
</script >

<style >
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

    .default-font-style {
	    font-family: Microsoft YaHei;
    }
</style >
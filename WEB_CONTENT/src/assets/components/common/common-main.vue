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
                            <el-col :span="12">
                                <el-form-item label="任务描述">
                                    <span class="tableSlotSpan">{{ props.row.taskDescription }}</span>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>
                </template>
            </el-table-column>
            <el-table-column prop="source" label="任务来源" sortable :filters="[
                                {text: '参加过', value: '参加过'},
                                {text: '我喜欢', value: '标注任务'},
                                {text: '猜你喜欢', value: '猜你喜欢'},
                                {text: '新任务', value: '新任务'}]"
                             :filter-method="filterSourceHandler"></el-table-column>
            <el-table-column prop="taskCategoryChi" label="类别" sortable
                             :filters="[
                                {text: '整體標註', value: 'GENERAL'},
                                {text: '區域標註', value: 'FRAME'},
                                {text: '區域劃分', value: 'SEGMENT'}]"
                             :filter-method="filterCategoryHandler"></el-table-column>
            <el-table-column prop="taskName" label="任务名称"></el-table-column>
            <!--<el-table-column prop="taskDescription" label="任务描述"></el-table-column>-->
            <el-table-column prop="rewardPerMicrotask" label="单张图奖励" sortable></el-table-column>
            <el-table-column fixed="right" label="操作">
                <template slot-scope="scope">
                    <el-button @click="handleAnnotationJump(scope.row.taskId,scope.row.taskCategory)" type="text" size="medium">标注</el-button>
                    <el-button type="text" size="medium" @click="handleReviewJump(scope.row.taskCategory,scope.row.taskId)">评审</el-button>
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
                    rewardPerMicrotask: '',    //总积分变成了每张图片多少钱  这里可以经过加工和处理
                    formatEndTime: '',   //将读出来的时间进行格式化再显示
                    attendance: 0,      //表示有多少人参加了这个标注任务
                    capacity: 0,          //表示这个标注任务共限制多少人参加
                 */
                tableData: [],
                contractData: '',
                routerDictionary: [],     //进一步优化代码，用一个字典来装router要跳转的名称
                sourceDictionary: [],     //添加这个字典来保存是什么来源的任务
            }
        }
        ,
        mounted: function () {
            let _this = this;
            this.$nextTick(function () {
                this.initializeMap();
                _this.getAll();

            })
        },
        methods: {
            initializeMap(){     //在加载的时候就维护这个map
                this.routerDictionary = new Array();
                this.routerDictionary['GENERAL'] = 'viewgeneral';
                this.routerDictionary['SEGMENT'] = 'viewsegment';
                this.routerDictionary['FRAME'] = 'viewframe';
                this.sourceDictionary["http://localhost:8086/workerTasks"] = '参加过';
                //"http://localhost:8086/newTasks","http://localhost:8086/workerTasks"
                this.sourceDictionary["http://localhost:8086/newTasks"] = '新任务';   //后面还可以扩充我喜欢或者为你推荐
            },
            filterCategoryHandler(value, row, column) {      //对任务的类别进行筛选
                return row.taskCategory === value;
            },
            filterSourceHandler(value,row,column){
              return row.source === value;
            },
            doWhileGetTableDataSuccess(response,url) {        //赖总的编程风格很友好啊，将代码都优化了
                console.log(response.data);
                if(response.data.length != 0){
                    this.tableData = this.tableData.concat(response.data);     //现在是多个response.data  用数组连接
                }
                for (let e of this.tableData) {
                    e.formatEndTime = DateUtils.dateFormat(e.endTime); //将日期进行格式化
                    if (e.capacity === 2147483647) {
                        e.capacity = "无限制";
                    }
                    e.source = this.sourceDictionary[url];     //添加来源
                }
                this.translate();
            },
            decideGetTableDataUrl() {//现在的返回值是一个数组
                if (this.message === "user")   //现在理论上来说应该得到我喜欢的任务，但是还没有实现
                    return ["http://localhost:8086/workerTasks"]; //用户中心得到的是以前存在的任务
                else
                    return ["http://localhost:8086/newTasks","http://localhost:8086/workerTasks"];
            },
            getTableData() {
                let header = {Authorization: this.$store.getters.getToken};
                let _this = this;
                let getUrl = this.decideGetTableDataUrl();
                for(let url of getUrl){
                    this.$http.get(
                        url,
                        {headers: header}
                    ).then(function (response) {
                        _this.doWhileGetTableDataSuccess(response,url);
                    }).catch(function (error) {
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
                }

                console.log(this.tableData);
            },
            handleAnnotationJump(taskId,path){      //处理任务中心的jump标注
                let _this = this;
                _this.$http.get('http://localhost:8086/task/' + taskId, {
                    headers: {
                        Authorization: _this.$store.getters.getToken,
                    }
                }).then(function (response) {
                    let imgNames = response.data;
                    _this.$store.commit('changeImgNames',imgNames);
                    _this.$router.push({name: path.toLowerCase(),params:{taskId:taskId}});
                }).catch(function (error) {
                    console.log(error);
                })
            },
            handleReviewJump(taskCategory,taskId){     //处理是review的jump
                let _this = this;
                _this.$http.get('http://localhost:8086/inspect/enterInspection/' + taskId, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    let annotationIds = response.data;
                    console.log(response.data);
                    _this.$store.commit('changeAnnotationIds',annotationIds);         //在vuex中提交更改
                    _this.$router.push({name: _this.routerDictionary[taskCategory],params:{taskId : taskId}});         //现在的路由不需要参数了
                }).catch(function (error) {
                    _this.successMessage();
                    console.log(error);
                })
            },
            successMessage(){                    //这里有可能出现问题的原因是当前项目没得需要评审的任务了
                this.$notify({
                    title: '系统提示',
                    message: '这个任务没有更多需要评审的标注了，换个任务试试吧^_^',
                    type: 'success'
                });
            },
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
            },
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

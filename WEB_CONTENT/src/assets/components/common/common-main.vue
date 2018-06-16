<template >
    <div class = "main" >
        <el-table :data = "tableData" center = "all"
                  class = "table" :row-class-name = "tableRowClassName" >
            <el-table-column type = "expand" style = "margin-bottom: 0px" >
                <template slot-scope = "props" >
                    <el-form label-position = "left" class = "tableSlot" >
                        <el-row >
                            <el-col :span = "6" >
                                <el-form-item label = "发起者" >
                                    <span class = "tableSlotSpan" >{{props.row.requesterName}}</span >
                                </el-form-item >
                            </el-col >
                            <el-col :span = "12" >
                                <el-form-item label = "任务描述" >
                                    <span class = "tableSlotSpan" >{{ props.row.taskDescription }}</span >
                                </el-form-item >
                            </el-col >
                        </el-row >
                    </el-form >
                </template >
            </el-table-column >
            <el-table-column prop = "source" label = "任务来源" sortable :filters = "[
                                {text: '参加过', value: '参加过'},
                                {text: '我喜欢', value: '标注任务'},
                                {text: '猜你喜欢', value: '猜你喜欢'},
                                {text: '新任务', value: '新任务'}]"
                             :filter-method = "filterSourceHandler" ></el-table-column >
            <el-table-column prop = "taskCategoryChi" label = "类别" sortable
                             :filters = "[
                                {text: '整体标注', value: 'GENERAL'},
                                {text: '画框标注', value: 'FRAME'},
                                {text: '区域标注', value: 'SEGMENT'}]"
                             :filter-method = "filterCategoryHandler" ></el-table-column >
            <el-table-column prop = "taskName" label = "任务名称" ></el-table-column >
            <!--<el-table-column prop="taskDescription" label="任务描述"></el-table-column>-->
            <el-table-column prop = "rewardPerMicrotask" label = "单张图奖励" sortable ></el-table-column >
            <el-table-column fixed = "right" label = "操作" >
                <template slot-scope = "scope" >
                    <el-popover placement = "left" trigger = "hover" >
                        <p style = "font-size: 16px; font-weight: bold; text-align: center;"
                           class = "default-font-style" >请选择要进行的操作</p >
                        <div style = "text-align: center;" >
                            <el-button type = "success" size = "medium"
                                       @click = "handleAnnotationJump(scope.row,scope.row.taskId,scope.row.taskCategory)"
                                       round class = "default-font-style" >图片标注</el-button >
                            <el-button type = "danger" size = "medium"
                                       @click = "handleReviewJump(scope.row.taskCategory,scope.row.taskId,'grade')"
                                       round class = "default-font-style" >正确性判断</el-button >
                            <el-button type = "info" size = "medium"
                                       @click = "handleReviewJump(scope.row.taskCategory,scope.row.taskId,'coverage')"
                                       v-if = "scope.row.taskCategory !== 'GENERAL'" round
                                       class = "default-font-style" >完整性判断</el-button >
                        </div >
                        <el-button slot = "reference" type = "primary" size = "medium" align = "left" round
                                   class = "default-font-style" >参与任务</el-button >
                    </el-popover >
                </template >
            </el-table-column >
        </el-table >

        <el-dialog class = "tutorial" title = "教程" :visible.sync = "dialogTutorialVisible" :modal = "false"
                   top = "9vh" >
            <p >亲爱的用户，在您开始进行标注前，请您仔细阅读下面教程，可能会让你事半功倍呦：</p >
            <p >首先您要了解什么是好的标注，接下来以<strong >画框标注</strong >为例：</p >
            <img src = "../../images/tutorial.png" height = "400" width = "600" >
            <p >对标注最大的要求首先是<strong >不偏不倚</strong >，根据任务的要求找到相应的物品，但是还远不止于此</p >
            <p >例如上面例子中的<strong >错误示范</strong >，虽然它们都找到了相应的物品，却是不合理的标注</p >
            <p >图二的标注不够精细，没有<strong >紧紧贴合</strong >物体的轮廓，框出了一些物体之外的背景</p >
            <p >图三的标注虽然尝试贴合物体的轮廓，却没有将物体<strong >完整</strong >的圈出来</p >
            <p >图五的标注看似不错，但是它圈出了一部分假想的，却不属于该物体<strong >可见的部分</strong ></p >
            <p >标注看似简单，但标出完美的标注还需要您的<strong >用心参与</strong ></p >
            <h1 >加油吧💪!</h1 >
            <el-button type = "primary" @click = "read('tutorial')" >确 定</el-button >
        </el-dialog >

        <el-dialog class = "tutorial" title = "教程" :visible.sync = "dialogGradingVisible" :modal = "false" top = "9vh" >
            <p >亲爱的用户，您接下来要进行一项简单却事关重大的任务</p >
            <p >您仅仅需要判断其他用户的标注是否<strong >准确</strong ></p >
            <p >接下来是几张优秀的标注和不准确的标注图片，请您过目：</p >
            <img src = "../../images/tutorial.png" height = "400" width = "600" >
            <p >上图二的标注不够精细，<strong >没有紧紧贴合</strong >物体的轮廓，框出了一些物体之外的背景，是<strong >不能过关</strong >的标注</p >
            <p >上图三的标注虽然尝试贴合物体的轮廓，却<strong >没有</strong >将物体<strong >完整</strong >的圈出来,同样也是<strong >不能过关</strong >的标注</p >
            <p >图五的标注看似不错，但是它圈出了一部分假想的，却<strong >不属于</strong >该物体<strong >可见的部分</strong >,也是<strong >不能过关</strong >的标注</p >
            <p >您的判断对最后的质量把控十分<strong >重要</strong >，请<strong >严格</strong >要求标注的质量，不吝将有问题的标注拒之门外</p >
            <p >我们将会随机的对您的判断进行<strong >核查</strong >，如果发现您的判断出现严重问题会对您发出<strong >警告</strong >，多次出现问题将会对您进行<strong >惩罚</strong ></p >
            <p >但是不要太过担心，毕竟这只是项简单的工作，只要<strong >用心参与</strong >就不会出现问题</p >
            <h1 >加油吧💪!</h1 >
            <el-button type = "primary" @click = "read('grade')" >确 定</el-button >
        </el-dialog >

        <el-dialog class = "tutorial" title = "教程" :visible.sync = "dialogCoverageVisible" :modal = "false"
                   top = "9vh" >
            <p >亲爱的用户，您接下来要进行一项简单却事关重大的任务</p >
            <p >您仅仅需要判断该图片是否还有没有被标注的同类物品，即标注是否<strong >完整</strong ></p >
            <p >接下来是针对同一张图片的一些例子：</p >
            <img src = "../../images/have1.png" height = "165" width = "248" >
            <p >该图片<strong >仅仅标注</strong >了一个物品，而其他物品没有被标注出来，是<strong >不完整</strong >的标注</p >
            <img src = "../../images/have2.png" height = "165" width = "248" >
            <p >该图片虽然标注了不只一个物品，却<strong >没有</strong >将<strong >所有物体</strong >圈出来,同样也是<strong >不完整</strong >的标注</p >
            <img src = "../../images/haveall.png" height = "165" width = "248" >
            <p >在这张图片中，所有物品都被标注出来了，<strong >没有</strong >任何的<strong >遗漏</strong >，所以这张图片是<strong >合格的</strong >，能<strong >通过</strong >完整性检验的标注</p >
            <p >您的判断对最后的质量把控十分<strong >重要</strong >，请<strong >严格</strong >判断标注是否完全，不吝将不完整的标注拒之门外</p >
            <p >我们将会随机的对您的判断进行<strong >核查</strong >，如果发现您的判断出现严重问题会对您发出<strong >警告</strong >，多次出现问题将会对您进行<strong >惩罚</strong ></p >
            <p >但是不要太过担心，毕竟这只是项简单的工作，只要<strong >用心参与</strong >就不会出现问题</p >
            <h1 >加油吧💪!</h1 >
            <el-button type = "primary" @click = "read('coverage')" >确 定</el-button >
        </el-dialog >
    </div >
</template >

<script >
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
                dialogTutorialVisible: false,   //判断教程是否显示
                dialogGradingVisible: false,
                dialogCoverageVisible: false,       //这个教程待写
                path: '',
                taskId: '',
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
            initializeMap() {     //在加载的时候就维护这个map
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
            filterSourceHandler(value, row, column) {
                return row.source === value;
            },
            showTutorial(id, category, callback) {
                this.dialogTutorialVisible = true;

            },
            read(router) {
                if (this.taskId !== '' & this.path !== '') {
                    if (router === 'tutorial') {
                        this.$router.push({name: this.path.toLowerCase(), params: {taskId: this.taskId}});
                    }
                    else {
                        this.$router.push({name: this.path, params: {taskId: this.taskId, taskType: router}});
                    }
                }
            },
            doWhileGetTableDataSuccess(response, url) {        //赖总的编程风格很友好啊，将代码都优化了
                console.log(response.data);
                if (response.data.length != 0) {
                    for (let e of response.data) {
                        if (!(e in this.tableData)) {
                            this.tableData.push(e);
                            e.source = this.sourceDictionary[url];     //添加来源
                        }
                    }
                }

                this.translate();
            },
            decideGetTableDataUrl() {//现在的返回值是一个数组
                if (this.message === "user")   //现在理论上来说应该得到我喜欢的任务，但是还没有实现
                    return ["http://localhost:8086/workerTasks"]; //用户中心得到的是以前存在的任务
                else
                    return ["http://localhost:8086/workerTasks", "http://localhost:8086/newTasks"];
            },
            getTableData() {
                let header = {Authorization: this.$store.getters.getToken};
                let _this = this;
                let getUrl = this.decideGetTableDataUrl();
                for (let url of getUrl) {
                    this.$http.get(
                        url,
                        {headers: header}
                    ).then(function (response) {
                        _this.doWhileGetTableDataSuccess(response, url);
                    }).catch(function (error) {
                        console.log(error);
                    });
                }
            },
            translate: function () {
                for (let i = 0; i < this.tableData.length; i++) {
                    if (this.tableData[i].taskCategory === "GENERAL") {
                        this.tableData[i].taskCategoryChi = "整体标注";
                    } else if (this.tableData[i].taskCategory === "FRAME") {
                        this.tableData[i].taskCategoryChi = "画框标注";
                    } else if (this.tableData[i].taskCategory === "SEGMENT") {
                        this.tableData[i].taskCategoryChi = "区域标注";
                    }
                }

                console.log(this.tableData);
            },
            handleAnnotationJump(row, taskId, path) {      //处理任务中心的jump标注
                let _this = this;
                _this.$http.get('http://localhost:8086/task/' + taskId, {
                    headers: {
                        Authorization: _this.$store.getters.getToken,
                    }
                }).then(function (response) {
                    if (response.status === 204) {     //noContent    说明没有更多的图片可供标注
                        _this.runOutMessage();
                    }
                    else {
                        let imgNames = response.data;
                        _this.$store.commit('changeImgNames', imgNames);
                        _this.dialogTutorialVisible = true;
                        _this.$store.commit('changeTagsForAnnotation',row.tagsForAnnotation);
                        _this.$store.commit('changeTaskDescription',row.taskDescription);
                        _this.$store.commit('changeTagsOfTask',row.taskTags);     //暂时只有标注需要这个
                        _this.taskId = taskId;
                        _this.path = path;
                    }
                }).catch(function (error) {

                    console.log(error);
                });
            },
            handleReviewJump(taskCategory, taskId, type) {     //处理是review的jump   type为coverage或grade
                let _this = this;
                let path = "";
                if (type === 'grade') {
                    path = 'http://localhost:8086/qualityVerification/taskId/' + taskId;  //评分的交互路径
                }
                else if (type === 'coverage') {
                    path = 'http://localhost:8086/coverageVerification/taskId/' + taskId;   //完整性判断的交互路径
                }

                _this.$http.get(path, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    if (response.status === 204) {     //noContent    说明没有更多的图片可供review
                        _this.successMessage();
                    }
                    else {
                        let imgNames = response.data;
                        console.log(response.data);
                        if (type === 'grade') {        //因为要先确认能进去方法再加载教程
                            _this.dialogGradingVisible = true;
                        }
                        else {
                            _this.dialogCoverageVisible = true;
                        }
                        _this.$store.commit('changeImgNames', imgNames);         //在vuex中提交更改
                        _this.taskId = taskId;
                        _this.path = _this.routerDictionary[taskCategory];
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            runOutMessage() {
                this.$notify({
                    title: '系统提示',
                    message: '这个任务暂时没有可供标注的图片了，换个任务试试吧^_^',
                    type: 'success'
                });
            },
            successMessage() {                    //这里有可能出现问题的原因是当前项目没得需要评审的任务了
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
            },
        }
    }
</script >

<style >
    .main {
	    padding-left: 3em;
	    padding-right: 3em;
	    padding-top: 2em;
	    font-family: Microsoft YaHei;
	    /*background-image: url(../../images/mainbg.jpg);*/
	    /*background-size: auto 100%;*/
	    height: 650px;
	    overflow: auto;
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

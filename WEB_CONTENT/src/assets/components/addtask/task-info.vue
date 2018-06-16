<template >
    <div class = "taskInfo" >
        <el-form ref = "form" :rules = "rules" :model = "form" label-width = "100px" >
            <!--<el-form-item >-->
            <!--<label class = "title" >新增任务</label >-->
            <!--</el-form-item >-->
            <el-form-item label = "任务名称" prop = "taskName" >
                <el-input class = "input" v-model = "form.taskName" ></el-input >
            </el-form-item >
            <el-form-item label = "任务描述" prop = "taskDescription" >
                <el-input type = "textarea" class = "input" v-model = "form.taskDescription" >
                </el-input >
            </el-form-item >

            <el-form-item label = "任务标签" prop = "taskTags" >
                <el-tag
		                :key = "tag"
		                v-for = "tag in form.taskTags"
		                closable
		                :disable-transitions = "false"
		                class = "tag"
		                @close = "tagClose(tag)"
                >
                    {{tag}}
                </el-tag >
                <el-button size = "mini" @click = "dialogFormVisible = true" style = "font-size: 16px"
                           icon = "el-icon-plus" ></el-button >

                <el-dialog title = "添加标签" :visible.sync = "dialogFormVisible" :modal-append-to-body = "false"
                           width = "500px" >
                    <div style = "font-size: 16px; display: inline; margin-right: 20px" >标签名称</div >
                    <el-autocomplete
		                    v-model = "tagValue"
		                    :fetch-suggestions = "querySearch"
		                    placeholder = "请输入标签（建议使用系统推荐标签）"
		                    style = "width: 300px"
		                    @keyup.enter.native = "tagInputConfirm"
                    ></el-autocomplete >
                    <div slot = "footer" class = "dialog-footer" >
                        <el-button @click = "dialogFormVisible = false" >取 消</el-button >
                        <el-button type = "primary" @click = "tagInputConfirm" >确 定</el-button >
                    </div >
                </el-dialog >
            </el-form-item >

            <el-form-item label = "任务类型" prop = "taskCategory" >
                <el-select class = "input" v-model = "form.taskCategory" placeholder = "请选择任务类型" >
                    <el-option class = "option" label = "整体标注" value = "GENERAL" ></el-option >
                    <el-option class = "option" label = "画框标注" value = "FRAME" ></el-option >
                    <el-option class = "option" label = "区域标注" value = "SEGMENT" ></el-option >
                </el-select >
            </el-form-item >
            <el-form-item label = "工人要求" prop = "workerRequirement" >
                <el-radio-group v-model = "form.workerRequirement" >
                    <el-radio label = "NONE" >无要求</el-radio >
                    <el-radio label = "EXPERIENCE" >经验要求</el-radio >
                </el-radio-group >
            </el-form-item >
            <el-form-item v-if = "form.workerRequirement=='EXPERIENCE'" prop = "requiredExperience" label = "经验要求" >
                <el-input class = "input" v-model.number = "form.requiredExperience" ></el-input >
            </el-form-item >
            <el-form-item label = "提供备选" prop = "haveAnswer" >
                <el-radio-group v-model = "form.haveAnswer" >
                    <el-radio label = "YES" >是</el-radio >
                    <el-radio label = "NO" >否</el-radio >
                </el-radio-group >
            </el-form-item >
            <el-form-item label = "备选答案" v-if = "form.haveAnswer == 'YES'" prop = "tagsForAnnotation" >
                <el-input v-if = "questionVisible"
                          v-model = "questionValue"
                          @keyup.enter.native = "questionInputConfirm"
                          @blur = "questionInputConfirm"
                >
                </el-input >
                <el-button v-else size = "small" @click = "questionInput" style = "font-size: 16px" >+</el-button >
                <el-row v-for = "tags in form.tagsForAnnotation" >
                    <el-tag
		                    type = "warning"
		                    :key = "tags"
		                    closable
		                    class = "question"
		                    @close = "questionClose(tags)" >
                        {{tags}}
                    </el-tag >
                </el-row >
            </el-form-item >
            <el-form-item label = "收费标准" prop = "rewardPerMicrotask" >
                <el-input class = "input" v-model.number = "form.rewardPerMicrotask" ></el-input >
            </el-form-item >
            <!--<el-form-item >-->
            <!--<el-button class = "finishButton" @click = "onSubmit('form')" >完成</el-button >-->
            <!--</el-form-item >-->
        </el-form >
    </div >
</template >

<script >
    import TagUtils from '../../js/utils/TagUtils.js'

    export default {
        // props: ['imageNames'],  //在提交的时候将theImg赋给imgNames数组
        data() {
            var bePositive = (rule, value, callback) => {
                if (!value) {
                    return callback(new Error('该字段不能为空'));
                }
                if (parseFloat(value).toString() == "NaN") {
                    callback(new Error('请输入数字值'));
                } else {
                    if (value <= 0) {
                        callback(new Error('数字必须大于0'));
                    } else {
                        callback();
                    }
                }

            };
            return {
                form: {
                    //除去taskId属于后端之后添加的，这个属性不需要在前端出现
                    taskName: '',       //新创建的task的名字
                    taskCategory: '',
                    taskDescription: '',
                    workerRequirement: '',           //是否對工人有要求
                    rewardStrategy: '',//修改为rewardStrategy
                    imgNames: [],
                    requiredExperience: 0,
                    createTime: new Date(),  //这个属性需要在addTask之前根据new Date时间进行添加
                    requesterId: '', //表示是谁创建了这个task
                    // valid only if taskType = general
                    questions: [],
                    nominees: [],//要求的工人
                    rewardPerMicrotask: 0,     //对于每张图片的收费标准
                    taskTags: [],
                    haveAnswer: '',            //是否提供内置答案
                    tagsForAnnotation: [],     //如果提供内置答案，采用内置答案
                },
                tempQuestion: '',
                allWorkers: [],  //在初始化的时候去后端拿所有工人列表   多选框的key是workerId  value是workerName
                dialogFormVisible: false,
                tagValue: "",
                systemTags: [],
                questionValue: "",
                questionVisible: false,
                rules: {
                    taskName: [{required: true, message: '请输入任务名称', trigger: 'blur'}],
                    taskCategory: [{required: true, message: '请选择任务类型', trigger: 'change'}],
                    taskTags: [{type: 'array', required: true, message: '请添加至少一个任务标签', trigger: 'change'}],
                    workerRequirement: [{required: true, message: '请选择工人要求', trigger: 'change'}],
                    endTime: [{type: 'date', required: true, message: '请选择截止日期', trigger: 'change'}],
                    rewardStrategy: [{required: true, message: '请选择奖励方式', trigger: 'change'}],
                    nominees: [{type: 'array', required: true, message: '请至少选择一个工人', trigger: 'change'}],
                    haveAnswer: [{required: true, message: '请选择是否提供备选答案选项', trigger: 'change'}],
                    tagsForAnnotation: [{type: 'array', required: true, message: '请至少提出一个问题', trigger: 'blur'}],
                    totalReward: [{validator: bePositive, required: true, message: '请填入一个正数', trigger: "blur"}],     //现在填入整数即可
                    requiredExperience: [{validator: bePositive, trigger: "blur"}]
                }
            }
        },
        mounted: function () {
            this.$nextTick(() => {
                this.systemTags = TagUtils.getSystemTags();   //获得系统Tag列表
                this.setUpBusEvent()
            });
        },
        // beforeDestroy() {
        //     this.$bus.off("onSubmit");
        // },
        methods: {
            /* 往vue-bus注冊事件 */
            setUpBusEvent() {
                this.$bus.$on("onSubmit", () => {
                        this.onSubmit();
                        this.$bus.off("onSubmit");
                        this.$bus.off("validate");
                    }
                );
                this.$bus.$on("validate", (successHandler, errorHandler) => {
                    if (this.validate("form")) {
                        successHandler();
                    } else {
                        errorHandler();
                    }
                });
            },
            getImageNamesFromBus() {
                this.$bus.$emit("getImageNames", (value) => {
                    this.form.imgNames = value;
                });
            },
            getImageNumFromBus() {
                let imgNum = 0;
                this.$bus.$emit("getImageNum", (value) => {
                    imgNum = value;
                });
                return imgNum;
            },
            setUpFormData() {
                this.form.createTime = new Date();
                this.form.createTime.setTime(new Date().getTime() + 8 * 60 * 60 * 1000);
                this.form.requesterId = this.$store.getters.getUserId;    //设置新建任务的用户名

                if (this.form.workerRequirement === "APPOINT") {
                    this.form.requiredExperience = 2147483647;
                }

                if (this.form.haveAnswer === "NO") {
                    this.form.tagsForAnnotation = [];   //如果不提供内置的tag，为空
                }
            },
            decidePostData() {
                return {
                    /*
					现在还存在两个问题，一个是当没有人数限制的时候rewardPerPerson怎么表示
					另一个是当要求指定工人的时候，requiredExperience怎么搞
					*/
                    taskName: this.form.taskName,
                    taskCategory: this.form.taskCategory,
                    taskDescription: this.form.taskDescription,
                    taskTags: this.form.taskTags,
                    workerRequirement: this.form.workerRequirement,
                    rewardStrategy: this.form.rewardStrategy,
                    imgNames: this.form.imgNames,
                    rewardPerMicrotask: this.form.rewardPerMicrotask,
                    requiredExperience: this.form.requiredExperience,
                    createTime: this.form.createTime,
                    requesterId: this.form.requesterId,
                    tagsForAnnotation: this.form.tagsForAnnotation,
                }
            },
            validate(formName) {
                let valid = false;
                this.$refs[formName].validate((validFlag) => {
                    valid = validFlag;
                });
                return valid;
            },
            async onSubmit() {      //处理提交并且post到后台
                // let valid = false;
                // this.$refs[formName].validate((validFlag) => {
                //     valid = validFlag;
                // });
                //
                // if (valid) {
                let imgNum = await this.getImageNumFromBus();
                if (imgNum === 0) {
                    this.badMessage("请添加图片！");
                } else {
                    await this.uploadImageSet();
                    await this.getImageNamesFromBus();
                    this.setUpFormData();

                    this.$http({
                        url: "http://localhost:8086/task",
                        method: "POST",
                        headers: {Authorization: this.$store.getters.getToken},
                        data: this.decidePostData()
                    }).then(this.doWhileSuccess).catch((error) => {
                        console.log(error);
                        this.badMessage("你的积分不足，请前往个人中心进行充值");
                        this.$router.push({path: '/profile'});
                    })
                }
                // }
            },
            doWhileSuccess(response) {
                this.messageHandler();
                //this.uploadImageSet();
                this.$router.push({path: '/profile'});
            },
            uploadImageSet() {
                // 上傳圖片
                this.$bus.$emit("uploadImageSet");
                return new Promise(resolve => {
                    setTimeout(() => resolve(), 1000);
                });
            },
            messageHandler() {
                this.$message({
                    message: '任务已经成功上传',
                    type: 'success'
                })
            },
            badMessage(msg) {
                this.$alert(msg, '系统警告', {
                    confirmButtonText: '确定'
                });
            },
            tagClose(tag) {
                this.form.taskTags.splice(this.form.taskTags.indexOf(tag), 1);
            },
            tagInputConfirm() {
                let tagValue = this.tagValue;
                if (this.duplicateKeys(tagValue, this.form.taskTags)) {
                    this.badMessage("标签重复");
                    this.tagValue = "";
                } else {
                    if (tagValue) {
                        this.form.taskTags.push(tagValue);
                    }
                    this.dialogFormVisible = false;
                    this.tagValue = '';
                }
            },
            querySearch(queryString, cb) {
                let systemTags = this.systemTags;
                let results = queryString ? systemTags.filter(this.createFilter(queryString)) : systemTags;
                // 调用 callback 返回建议列表的数据
                cb(results);
            },
            createFilter(queryString) {
                return (systemTag) => {
                    return (systemTag.value.toLowerCase().indexOf(queryString.toLowerCase()) >= 0);
                };
            },

            questionClose(question) {
                this.form.tagsForAnnotation.splice(this.form.tagsForAnnotation.indexOf(question), 1);
            },
            questionInput() {
                this.questionVisible = true;
                this.$nextTick(_ => {
                    this.$refs.saveQuestionInput.$refs.input.focus();
                });
            },
            questionInputConfirm() {
                let questionValue = this.questionValue;
                if (this.duplicateKeys(questionValue, this.form.tagsForAnnotation)) {
                    this.badMessage("备选答案重复");
                    this.questionValue = "";
                } else {
                    if (questionValue) {
                        this.form.tagsForAnnotation.push(questionValue);
                    }
                    this.questionVisible = false;
                    this.questionValue = '';
                }
            },
            duplicateKeys(key, list) {
                for (let i = 0; i < list.length; i++) {
                    if (key === list[i]) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
</script >

<style >
    .el-form-item__label {
	    font-size: 16px !important;
	    padding: 0 20px 0 0 !important;
    }

    .taskInfo {
	    text-align: left;
	    margin-left: auto;
	    margin-right: auto;
    }

    .title {
	    margin-left: 0;
	    font-size: 36px;
    }

    .input {
	    width: 24em;
	    font-size: 16px;
    }

    .finishButton {
	    font-family: Microsoft YaHei;
	    width: 24em;
	    background: #CAFBF2;
	    border-color: #CAFBF2;
    }

    .option {
	    font-family: Microsoft YaHei;
    }

    #select { /*用浮动实现label和选择框在同一排*/
	    float: left;
    }

    .setSize {
	    min-width: 336px;
	    max-width: 336px;
    }

    #d {
	    clear: both;
    }

    .tag {
	    margin-right: 20px;
	    font-size: 16px;
	    font-weight: bold;
    }

    .question {
	    margin-right: 20px;
	    font-size: 16px;
	    font-weight: bold;
    }

    .el-radio__label {
	    font-size: 16px !important;
    }

    #picker {
	    min-width: 384px;
	    max-width: 384px;
    }
</style >
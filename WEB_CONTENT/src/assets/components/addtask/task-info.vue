<template>
    <div class="taskInfo">
        <el-form ref="form" :rules="rules" :model="form" label-width="80px">
            <el-form-item>
                <label class="title">新增任務</label>
            </el-form-item>
            <el-form-item label="任務名稱" prop="taskName">
                <el-input class="input" v-model="form.taskName"></el-input>
            </el-form-item>
            <el-form-item label="任務描述" prop="taskDescription">
                <el-input type="textarea" class="input" v-model="form.taskDescription">
                </el-input>
            </el-form-item>

            <el-form-item label="标签" prop="dynamicTags">
                <el-tag
                        :key="tag"
                        v-for="tag in form.dynamicTags"
                        closable
                        :disable-transitions="false"
                        class="tag"
                        @close="tagClose(tag)">
                    {{tag}}
                </el-tag>
                <el-input v-if="tagVisible"
                          v-model="tagValue"
                          ref="saveTagInput"
                          size="small"
                          @keyup.enter.native="tagInputConfirm"
                          @blur="tagInputConfirm"
                >
                </el-input>
                <el-button v-else size="small" @click="tagInput" style="font-size: 16px">+</el-button>
            </el-form-item>

            <el-form-item label="任務類型" prop="taskCategory">
                <el-select class="input" v-model="form.taskCategory" placeholder="請選擇任務類型">
                    <el-option class="option" label="區域劃分" value="SEGMENT"></el-option>
                    <el-option class="option" label="整體標註" value="GENERAL"></el-option>
                    <el-option class="option" label="區域標註" value="FRAME"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="截止日期" prop="endTime">
                <el-date-picker class="input" type="datetime" placeholder="選擇日期"
                                v-model="form.endTime" :picker-options="handlePicker"></el-date-picker>
            </el-form-item>
            <el-form-item label="工人要求" prop="workerRequirement">
                <el-radio-group v-model="form.workerRequirement">
                    <el-radio label="NONE">無要求</el-radio>
                    <el-radio label="EXPERIENCE">經驗要求</el-radio>
                    <el-radio label="APPOINT">工人指定</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.workerRequirement=='EXPERIENCE'" prop="requiredExperience" label="要求經驗">
                <el-input class="input" v-model.number="form.requiredExperience"></el-input>
            </el-form-item>
            <div>
                <el-form-item id="select" v-if="form.workerRequirement=='APPOINT'" prop="nominees" label="选择工人">
                    <el-select id="select" class="setSize" v-if="form.workerRequirement=='APPOINT'"
                               v-model="form.nominees"
                               multiple placeholder="请选择">
                        <el-option v-for="item in allWorkers" :label="item.workerName" :key="item.workerId"
                                   :value="item.workerId"></el-option>
                    </el-select>
                </el-form-item>
            </div>
            <el-form-item id='d' label="獎勵形式" prop="rewardStrategy">
                <el-radio-group v-model="form.rewardStrategy">
                    <el-radio label="TOTAL">固定積分</el-radio>
                    <el-radio label="INDIVIDUAL">平均積分</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="合計獎勵" prop="totalReward">
                <el-input class="input" v-model.number="form.totalReward"></el-input>
            </el-form-item>
            <el-form-item v-if="form.rewardStrategy=='INDIVIDUAL' && form.workerRequirement !== 'APPOINT'"
                          prop="capacity" label="限制人數">
                <el-input class="input" v-model.number="form.capacity"></el-input>
            </el-form-item>

            <el-form-item label="問題" v-if="form.taskCategory == 'GENERAL'" prop="dynamicTags">
                <el-input v-if="questionVisible"
                          v-model="questionValue"
                          ref="saveQuestionInput"
                          @keyup.enter.native="questionInputConfirm"
                          @blur="questionInputConfirm"
                >
                </el-input>
                <el-button v-else size="small" @click="questionInput" style="font-size: 16px">+</el-button>
                <el-row v-for="question in form.questions">
                    <el-tag
                            type="warning"
                            :key="question"
                            closable
                            class="question"
                            @close="questionClose(question)">
                        {{question}}
                    </el-tag>
                </el-row>
            </el-form-item>
            <el-form-item>
                <el-button class="finishButton" @click="onSubmit('form')">完成</el-button>
            </el-form-item>
        </el-form>

    </div>
</template>

<script>
    export default {
        // props: ['imageNames'],  //在提交的时候将theImg赋给imgNames数组
        data() {
            var bePositive = (rule, value, callback) => {
                if (!value) {
                    return callback(new Error('该字段不能为空'));
                }
                if (!Number.isInteger(value)) {
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
                    endTime: new Date(),
                    workerRequirement: '',           //是否對工人有要求
                    rewardStrategy: '',//修改为rewardStrategy
                    imgNames: [],
                    totalReward: '',
                    //当积分模式是固定积分的时候用户直接填入totalReward  否则用户填入限制人数和总奖励
                    capacity: '',       //人数限制
                    requiredExperience: 0,
                    createTime: new Date(),  //这个属性需要在addTask之前根据new Date时间进行添加
                    requesterId: '', //表示是谁创建了这个task
                    // valid only if taskType = general
                    questions: [],
                    nominees: [],//要求的工人
                    rewardPerPerson: 0,//对于每个人所给的钱
                    dynamicTags: [],
                },
                tempQuestion: '',
                allWorkers: [],  //在初始化的时候去后端拿所有工人列表   多选框的key是workerId  value是workerName
                tagValue: "",
                tagVisible: false,
                questionValue: "",
                questionVisible: false,
                handlePicker: {
                    disabledDate(nowDate) {
                        let date = new Date();
                        return nowDate.getTime() < date;    //设置禁用时间，在今天之前是禁用的。
                    }
                },

                rules: {
                    taskName: [{required: true, message: '请输入任务名称', trigger: 'blur'}],
                    taskCategory: [{required: true, message: '请选择任务类型', trigger: 'change'}],
                    workerRequirement: [{required: true, message: '请选择工人要求', trigger: 'change'}],
                    endTime: [{type: 'date', required: true, message: '请选择截止日期', trigger: 'change'}],
                    rewardStrategy: [{required: true, message: '请选择奖励方式', trigger: 'change'}],
                    nominees: [{type: 'array', required: true, message: '请至少选择一个工人', trigger: 'change'}],
                    questions: [{type: 'array', required: true, message: '请至少提出一个问题', trigger: 'blur'}],
                    capacity: [{validator: bePositive, trigger: "blur"}],
                    totalReward: [{validator: bePositive, trigger: "blur"}],
                    requiredExperience: [{validator: bePositive, trigger: "blur"}]
                }
            }
        },
        mounted: function () {
            let _this = this;
            this.$nextTick(function () {
                _this.getAllWorkers();   //获得所有当前的工人列表
            });

        },
        methods: {
        	getImageNamesFromBus(){
				this.$bus.$emit("getImageNames", (value)=> {
					this.form.imgNames = value;
				});
            },
            getImageNumFromBus(){
        	    let imgNum = 0;
                this.$bus.$emit("getImageNum", (value)=> {
                    imgNum = value;
                });
                return imgNum;
            },
            setUpFormData(){
				this.form.createTime = new Date();
				this.form.createTime.setTime(new Date().getTime() + 8 * 60 * 60 * 1000);
				this.form.requesterId = this.$store.getters.getUserId;    //设置新建任务的用户名

				if (this.form.rewardStrategy === "INDIVIDUAL") {
					this.form.rewardPerPerson = this.form.totalReward / this.form.capacity;
				}

				if (this.form.workerRequirement === "APPOINT") {
					this.form.requiredExperience = 2147483647;
				}

				if (this.form.rewardStrategy === "TOTAL") {
					this.form.capacity = 2147483647;
				}

				if (this.form.taskCategory !== 'GENERAL') {
					this.form.questions = [];
				}

				this.form.endTime.setTime(this.form.endTime.getTime() + 8 * 60 * 60 * 1000);
            },
            decidePostData(){
				return {
					/*
                    现在还存在两个问题，一个是当没有人数限制的时候rewardPerPerson怎么表示
                    另一个是当要求指定工人的时候，requiredExperience怎么搞
                    */
					taskName: this.form.taskName,
                    taskCategory: this.form.taskCategory,
					taskDescription: this.form.taskDescription,
					endTime: this.form.endTime,
					workerRequirement: this.form.workerRequirement,
					rewardStrategy: this.form.rewardStrategy,
					imgNames: this.form.imgNames,
					totalReward: this.form.totalReward,
					capacity: this.form.capacity,
					requiredExperience: this.form.requiredExperience,
					createTime: this.form.createTime,
					requesterId: this.form.requesterId,
					questions: this.form.questions,
					nominees: this.form.nominees,
					rewardPerPerson: this.form.rewardPerPerson,
				}
            },
            async onSubmit(formName) {      //处理提交并且post到后台
                let valid = false;
                this.$refs[formName].validate((validFlag) => {
                    valid = validFlag;
                });

                if(valid) {
                    let imgNum = await this.getImageNumFromBus();
                    if(imgNum === 0){
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
                        }).then(this.doWhileSuccess).catch((error)=> {
                            this.badMessage("你的积分不足，请前往个人中心进行充值");
                            this.$router.push({path: '/profile'});
                        })
                    }
                }
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
            /*
            addQuestion: function () {
                if (this.tempQuestion !== null && this.tempQuestion.length !== 0) {
                    this.form.questions.push(this.tempQuestion);
                    this.tempQuestion = '';
                }
                console.log(this.form.questions)
            },
            */
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
                this.form.dynamicTags.splice(this.form.dynamicTags.indexOf(tag), 1);
            },
            tagInput() {
                this.tagVisible = true;
                this.$nextTick(_ => {
                    this.$refs.saveTagInput.$refs.input.focus();
                });
            },
            tagInputConfirm() {
                let tagValue = this.tagValue;
                if (tagValue) {
                    this.form.dynamicTags.push(tagValue);
                }
                this.tagVisible = false;
                this.tagValue = '';
            },

            questionClose(question) {
                this.form.questions.splice(this.form.questions.indexOf(question), 1);
            },
            questionInput() {
                this.questionVisible = true;
                this.$nextTick(_ => {
                    this.$refs.saveQuestionInput.$refs.input.focus();
                });
            },
            questionInputConfirm() {
                let questionValue = this.questionValue;
                if (questionValue) {
                    this.form.questions.push(questionValue);
                }
                this.questionVisible = false;
                this.questionValue = '';
            },

            getAllWorkers() {
                let _this = this;
                //console.log(store);
                this.$http.get("http://localhost:8086/requester/allWorkers", {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    _this.allWorkers = response.data;
                    for (let e of _this.allWorkers) {
                        e.workerName = e.nickname + '/(' + e.emailAddress + ')';
                    }
                })
            }
        }
    }
</script>

<style scoped>
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

    .el-form-item__label {
        font-size: 16px;
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

</style>
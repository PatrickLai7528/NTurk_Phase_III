<template>
    <el-container>
        <el-main>
            <el-header>
                <el-progress :text-inside="true" :stroke-width="18" v-bind:percentage=percent
                             status="success"></el-progress>
            </el-header>
            <div class="block">
                <el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always"
                             v-on:change="onIndexChange">
                    <el-carousel-item v-for="single in tableData.imgNames">
                        <img v-bind:src="'http://localhost:8086/image/' + single" alt="图片" class="pic">
                    </el-carousel-item>
                </el-carousel>
            </div>
        </el-main>
        <el-aside width="300px" style="alignment: center">
            <el-form ref="form" :model="form">
                <el-form v-model="form.region" v-for="pair in annotationData[nowIndex].answerPairs">
                    <el-form-item class="Q_And_A" v-bind:label="pair.question">
                        <el-input v-model="pair.answer" v-on:blur="allDone" v-on:focus="allDone" v-on:change="allDone"></el-input>
                    </el-form-item>
                </el-form>

                <el-form-item>
                    <el-button type="primary" @click="saveAndCommit(nowIndex)" :disabled=submitDisabled>提交</el-button>
                    <el-button @click="onCancel">取消</el-button>
                </el-form-item>
            </el-form>
        </el-aside>
    </el-container>
</template>
<script>
    export default {
        data() {
            return {
                form: {
                    name: '',
                    region: '',
                    date1: '',
                    date2: '',
                    delivery: false,
                    type: [],
                    resource: '',
                    desc: '',
                },
                annotationData: [],    //在后端读出的annotation信息放在annotationData中
                questionData: [],     //应该增加questionData在加载任务的时候就将questionData加载出来
                isNew: [],     //在这个数组中放布尔值来表示应该是更新还是新建记录  如果之前后端加载不出来这个标记就是true=post 如果可以就是false=put
                percent: 0,
                nowIndex: 0,
                number: 0,
                tableData: [{//这个是通过taskId读出来的task
                    taskId: '',
                    taskName: '',
                    requesterId: '',
                    taskCategory: '',
                    requester: '',
                    reward: '',
                    imgNames: [],
                    questions: [],
                }],
                userClick: true,
                submitDisabled: "disabled",
            }
        },
        mounted: function () {
            this.$nextTick(function () {
                //保证el已经插入文档
                let _this = this;
                this.$nextTick(function () {
                    _this.load();
                    _this.allDone();  //在跳转到这个页面的时候调用allDone方法
                })
            });
        },
        methods: {
            load() {
                let _this = this;
                let theId = this.$route.params.taskId;
                let route = 'http://localhost:8086/tasks/id/' + theId;
                //{headers:{Authorization:that.$store.getters.getToken}
                this.$http.get(route,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
                    _this.tableData = response.data;//将特定task的内容读入tableData,然后去后端的annotation里面遍历
                    _this.number = _this.tableData.imgNames.length;
                    _this.questionData = _this.tableData.questions;

                    _this.percent = parseFloat(((_this.nowIndex + 1) / _this.number * 100).toFixed(1));
                    _this.getAnnotation();
                    //console.log(_this.isNew);
                    //console.log(_this.annotationData);

                }).catch(function (error) {
                    console.log(error);
                });
            },
            saveAndCommit(newIndex){    //对用按钮点击提交再封装一个方法  装饰者模式
                let flag = this.judgeEmpty(newIndex);
                if(flag === false){
                    this.badMessage();
                }
                else{
                    this.onSave(newIndex);
                    //在这里调用commit方法
                    let _this = this;
                    let route = 'http://localhost:8086/contract/complete/' + _this.tableData.taskId;
                    let data = {};
                    _this.$http.put(route,data,{headers:{Authorization:_this.$store.getters.getToken}}).then(function(response){
                        console.log("finish");
                    }).catch(function(error){
                        console.log(_this.$store.getters.getToken);
                       console.log(error);
                    });
                    _this.messageHandler();
                    _this.$router.push({path: '/profile'});
                }
            },
            messageHandler(){
                this.$message({
                    message:'任务已经提交，请安心等待结果和奖励^_^',
                    type:'success'
                })
            },
            badMessage(){
                this.$alert('您还没有完成这个任务', '系统警告', {
                    confirmButtonText: '确定'
                });
            },
            onSave(newIndex) {
                let _this = this;
                if (this.isNew[this.nowIndex] === false) {
                    this.$http.put('http://localhost:8086/generalAnnotation',

                        JSON.stringify(_this.annotationData[_this.nowIndex]),
                        {headers: {'Content-Type': 'application/json',Authorization:_this.$store.getters.getToken}}).then(function (response) {
                        _this.nowIndex = newIndex;
                        _this.percent = parseFloat(((newIndex + 1) / _this.number * 100).toFixed(1));
                    }).catch(function (error) {
                        console.log(error);
                    });
                }
                else {
                    this.isNew[this.nowIndex] = false;
                    this.$http.post('http://localhost:8086/generalAnnotation/taskId/' + _this.tableData.taskId,
                        JSON.stringify(_this.annotationData[_this.nowIndex]),
                        {headers: {'Content-Type': 'application/json',Authorization:_this.$store.getters.getToken}}).then(function (response) {

                        let nowPath = _this.annotationData[_this.nowIndex].imgName;
                        let route = 'http://localhost:8086/generalAnnotation/taskId/' + _this.tableData.taskId + '/imgName/' + nowPath;
                        _this.$http.get(route,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (res) {
                            _this.$set(_this.annotationData, _this.nowIndex, res.data);   //更新前端数组中的元素,因为异步执行的关系，所以必须放在add里面

                            _this.nowIndex = newIndex;
                            _this.percent = parseFloat(((newIndex + 1) / _this.number * 100).toFixed(1));
                        }).catch(function (err) {
                            console.log(err);
                        })
                    }).catch(function (error) {
                        console.log(error);
                    });
                }

                console.log(this.isNew);

            },
            allDone(){//在这里判断和改变提交按钮的disabled与否
                /*
                for(let i = 0;i < this.annotationData.length;i++){
                    let valid = this.judgeEmpty(i);
                    if(valid === false){
                        this.submitDisabled = 'disabled';   //只要有一个没有填写完成就不能提交
                        break;
                    }
                }

                this.submitDisabled = false;
                */
                let flag = false;
                console.log(this.annotationData);
                for(let e of this.annotationData){
                    for(let pairs of e.answerPairs){
                        if(pairs.answer === null || pairs.answer === undefined || pairs.answer === ""){
                            this.submitDisabled = 'disabled';
                            flag = true;
                        }
                    }
                }
                if(flag === false){
                    this.submitDisabled = false;
                }
            },
            onCancel() {
                this.$router.push({path: '/tasklobby'});
            },
            onIndexChange: function (newIndex, oldIndex) {
                // oldIndex===-1是一开始加载走马灯的情况
                //this.nowIndex = newIndex;
                // this.percent = parseFloat(((newIndex + 1) / this.number * 100).toFixed(1));
                if (oldIndex !== -1) {
                    // 阻止从第一张直接切换到最后一张
                    if (oldIndex === 0 && newIndex === this.tableData.imgNames.length - 1 && this.userClick === true) {
                        this.userClick = false;
                        this.$refs.carousel.setActiveItem(0);
                    }
                    else if (newIndex === 0 && oldIndex === this.tableData.imgNames.length - 1 && this.userClick === false) {
                        this.userClick = true;
                    }

                    // 阻止从最后一张直接切换到第一张
                    else if (oldIndex === this.tableData.imgNames.length - 1 && newIndex === 0 && this.userClick === true) {
                        this.userClick = false;
                        this.$refs.carousel.setActiveItem(this.tableData.imgNames.length - 1);
                    }
                    else if (newIndex === this.tableData.imgNames.length - 1 && oldIndex === 0 && this.userClick === false) {
                        this.userClick = true;
                    }

                    // 正常情况
                    else {
                        if (this.userClick === true) {
                            if (this.judgeEmpty(oldIndex) === false && newIndex > oldIndex) {
                                this.userClick = false;
                                this.$refs.carousel.setActiveItem(oldIndex);
                                this.badMessage();
                            }
                            else {
                                this.saveAndLoad(newIndex);
                            }
                        }
                        else {
                            this.userClick = true;
                        }
                    }
                }
            },
            async saveAndLoad(newIndex) {
                await this.onSave(newIndex);
            },
            getIndex: function (imgSrc) {          //调用这个方法得到当前图片在imgNames中的位置保持同步
                for (let i = 0; i < this.tableData.imgNames.length; i++) {
                    if (imgSrc === this.tableData.imgNames[i]) {
                        return i;
                    }
                }
            },
            judgeEmpty:function(oldIndex){
                let flag = true;
                for(let ans of this.annotationData[oldIndex].answerPairs){
                    if(ans.answer === null || ans.answer === undefined || ans.answer === ''){
                        flag = false;   //在general标记中只要有一个没有填写就是没有完成
                        break;
                    }
                }

                return flag;   //如果flag为false就不能翻页
            },
            getAnnotation: function () {
                let _this = this;
                for (let path of _this.tableData.imgNames) {
                    let route = 'http://localhost:8086/generalAnnotation/taskId/' + _this.tableData.taskId +'/imgName/' + path;
                    this.$http.get(route,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
                        //let tem = eval(response.data);
                        //_this.testData = response.data;
                        let index = _this.getIndex(path);
                        _this.$set(_this.annotationData, index, response.data);    //在组件中不能使用Vue.set来进行注册，应该用this.$set方法
                        //_this.annotationData.splice(index,1,response.data);   //必须用splice方法vue才能检测到数组元素的变化
                        _this.isNew[index] = false;//不是新的

                    }).catch(function (error) { //如果后端没有数据记录要自己造一个空的标注对象push进去
                        let index = _this.getIndex(path);
                        _this.isNew[index] = true;

                        function QuesAndAnswer(ques) {
                            this.question = ques;
                            this.answer = '';
                        }

                        function Annotation(img) {
                            this.imgName = img;
                            this.answerPairs = [];
                        }

                        let emptyAnnotation = new Annotation(path);
                        for (let ques of _this.questionData) {
                            let tem = new QuesAndAnswer(ques);
                            emptyAnnotation.answerPairs.push(tem);
                        }
                        _this.$set(_this.annotationData, index, emptyAnnotation);

                        _this.allDone();
                        //_this.annotationData.splice(index,1,emptyAnnotation);
                    })
                }
            }
        }
    }
</script>

<style scoped>
    .el-carousel__item h3 {
        color: #475669;
        font-size: 14px;
        opacity: 0.75;
        line-height: 150px;
        margin: 0;
    }

    .el-carousel__item:nth-child(2n) {
        background-color: #99a9bf;
    }

    .el-carousel__item:nth-child(2n+1) {
        background-color: #d3dce6;
    }

    .Q_And_A {
        font-size: 18px;
        line-height: 40px;
        margin: 30px;
    }

    .pic {
        height: 100%;
    }

    .el-form-item {
        margin: 20px;
    }

    .el-button {
        width: 100px;
        margin: 10px;
    }
</style>
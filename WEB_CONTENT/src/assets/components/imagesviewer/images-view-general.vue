<template>
    <el-container>
        <el-main>
            <div class="block">
                <el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always"
                             v-on:change="onIndexChange">
                    <el-carousel-item v-for="single in imgNames">
                        <img v-bind:src="'http://localhost:8086/image/' + single" alt="图片" class="pic">
                    </el-carousel-item>
                </el-carousel>
            </div>
        </el-main>
        <el-aside width="300px" style="alignment: center">
            <el-form ref="form" :model="form">
                <el-form v-model="form.region" v-for="pair in annotationData[nowIndex].answerPairs">
                    <el-form-item class="Q_And_A" v-bind:label="pair.question">
                        <el-input v-model="pair.answer" disabled></el-input>
                    </el-form-item>
                </el-form>
            </el-form>
            <div v-if="isRequester === false">
                <div class="prompt text" v-if="taskType === 'grade'">请判断该标注的正确性：</div>
                <div class="prompt text" v-if="taskType === 'coverage'">请判断标注的完整性：</div>
                <div v-if="taskType === 'grade'">
                    <div>
                        <img src="../../images/good.svg" width="300" height="100">
                        <el-radio class="text" v-model="nowRating" label="1">我觉得可以</el-radio>
                    </div>
                    <div class="next">
                        <img src="../../images/bad.svg" width="300" height="100">
                        <el-radio class="text" v-model="nowRating" label="2">我觉得不行</el-radio>
                    </div>
                </div>
                <div v-if="taskType === 'coverage'">
                    <div>
                        <img src="../../images/continued.svg" width="300" height="100">
                        <el-radio class="text" v-model="nowRating" label="1">还有漏网之鱼</el-radio>
                    </div>
                    <div class="next">
                        <img src="../../images/done.svg" width="300" height="100">
                        <el-radio class="text" v-model="nowRating" label="2">已经一网打尽</el-radio>
                    </div>
                </div>
                <el-button id="commit-button" :disabled=commitDisabled @click="commitRating" type="primary">提交<i class="el-icon-upload el-icon--right"></i></el-button>
            </div>
        </el-aside>
    </el-container>
</template>

<script>
    import UserUtils from '../../js/utils/UserUtils.js'
    export default {
        data(){
            return{
                //先通过contractId得到task信息，然后去task得到imgNames，但是调用后台方法的时候使用contractId+imgName
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
                annotationData: [],    //在后端读出的annotation信息放在annotationData中
                questionData: [],     //应该增加questionData在加载任务的时候就将questionData加载出来
                nowIndex: 0,
                taskId: this.$route.params.taskId,
                contractId: this.$route.params.contractId,
                mandatoryTime:this.$route.params.mandatoryTime,   //表示还要再评几次
                nowRating: 0,        //对当前图片的评分
                ratings: [],           //对这个合同所有的评分数组
                commitDisabled: 'disabled',
                isRequester: null,
                annotationIds: this.$store.getters.getAnnotationIds,
                taskType: this.$route.params.taskType,
                imgNames:[]
            }
        },
        mounted: function () {
            let _this = this;
            this.$nextTick(function () {
                /*
                const canvas = document.querySelector('#canvas');
                _this.context = canvas.getContext('2d');
                */
                this.isRequester = UserUtils.isRequester(this);
                _this.number = _this.$store.getters.getAnnotationIds.length;
                _this.percent = parseFloat(((_this.nowIndex + 1) / _this.number * 100).toFixed(1));
                _this.loadAnnotationList();
            })
        },
        watch:{
            $route: function (to,from) {
                if(to.name === 'viewgeneral'){
                    this.taskId = this.$route.params.taskId;
                    this.contractId = this.$route.params.contractId;
                    this.mandatoryTime = this.$route.params.mandatoryTime;   //表示还要再评几次
                    this.ratings = [];
                    this.tableData = [];
                    this.nowRating = 0;
                    this.annotation = {};
                    this.loadAnnotationList();
                }
            }
        },
        methods:{
            //像评分和提交的组件，如果当前登陆者是发起者，是不显示的
            commitRating(){
                //list里面的对象包含annotationId和rate
                function Inspection(annotationId,rate){
                    this.annotationId = annotationId;
                    this.rate = rate;
                }

                let inspections = [];   //这是最后的数组，所有的评分结果放在这个数组里
                for(let i = 0;i < this.ratings.length;i++){
                    let nowInspection = new Inspection(this.annotationData[i].annotationId,this.ratings[i]);
                    inspections.push(nowInspection);
                }

                function AllInspection(contractId,inspectionList){
                    this.inspections = inspectionList;
                    this.contractId = contractId;
                }

                let InspectionContract = new AllInspection(this.contractId,inspections);
                console.log(InspectionContract);

                let _this = this;
                this.$http.post('http://localhost:8086/inspect',
                    JSON.stringify(InspectionContract),
                    {headers: {'Content-Type': 'application/json',Authorization:this.$store.getters.getToken}}).then(function (response){
                    _this.mandatoryTime = _this.mandatoryTime - 1;   //将必做次数递减
                    if(_this.mandatoryTime >= 1){
                        _this.showMessage();    //显示提示，要接着做
                    }
                    else{       //完成了，显示提示消息，返回上一级
                        _this.successMessage();
                        _this.$router.push({path:'/profile'});
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            successMessage(){
                this.$notify({
                    title: '提交成功',
                    message: '恭喜你完成评审任务，请耐心等待系统发放奖励^_^',
                    type: 'success'
                });
            },
            showMessage(){        //显示要继续做的提示并且在点击确认后跳到下一个界面去
                let _this = this;
                this.$confirm('您还有要进行评审的任务，是否继续', '温馨提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let contractId = '';
                    _this.$http.get('http://localhost:8086/contract/review/' + _this.taskId, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                        contractId = response.data.contractId;   //得到contractId
                        console.log(contractId);
                        _this.$router.push({name: 'viewgeneral',params:{taskId:_this.taskId,contractId:contractId,mandatoryTime:_this.mandatoryTime}});
                    }).catch(function (error) {
                        _this.successMessage();
                        _this.$router.push({path: '/profile'});
                        console.log(error);
                    })
                }).catch(() => {
                    _this.$router.push({path: '/profile'});    //任务取消，返回任务中心
                    console.log("-1");
                });
            },
            canCommit(){
                if(this.ratings.length === this.tableData.imgNames.length){
                    this.commitDisabled = false;
                }
                else{
                    this.commitDisabled = 'disabled';
                }
            },
            loadAnnotationList(){            //现在加载逻辑非常简单  annotationId都有，只要按照顺序push就好了
                let _this = this;
                for(let annotationId of this.annotationIds){
                    let route = "http://localhost:8086/generalAnnotation/id" + annotationId;
                    this.$http.get(route,{headers:{Authorization: _this.$store.getters.getToken}}).then(function(response){
                        _this.annotation = response.data;
                        _this.imgNames.push(_this.annotation.imgName);
                        _this.annotationData.push(_this.annotation);
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            },
            getIndex: function (imgSrc) {          //调用这个方法得到当前图片在imgNames中的位置保持同步
                for (let i = 0; i < this.tableData.imgNames.length; i++) {
                    if (imgSrc === this.tableData.imgNames[i]) {
                        return i;
                    }
                }
            },
            onIndexChange: function (newIndex, oldIndex) {
                this.nowIndex = newIndex;
                this.nowRating = 0;
                this.commitDisabled = 'disabled';
            },
            ratingChange: function(score){
                this.ratings[this.nowIndex] = score;
                this.canCommit();
            },
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

    .prompt {
        margin-top: 100px;
    }

    #commit-button {
        margin-top: 50px;
    }

    .next{
        margin-top: 20px;
    }

    .text{
        font-family: Arial, KaiTi, STXihei, "华文细黑", "Microsoft YaHei", "微软雅黑";
        font-weight:600;
    }

</style>
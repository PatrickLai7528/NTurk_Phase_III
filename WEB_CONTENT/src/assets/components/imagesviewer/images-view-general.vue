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
                <el-form-item class="Q_And_A" v-bind:label="taskDescription">
                    <el-input v-model="annotationData[nowIndex]" disabled></el-input>
                </el-form-item>
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

        <el-dialog class="warn" title="错误提示" :visible.sync="dialogVisible" :modal="false" top="9vh">
            <p>亲爱的用户，在您刚才的评判过程中，我们发现了错误的判决：</p>
            <img v-bind:src=wrongImg alt="错误图片" height="270px" width="480px">
            <p>这道题的问题和答案分别是：</p>
            <p>问题：{{taskDescription}}</p>
            <p>答案是：{{wrongAnnotation.answer}}</p>
            <p>您认为:{{wrongAnswerPairs[0]}}</p>
            <p>但实际上：{{wrongAnswerPairs[1]}}</p>
            <p>您的答案对标注的质量非常关键，请您在下次的标注中更加<strong>用心</strong>和<strong>仔细</strong></p>
            <p>我们还会<strong>继续检查</strong>您的答案，如果下次再发现问题可能会对您采取<strong>惩罚措施</strong></p>

            <el-button type="primary" @click="read()">确 定</el-button>
        </el-dialog>
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
                annotationData: [],    //在后端读出的annotation信息放在annotationData中
                questionData: [],     //应该增加questionData在加载任务的时候就将questionData加载出来
                nowIndex: 0,
                nowRating: 0,        //对当前图片的评分
                ratings: [],           //对这个合同所有的评分数组
                commitDisabled: 'disabled',
                isRequester: null,
                taskType: this.$route.params.taskType,
                taskId: this.$route.params.taskId,
                imgNames:this.$store.getters.getImgNames,
                taskDescription: '',         //现在还需要taskDescription进行问题问答
                wrongImg: '',
                wrongAnnotation:{},
                dialogVisible: false,
                wrongAnswerPairs: [],
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
                _this.imgNames = _this.$store.getters.getImgNames;
                _this.number = _this.imgNames.length;
                _this.percent = parseFloat(((_this.nowIndex + 1) / _this.number * 100).toFixed(1));
                _this.loadAnnotationList();
            })
        },
        watch:{
            $route: function (to,from) {
                if(to.name === 'viewgeneral'){
                    this.taskId = this.$route.params.taskId;
                    this.imgNames = this.$store.getters.getImgNames;
                    this.taskDescription = this.$store.getters.getTaskDescription;
                    this.nowIndex = 0;
                    this.number = this.imgNames.length;
                    this.percent = parseFloat(((this.nowIndex + 1) / this.number * 100).toFixed(1));
                    this.ratings = [];
                    this.nowRating = 0;
                    this.annotation = {};
                    this.annotationData = [];
                    this.loadAnnotationList();
                }
            }
        },
        methods:{
            //像评分和提交的组件，如果当前登陆者是发起者，是不显示的
            read(){
              this.dialogVisible = false;
            },
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

                let _this = this;

                let path = '';
                if(_this.taskType === 'coverage'){
                    path = 'http://localhost:8086/coverageVerification/saveVerifications';
                }
                else if(_this.taskType === 'grade'){
                    path = 'http://localhost:8086/qualityVerification/saveVerifications';
                }
                else{
                    console.log("error");
                }

                this.$http.post(path,
                    JSON.stringify(inspections),
                    {headers: {'Content-Type': 'application/json',Authorization:this.$store.getters.getToken}}).then(function (response){
                        let failedIds = response.data.failedIds;
                        let forbidden = response.data.forbidden;

                        if(forbidden === true) {   //如果被禁赛了，输出禁赛信息
                            _this.forbiddenMessage();
                        }
                        else if(failedIds !== undefined || failedIds.length !== 0){    //说明这次的回答有不正确的地方
                            _this.wrongImg = failedIds[0];   //把第一条挑出来
                            let wrongIndex = _this.findIndexByImg(_this.wrongImg);    //去查找index
                            _this.wrongAnswerPairs = _this.translateRate(_this.ratings[wrongIndex]);

                            _this.wrongImg = 'http://localhost:8086/image/' + _this.wrongImg;
                            _this.showDialog();     //显示错误教程
                        }
                        else if(_this.canGoon()){          //判断还能不能继续做
                            _this.showMessage();    //能继续做，鼓励继续
                        }
                        else{
                            _this.$router.push({path: '/profile'});    //不能继续，返回任务中心
                        }

                        _this.showMessage();
                }).catch(function (error) {
                    console.log(error);
                });
            },
            findIndexByImg(img){
                for(let i = 0;i < this.number;i++){
                    if(this.imgNames[i] === img){
                        return i;
                    }
                }

                return -1;
            },
            translateRate(rate){    //返回一个二元组，第一个是错误的信息，第二个是正确的信息
                if(this.taskType === 'grade'){
                    if(rate === 0){
                        return ["这张图片的标注有问题，不能过关","这张图片的标注是正确的"];
                    }
                    else{
                        return ["这张图片的标注是正确的","这张图片的标注有问题，不能过关"];
                    }
                }
                else if(this.taskType === 'coverage'){
                    if(rate === 0){
                        return ["这张图片还有其他可以标注的","这张图片已经没有其他可供标注的"];
                    }
                    else{
                        return ["这张图片已经没有其他可供标注的","这张图片还有其他可以标注的"];
                    }
                }
            },
            showDialog(){
                this.wrongAnnotation = this.getWrongImgAnnotation(this.wrongImg);
                this.dialogVisible = true;   //显示错误提示
            },
            canGoon(){     //TODO： 通过taskId得到task，判断还能不能继续作评审工作  返回bool

            },
            forbiddenMessage(){
                this.$alert('您因为在这个任务中评审正确率太低，已经被禁止参加这个任务的评审工作', '禁赛通知', {
                    confirmButtonText: '确定',
                    type: 'error',
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
                this.$confirm('不够过瘾，再来一组^_^', '温馨提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let contractId = '';
                    let path = '';
                    if(_this.taskType === 'grade'){
                        path = 'http://localhost:8086/qualityVerification/taskId/' + _this.taskId;  //评分的交互路径
                    }
                    else if(_this.taskType === 'coverage'){
                        path = 'http://localhost:8086/coverageVerification/taskId/' + _this.taskId;   //完整性判断的交互路径
                    }
                    else{
                        console.log("emmmm");
                    }

                    _this.$http.get(path, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                        let imgNames = response.data;
                        _this.$store.commit('changeImgNames',imgNames);
                        _this.$router.push({name: 'viewgeneral',params:{taskId:_this.taskId,taskType:_this.taskType}});
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
                for(let img of this.imgNames){
                    let route = "http://localhost:8086/generalAnnotation/imgNames/" + img;
                    this.$http.get(route,{headers:{Authorization: _this.$store.getters.getToken}}).then(function(response){
                        _this.annotation = response.data;
                        _this.annotationData.push(_this.annotation);
                    }).catch(function (error) {
                        console.log(error);
                    })
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
<template >

    <el-container >
        <el-main class = "wrap" >
            <div class = "block" >
                <el-carousel id = "carousel" ref = "carousel" height = "36em" v-bind:autoplay = "false" arrow = "always"
                             v-on:change = "onIndexChange" >
                    <el-carousel-item id = "carouselItem" v-for = "item in imgNames.length" :key = "item" >
                        <div id = "canvasDiv" >
                            <canvas
		                            id = "canvas"
		                            class = "fl"
                            >
                            </canvas >
                        </div >
                    </el-carousel-item >
                </el-carousel >
            </div >
        </el-main >
        <el-aside width = "300px" style = "alignment: center" >
            <!--<el-button @click="updateThisAnnotation()">更新</el-button>-->
            <!--<el-button @click="previousPictureAndUpdateThisAnnotation()">上一個</el-button>-->
            <!--<el-button @click="nextPictureAndUpdateThisAnnotation()">下一個</el-button>-->
            <!--<el-button @click="clearThisAnnotation()">清除</el-button>-->
            <!--<el-button @click="undoThisAnnotation()">UNDO</el-button>-->
            <ul style = "list-style-type:none" >
                <li >
                    <!--<el-button @click="handleCommit(nowIndex)" :disabled=submitDisabled>提交</el-button>-->
                </li >
                <li v-for = "(val,index) in frames" >
                    <el-popover
		                    placement = "right"
		                    width = "150"
		                    trigger = "click"
                    >
                        <el-input
		                        type = "textarea"
		                        :rows = "2"
		                        v-model = "val.tag"
		                        :disabled = "true"
                        >
                        </el-input >
                        <el-button slot = "reference" class = "tag" >標記{{index+1}}</el-button >
                        <el-button @click = "deleteTag(index)" :disabled = "true" >刪除</el-button >
                    </el-popover >
                </li >
            </ul >
            <div v-if = "isRequester === false" >
                <div class = "prompt text" v-if = "taskType === 'grade'" >请判断该标注的正确性：</div >
                <div class = "prompt text" v-if = "taskType === 'coverage'" >请判断标注的完整性：</div >
                <div v-if = "taskType === 'grade'" >
                    <div >
                    <img src = "../../images/good.svg" width = "300" height = "100" >
                    <el-radio class = "text" v-model = "nowRating" label = "1"
                              v-on:change = "ratingChange" >我觉得可以</el-radio >
                    </div >
                    <div class = "next" >
                    <img src = "../../images/bad.svg" width = "300" height = "100" >
                    <el-radio class = "text" v-model = "nowRating" label = "0"
                              v-on:change = "ratingChange" >我觉得不行</el-radio >
                    </div >
                </div >
                <div v-if = "taskType === 'coverage'" >
                    <div >
                    <img src = "../../images/continued.svg" width = "300" height = "100" >
                    <el-radio class = "text" v-model = "nowRating" label = "0"
                              v-on:change = "ratingChange" >还有漏网之鱼</el-radio >
                    </div >
                    <div class = "next" >
                    <img src = "../../images/done.svg" width = "300" height = "100" >
                    <el-radio class = "text" v-model = "nowRating" label = "1"
                              v-on:change = "ratingChange" >已经一网打尽</el-radio >
                    </div >
                </div >
                <el-button id = "commit-button" :disabled = commitDisabled @click = "commitRating"
                           type = "primary" >提交<i class = "el-icon-upload el-icon--right" ></i ></el-button >
            </div >
        </el-aside >

        <el-dialog class = "warn" title = "错误提示" :visible.sync = "dialogVisible" :modal = "false" top = "9vh" >
            <p >亲爱的用户，在您刚才的评判过程中，我们发现了错误的判决：</p >
            <div id = "canvasDiv2" >
                <div v-html = "canvasHtml" >
                    {{canvasHtml}}
                </div >
                <div v-html = "tagHtml" >{{tagHtml}}</div >
            </div >
            <p >这道题的图片和标注如上图所示</p >
            <p >您认为:{{wrongAnswerPairs[0]}}</p >
            <p >但实际上：{{wrongAnswerPairs[1]}}</p >
            <p >您的答案对标注的质量非常关键，请您在下次的标注中更加<strong >用心</strong >和<strong >仔细</strong ></p >
            <p >我们还会<strong >继续检查</strong >您的答案，如果下次再发现问题可能会对您采取<strong >惩罚措施</strong ></p >

            <el-button type = "primary" @click = "read()" >确 定</el-button >
        </el-dialog >
    </el-container >
</template >

<style >
    .el-carousel__item:nth-child(2n) {
	    background-color: #99a9bf;
    }

    .el-carousel__item:nth-child(2n+1) {
	    background-color: #d3dce6;
    }

    .fl {
	    float: left;
	    display: block;
    }

    #carouselItem {
	    text-align: center;
    }

    #canvas {
	    border-right: 1px #585858 solid;
	    cursor: crosshair;
	    background-color: black;
    }

    #canvasDiv {
	    position: relative;
	    display: inline-block;
    }

    .tag {
	    background-color: #df4b26;
	    color: white;
    }

    .prompt {
	    margin-top: 250px;
    }

    #commit-button {
	    margin-top: 50px;
    }

    .next {
	    margin-top: 20px;
    }

    .text {
	    font-family: Arial, KaiTi, STXihei, "华文细黑", "Microsoft YaHei", "微软雅黑";
	    font-weight: 600;
    }
</style >

<script >
    import UserUtils from '../../js/utils/UserUtils.js'
    import ImageViewer from '../../js/ImageViewer.js'
    import FrameDrawingStrategy from '../../js/strategy/FrameDrawingStrategy.js'

    export default {
        data() {
            return {
                context: {},
                canvasHeight: 0,
                canvasWidth: 0,
                annotation: {},
                annotationData: [],   //这个是供前端加载和服务器同步的标记数组
                frames: [
                    {
                        p1: {
                            x: 0,
                            y: 0
                        },
                        p2: {
                            x: 0,
                            y: 0
                        },
                        tag: "",
                        color: "",
                    }
                ],
                frameIndex: 0,
                nowIndex: 0,
                pic:
                    {}
                ,
                color: "#df2c2d",
                isNew: [],
                startPoint:
                    {
                        x: 0,
                        y: 0,
                    }
                ,
                endPoint: {
                    x: 0,
                    y: 0
                },
                percent: 0,
                number: 0,
                userClick: true,
                nowRating: 0,        //对当前图片的评分
                ratings: [],           //对这个合同所有的评分数组
                commitDisabled: 'disabled',
                isRequester: null,
                taskId: this.$route.params.taskId,
                imgNames: this.$store.getters.getImgNames,     //可以得到所有标注的编号，再通过所有标注的编号去找到这个标注和这个标注对应的imgName
                taskType: this.$route.params.taskType,
                dialogVisible: false,
                wrongImg: "taskPic-2018061515075768226.jpg",            //返回的是wrongImg
                wrongAnnotation: {},      //这是那个wrongImg的annotation
                wrongAnswerPairs: [],
                tagHtml: '',
                canvasHtml: '<canvas id="canvas"></canvas>',
            }
        },
        mounted() {
            let _this = this;
            this.$nextTick(function () {
                /*
                const canvas = document.querySelector('#canvas');
                _this.context = canvas.getContext('2d');
                */
                this.isRequester = UserUtils.isRequester(this);
                if(this.isRequester === false){
                    _this.imgNames = _this.$store.getters.getImgNames.imgNames;
                }
                else{
                    _this.imgNames = _this.$store.getters.getImgNames;    //是发起者的时候这里略微有点不一样
                }

                _this.number = _this.imgNames.length;
                _this.percent = parseFloat(((_this.nowIndex + 1) / _this.number * 100).toFixed(1));
                _this.loadAnnotationList(_this.loadImageAndAnnotation);
                this.setDialogContent();
            });
        },
        methods: {
            selectCanvas() {
                return document.getElementById("canvas");
            },
            setDialogContent() {
                let id, doIt;
                doIt = () => {
                    let imageViewer, imageNameList = [], canvas, drawingStrategy, markingType, config;
                    config = {strokeStyle: "black"};
                    canvas = this.selectCanvas();
                    imageNameList.push(this.wrongImg);
                    imageViewer = new ImageViewer(canvas, imageNameList, "");
                    imageViewer.drawCurrent();
                    drawingStrategy = new FrameDrawingStrategy();
                    markingType = drawingStrategy.getMarkingTypeName();
                    this.wrongAnnotation[markingType].forEach((value, index, array) => {
                        drawingStrategy.drawThis(canvas.getContext("2d"), value, config);
                    })
                };
                if (this.dialogVisible) {
                    doIt();
                } else {
                    id = setInterval(() => {
                        if (this.dialogVisible) {
                            doIt();
                            clearInterval(id);
                        }
                    }, 2000)
                }
            },
            read() {
                this.dialogVisible = false;
            }
            ,
            commitRating() {
                //list里面的对象包含annotationId和rate
                function Verification(annotationId,rate){
                    this.annotationId = annotationId;
                    this.rate = rate;
                }

                let verifications = [];   //这是最后的数组，所有的评分结果放在这个数组里

                let data = {
                    verifications: verifications,
                };

                console.log(this.annotationData[0]);

                for(let i = 0;i < this.ratings.length;i++){
                    let nowInspection = new Verification(this.annotationData[i].annotationId,this.ratings[i]);
                    verifications.push(nowInspection);
                }

                let _this = this;
                let path = '';
                if (_this.taskType === 'coverage') {
                    path = 'http://localhost:8086/coverageVerification/saveVerifications';
                }
                else if (_this.taskType === 'grade') {
                    path = 'http://localhost:8086/qualityVerification/saveVerifications';
                }

                console.log('Inspections');
                console.log(verifications);
                this.$http.post(path,
                    JSON.stringify(data),
                    {headers: {'Content-Type': 'application/json',Authorization:_this.$store.getters.getToken}}).then(function (response){
                        console.log(response);    //现在交互有一定的改变，如果掉到坑里也还是正常返回
                        let res = response.data;
                        let forbidden = res.forbidden;

                        if(forbidden === true) {   //如果被禁赛了，输出禁赛信息
                            _this.forbiddenMessage();
                        }
                        else if(res.failedImgNames !== undefined && res.failedImgNames.length !== 0){    //说明这次的回答有不正确的地方
                            _this.wrongImg = res.failedImgNames[0];   //把第一条挑出来
                            let wrongIndex = _this.findIndexByImg(_this.wrongImg);    //去查找index
                            _this.wrongAnswerPairs = _this.translateRate(_this.ratings[wrongIndex]);

                            _this.wrongImg = 'http://localhost:8086/image/' + _this.wrongImg;
                            _this.showDialog();     //显示错误教程
                        }
                        else{
                            _this.canGoon(_this.showMessage);
                        }
                }).catch(function (error) {
                    console.log(error);
                });
            }
            ,
            findIndexByImg(img) {
                for (let i = 0; i < this.number; i++) {
                    if (this.imgNames[i] === img) {
                        return i;
                    }
                }

                return -1;
            }
            ,
            translateRate(rate) {    //返回一个二元组，第一个是错误的信息，第二个是正确的信息
                if (this.taskType === 'grade') {
                    if (rate === 0) {
                        return ["这张图片的标注有问题，不能过关", "这张图片的标注是正确的"];
                    }
                    else {
                        return ["这张图片的标注是正确的", "这张图片的标注有问题，不能过关"];
                    }
                }
                else if (this.taskType === 'coverage') {
                    if (rate === 0) {
                        return ["这张图片还有其他可以标注的", "这张图片已经没有其他可供标注的"];
                    }
                    else {
                        return ["这张图片已经没有其他可供标注的", "这张图片还有其他可以标注的"];
                    }
                }
            },
            showDialog() {
                this.wrongAnnotation = this.getWrongImgAnnotation(this.wrongImg);
                this.dialogVisible = true;   //显示错误提示
            },
            canGoon(callback1){
                let _this = this;
                let route = 'http://localhost:8086/taskId/' + this.taskId;
                this.$http.get(route,{headers:{Authorization: _this.$store.getters.getToken}}).then(function(response){
                    let taskInfo = response.data;
                    console.log(taskInfo.verifyQuality);
                    if(_this.taskType === 'grade'){
                        if(taskInfo.verifyQuality > 0) {
                            callback1();
                        }
                        else{
                            _this.$router.push({path: '/profile'});
                        }
                    }
                    else if(_this.taskType === 'coverage'){
                        if(taskInfo.verifyCoverage > 0){
                            callback1();
                        }
                        else{
                            _this.$router.push({path: '/profile'});
                        }
                    }
                }).catch(function (error) {                 //理论上来说不会出现这种情况
                    console.log(error);
                });
            },
            forbiddenMessage(){
                this.$alert('您因为在这个任务中评审正确率太低，已经被禁止参加这个任务的评审工作', '禁赛通知', {
                    confirmButtonText: '确定',
                    type: 'error',
                });
            },
            successMessage() {
                this.$notify({
                    title: '提交成功',
                    message: '恭喜你完成评审任务，请耐心等待系统发放奖励^_^',
                    type: 'success'
                });
            },
            getWrongImgAnnotation(wrongImg) {
                let _this = this;
                let index = _this.findIndexByImg(wrongImg);
                _this.wrongAnnotation = _this.annotationData[index];        //不用再去后台拿一遍了
            },
            showMessage() {        //显示要继续做的提示并且在点击确认后跳到下一个界面去
                let _this = this;
                this.$confirm('不够过瘾，再来一组^_^', '温馨提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let path = '';
                    if (_this.taskType === 'grade') {
                        path = 'http://localhost:8086/qualityVerification/taskId/' + _this.taskId;  //评分的交互路径
                    }
                    else if (_this.taskType === 'coverage') {
                        path = 'http://localhost:8086/coverageVerification/taskId/' + _this.taskId;   //完整性判断的交互路径
                    }

                    _this.$http.get(path, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                        let imgNames = response.data;
                        _this.$store.commit('changeImgNames', imgNames);
                        location.reload();
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
            canCommit() {
                if (this.ratings.length === this.imgNames.length) {
                    this.commitDisabled = false;
                }
                else {
                    this.commitDisabled = 'disabled';
                }
            },
            ratingChange: function (score) {
                this.ratings[this.nowIndex] = score;
                this.canCommit();
            }
            ,
            loadAnnotationList(callback) {            //现在加载逻辑非常简单
                let _this = this;
                let whatfor = 3;
                if (_this.taskType === 'coverage') {
                    whatfor = 2;
                }
                else if (_this.taskType === 'grade') {
                    whatfor = 1;
                }

                console.log(_this.imgNames);

                let flag = false;
                for (let i = 0; i < this.imgNames.length; i++) {
                    let route = "http://localhost:8086/frameAnnotation/imgName/" + this.imgNames[i] + "/whatFor/" + whatfor;
                    this.$http.get(route, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                        if (response.status === 204) {
                            _this.frames = [];
                            _this.annotation = {
                                'imgName': _this.imgNames[i],
                                'frame': _this.frames,
                            };
                            _this.$set(_this.annotationData,i,_this.annotation);
                            _this.annotationData[i] = _this.annotation;
                            if (_this.allLoad()) {
                                callback()
                            }
                        }
                        else {
                            let temp = response.data;
                            if (_this.taskType === 'grade') {     //只有grade任务才牵扯变色
                                for (let item of temp.frames) {
                                    item.color = "#1ABC9C";   //对于过去的标注，是青色
                                }

                                temp.frame.color = '#C0392B';
                            }

                            temp.frames.push(temp.frame);

                            if(_this.taskType !== 'grade'){         //这里要注意非grade的逻辑是一样的
                                for(let item of temp.frames){
                                    item.color = '#C0392B';
                                }
                            }

                            _this.annotation = {
                              'imgName': _this.imgNames[i],
                              'frame': temp.frames,        //这里先这样进行加工，等变色方法出来了再说
                              'annotationId': temp.annotationId,            //如果是从后端读出来的就有annotationId
                            };
                            console.log(_this.annotation);
                            _this.$set(_this.annotationData,i,_this.annotation);
                            if (_this.allLoad()) {
                                callback();
                            }
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }
            },
            allLoad(){
                if(this.annotationData.length !== this.imgNames.length){
                    return false;
                }

                for(let i = 0;i < this.annotationData.length;i++){
                    if(this.annotationData[i] === undefined){
                        return false;
                    }

                    return true;
                }
            },
            onIndexChange: function (newIndex, oldIndex) {
                this.nowIndex = newIndex;
                this.loadWhenChange(newIndex);
                this.nowRating = 0;
                this.commitDisabled = 'disabled';
            },
            loadWhenChange(newIndex) {
                this.nowIndex = newIndex;
                this.loadImageAndAnnotation();
                this.percent = parseFloat(((newIndex + 1) / this.number * 100).toFixed(1));
            },
            loadImageAndAnnotation() {
                let _this = this;
                _this.pic = new Image();
                _this.pic.src = "http://localhost:8086/image/" + _this.imgNames[this.nowIndex];
                // this.pic.addEventListener('load', function () {
                _this.pic.onload = function () {
                    //alert("here");
                    // draw(this);
                    const temp = document.getElementById('carousel');
                    const canvasDiv = temp.querySelectorAll('#canvasDiv').item(_this.nowIndex);
                    const canvas = canvasDiv.querySelector('#canvas');
                    _this.context = canvas.getContext('2d');

                    let ratio = this.width / this.height;
                    _this.canvasHeight = document.getElementById('carousel').offsetHeight;
                    _this.canvasWidth = _this.canvasHeight * ratio;
                    canvas.height = _this.canvasHeight;
                    canvas.width = _this.canvasWidth;
                    // 这句放这里是有点怪…但是好像改了width的height，lineWidth会变……

                    _this.context.drawImage(this, 0, 0, _this.canvasWidth, _this.canvasHeight);

                    // 必须先img，然后再annotation，不然img就覆盖annotation了…
                    _this.loadAnnotation();
                }

            },
            /**
             * loading-related methods. (mouse-event listeners are down below)
             * */
            loadAnnotation() {
                this.annotation = this.annotationData[this.nowIndex];
                this.frames = this.annotation.frame;             //之前实现的有问题，应该现把frames加载，然后调用initialDraw方法
                console.log(this.frames);
                this.initialDraw();
            },
            /**
             * draw methods. (and tag)
             * */
            initialDraw() {
                // 因为tag可以只draw一次，但是frame必须每次重来
                // 所以，第一次每个frame都要addTag。之后就只有新建的才需要了。
                const temp = document.getElementById('carousel');
                const canvasDiv = temp.querySelectorAll('#canvasDiv').item(this.nowIndex);
                const canvas = canvasDiv.querySelector('#canvas');
                this.context = canvas.getContext('2d');
                this.context.clearRect(0, 0, this.canvasWidth, this.canvasHeight);

                const childNodes = canvasDiv.childNodes;
                console.log(childNodes.length);
                for (let i = 0; i < childNodes.length; i++) {
                    // 又是这个循环变长的坑……childNodes中间如果改了就会不可控…
                    const cn = childNodes[i];
                    console.log(cn);
                    if (cn.tagName === 'DIV') {
                        console.log("is in");
                        canvasDiv.removeChild(cn);
                        i--;
                    }
                }
                // draw
                this.context.drawImage(this.pic, 0, 0, this.canvasWidth, this.canvasHeight);

                this.context.lineWidth = 5;
                console.log(this.frames);
                for (let i = 0; i < this.frames.length; i++) {
                    const f = this.frames[i];
                    if (this.frames[i].color !== undefined && this.frames[i].color !== "") {    //看变色
                        this.context.strokeStyle = this.frames[i].color;
                    }
                    else {
                        this.context.strokeStyle = this.color;
                    }
                    this.context.strokeRect(f.p1.x, f.p1.y,
                        f.p2.x - f.p1.x, f.p2.y - f.p1.y);
                    this.addTag(f, i);
                }
            },
            addTag(frame, index) { // the interface may be simplified. (no “frame” parameter)
                const p = this.getRightTopPoint(frame);
                const canvas = document.querySelector('#canvas');
                const cssString = "position:absolute; white-space: nowrap;" + "top:" + (p.y + canvas.offsetTop) + "px;" + "left:" + p.x + "px;";

                const htmlString = `<el-tag style="background: #e5e9f2">標記${index+1} </el-tag>`;
                let div = document.createElement('div');
                div.id = 'tag' + index;
                div.innerHTML = htmlString;
                div.setAttribute('style', cssString);
                // end
                const temp = document.getElementById('carousel');
                const canvasDiv = temp.querySelectorAll('#canvasDiv').item(this.nowIndex);
                canvasDiv.appendChild(div);
                // this.canCommit();
            },
            drawRec() {
                const temp = document.getElementById('carousel');
                const canvasDiv = temp.querySelectorAll('#canvasDiv').item(this.nowIndex);
                const canvas = canvasDiv.querySelector('#canvas');
                this.context = canvas.getContext('2d');
                this.context.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
                // 目前只能想到把所有的重画一遍

                this.context.drawImage(this.pic, 0, 0, this.canvasWidth, this.canvasHeight);
                // for (const f of frames) {
                // console.log("frames length  " + this.frames.length);

                this.context.strokeStyle = this.color;
                this.context.lineWidth = 5;
                for (let i = 0; i < this.frames.length; i++) {
                    let f = this.frames[i];
                    // console.log("frames + " + i + " " + f.p1.x + ' / ' + f.p1.y);
                    // console.log("frames + " + i + " " + f.p2.x + ' / ' + f.p2.y);
                    this.context.strokeRect(f.p1.x, f.p1.y,
                        f.p2.x - f.p1.x, f.p2.y - f.p1.y);
                }
                this.context.strokeRect(this.startPoint.x, this.startPoint.y, this.endPoint.x - this.startPoint.x, this.endPoint.y - this.startPoint.y);
            },
            // 这个不能写成成员函数，因为从后端拿过来不知道这是什么类型
            getRightTopPoint(frame) {
                let p1 = {
                    x: frame.p1.x,
                    y: frame.p2.y
                };
                let p2 = {
                    x: frame.p2.x,
                    y: frame.p1.y
                };
                return [p1, p2, frame.p1, frame.p2].reduce(function (p1, p2) {
                    function compareP(p1, p2) {
                        if (p1.x >= p2.x && p1.y <= p2.y) { // 这里的 “=” 可能处理来比较草率，但应付这个应该还行…
                            return 1;
                        } else if (p1.x <= p2.x && p1.y >= p2.y) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }

                    return compareP(p1, p2) > 0 ? p1 : p2;
                });
            },
        }
    }
</script >
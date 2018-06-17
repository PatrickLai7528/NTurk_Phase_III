<template >
    <el-container >
        <el-main class = "wrap" >
            <div class = "block" >
                <el-carousel id = "carousel" ref = "carousel" height = "36em" v-bind:autoplay = "false" arrow = "always"
                             v-on:change = "onIndexChange" >
                    <el-carousel-item id = "carouselItem" v-for = "item in imgNames.length" :key = "item" >
                        <div id = "canvasDiv" >
                            <canvas id = "canvas" class = "fl" >
                            </canvas >
                        </div >
                    </el-carousel-item >
                </el-carousel >
            </div >
        </el-main >
        <el-aside width = "300px" style = "alignment: center" >
            <ul style = "list-style-type:none" >
                <li v-for = "(val,index) in segments" >
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
                        <el-radio class = "text" v-model = "nowRating" :label = "1"
                                  v-on:change = "ratingChange" >我觉得可以</el-radio >
                    </div >
                    <div class = "next" >
                        <img src = "../../images/bad.svg" width = "300" height = "100" >
                        <el-radio class = "text" v-model = "nowRating" :label = "0"
                                  v-on:change = "ratingChange" >我觉得不行</el-radio >
                    </div >
                </div >
                <div v-if = "taskType === 'coverage'" >

                        <img src = "../../images/continued.svg" width = "300" height = "100" >
                        <el-radio class = "text" v-model = "nowRating" :label = "0"
                                  v-on:change = "ratingChange" >还有漏网之鱼</el-radio >

                    <div class = "next" >
                        <img src = "../../images/done.svg" width = "300" height = "100" >
                        <el-radio class = "text" v-model = "nowRating" :label = "1"
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

    .el-button {
	    width: 100px;
	    margin: 10px;
    }

    .tag {
	    background-color: #df4b26;
	    color: white;
    }

    .prompt {
	    margin-top: 250px;
    }

    #rate-bar {
	    margin-top: 20px;
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
    import SegmentDrawingStrategy from '../../js/strategy/SegmentDrawingStrategy.js'

    export default {
        data() {
            return {
                context: {},
                nowIndex: 0,
                annotation: {},
                annotationData: [],
                isNew: [],   //通过isNew列表来判断是否是新的
                segments: [
                    {
                        polygon: [
                            {
                                x: 0,
                                y: 0,
                            }
                        ],
                        tag: "this is tag",
                        color: "#a12311"
                    }
                ],
                segmentIndex: 0,
                pic: {},
                // 配置参数
                config: {
                    lineWidth: 1,
                    lineColor:
                        '#000000',
                    fillStyle:
                        "blue",
                    height:
                        510,
                    width:
                        510,
                    /*
                    beginLocationOfX:
                        0,
                    beginLocationOfY:
                        0,
                    */
                }
                ,
                flag: {
                    paintingEnabled: false,
                    //isNew: false,
                    canvasMoveUse: false,
                }
                ,
                // lastPoint: {
                //     x: 0,
                //     y: 0,
                // }
                percent: 0,
                number: 0,
                userClick: true,
                submitDisabled: "disabled",
                nowRating: 0,        //对当前图片的评分
                ratings: [],           //对这个合同所有的评分数组
                commitDisabled: 'disabled',
                isRequester: null,
                taskType: this.$route.params.taskType,
                imgNames: this.$store.getters.getImgNames,
                taskId: this.$route.params.taskId,
                dialogVisible: false,
                wrongAnnotation: {},
                wrongImg: '',
                canvasHtml: '<canvas id="canvas"></canvas>',
                tagHtml: '',
                wrongAnswerPairs: [],
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
                if (this.isRequester === false) {
                    _this.imgNames = _this.$store.getters.getImgNames.imgNames;
                }
                else {
                    _this.imgNames = _this.$store.getters.getImgNames;    //是发起者的时候这里略微有点不一样
                }

                _this.number = _this.imgNames.length;
                _this.percent = parseFloat(((_this.nowIndex + 1) / _this.number * 100).toFixed(1));
                _this.loadAnnotationList(_this.loadImageAndAnnotation);
                _this.setDialogContent();
            });
        },
        methods: {
            selectCanvas() {
                return document.getElementById("canvas");
            },
            setDialogContent(annotation) {
                // let id, doIt;
                // doIt = () => {
                let imageViewer, imageNameList = [], canvas, drawingStrategy, markingType, config;
                config = {strokeStyle: "black"};
                canvas = this.selectCanvas();
                imageNameList.push(this.wrongImg);
                imageViewer = new ImageViewer(canvas, imageNameList, "");
                imageViewer.drawCurrent();
                drawingStrategy = new SegmentDrawingStrategy();
                markingType = drawingStrategy.getMarkingTypeName();
                annotation[markingType].forEach((value, index, array) => {
                    drawingStrategy.drawThis(canvas.getContext("2d"), value, config);
                });
            },
            read() {
                this.dialogVisible = false;
            },
            commitRating() {
                //list里面的对象包含annotationId和rate
                function Verification(annotationId, rate) {
                    this.annotationId = annotationId;
                    this.rate = rate;
                }

                let verifications = [];   //这是最后的数组，所有的评分结果放在这个数组里

                for (let i = 0; i < this.ratings.length; i++) {
                    let nowInspection = new Verification(this.annotationData[i].annotationId, this.ratings[i]);
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

                let data = {
                    verifications: verifications,
                };

                console.log(data);
                _this.$http.post(path,
                    JSON.stringify(data),
                    {
                        headers: {
                            'Content-Type': 'application/json',
                            Authorization: _this.$store.getters.getToken
                        }
                    }).then(function (response) {
                    let res = response.data;
                    let forbidden = res.forbidden;

                    if (forbidden === true) {   //如果被禁赛了，输出禁赛信息
                        _this.forbiddenMessage();
                    }
                    else if (res.failedImgNames !== undefined && res.failedImgNames.length !== 0) {    //说明这次的回答有不正确的地方
                        _this.wrongImg = res.failedImgNames[0];   //把第一条挑出来
                        let wrongIndex = _this.findIndexByImg(_this.wrongImg);    //去查找index
                        _this.wrongAnswerPairs = _this.translateRate(_this.ratings[wrongIndex]);

                        _this.wrongImg = 'http://localhost:8086/image/' + _this.wrongImg;
                        _this.wrongAnnotation = _this.annotationData[wrongIndex];    //把wrongAnnotation找到
                        _this.showDialog(_this.wrongAnnotation);     //显示错误教程
                    }
                    else {
                        _this.canGoon(_this.showMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            findIndexByImg(img) {
                for (let i = 0; i < this.number; i++) {
                    if (this.imgNames[i] === img) {
                        return i;
                    }
                }

                return -1;
            },
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
            showDialog(annotation) {
                this.dialogVisible = true;   //显示错误提示
                this.setDialogContent(annotation);
            },
            canGoon(callback1) {
                let _this = this;
                let route = 'http://localhost:8086/get/taskId/' + this.taskId;
                this.$http.get(route, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    let taskInfo = response.data;
                    console.log(taskInfo.verifyQuality);
                    if (_this.taskType === 'grade') {
                        if (taskInfo.verifyQuality > 0) {
                            callback1();
                        }
                        else {
                            _this.$router.push({path: '/profile'});
                        }
                    }
                    else if (_this.taskType === 'coverage') {
                        if (taskInfo.verifyCoverage > 0) {
                            callback1();
                        }
                        else {
                            _this.$router.push({path: '/profile'});
                        }
                    }
                }).catch(function (error) {                 //理论上来说不会出现这种情况
                    console.log(error);
                });
            },
            forbiddenMessage() {
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
            showMessage() {        //显示要继续做的提示并且在点击确认后跳到下一个界面去
                let _this = this;
                this.$confirm('不够过瘾，再来一组^_^', '温馨提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let contractId = '';
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
                        console.log(imgNames);
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
            },
            loadAnnotationList(callback) {            //现在加载逻辑非常简单  annotationId都有，只要按照顺序push就好了
                let _this = this;
                let whatfor = 3;
                if (_this.taskType === 'coverage') {
                    whatfor = 2;
                }
                else if (_this.taskType === 'grade') {
                    whatfor = 1;
                }

                for (let i = 0; i < _this.imgNames.length; i++) {
                    let route = "http://localhost:8086/segmentAnnotation/imgName/" + _this.imgNames[i] + "/whatFor/" + whatfor;
                    this.$http.get(route, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                        if (response.status === 204) {
                            _this.segments = [];
                            _this.annotation = {
                                'imgName': _this.imgNames[i],
                                'segment': _this.segments
                            };
                            _this.$set(_this.annotationData, i, _this.annotation);

                            if (_this.allLoad()) {
                                callback();
                            }
                        }
                        else {
                            let temp = response.data;

                            if (_this.taskType === 'grade') {
                                for (let item of temp.segments) {
                                    item.color = '#64bc67'
                                }

                                temp.segment.color = '#C0392B';
                            }

                            temp.segments.push(temp.segment);

                            if (_this.taskType !== 'grade') {         //这里要注意非grade的逻辑是一样的
                                for (let item of temp.segments) {
                                    item.color = '#C0392B';
                                }
                            }
                            _this.annotation = {
                                'imgName': _this.imgNames[i],
                                'segment': temp.segments,
                                'annotationId': temp.annotationId,
                            };

                            _this.$set(_this.annotationData, i, _this.annotation);

                            if (_this.allLoad()) {
                                callback();
                            }
                        }

                    }).catch(function (error) {             //FIXME:因为发起者看有一些标注是没有的，所以创建一个空对象push进去  现在开起来不知道这个逻辑可不可行
                        console.log(error);
                    })
                }
            },
            allLoad() {
                if (this.annotationData.length !== this.imgNames.length) {
                    return false;
                }

                for (let i = 0; i < this.annotationData.length; i++) {
                    if (this.annotationData[i] === undefined) {
                        return false;
                    }

                    return true;
                }
            },
            onIndexChange: function (newIndex, oldIndex) {
                this.nowIndex = newIndex;
                this.nowRating = 5;
                this.commitDisabled = 'disabled';
                this.loadWhenChange(newIndex);
            },
            loadImageAndAnnotation() {
                let _this = this;
                this.pic = new Image();
                this.pic.src = "http://localhost:8086/image/" + this.imgNames[this.nowIndex];
                // this.pic.addEventListener('load', function () {
                this.pic.onload = function () {
                    // draw(this);
                    const temp = document.getElementById('carousel');
                    const canvasDiv = temp.querySelectorAll('#canvasDiv').item(_this.nowIndex);
                    const canvas = canvasDiv.querySelector('#canvas');
                    _this.context = canvas.getContext('2d');

                    let ratio = this.width / this.height;
                    _this.config.height = document.getElementById('carousel').offsetHeight;
                    _this.config.width = _this.config.height * ratio;
                    canvas.height = _this.config.height;
                    canvas.width = _this.config.width;

                    _this.context.drawImage(this, 0, 0, _this.config.width, _this.config.height);

                    // 必须先img，然后再annotation，不然img就覆盖annotation了…
                    _this.loadAnnotation();
                }
            },
            /**
             * loading-related methods. (mouse-event listeners are down below)
             * */
            loadAnnotation() {
                this.annotation = this.annotationData[this.nowIndex];
                this.segments = this.annotation.segment;
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
                this.context.clearRect(0, 0, this.config.width, this.config.height);

                const childNodes = canvasDiv.childNodes;
                for (let i = 0; i < childNodes.length; i++) {
                    // 又是这个循环变长的坑……childNodes中间如果改了就会不可控…
                    const cn = childNodes[i];
                    if (cn.tagName === 'DIV') {
                        canvasDiv.removeChild(cn);
                        i--;
                    }
                }
                this.context.drawImage(this.pic, 0, 0, this.config.width, this.config.height);
                for (let i = 0; i < this.segments.length; i++) {
                    const s = this.segments[i];
                    // this.config.lineColor = s.color;
                    // this.context.lineStyle = this.config.color;
                    this.context.lineWidth = this.config.lineWidth;
                    if (this.segments[i].color !== undefined && this.segments[i].color !== '') {
                        this.context.strokeStyle = this.segments[i].color;
                    }
                    else {
                        this.context.strokeStyle = this.color;
                    }

                    this.drawArea(s.polygon);
                    this.addTag(s, i);
                }
                // this.context.strokeStyle = this.color;
            },
            drawArea(polygon) {
                this.context.save();
                this.context.beginPath();
                this.context.moveTo(polygon[0].x, polygon[0].y);
                for (let p = 1; p < polygon.length; p++) {
                    this.context.lineTo(polygon[p].x, polygon[p].y);
                    this.context.stroke();
                }
                this.context.fillStyle = this.config.fillStyle;
                this.context.globalAlpha = 0.3;
                this.context.fill();
                this.context.closePath();
                this.context.restore();
                this.segmentIndex++;
            },
            addTag(segment, index) { // the interface may be simplified. (no “frame” parameter)
                const p = segment.polygon[0];
                const canvas = document.querySelector('#canvas');
                const cssString = "position:absolute; white-space: nowrap;" + "top:" + (p.y + canvas.offsetTop) + "px;" + "left:" + p.x + "px;";

                const htmlString = `<el-tag style="background: #e5e9f2">標記${index + 1}</el-tag>`;
                let div = document.createElement('div');
                div.id = 'div' + index;
                div.innerHTML = htmlString;
                div.setAttribute('style', cssString);
                const temp = document.getElementById('carousel');
                const canvasDiv = temp.querySelectorAll('#canvasDiv').item(this.nowIndex);
                canvasDiv.appendChild(div);
                // this.canCommit();
            },
            /**
             * xhr-related methods
             * */
            loadWhenChange(newIndex) {
                this.nowIndex = newIndex;
                this.segmentIndex = 0;
                this.loadImageAndAnnotation();
                this.percent = parseFloat(((newIndex + 1) / this.number * 100).toFixed(1));
            },
        }
    }
</script >
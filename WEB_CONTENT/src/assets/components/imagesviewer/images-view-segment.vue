<template>
    <el-container>
        <el-main class="wrap">
            <div class="block">
                <el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always"
                             v-on:change="onIndexChange">
                    <el-carousel-item id="carouselItem" v-for="item in imgNames.length" :key="item">
                        <div id="canvasDiv">
                            <canvas id="canvas" class="fl">
                            </canvas>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </el-main>
        <el-aside width="300px" style="alignment: center">
            <ul style="list-style-type:none">
                <li v-for="(val,index) in segments">
                    <el-popover
                            placement="right"
                            width="150"
                            trigger="click"
                    >
                        <el-input
                                type="textarea"
                                :rows="2"
                                v-model="val.tag"
                                :disabled="true"
                        >
                        </el-input>
                        <el-button slot="reference" class="tag">標記{{index+1}}</el-button>
                        <el-button @click="deleteTag(index)" :disabled="true">刪除</el-button>
                    </el-popover>
                </li>
            </ul>
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

<style>
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

    .next{
        margin-top: 20px;
    }

    .text{
        font-family: Arial, KaiTi, STXihei, "华文细黑", "Microsoft YaHei", "微软雅黑";
        font-weight:600;
    }
</style>

<script>
    import UserUtils from '../../js/utils/UserUtils.js'
    export default {
        data() {
            return {
                context: {},
                imgNames: [],
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
                taskId: this.$route.params.taskId,
                contractId: this.$route.params.contractId,
                mandatoryTime:this.$route.params.mandatoryTime,   //表示还要再评几次
                nowRating: 0,        //对当前图片的评分
                ratings: [],           //对这个合同所有的评分数组
                commitDisabled: 'disabled',
                isRequester: null,
                annotationIds: this.$store.getters.getAnnotationIds,     //可以得到所有标注的编号，再通过所有标注的编号去找到这个标注和这个标注对应的imgName
                taskType: this.$route.params.taskType,
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
                _this.number = _this.$store.getters.getAnnotationIds.length;
                _this.percent = parseFloat(((_this.nowIndex + 1) / _this.number * 100).toFixed(1));
                let promise = new Promise(function (resolve) {
                    _this.loadAnnotationList();
                    resolve();
                });

                promise.then(function () {
                    _this.loadImageAndAnnotation();            //使用promise处理从后端取得和加载的同步关系
                });
            });
        },
        watch:{                        //路由参数变化重新加载界面
            $route: function (to,from) {
                if(to.name === 'viewsegment'){
                    let _this = this;
                    this.taskId = this.$route.params.taskId;
                    this.ratings = [];
                    this.imgNames = [];
                    this.nowRating = 0;
                    this.annotation = {};
                    _this.number = _this.$store.annotationIds.length;
                    _this.percent = parseFloat(((_this.nowIndex + 1) / _this.number * 100).toFixed(1));
                    let promise = new Promise(function (resolve) {
                        _this.loadAnnotationList();
                        resolve();
                    });

                    promise.then(function () {
                        _this.loadImageAndAnnotation();            //使用promise处理从后端取得和加载的同步关系
                    });
                }
            }
        },
        methods: {
            commitRating(){
                //list里面的对象包含annotationId和rate
                function Inspection(annotationId,rate){
                    this.annotationId = annotationId;
                    this.rate = rate;
                }

                let inspections = [];   //这是最后的数组，所有的评分结果放在这个数组里
                console.log(this.annotationData[0]);
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
                        _this.$router.push({name: 'viewsegment',params:{taskId:_this.taskId,contractId:contractId,mandatoryTime:_this.mandatoryTime}});
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
                if(this.ratings.length === this.imgNames.length){
                    this.commitDisabled = false;
                }
                else{
                    this.commitDisabled = 'disabled';
                }
            },
            ratingChange: function(score){
                this.ratings[this.nowIndex] = score;
                this.canCommit();
            },
            loadAnnotationList(){            //现在加载逻辑非常简单  annotationId都有，只要按照顺序push就好了
                let _this = this;
                for(let annotationId of this.annotationIds){
                    let route = "http://localhost:8086/segmentAnnotation/id" + annotationId;
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
                for (let i = 0; i < this.imgNames.length; i++) {
                    if (imgSrc === this.imgNames[i]) {
                        return i;
                    }
                }
            },
            onIndexChange: function (newIndex, oldIndex) {
                this.nowIndex = newIndex;
                this.nowRating = 0;
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
                let _this = this;
                this.$http.get('http://localhost:8086/segmentAnnotation/contractId/' + _this.contractId + '/imgName/' + this.imgNames[this.nowIndex], {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    if (response.status === 200) {
                        _this.annotation = response.data;
                        _this.segments = _this.annotation.segments;
                        _this.annotationData[_this.nowIndex] = _this.annotation;    //如果是评分，不会有没有做完的annotation
                        _this.initialDraw();
                    }
                }).catch(function (error) {
                    _this.segments = [];
                    _this.annotation = {
                        'imgName': _this.imgNames[_this.nowIndex],
                        'segments': _this.segments
                    };
                    _this.isNew[_this.nowIndex] = true;
                    _this.annotationData[_this.nowIndex] = _this.annotation;    //如果是评分，不会有没有做完的annotation
                    _this.initialDraw();
                    console.log(error);
                })
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
                    this.config.lineColor = s.color;
                    this.context.lineStyle = this.config.color;
                    this.context.lineWidth = this.config.lineWidth;
                    this.drawArea(s.polygon);
                    this.addTag(s, i);
                }
                this.context.strokeStyle = this.color;
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

                const htmlString = "<el-tag style='background: #e5e9f2'>標記" + (index + 1) + " </el-tag>";
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
</script>
<template>

    <el-container>
        <el-main class="wrap">
            <div class="block">
                <el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always"
                             v-on:change="onIndexChange">
                    <el-carousel-item id="carouselItem" v-for="item in imgNames.length" :key="item">
                        <div id="canvasDiv">
                            <canvas
                                    id="canvas"
                                    class="fl"
                            >
                            </canvas>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </el-main>
        <el-aside width="300px" style="alignment: center">
            <!--<el-button @click="updateThisAnnotation()">更新</el-button>-->
            <!--<el-button @click="previousPictureAndUpdateThisAnnotation()">上一個</el-button>-->
            <!--<el-button @click="nextPictureAndUpdateThisAnnotation()">下一個</el-button>-->
            <!--<el-button @click="clearThisAnnotation()">清除</el-button>-->
            <!--<el-button @click="undoThisAnnotation()">UNDO</el-button>-->
            <ul style="list-style-type:none">
                <li>
                    <!--<el-button @click="handleCommit(nowIndex)" :disabled=submitDisabled>提交</el-button>-->
                </li>
                <li v-for="(val,index) in frames">
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
                canvasHeight: 0,
                canvasWidth: 0,
                imgNames: [],
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
                        tag: ""
                        // color: "#"
                    }
                ],
                frameIndex: 0,
                nowIndex: 0,
                pic:
                    {}
                ,
                color: "#df4b26",
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
                taskId: this.$route.params.taskId,
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
        watch:{
            $route: function (to,from) {
                if(to.name === 'viewframe'){
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

                let _this = this;
                this.$http.post('http://localhost:8086/inspect',
                    JSON.stringify(inspections),
                    {headers: {'Content-Type': 'application/json',Authorization:this.$store.getters.getToken}}).then(function (response){
                    _this.showMessage();
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
                this.$confirm('不够过瘾，再来一组^_^', '温馨提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let contractId = '';
                    _this.$http.get('http://localhost:8086/inspect/enterInspection/' + _this.taskId, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                        let anno = response.data;   //得到contractId
                        _this.$store.commit('changeAnnotationIds',anno);
                        _this.$router.push({name: 'viewframe'});
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
                    let route = "http://localhost:8086/frameAnnotation/id" + annotationId;
                    this.$http.get(route,{headers:{Authorization: _this.$store.getters.getToken}}).then(function(response){
                        _this.annotation = response.data;
                        _this.imgNames.push(_this.annotation.imgName);
                        _this.annotationData.push(_this.annotation);
                    }).catch(function (error) {
                        console.log(error);
                    })
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
                this.context.strokeStyle = this.color;
                this.context.lineWidth = 5;
                for (let i = 0; i < this.frames.length; i++) {
                    const f = this.frames[i];
                    this.context.strokeRect(f.p1.x, f.p1.y,
                        f.p2.x - f.p1.x, f.p2.y - f.p1.y);
                    this.addTag(f, i);
                }
            },
            addTag(frame, index) { // the interface may be simplified. (no “frame” parameter)
                const p = this.getRightTopPoint(frame);
                const canvas = document.querySelector('#canvas');
                const cssString = "position:absolute; white-space: nowrap;" + "top:" + (p.y + canvas.offsetTop) + "px;" + "left:" + p.x + "px;";

                const htmlString = "<el-tag style='background: #e5e9f2'>標記" + (index + 1) + " </el-tag>";
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
</script>
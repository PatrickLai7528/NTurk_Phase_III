<template>
    <el-container>
        <el-main class="wrap">
            <div class="block">
                <div id="canvasDiv">
                    <div v-html="canvasHtml">
                        {{canvasHtml}}
                    </div>
                    <div v-html="tagHtml">{{tagHtml}}</div>
                </div>
                <div id="previous-button">
                    <el-button icon="el-icon-arrow-left" circle @click="previous()"></el-button>
                </div>
                <div id="next-button">
                    <el-button icon="el-icon-arrow-right" circle @click="next()"></el-button>
                </div>
            </div>
        </el-main>
        <el-aside width="300px" style="alignment: center">
            <ul style="list-style-type:none">
                <li v-for="(val,index) in tagText">
                    <el-popover
                            placement="right"
                            width="150"
                            trigger="click"
                    >
                        <el-input
                                type="textarea"
                                :rows="2"
                                v-model="val.text"
                                :disabled="true"
                        >
                        </el-input>
                        <el-button slot="reference" class="tag">標記{{index+1}}</el-button>
                        <el-button @click="deleteTag(index)" :disabled="true">刪除</el-button>
                    </el-popover>
                </li>
            </ul>
            <div v-if="isRequester === false">
                <div id="prompt">请点击星级进行评分：</div>
                <el-rate
                        id="rate-bar"
                        v-model="nowRating"
                        :colors="['#99A9BF', '#F7BA2A', '#FF6347']"
                        v-on:change="ratingChange"
                >
                </el-rate>
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

    #prompt {
        margin-top: 250px;
    }

    #rate-bar {
        margin-top: 20px;
    }

    #commit-button {
        margin-top: 50px;
    }
</style>

<script>
<<<<<<< HEAD
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
                nowRating: 0,        //对当前图片的评分
                ratings: [],           //对这个合同所有的评分数组
                commitDisabled: 'disabled',
                isRequester: null
            }
        },
        mounted() {
            let _this = this;
            this.$nextTick(function () {
                _this.getImgNames();
                // _this.initDraw()
                // _this.setCanvasStyle()
                this.isRequester = UserUtils.isRequester(this);
            })
        },
        methods: {
            commitRating(){
                //to-do
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
            getImgNames() {
                let _this = this;
                let route = 'http://localhost:8086/tasks/id/' + this.taskId;
                this.$http.get(route, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                    console.log(response.data.imgNames);
                    let temp = response.data.imgNames;
                    for (let i = 0; i < temp.length; i++) {
                        console.log(temp[i]);
                        _this.imgNames.push(temp[i]);
                    }
                    _this.loadImageAndAnnotation();
                    _this.number = _this.imgNames.length;
                    _this.percent = parseFloat(((_this.nowIndex + 1) / _this.number * 100).toFixed(1));
                    // _this.loadFrontList(function () {    //进行初始化检查
                    // _this.canCommit();
                    // })
                }).catch(function (error) {
                    console.log(error);
                });
            },
            loadFrontList() {   //用这种方式来初始化前端的annotation数组
                let _this = this;
                console.log(_this.imgNames);
                for (let path of _this.imgNames) {
                    let route = 'http://localhost:8086/segmentAnnotation/taskId/' + _this.taskId + '/imgName/' + path;
                    this.$http.get(route, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
                        //let tem = eval(response.data);
                        //_this.testData = response.data;
                        let index = _this.getIndex(path);
                        _this.$set(_this.annotationData, index, response.data);    //在组件中不能使用Vue.set来进行注册，应该用this.$set方法
                        //_this.annotationData.splice(index,1,response.data);   //必须用splice方法vue才能检测到数组元素的变化
                        _this.isNew[index] = false;//不是新的
                        console.log("ha");
                        console.log(_this.annotationData);

                    }).catch(function (error) { //如果后端没有数据记录要自己造一个空的标注对象push进去
                        let index = _this.getIndex(path);
                        //在这里最好不要改变_this.segments   先进行尝试
                        let tempAnnotation = {
                            'imgName': _this.imgNames[index],
                            'segments': [],
                        };
                        _this.$set(_this.annotationData, index, tempAnnotation);  //在前端注册index
                        _this.isNew[index] = true;
                        console.log(_this.annotationData);
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
                        _this.initialDraw();
                    }
                }).catch(function (error) {
                    _this.segments = [];
                    _this.annotation = {
                        'imgName': _this.imgNames[_this.nowIndex],
                        'segments': _this.segments
                    };
                    _this.isNew[_this.nowIndex] = true;
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
=======
	import ImageViewer from '../../js/ImageViewer.js'
	import AnnotationViewer from '../../js/AnnotationViewer.js'
	import SegmentDrawingStrategy from '../../js/strategy/SegmentDrawingStrategy.js'

	export default {
		data() {
			return {
				viewer: {},
				tagText: [{}],
				tagHtml: "",
				canvasHtml: `<canvas id="canvas" class="fl" width="600" height="400"></canvas>`,
				taskId: this.$route.params.taskId,
				contractId: this.$route.params.contractId,
				canvas: {}
			}
		},
		mounted() {
			this.$nextTick(function () {
				this.canvas = document.getElementById("canvas");
				this.getImageNames();
			})
		},
		methods: {
			getImageNames() {
				let route = 'http://localhost:8086/tasks/id/' + this.taskId;
				let header = {headers: {Authorization: this.$store.getters.getToken}};
				this.$http.get(route, header)
					.then((response) => {
						let imageViewer = new ImageViewer(this.canvas, response.data.imgNames, "http://localhost:8086/image/");
						// imageViewer.drawCurrent();
						this.viewer = new AnnotationViewer(new SegmentDrawingStrategy(), imageViewer, 'http://localhost:8086/segmentAnnotation/contractId/', this.contractId, this.$http);
						this.viewer.drawCurrent(header, this.doThisEveryImageUpdate);
						// this.trySomething();
					})
					.catch(function (error) {
						console.log(error);
					})
			},
			doThisEveryImageUpdate() {
				this.tagHtml = this.viewer.shareTag();
				this.tagText = this.viewer.shareTagText();
			},
			next() {
				this.viewer.drawNext(this.doThisEveryImageUpdate);
			},
			previous() {
				this.viewer.drawPrevious(this.doThisEveryImageUpdate);
			}
		}
	}
>>>>>>> 161250051_refactor
</script>
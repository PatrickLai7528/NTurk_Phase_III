<template>

    <el-container>
        <el-main class="wrap">
            <div class="block">
                <el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always"
                             v-on:change="onIndexChange">
                    <el-carousel-item id="carouselItem" v-for="item in imgNames.length" :key="item">
                        <div id="canvasDiv">
                            <canvas id="canvas" class="fl" width="600"
                                    height="400">
                            </canvas>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </el-main>
        <el-aside width="300px" style="alignment: center">
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

</style>

<script>
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
						p1: {x: 0, y: 0},
						p2: {x: 0, y: 0},
						tag: ""
					}
				],
				frameIndex: 0,
				nowIndex: 0,
				pic: {},
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
				submitDisabled: "disabled",
				taskId: this.$route.params.taskId,
				contractId: this.$route.params.contractId
			}
		},
		mounted() {
			this.$nextTick(() => {
				/*
                // const canvas = document.querySelector('#canvas');
                _this.context = canvas.getContext('2d');
                */
				this.getImgNames();
			})
		},
		methods: {
			doWhileGetImageNamesSuccess(response) {
				let temp = response.data.imgNames;
				for (let i = 0; i < temp.length; i++) {
					//console.log(temp[i])
					this.imgNames.push(temp[i]);
				}
				this.loadImageAndAnnotation();
				this.number = this.imgNames.length;
				for (let j = 0; j < this.number; j++) {
					this.isNew.push(true);
				}
				this.percent = parseFloat(((this.nowIndex + 1) / this.number * 100).toFixed(1));
			},
			getImgNames() {
				let _this = this;
				let route = 'http://localhost:8086/tasks/id/' + this.taskId;
				let header = {headers: {Authorization: this.$store.getters.getToken}};
				//
				//{headers:{Authorization:that.$store.getters.getToken}
				this.$http.get(route, header)
					.then(this.doWhileGetImageNamesSuccess)
					.catch(function (error) {
						console.log(error);
					});
			},
			onIndexChange: function (newIndex, oldIndex) {
				this.nowIndex = newIndex;
				this.loadWhenChange(newIndex);
			},
			loadFrontList() {   //用这种方式来初始化前端的annotation数组
				let _this = this;
				for (let path of _this.imgNames) {
					let route = 'http://localhost:8086/frameAnnotation/taskId/' + _this.taskId + '/imgName/' + path;
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
						_this.frames = [];
						let tempAnnotation = {
							'imgName': _this.imgNames[index],
							'frames': _this.frames
						};
						_this.$set(_this.annotationData, index, tempAnnotation);  //在前端注册index
						_this.isNew[index] = true;
						console.log("he");
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
			/**
			 * loading-related methods. (mouse-event listeners are down below)
			 * */
			loadImageAndAnnotation() {
				let _this = this;
				_this.pic = new Image();
				_this.pic.src = "http://localhost:8086/image/" + _this.imgNames[this.nowIndex];
				console.log("src : " + _this.src);
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
				let _this = this;
				this.$http.get('http://localhost:8086/frameAnnotation/contractId/' + _this.contractId + '/imgName/' + this.imgNames[this.nowIndex], {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
					_this.annotation = response.data;
					_this.frames = _this.annotation.frames;
					_this.initialDraw();
					_this.isNew[_this.nowIndex] = false;    //如果是从后端加载的标注信息，isNew为false
				}).catch(function (error) {
					_this.frames = [];
					_this.annotation = {
						'imgName': _this.imgNames[_this.nowIndex],
						'frames': _this.frames
					};
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
				console.log("find me " + typeof canvas);
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
			/**
			 * xhr-related methods
			 * */
			loadWhenChange(newIndex) {
				this.nowIndex = newIndex;
				this.loadImageAndAnnotation();
				this.percent = parseFloat(((newIndex + 1) / this.number * 100).toFixed(1));
			},
		}
	}
</script>
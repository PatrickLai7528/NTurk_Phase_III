<template>
    <el-container>
        <el-main class="wrap">
            <el-header>
                <el-progress :text-inside="true" :stroke-width="18" v-bind:percentage=percent
                             status="success"></el-progress>
            </el-header>
            <div class="block">
                <el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always"
                             v-on:change="onIndexChange">
                    <el-carousel-item id="carouselItem" v-for="item in imgNames.length" :key="item">
                        <div id="canvasDiv">
                            <canvas
                                    id="canvas"
                                    class="fl"
                                    @mousedown="canvasDown($event)"
                                    @mouseup="canvasUp($event)"
                                    @mousemove="canvasMove($event)"
                                    @touchstart="canvasDown($event)"
                                    @touchend="canvasUp($event)"
                                    @touchmove="canvasMove($event)"
                            >
                            </canvas>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </el-main>
        <el-aside width="300px" style="alignment: center">
            <ul style="list-style-type:none">
                <li>
                    <el-button @click="previousImg()">上一個</el-button>
                    <el-button @click="nextImg()">下一個</el-button>
                </li>
                <li>
                    <el-button @click="handleCommit(nowIndex)" :disabled=submitDisabled>提交</el-button>
                    <el-button @click="onCancel()">取消</el-button>
                </li>
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
                        >
                        </el-input>
                        <el-button slot="reference" class="tag">標記{{index+1}}</el-button>
                        <el-button @click="deleteTag(index)">刪除</el-button>
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
			}
		},
		mounted() {
			let _this = this;
			this.$nextTick(function () {
				_this.getImgNames();
				// _this.initDraw()
				// _this.setCanvasStyle()
			})
		},
		methods: {
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
					_this.loadFrontList(function () {    //进行初始化检查
						_this.canCommit();
					})
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
			handleCommit(nowIndex) {
				let flag = this.canCommit();
				if (flag) {
					this.putOrPost(nowIndex);
					let route = 'http://localhost:8086/contract/complete/' + this.taskId;
					let _this = this;
					let data = {};
					_this.$http.put(route, data, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
						console.log("finish");
					}).catch(function (error) {
						console.log(error);
					});
					this.messageHandler();
					this.$router.push({path: '/profile'});
				}
				else {
					this.putOrPost(nowIndex);
					this.badMessage();
				}
			},
			messageHandler() {
				this.$message({
					message: '任务已经提交，请安心等待结果和奖励^_^',
					type: 'success'
				})
			},
			badMessage() {
				this.$alert('您还没有完成这个任务', '系统警告', {
					confirmButtonText: '确定'
				});
			},
			canCommit() {
				//首先判断当前的annotation是否为空，然后在判断别的
				if (this.annotation.segments.length === 0) {
					this.submitDisabled = 'disabled';
					console.log("haha");
					console.log(this.annotationData);
					return false;
				}

				for (let e of this.annotationData) {
					if (e.imgName !== this.annotation.imgName) {
						if (e.segments.length === 0) {
							this.submitDisabled = 'disabled';
							console.log("hehe");
							console.log(this.annotationData);
							return false;
						}
					}
				}

				console.log("enen");
				console.log(this.annotationData);
				this.submitDisabled = false;
				return true;
			},
			/*
            previousImg() {
                if (this.nowIndex > 0) {
                    this.nowIndex--;
                    for (let i = 0; i < this.segments.length; i++) {
                        this.deleteTag(i);
                    }
                    this.segmentIndex = 0;
                    this.loadImageAndAnnotation();
                }
            },
            nextImg() {
                if (this.nowIndex < this.imgNames.length - 1) {
                    this.nowIndex++;
                    for (let i = 0; i < this.segments.length; i++) {
                        this.deleteTag(i);
                    }
                    this.segmentIndex = 0;
                    this.loadImageAndAnnotation();
                }
            },
            */
			onIndexChange: function (newIndex, oldIndex) {
				// oldIndex===-1是一开始加载走马灯的情况
				if (oldIndex !== -1) {
					// 阻止从第一张直接切换到最后一张
					if (oldIndex === 0 && newIndex === this.imgNames.length - 1 && this.userClick === true) {
						this.userClick = false;
						this.$refs.carousel.setActiveItem(0);
					}
					else if (newIndex === 0 && oldIndex === this.imgNames.length - 1 && this.userClick === false) {
						this.userClick = true;
					}

					// 阻止从最后一张直接切换到第一张
					else if (oldIndex === this.imgNames.length - 1 && newIndex === 0 && this.userClick === true) {
						this.userClick = false;
						this.$refs.carousel.setActiveItem(this.imgNames.length - 1);
					}
					else if (newIndex === this.imgNames.length - 1 && oldIndex === 0 && this.userClick === false) {
						this.userClick = true;
					}

					// 正常情况
					else {
						if (this.userClick === true) {
							if (this.segments.length === 0 && newIndex > oldIndex) {
								this.userClick = false;
								this.$refs.carousel.setActiveItem(oldIndex);
							}
							else {
								this.saveAndLoad(oldIndex, newIndex);
							}
						}
						else {
							this.userClick = true;
						}
					}
				}
			},
			async saveAndLoad(oldIndex, newIndex) {
				await this.putOrPost(oldIndex);
				await this.loadWhenChange(newIndex);
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
				this.$http.get('http://localhost:8086/segmentAnnotation/taskId/' + _this.taskId + '/imgName/' + this.imgNames[this.nowIndex], {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
					if (response.status === 200) {
						_this.annotation = response.data;
						_this.segments = _this.annotation.segments;
						// _this.segments = _this.segments.concat(_this.annotation.segments);
						// for (let i = 0; i < _this.segments.length; i++) {
						//     _this.segments[i].polygon = _this.segments[i].polygon.concat(_this.annotation.segments[i].polygon);
						// }
						// console.log(_this.segments);
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

				// this.context.clearRect(0, 0, this.config.width, this.config.height);
				// draw
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
				// console.log(polygon);
				// 目前只能想到把所有的重画一遍

				// this.context.drawImage(this.pic, 0, 0);
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
				// const p = frames[frames.length - 1].getRightTopPoint();
				const p = segment.polygon[0];
				// console.log("frame = " + "p1:" + frame.p1.x + "y:" + frame.p1.y);
				// console.log("frame = " + "p2:" + frame.p2.x + "y:" + frame.p2.y);
				// console.log("p = " + "x:" + p.x + "y:" + p.y);
				// console.log(canvas.getBoundingClientRect().top);
				// console.log(canvas.offsetTop);
				const canvas = document.querySelector('#canvas');
				// console.log(canvas == null);
				const cssString = "position:absolute; white-space: nowrap;" + "top:" + (p.y + canvas.offsetTop) + "px;" + "left:" + p.x + "px;";

				const htmlString = "<el-tag style='background: #e5e9f2'>標記" + (index + 1) + " </el-tag>";

				// console.log("htmlString + " + htmlString);
				// console.log("cssString + " + cssString);
				//
				let div = document.createElement('div');
				div.id = 'div' + index;
				div.innerHTML = htmlString;
				div.setAttribute('style', cssString);
				// end

				const temp = document.getElementById('carousel');
				const canvasDiv = temp.querySelectorAll('#canvasDiv').item(this.nowIndex);
				canvasDiv.appendChild(div);
				this.canCommit();
			},
			canvasMove(e) {
				if (this.canvasMoveUse) {
					console.log('canvasMove');
					const t = e.target;
					let canvasX;
					let canvasY;
					canvasX = e.offsetX;
					canvasY = e.offsetY;
					this.context.lineTo(canvasX, canvasY);
					this.context.stroke();
					// if (Math.abs(canvasX - this.lastPoint.x) > 3 && Math.abs(canvasY - this.lastPoint.y) > 3) {
					this.segments[this.segmentIndex].polygon.push({
						x: canvasX,
						y: canvasY,
					})
					// }
					// this.lastPoint.x = canvasX;
					// this.lastPoint.y = canvasY;
				}
			},
			// mouseup
			canvasUp(e) {
				this.flag.paintingEnabled = false;
				this.context.closePath();
				this.context.save();
				this.context.globalAlpha = 0.3;
				this.context.fillStyle = this.config.fillStyle;
				this.context.fill();
				this.context.restore();
				this.canvasMoveUse = false;
				const canvasX = e.offsetX;
				const canvasY = e.offsetY;

				// if (Math.abs(canvasX - this.lastPoint.x) > 3 && Math.abs(canvasY - this.lastPoint.y) > 3) {
				this.segments[this.segmentIndex].polygon.push({
					x: canvasX,
					y: canvasY,
				})
				// console.log("it is going to add tag");
				// this.addTag(this.segments[this.segmentIndex], this.segments.length);
				// console.log(this.segments[this.segmentIndex].polygon);
				// console.log(this.segmentIndex)
				if (this.segmentIndex < this.segments.length) {
					this.segmentIndex++;
				}
				this.addTag(this.segments[this.segments.length - 1], this.segments.length - 1);
				// console.log("segment index = " + this.segmentIndex);
			},
			// mousedown
			canvasDown(e) {
				this.flag.paintingEnabled = true;
				console.log('canvasDown')
				this.canvasMoveUse = true;
				// client是基于整个页面的坐标
				// offset是cavas距离顶部以及左边的距离
				const canvasX = e.offsetX;
				const canvasY = e.offsetY;
				this.segments.push({
					polygon: [
						{
							x: canvasX,
							y: canvasY,
						}
					],
				});
				// this.lastPoint.x = canvasX;
				// this.lastPoint.y = canvasY;
				this.context.beginPath();
				this.context.moveTo(canvasX, canvasY);
				console.log('moveTo', canvasX, canvasY);
			},
			/**
			 * xhr-related methods
			 * */
			loadWhenChange(newIndex) {
				this.nowIndex = newIndex;
				//this.percent =  parseFloat(((newIndex+1)/this.number * 100).toFixed(1));
				for (let i = 0; i < this.segments.length; i++) {

					// this.deleteTag(i);
				}
				this.segmentIndex = 0;
				this.loadImageAndAnnotation();
				this.percent = parseFloat(((newIndex + 1) / this.number * 100).toFixed(1));
			},
			putOrPost(nowIndex) {
				// let method = isNew ? 'POST' : 'PUT';
				let _this = this;
				if (this.isNew[nowIndex]) {
					console.log(_this.img, _this.annotation);
					this.$http.post('http://localhost:8086/segmentAnnotation/taskId/' + _this.taskId, _this.annotation, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
						//window.alert('save!');
						_this.isNew[nowIndex] = false;
						//新增之后重新加载
						_this.$http.get('http://localhost:8086/segmentAnnotation/taskId/' + _this.taskId + '/imgName/' + _this.imgNames[nowIndex], {headers: {Authorization: _this.$store.getters.getToken}}).then(function (res) {
							_this.$set(_this.annotationData, nowIndex, res.data);
							_this.canCommit();
						}).catch(function (err) {
							console.log(err);
						})
					}).catch(function (error) {
						console.log(error);
					})
				} else {
					this.$http.put('http://localhost:8086/segmentAnnotation', _this.annotation, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
						//window.alert('save!');
						_this.isNew[nowIndex] = false;
						//新增之后重新加载
						_this.$http.get('http://localhost:8086/segmentAnnotation/taskId/' + _this.taskId + '/imgName/' + _this.imgNames[nowIndex], {headers: {Authorization: _this.$store.getters.getToken}}).then(function (res) {
							_this.$set(_this.annotationData, nowIndex, res.data);
							_this.canCommit();
							console.log(_this.annotationData);   //debug使用
						}).catch(function (err) {
							console.log(err);
						})
					}).catch(function (error) {
						console.log(error);
					})
				}
			},
			deleteTag(i) {
				for (let k = i; k < this.segments.length - 1; k++) {
					this.segments[k].tag = this.segments[k + 1].tag;
				}
				this.segments.splice(i, 1);
				// console.log("segments.length :" + this.segments.length);
				this.initialDraw();
				this.canCommit();
			},
			onCancel() {
				this.$router.push({path: '/tasklobby'});
			}

		}
	}
</script>
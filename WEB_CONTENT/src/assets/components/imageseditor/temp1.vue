<template>

    <el-container>
        <el-main class="wrap">
            <el-header>
                <el-progress :text-inside="true" :stroke-width="18" v-bind:percentage=percent status="success"></el-progress>
            </el-header>
            <div class="block">
                <el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always" v-on:change="onIndexChange">
                    <el-carousel-item id="carouselItem" v-for="item in imgNames.length" :key="item">
                        <div id="canvasDiv">
                            <canvas
                                    id="canvas"
                                    class="fl"
                                    @mousedown="canvasDown($event)"
                                    @mouseup="canvasUp($event)"
                                    @mousemove="canvasMove($event)"
                                    @mouseout="canvasOut($event)"
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
            <!--<el-button @click="updateThisAnnotation()">更新</el-button>-->
            <!--<el-button @click="previousPictureAndUpdateThisAnnotation()">上一個</el-button>-->
            <!--<el-button @click="nextPictureAndUpdateThisAnnotation()">下一個</el-button>-->
            <!--<el-button @click="clearThisAnnotation()">清除</el-button>-->
            <!--<el-button @click="undoThisAnnotation()">UNDO</el-button>-->
            <ul style="list-style-type:none">
                <li>
                    <el-button @click="handleCommit(nowIndex)" :disabled=submitDisabled>提交</el-button>
                    <el-button @click="onCancel()">取消</el-button>
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
        text-align:center;
    }

    #canvas {
        border-right: 1px #585858 solid;
        cursor: crosshair;
        background-color: black;
    }

    #canvasDiv {
        position: relative;
        display:inline-block;
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
				annotationData:[],   //这个是供前端加载和服务器同步的标记数组
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
				isNew:[],
				isMouseDown:
					false,
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
				percent:0,
				number:0,
				userClick: true,
				submitDisabled: "disabled",
				taskId:this.$route.params.taskId,
			}
		},
		mounted() {
			let _this = this;
			this.$nextTick(function () {
				/*
                const canvas = document.querySelector('#canvas');
                _this.context = canvas.getContext('2d');
                */
				_this.getImgNames();
			})
		},
		methods: {
			getImgNames() {
				let _this = this;
				let theId = this.taskId;
				let route = 'http://localhost:8086/tasks/id/' + theId;
				//{headers:{Authorization:that.$store.getters.getToken}
				this.$http.get(route,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
					console.log(response.data.imgNames);
					let temp = response.data.imgNames;
					for (let i = 0; i < temp.length; i++) {
						//console.log(temp[i])
						_this.imgNames.push(temp[i]);
					}
					_this.loadImageAndAnnotation();
					_this.number = _this.imgNames.length;
					for(let j = 0;j < _this.number;j++){
						_this.isNew.push(true);
					}
					_this.percent = parseFloat(((_this.nowIndex+1)/_this.number * 100).toFixed(1));
					_this.loadFrontList(function () {
						_this.canCommit();      //利用回调函数在初始化的时候判断能否提交
					});

				}).catch(function (error) {
					console.log(error);
				});
			},
			onIndexChange:function (newIndex,oldIndex) {
				// oldIndex===-1是一开始加载走马灯的情况
				if(oldIndex!==-1){
					// 阻止从第一张直接切换到最后一张
					if(oldIndex===0 && newIndex===this.imgNames.length-1 && this.userClick===true){
						this.userClick=false;
						this.$refs.carousel.setActiveItem(0);
					}
					else if(newIndex===0 && oldIndex===this.imgNames.length-1 && this.userClick===false){
						this.userClick=true;
					}

					// 阻止从最后一张直接切换到第一张
					else if(oldIndex===this.imgNames.length-1 && newIndex===0 && this.userClick===true){
						this.userClick=false;
						this.$refs.carousel.setActiveItem(this.imgNames.length-1);
					}
					else if(newIndex===this.imgNames.length-1 && oldIndex===0 && this.userClick===false){
						this.userClick=true;
					}

					// 正常情况
					else{
						if(this.userClick===true){
							if(this.frames.length===0 && newIndex>oldIndex){
								this.userClick=false;
								this.$refs.carousel.setActiveItem(oldIndex);
							}
							else{
								this.saveAndLoad(oldIndex,newIndex);
							}
						}
						else{
							this.userClick=true;
						}
					}
				}
			},
			async saveAndLoad(oldIndex,newIndex) {//保存原index里面的内容，加载新index的内容
				await this.putOrPost(oldIndex);
				await this.loadWhenChange(newIndex);
			},
			loadFrontList() {   //用这种方式来初始化前端的annotation数组
				let _this = this;
				for (let path of _this.imgNames) {
					let route = 'http://localhost:8086/frameAnnotation/taskId/'+ _this.taskId + '/imgName/' + path;
					this.$http.get(route,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
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
			handleCommit(nowIndex){
				let flag = this.canCommit();
				if(flag){
					this.putOrPost(nowIndex);
					console.log(this.taskId);
					let route = 'http://localhost:8086/contract/complete/' + this.taskId;
					let _this = this;
					let data = {
						point: 1,
						money: 1,
					};
					_this.$http.put(route,data,{headers:{Authorization:_this.$store.getters.getToken}}).then(function(response){
						console.log("finish");
					}).catch(function(error){
						console.log(error);
					});
					_this.messageHandler();
					this.$router.push({path: '/profile'});
				}
				else{
					this.putOrPost(nowIndex);
					this.badMessage();

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
			canCommit(){
				//首先判断当前的annotation是否为空，然后在判断别的
				if(this.annotation.frames.length === 0){
					this.submitDisabled = 'disabled';
					return false;
				}

				for(let e of this.annotationData){
					if(e.imgName !== this.annotation.imgName){
						if(e.frames.length === 0){
							this.submitDisabled = 'disabled';
							return false;
						}
					}
				}

				this.submitDisabled = false;
				return true;
			},
			/**
			 * loading-related methods. (mouse-event listeners are down below)
			 * */
			loadImageAndAnnotation() {
				let _this = this;
				_this.pic = new Image();
				_this.pic.src = "http://localhost:8086/image/" + _this.imgNames[this.nowIndex];
				// this.pic.addEventListener('load', function () {
				_this.pic.onload = function () {
					//alert("here");
					// draw(this);
					const temp=document.getElementById('carousel');
					const canvasDiv = temp.querySelectorAll('#canvasDiv').item(_this.nowIndex);
					const canvas = canvasDiv.querySelector('#canvas');
					_this.context = canvas.getContext('2d');

					let ratio=this.width/this.height;
					_this.canvasHeight = document.getElementById('carousel').offsetHeight;
					_this.canvasWidth = _this.canvasHeight*ratio;
					canvas.height = _this.canvasHeight;
					canvas.width = _this.canvasWidth;

					/*
                    _this.canvasWidth = this.width;
                    _this.canvasHeight = this.height;
                    canvas.height = _this.canvasHeight;
                    canvas.width = _this.canvasWidth;
                    */
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
				this.$http.get('http://localhost:8086/frameAnnotation/taskId/' + _this.taskId + '/imgName/' + this.imgNames[this.nowIndex],{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
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
				const temp=document.getElementById('carousel');
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

				// popover和tag的button就是要分别来remove…
				// const popovers = document.getElementsByClassName('popover');
				// for (const pv of popovers) {
				//     document.body.removeChild(pv);
				// }

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
				// const p = frames[frames.length - 1].getRightTopPoint();
				const p = this.getRightTopPoint(frame);
				// console.log("frame = " + "p1:" + frame.p1.x + "y:" + frame.p1.y);
				// console.log("frame = " + "p2:" + frame.p2.x + "y:" + frame.p2.y);
				// console.log("p = " + "x:" + p.x + "y:" + p.y);
				// console.log(canvas.getBoundingClientRect().top);
				// console.log(canvas.offsetTop);
				const canvas = document.querySelector('#canvas');
				const cssString = "position:absolute; white-space: nowrap;" + "top:" + (p.y + canvas.offsetTop) + "px;" + "left:" + p.x + "px;";

				const htmlString = "<el-tag style='background: #e5e9f2'>標記" + (index + 1) + " </el-tag>";

				// console.log("htmlString + " + htmlString);
				// console.log("cssString + " + cssString);
				//
				let div = document.createElement('div');
				div.id = 'tag' + index;
				div.innerHTML = htmlString;
				div.setAttribute('style', cssString);
				// end
				const temp=document.getElementById('carousel');
				const canvasDiv = temp.querySelectorAll('#canvasDiv').item(this.nowIndex);
				canvasDiv.appendChild(div);
				this.canCommit();
			},
			canvasDown(e) {
				this.startPoint.x = e.offsetX;
				this.startPoint.y = e.offsetY;
				this.isMouseDown = true;
			},
			canvasOut(e) {
				if(this.isMouseDown === true){
					this.canvasUp(e);
				}
			},
			canvasUp(e) {
				this.endPoint.x = e.offsetX;
				this.endPoint.y = e.offsetY;

				if (this.startPoint.x === this.endPoint.x && this.startPoint.y === this.endPoint.y) {
					this.isMouseDown = false;
					return;
				}

				this.drawRec();

				// const newPointX = {
				//     x: this.startPoint.x,
				//     y: this.startPoint.y,
				// }
				// const newPointY = {
				//     x: this.endPoint.x,
				//     y: this.endPoint.y,
				// }
				let _this = this;
				const frame = {
					p1: {
						x: _this.startPoint.x,
						y: _this.startPoint.y,
					},
					p2: {
						x: _this.endPoint.x,
						y: _this.endPoint.y,
					},
				};
				// frame.p1 = this.startPoint;
				// frame.p2 = this.endPoint;
				// console.log("********");
				// console.log(frame.p1.x + ' / ' + frame.p1.y);
				// console.log(frame.p2.x + ' / ' + frame.p2.y);
				// console.log("********");
				// console.log("????" + this.frames.length);
				this.frames.push(frame);
				this.addTag(frame, this.frames.length - 1);
				this.isMouseDown = false;
			},
			canvasMove(e) {
				if (this.isMouseDown) {
					this.endPoint.x = e.offsetX;
					this.endPoint.y = e.offsetY;
					this.drawRec();
				}
			},
			drawRec() {
				const temp=document.getElementById('carousel');
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
			// onInputListener(index) {
			//     let textArea = document.getElementById('tag' + index);
			//     frames[index].tag = textArea.value;
			// },
			deleteTag(i) {
				for (let k = i; k < this.frames.length - 1; k++) {
					this.frames[k].tag = this.frames[k + 1].tag;
				}
				this.frames.splice(i, 1);
				console.log("frames.length :" + this.frames.length);
				this.canCommit();
				this.initialDraw();
			},
			/**
			 * xhr-related methods
			 * */
			loadWhenChange(newIndex){
				this.nowIndex = newIndex;
				for (let i = 0; i < this.frames.length; i++) {
					this.deleteTag(i);
				}
				this.loadImageAndAnnotation();
				this.percent = parseFloat(((newIndex+1)/this.number * 100).toFixed(1));
			},
			putOrPost(nowIndex) {
				// let method = isNew ? 'POST' : 'PUT';
				let _this = this;
				if (this.isNew[nowIndex]) {
					this.$http.post('http://localhost:8086/frameAnnotation/taskId/' + _this.taskId, _this.annotation,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
						//window.alert('save!');   新增之后要改变isNew
						_this.isNew[nowIndex] = false;
						//新增之后重新加载
						_this.$http.get('http://localhost:8086/frameAnnotation/taskId/' + _this.taskId + '/imgName/' + _this.imgNames[nowIndex],{headers:{Authorization:_this.$store.getters.getToken}}).then(function (res) {
							_this.$set(_this.annotationData, nowIndex, res.data);
							_this.canCommit();
							console.log(_this.annotationData);   //debug使用
						}).catch(function (err) {
							console.log(err);
						})
					}).catch(function (error) {
						console.log(error);
					});
				} else {
					this.$http.put('http://localhost:8086/frameAnnotation', _this.annotation,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
						//window.alert('save!');
						//新增之后重新加载
						_this.$http.get('http://localhost:8086/frameAnnotation/taskId/' + _this.taskId + '/imgName/' + _this.imgNames[nowIndex],{headers:{Authorization:_this.$store.getters.getToken}}).then(function (res) {
							_this.$set(_this.annotationData, nowIndex, res.data);
							console.log(_this.annotationData);   //debug使用
							_this.canCommit();
						}).catch(function (err) {
							console.log(err);
						})
					}).catch(function (error) {
						console.log(error);
					});
				}
				// let xhr = new XMLHttpRequest();
				// xhr.open(method, 'http://localhost:8086/frameAnnotation', true);
				// xhr.setRequestHeader("Content-Type", "application/json");
				// xhr.send(JSON.stringify(annotation));
			},
			onCancel(){
				this.$router.push({path: '/tasklobby'});
			}
		}
	}
</script>
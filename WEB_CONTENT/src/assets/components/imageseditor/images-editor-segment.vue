<template>
    <div class="editor-frame">
        <el-row>
            <el-col :span="15">
                <el-card class="pic-area">
                    <div slot="header" class="pic-card-header">
                        <span>任務描述: {{taskDescription}}</span>
                    </div>
                    <el-row>
                        <el-col :span="24">
                            <div id="canvasDiv">
                                <div v-html="canvasHtml">
                                    {{canvasHtml}}
                                </div>
                                <div v-html="tagHtml">{{tagHtml}}</div>
                            </div>
                        </el-col>
                    </el-row>
                </el-card>
            </el-col>
            <el-col :span="6" :offset="1">
                <el-card class="tag-area">
                    <div slot="header" class="tag-card-header">
                        <span>標註與設置</span>
                    </div>
                    <div class="operation">
                        <el-row :gutter="30">
                            <el-col :span="8">
                                <el-button icon="el-icon-arrow-left" circle @click="previous()"></el-button>
                            </el-col>
                            <el-col :span="8">
                                <el-button @click="submit()" style="font-family:Microsoft YaHei; ">提交
                                </el-button>
                            </el-col>
                            <el-col :span="8">
                                <el-button icon="el-icon-arrow-right" circle @click="next()"></el-button>
                            </el-col>
                        </el-row>
                    </div>
                    <el-collapse>
                        <el-collapse-item>
                            <template slot="title">
                                <span style="font-size: 16px">標籤</span>
                            </template>
                            <div>
                                <el-tag size="small" type="info">小型标签</el-tag>
                                <el-tag size="small" type="info">小型标签</el-tag>
                                <el-tag size="small" type="info">小型标签</el-tag>
                                <el-tag size="small" type="info">小型标签</el-tag>
                            </div>
                        </el-collapse-item>
                        <el-collapse-item>
                            <template slot="title">
                                <span style="font-size: 16px">進度</span>
                            </template>
                            <div>
                                <el-progress type="circle" :percentage=percent color="#8e71c7"></el-progress>
                            </div>
                        </el-collapse-item>
                        <el-collapse-item>
                            <template slot="title">
                                <span style="font-size: 16px">標註</span>
                            </template>
                            <div>
                                <div v-if="isTagShow" v-for="(val,index) in tagText" style="margin-top: 0.3em">
                                    <el-input size="small" placeholder="请输入内容" v-model="val.text">
                                        <template slot="prepend">標註{{val.index+1}}</template>
                                        <el-button size="mini" slot="append" icon="el-icon-delete"
                                                   @click="deleteTag(index)"></el-button>
                                    </el-input>
                                </div>
                            </div>
                        </el-collapse-item>
                    </el-collapse>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<style>
    .operation {
        margin-bottom: 0.5em;
        text-align: center;
    }

    #canvasDiv {
        text-align: center;
    }

    .editor-frame {
        font-family: Microsoft YaHei;
    }

    .pic-area {
        width: 100%;
        height: 100%;
        padding: 0.7em;
        margin: 1.5em;
        text-align: left;
    }

    .tag-card-header {
        font-family: Microsoft YaHei;
        padding-bottom: 0;
        font-size: 20px;
    }

    .pic-card-header {
        font-family: Microsoft YaHei;
        padding-bottom: 0;
        font-size: 20px;
    }

    .tag-area {
        width: 100%;
        height: 100%;
        padding: 0.7em;
        margin: 1.5em;
        text-align: left;
        font-size: 18px;
    }

    #canvas {
        border-right: 1px #585858 solid;
        cursor: crosshair;
        background-color: black;
    }
</style>

<script>
	import ImageViewer from '../../js/ImageViewer.js'
	import AnnotationViewer from '../../js/AnnotationViewer.js'
	import AnnotationEditor from '../../js/AnnotaionEditor.js'
	import SegmentDrawingStrategy from '../../js/strategy/SegmentDrawingStrategy.js'

	export default {
		data() {
			return {
				isTagShow: false,
				tagHtml: '',
				tagText: [],
				viewer: {},
				canvasHtml: '<canvas id="canvas"></canvas>',
				tagText: [{}],
				submitDisabled: "disabled",
				contractId: this.$route.params.contractId,
				taskId: this.$route.params.taskId,
				imageLength: 0,
				currentPlace: 1,
				taskDescription: ""
			}
		},
		computed: {
			percent() {
				console.log(this.currentPlace)
				let result, lowerLimit;
				lowerLimit = 1 / this.imageLength * 100;
				result = this.currentPlace / this.imageLength * 100;
				result = result.toFixed(1);
				console.log(lowerLimit);
				console.log(result);
				lowerLimit = lowerLimit.toFixed(1);
				// if (result >= 100) {
				// 	return parseFloat(100.0);
				if (result != 100 && result < lowerLimit)
					return parseFloat(lowerLimit);
				// 	return parseFloat(lowerLimit);
				// } else {
				return parseFloat(result);
				// }
			}
		},
		mounted() {
			this.$nextTick(() => {
				this.canvas = document.getElementById("canvas");
				this.canvas.addEventListener("mousedown", this.canvasDown);
				this.canvas.addEventListener("mouseup", this.canvasUp);
				this.canvas.addEventListener("mousemove", this.canvasMove);
				this.canvas.addEventListener("touchstart", this.canvasDown);
				this.getImgNames();
			})
		},
		methods: {
			getImgNames() {
				let route = 'http://localhost:8086/tasks/id/' + this.taskId;
				let header = {headers: {Authorization: this.$store.getters.getToken}};
				this.$http.get(route, header)
					.then((response) => {
						let viewer = new ImageViewer(this.canvas, response.data.imgNames, "http://localhost:8086/image/");
						console.log(response);
						this.imageLength = response.data.imgNames.length;
						this.taskDescription = response.data.taskDescription;
						viewer = new AnnotationViewer(new SegmentDrawingStrategy(), viewer, 'http://localhost:8086/segmentAnnotation/taskId/', this.taskId, this.$http);
						this.viewer = new AnnotationEditor(viewer, header, 'http://localhost:8086/segmentAnnotation/taskId/', 'http://localhost:8086/segmentAnnotation', this.taskId, this.$http);
						this.viewer.drawCurrent(header, () => {
							this.viewer.setTagUpdateCallback(this.updateTagHtml);
							this.viewer.setTagTextUpdateCallback(this.updateTagText);
							this.viewer.setTagTextGettingCallback(this.getTagText);
						});
					})
					.catch(function (error) {
						console.log(error);
					})
			},
			updateTagHtml() {
				this.tagHtml = this.viewer.shareTag();
			},
			updateTagText() {
				if (this.isTagShow === false)
					this.isTagShow = true;
				this.tagText = this.viewer.shareTagText();
			},
			getTagText() {
				return this.tagText;
			},
			next() {
				this.viewer.forceUpdate(this.tagText);
				this.viewer.drawNext(() => {
					if (this.currentPlace < this.imageLength)
						this.currentPlace++;
				});
			},
			previous() {
				this.viewer.forceUpdate(this.tagText);
				this.viewer.drawPrevious(() => {
					if (this.currentPlace > 1)
						this.currentPlace--;
				});
			},
			canvasDown(e) {
				this.viewer.handleMouseDown(e);
			},
			canvasUp(e) {
				this.viewer.handleMouseUp(e);
			},
			canvasMove(e) {
				this.viewer.handleMouseMove(e);
			},
			forceUpdate() {
				this.viewer.forceUpdate(this.tagText);
			},
			deleteTag(index) {
				this.viewer.forceUpdate(this.tagText);
				this.viewer.deleteTag(index);
			},
			submit() {
				if (this.viewer.submitCurrent(this.imageLength)) {
					this.viewer.submitCurrent(this.imageLength);
					this.$confirm('此任務已經完成, 請問是否進行下一個?', '提示', {
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					}).then(() => {
						// do things here;
					}).catch(() => {
						this.$router.push({path: '/profile'});
					});
				} else {
					this.showMessage("notDone");
				}
			},
			showMessage(type) {
				if ("success" === type) {
					this.$message({
						message: '任务已经提交，请安心等待结果和奖励^_^',
						type: 'success'
					})
				}else if("notDone" === type){
					this.$message({
						message: '您还没有完成这个任务^_^',
						type: 'error'
					})
                }
			}
		}
	}
</script>
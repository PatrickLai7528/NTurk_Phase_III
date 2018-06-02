<template>

    <el-container>
        <el-main class="wrap">
            <el-header>
                <el-progress :text-inside="true" :stroke-width="18" v-bind:percentage=percent
                             status="success"></el-progress>
            </el-header>
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
                <li>
                    <el-button @click="handleCommit(nowIndex)" :disabled=submitDisabled>提交</el-button>
                    <el-button @click="onCancel()">取消</el-button>
                </li>
                <li v-if="isTagShow" v-for="(val,index) in tagText">
                    <el-popover
                            placement="right"
                            width="150"
                            trigger="click"
                    >
                        <el-input
                                type="textarea"
                                :rows="2"
                                v-model="val.text"
                                @change="forceUpdate"
                        >
                        </el-input>
                        <el-button slot="reference" class="tag">標記{{val.index+1}}</el-button>
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
				currentPlace: 1
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
						this.imageLength = response.data.imgNames.length;
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
				this.viewer.drawNext(() => {
					if (this.currentPlace < this.imageLength)
						this.currentPlace++;
				});
			},
			previous() {
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
				// this.viewer.forceUpdate(this.tagText);
				this.viewer.deleteTag(index);
			}
		}
	}
</script>
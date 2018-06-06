<template>
    <el-container class="editor-frame">
        <el-card class="pic-area">
            <div slot="header" class="pic-card-header">
                <span>編輯器</span>
            </div>
            <div id="canvasDiv">
                <div v-html="canvasHtml">
                    {{canvasHtml}}
                </div>
                <div v-html="tagHtml">{{tagHtml}}</div>
            </div>
        </el-card>
        <el-main>
            <el-card class="tag-area">
                <div slot="header" class="tag-card-header">
                    <span>標註</span>
                </div>
                <ul style="list-style-type:none">
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
            </el-card>
        </el-main>
    </el-container>
</template>

<style>
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
        font-size: 24px;
    }

    .pic-card-header {
        font-family: Microsoft YaHei;
        padding-bottom: 0;
        font-size: 24px;
    }

    .config-card-header {
        font-family: Microsoft YaHei;
        padding-bottom: 0;
        font-size: 24px;
    }

    .tag-area {
        width: 100%;
        height: 100%;
        padding: 0.7em;
        margin: 1.5em;
        text-align: left;
    }

    .config-area {
        width: 100%;
        height: 100%;
        padding: 0.7em;
        margin: 1.5em;
        text-align: left;
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
	import FrameDrawingStrategy from '../../js/strategy/FrameDrawingStrategy.js'
	import AnnotationEditor from '../../js/AnnotaionEditor.js'
	import ElAside from "element-ui/packages/aside/src/main";
	import ElMain from "element-ui/packages/main/src/main";

	export default {
		components: {
			ElMain,
			ElAside
		},
		data: function () {
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
				console.log(this.canvas);
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
						viewer = new AnnotationViewer(new FrameDrawingStrategy(), viewer, 'http://localhost:8086/frameAnnotation/taskId/', this.taskId, this.$http);
						this.viewer = new AnnotationEditor(viewer, header, 'http://localhost:8086/frameAnnotation/taskId/', 'http://localhost:8086/frameAnnotation', this.taskId, this.$http);
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
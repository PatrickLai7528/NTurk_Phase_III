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
                <li>
                    <el-form v-for="pair in answerPairs">
                        <el-form-item class="Q_And_A" :label="pair.question">
                            <el-input v-model="pair.answer"></el-input>
                        </el-form-item>
                    </el-form>
                </li>
            </ul>
        </el-aside>
    </el-container>
</template>
<script>
	import ImageViewer from '../../js/ImageViewer.js'
	import AnnotationViewer from '../../js/AnnotationViewer.js'
	import AnnotationEditor from '../../js/AnnotaionEditor.js'
	import AnswerPairsDrawingStrategy from '../../js/strategy/AnswerPairsDrawingStrategy.js'

	export default {
		data() {
			return {
				tagHtml: '',
				viewer: {},
				canvasHtml: '<canvas id="canvas"></canvas>',
				answerPairs: [],
				answerPairsDrawingStrategy: {},
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
				let header = {
					headers: {
						'Content-Type': 'application/json',
						Authorization: this.$store.getters.getToken
					}
				}
				this.$http.get(route, header)
					.then((response) => {
						console.log(response);
						this.questions = response.data.questions;
						console.log(this.questions)
						this.initQuestion(this.questions);
						let viewer = new ImageViewer(this.canvas, response.data.imgNames, "http://localhost:8086/image/");
						this.imageLength = response.data.imgNames.length;
						this.answerPairsDrawingStrategy = new AnswerPairsDrawingStrategy()
						viewer = new AnnotationViewer(this.answerPairsDrawingStrategy, viewer, 'http://localhost:8086/generalAnnotation/taskId/', this.taskId, this.$http);
						this.viewer = new AnnotationEditor(viewer, header, 'http://localhost:8086/generalAnnotation/taskId/', 'http://localhost:8086/generalAnnotation', this.taskId, this.$http);
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
				let temp = this.viewer.shareTag();
				console.log(this.tagHtml);
				if (temp === "") return;
				this.answerPairs = this.answerPairsDrawingStrategy.interpretTag(temp);
			},
			updateTagText() {
				// let temp = this.viewer.shareTag();
				// console.log(this.tagHtml);
				// this.answerPairs = this.answerPairsDrawingStrategy.interpretTag(temp);
				// let temp = this.viewer.shareTagText();
				// if (temp.length !== 0) {
				// 	this.answerPairs = temp;
				// }
				// console.log(this.answerPairs)
			},
			getTagText() {
				// let temp;
				// this.answerPairs.forEach((value, index, array) => {
				// 	temp += this.answerPairsDrawingStrategy.addTag({}, value, index);
				// });
				// return temp;
			},
			next() {
				this.viewer.setAnswerPairs(this.answerPairs);
				this.viewer.drawNext(() => {
					this.initQuestion(this.questions);
					if (this.currentPlace < this.imageLength)
						this.currentPlace++;
				});
			},
			previous() {
				this.viewer.setAnswerPairs(this.answerPairs);
				this.viewer.drawPrevious(() => {
					this.initQuestion(this.questions);
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
				this.viewer.forceUpdate(this.answerPairs);
			},
			deleteTag(index) {
				// this.viewer.forceUpdate(this.tagText);
				this.viewer.deleteTag(index);
			},
			initQuestion(questions) {
				let q;
				this.answerPairs = [];
				for (q of questions) {
					this.answerPairs.push({question: q, answer: ""})
				}
			}
		}
	}
</script>

<style scoped>
    .el-carousel__item h3 {
        color: #475669;
        font-size: 14px;
        opacity: 0.75;
        line-height: 150px;
        margin: 0;
    }

    .el-carousel__item:nth-child(2n) {
        background-color: #99a9bf;
    }

    .el-carousel__item:nth-child(2n+1) {
        background-color: #d3dce6;
    }

    .Q_And_A {
        font-size: 18px;
        line-height: 40px;
        margin: 30px;
    }

    .pic {
        height: 100%;
    }

    .el-form-item {
        margin: 20px;
    }

    .el-button {
        width: 100px;
        margin: 10px;
    }
</style>
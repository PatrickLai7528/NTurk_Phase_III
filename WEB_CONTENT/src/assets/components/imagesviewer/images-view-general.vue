<template>
    <el-container>
        <el-main>
            <div class="block">
                <div id="canvasDiv">
                    <div v-html="canvasHtml">
                        {{canvasHtml}}
                    </div>
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
            <<<<<<< HEAD
            <el-form ref="form" :model="form">
                <el-form v-model="form.region" v-for="pair in annotationData[nowIndex].answerPairs">
                    <el-form-item class="Q_And_A" v-bind:label="pair.question">
                        <el-input v-model="pair.answer" disabled></el-input>
                    </el-form-item>
                </el-form>
            </el-form>
            <div v-if="isRequester === false">
                <div id="prompt">请点击星级进行评分：</div>
                <el-rate
                        id="rate-bar"
                        v-model="nowRating"
                        :colors="['#99A9BF', '#F7BA2A', '#FF6347']"
                        v-on:change="ratingChange"
                >
                </el-rate>
                <el-button id="commit-button" :disabled=commitDisabled @click="commitRating" type="primary">提交<i
                        class="el-icon-upload el-icon--right"></i></el-button>
            </div>
            =======
            <!--<el-form ref="form" :model="form">-->
            <el-form v-for="pair in answerPairs">
                <el-form-item class="Q_And_A" :label="pair.question">
                    <el-input v-model="pair.answer" :disabled=true></el-input>
                </el-form-item>
            </el-form>
            <!--</el-form>-->
            >>>>>>> 161250051_refactor
        </el-aside>
    </el-container>
</template>

<script>
	import ImageViewer from '../../js/ImageViewer.js'
	import AnnotationViewer from '../../js/AnnotationViewer.js'
	import AnswerPairsDrawingStrategy from '../../js/strategy/AnswerPairsDrawingStrategy.js'

	export default {
		data() {
			return {
				viewer: {},
				canvas: {},
				canvasHtml: `<canvas id="canvas" class="fl" width="600" height="400"></canvas>`,
				taskId: this.$route.params.taskId,
				contractId: this.$route.params.contractId,
				answerPairs: [{}],
				answerPairsDrawingStrategy: {}
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
						this.answerPairsDrawingStrategy = new AnswerPairsDrawingStrategy()
						this.viewer = new AnnotationViewer(this.answerPairsDrawingStrategy, imageViewer, 'http://localhost:8086/generalAnnotation/contractId/', this.contractId, this.$http);
						this.viewer.drawCurrent(header, this.doThisEveryImageUpdate);
					})
					.catch(function (error) {
						console.log(error);
					})
			},
			doThisEveryImageUpdate() {
				this.answerPairs = this.answerPairsDrawingStrategy.interpretTag(this.viewer.shareTag());
				// this.tagHtml = this.viewer.shareTag();
				// this.tagText = this.viewer.shareTagText();
			},
			next() {
				this.viewer.drawNext(this.doThisEveryImageUpdate);
			},
			previous() {
				this.viewer.drawPrevious(this.doThisEveryImageUpdate);
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

    #prompt {
        margin-top: 100px;
    }

    #rate-bar {
        margin-top: 20px;
    }

    #commit-button {
        margin-top: 50px;
    }
</style>
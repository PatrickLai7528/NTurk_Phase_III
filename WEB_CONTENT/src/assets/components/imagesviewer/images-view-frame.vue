<template>

    <el-container>
        <el-main class="wrap">
            <div class="block">
                <!--<el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always"-->
                <!--v-on:change="onIndexChange">-->
                <!--<el-carousel-item id="carouselItem" v-for="item in imgNames.length" :key="item">-->
                <div id="canvasDiv">
                    <div v-html="canvasHtml">
                        {{canvasHtml}}
                    </div>
                    <div v-html="tagHtml">{{tagHtml}}</div>
                </div>
                <!--<div v-html="allEditAreaHtml">-->
                <!--<div>{{allEditAreaHtml}}</div>-->
                <!--</div>-->
                <div id="previous-button">
                    <el-button icon="el-icon-arrow-left" circle @click="previous()"></el-button>
                </div>
                <div id="next-button">
                    <el-button icon="el-icon-arrow-right" circle @click="next()"></el-button>
                </div>
                <!--</el-carousel-item>-->
                <!--</el-carousel>-->
            </div>
        </el-main>
        <el-aside width="300px" style="alignment: center">
            <ul style="list-style-type:none">
                <li>
                </li>
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
	import FrameDrawingStrategy from '../../js/strategy/FrameDrawingStrategy.js'
	import AnnotationEditor from '../../js/AnnotaionEditor.js'

	export default {
		data: function () {
			return {
				tagHtml: '',
				viewer: {},
				canvasHtml: `
                    <canvas id="canvas" class="fl"></canvas>`,
				tagText: [{}],
				submitDisabled: "disabled",
				taskId: this.$route.params.taskId,
				contractId: this.$route.params.contractId,
			}
		},
		mounted() {
			this.$nextTick(() => {
				this.canvas = document.getElementById("canvas");
				// console.log("new canvas = " + this.canvas);
				this.getImgNames();
				// console.log(this.allEditAreaHtml)
			})
		},
		methods: {
			getImgNames() {
				let route = 'http://localhost:8086/tasks/id/' + this.taskId;
				let header = {headers: {Authorization: this.$store.getters.getToken}};
				this.$http.get(route, header)
					.then((response) => {
						let imageViewer = new ImageViewer(this.canvas, response.data.imgNames, "http://localhost:8086/image/");
						this.viewer = new AnnotationViewer(new FrameDrawingStrategy(), imageViewer, 'http://localhost:8086/frameAnnotation/contractId/', this.contractId, this.$http);
						this.viewer.drawCurrent(header, this.doThisEveryImageUpdate);
					})
					.catch(function (error) {
						console.log(error);
					})
			},
			doThisEveryImageUpdate() {
				this.tagHtml = this.viewer.shareTag();
				this.tagText = this.viewer.shareTagText();
			},
			onIndexChange(newIndex, oldIndex) {
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
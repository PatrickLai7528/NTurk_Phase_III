<template>

    <el-container>
        <el-main class="wrap">
            <div class="block">
                <!--<el-carousel id="carousel" ref="carousel" height="36em" v-bind:autoplay="false" arrow="always"-->
                <!--v-on:change="onIndexChange">-->
                <!--<el-carousel-item id="carouselItem" v-for="item in imgNames.length" :key="item">-->
                <div v-html="allEditAreaHtml">
                    <div>{{allEditAreaHtml}}</div>
                </div>
                <div id="Previous-button">
                    <el-button icon="el-icon-arrow-left" circle @click="Previous()"></el-button>
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
	import ImageViewer from '../../js/ImageViewer.js'
	import AnnotationViewer from '../../js/AnnotationViewer.js'

	export default {
		computed: {
			allEditAreaHtml() {
				return '<div id = "canvasDiv">' + this.canvasHtml + this.tagHtml + '</div>'
			},
            frames(){
				return this.viewer.shareMarking();
            }
		},
		data: function () {
			return {
				tagHtml: '',
                viewer:{},
				// language=HTML
				canvasHtml: `
                    <canvas id="canvas" class="fl" width="600" height="400"></canvas>`,
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
				contractId: this.$route.params.contractId,
			}
		},
		mounted() {
			this.$nextTick(() => {
				this.canvas = document.getElementById("canvas");
				console.log("new canvas = " + this.canvas);
				this.getImgNames();
				console.log(this.allEditAreaHtml)
			})
		},
		methods: {
			getImgNames() {
				let route = 'http://localhost:8086/tasks/id/' + this.taskId;
				let header = {headers: {Authorization: this.$store.getters.getToken}};
				this.$http.get(route, header)
					.then((response) => {
						this.getAnnotations();
						var imageViewer = new ImageViewer(this.canvas, response.data.imgNames, "http://localhost:8086/image/");
						this.viewer = new AnnotationViewer(imageViewer, 'http://localhost:8086/frameAnnotation/contractId/', this.contractId, this.$http)
						let canvasDiv = document.querySelector("#canvasDiv");
						let header = {headers: {Authorization: this.$store.getters.getToken}};
						this.viewer.drawCurrent(canvasDiv, header);
						// this.render();
						// this.viewer.drawNext();
						// this.viewer.drawPrevious()
						// console.log(this.viewer);
						// console.log(this.canvas);
						// this.viewer.drawCurrent();
					})
					.catch(function (error) {
						console.log(error);
					})
			},
			onIndexChange(newIndex, oldIndex) {
			},
			getAnnotations() {
				let route = ""
			},
			next() {
				this.viewer.drawNext();
			},
			Previous() {
				this.viewer.drawPrevious();
			}
		}
	}

</script>
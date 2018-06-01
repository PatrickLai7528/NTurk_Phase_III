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
                <el-button id="commit-button" :disabled=commitDisabled @click="commitRating" type="primary">提交<i class="el-icon-upload el-icon--right"></i></el-button>
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
<<<<<<< HEAD
    import UserUtils from '../../js/utils/UserUtils.js'
    export default {
        data(){
            return{
                //先通过contractId得到task信息，然后去task得到imgNames，但是调用后台方法的时候使用contractId+imgName
                form: {
                    name: '',
                    region: '',
                    date1: '',
                    date2: '',
                    delivery: false,
                    type: [],
                    resource: '',
                    desc: '',
                },
                tableData: [{//这个是通过taskId读出来的task
                    taskId: '',
                    taskName: '',
                    requesterId: '',
                    taskCategory: '',
                    requester: '',
                    reward: '',
                    imgNames: [],
                    questions: [],
                }],
                annotationData: [],    //在后端读出的annotation信息放在annotationData中
                questionData: [],     //应该增加questionData在加载任务的时候就将questionData加载出来
                nowIndex: 0,
                contractId: this.$route.params.contractId,
                taskId: this.$route.params.taskId,
                nowRating: 0,        //对当前图片的评分
                ratings: [],           //对这个合同所有的评分数组
                commitDisabled: 'disabled',
                isRequester: null
            }
        },
        mounted: function () {
            this.$nextTick(function () {
                //保证el已经插入文档
                let _this = this;
                this.isRequester = UserUtils.isRequester(this);
                this.$nextTick(function () {
                    _this.load();
                })
            });
        },
        methods:{
            //像评分和提交的组件，如果当前登陆者是发起者，是不显示的
            commitRating(){
                //to-do
            },
            canCommit(){
                if(this.ratings.length === this.tableData.imgNames.length){
                    this.commitDisabled = false;
                }
                else{
                    this.commitDisabled = 'disabled';
                }
            },
            load() {
                let _this = this;
                let theId = this.$route.params.taskId;
                let route = 'http://localhost:8086/tasks/id/' + theId;
                //{headers:{Authorization:that.$store.getters.getToken}
                this.$http.get(route,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
                    _this.tableData = response.data;//将特定task的内容读入tableData,然后去后端的annotation里面遍历
                    _this.number = _this.tableData.imgNames.length;
                    _this.questionData = _this.tableData.questions;

                    _this.getAnnotation();

                }).catch(function (error) {
                    console.log(error);
                });
            },
            getIndex: function (imgSrc) {          //调用这个方法得到当前图片在imgNames中的位置保持同步
                for (let i = 0; i < this.tableData.imgNames.length; i++) {
                    if (imgSrc === this.tableData.imgNames[i]) {
                        return i;
                    }
                }
            },
            onIndexChange: function (newIndex, oldIndex) {
                this.nowIndex = newIndex;
                this.nowRating = 0;
                this.commitDisabled = 'disabled';
            },
            ratingChange: function(score){
                this.ratings[this.nowIndex] = score;
                this.canCommit();
            },
            getAnnotation: function () {
                let _this = this;
                for (let path of _this.tableData.imgNames) {
                    let route = 'http://localhost:8086/generalAnnotation/contractId/' + _this.contractId +'/imgName/' + path;
                    this.$http.get(route,{headers:{Authorization:_this.$store.getters.getToken}}).then(function (response) {
                        let index = _this.getIndex(path);
                        _this.$set(_this.annotationData, index, response.data);    //在组件中不能使用Vue.set来进行注册，应该用this.$set方法

                    }).catch(function (error) { //如果后端没有数据记录要自己造一个空的标注对象push进去
                        let index = _this.getIndex(path);

                        function QuesAndAnswer(ques) {
                            this.question = ques;
                            this.answer = '';
                        }

                        function Annotation(img) {
                            this.imgName = img;
                            this.answerPairs = [];
                        }

                        let emptyAnnotation = new Annotation(path);
                        for (let ques of _this.questionData) {
                            let tem = new QuesAndAnswer(ques);
                            emptyAnnotation.answerPairs.push(tem);
                        }
                        _this.$set(_this.annotationData, index, emptyAnnotation);
                    })
                }
            }
        }


    }
=======
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
>>>>>>> 161250051_refactor
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
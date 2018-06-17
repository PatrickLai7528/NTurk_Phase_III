<template >
    <div class = "editor-frame" >
        <el-row >
            <el-col :span = "15" >
                <el-card class = "pic-area" >
                    <div slot = "header" class = "pic-card-header" >
                        <el-row >
                            <el-col :span = "16" >
                                <span >任務描述: {{taskDescription}}</span >
                            </el-col >
                            <el-col :span = "8" style = "text-align: right" >
                                <div v-html = "countDown" >{{countDown}}</div >
                            </el-col >
                        </el-row >
                    </div >
                    <el-row >
                        <el-col :span = "24" >
                            <div id = "canvasDiv" >
                                <div v-html = "canvasHtml" >
                                    {{canvasHtml}}
                                </div >
                                <div v-html = "tagHtml" >{{tagHtml}}</div >
                            </div >
                        </el-col >
                    </el-row >
                </el-card >
            </el-col >
            <el-col :span = "6" :offset = "1" >
                <el-card class = "tag-area" >
                    <div slot = "header" class = "tag-card-header" >
                        <span >標註與設置</span >
                    </div >
                    <div class = "operation" >
                        <el-row :gutter = "30" >
                            <el-col :span = "8" >
                                <el-button icon = "el-icon-arrow-left" circle @click = "previous()" ></el-button >
                            </el-col >
                            <el-col :span = "8" >
                                <el-button @click = "submit()" style = "font-family:Microsoft YaHei; " >提交
                                </el-button >
                            </el-col >
                            <el-col :span = "8" >
                                <el-button icon = "el-icon-arrow-right" circle @click = "next()" ></el-button >
                            </el-col >
                        </el-row >
                    </div >
                    <el-collapse >
                        <el-collapse-item >
                            <template slot = "title" >
                                <span style = "font-size: 16px" >標籤</span >
                            </template >
                            <div >
                                <el-tag size = "small" type = "success"
                                        v-for = "item in tagsOfTask" >{{item}}</el-tag >
                            </div >
                        </el-collapse-item >
                        <el-collapse-item >
                            <template slot = "title" >
                                <span style = "font-size: 16px" >進度</span >
                            </template >
                            <div >
                                <el-progress type = "circle" :percentage = percent color = "#8e71c7" ></el-progress >
                            </div >
                        </el-collapse-item >
                        <el-collapse-item >
                            <template slot = "title" >
                                <span style = "font-size: 16px" >問題</span >
                            </template >
                            <div >
                                <div style = "margin-top: 0.3em" >
                                    <!--<el-form v-for = "pair in answerPairs" >-->
                                    <!--<el-form-item class = "Q_And_A" :label = "pair.question" >-->
                                    <!--<el-input v-model = "pair.answer" ></el-input >-->
                                    <!--</el-form-item >-->
                                    <!--</el-form >-->
                                <el-autocomplete
		                                v-model = "answer"
		                                :fetch-suggestions = "querySearch"
		                                placeholder = "请输入内容"
                                ></el-autocomplete >
                                </div >
                            </div >
                        </el-collapse-item >
                    </el-collapse >
                </el-card >
            </el-col >
        </el-row >
    </div >
</template >

<style >
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
</style >
<script >
	import ImageViewer from '../../js/ImageViewer.js'
    import AnnotationViewer from '../../js/AnnotationViewer.js'
    import AnnotationEditor from '../../js/AnnotaionEditor.js'
    import AnswerPairsDrawingStrategy from '../../js/strategy/AnswerPairsDrawingStrategy.js'
    import countdown from 'light-countdown'

    export default {
        data() {
            return {
                countDown: '<div id="countDown"></div>',
                tagHtml: '',
                viewer: {},
                canvasHtml: '<canvas id="canvas"></canvas>',
                answers: [],
                answer: "",
                answerPairsDrawingStrategy: {},
                submitDisabled: "disabled",
                contractId: this.$route.params.contractId,
                taskId: this.$route.params.taskId,
                imageLength: 0,
                currentPlace: 1,
                taskDescription: "",
                imgNames: this.$store.getters.getImgNames,
                tagsForAnnotation: this.$store.getters.getTagsForAnnotation,
                tagsOfTask: [],
            }
        },
        computed: {
            percent() {
                let result, lowerLimit;
                lowerLimit = 1 / this.imageLength * 100;
                result = this.currentPlace / this.imageLength * 100;
                result = result.toFixed(1);
                console.log(lowerLimit);
                console.log(result);
                lowerLimit = lowerLimit.toFixed(1);
                if (result != 100 && result < lowerLimit)
                    return parseFloat(lowerLimit);
                return parseFloat(result);
            }
        },
        mounted() {
            this.$nextTick(() => {
                this.canvas = document.getElementById("canvas");
                this.canvas.addEventListener("mousedown", this.canvasDown);
                this.canvas.addEventListener("mouseup", this.canvasUp);
                this.canvas.addEventListener("mousemove", this.canvasMove);
                this.canvas.addEventListener("touchstart", this.canvasDown);
                /**
                 *  this.$store.getters.getImgNames 取得的是一個對象, 只要裡面的imgNames數組就行
                 */
                this.imgNames = this.$store.getters.getImgNames.imgNames;     //给imgNames赋值
                this.tagsForAnnotation = this.$store.getters.getTagsForAnnotation;
                this.taskDescription = this.$store.getters.getTaskDescription;      //对于general来说，taskDescription尤其重要
                //this.tagsOfTask = this.$store.getters.getTagsOfTask;  TODO: 等待后端加上tags
                console.log(this.tagsForAnnotation);
                this.getImgNames();
                this.setCountDown();
            })
        },
        methods: {
            setCountDown() {
                let _this = this;
                countdown({
                    timeEnd: (new Date().getTime() + 900000),
                    selector: '#countDown',
                    msgPattern: '剩餘任務時間: {minutes}分{seconds}秒',
                    afterCount() {
                        _this.showMessage("timeOut");
                        _this.$router.push({path: '/profile'});
                    }
                });
            },
            getImgNames() {
                let viewer = new ImageViewer(this.canvas, this.imgNames, "http://localhost:8086/image/");
                let header = {headers: {Authorization: this.$store.getters.getToken}};
                this.imageLength = this.imgNames.length;
                console.log(this.imgNames);
                this.answerPairsDrawingStrategy = new AnswerPairsDrawingStrategy();
                viewer = new AnnotationViewer(this.answerPairsDrawingStrategy, viewer,
                    'http://localhost:8086/generalAnnotation/imgName/', this.$http);
                this.viewer = new AnnotationEditor(viewer, header,
                    'http://localhost:8086/generalAnnotation/saveAnnotations/', this.$http,0);
                this.viewer.drawCurrent(header, () => {
                    this.viewer.setTagUpdateCallback(this.updateTagHtml);
                    this.viewer.setTagTextUpdateCallback(this.updateTagText);
                    this.viewer.setTagTextGettingCallback(this.getTagText);
                    this.answer = this.viewer.getAnswer();
                });
            },
            updateTagHtml() {
                let temp = this.viewer.shareTag();
                console.log(this.tagHtml);
                if (temp === "") return;
                this.answers = this.answerPairsDrawingStrategy.interpretTag(temp);
            },
            updateTagText() {
            },
            getTagText() {
            },
            next() {
                this.viewer.setAnswer(this.answer);
                this.viewer.drawNext(() => {
                    this.answer = this.viewer.getAnswer();
                    if (this.currentPlace < this.imageLength)
                        this.currentPlace++;
                });
            },
            previous() {
                this.viewer.setAnswer(this.answer);
                this.viewer.drawPrevious(() => {
                    this.answer = this.viewer.getAnswer();
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
                this.viewer.forceUpdate(this.answers);
            },
            deleteTag(index) {
                // this.viewer.forceUpdate(this.tagText);
                // this.viewer.deleteTag(index);
            },
            submit() {
                this.viewer.setAnswer(this.answer);
                if (this.viewer.submitCurrent(this.imageLength)) {
                    this.$confirm('此任務已經完成, 請問是否進行下一個?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        // do things here;
                        let _this = this;
                        _this.$http.get('http://localhost:8086/task/' + _this.taskId, {
                            headers: {
                                Authorization: _this.$store.getters.getToken,
                            }
                        }).then(function (response) {
                            if (response.status === 204) {     //noContent    说明没有更多的图片可供标注
                                _this.runOutMessage();
                            }
                            else {
                                let imgNames = response.data;
                                _this.$store.commit('changeImgNames', imgNames);   //现在只需要改变imgNames就好
                                location.reload();
                            }
                        }).catch(function (error) {
                            console.log(error);
                        });
                    }).catch(() => {
                        this.$router.push({path: '/profile'});
                    });
                } else {
                    this.showMessage("notDone");
                }
            },
            runOutMessage() {
                this.$notify({
                    title: '系统提示',
                    message: '这个任务暂时没有可供标注的图片了，换个任务试试吧^_^',
                    type: 'success'
                });
                this.$router.push({path: '/profile'});
            },
            showMessage(type) {
                if ("success" === type) {
                    this.$message({
                        message: '任务已经提交，请安心等待结果和奖励^_^',
                        type: 'success'
                    })
                } else if ("notDone" === type) {
                    this.$message({
                        message: '您还没有完成这个任务^_^',
                        type: 'error'
                    })
                }
            },
            querySearch(queryString, cb) {
                let returnList = [];
                console.log(this.tagsForAnnotation);
                this.tagsForAnnotation.forEach((value, index, array) => {
                    returnList.push({"value": value});
                });
                console.log(returnList);
                cb(returnList);
            },
        }
    }
</script >

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
                            <!--<div v-html = "countDown" >{{countDown}}</div >-->
                        <div id = "countDown" ></div >
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
                                <el-tag size = "small" type = "primary"
                                        v-for = "item in tagsOfTask" class="tags">{{item}}</el-tag >
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
                                <span style = "font-size: 16px" >標註</span >
                            </template >
                            <div >
                                <div v-if = "isTagShow" v-for = "(val,index) in tagText" style = "margin-top: 0.3em" >
                                    <el-autocomplete size = "small" placeholder = "请输入内容" v-model = "val.text"
                                                     :fetch-suggestions = "querySearch" :disabled = val.old >
                                        <template slot = "prepend" >標註{{val.index+1}}</template >
                                        <el-button size = "mini" slot = "append" icon = "el-icon-delete"
                                                   @click = "deleteTag(index)" ></el-button >
                                    </el-autocomplete >
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

    .tags{
        margin-left: 15px;
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
    import FrameDrawingStrategy from '../../js/strategy/FrameDrawingStrategy.js'
    import AnnotationEditor from '../../js/AnnotaionEditor.js'
    import CountDown from '../../js/countDown/CountDown.js'

    export default {
        data: function () {
            return {
                countDown: '<div id="countDown"></div>',
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
                taskDescription: "",      //现在这个变得重要了
                imgNames: this.$store.getters.imgNames,
                tagsForAnnotation: this.$store.getters.tagsForAnnotation,
                tagsOfTask: this.$store.getters.tagsOfTask,
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
        }
        ,
        mounted() {
            this.$nextTick(() => {
                this.canvas = document.getElementById("canvas");
                this.canvas.addEventListener("mousedown", this.canvasDown);
                this.canvas.addEventListener("mouseup", this.canvasUp);
                this.canvas.addEventListener("mousemove", this.canvasMove);
                this.canvas.addEventListener("touchstart", this.canvasDown);
                console.log(this.canvas);
                /**
                 *  this.$store.getters.getImgNames 取得的是一個對象, 只要裡面的imgNames數組就行
                 */
                this.imgNames = this.$store.getters.getImgNames.imgNames;     //给imgNames赋值
                this.tagsForAnnotation = this.$store.getters.getTagsForAnnotation;         //得到任务的备选答案描述
                this.taskDescription = this.$store.getters.getTaskDescription;
                this.tagsOfTask = this.$store.getters.getTagsOfTask;
                console.log(this.tagsForAnnotation);
                this.getImgNames();
                this.setCountDown();
            })
        },
        /**
         * 在destroy之前把計時器刪除
         */
        beforeDestroy() {
            this.countDown.clearTimer();
        },
        methods: {
            setCountDown() {
                let _this = this;
                let countDown = document.querySelector("#countDown");
                console.log(countDown);
                this.countDown = new CountDown({
                    timeEnd: (new Date().getTime() + 900000),
                    selector: '#countDown',
                    msgPattern: '剩餘任務時間: {minutes}分{seconds}秒',
                    afterCount() {
                        _this.showMessage("timeOut");
                        _this.$router.push({path: '/profile'});
                    }
                });
                this.countDown.init();
            },
            getImgNames() {
                let viewer = new ImageViewer(this.canvas, this.imgNames, "http://localhost:8086/image/");
                let header = {headers: {Authorization: this.$store.getters.getToken}};
                this.imageLength = this.imgNames.length;
                console.log(this.imgNames);
                viewer = new AnnotationViewer(new FrameDrawingStrategy(), viewer,
                    'http://localhost:8086/frameAnnotation/imgName/', this.$http, 0);
                this.viewer = new AnnotationEditor(viewer, header,
                    'http://localhost:8086/frameAnnotation/saveAnnotations/', this.$http);
                this.viewer.drawCurrent(header, () => {
                    this.viewer.setTagUpdateCallback(this.updateTagHtml);
                    this.viewer.setTagTextUpdateCallback(this.updateTagText);
                    this.viewer.setTagTextGettingCallback(this.getTagText);
                });
            },
            updateTagHtml() {
                this.tagHtml = this.viewer.shareTag();
            }
            ,
            updateTagText() {
                if (this.isTagShow === false)
                    this.isTagShow = true;
                this.tagText = this.viewer.shareTagText();
            }
            ,
            getTagText() {
                return this.tagText;
            }
            ,
            next() {
                this.viewer.forceUpdate(this.tagText);
                this.viewer.drawNext(() => {
                    if (this.currentPlace < this.imageLength)
                        this.currentPlace++;
                });
            }
            ,
            previous() {
                this.viewer.forceUpdate(this.tagText);
                this.viewer.drawPrevious(() => {
                    if (this.currentPlace > 1)
                        this.currentPlace--;
                });
            }
            ,
            canvasDown(e) {
                this.viewer.handleMouseDown(e);
            }
            ,
            canvasUp(e) {
                this.viewer.handleMouseUp(e);
            }
            ,
            canvasMove(e) {
                this.viewer.handleMouseMove(e);
            }
            ,
            forceUpdate() {
                this.viewer.forceUpdate(this.tagText);
            }
            ,
            deleteTag(index) {
                this.viewer.forceUpdate(this.tagText);
                this.viewer.deleteTag(index);
            }
            ,
            submit() {
                if (this.viewer.submitCurrent(this.imageLength)) {
                    this.$confirm('此任務已經完成, 請問是否進行下一個?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {                   //进行下一个任务
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
                        })
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
                    const h = this.$createElement;
                    this.$message({
                        message: h('p', null, [
                            h('i', {style: 'font-family: Microsoft YaHei;'}, '任务已经提交，请安心等待结果和奖励^_^')
                        ]),
                        type: 'success'
                    });
                } else if ("notDone" === type) {
                    const h = this.$createElement;
                    this.$message({
                        message: h('p', null, [
                            h('i', {style: 'font-family: Microsoft YaHei;'}, '您还没有完成这个任务^_^')
                        ]),
                        type: 'error'
                    });
                } else if ("timeOut" === type) {
                    const h = this.$createElement;
                    this.$message({
                        message: h('p', null, [
                            h('i', {style: 'font-family: Microsoft YaHei;'}, '任務已超時^_^')
                        ]),
                        type: 'error'
                    });
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
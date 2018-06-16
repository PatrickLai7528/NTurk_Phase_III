<template >
    <div class = "add-task" >
        <el-row style = "height: 100%" >
            <el-col :span = "4" :offset = "1" >
                <el-card class = "guide-card" >
                    <div slot = "header" class = "guide-header" >
                        <span >創建任務指南</span >
                    </div >
                    <div style = "height: 500px" >
                        <el-steps direction = "vertical" :active = "activeAddTaskStep" >
                            <el-step :title = "addTaskStep[0].simpleDescription" :status = "addTaskStep[0].status" >
                                <div slot = "description" style = "font-size: 15px" >
                                    <div v-html = "addTaskStep[0].description" >
                                        {{addTaskStep[0].description}}
                                    </div >
                                </div >
                            </el-step >
                            <el-step :title = "addTaskStep[1].simpleDescription" >
                                <div slot = "description" >
                                    <div v-html = "addTaskStep[1].description" >
                                        {{addTaskStep[1].description}}
                                    </div >
                                </div >
                            </el-step >
                        </el-steps >
                    </div >
                </el-card >
            </el-col >
            <el-col :span = "12" :offset = "1" >
                <el-card class = "add-task-step" >
                    <div slot = "header" class = "add-task-step-header" >
                    <span >步驟{{activeAddTaskStep+1}}: {{addTaskStep[activeAddTaskStep].simpleDescription}}</span >
                    <el-button class = "the-operation-button" size = "mini" v-show = "activeAddTaskStep===1"
                               @click = "previousStep()" >上一步</el-button >
                    <el-button class = "the-operation-button" size = "mini"
                               @click = "buttonClickEvent()" >{{buttonContent}}</el-button >
                    </div >
                    <div class = "add-task-content" >
                        <task-info v-show = "activeAddTaskStep === 0" ></task-info >
                        <task-img-upload v-show = "activeAddTaskStep === 1" ></task-img-upload >
                    </div >
                    
                </el-card >
            </el-col >
        </el-row >
    </div >
</template >

<script >
    import taskImgUpload from './task-img-upload.vue'
    import taskInfo from './task-info.vue'
    import ElRow from "element-ui/packages/row/src/row";

    export default {
        components: {
            ElRow,
            taskImgUpload,
            taskInfo
        },
        name: "",
        computed: {
            buttonContent() {
                let resultContent;
                switch (this.activeAddTaskStep) {
                    case 0:
                        resultContent = "下一步";
                        break;
                    case 1:
                        resultContent = "完成";
                        break;
                    default:
                        throw new Error("unexpected add task step off" + this.activeAddTaskStep);
                }
                return resultContent;
            },
            buttonClickEvent() {
                let resultClickEvent;
                switch (this.activeAddTaskStep) {
                    case 0:
                        console.log("find me");
                        resultClickEvent = () => {
                            let successHandler, errorHandler;
                            successHandler = () => {

                                this.addTaskStep[0].status = "process";
                                this.activeAddTaskStep++;
                            };
                            errorHandler = () => {
                                this.addTaskStep[0].status = "error";
                            };
                            this.$bus.$emit("validate", successHandler, errorHandler);
                        };
                        break;
                    case 1:
                        resultClickEvent = function () {
                            this.$bus.$emit("onSubmit");
                        };
                        break;
                    default:
                        throw new Error("unexpected add task step off" + this.activeAddTaskStep);
                }
                return resultClickEvent;
            }
        },
        data() {
            return {
                imgNames: [],
                activeAddTaskStep: 0,
                addTaskStep: [
                    {
                        index: 0,
                        simpleDescription: "填寫任務資料及要求",
                        description:
                            `
								<div">
									<span>任務名稱: 應簡短地說明<strong style="color: #FF3B3F">任務是關於甚麼的、是怎麼樣的圖片集</strong></span><br>
									<span>任務描述: 用於提醒工人這個任務的工作內容, 用<strong style="color: #FF3B3F">提問形式</strong>更佳</span><br>
									<span>任務標籤: 用於推荐系統, 建議<strong style="color: #FF3B3F">使用系統內置標籤</strong></span><br>
									<span>任務類型: </span><br>
									<span>　　整體標註: 使用<strong style="color: #FF3B3F">問答形式</strong>, 工人根據你的任務描述來填寫答案</span><br>
									<span>　　區域劃分: 使用<strong style="color: #FF3B3F">不規則圖形</strong>標記, 可填寫每個標記的文字說明</span><br>
									<span>　　區域標註: 使用<strong style="color: #FF3B3F">矩形</strong>標記, 可填寫每個標記的文字說明</span><br>
									<span>工作要求: </span><br>
									<span>　　無要求: 對<strong style="color: #FF3B3F">所有工人</strong>參與</span><br>
									<span>　　經驗要求: 達到<strong style="color: #FF3B3F">指定經驗以上的工人</strong>才參與</span><br>
									<span>提供備選: 作為<strong style="color: #FF3B3F">標註的文字描述的選項</strong></span>
								</div>
							`,
                        status: "process",
                    }, {
                        index: 1,
                        simpleDescription: "上傳任務圖片集",
                        description: "",
                    }
                ]
            }
        },
        methods: {
            previousStep() {
                if (this.activeAddTaskStep > 0)
                    this.activeAddTaskStep--;
            },
            handleAddImg(payload) {
                this.imgNames = [];
                for (let e of payload.fileList2) {
                    this.imgNames.push(e.name);
                }
                console.log(this.imgNames);   //测试成功，已经将子组件emit的内容传到父组件了
            }
        }
    }
</script >

<style scoped >
    .el-col {
	    height: 100%;
	    min-width: 500px;
    }

    .my-task-info {
	    display: flex;
	    text-align: center;
	    justify-content: left;
	    margin-top: 2em;
	    min-height: 550px;
	    width: 100%;
	    height: 80%;
    }

    .taskimg {
	    padding-top: 2em;
	    width: 100%;
    }

    .add-task {
	    font-family: Microsoft YaHei;
	    height: 100%;
    }

    .guide-header {
	    font-family: Microsoft YaHei;
	    font-size: 24px;
	    text-align: left;
    }

    .guide-card {
	    margin-top: 2em;
	    margin-right: 0em;
	    margin-left: 2em;
	    margin-bottom: 0em;
	    height: 100%;
	    max-height: 680px;
    }

    .add-task-step {
	    margin-top: 2em;
	    margin-right: 2em;
	    margin-left: 0em;
	    margin-bottom: 0em;
	    height: 100%;
	    max-height: 680px;
	    overflow: auto;
    }

    .add-task-step-header {
	    font-family: Microsoft YaHei;
	    font-size: 24px;
	    text-align: left;
    }

    .the-operation-button {
	    font-family: Microsoft YaHei;
	    width: 8em;
	    background: #EFEFEF;
	    border-color: #EFEFEF;
	    color: #000;
	    /*margin: 0.5em;*/
	    margin-left: 3em;
    }
</style >
<template>
    <div class="add-task">
        <el-row style="height: 100%">
            <el-col :span="12" style="height: 100%">
                <div class="my-task-info">
                    <task-info v-bind:the-img="imgNames" style="display: inline-block"></task-info>
                </div>
            </el-col>
            <!--<transition name="el-fade-in">-->
            <!--<el-col :span="12" id="border-line">-->
            <!--</el-col>-->
            <el-col :span="12" id="border">
                <div class="taskimg">
                    <task-img-upload v-on:fileList2="handleAddImg"></task-img-upload>
                </div>
            </el-col>
            <!--</transition>-->
        </el-row>
    </div>
</template>

<script>
    import taskImgUpload from './task-img-upload.vue'
    import taskInfo from './task-info.vue'

    export default {
        components: {
            taskImgUpload,
            taskInfo
        },
        name: "",
        data(){
            return{
                imgNames:[],
            }
        },
        methods: {
            handleAddImg(payload){
                this.imgNames = [];
                for(let e of payload.fileList2){
                    this.imgNames.push(e.name);
                }
                console.log(this.imgNames);   //测试成功，已经将子组件emit的内容传到父组件了
            }
        }
    }
</script>

<style scoped>
    .my-task-info {
        display: flex;
        text-align: center;
        justify-content: left;
        margin-top: 5em;
        min-height: 550px;
        /*border-color: #A9A9A9;*/
        /*border-right-width: 0.4em;*/
        /*border-left-width: 0;*/
        /*border-top-width: 0;*/
        /*border-bottom-width: 0;*/
        /*border-style: solid;*/
        width: 100%;
        height: 80%;
    }

    .taskimg {
        /*display: flex;*/
        /*align-items: left;*/
        /*justify-content: left;*/
        /*display: -webkit-flex; !* Safari *!*/
        padding-top: 5em;
        padding-left: 2em;
        padding-right: 2em;
        width: 100%;
    }

    #border {
        border-color: #A9A9A9;
        border-left-width: 0.4em;
        border-right-width: 0;
        border-top-width: 0;
        border-bottom-width: 0;
        border-style: solid;
        height: 100%;
    }

    .add-task {
        font-family: Microsoft YaHei;
        height: 100%;
    }
</style>
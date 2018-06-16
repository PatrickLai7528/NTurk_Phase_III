<template >
    <div class = "user-brief-info" >
        <el-card class = "user-icon" >
            <img :src = iconName class = "image" >
            <div class = "info-div" >
                <div class = "user-name" >
                    <span >{{userName}}</span >
                </div >
                <div class = "detail-info" v-for = "info in infoList" >
                    <i v-bind:class = "info.iconType" >
                        <span class = "user-brief-info-label" >{{info.showName}}：{{info.value}}</span >
                    </i >
                </div >
                <div class = "detail-info" v-if = "isWorker" >
                    <i class = "el-icon-menu" >
                        <span class = "user-brief-info-label" >用户标签：</span >
                        <el-button size = "mini" @click = "dialogFormVisible = true" round
                                   class = "default-font-style" >编辑
                        </el-button >
                        <el-dialog class = "default-font-style" title = "修改您的用户标签" :visible.sync = "dialogFormVisible"
                                   :modal-append-to-body = "false" width = "600px"
                                   style = "text-align: center" :before-close = "beforeCloseDialog" >
                            <div class = "default-font-style" >
                                <el-tag
		                                :key = "tag"
		                                v-for = "tag in userTags"
		                                closable
		                                :disable-transitions = "false"
		                                class = "tag"
		                                @close = "deleteTag(tag)"
		                                style = "margin: 5px; font-size: 16px; font-weight: bold"
		                                type = "success"
                                >
                                    {{tag}}
                                </el-tag >
                            </div >
                            <div class = "splitLine sysTags" >
                                <el-checkbox-group v-model = "userTags" >
                                    <el-checkbox-button
		                                    class = "systemTag"
		                                    v-for = "tag in systemTags"
		                                    :label = "tag.value"
		                                    :key = "tag.value" >
                                    </el-checkbox-button >
                                </el-checkbox-group >
                            </div >
                        </el-dialog >
                    </i >
                </div >
            </div >
        </el-card >
    </div >
</template >

<script >
	import UserUtils from '../../js/utils/UserUtils.js'
    import DateUtils from '../../js/utils/DateUtils.js'
    import TagUtils from '../../js/utils/TagUtils.js'

    export default {
        data() {
            return {
                iconName: "",
                userName: "",
                infoList: {
                    /* 順序會影響, 不要打亂 */
                    type: {iconType: "el-icon-document", showName: "類別", value: ""},
                    province: {iconType: "el-icon-location", showName: "地址", value: ""},
                    emailAddress: {iconType: "el-icon-message", showName: "郵箱", value: ""},
                    createTime: {iconType: "el-icon-date", showName: "注冊日期", value: ""},
                    credit: {iconType: "el-icon-goods", showName: "积分", value: ""},
                    experiencePoint: {iconType: "el-icon-star-on", showName: "經驗", value: ""},
                    rank: {iconType: "el-icon-view", showName: "排名", value: ""}
                },
                isWorker: false,
                userTags: [],
                systemTags: [],
                dialogFormVisible: false
            };
        },
        created() {
            this.$bus.$on("refreshInfo", () => {
                this.loadInfo();
            });
        },
        mounted: function () {
            this.loadInfo();
        },
        methods: {
            // dateFormat(date) {
            // 	// date has the format: "yyyy-MM-dd hh:mm:ss"
            // 	return date === null ? "null" : date.substring(0, 10);
            // },
            doWhileGetInfoSuccess(response) {
                let data = response.data;
                this.iconName = "http://localhost:8086/image/" + data.iconName;
                this.infoList.type.value = UserUtils.isWorker(this) ? "工人" : "发起者";
                this.userName = data.nickname;
                this.infoList.province.value = data.province;
                this.infoList.emailAddress.value = data.emailAddress;
                this.infoList.createTime.value = DateUtils.simpleDateFormate(data.createTime);
                this.infoList.credit.value = data.credit;
                this.infoList.experiencePoint.value = data.experiencePoint;
                this.infoList.rank.value = data.rank;
                this.isWorker = UserUtils.isWorker(this);

                if (this.isWorker) {
                    this.userTags = ["食物"];
                    this.systemTags = TagUtils.getSystemTags();
                }
            },
            loadInfo() {
                let route = "http://localhost:8086/myInfo";
                let header = {headers: {Authorization: this.$store.getters.getToken}};
                this.$http.get(route, header)
                    .then(this.doWhileGetInfoSuccess)
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            deleteTag(tag) {
                this.userTags.splice(this.userTags.indexOf(tag), 1);
            },
            beforeCloseDialog (done) {
                if(this.userTags.length<3){
                    this.$message({
                        message: '请至少选择三个标签',
                        type: 'warning'
                    })
                }
                else{
                    this.$message({
                        message: '修改标签成功',
                        type: 'success'
                    })
                    done();
                }
            },
            message(msg, type) {
                this.$notify({
                    message: `<p style="font-family: Microsoft YaHei; font-size: 18px">${msg}</p>`,
                    type: type,
                    dangerouslyUseHTMLString: true,
                });
            },
        }
    };
</script >

<style >
    .user-brief-info {
	    height: 600px;
	    font-family: Microsoft YaHei;
    }

    .user-icon {
	    text-align: left;
    }

    .user-name {
	    font-weight: bold;
	    font-size: 24px;
	    margin-top: 14px;
	    line-height: 10px;
	    padding-bottom: 14px;
	    margin-bottom: 14px;
	    border-bottom-color: #A9A9A9;
	    border-bottom-width: 1px;
	    border-left-width: 0;
	    border-right-width: 0;
	    border-top-width: 0;
	    border-style: solid;
    }

    .image {
	    width: 200px;
	    height: 200px;
	    /*max-width: 200px;*/
    }

    .info-div {
	    padding: 10px;
	    text-align: left;
    }

    .detail-info {
	    font-size: 15px;
	    padding-bottom: 10px;
	    font-family: Microsoft YaHei;
    }

    .user-brief-info-label {
	    font-family: Microsoft YaHei;
	    padding-left: 9px;
    }

    .userTag {
	    margin: 5px;
	    font-size: 16px;
	    font-weight: bold;
    }

    .systemTag {
	    margin: 5px;
	    font-family: Microsoft YaHei;
    }

    .el-checkbox-button__inner {
	    border: 0 !important;
	    background-color: #f0f0f0;
	    border-radius: 4px;
    }

    .el-checkbox-button.is-checked .el-checkbox-button__inner {
	    box-shadow: 0 0 0 0 #8cc5ff !important;
    }

    .el-checkbox-button:first-child .el-checkbox-button__inner {
	    border-radius: 4px !important;
    }

    .el-checkbox-button:last-child .el-checkbox-button__inner {
	    border-radius: 4px !important;
    }

    .el-dialog__body {
	    padding: 10px 20px 30px 20px;
    }

    .splitLine {
	    margin-top: 10px;
	    padding-top: 10px;
	    border: 0 solid #dddddd;
	    border-top-width: 2px;
    }

    .sysTags {
	    max-height: 500px;
	    overflow-y: auto;
    }

    .default-font-style {
	    font-family: Microsoft YaHei;
    }
</style >
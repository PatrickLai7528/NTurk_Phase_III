<template>
    <div class="user-brief-info">
        <el-card class="user-icon">
            <img :src=iconName class="image">
            <div class="info-div">
                <div class="user-name">
                    <span>{{userName}}</span>
                </div>
                <div class="detail-info">
                    <i class="el-icon-document">
                        <span style="font-family: Microsoft YaHei; padding-left: 9px">类别：{{type}}</span>
                    </i>
                </div>
                <div class="detail-info">
                    <i class="el-icon-location">
                        <span style="font-family: Microsoft YaHei; padding-left: 9px">地址：{{province}}</span>
                    </i>
                </div>
                <div class="detail-info">
                    <i class="el-icon-message">
                        <span style="font-family: Microsoft YaHei; padding-left: 9px">邮箱：{{emailAddress}}</span>
                    </i>
                </div>
                <div class="detail-info">
                    <i class="el-icon-date">
                        <span style="font-family: Microsoft YaHei; padding-left: 9px">注册日期：{{createTime}}</span>
                    </i>
                </div>
                <div class="detail-info">
                    <i class="el-icon-goods">
                        <span style="font-family: Microsoft YaHei; padding-left: 9px">积分：{{credit}}</span>
                    </i>
                </div>
                <div class="detail-info">
                    <i class="el-icon-star-on">
                        <span style="font-family: Microsoft YaHei; padding-left: 9px">经验：{{experiencePoint}}</span>
                    </i>
                </div>
                <div class="detail-info">
                    <i class="el-icon-view">
                        <span style="font-family: Microsoft YaHei; padding-left: 9px">排名：{{rank}}</span>
                    </i>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                iconName: '',
                userName: '',
                type: '',
                province: '',
                emailAddress: '',
                createTime: '',
                credit: 0,
                experiencePoint: 0,
                rank: 0
            }
        },
        created() {
            this.$bus.$on("refreshInfo", ()=> {
                this.loadInfo();
            });
        },
        mounted: function () {
            this.loadInfo();
        },
        methods: {
            dateFormat(date) {
                // date has the format: "yyyy-MM-dd hh:mm:ss"
                return date === null ? 'null' : date.substring(0,10) ;
            },
            loadInfo() {
                let route = 'http://localhost:8086/myInfo';
                this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                    let data = response.data;
                    this.iconName = 'http://localhost:8086/image/' + data.iconName;
                    this.type = this.$store.getters.getUserType === 'WORKER'?'工人':'发起者';
                    this.userName = data.nickname;
                    this.province = data.province;
                    this.emailAddress = data.emailAddress;
                    this.createTime = this.dateFormat(data.createTime);
                    this.credit = data.credit;
                    this.experiencePoint = data.experiencePoint;
                    this.rank = data.rank;
                }).catch(function (error) {
                    console.log(error);
                });
            }
        }
    }
</script>

<style scoped>
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
</style>
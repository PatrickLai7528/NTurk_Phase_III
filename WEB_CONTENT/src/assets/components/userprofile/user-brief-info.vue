<template>
    <div class="user-brief-info">
        <el-card class="user-icon">
            <img :src=iconName class="image">
            <div class="info-div">
                <div class="user-name">
                    <span>{{userName}}</span>
                </div>
                <div class="detail-info" v-for="info in infoList">
                    <i v-bind:class="info.iconType">
                        <span class="user-brief-info-label">{{info.showName}}：{{info.value}}</span>
                    </i>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script>
	import UserUtils from '../../js/utils/UserUtils.js'
	import DateUtils from '../../js/utils/DateUtils.js'

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
				}
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
				console.log("before : " + data.createTime);
				this.infoList.createTime.value = DateUtils.simpleDateFormate(data.createTime);
				console.log("after : " + this.infoList.createTime.value);
				this.infoList.credit.value = data.credit;
				this.infoList.experiencePoint.value = data.experiencePoint;
				this.infoList.rank.value = data.rank;
			},
			loadInfo() {
				let route = "http://localhost:8086/myInfo";
				let header = {headers: {Authorization: this.$store.getters.getToken}};
				this.$http.get(route, header)
					.then(this.doWhileGetInfoSuccess)
					.catch(function (error) {
						console.log(error);
					});
			}
		}
	};
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

    .user-brief-info-label {
        font-family: Microsoft YaHei;
        padding-left: 9px;
    }
</style>
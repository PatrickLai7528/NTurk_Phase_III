<template>
    <div class="user-profile-main">
        <el-card>
            <el-tabs class="tabs">
                <el-tab-pane label="概述">
                    <el-row>
                        <el-col :span="18" :offset="3" class="title">
                            <span>任務记录</span>
                        </el-col>
                    </el-row>
                    <el-row class="functionPane">
                        <el-col :span="12">
                            <el-row v-for="(task,index) in tasks">
                                <el-col>
                                    <el-card class="tasks-brief" v-if="index % 2 == 0">
                                        <div slot="header" class="top-info">
                                            <span>{{tasks[index].taskName}}</span>
                                        </div>
                                        <div class="middle-info">
                                            <span>类型：{{tasks[index].taskCategory}}</span>
                                        </div>
                                        <div class="bottom-info" v-if="isWorker === true">
                                            <span>状态：{{tasks[index].contractStatus}}</span>
                                        </div>
                                        <div class="bottom-info" v-if="isWorker === false">
                                            <span>创建时间：{{tasks[index].createTime}}</span>
                                        </div>
                                    </el-card>
                                </el-col>
                            </el-row>
                        </el-col>
                        <el-col :span="12">
                            <el-row v-for="(task,index) in tasks">
                                <el-col>
                                    <el-card class="tasks-brief" v-if="index % 2 == 1">
                                        <div slot="header" class="top-info">
                                            <span>{{tasks[index].taskName}}</span>
                                        </div>
                                        <div class="middle-info">
                                            <span>类型：{{tasks[index].taskCategory}}</span>
                                        </div>
                                        <div class="bottom-info" v-if="isWorker === true">
                                            <span>状态：{{tasks[index].contractStatus}}</span>
                                        </div>
                                        <div class="bottom-info" v-if="isWorker === false">
                                            <span>创建时间：{{tasks[index].createTime}}</span>
                                        </div>
                                    </el-card>
                                </el-col>
                            </el-row>
                        </el-col>
                    </el-row>
                </el-tab-pane>
                <el-tab-pane label="我的任務">
                    <el-row>
                        <el-col :span="18" :offset="3" class="title">
                            <span>任务参与</span>
                        </el-col>
                    </el-row>
                    <el-row class="functionPane">
                        <div>
                            <task-lobby class="task-lobby" message="user" style="font-size: 16px"
                                        v-if="isWorker === true"></task-lobby>
                            <router-view v-if="isWorker === false" class="a"></router-view>
                        </div>
                    </el-row>
                </el-tab-pane>
                <el-tab-pane label="兌換中心">
                    <el-row>
                        <el-col :span="18" :offset="3" class="title">
                            <span>兌換活動</span>
                        </el-col>
                    </el-row>
                    <el-row class="functionPane">
                        <el-col :span="12" v-if="isWorker === true">
                            <el-row>
                                <el-col :span="24">
                                    <el-card class="tasks-brief">
                                        <div slot="header" class="top-info">
                                            <span>{{workerBasicExchange.name}}</span>
                                        </div>
                                        <div class="middle-info">
                                            <span>{{workerBasicExchange.description}}</span>
                                        </div>
                                        <div class="exchange-counter">
                                            <el-input-number size="mini" 　:min="1"
                                                             v-model="workerBasicExchange.counter"></el-input-number>
                                            <el-button type="text" class="join-exchange-event-button"
                                                       @click="exchange(0)">兌換
                                            </el-button>
                                        </div>
                                    </el-card>
                                </el-col>
                            </el-row>
                        </el-col>
                        <el-col :span="12" v-if="isWorker === true">
                            <el-row>
                                <el-col :span="24">
                                    <el-card class="tasks-brief">
                                        <div slot="header" class="top-info">
                                            <span>{{workerQuotaExchange.name}}</span>
                                        </div>
                                        <div class="middle-info">
                                            <span>{{workerQuotaExchange.description}}</span>
                                        </div>
                                        <div class="exchange-counter">
                                            <el-input-number size="mini" 　:min="1"
                                                             v-model="workerQuotaExchange.counter"></el-input-number>
                                            <el-button type="text" class="join-exchange-event-button"
                                                       @click="exchange(1)">兌換
                                            </el-button>
                                        </div>
                                    </el-card>
                                </el-col>
                            </el-row>
                        </el-col>
                        <el-col :span="12" v-if="isWorker === false">
                            <el-row>
                                <el-col :span="24">
                                    <el-card class="tasks-brief">
                                        <div slot="header" class="top-info">
                                            <span>{{requesterExchange.name}}</span>
                                        </div>
                                        <div class="middle-info">
                                            <span>{{requesterExchange.description}}</span>
                                        </div>
                                        <div class="exchange-counter">
                                            <el-input-number size="mini" 　:min="1"
                                                             v-model="requesterExchange.counter"></el-input-number>
                                            <el-button type="text" class="join-exchange-event-button"
                                                       @click="exchange(2)">兌換
                                            </el-button>
                                        </div>
                                    </el-card>
                                </el-col>
                            </el-row>
                        </el-col>
                    </el-row>
                </el-tab-pane>
                <el-tab-pane label="消息中心">
                    <el-row class="functionPane">
                        <el-col>
                            <el-alert
                                    v-for="(notification,index) in notificationList"
                                    :title="notification.title"
                                    type="info"
                                    :description="notification.content"
                                    :closable="false"
                                    style="text-align: left; margin-bottom: 20px"
                            >
                            </el-alert>
                        </el-col>
                    </el-row>
                </el-tab-pane>
            </el-tabs>
        </el-card>
    </div>
</template>

<script>
	import taskLobby from '../common/common-main.vue'
	import requesterLobby from '../common/common-requester-lobby.vue'
	import UserUtils from '../../js/utils/UserUtils.js'
	import DateUtils from '../../js/utils/DateUtils.js'
	import TranslateUtils from '../../js/utils/TranslateUtils.js'

	export default {
		name: "",
		components: {
			taskLobby,
			requesterLobby,
		},
		data() {
			return {
				isWorker: null,
				notificationList: [{
					title: '假裝有消息',
					content: '假裝有描述',
				}],
				tasks: [{
					taskName: '',
					taskCategory: '',
					contractStatus: '',
					createTime: ''
				}],
				workerBasicExchange: {
					name: '基本兌換',
					description: '1積分能兑换1元',
					money: '1',
					requiredCredit: '1',
					counter: 0
				},
				workerQuotaExchange: {
					name: '定額兌換',
					description: '1000積分能兌換1500元',
					money: '1500',
					requiredCredit: '1000',
					counter: 0
				},
				requesterExchange: {
					name: '基本兌換',
					endTime: '',
					description: '1元能兑换1積分',
					money: '1',
					requiredCredit: '1',
					counter: 0
				}
			}
		},
		created() {
			let _this = this;
			this.$bus.$on("refreshPane", () => {
				this.isWorker = UserUtils.isWorker(_this);
				this.loadTask();
				this.loadMessage();
			});
		},
		mounted: function () {
			this.isWorker = UserUtils.isWorker(this);
			this.loadTask();
			this.loadMessage();
		},
		methods: {
			decideGetTaskUrl() {
				if (this.isWorker)
					return "http://localhost:8086/workerTasks";
				else
					return "http://localhost:8086/requesterTasks";
			},
			doWhileGetTaskSuccess(response) {
				this.tasks = [];
				for (let i = response.data.length - 1; i >= 0; i--) {
					let data = response.data[i];
					let createTime = data.createTime;
					data.createTime = DateUtils.simpleDateFormate(createTime);
					this.tasks.push(data);
				}
				this.translate();
			},
			loadTask() {
				let header = {headers: {Authorization: this.$store.getters.getToken}};
				this.$http.get(this.decideGetTaskUrl(), header)
					.then(this.doWhileGetTaskSuccess)
					.catch((error) => {
						console.log(error);
					})
			},
			translate: function () {
				this.tasks.forEach((value, index, array) => {
					value.taskCategory = TranslateUtils.translateTaskCategory(value.taskCategory);
					if (this.isWorker) /* 眾包發起者不顯示合同狀態 */
						value.contractStatus = TranslateUtils.translateContractStatus(value.contractStatus)
				});
			},
			loadMessage() {
				let getMessageUrl = "http://localhost:8086/message";
				let getMessageHeader = {headers: {Authorization: this.$store.getters.getToken}};
				this.$http.get(getMessageUrl, getMessageHeader)
					.then(this.doWhileGetMessageSuccess)
					.catch(function (error) {
						console.log(error);
					})
			},
			doWhileGetMessageSuccess(response) {
				this.notificationList = [];
				for (let i = response.data.length - 1; i >= 0; i--) {
					response.data[i].title = "系统消息：" + response.data[i].title;
					this.notificationList.push(response.data[i]);
				}
			},
			exchange(index) {
				let point, money, route;
				if (this.isWorker) {
					if (index === 0) {
						point = this.workerBasicExchange.counter;
						money = point;
					}
					else {
						point = this.workerQuotaExchange.counter;
						money = point / 1000 * 1500 + point % 1000;
					}
					route = "http://localhost:8086/userProfile/worker/exchange/";
				}
				else {
					money = this.requesterExchange.counter;
					point = money;
					route = "http://localhost:8086/userProfile/requester/exchange/";
				}

				let data = {
					point: point,
					money: money,
				};

				this.$http.post(route, data, {headers: {Authorization: this.$store.getters.getToken}}
				).then((response) => {
					this.messageHandler();
					this.refreshInfo();
					this.loadMessage();
					this.workerBasicExchange.counter = 1;
					this.workerQuotaExchange.counter = 1;
					this.requesterExchange.counter = 1;
				}).catch((error) => {
					if (error.response.status === 400) {
						this.badMessage();
					}
					else {
						console.log(error);
					}
					this.workerBasicExchange.counter = 1;
					this.workerQuotaExchange.counter = 1;
					this.requesterExchange.counter = 1;
				})
			},
			messageHandler() {
				this.$message({
					message: '兑换成功^_^',
					type: 'success'
				})
			},
			badMessage() {
				this.$alert('您的积分不足，多做点任务再来兑换吧^_^', '系统警告', {
					confirmButtonText: '确定'
				});
			},
			refreshInfo() {
				this.$bus.$emit("refreshInfo");
			}
		}
	}
</script>

<style scoped>
    .tabs {
        font-family: Microsoft YaHei;
        overflow: auto;
    }

    .functionPane {
        height: 500px;
        overflow: auto;
    }

    .tasks-brief {
        margin: 1em;
        text-align: left;
    }

    .top-info {
        font-weight: bold;
        font-size: 20px;
    }

    .middle-info {
        font-size: 16px;
        color: #444444;
    }

    .bottom-info {
        font-size: 16px;
        margin-top: 14px;
        color: #FF3B3F;
        line-height: 10px;
        font-weight: lighter;
    }

    .task-time-line {
        margin: 1em;
    }

    .task-lobby {
        padding: 0;
        margin: 0;
    }

    .title {
        font-size: 1.5em;
        background: rgba(202, 235, 242, 0.5);
        font-weight: lighter;
    }

    .join-exchange-event-button {
        margin-left: 1em;
        padding: 2px;
        color: #FF3B3F;
        font-family: Microsoft YaHei;
        font-weight: bolder;
        background: #EFEFEF;
        width: 40px;
        height: 30px;
    }

    .exchange-counter {
        font-size: 16px;
        margin-top: 14px;
        line-height: 10px;
        font-weight: lighter;
    }

    .a {
        padding: 0px;

    }
</style>
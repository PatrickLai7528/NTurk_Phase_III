<template>
    <div class="main">
        <el-table :data="tableData" center="all"
                  class="table" :row-class-name="tableRowClassName">
            <el-table-column type="expand" style="margin-bottom: 0px">
                <template slot-scope="props">
                    <el-form label-position="left" class="tableSlot">
                        <el-row>
                            <el-col :span="6">
                                <el-form-item label="發起者">
                                    <span class="tableSlotSpan">{{props.row.requesterName}}</span>
                                </el-form-item>
                            </el-col>

                            <el-col :span="6">
                                <el-form-item label="已加入人数">
                                    <span class="tableSlotSpan">{{props.row.attendance}}</span>
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                                <el-form-item label="限制人数">
                                    <span class="tableSlotSpan">{{props.row.capacity}}</span>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="24">
                                <el-form-item label="任务描述">
                                    <span class="tableSlotSpan">{{ props.row.taskDescription }}</span>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>
                </template>
            </el-table-column>
            <el-table-column prop="taskCategoryChi" label="类别" sortable
                             :filters="[
                                {text: '整體標註', value: 'GENERAL'},
                                {text: '區域標註', value: 'FRAME'},
                                {text: '區域劃分', value: 'SEGMENT'}]"
                             :filter-method="filterCategoryHandler"></el-table-column>
            <el-table-column prop="taskName" label="任务名称"></el-table-column>
            <!--<el-table-column prop="taskDescription" label="任务描述"></el-table-column>-->
            <el-table-column prop="totalReward" label="總積分" sortable></el-table-column>
            <el-table-column prop="formatEndTime" label="截止日期" sortable></el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <!--<router-link v-blind:to="'pagesSelector(taskCategory)'">-->
                    <!--<router-link>-->
                    <el-button plain size="mini" class="submitButton"
                               @click="handleClick(scope.row,scope.row.taskId,scope.row.taskCategory)">
                        进入
                    </el-button>
                    <!--</router-link>-->
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
	import DateUtils from '../../js/utils/DateUtils.js'

	export default {
		props: ['message'],
		data() {
			return {
				tableData: [{
					taskId: '',
					taskName: '',
					requesterId: '',
					requesterName: '',
					taskCategory: '',
					taskCategoryChi: '',
					requester: '',
					reward: '',
					endTime: '',
					taskState: '',
					totalReward: '',    //任务的总积分奖励
					formatEndTime: '',   //将读出来的时间进行格式化再显示
					attendance: 0,      //表示有多少人参加了这个标注任务
					capacity: 0,          //表示这个标注任务共限制多少人参加
				}],
				contractData: '',
			}
		}
		,
		mounted: function () {
			let _this = this;
			this.$nextTick(function () {
				console.log(_this.message);
				_this.getAll();
			})
		},
		methods: {
			filterCategoryHandler(value, row, column) {      //对任务的类别进行筛选
				return row.taskCategory === value;
			},
			doWhileGetTableDataSuccess(response) {
				this.tableData = response.data;
				for (let e of this.tableData) {
					e.formatEndTime = DateUtils.dateFormat(e.endTime); //将日期进行格式化
					if (e.capacity === 2147483647) {
						e.capacity = "无限制";
					}
				}
				this.translate();
			},
			decideGetTableDataUrl() {
				if (this.message === "user")
					return "http://localhost:8086/workerTasks"; //用户中心得到的是以前存在的任务
				else
					return "http://localhost:8086/newTasks"
			},
			getTableData() {
				let header = {Authorization: this.$store.getters.getToken};
				let getUrl = this.decideGetTableDataUrl();
				this.$http.get(
					getUrl,
					{headers: header}
				).then(this.doWhileGetTableDataSuccess).catch(function (error) {
					console.log(error);
				});
			},
			translate: function () {
				for (let i = 0; i < this.tableData.length; i++) {
					if (this.tableData[i].taskCategory === "GENERAL") {
						this.tableData[i].taskCategoryChi = "整體標註";
					} else if (this.tableData[i].taskCategory === "FRAME") {
						this.tableData[i].taskCategoryChi = "區域標註";
					} else if (this.tableData[i].taskCategory === "SEGMENT") {
						this.tableData[i].taskCategoryChi = "區域劃分";
					}
				}
			},
			handleClick(row, Id, taskCategory) {
				let _this = this;
				const h = this.$createElement;
				this.$message({
					message: h('p', null, [
						h('i', {style: 'color: teal'}, '參加成功:' + taskCategory)
					])
				});

				function Contract(taskId, workerId) {
					this.taskId = taskId;
					this.workerId = workerId;
				}

				if (taskCategory === "GENERAL") {    //不仅要跳转还要判断是否要压入一个新的contract
					if (_this.message !== 'user') {
						let contract = new Contract(Id, _this.$store.getters.getUserId);
						_this.$http.post('http://localhost:8086/contract', JSON.stringify(contract), {
							headers: {
								Authorization: _this.$store.getters.getToken,
								'Content-Type': 'application/json'
							}
						}).then(function (response) {
							_this.$router.push({name: 'general', params: {taskId: Id}});
						}).catch(function (error) {
							console.log(error);
						})

					} else {
						//在这里判断这个任务是不是已经提交了，如果已经提交，不能跳转到修改界面
						_this.$http.get('http://localhost:8086/contract/taskId/' + Id, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
							_this.contractData = response.data;
							if (_this.contractData.contractStatus === 'COMPLETED') {
								_this.messageHandler();
							}
							else if (_this.contractData.contractStatus === 'ABORT') {
								_this.badMessage();
							}
							else {
								_this.$router.push({name: 'general', params: {taskId: Id}});
							}
						}).catch(
							function (error) {
								console.log(error)
							}
						);
					}
				} else if (taskCategory === "SEGMENT") {
					if (_this.message !== 'user') {
						let contract = new Contract(Id, _this.$store.getters.getUserId);
						_this.$http.post('http://localhost:8086/contract', JSON.stringify(contract), {
							headers: {
								Authorization: _this.$store.getters.getToken,
								'Content-Type': 'application/json'
							}
						}).then(function (response) {
							_this.$router.push({name: 'segment', params: {taskId: Id}});
						}).catch(function (error) {
							console.log(error);
						})

					}
					else {
						_this.$http.get('http://localhost:8086/contract/taskId/' + Id, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
							_this.contractData = response.data;
							if (_this.contractData.contractStatus === 'COMPLETED') {
								_this.messageHandler();
							}
							else if (_this.contractData.contractStatus === 'ABORT') {
								_this.badMessage();
							}
							else {
								_this.$router.push({name: 'segment', params: {taskId: Id}});
							}
						}).catch(
							function (error) {
								console.log(error)
							}
						);
					}
				} else if (taskCategory === "FRAME") {
					if (_this.message !== 'user') {
						let contract = new Contract(Id, _this.$store.getters.getUserId);
						_this.$http.post('http://localhost:8086/contract', JSON.stringify(contract), {
							headers: {
								Authorization: _this.$store.getters.getToken,
								'Content-Type': 'application/json'
							}
						}).then(function (response) {
							_this.$router.push({name: 'frame', params: {taskId: Id}});
						}).catch(function (error) {
							console.log(error);
						})

					}
					else {
						_this.$http.get('http://localhost:8086/contract/taskId/' + Id, {headers: {Authorization: _this.$store.getters.getToken}}).then(function (response) {
							_this.contractData = response.data;
							if (_this.contractData.contractStatus === 'COMPLETED') {
								_this.messageHandler();
							}
							else if (_this.contractData.contractStatus === 'ABORT') {
								_this.badMessage();
							}
							else {
								_this.$router.push({name: 'frame', params: {taskId: Id}});
							}
						}).catch(
							function (error) {
								console.log(error)
							}
						);
					}
				}
			}
			,
			messageHandler() {
				this.$message({
					message: '您已经提交了该任务，不能对结果进行修改，请安心等待结果和奖励^_^',
					type: 'success'
				})
			},
			badMessage() {
				this.$alert('这个任务已经过期了，不能对结果进行修改', '系统警告', {
					confirmButtonText: '确定'
				});
			},
			getAll() {
				this.getTableData();
			}
			,
			tableRowClassName({row, rowIndex}) {
				console.log("here" + rowIndex);
				if (rowIndex % 2 === 1) {
					return 'odd-row';
				} else {
					return 'even-row';
				}
			}
			,
		}
	}
</script>

<style>
    .main {
        padding-left: 3em;
        padding-right: 3em;
        padding-top: 5em;
        font-family: Microsoft YaHei;
        /*background-image: url(../../images/mainbg.jpg);*/
        /*background-size: auto 100%;*/
        height: 650px;
    }

    .table {
        width: 100%;
        text-align: left;
        font-size: 1em;
    }

    .tableSlot {
        margin-bottom: 0;
    }

    .tableSlotSpan {
        font-weight: lighter;
        color: #FF3B3F;
    }

    .el-table .even-row {
        /*background: #CAFBF2;*/
        /*background: rgba(202, 235, 242, 0.5);*/
        background: #EFEFEF;
    }

    .el-table .odd-row {
        background: #FFFFFF;
        /*background: rgba(169, 169, 169, 0.5);*/
    }

    .submitButton {
        font-family: Microsoft YaHei;
        background: #A9A9A9;
        border-color: #A9A9A9;
        color: #FFFFFF;
        font-size: 13px;
    }

    .cell {
        display: table-cell;
        vertical-align: middle;
    }
</style>
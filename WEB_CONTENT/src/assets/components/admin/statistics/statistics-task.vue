<template>
    <el-container class="statistics-task">
        <el-main>
            <el-tabs style="width: 100%; overflow-y: hidden" class="admin-tabs">
                <el-tab-pane label="任务参与">
                    <div id="taskParticipation" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
                <el-tab-pane label="任务状态">
                    <div id="taskState" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
                <el-tab-pane label="任务增长">
                    <div id="taskGrowth" style="height: 600px; width: 900px"></div>
                </el-tab-pane>
            </el-tabs>
        </el-main>
    </el-container>
</template>

<script>
	import TranslateUtils from '../../../js/utils/TranslateUtils.js'

	export default {
		mounted: function () {
			this.$nextTick(() => {
				this.setTaskParticipation();
				this.setTaskState();
				this.setTaskGrowth();
			})
		},
		methods: {
			setTaskParticipation: function () {
				let echarts = require('echarts');
				let myChart = echarts.init(document.getElementById('taskParticipation'));

				let route = 'http://localhost:8086/admin/task/taskParticipation';
				this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response) => {
					// let data = this.translate(response.data);
                    let data = [
                        {name: '整体标注', value: 4345},
                        {name: '画框标注', value: 3564},
                        {name: '区域标注', value: 3947},
                    ];
					let taskList = data.map((item) => {
						return item.name;
					});

					let option = {
						textStyle: {
							fontSize: 20
						},
						title: {
							text: '任务参与统计',
							subtext: '参与人数',
							x: 'center',
							textStyle: {
								fontSize: 20
							}
						},
						tooltip: {
							trigger: 'item',
							formatter: "{a} <br/>{b}: {c}人 ({d}%)",
							textStyle: {
								fontSize: 20
							}
						},
						legend: {
							orient: 'vertical',
							left: 'left',
							data: taskList
						},
						series: [
							{
								name: '参与人数统计',
								type: 'pie',
								radius: '70%',
								center: ['40%', '50%'],
								data: data
							}
						],
						color: [
							'#FB7293', '#FFDB5C',
							'#37A2DA', '#9FE6B8',
							'#FF9F7F', '#FB7293',
							'#E7BCF3', '#8378EA',
							'#32C5E9',
						]
					};
					// 使用刚指定的配置项和数据显示图表。
					myChart.setOption(option);
				}).catch(function (error) {
					console.log(error);
				});
			},
			setTaskState: function () {
				let echarts = require('echarts');
				let myChart = echarts.init(document.getElementById('taskState'));

				let route = 'http://localhost:8086/admin/task/taskState';
				this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response) => {
					console.log("i am in setTaskState");
					console.log(response.data);
					// let data = this.translate(response.data);
                    let data = [
                        {taskCategory: '整体标注', completed: 189, inProgress: 142},
                        {taskCategory: '画框标注', completed: 163, inProgress: 154},
                        {taskCategory: '区域标注', completed: 158, inProgress: 167},
                    ];

					let taskCategoryList = data.map((item) => {
						return item.taskCategory;
					});
					let inProcessTaskList = data.map((item) => {
						return item.inProgress;
					});
					let completedTaskList = data.map((item) => {
						return item.completed;
					});

					let option = {
						textStyle: {
							fontSize: 20
						},
						title: {
							text: '任务状态统计',
							subtext: '已完成/进行中',
							textStyle: {
								fontSize: 20
							},
						},
						tooltip: {
							trigger: 'axis',
							axisPointer: {
								type: 'shadow'
							},
							textStyle: {
								fontSize: 20
							},
						},
						legend: {
							data: ['已完成', '进行中']
						},
						grid: {
							left: '3%',
							right: '3%',
							top: '15%',
							bottom: '4%',
							containLabel: true
						},
						xAxis: {
							type: 'category',
							data: taskCategoryList,
							axisLabel: {
								textStyle: {
									fontSize: 16
								}
							},
						},
						yAxis: {
							type: 'value',
							axisLabel: {
								textStyle: {
									fontSize: 16
								}
							},
						},
						series: [
							{
								name: '已完成',
								type: 'bar',
								barWidth: '30%',
								data: completedTaskList
							},
							{
								name: '进行中',
								type: 'bar',
								barWidth: '30%',
								data: inProcessTaskList
							}
						]
					};
					// 使用刚指定的配置项和数据显示图表。
					myChart.setOption(option);
				}).catch(function (error) {
					console.log(error);
				});
			},
			setTaskGrowth: function () {
				let echarts = require('echarts');
				let myChart = echarts.init(document.getElementById('taskGrowth'));

				let route = 'http://localhost:8086/admin/task/taskGrowth';
				this.$http.get(route, {headers: {Authorization: this.$store.getters.getToken}}).then((response) => {
					//let data = this.translate(response.data);
                    let data = [
                        {date: "2018/05/25", general: 30, frame: 25, segment: 23},
                        {date: "2018/05/26", general: 26, frame: 32, segment: 18},
                        {date: "2018/05/27", general: 24, frame: 28, segment: 27},
                        {date: "2018/05/28", general: 28, frame: 33, segment: 30},
                        {date: "2018/05/29", general: 22, frame: 26, segment: 35},
                        {date: "2018/05/30", general: 19, frame: 27, segment: 29},
                        {date: "2018/06/01", general: 24, frame: 30, segment: 28},
                        {date: "2018/06/02", general: 29, frame: 25, segment: 25},
                        {date: "2018/06/03", general: 31, frame: 22, segment: 29},
                        {date: "2018/06/04", general: 28, frame: 20, segment: 26},
                        {date: "2018/06/05", general: 25, frame: 24, segment: 32},
                        {date: "2018/06/06", general: 23, frame: 28, segment: 34},
                        {date: "2018/06/07", general: 31, frame: 27, segment: 27},
                        {date: "2018/06/08", general: 27, frame: 26, segment: 30},
                    ];
                    let dateList = data.map((item) => {
						return item.date;
					});
					let generalList = data.map((item) => {
						return item.general;
					});
					let frameList = data.map((item) => {
						return item.frame;
					});
					let segmentList = data.map((item) => {
						return item.segment;
					});

					let option = {
						textStyle: {
							fontSize: 20
						},
						title: {
							text: '每日任务发布数量',
							textStyle: {
								fontSize: 20
							},
						},
						tooltip: {
							trigger: 'axis',
							textStyle: {
								fontSize: 20
							},
						},
						legend: {
							data: ['整体标注', '画框标注', '区域标注'],
							textStyle: {
								fontSize: 20
							},
						},
						grid: {
							left: '3%',
							right: '10%',
							bottom: '3%',
							containLabel: true
						},
						xAxis: {
							type: 'category',
							boundaryGap: false,
							data: dateList,
							axisLabel: {
								textStyle: {
									fontSize: 16
								}
							},
						},
						yAxis: {
							type: 'value',
							axisLabel: {
								textStyle: {
									fontSize: 16
								}
							},
						},
						series: [
							{
								name: '整体标注',
								type: 'line',
                                smooth: true,
								data: generalList,
								lineStyle: {
									width: 5
								}
							},
							{
								name: '画框标注',
								type: 'line',
                                smooth: true,
								data: frameList,
								lineStyle: {
									width: 5
								}
							},
							{
								name: '区域标注',
								type: 'line',
                                smooth: true,
								data: segmentList,
								lineStyle: {
									width: 5
								}
							}
						]
					};
					// 使用刚指定的配置项和数据显示图表。
					myChart.setOption(option);
				}).catch(function (error) {
					console.log(error);
				});
			},
			translate: function (data) {
				// console.log(data);
				// data.forEach((value, index, array) => {
				// 	// console.log("in translate function");
				// 	// console.log(value);
				// 	// console.log(index);
				// 	// console.log(array);
				// 	// value.name = TranslateUtils.translateTaskCategory(value.name);
				// 	// value.taskCategory = TranslateUtils.translateTaskCategory(value.taskCategory);
				// });
				for (let i = 0; i < data.length; i++) {
					if (data[i].name === "GENERAL") {
						data[i].name = "整体标注";
					} else if (data[i].name === "FRAME") {
						data[i].name = "画框标注";
					} else if (data[i].name === "SEGMENT") {
						data[i].name = "区域标注";
					}
				}
				for (let j = 0; j < data.length; j++) {
					if (data[j].taskCategory === "GENERAL") {
						data[j].taskCategory = "整体标注";
					} else if (data[j].taskCategory === "FRAME") {
						data[j].taskCategory = "画框标注";
					} else if (data[j].taskCategory === "SEGMENT") {
						data[j].taskCategory = "区域标注";
					}
				}
				return data;
			}
		}
	}
</script>

<style scoped>
    .el-main {
        padding: 0;
    }

    .statistics-task {
        font-family: Microsoft YaHei;
        text-align: left;
        font-size: 20px;
    }
</style>
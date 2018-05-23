<template>
    <el-menu
            :default-active="$route.path"
            class="menu"
            mode="horizontal"
            background-color="#EFEFEF"
            text-color="#000"
            active-text-color="#FF3F3B"
            :router="true"
    >
        <!--background-color="#4484CE"-->
        <el-menu-item index="/entry">NTurk</el-menu-item>
        <el-menu-item index="/about">關於</el-menu-item>
        <el-menu-item index="/profile" v-if="showProfile()">會員中心</el-menu-item>
        <el-menu-item index="/tasklobby" v-if="showTaskLobby()">任務大廳</el-menu-item>
        <el-menu-item index="/addTask" v-if="showAddTask()">创建任务</el-menu-item>
        <el-menu-item index="/admin" v-if="showAdmin()">管理员</el-menu-item>
        <el-menu-item index="#" v-if="showLogout()" class="logout-icon-menu-item"
                      @click="logout()">
            登出
            <!--<img :src="logoutIcon" class="logout-icon">-->
        </el-menu-item>
    </el-menu>
</template>
<script>
	import UserUtils from '../../js/UserUtils.js';
	export default {
		data() {
			return {
				logoutIcon: "./src/assets/images/logout.png",
				activeIndex: "/entry",
				searchFor: '',
			};
		},
		methods: {
			showLogin() {
				return !UserUtils.isWorker(this) && !UserUtils.isRequester(this)
					&& !UserUtils.isAdmin(this);
			},
			showTaskLobby() {
				return UserUtils.isWorker(this);
			},
			showProfile() {
				return UserUtils.isWorker(this) || UserUtils.isRequester(this);
			},
			showAdmin() {
				return UserUtils.isAdmin(this);
			},
			showAddTask() {
				return UserUtils.isRequester(this);
			},
			showLogout() {
				return UserUtils.isWorker(this) || UserUtils.isRequester(this)
					|| UserUtils.isAdmin(this);
			},
			logout() {
				this.$store.dispatch('doSignOut');
				console.log(this.$store.state);
				console.log(sessionStorage);
				this.$router.push({path: '/entry'});
				this.$router.forward();
			}
		}
	}
</script>
<style scoped>
    .menu {
        font-family: Microsoft YaHei;
    }

    .logout-icon-menu-item {
        position: absolute;
        right: 0;
    }


</style>
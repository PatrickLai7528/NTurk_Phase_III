<template>
    <el-container class="main-el-container">
        <el-header class=el-header>
            <our-common-header></our-common-header>
        </el-header>
        <el-main class=el-main>
            <transition name="el-fade-in-linear">
                <router-view></router-view>
            </transition>
            <!--<our-common-main></our-common-main>-->
        </el-main>
        <el-footer class=el-footer height="auto">
            <our-common-footer></our-common-footer>
        </el-footer>
    </el-container>
</template>

<script>
    import ourCommonHeader from
            './assets/components/common/common-header.vue'
    import ourCommonMain from './assets/components/common/common-main.vue'
    import ourCommonFooter from './assets/components/common/copyright-footer.vue'
    import ourEntry from './assets/components/entry/entry.vue'
    import ourAddTask from './assets/components/addtask/add-task.vue'
    import ourAbout from './assets/components/common/common-about.vue'
    import ourUserProfile from './assets/components/userprofile/user-profile.vue'
    import ourAdmin from './assets/components/admin/admin.vue'

    export default {
        data() {
            return {
                fullHeight: document.documentElement.clientHeight
            };
        },
        methods: {
            randomTransition: function () {
                let random = Math.random() * 100;
                let index = Math.round(random) % 5; //四舍五入
                console.log(index);
                switch (index) {
                    case '0':
                        return "el-fade-in-linear";
                    case '1':
                        return "el-fade-in";
                    case '2':
                        return "el-zoom-in-center";
                    case '3':
                        return "el-zoom-in-top";
                    case '4':
                        return "el-zoom-in-bottom";
                }
            }
        },
        components: {
            ourEntry,
            ourCommonHeader,
            ourCommonMain,
            ourCommonFooter,
            ourAddTask,
            ourAbout,
            ourUserProfile,
            ourAdmin
        }
    };
</script>

<style scoped>
    #app {
        font-family: Helvetica, sans-serif;
        text-align: center;
        background: #99CED4;
    }

    .el-header {
        text-align: center;
        line-height: 60px;
        position: fixed;
        width: 100%;
        height: 8%;
        max-height: 20%;
        top: 0;
        left: 0;
        z-index: 2;
        padding: 0;
    }

    .el-footer {
        text-align: right;
        position: fixed;
        width: 100%;
        bottom: 0;
        left: 0;
        z-index: 2;
        padding: 0;
    }

    .el-main {
        /*background-color: #B4DBC0;*/
        /*border-color: #e5e9f2;*/
        text-align: center;
        z-index: 1;
        margin: 0;
        padding: 0;
        height: 800px;
        overflow: visible;
    }

    .main-el-container {
        position: absolute;
        left: 0;
        right: 0;
        margin-top: 54px;
    }

    .el-container {
        margin-top: 30px;
    }
</style>

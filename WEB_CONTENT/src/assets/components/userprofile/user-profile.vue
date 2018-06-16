<template>
    <el-container id="user-profile">
        <el-aside id="user-profile-el-aside">
            <user-brief-info></user-brief-info>
        </el-aside>
        <el-main id="user-profile-pane">
            <user-profile-pane></user-profile-pane>
        </el-main>
    </el-container>
</template>

<script>
    import userBriefInfo from './user-brief-info.vue'
    import userProfilePane from './user-profile-pane.vue'

    export default {
        components: {
            userBriefInfo,
            userProfilePane
        }
    }
</script>

<style>
    #user-profile {
        margin-top: 2em;
        width: 100%;
    }

    #user-profile-el-aside {
        margin-left: 5em;
        margin-right: 1.5em;
    }

    #user-profile-pane {
        padding: 0;
        margin-right: 5em;
        margin-left: 1.5em;
        font-size: 20px;
    }

    .el-tabs__item {
         padding: 0 20px;
         height: 40px;
         box-sizing: border-box;
         line-height: 40px;
         display: inline-block;
         list-style: none;
         font-size: 16px;
         font-weight: 500;
         position: relative;
    }

    .el-alert .el-alert__title {
        font-size: 18px;
    }

    .el-alert .el-alert__description {
        font-size: 18px;
    }
</style>
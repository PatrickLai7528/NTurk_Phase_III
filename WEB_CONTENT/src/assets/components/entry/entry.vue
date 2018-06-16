<template >
    <el-row style = "height: 100%" >
        <el-col :span = "12" style = "height: 100%" >
            <div class = "logo" >
                <el-container style = "height: 80%" >
                    <el-main >
                        <h1 class = "NTurk" >nTurk</h1 >
                    </el-main >
                    <el-footer >
                        <h3 class = "slogon" >你的眾包標註平台</h3 >
                    </el-footer >
                    <el-footer >
                        <el-button type = "text" @click = "toQuickStart()" >
                            <h3 class = "slogon" >快速上手</h3 >
                        </el-button >
                    </el-footer >
                    <el-footer v-if = "notLogIn()" class = "sx" >
                        <el-button type = "text" @click = "toLogIn()" >
                            <h3 class = "slogon" >登錄</h3 >
                        </el-button >
                    </el-footer >
                </el-container >
            </div >
        </el-col >
        <!--<transition name="el-fade-in">-->
        <el-col :span = "12" style = "height: 100%" >
            <div class = "logInOrSignUp" >
                <router-view ></router-view >
            </div >
        </el-col >
        <!--</transition>-->
    </el-row >

</template >

<script >
	import logIn from './log-in.vue'
    import UserUtils from '../../js/utils/UserUtils.js'

    export default {
        components: {
            logIn
        },
        name: "",
        methods: {
            mounted: function () {
                this.$nextTick(function () {
                })
            },
            toQuickStart: function () {
                this.$router.push({path: '/entry'});
                this.$router.forward();
            },
            toLogIn: function () {
                this.$router.push({path: '/entry/login'});
                this.$router.forward();
            },
            notLogIn: function () {
                console.log("here is entry.vue");
                console.log(this.$store);
                console.log(sessionStorage);
                return !(UserUtils.isWorker(this)
                    || UserUtils.isRequester(this)
                    || UserUtils.isAdmin(this));
            }

        }
    }
</script >

<style scoped >
    .logo {
	    padding-top: 3.3em;
	    background-image: url(../../images/bg.jpg);
	    background-size: auto 100%;
	    height: 100%;
	    width: 100%;
    }

    .logInOrSignUp {
	    display: flex;
	    align-items: center;
	    justify-content: center;
	    display: -webkit-flex; /* Safari */
	    padding-top: 3.3em;
	    background: #FFFFFF;
	    width: 100%;
	    height: 80%;
    }

    .NTurk {
	    font-family: Arial;
	    font-size: 8em;
	    color: #EFEFEF;
	    font-weight: lighter;
	    font-variant: small-caps;
    }

    .el-footer {
	    padding-right: 1em;
	    padding-left: 1em;
	    /*background: #CAFBF2;*/
	    opacity: 0.6;
	    background-color: #FF3B3F;
	    margin-bottom: 1em;
    }

    .slogon {
	    font-family: Microsoft YaHei;
	    font-weight: lighter;
	    color: #FFF;
    }

    .el-button {
	    padding-top: 4px;
	    padding-right: 0;
	    padding-bottom: 0;
	    padding-left: 0;
    }

    .sx {
	    height: 70px;
    }
</style >
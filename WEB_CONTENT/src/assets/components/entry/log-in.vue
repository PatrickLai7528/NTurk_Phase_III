<template>
    <div class="logIn">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px">
            <el-form-item>
                <label class="label">密碼登錄</label>
            </el-form-item>
            <el-form-item label="帳號" prop="account">
                <el-input class="input" auto-complete="off" v-model="ruleForm.account" placeholder="請用郵箱登錄"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input class="input" type="password" v-model="ruleForm.password" auto-complete="off"
                          placeholder="請輸入密碼"></el-input>
            </el-form-item>
            <el-form-item style="margin-bottom: 0">
                <el-button @click="submitForm('ruleForm')"
                           class="logInButton">登錄
                </el-button>
            </el-form-item>
            <el-form-item>
                <el-button type="text" class="otherChoice" style="padding-left: 1em;">忘記密碼</el-button>
                <el-button type="text" class="otherChoice" @click="toSignUpPage()">免費註冊</el-button>
                <el-button type="text" class="otherChoice">其它登錄方式</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script scoped>
    // import store from '../../store/store.js'
    import PasswordValidator from '../../js/PasswordValidator.js'
    import AccountValidator from '../../js/AccountValidator.js'
    import VirtualInterface from '../../js/interfaces/VirtualInterface.js'
    import ValidatorInterface from '../../js/interfaces/ValidatorInterface.js'

    export default {
        data() {
            var validatePassword = (rule, value, callback) => {
                let passwordValidator = new PasswordValidator(value);
                // make sure that passwordValidator has a method called validate
                VirtualInterface.ensureImplements(passwordValidator, ValidatorInterface);
                let result = passwordValidator.validate();
                return result.isValid ? callback() : callback(result.errMsg)
            };
            var validateAccount = (rule, value, callback) => {
                let accountValidator = new AccountValidator(value);
                // make sure that accountValidator has a method called validate
                VirtualInterface.ensureImplements(accountValidator, ValidatorInterface);
                let result = accountValidator.validate();
                return result.isValid ? callback() : callback(result.errMsg)
            };
            return {
                ruleForm: {
                    password: '',
                    account: '',
                },
                rules: {
                    password: [
                        {validator: validatePassword, trigger: 'blur'},
                    ],
                    account: [
                        {validator: validateAccount, trigger: 'blur'}
                    ],
                },
            };
        },
        mounted: function () {
            /*
                test if store.js work or not, after refreshing the pages
             */
            // console.log(sessionStorage);
            // console.log(this.$store.getters.getUserId);
            // console.log(this.$store.getters.getUserType);
            // console.log(this.$store.getters.getToken);
        },
        methods: {
            toSignUpPage: function () {
                this.$router.push({path: '/entry/signup'});
                this.$router.forward();
            },
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.doTheLogIn();
                    } else {
                        return false;
                    }
                });
            },
            doTheLogIn: function () {
                this.$http({
                    url: "http://localhost:8086/auth/",
                    method: "POST",
                    data: {
                        emailAddress: this.ruleForm.account,
                        password: this.ruleForm.password
                    },
                }).then((response)=> {
                    this.$store.dispatch('logIn', {
                        userId: response.data.userId,
                        token: response.data.token,
                        userType: response.data.userType
                    });
                    if(this.$store.getters.getUserType === 'ADMIN'){
                        this.$router.push({path: '/admin'});
                    }
                    else{
                        this.$router.push({path: '/profile'});
                    }
                    this.showMsg("登入成功");
                    this.$router.forward();
                }).catch((error)=> {
                    this.showMsg("賬戶或密碼錯誤");
                    //console.log(error);
                })
            },
            showMsg: function (msg) {
                this.$message({
                    title: '通知',
                    message: msg
                });
            },
        }
    }
</script>

<style scoped>
    .logIn {
        font-family: Microsoft YaHei;
    }

    .input {
        width: 18em;
    }

    .label {
        font-size: 24px;
    }

    .logInButton {
        font-family: Microsoft YaHei;
        color: #FFFFFF;
        width: 18em;
        background: #A9A9A9;
        border-color: #A9A9A9;
    }

    .otherChoice {
        font-family: Microsoft YaHei;
        color: #A9A9A9;
    }
</style>
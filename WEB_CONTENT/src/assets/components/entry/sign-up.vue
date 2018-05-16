<template>
    <div class="signUp">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px">
            <el-form-item>
                <label class="label">註冊新帳號</label>
            </el-form-item>
            <el-form-item></el-form-item>
            <el-form-item label="帳號" prop="account">
                <el-input v-model="ruleForm.account" placeholder="郵箱">
                </el-input>
            </el-form-item>
            <el-form-item label="用戶名" prop="userName">
                <el-input v-model="ruleForm.userName" placeholder="用戶名"></el-input>
            </el-form-item>
            <el-form-item label="密碼" prop="password">
                <el-input v-model="ruleForm.password" placeholder="密碼" type="password"></el-input>
            </el-form-item>
            <el-form-item label="確認密碼" prop="confirmPassword">
                <el-input v-model="ruleForm.confirmPassword" placeholder="請重新輪入密碼" type="password"></el-input>
            </el-form-item>
            <el-form-item label="省份" prop="province">
                <el-select v-model="ruleForm.province" placeholder="請選擇" class="input" filterable>
                    <el-option
                            class="option"
                            v-for="item in provinces"
                            :key="item.value"
                            :value="item.value">
                    </el-option>
                </el-select>
                <!--<el-input v-model="ruleForm.password" placeholder="請輸入所在省份" type="password"></el-input>-->
            </el-form-item>
            <el-form-item label="賬戶類型" prop="userType">
                <el-select v-model="ruleForm.userType" placeholder="請選擇" class="input" filterable>
                    <el-option
                            class="option"
                            v-for="item in userType"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="用戶頭像" prop="userIcon">
                <el-upload ref="upload" :multiple="false" :action='userIconUploadURL'
                           :auto-upload="false" :before-upload="beforeUpload">
                    <el-button class="upload-button">選取文件</el-button>
                </el-upload>
            </el-form-item>
            <el-form-item>
                <el-button class="signUpButton" @click="submitForm('ruleForm')">注冊</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script scoped>
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
            var validateEmpty = (rule, value, callback) => {
                return value === '' ? callback("該欄不能為空") : callback();
            };
            var validateConfirmPassword = (rule, value, callback) => {
                return value !== this.ruleForm.password ? callback("請確定密碼") : callback();
            };
            return {
                provinces: [{
                    value: '北京'
                }, {
                    value: '天津'
                }, {
                    value: '上海'
                }, {
                    value: '重庆'
                }, {
                    value: '河北'
                }, {
                    value: '辽宁'
                }, {
                    value: '吉林'
                }, {
                    value: '黑龙江'
                }, {
                    value: '江苏'
                }, {
                    value: '浙江'
                }, {
                    value: '安徽'
                }, {
                    value: '福建'
                }, {
                    value: '江西'
                }, {
                    value: '山东'
                }, {
                    value: '河南'
                }, {
                    value: '湖北'
                }, {
                    value: '湖南'
                }, {
                    value: '广东'
                }, {
                    value: '海南'
                }, {
                    value: '四川'
                }, {
                    value: '贵州'
                }, {
                    value: '云南'
                }, {
                    value: '山西'
                }, {
                    value: '甘肃'
                }, {
                    value: '青海'
                }, {
                    value: '台湾'
                }, {
                    value: '内蒙古'
                }, {
                    value: '广西'
                }, {
                    value: '西藏'
                }, {
                    value: '宁夏'
                }, {
                    value: '新疆'
                }, {
                    value: '香港'
                }, {
                    value: '澳门'
                },],
                userType: [{
                    value: 'WORKER',
                    label: '眾包工人'
                }, {
                    value: 'REQUESTER',
                    label: '眾包發起者'
                },],
                filename: {
                    filename: "",
                },
                ruleForm: {
                    account: '',
                    userName: '',
                    password: '',
                    confirmPassword: '',
                    province: '',
                    userType: '',
                    userIcon: '',
                },
                userIconUploadURL: "http://localhost:8086/image",
                rules: {
                    password: [
                        {validator: validatePassword, trigger: 'blur'}
                    ],
                    account: [
                        {validator: validateAccount, trigger: 'blur'}
                    ],
                    userName: [
                        {validator: validateEmpty, trigger: 'blur'},
                    ],
                    confirmPassword: [
                        {validator: validateConfirmPassword, trigger: 'blur'}
                    ],
                    province: [
                        {validator: validateEmpty, trigger: 'blur'}
                    ],
                    userType: [
                        {validator: validateEmpty, trigger: 'blur'}
                    ],
                },
                isUploadIcon: false,
            };
        },
        mounted: function () {
            let _this = this;
            this.$nextTick(function () {
                    // 隨機生成一個名字
                    var d = new Date();
                    var random = Math.floor(Math.random() * (10000 - 1) + 1);
                    _this.ruleForm.userIcon = "usericon_" + d.getMilliseconds().toString().substring(0, 10) + random + '.jpg';
                    _this.userIconUploadURL += "/" + _this.ruleForm.userIcon;
                    console.log(_this.ruleForm);
                    console.log(_this.userIconUploadURL);
                }
            )
        },
        methods: {
            beforeUpload: function (file) {
                if (file === undefined)
                    return false;
                // console.log(file);
                const isLt2M = file.size / 1024 / 1024 < 2;
                if (!isLt2M) {
                    this.show('上传头像图片大小不能超过 2MB!', 'error');
                }
                this.isUploadIcon = true;
                return isLt2M;
            }
            ,
            submitForm(formName) {
                let _this = this;
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        _this.doTheSignUp();
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            }
            ,
            decidePostUrl() {
                if (this.ruleForm.userType === 'WORKER') {
                    return 'http://localhost:8086/auth/worker';
                } else if (this.ruleForm.userType === "REQUESTER") {
                    return 'http://localhost:8086/auth/requester'
                } else {
                    throw new Error("Unexpected User Type error!");
                }
            }
            ,
            decidePostData() {
                return {
                    emailAddress: this.ruleForm.account,
                    password: this.ruleForm.password,
                    nickname: this.ruleForm.userName,
                    iconName: this.isUploadIcon ? this.ruleForm.userIcon : '',
                    province: this.ruleForm.province,
                }
            }
            ,
            doWhileSignUpSuccess(response) {
                this.showMsg("注冊成功!", 'success');
                this.$router.push({path: '/entry/login'})
            }
            ,
            doWhileSignUpError(error) {
                if (error.response.status === 400) {
                    this.showMsg("賬號已重名", "error");
                } else {
                    this.showMsg("注冊失敗", 'error');
                }
            }
            ,
            doTheSignUp: function () {
                let _this = this;
                //上傳圖片
                this.$refs.upload.submit();
                this.$http({
                        url: _this.decidePostUrl(),
                        method: "POST",
                        data: _this.decidePostData()
                    }
                ).then(_this.doWhileSignUpSuccess).catch(_this.doWhileSignUpError)
            }
            ,
            showMsg: function (msg, type) {
                this.$notify({
                    type: type,
                    title: '通知',
                    message: msg,
                });
            }
            ,
        }
        ,
        computed: {
            headers() {   //将获得headers放在computed计算属性中试试,将token变成store.state.token
                return {Authorization: this.$store.getters.getToken};
            }
        }
    }
</script>

<style scoped>
    .input {
        width: 100%;
    }

    .signUp {
        font-family: Microsoft YaHei;
    }

    .title {
        font-size: 24px;
    }

    .signUpButton {
        font-family: Microsoft YaHei;
        color: #FFFFFF;
        background: #A9A9A9;
        border-color: #A9A9A9;
        min-width: 18em;
        width: 100%;
    }

    .label {
        font-size: 24px;
    }

    .upload-button {
        font-family: Microsoft YaHei;
        color: #000;
        min-width: 18em;
        width: 100%;
        background: #CAFBF2;
        border-color: #CAFBF2;
    }

    .image {
        width: 100%;
    }

    .option {
        font-family: Microsoft YaHei;
    }
</style>
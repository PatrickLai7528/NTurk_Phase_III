<template>
    <div class="signUp">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px">
            <el-form-item>
                <label class="label">注册新账号</label>
            </el-form-item>
            <el-form-item></el-form-item>
            <el-form-item label="账号" prop="account">
                <el-input v-model="ruleForm.account" placeholder="邮箱">
                </el-input>
            </el-form-item>
            <el-form-item label="用户名" prop="userName">
                <el-input v-model="ruleForm.userName" placeholder="用户名"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="ruleForm.password" placeholder="密码" type="password"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="ruleForm.confirmPassword" placeholder="请重新输入密码" type="password"></el-input>
            </el-form-item>
            <el-form-item label="省份" prop="province">
                <el-select v-model="ruleForm.province" placeholder="请选择" class="input" filterable>
                    <el-option
                            class="option"
                            v-for="item in provinces"
                            :key="item.value"
                            :value="item.value">
                    </el-option>
                </el-select>
                <!--<el-input v-model="ruleForm.password" placeholder="請輸入所在省份" type="password"></el-input>-->
            </el-form-item>
            <el-form-item label="账户类型" prop="userType">
                <el-select v-model="ruleForm.userType" placeholder="请选择" class="input" filterable>
                    <el-option
                            class="option"
                            v-for="item in userType"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="工人标签" prop="userType" v-if="ruleForm.userType==='WORKER'" style="text-align: left">
                <el-tag
                        :key="tag"
                        v-for="tag in ruleForm.userTags"
                        closable
                        :disable-transitions="false"
                        class="userTag"
                        @close="deleteTag(tag)"
                >
                    {{tag}}
                </el-tag>
                <el-button size="mini" @click="dialogFormVisible = true" style="font-size: 16px; align: left" icon="el-icon-plus"></el-button>
                <el-dialog title="添加系统标签" :visible.sync="dialogFormVisible" :modal-append-to-body="false" width="600px" style="text-align: center">
                    <div style="max-height: 500px; overflow-y: auto">
                        <el-checkbox-group v-model="ruleForm.userTags">
                            <el-checkbox-button
                                    class="systemTag"
                                    v-for="tag in systemTags"
                                    :label="tag.value"
                                    :key="tag.value">
                            </el-checkbox-button>
                        </el-checkbox-group>
                    </div>
                </el-dialog>
            </el-form-item>
            <el-form-item label="用户头像" prop="userIcon">
                <el-upload ref="upload" :multiple="false" :action='userIconUploadURL'
                           :auto-upload="false" :before-upload="beforeUpload">
                    <el-button class="upload-button">选取文件</el-button>
                </el-upload>
            </el-form-item>
            <el-form-item>
                <el-button class="signUpButton" @click="submitForm('ruleForm')">注册</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script scoped>
	import PasswordValidator from '../../js/PasswordValidator.js'
	import AccountValidator from '../../js/AccountValidator.js'
	import VirtualInterface from '../../js/interfaces/VirtualInterface.js'
	import ValidatorInterface from '../../js/interfaces/ValidatorInterface.js'
	import ProvinceDataUtils from '../../js/utils/ProvinceDataUtils.js'
	export default {
		data() {
			let validatePassword = (rule, value, callback) => {
				let passwordValidator = new PasswordValidator(value);
				// make sure that passwordValidator has a method called validate
				VirtualInterface.ensureImplements(passwordValidator, ValidatorInterface);
				let result = passwordValidator.validate();
				return result.isValid ? callback() : callback(result.errMsg)
			};
			let validateAccount = (rule, value, callback) => {
				let accountValidator = new AccountValidator(value);
				// make sure that accountValidator has a method called validate
				VirtualInterface.ensureImplements(accountValidator, ValidatorInterface);
				let result = accountValidator.validate();
				return result.isValid ? callback() : callback(result.errMsg)
			};
			let validateEmpty = (rule, value, callback) => {
				return value === '' ? callback("该栏不能为空") : callback();
			};
			let validateConfirmPassword = (rule, value, callback) => {
				return value !== this.ruleForm.password ? callback("请确定密码") : callback();
			};
			return {
				provinces: ProvinceDataUtils.getProvinceData(),
				userType: [{
					value: 'WORKER',
					label: '众包工人'
				}, {
					value: 'REQUESTER',
					label: '众包发起者'
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
                    userTags: []
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
                systemTags: [],
                dialogFormVisible: false,
                tagValue: ""
			};
		},
		mounted: function () {
			this.$nextTick(()=> {
					// 隨機生成一個名字
					let d = new Date();
					let random = Math.floor(Math.random() * (10000 - 1) + 1);
					this.ruleForm.userIcon = "usericon_" + d.getMilliseconds().toString().substring(0, 10) + random + '.jpg';
					this.userIconUploadURL += "/" + this.ruleForm.userIcon;
					this.getSystemTags();
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
			},
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
			},
			decidePostUrl() {
				if (this.ruleForm.userType === 'WORKER') {
					return 'http://localhost:8086/auth/worker';
				} else if (this.ruleForm.userType === "REQUESTER") {
					return 'http://localhost:8086/auth/requester'
				} else {
					throw new Error("Unexpected User Type error!");
				}
			},
			decidePostData() {
				return {
					emailAddress: this.ruleForm.account,
					password: this.ruleForm.password,
					nickname: this.ruleForm.userName,
					iconName: this.isUploadIcon ? this.ruleForm.userIcon : '',
					province: this.ruleForm.province,
				}
			},
			doWhileSignUpSuccess(response) {
				this.showMsg("注册成功!", 'success');
				this.$router.push({path: '/entry/login'})
			},
			doWhileSignUpError(error) {
				if (error.response.status === 400) {
					this.showMsg("账号重名", "error");
				} else {
					this.showMsg("注册失败", 'error');
				}
			},
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
			},
			showMsg: function (msg, type) {
				this.$notify({
					type: type,
					title: '通知',
					message: msg,
				});
			},
            getSystemTags() {
                // this.$http.get("http://localhost:8086/requester/allWorkers", {headers: {Authorization: this.$store.getters.getToken}}).then((response)=> {
                //     this.systemTags = response.data;
                // })
                this.systemTags =
                    [{value: "花朵"}, {value: "学校"}, {value: "食物"}, {value: "军事"},
                        {value: "生活"}, {value: "风景"}, {value: "自然"}, {value: "树木"},
                        {value: "生命"}, {value: "军人"}, {value: "食品"},
                        {value: "命运"}, {value: "天使"}, {value: "恶魔"}, {value: "骑士"},
                        {value: "鼠标"}, {value: "iPad"}, {value: "城市"}, {value: "小草"},
                        {value: "杯子"}, {value: "美少女"}, {value: "乡村"},
                        {value: "路数"}, {value: "运动"}, {value: "标日"}, {value: "菜鸡"},
                        {value: "农田"}, {value: "桌子"}, {value: "盘子"}, {value: "袋子"},
                        {value: "辅导员"}, {value: "代码"}, {value: "辣鸡"}];
            },
            deleteTag(tag) {
                this.ruleForm.userTags.splice(this.ruleForm.userTags.indexOf(tag), 1);
            },
            addTag(tag) {
			    alert(tag);
                this.ruleForm.userTags.push(tag);
            },
		},
		computed: {
			headers() {   //将获得headers放在computed计算属性中试试,将token变成store.state.token
				return {Authorization: this.$store.getters.getToken};
			}
		}
	}
</script>

<style>
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

    .userTag {
        margin-right: 20px;
        font-size: 16px;
        font-weight: bold;
    }

    .systemTag {
        margin: 5px;
    }

    .el-checkbox-button__inner {
        border: 0!important;
        background-color: #f0f0f0;
        border-radius: 4px;
    }

    .el-checkbox-button.is-checked .el-checkbox-button__inner {
        box-shadow: 0 0 0 0 #8cc5ff!important;
    }
</style>
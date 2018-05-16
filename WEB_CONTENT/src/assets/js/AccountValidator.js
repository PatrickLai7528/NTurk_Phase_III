/*
    AccountValidator類, 用於登陸和注冊的帳號驗證
    實現ValidatorInterface接口
 */
var AccountValidator = function (value) {

    // private constants
    const _MESSAGE = {
        emptyError: "該欄不能為空",
        emailFormatError: "請使用郵箱",
        success: "驗證通過"
    };

    // private
    var _value;

    // privileged methods
    this.getEmptyErrorMessage = function () {
        return _MESSAGE.emptyError;
    };

    this.getEmailFormatErrorMessage = function () {
        return _MESSAGE.emailFormatError;
    };

    this.getSuccessMessage = function () {
        return _MESSAGE.success;
    };

    this.validateEmpty = function () {
        return _value !== '';
    }

    this.validateEmailFormat = function () {
        /*
            设合法的邮箱地址格式为“字段1”@“字段2”.“字段3”
            字段1为数字和任意字母的组合
            字段2只允许为字母和数字的组合
            字段3只允许为字母
         */
        var pattern = RegExp("([a-zA-Z0-9]+[-_\\.]?)+@[a-zA-Z0-9]+\\.[a-z]");
        return pattern.test(_value);
    }

    this.setValue = function (value) {
        if (typeof value !== 'string') {
            throw new Error("AccountValidator called with value's " + typeof value + ", but expected a string");
        }
        _value = value;
    }
    this.setValue(value);
};

AccountValidator.prototype.validate = function () {
    if (!this.validateEmpty()) {
        return {
            isValid: false,
            errMsg: this.getEmptyErrorMessage()
        }
    }
    if (!this.validateEmailFormat()) {
        return {
            isValid: false,
            errMsg: this.getEmailFormatErrorMessage()
        }
    }
    return {
        isValid: true,
        errMsg: this.getSuccessMessage()
    }
}

export default AccountValidator;

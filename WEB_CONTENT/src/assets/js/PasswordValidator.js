/*
    PasswordValidator類, 用於登陸和注冊的密碼驗證
    實現ValidatorInterface接口
 */
var PasswordValidator = function (value) {
    // private constant
    const _MESSAGE = {
        emptyError: "该栏不能为空",
        lengthError: "密码长度必须5-16个字符",
        success: "验证通过"
    };

    // private
    var _value;

    // privileged methods
    this.getEmptyErrorMessage = function () {
        return _MESSAGE.emptyError;
    };

    this.getLengthErrorMessage = function () {
        return _MESSAGE.lengthError;
    };

    this.getSuccessMessage = function () {
        return _MESSAGE.success;
    };

    this.validateEmpty = function () {
        return _value !== '';
    }

    this.validateLength = function () {
        return _value.length <= 16 && _value.length >= 5
    }

    this.setValue = function (value) {
        // argument check
        if (typeof value !== 'string') {
            throw new Error("PasswordValidator called with value's " + typeof value + ", but expected a string");
        }
        _value = value;
    }

    // constructor's code
    this.setValue(value);
};

// public method
PasswordValidator.prototype.validate = function () {
    if (!this.validateEmpty()) {
        return {
            isValid: false,
            errMsg: this.getEmptyErrorMessage()
        }
    }
    if (!this.validateLength()) {
        return {
            isValid: false,
            errMsg: this.getLengthErrorMessage()
        }
    }
    return {
        isValid: true,
        errMsg: this.getSuccessMessage()
    }
}

export default PasswordValidator;

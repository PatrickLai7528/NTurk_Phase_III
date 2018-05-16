/*
    測試AccountValidator
 */
import AccountValidator from '../AccountValidator.js'
import ValidatorInterface from '../interfaces/ValidatorInterface.js'
import VirtualInterface from '../interfaces/VirtualInterface.js'

describe('Test for AccountValidator', function () {
    it('Class AccountValidator implements ValidatorInterface', function () {
        var accountValidator = new AccountValidator("123");
        expect(function () {
            try {
                VirtualInterface.ensureImplements(accountValidator, ValidatorInterface)
            } catch (e) {
                throw e;
            }
        }).not.toThrowError();
    });

    it('Test Class AccountValidator\'s constructor with a non-string arguments', function () {
        var accountValidator = new AccountValidator("123");
        expect(function () {
            try {
                accountValidator.setValue(function () {
                })
            } catch (e) {
                throw e;
            }
        }).toThrowError("AccountValidator called with value's function, but expected a string")
    });

    it('Test 1 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("123_moonsnow@163.com");
        expect(accountValidator.validate()).toEqual({
            isValid: true,
            errMsg: accountValidator.getSuccessMessage()
        })
    });

    it('Test 2 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("123456789123456789");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmailFormatErrorMessage()
        })
    });

    it('Test 3 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmptyErrorMessage()
        })
    });

    it('Test 4 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("moonsnow_ha@163.com");
        expect(accountValidator.validate()).toEqual({
            isValid: true,
            errMsg: accountValidator.getSuccessMessage()
        })
    });

    it('Test 5 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("moonsnowha@163");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmailFormatErrorMessage()
        })
    });

    it('Test 6 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("moonsnowha@.com");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmailFormatErrorMessage()
        })
    });

    it('Test 7 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("@.com");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmailFormatErrorMessage()
        })
    });

    it('Test 8 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("123.com");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmailFormatErrorMessage()
        })
    });

    it('Test 8 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("12.3@.com");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmailFormatErrorMessage()
        })
    });

    it('Test 9 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("123@@@@@.com");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmailFormatErrorMessage()
        })
    });
    it('Test 10 for Class AccountValidator\'s validate method', function () {
        var accountValidator = new AccountValidator("123@123123");
        expect(accountValidator.validate()).toEqual({
            isValid: false,
            errMsg: accountValidator.getEmailFormatErrorMessage()
        })
    });

});
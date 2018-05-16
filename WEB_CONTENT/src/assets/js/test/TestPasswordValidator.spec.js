/*
    測試PasswordValidator
 */
import PasswordValidator from '../PasswordValidator.js'
import ValidatorInterface from '../interfaces/ValidatorInterface.js'
import VirtualInterface from '../interfaces/VirtualInterface.js'

describe('Test for PasswordValidator', function () {
    it('Class PasswordValidator implements ValidatorInterface', function () {
        var passwordValidator = new PasswordValidator("123");
        expect(function () {
            try {
                VirtualInterface.ensureImplements(passwordValidator, ValidatorInterface)
            } catch (e) {
                throw e;
            }
        }).not.toThrowError();
    });

    it('Test Class PasswordValidator\'s constructor with a non-string arguments', function () {
        var passwordValidator = new PasswordValidator("123");
        expect(function () {
            try {
                passwordValidator.setValue(function () {
                })
            } catch (e) {
                throw e;
            }
        }).toThrowError("PasswordValidator called with value's function, but expected a string")
    });

    it('Test 1 for Class PasswordValidator\'s validate method', function () {
        var passwordValidator = new PasswordValidator("123");
        expect(passwordValidator.validate()).toEqual({
            isValid: false,
            errMsg: passwordValidator.getLengthErrorMessage()
        })
    });

    it('Test 2 for Class PasswordValidator\'s validate method', function () {
        var passwordValidator = new PasswordValidator("123456789123456789");
        expect(passwordValidator.validate()).toEqual({
            isValid: false,
            errMsg: passwordValidator.getLengthErrorMessage()
        })
    });

    it('Test 3 for Class PasswordValidator\'s validate method', function () {
        var passwordValidator = new PasswordValidator("");
        expect(passwordValidator.validate()).toEqual({
            isValid: false,
            errMsg: passwordValidator.getEmptyErrorMessage()
        })
    });

    it('Test 4 for Class PasswordValidator\'s validate method', function () {
        var passwordValidator = new PasswordValidator("1231236");
        expect(passwordValidator.validate()).toEqual({
            isValid: true,
            errMsg: passwordValidator.getSuccessMessage()
        })
    });
});
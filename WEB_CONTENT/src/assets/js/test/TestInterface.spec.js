import VirtualInterface from '../interfaces/VirtualInterface.js'

describe('Test Case For VirtualInterface.js', function () {
    it('test not to throw error', function () {
        // Interface ResultSet
        var ResultSet = new VirtualInterface('ResultSet', ['getDate', 'getResult']);

        // class ResultObject
        function ResultObject() { // implements ResultSet
        }

        ResultObject.prototype.getDate = function () {
            return 'date';
        }

        ResultObject.prototype.getResult = function () {
            return 'Result';
        }

        var resultObject = new ResultObject();

        expect(function () {
            try {
                VirtualInterface.ensureImplements(resultObject, ResultSet)
            } catch (error) {
                throw error;
            }
        }).not.toThrowError();
    });

    it('test throw error', function () {
        // Interface ResultSet
        var ResultSet = new VirtualInterface('ResultSet', ['getDate', 'getResult']);

        // class ResultObject
        function ResultObject() { // implements ResultSet
        }

        ResultObject.prototype.getDate = function () {
            return 'date';
        }

        var resultObject = new ResultObject();

        expect(function () {
            try {
                VirtualInterface.ensureImplements(resultObject, ResultSet)
            } catch (error) {
                throw error;
            }
        }).toThrowError();
    });

    it('test one argument for VirtualInterface.ensureImplements', function () {
        // Interface ResultSet
        var ResultSet = new VirtualInterface('ResultSet', ['getDate', 'getResult']);

        // class ResultObject
        function ResultObject() { // implements ResultSet
        }

        ResultObject.prototype.getDate = function () {
            return 'date';
        }

        ResultObject.prototype.getResult = function () {
            return 'Result';
        }

        expect(function () {
            try {
                VirtualInterface.ensureImplements(ResultSet)
            } catch (error) {
                throw error;
            }
        }).toThrowError();
    })

    it('test one argument for constructor of VirtualInterface', function () {
        expect(function () {
            try {
                // Interface ResultSet
                var ResultSet = new VirtualInterface('ResultSet');
            } catch (error) {
                throw error;
            }
        }).toThrowError(Error, "VirtualInterface constructor called with 1 arguments, but expected exactly 2.");
    })
    it('test one argument for VirtualInterface.ensureImplements', function () {
        // Interface ResultSet
        var ResultSet = new VirtualInterface('ResultSet', ['getDate', 'getResult']);

        // class ResultObject
        function ResultObject() { // implements ResultSet
        }

        ResultObject.prototype.getDate = function () {
            return 'date';
        }

        ResultObject.prototype.getResult = function () {
            return 'Result';
        }

        expect(function () {
            try {
                VirtualInterface.ensureImplements(ResultSet)
            } catch (error) {
                throw error;
            }
        }).toThrowError(Error, "Function VirtualInterface.ensureImplements called with 1 arguments, but expected at least 2.");
    })

    it('test more than two arguments for VirtualInterface, but one of them is not interface', function () {
        expect(function () {
            try {
                // Interface ResultSet
                var ResultSet = new VirtualInterface('ResultSet', ['getDate', 'getResult']);

                // class ResultObject
                function ResultObject() { // implements ResultSet
                }

                ResultObject.prototype.getDate = function () {
                    return 'date';
                }

                ResultObject.prototype.getResult = function () {
                    return 'Result';
                }
                var forTest = {}
                var resultObject = new ResultObject();
                VirtualInterface.ensureImplements(resultObject, ResultSet, forTest)
            } catch (error) {
                throw error;
            }
        }).toThrowError(Error, "Function VirtualInterface.ensureImplements expects arguments two and above to be instances of VirtualInterface");
    })

    it('test passing non-string value for the second argument of VirtualInterface', function () {
        expect(function () {
            try {
                // Interface ResultSet
                var ResultSet = new VirtualInterface('ResultSet', [123, 12314]);
            } catch (error) {
                throw error;
            }
        }).toThrowError(Error, "VirtualInterface constructor expected method names to be passed in as a string.");
    })
});
/*
    Interface 接口
 */

// constructor
var VirtualInterface = function (name, methods) {;
    this.a = 100
    if (arguments.length != 2) {
        throw new Error("VirtualInterface constructor called with " + arguments.length + " arguments, but expected exactly 2.");
    }
    this.name = name;
    this.methods = [];
    for (var i = 0, len = methods.length; i < len; i++) {
        if (typeof methods[i] !== 'string') {
            throw new Error("VirtualInterface constructor expected method names to be passed in as a string.");
        }
        this.methods.push(methods[i]);
    }
}

// Static class method
VirtualInterface.ensureImplements = function (object) {
    if (arguments.length < 2) {
        throw new Error("Function VirtualInterface.ensureImplements called with " + arguments.length + " arguments, but expected at least 2.");
    }

    for (var i = 1, len = arguments.length; i < len; i++) {
        var virtualInterface = arguments[i];
        if (virtualInterface.constructor !== VirtualInterface) {
            throw new Error("Function VirtualInterface.ensureImplements expects arguments two and above to be instances of VirtualInterface");
        }
        for (var j = 0, methodsLen = virtualInterface.methods.length; j < methodsLen; j++) {
            var method = virtualInterface.methods[j];
            if (!object[method] || typeof object[method] !== 'function') {
                throw new Error("Function VirtualInterface.ensureImplements: object does not implements the " + virtualInterface.name + " virtualInterface. Method " + method + " was not found.");
            }
        }
    }
};

export default VirtualInterface;

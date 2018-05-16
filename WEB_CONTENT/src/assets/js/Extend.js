/*
    extend function
 */

var extend = function (subClass, superClass) {
    var F = Function();
    F.prototype = superClass.prototype;
    subClass.prototype = new F();
    subClass.prototype.constructor = subClass;
}

export default extend;
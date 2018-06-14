let privateVariables = {
    startPoint: {},
    endPoint: {},
    isMouseDown: false
}

/**
 * FrameDrawingStrategy的私有方法
 * @type {{getTagLocation(*): {x: number|*, y: number|*}}}
 */
let privateMethods = {
    /**
     * 取得frame方框右上角的相對canvas的坐標
     * @param frame
     * @returns {{x: number|*, y: number|*}}
     */
    getTagLocation(frame) {
        let p1 = {
            x: frame.p1.x,
            y: frame.p2.y
        };
        let p2 = {
            x: frame.p2.x,
            y: frame.p1.y
        };
        return [p1, p2, frame.p1, frame.p2].reduce(function (p1, p2) {
            function compareP(p1, p2) {
                if (p1.x >= p2.x && p1.y <= p2.y) { // 这里的 “=” 可能处理来比较草率，但应付这个应该还行…
                    return 1;
                } else if (p1.x <= p2.x && p1.y >= p2.y) {
                    return -1;
                } else {
                    return 0;
                }
            }

            return compareP(p1, p2) > 0 ? p1 : p2;
        });
    }
};

/**
 * 策略模式 for Frame
 * 實現MarkingDrawingStrategy接口
 */
class FrameDrawingStrategy {
    constructor() {
        // no need to do anything...
    }

    /**
     * 供annotation[markingTypeName]使用, 和其它用途
     * @returns {string}
     */
    getMarkingTypeName() {
        return "frame";
    }

    /**
     *
     * @param context canvas.getContext("2d")
     * @param answerPairs: {question: string, answer:string}
     * @param config:    {
	 * 						lineWidth: number,
	 * 						strokeStyle: string,
	 * 						lineColor: :string,
	 * 						fillStyle: string,
	 * 						globalAlpha: float,
	 * 					}
     */
    drawThis(context, frame, config) {
        // console.log(frame);
        context.save();
        context.strokeStyle = config.strokeStyle;
        context.strokeRect(frame.p1.x, frame.p1.y,
            frame.p2.x - frame.p1.x, frame.p2.y - frame.p1.y);
        context.restore();
    }

    /**
     * 返回tag的html字符串
     * @param canvas canvas.getContext("2d")
     * @param frame
     * @param index answerPairs在annotation中的index
     * @returns {string}
     */
    addTag(canvas, frame, index) {
        let tagLocation;
        // console.log(canvas);
        // console.log(frame);
        // console.log(index);
        tagLocation = privateMethods.getTagLocation(frame);
        // 小心這個newTagHtml的格式...很容易寫錯的
        let newTagHtml = "<el-tag style='background: #e5e9f2;position:absolute; white-space: nowrap;" + "top:" + (tagLocation.y + canvas.offsetTop) + "px;" + "left:" + tagLocation.x + "px;'>標記" + (index + 1) + "</el-tag>";
        // tagHtml += newTagHtml; // += 可能一張圖片有多個Tag
        return newTagHtml;
    }

    generateMarkingAfterMouseUp(e) {
        let frame;
        privateVariables.endPoint = {x: e.offsetX, y: e.offsetY};
        // startPoint = privateVariables.startPoint;
        // endPoint = {x: e.offsetX, y: e.offsetY};
        privateVariables.isMouseDown = false;
        frame = {p1: privateVariables.startPoint, p2: privateVariables.endPoint};
        if (privateVariables.startPoint.x === privateVariables.startPoint.x && privateVariables.startPoint.y === privateVariables.endPoint.y) {
            privateVariables.isMouseDown = false;
            return null;
        }
        return frame;
    }

    generateMarkingAfterMouseDown(e) {
        console.log("in")
        let frame;
        privateVariables.startPoint = {x: e.offsetX, y: e.offsetY};
        privateVariables.endPoint = privateVariables.startPoint;
        privateVariables.isMouseDown = true;
        frame = {p1: privateVariables.startPoint, p2: privateVariables.endPoint}
        return frame
    }

    generateMarkingAfterMouseMove(e) {
        let frame;
        if (privateVariables.isMouseDown) {
            privateVariables.endPoint = {x: e.offsetX, y: e.offsetY};
            // startPoint = privateVariables.points[0];
            // privateVariables.points[1] = currentPoint;
            frame = {p1: privateVariables.startPoint, p2: privateVariables.endPoint}
        } else {
            frame = null;
        }
        return frame;
    }

    isMarkingEmpty(marking) {
        if (null === marking || undefined === marking || 0 === marking.length) {
            return true;
        } else {
            return false;
        }
    }
}

export default FrameDrawingStrategy;
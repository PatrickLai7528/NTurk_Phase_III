/*
    CanvasControllerForSegment 類
 */
import extend from './Extend.js'
import CanvasController from './CanvasController.js'

var CanvasControllerForSegment = function () {
    var _flag = {
        paintingEnabled: false,
        isMouseMoved: false
    };

    var _setFill = function () {
        this.getContext().save();
        this.getContext().globalAlpha = 0.3;
        this.getContext().fillStyle = this.config.fillStyle;
        this.getContext().fill();
        this.getContext().restore();
    }


    this.getMouseDownEvent = function (e) {
        return function () {
            _flag.paintingEnabled = true;
            _flag.isMouseMoved = true;
            const canvasX = e.offsetX;
            const canvasY = e.offsetY;
            this.getCurrentDisplayables().nextSegmentOrFrame({
                polygon: [
                    {
                        x: canvasX, y: canvasY
                    }
                ]
            });
            this.getContext().beginPath();
            this.getContext().moveTo(canvasX, canvasY);
        }
    };

    this.getMouseUpEvent = function (e) {
        return function () {
            // set the flag
            _flag.paintingEnabled = false;
            _flag.isMouseMoved = false;

            // finish drawing a segment
            this.getContext().closePath();

            // set the style of fill and fill it
            _setFill();

            const canvasX = e.offsetX;
            const canvasY = e.offsetY;

            this.getCurrentDisplayables().addPolygonOrFrame(canvasX, canvasY);
        }
    }

    this.getMouseMoveEvent = function (e) {
        return function () {
            if (_flag.isMouseMoved) {
                const t = e.target;
                let canvasX;
                let canvasY;
                canvasX = e.offsetX;
                canvasY = e.offsetY;
                this.getContext().lineTo(canvasX, canvasY);
                this.getContext().stroke();
                this.getCurrentDisplayables().addPolygonOrFrame(canvasX, canvasY);
            }
        }
    }
};

extend(CanvasControllerForSegment, CanvasController);

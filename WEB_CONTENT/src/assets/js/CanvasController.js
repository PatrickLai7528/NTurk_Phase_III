/*
    CanvasController
    負責Canvas的配置和使用
    顯示Displayable
    執行MouseCommand
 */

import Interface from './interfaces/VirtualInterface.js'
import CommandStrategyInterface from './interfaces/CommandStrategeInterface.js'

var CanvasController = function (canvas, images, config /*commandStrategy*/) {
        // private variables
        var _canvas;
        var _images;
        var _attachment;
        var _currentIndex = 0;
        var _config = {height: '', width: ''};

        // privileged methods
        this._setConfig = function (config) {
            _config.width = config.height;
            _config.width = config.width;
        };

        this.getContext = function () {
            return _canvas.getContext('2d');
        };

        this.setCanvas = function (canvas) {
            _canvas = canvas;
            _canvas.height = _config.height;
            _canvas.width = _config.width;
        };

        this.getCurrentImages = function () {
            if (_currentIndex >= 0 && _currentIndex < _images.length)
                return _images[_currentIndex];
        };

        this.setImages = function (images) {
            //images should be a array
            if (Object.prototype.toString.call(images) !== '[object Array]') {
                throw new Error("CanvasController.setImages called with " + Object.prototype.toString.call(images) + ", but expected a array");
            }
            _images = images;
        };

        /*
            this should be private, _ means this method should not be called outside the class
            if make this methods private, other public methods may not be able to visit this method
        */
        this._getCurrentIndex = function () {
            return _currentIndex;
        };

        this.drawCurrentImage = function () {
            this.getContext().drawImage(this.getCurrentImages(), 0, 0, _config.width, _config.height);
            // _this.loadAnnotation();
        };

        this.removeImageAndAttachement = function (canvasDiv) {
            this.getContext().clearRect(0, 0, this.config.width, this.config.height);

            const childNodes = canvasDiv.childNodes;
            for (let i = 0; i < childNodes.length; i++) {
                const cn = childNodes[i];
                if (cn.tagName === 'DIV') {
                    canvasDiv.removeChild(cn);
                    i--;
                }
            }

            // draw
            this.drawImage(this.getCurrentImages());
        };

        this._setLineStyle = function () {
            this.getContext().lineStyle = _config.color;
            this.getContext().lineWidth = _config.lineWidth;
            this.getContext().strokeColor = this.color;
        };

        this.drawAttachment = function () {
            for (let i = 0; i < _attachment.length; i++) {
                const a = _attachment[i];
                _config.lineColor = a.color;
                this._setLineStyle();
                a.drawAll(this.getContext());
            }
        };

        // Constructor's code
        this.setCanvas(canvas);
        this._setConfig(config);
    }
;

export default CanvasController;
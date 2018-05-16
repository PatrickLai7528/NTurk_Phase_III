/*
    SegmentList類
 */
var SegmentList = function () {
    // private
    var _segmentList = [];
    var _currentSegmentIndex;

    // privileged methods
    this._getCurrentSegmentIndex = function () {
        return _currentSegmentIndex;
    };

    this._getSegmentList = function (index) {
        return _segmentList[index];
    };

    this.newOne = function () {
        _segmentList.push({
            polygon: [
                {x: 0, y: 0}
            ],
            tag: "",
            color: "#FFF"
        });
        _currentSegmentIndex += 1;
    };

    this.addTag = function (canvas, canvasDiv) {
        // if _currentSegmentIndex === -1, means none segment is added
        if (_currentSegmentIndex === -1)
            this.newOne();
        // the first polygon of the _segmentList[_currentSegmentIndex], which is the begin point
        const p = _segmentList[_currentSegmentIndex].polygon[0];
        // const canvas = document.querySelector('#canvas');
        // place this tag on the begin point, p
        const cssString = "position:absolute; white-space: nowrap;" + "top:" + (p.y + canvas.offsetTop) + "px;" + "left:" + p.x + "px;";
        const htmlString = "<el-tag style='background: #e5e9f2'>標記" + (_currentSegmentIndex + 1) + " </el-tag>";
        let div = document.createElement('div');
        div.id = 'div' + _currentSegmentIndex;
        div.innerHTML = htmlString;
        div.setAttribute('style', cssString);
        // const temp = document.getElementById('carousel');
        // const canvasDiv = temp.querySelectorAll('#canvasDiv').item(_currentSegmentIndex);
        canvasDiv.appendChild(div);
    };

    this.deleteTag = function (i) {
        for (let k = i; k < _segmentList.length - 1; k++) {
            _segmentList[k].tag = _segmentList[k + 1].tag;
        }
        this.segments.splice(i, 1);
        // this.initialDraw();
    };

    this.drawAll = function (context) {
        for (let i = 0; i < _segmentList.length; i++) {
            const s = _segmentList[i];
            var polygon = s.polygon;
            // this.config.lineColor = s.color;
            // this.context.lineStyle = this.config.color;
            // this.context.lineWidth = this.config.lineWidth;
            // this.context.save();
            context.beginPath();
            context.moveTo(polygon[0].x, polygon[0].y);
            for (let p = 1; p < polygon.length; p++) {
                this.context.lineTo(polygon[p].x, polygon[p].y);
                this.context.stroke();
            }
            // this.context.fillStyle = this.config.fillStyle;
            this.context.globalAlpha = 0.3;
            this.context.fill();
            this.context.closePath();
            // this.context.restore();
            _currentSegmentIndex++;
            this.addTag(i);
        }
    }

    // constructor's code
    _currentSegmentIndex = -1;
};

export default SegmentList;
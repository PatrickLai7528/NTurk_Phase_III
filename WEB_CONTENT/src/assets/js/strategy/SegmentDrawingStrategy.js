let privateVariables = {
	pointList: [],
	isMouseDown: false
}
/**
 * SegmentDrawingStrategy的私有方法
 * @type {{getTagLocation(*): {x: number|*, y: number|*}}}
 */
let privateMethods = {
	/**
	 * 取得Segment筆跡起點相對canvas的坐標
	 * @param frame
	 * @returns {{x: number|*, y: number|*}}
	 */
	getTagLocation(segment) {
		return segment.polygon[0];
	}
}

/**
 * 策略橂式 for Segments
 * 實現MarkingDrawingStrategy接口
 */
class SegmentDrawingStrategy {
	constructor() {
		// no need to do anything...
	}

	/**
	 * 供annotation[markingTypeName]使用, 和其它用途
	 * @returns {string}
	 */
	getMarkingTypeName() {
		return "segments";
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
	drawThis(context, segment, config) {
		let polygon;
		polygon = segment.polygon;
		context.save();
		context.beginPath();
		context.moveTo(polygon[0].x, polygon[0].y);
		for (let p = 1; p < polygon.length; p++) {
			context.lineTo(polygon[p].x, polygon[p].y);
			context.stroke();
		}
		context.fillStyle = config.fillStyle;
		context.globalAlpha = config.globalAlpha;
		context.fill();
		context.closePath();
		context.restore();
	}

	/**
	 * 返回tag的html字符串
	 * @param canvas canvas.getContext("2d")
	 * @param segment
	 * @param index answerPairs在annotation中的index
	 * @returns {string}
	 */
	addTag(canvas, segment, index) {
		let tagLocation;
		tagLocation = privateMethods.getTagLocation(segment);
		// 小心這個newTagHtml的格式...很容易寫錯的
		let newTagHtml = "<el-tag style='background: #e5e9f2;position:absolute; white-space: nowrap;" + "top:" + (tagLocation.y + canvas.offsetTop) + "px;" + "left:" + tagLocation.x + "px;'>標記" + (index + 1) + "</el-tag>";
		// tagHtml += newTagHtml; // += 可能一張圖片有多個Tag
		return newTagHtml;
	}

	generateMarkingAfterMouseUp(e) {
		let currentPoint;
		currentPoint = {x: e.offsetX, y: e.offsetY};
		privateVariables.isMouseDown = false;
		privateVariables.pointList.push(currentPoint);
		return {polygon: privateVariables.pointList};
		// this.flag.paintingEnabled = false;
		// this.context.closePath();
		// this.context.save();
		// this.context.globalAlpha = 0.3;
		// this.context.fillStyle = this.config.fillStyle;
		// this.context.fill();
		// this.context.restore();
		// this.canvasMoveUse = false;
		// const canvasX = e.offsetX;
		// const canvasY = e.offsetY;

		// if (Math.abs(canvasX - this.lastPoint.x) > 3 && Math.abs(canvasY - this.lastPoint.y) > 3) {
		// this.segments[this.segmentIndex].polygon.push({
		// 	x: canvasX,
		// 	y: canvasY,
		// })
		// console.log("it is going to add tag");
		// this.addTag(this.segments[this.segmentIndex], this.segments.length);
		// console.log(this.segments[this.segmentIndex].polygon);
		// console.log(this.segmentIndex)
		// if (this.segmentIndex < this.segments.length) {
		// 	this.segmentIndex++;
		// }
		// this.addTag(this.segments[this.segments.length - 1], this.segments.length - 1);
		// console.log("segment index = " + this.segmentIndex);
	}

	generateMarkingAfterMouseDown(e) {
		let currentPoint;
		currentPoint = {x: e.offsetX, y: e.offsetY};
		privateVariables.isMouseDown = true;
		privateVariables.pointList = [];
		privateVariables.pointList.push(currentPoint);
		// this.lastPoint.x = canvasX;
		// this.lastPoint.y = canvasY;
		// this.context.beginPath();
		// this.context.moveTo(canvasX, canvasY);
		// console.log('moveTo', canvasX, canvasY);
		return {polygon: privateVariables.pointList};
	}

	generateMarkingAfterMouseMove(e) {
		let currentPoint;
		if (privateVariables.isMouseDown === true) {
			currentPoint = {x: e.offsetX, y: e.offsetY};
			privateVariables.pointList.push(currentPoint);
			return {polygon: privateVariables.pointList};
			// const t = e.target;
			// let canvasX;
			// let canvasY;
			// canvasX = e.offsetX;
			// canvasY = e.offsetY;
			// this.context.lineTo(canvasX, canvasY);
			// this.context.stroke();
			// if (Math.abs(canvasX - this.lastPoint.x) > 3 && Math.abs(canvasY - this.lastPoint.y) > 3) {
			// this.segments[this.segmentIndex].polygon.push({
			// 	x: canvasX,
			// 	y: canvasY,
			// })
		}
		return null;
	}

}

export default SegmentDrawingStrategy;
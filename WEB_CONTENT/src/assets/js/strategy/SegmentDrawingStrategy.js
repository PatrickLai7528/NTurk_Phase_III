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
 * 實現MarkingDrawingStrategy接口
 */
class SegmentDrawingStrategy {
	constructor() {
		// no need to do anything...
	}

	getMarkingTypeName() {
		return "segments";
	}

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

	addTag(canvas, segment, index) {
		let tagLocation;
		tagLocation = privateMethods.getTagLocation(segment);
		// 小心這個newTagHtml的格式...很容易寫錯的
		let newTagHtml = "<el-tag style='background: #e5e9f2;position:absolute; white-space: nowrap;" + "top:" + (tagLocation.y + canvas.offsetTop) + "px;" + "left:" + tagLocation.x + "px;'>標記" + (index + 1) + "</el-tag>";
		// tagHtml += newTagHtml; // += 可能一張圖片有多個Tag
		return newTagHtml;
	}
}

export default SegmentDrawingStrategy;
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
 * 實現MarkingDrawingStrategy接口
 */
class FrameDrawingStrategy {
	constructor() {
		// no need to do anything...
	}

	getMarkingTypeName() {
		return "frames";
	}

	drawThis(context, frame, config) {
		console.log(frame);
		context.strokeRect(frame.p1.x, frame.p1.y,
			frame.p2.x - frame.p1.x, frame.p2.y - frame.p1.y);
	}

	addTag(canvas, frame, index) {
		let tagLocation;
		tagLocation = privateMethods.getTagLocation(frame);
		// 小心這個newTagHtml的格式...很容易寫錯的
		let newTagHtml = "<el-tag style='background: #e5e9f2;position:absolute; white-space: nowrap;" + "top:" + (tagLocation.y + canvas.offsetTop) + "px;" + "left:" + tagLocation.x + "px;'>標記" + (index + 1) + "</el-tag>";
		// tagHtml += newTagHtml; // += 可能一張圖片有多個Tag
		return newTagHtml;
	}
}

export default FrameDrawingStrategy;
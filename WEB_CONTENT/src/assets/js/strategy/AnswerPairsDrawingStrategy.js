/**
 *    策略模式 for AnswerParis
 *    實現MarkingDrawingStrategy接口
 */
class AnswerPairsDrawingStrategy {
	constructor() {
		// no need to do anything...
	}

	/**
	 * 供annotation[markingTypeName]使用, 和其它用途
	 * @returns {string}
	 */
	getMarkingTypeName() {
		return "answerPairs";
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
	drawThis(context, answerPairs, config) {
		// do nothing hahahaahahahahahaha
	}

	/**
	 * 返回自定義格式的tag串
	 * @param canvas canvas.getContext("2d")
	 * @param answerPairs {question: string, answer:string}
	 * @param index answerPairs在annotation中的index
	 * @returns {string}
	 */
	addTag(canvas, answerPairs, index) {
		let newTag = "index:" + index + ",question:" + answerPairs.question + ",answer:" + answerPairs.answer + ";";
		return newTag;
	}

	/**
	 * 解釋自定義的tag字符串, 返回解釋結果
	 * @param tags
	 * @returns {
	 * 				index: number,
	 * 				question: string,
	 * 				answer: string
	 * 			}
	 */
	interpretTag(tags) {
		let returnObject = [];
		let tagArray, tagPerLine, tagElement;
		// try {
		if (tags === null || tags === undefined)
			return;
		console.log(tags);
		tagArray = tags.split(";");
		for (tagPerLine of tagArray) {
			if (tagPerLine === null || tagPerLine === undefined || tagPerLine === "")
				continue;
			tagElement = tagPerLine.split(",");
			returnObject.push({
				index: tagElement[0].split(":")[1],
				question: tagElement[1].split(":")[1],
				answer: tagElement[2].split(":")[1],
			})
		}
		// } catch (error) {
		// throw new Error("interpretTags in AnswerPairsDrawingStrategy: the format of tags is not correct\n" + "raw error message:" + error);
		// }
		return returnObject;
	}

	generateMarkingAfterMouseUp(e) {
		null;
	}

	generateMarkingAfterMouseDown(e) {
		return null;
	}

	generateMarkingAfterMouseMove(e) {
		return null;
	}

	isMarkingEmpty(marking) {
		if (null === marking.answer || undefined == marking.answer || "" === marking.answer) {
			return true;
		} else {
			return false;
		}
	}
}

export default AnswerPairsDrawingStrategy;
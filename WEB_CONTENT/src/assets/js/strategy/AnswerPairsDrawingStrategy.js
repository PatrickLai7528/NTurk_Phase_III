/**
 * 實現MarkingDrawingStrategy接口
 */
class AnswerPairsDrawingStrategy {
	constructor() {
		// no need to do anything...
	}

	getMarkingTypeName() {
		return "answerPairs";
	}

	drawThis(context, answerPairs, config) {
		// do nothing hahahaahahahahahaha
	}

	addTag(canvas, answerPairs, index) {
		let newTag = "index:" + index + ",question:" + answerPairs.question + ",answer:" + answerPairs.answer + ";";
		return newTag;
	}

	interpretTag(tags) {
		let returnObject = [];
		let tagArray, tagPerLine, tagElement;
		try {
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
		} catch (error) {
			throw new Error("interpretTags in AnswerPairsDrawingStrategy: the format of tags is not correct\n" + "raw error message:" + error);
		}
		return returnObject;
	}
}

export default AnswerPairsDrawingStrategy;
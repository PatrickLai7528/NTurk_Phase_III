/*
	翻譯的工具類
 */
class TranslateUtils {
	constructor() {
	}

	static translateTaskCategory(taskCategory) {
		if (typeof  taskCategory !== "string") {
			throw new Error("translateTaskCategory expected a string, but got a " + typeof taskCategory)
		}
		if ("GENERAL" === taskCategory) {
			return "整体标注";
		} else if ("FRAME" === taskCategory) {
			return "画框标注";
		} else if ("SEGMENT" === taskCategory) {
			return "区域标注";
		} else {
			/* 非定義taskCategory */
			throw new Error("undefined task category");
		}
	}

	static translateContractStatus(contractStatus) {
		if(typeof  contractStatus !== "string"){
			throw new Error("transalteContractStatus expected a string, but got a "+ typeof contractStatus);
		}
		if (contractStatus === "IN_PROGRESS") {
			return "进行中";
		} else if (contractStatus === "VIRGIN") {
			return "无人参与";
		} else if (contractStatus === "COMPLETED") {
			return "已完成";
		} else if (contractStatus === "ABORT") {
			return "已废弃";
		}else{
			throw new Error("undefined contract status");
		}
	}
}

export default TranslateUtils
/*
	標記查看類, 裝飾ImageViewer
 */
import VirtualInterface from './interfaces/VirtualInterface.js'
import DrawableInterface from './interfaces/DrawableViewerInterface.js'
import CanvasShareableInterface from './interfaces/CanvasShareableInterface.js'

let privateVariables = {
	contractId: "",
	viewer: {}, /* instance of ImageViewer */
	annotations: [],
	canvas: {},
	baseUrl: "",
	http: {},
	isLastAnnotationLoading: false,
	config: {
		lineWidth: 5,
		strokeStyle: "#df4b26",
	}
};
let privateMethods;
privateMethods = {
	loadCurrentAnnotation: function (callback) {
		/* 檢查參數 */
		let url;
		let header = privateVariables.header;
		if ("function" !== typeof callback)
			throw new Error("loadCurrentAnnotation in AnnotationViewer: callback = " + callback + "is not a function");
		url = `${privateVariables.baseUrl + privateVariables.contractId}/imgName/${privateVariables.viewer.shareCurrentImageName()}`;
		privateVariables.isLastAnnotationLoading = true;
		privateVariables.http.get(url, header)
			.then((response) => {
				privateVariables.annotations = response.data;
				console.log(privateVariables.annotations);
				callback();
			})
			.catch((error) => {
				console.log(error);
			})
	},
	_drawCurrentAnnotation: function (canvasDiv) {
		// 應檢查canvasDiv...
		let doAfterFinishDrawing;
		let context;
		let childNodes;
		// console.log(canvasDiv);
		// childNodes = canvasDiv.childNodes;
		// for (let i = 0; i < childNodes.length; i++) {
		// 	const cn = childNodes[i];
		// 	if ('DIV' !== cn.tagName) {
		// 		continue;
		// 	}
		// 	console.log("is in");
		// 	canvasDiv.removeChild(cn);
		// 	i--;
		// }
		context = privateVariables.canvas.getContext("2d");
		// context.clearRect(0, 0, privateVariables.config.defaultWidth, privateVariables.config.defaultHeight);
		doAfterFinishDrawing = () => {
			context.strokeStyle = privateVariables.config.strokeStyle;
			context.lineWidth = privateVariables.config.lineWidth;
			for (let i = 0; i < privateVariables.annotations.frames.length; i++) {
				const f = privateVariables.annotations.frames[i];
				context.strokeRect(f.p1.x, f.p1.y,
					f.p2.x - f.p1.x, f.p2.y - f.p1.y);
				this.addTag(canvasDiv, f, i);
			}
			privateVariables.isLastAnnotationLoading = false;
		};
		privateVariables.viewer.drawCurrent(doAfterFinishDrawing);
	},
	addTag: function (canvasDiv, frame, index) {
		console.log(canvasDiv);
		const p = this.getRightTopPoint(frame);
		const cssString = "position:absolute; white-space: nowrap;" + "top:" + (p.y + privateVariables.canvas.offsetTop) + "px;" + "left:" + p.x + "px;";
		const htmlString = "<el-tag style='background: #e5e9f2'>標記" + (index + 1) + " </el-tag>";
		let div = document.createElement('div');
		div.id = 'tag' + index;
		div.innerHTML = htmlString;
		div.setAttribute('style', cssString);
		canvasDiv.appendChild(div);
	},
	getRightTopPoint(frame) {
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
	},
};

class AnnotationViewer {
	constructor(imageViewer, baseUrl, contractId, http) {
		/* 檢查參數 */
		if (typeof baseUrl !== "string")
			throw new Error("AnnotationViewer's constructor viewer expected a baseUrl of string, but got a " + typeof baseUrl);

		if (typeof contractId !== "string" && typeof contractId !== "number")
			throw new Error("AnnotationViewer's constructor viewer expected a contractId of string, but got a " + typeof contractId);

		if (baseUrl[baseUrl.length - 1] !== "/")
			throw new Error("AnnotationViewer's constructor: baseUrl should included the last '/'");

		if (!http["get"])
			throw new Error("AnnotationViewer's constructor: http has no method called get");

		/* 檢查imageViewer 是否實現DrawableInterface */
		// console.log(imageViewer);
		VirtualInterface.ensureImplements(imageViewer, DrawableInterface);
		VirtualInterface.ensureImplements(imageViewer, CanvasShareableInterface);

		privateVariables.viewer = imageViewer;
		privateVariables.canvas = imageViewer.shareCanvas();
		privateVariables.config.defaultWidth = imageViewer.shareWidth();
		privateVariables.config.defaultHeight = imageViewer.shareHeight();
		privateVariables.baseUrl = baseUrl;
		privateVariables.contractId = contractId;
		privateVariables.http = http;
		console.log(privateVariables);
	}

	drawCurrent(canvasDiv, header, callback = function () {
	}) {
		let id, doIt;
		doIt = () => {
			var defaultCallback;
			if (privateVariables.header === null || privateVariables.header === undefined)
				privateVariables.header = header;
			if (privateVariables.canvasDiv === null || privateVariables.canvasDiv === undefined)
				privateVariables.canvasDiv = canvasDiv;
			/* get請求成功之後調用的回調函數 */
			defaultCallback = () => {
				// console.log("defaultCallback in");
				// console.log("annotations = " + privateMethods.getCurrentFrame());
				privateMethods._drawCurrentAnnotation(privateVariables.canvasDiv);
				callback();
			};
			privateMethods.loadCurrentAnnotation(defaultCallback);
		};
		if (privateVariables.isLastAnnotationLoading === false) {
			doIt();
		} else {
			id = setInterval(() => {
				if (privateVariables.isLastAnnotationLoading == false) {
					doIt();
					clearInterval(id);
				}
			}, 100);
		}
		return this;
	}

	drawNext(callback = function () {
	}) {
		let id, doIt;
		doIt = () => {
			privateVariables.viewer.drawNext(() => {
				// if (privateVariables.currentFrameIndex < privateVariables.annotations.frames.length) {
				// 	privateVariables.currentFrameIndex++;
				this.drawCurrent(privateVariables.header, callback);
				// }
			});
		};
		if (privateVariables.isLastAnnotationLoading == false) {
			doIt();
		} else {
			id = setInterval(() => {
				if (privateVariables.isLastAnnotationLoading == false) {
					doIt();
					clearInterval(id);
				}
			}, 100);
		}
		return this;
	}

	drawPrevious(callback = function () {
	}) {
		let id, doIt;
		doIt = () => {
			privateVariables.viewer.drawPrevious(() => {
				// if (privateVariables.currentFrameIndex > 0) {
				// 	console.log("in interval callback");
				// 	privateVariables.currentFrameIndex--;
				this.drawCurrent(privateVariables.header, callback);
				// }
			});
		};
		if (privateVariables.isLastAnnotationLoading == false) {
			doIt();
		} else {
			id = setInterval(() => {
				if (privateVariables.isLastAnnotationLoading == false) {
					doIt();
					clearInterval(id);
				}
			}, 100);
		}
		return this;
	}

	shareMarking() {
		return privateVariables.annotations.frames;
	}
}

export default AnnotationViewer;
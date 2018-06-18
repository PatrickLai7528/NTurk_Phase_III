import VirtualInterface from './interfaces/VirtualInterface.js'
import DrawableInterface from './interfaces/DrawableInterface.js'
import CanvasShareableInterface from './interfaces/CanvasShareableInterface.js'
import MarkingDrawingStrategy from './interfaces/MarkingDrawingStrategy.js'

/**
 * AnnotationViewer的私有變量
 * @type {{
 * 			tagHtml: string,
 * 			id: string,
 * 			viewer: {},
 * 			annotations: {},
 * 			canvas: {},
 * 			baseUrl: string,
 * 			http: {},
 * 			isLastAnnotationLoading: boolean,
 * 			config: {
 * 				lineWidth: number,
 * 				strokeStyle: string
 * 				lineColor: :string,
 * 				fillStyle: string,
 * 				globalAlpha: float,
 * 			}
 * 			markingDrawingStrategy: MarkingDrawingStrategy
 * 		}}
 */
let privateVariables = {
    tagHtml: "", /* 顯示tag的html */
    id: "", /* 合同ID */
    viewer: {}, /* instance of ImageViewer */
    annotations: new Map(), /* get請求的數據保存在此 */
    canvas: {},
    baseUrl: "", /* get請求的url, 需包括最後一個"/" */
    http: {}, /* 具有發get請求的東西 */
    isLastAnnotationLoading: false, /* 上一個annotation的get請求是否加載中了, 主要用於輪詢 */
    isCurrentAnnotationNew: true,
    config: {
        /* canvas相關配置, 打算寫成可配置 */
        lineWidth: 5,
        strokeStyle:
            "yellow",
        lineColor:
            "#000000",
        fillStyle:
            "yellow",
        globalAlpha:
            0.3,
    },
    markingDrawingStrategy: {},
    isForceUpdate: new Map()
};
/**
 *
 * @type {{
 * 			loadCurrentAnnotation: privateMethods.loadCurrentAnnotation,
 * 			_drawCurrentAnnotation: privateMethods._drawCurrentAnnotation, 這裡打算用策略模式處理不同類型的標註
 * 			addTag: privateMethods.addTag,
 * 			getRightTopPoint(*): {x: number, y: number}
 * 		}}
 */
let privateMethods = {
    /**
     * 往baseUrl+id+/imageName/+currentImageName 發get請求
     * 使用privateVariables.header, 應該是不安全的...
     * @param callback 請求獲得回應後調用的回調函數
     */
    loadCurrentAnnotation: function (callback) {
        let url; // 請求url
        let header; // 認證
        /* 檢查參數 */
        if ("function" !== typeof callback)
            throw new Error("loadCurrentAnnotation in AnnotationViewer: callback = " + callback + "is not a function");
        url = `${privateVariables.baseUrl + privateVariables.viewer.shareCurrentImageName()}/whatFor/${privateVariables.whatFor}`;
        header = privateVariables.header;
        privateVariables.isLastAnnotationLoading = true; // 標記位, 開始加載
        privateVariables.http.get(url, header)
            .then((response) => {
                console.log(response);
                let oldAnnotation, markingType, temp, pluralMarking;
                markingType = privateVariables.markingDrawingStrategy.getMarkingTypeName();
                temp = response.data[markingType];
                // console.log(temp);
                oldAnnotation = response.data;
                oldAnnotation[markingType] = [];
                oldAnnotation[markingType].push(temp);
                // console.log("in then");
                // console.log(response);
                // console.log(oldAnnotation);
                if (privateVariables.markingDrawingStrategy.hasPluralMarking()) {
                    pluralMarking = oldAnnotation[privateVariables.markingDrawingStrategy.getPluralMarkingTypeName()];
                    if (pluralMarking && !pluralMarking instanceof Array) {
                        throw new Error("Plural Marking is not Array");
                    }
                    pluralMarking.forEach((value, index, array) => {
                        oldAnnotation[markingType].push(value);
                    })
                }
                privateVariables.annotations.set(privateVariables.viewer.shareCurrentImageName(), oldAnnotation);
                privateVariables.isCurrentAnnotationNew = false;
            })
            .catch((error) => {
                // if (error.response.status === "404") {
                let annotation = {};
                annotation.imgName = privateVariables.viewer.shareCurrentImageName();
                annotation[privateVariables.markingDrawingStrategy.getMarkingTypeName()] = [];
                privateVariables.annotations.set(privateVariables.viewer.shareCurrentImageName(), annotation);
                privateVariables.isCurrentAnnotationNew = true;
                // }
                // error.absorb();
                // console.log("in cather");
                // console.log(error);
            })
            .finally(() => {
                privateVariables.isLastAnnotationLoading = false;
                callback();
            })
    },
    _drawCurrentAnnotation: function (callback) {
        // 應檢查canvasDiv...
        let doAfterFinishDrawing;
        let context;
        let markingTypeName;
        let marking;
        let i;
        markingTypeName = privateVariables.markingDrawingStrategy.getMarkingTypeName();
        context = privateVariables.canvas.getContext("2d");
        // context.clearRect(0, 0, privateVariables.config.defaultWidth, privateVariables.config.defaultHeight);
        doAfterFinishDrawing = () => {
            privateVariables.tagHtml = "";
            context.strokeStyle = privateVariables.config.strokeStyle;
            context.lineWidth = privateVariables.config.lineWidth;
            for (i = 0; i < this.getCurrentAnnotation()[markingTypeName].length; i++) {
                marking = this.getCurrentAnnotation()[markingTypeName][i];
                privateVariables.markingDrawingStrategy.drawThis(context, marking, privateVariables.config);
                privateVariables.tagHtml += privateVariables.markingDrawingStrategy.addTag(privateVariables.canvas, marking, i);
            }
            callback();
        };
        privateVariables.viewer.drawCurrent(doAfterFinishDrawing);
    },
    getCurrentAnnotation() {
        return privateVariables.annotations.get(privateVariables.viewer.shareCurrentImageName());
    }
};

/**
 *  標記瀏覽器類,
 *  實現TagShareableInterface接口(這個接口我還沒寫), CanvasShareableInterface接口, Drawable接口
 *  需配合ImageViewer使用
 */
class AnnotationViewer {
    /**
     * 構造器
     * @param imageViewer has to implement CanvasSharable and Drawable, or instance of ImageViewer Class
     * @param baseUrl has to contain the last "/"
     * @param id can be string or number
     * @param http has to have a method call "get"
     */
    constructor(markingDrawingStrategy, imageViewer, baseUrl, http, whatFor) {
        /* 檢查參數 */
        if (typeof baseUrl !== "string")
            throw new Error("AnnotationViewer's constructor viewer expected a baseUrl of string, but got a " + typeof baseUrl);

        if (baseUrl[baseUrl.length - 1] !== "/")
            throw new Error("AnnotationViewer's constructor: baseUrl should included the last '/'");

        if (!http["get"])
            throw new Error("AnnotationViewer's constructor: http has no method called get");

        /* 檢查imageViewer 是否實現DrawableInterface, 若否會抛出錯誤, 詳情看VirtualInterface */
        VirtualInterface.ensureImplements(imageViewer, DrawableInterface);
        VirtualInterface.ensureImplements(imageViewer, CanvasShareableInterface);
        /* 檢查markingDrawingStrategy 是否實現MarkingDrawingStrategy, 若否會抛出錯誤, 詳情看VirtualInterface */
        VirtualInterface.ensureImplements(markingDrawingStrategy, MarkingDrawingStrategy)

        privateVariables.viewer = imageViewer;
        privateVariables.canvas = imageViewer.shareCanvas();
        privateVariables.config.defaultWidth = imageViewer.shareWidth();
        privateVariables.config.defaultHeight = imageViewer.shareHeight();
        privateVariables.baseUrl = baseUrl;
        privateVariables.http = http;
        privateVariables.tagHtml = "";
        privateVariables.markingDrawingStrategy = markingDrawingStrategy;
        privateVariables.whatFor = whatFor;
    }

    /**
     * 加載viewer.getCurrentImageName對應的圖片, 和
     * 加載viewer.getCurrentImageName和id對應的annotation
     * @param tagHtml i do not think this is needed any more
     * @param header header就是header
     * @param callback 不保證即時能加載完, 若有操作需依賴繪製完的狀態, 需用回調函數
     * @returns {AnnotationViewer}
     */
    drawCurrent(header, callback = function () {
    }) {
        let id, doIt, isCurrentImageForcedUpdate, currentImageName;
        // console.log(privateVariables.isForceUpdate);

        doIt = () => { // 加載的實際操作
            let defaultCallback;
            currentImageName = privateVariables.viewer.shareCurrentImageName();
            isCurrentImageForcedUpdate = privateVariables.isForceUpdate.get(currentImageName);
            privateVariables.isForceUpdate.set(currentImageName, false);
            if (privateVariables.header === null || privateVariables.header === undefined)
                privateVariables.header = header;
            /* get請求成功之後調用的回調函數 */

            defaultCallback = () => {
                privateMethods._drawCurrentAnnotation(callback);
            };
            if (!isCurrentImageForcedUpdate && privateMethods.getCurrentAnnotation() !== null && privateMethods.getCurrentAnnotation() !== undefined) {
                // console.log("already has this data");
                privateMethods._drawCurrentAnnotation(callback);
            } else {
                privateMethods.loadCurrentAnnotation(defaultCallback);
            }
        };
        /* 上一個annotation已加載完成, 加載現在的annotation*/
        if (privateVariables.isLastAnnotationLoading === false) {
            doIt();
        } else {
            /* 上一個annotation還沒加載完成, 輪詢等待 */
            id = setInterval(() => {
                if (privateVariables.isLastAnnotationLoading == false) {
                    doIt();
                    clearInterval(id); // 取消輪詢
                }
            }, 10);
        }
        return this;
    }

    /**
     * 加載下一張圖片和annotaion
     * @param callback
     * @returns {AnnotationViewer}
     */
    drawNext(callback = function () {
    }) {
        let id, doIt;
        doIt = () => {
            privateVariables.viewer.drawNext(() => {
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
            }, 10);
        }
        return this;
    }

    /**
     * 加載上一張圖片和annotation
     * @param callback
     * @returns {AnnotationViewer}
     */
    drawPrevious(callback = function () {
    }) {
        let id, doIt;
        doIt = () => {
            privateVariables.viewer.drawPrevious(() => {
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
            }, 10);
        }
        return this;
    }

    /**
     * 返回當前annotation的tagList, 最好放在回調函數裡用
     * 不然可以出現以下情況:
     *        1.    annotaion還沒加載完畢時, 返回了上一個annotation的tagList 或者 null 或者 undefined
     * @returns {[{text:string}]} // 可能追加其它的屬性
     */
    shareTagText() {
        let tagText = new Array(), item, markingTypeName, i = 0;
        markingTypeName = privateVariables.markingDrawingStrategy.getMarkingTypeName();
        for (item of privateMethods.getCurrentAnnotation()[markingTypeName]) {
            tagText.push({text: item.tag, index: i, old: true});
            i++;
        }
        return tagText;
    }

    /**
     *  返回當前annotation的顯示tag的html, 最好放在回調函數調用
     * 不然可以出現以下情況:
     *        1.    annotaion還沒加載完畢時, 返回了上一個annotation的tagHtml 或者 null 或者 undefined
     * @returns {string}
     */
    shareTag() {
        return privateVariables.tagHtml;
    }

    /**
     * 返回canvas的高度
     * @returns {height: number(px)}
     */
    shareHeight() {
        return privateVariables.viewer.shareHeight();
    }

    /**
     * 返回canvas的寛度
     * @returns {width: number(px)}
     */
    shareWidth() {
        return privateVariables.viewer.shareWidth();
    }

    /**
     * 返回canvas
     * @returns {canvas}
     */
    shareCanvas() {
        return privateVariables.viewer.shareCanvas();
    }

    /**
     * 返回當前展示的圖片的名字, 不依賴加載情況, 不用放在回調函數用
     * @returns {imageName: string}
     */
    shareCurrentImageName() {
        return privateVariables.viewer.shareCurrentImageName();
    }

    shareMarkingDrawingStrategy() {
        return privateVariables.markingDrawingStrategy;
    }

    shareCurrentAnnotation() {
        return privateMethods.getCurrentAnnotation()
    }

    isCurrentAnnotationNew() {
        return privateVariables.isCurrentAnnotationNew;
    }

    forceUpdate(imageName) {
        privateVariables.isForceUpdate.set(imageName, true);
    }

    drawThisAnnotation() {

    }
}

export default AnnotationViewer;
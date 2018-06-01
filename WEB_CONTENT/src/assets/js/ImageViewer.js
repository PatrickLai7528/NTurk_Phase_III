/**
 * ImageViewer 的私有變量
 * @type {{
 * 		imageNames: Array,
 * 		images: Array,
 * 		currentImageIndex: number,
 * 		isLastImageLoading: boolean,
 * 		config: {defaultHeight: number, defaultWidth: number}}}
 */
let privateVariables = {
		imageNames: [], /* 圖片的名字, 包括後綴, 如.jpg */
		images: [], /* 若images 是[{}], 則drawImage會報錯說第一個參數不是圖片 */
		currentImageIndex: 0, /* 表示正在顯示的圖片在images裡的索引值, 和在imageNames裡對應的索引值, 有效範圍是0~imageNames.length */
		isLastImageLoading: false, /* 表示當前上一張圖片是加載完成, drawPrevious/NextImage要等到下一張圖片加載完成再執行 */
		config: {
			/* 應該提供接口, 可以被設置 */
			defaultHeight: 400, /* 單位px */
			defaultWidth: 600,
		}
	}
;
/**
 * ImageViewer的私有方法
 * @type {{
 * 		loadCurrentImage: privateMethods.loadCurrentImage,
 * 		getCurrentImage: privateMethods.getCurrentImage
 * 	}}
 */
let privateMethods = {
	/**
	 * 加載currentImageIndex對應的圖片
	 * @param caller : ImageViewer instance
	 * @param callback : function 圖片加載完成後調用的回調函數
	 */
	loadCurrentImage: function (caller, callback) {
		/* 檢查參數 */
		if (!caller['shareCurrentImageName'])
			throw new Error("loadCurrentImage in ImageViewer: the caller has no method called shareCurrentImageName");
		if (typeof callback !== "function")
			throw new Error("loadCurrentImage in ImageViewer: " + "callback = " + callback + "is not a function");
		let tempImage = new Image();
		/* 設圖片的路由, baseUrl 需有最後一個的/  */
		tempImage.src = privateVariables.baseUrl + caller.shareCurrentImageName();
		privateVariables.isLastImageLoading = true; // 標記位, 開始加載圖片
		tempImage.onload = function () {
			/* 當前圖片加載完成, 增加到images */
			privateVariables.images.push(this);
			// console.log(privateVariables.images);
			callback();
			privateVariables.isLastImageLoading = false; // 標記位, 圖片加載完畢
		}
	},
	/**
	 * 取得currentImageIndex對應的image
	 * @returns {image}
	 */
	getCurrentImage: function () {
		if (privateVariables.currentImageIndex <= privateVariables.images.length)
			return privateVariables.images[privateVariables.currentImageIndex];
	},
};

/**
 *  圖片瀏覽器類
 *  實現CanvasShareableInterface接口, DrawableInterface接口
 */
class ImageViewer {
	/**
	 * 圖片加載流程:
	 * drawCurrent      ->    loadCurrentImage    ->    image.onload    ->    defaultCallBack    ->    callback
	 * 對應狀態說明:
	 * 未繪制,未加載     ->    未繪制,加載中        ->    未繪制, 加載完    ->    繪制中, 加載完        ->    繪制完, 加載完
	 */

	/**
	 *    構造方法
	 * @param canvas : htmlElement canvas
	 * @param imageNames : Array of String
	 * @param baseUrl: String which contained the last "/"
	 */
	constructor(canvas, imageNames, baseUrl) {
		/* 參數檢查 */
		if (null === canvas) /* 此處我不知道如何檢查傳入的canvas是不是canvas */
			throw new Error("ImageViewer's constructor expected a canvas, but got a null");

		if (!imageNames instanceof Array)
			throw new Error("ImageViewer's constructor expected a imageNames of array, but got a " + typeof imageNames);

		if (typeof baseUrl !== "string")
			throw new Error("ImageViewer's constructor expected a baseUrl of string, but got a " + typeof baseUrl);

		if (baseUrl[baseUrl.length - 1] !== "/")
			throw new Error("ImageViewer's constructor: baseUrl should included the last '/'");

		privateVariables.canvas = canvas;
		privateVariables.canvas.height = privateVariables.config.defaultHeight;
		privateVariables.canvas.width = privateVariables.config.defaultWidth;
		privateVariables.imageNames = imageNames;
		privateVariables.baseUrl = baseUrl;
		privateVariables.currentImageIndex = 0;
	}

	/**
	 * 在canvas裡繪制currentImageIndex對應的圖片
	 * 圖片高度和寛度由config決定
	 * @param callback 默認callback為空函數, 繪制完後調用
	 */
	drawCurrent(callback = function () {
	}) {
		let doIt, id;
		doIt = () => {
			let defaultCallback;
			defaultCallback = () => {/* 圖片加載完了之後調用的回調函數 */
				privateVariables.canvas
					.getContext("2d")
					.drawImage(
						privateMethods.getCurrentImage(),
						0, /* 在畫布上放置圖像的x坐標位置 */
						0, /* 在畫布上放置圖像的y坐標位置 */
						privateVariables.config.defaultWidth, /* 要使用的圖像的寛度 */
						privateVariables.config.defaultHeight /* 要使用的圖像的高度 */
					);
				callback(); // 用戶定義的回調函數, 繪制後執行
			};
			/* 注意defaultCallback不要加() */
			if (privateMethods.getCurrentImage() === null || privateMethods.getCurrentImage() === undefined) {
				privateMethods.loadCurrentImage(this, defaultCallback);
			} else {
				defaultCallback();
			}
		};
		if (privateVariables.isLastImageLoading == false) {/* 上一張已加載完成 */
			doIt();
		} else {/* 輪詢等待上一張加載完成 */
			id = setInterval(() => {
				if (privateVariables.isLastImageLoading == false) {
					doIt();
					clearInterval(id);
				}
			}, 10);
		}
		return this;
	}

	/**
	 * 加載下一張圖片, 不保證即時能繪制完, 若有操作需依賴繪製完的狀態, 需用回調函數
	 * @param callback 默認callback為空函數,繪制完後調用
	 */
	drawNext(callback = function () {
	}) {
		/* drawNext時, 前一張圖片是
		 * 1:	加載完成
		 * 			馬上執行
		 * 2:	未加載完成
		 * 			輪詢等待前一張圖片加載完成
		 * 			原因:
		 * 				此處修改currentImageIndex, 如原來currentImageIndex = 0,
		 * 				修改後為currentImageIndex = 1, 在前一張圖片加載完時, 進入繪制狀態
		 * 				調用getCurrentImage(), 獲得的是undefined
		 */
		let id; // setInterval返回值
		let doIt; // 加載下一張圖的實際操作, 避免寫兩次所以寫成函數
		doIt = () => {
			if (privateVariables.currentImageIndex < privateVariables.imageNames.length) { // false => 已經是最後一張了
				privateVariables.currentImageIndex++;
				this.drawCurrent(callback);
			}
		};
		/* 上一張圖片已經加載完, 直接加載下一張圖片 */
		if (privateVariables.isLastImageLoading === false) {
			doIt();
		} else {
			/* 上一張圖片還沒有加載完畢, 輪詢等待上一張圖片加載完畢後再加載下一張圖片 */
			id = setInterval(() => {
				if (privateVariables.isLastImageLoading === false) {
					doIt();
					clearInterval(id);
				}
			}, 10);
		}
		return this;
	}

	/**
	 * 加載下一張圖片, 不保證即時能繪制完, 若有操作需依賴繪製完的狀態, 需用回調函數
	 * @param callback 默認callback為空函數, 繪制完後調用
	 */
	drawPrevious(callback = function () {
	}) {
		/* drawPrevious時, 前一張圖片是
		 * 1:	加載完成
		 * 			馬上執行
		 * 2:	未加載完成
		 * 			輪詢等待前一張圖片加載完成
		 * 			原因:
		 * 				此處修改currentImageIndex, 如原來currentImageIndex = 0,
		 * 				修改後為currentImageIndex = 1, 在前一張圖片加載完時, 進入繪制狀態(defaultCallback裡)
		 * 				調用getCurrentImage(), 獲得的是undefined
		 */
		let id; // setInterval返回值
		let doIt; // 加載下一張圖的實際操作, 避免寫兩次所以寫成函數
		doIt = () => {
			if (privateVariables.currentImageIndex > 0) { // currentImageIndex = 0 => 已經第一張圖片, 不能加載上一張
				privateVariables.currentImageIndex--;
				this.drawCurrent(callback);
			}
		};
		/* 上一張圖片已經加載完, 直接加載下一張圖片 */
		if (privateVariables.isLastImageLoading === false) {
			doIt();
		} else {
			/* 上一張圖片還沒有加載完畢, 輪詢等待上一張圖片加載完畢後再加載下一張圖片 */
			id = setInterval(() => {
				if (privateVariables.isLastImageLoading === false) {
					doIt();
					clearInterval(id); // 取消輪詢
				}
			}, 10);
		}
		return this;
	}

	/**
	 * 取得canvas
	 * @returns {{}|privateVariables.canvas|*}
	 */
	shareCanvas() {
		return privateVariables.canvas;
	}

	/**
	 * 取得正在展示的圖片的名字
	 * @returns {imageName: string}
	 */
	shareCurrentImageName() {
		let index, ans;
		if (privateVariables.currentImageIndex < privateVariables.imageNames.length)
			index = privateVariables.currentImageIndex;
		else {
			index = privateVariables.imageNames.length - 1;
			privateVariables.currentImageIndex = index;
		}
		ans = privateVariables.imageNames[index];
		if (typeof ans !== "string")
			throw new Error("shareCurrentImageName in ImageViewer: the " + privateVariables.currentImageIndex + " of image name is not string");
		return ans;
	}

	shareHeight() {
		return privateVariables.config.defaultHeight;
	}

	shareWidth() {
		return privateVariables.config.defaultWidth;
	}
}

export default ImageViewer;
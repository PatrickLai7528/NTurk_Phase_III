import ImageViewer from "../ImageViewer.js";

describe("Unit Test For Class ImageViewer", function () {
	let stub = {
		/* 後端需有這些圖片才能測試 */
		imageNames: ["1.jpg", "2.jpg", "3.jpg", "4.jpg"],
		baseUrl: "http://localhost:8086/image/",
		canvas: document.createElement("canvas")
	};

	let imageViewer;

	it("Test 1: Arguments checking", function () {
		expect(function () {
			try {
				imageViewer = new ImageViewer(stub.canvas, stub.imageNames, stub.baseUrl);
			} catch (error) {
				throw error;
			}
		}).not.toThrowError();
		expect(function () {
			try {
				imageViewer = new ImageViewer(stub.canvas, [123123, "", ""], stub.baseUrl);
				imageViewer.drawCurrent();
			} catch (e) {
				throw e;
			}
		}).toThrowError();
		expect(function () {
			try {
				imageViewer = new ImageViewer(stub.canvas, stub.imageNames, {});
			} catch (e) {
				throw e;
			}
		}).toThrowError();
		expect(function () {
			try {
				imageViewer = new ImageViewer(null, stub.imageNames, stub.baseUrl);
			} catch (e) {
				throw e;
			}
		}).toThrowError();
	});
	it("Test 2: Loading & Controlling Image", function () {
		imageViewer = new ImageViewer(stub.canvas, stub.imageNames, stub.baseUrl);
		imageViewer.drawCurrent();
		expect(imageViewer.shareCurrentImageName() === stub.imageNames[0]);
		expect(imageViewer.drawNext().shareCurrentImageName() === stub.imageNames[1]);
		expect(imageViewer.drawNext().shareCurrentImageName() === stub.imageNames[2]);
		expect(imageViewer.drawNext().shareCurrentImageName() === stub.imageNames[3]);
		expect(imageViewer.drawPrevious().shareCurrentImageName() === stub.imageNames[2]);
		expect(imageViewer.drawNext().shareCurrentImageName() === stub.imageNames[3]);
	});

	it("Test 3: Getter", function () {
		imageViewer = new ImageViewer(stub.canvas, stub.imageNames, stub.baseUrl);
		expect(imageViewer.shareCanvas() === stub.canvas);
	});
});
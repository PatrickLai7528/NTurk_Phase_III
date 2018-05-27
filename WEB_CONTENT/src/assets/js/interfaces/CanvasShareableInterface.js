/*
    CanvasShareable接口
 */

import VirtualInterface from "./VirtualInterface";

let CanvasShareableInterface = new VirtualInterface('CanvasShareableInterface',
	[
	    "shareCanvas","shareCurrentImageName",
	    "shareHeight","shareWidth"
	]);

export default CanvasShareableInterface;
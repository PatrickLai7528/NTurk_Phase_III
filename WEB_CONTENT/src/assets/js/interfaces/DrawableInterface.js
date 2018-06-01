/*
    Drawable接口
 */

import VirtualInterface from "./VirtualInterface";

let DrawableInterface = new VirtualInterface('DrawableInterface',
	[
		'drawCurrent', 'drawNext',
		'drawPrevious'
	]);

export default DrawableInterface;
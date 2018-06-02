/*
    MarkingDrawingStrategy接口
 */

import VirtualInterface from "./VirtualInterface";

let MarkingDrawingStrategy = new VirtualInterface('MarkingDrawingStrategy',
	[
		'getMarkingTypeName', 'drawThis',
		'addTag'
	]);

export default MarkingDrawingStrategy;
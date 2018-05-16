/*
    CommandStrategy接口
 */

import VirtualInterface from "./VirtualInterface";

var CommandStrategyInterface = new VirtualInterface('CommandStrategyInterface', ['getMouseDownCommand', 'getMouseUpCommand', 'getMouseMouseCommand']);

export default CommandStrategyInterface;
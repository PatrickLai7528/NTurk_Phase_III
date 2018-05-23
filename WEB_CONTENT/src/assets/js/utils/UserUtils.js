/*
	用戶工具類
 */

class UserUtils {
	static isWorker(context) {
		return context.$store.getters.getUserType === 'WORKER';
	}

	static isRequester(context) {
		return context.$store.getters.getUserType === "REQUESTER";
	}

	static isAdmin(context) {
		return context.$store.getters.getUserType === 'ADMIN';
	}

	constructor() {
	}
}

export default UserUtils;
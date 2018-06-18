/*
	系统标签工具类
 */

class TagUtils {
	static getSystemTags(http, cb){
        http.get("http://localhost:8086/systemTags").then((response)=>{
            let tags = response.data;
            cb(tags);
        }).catch((error)=> {
            console.log(error);
            cb([]);
        });
    }

    static callback(){
	    alert();
    }

	constructor() {
	}
}

export default TagUtils;
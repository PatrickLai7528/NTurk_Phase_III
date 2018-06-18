/*
	系统标签工具类
 */

class TagUtils {
	static getSystemTags(http){
	    let tags = [];
        http.get("http://localhost:8086/systemTags").then((response)=>{
            tags = response.data;
        }).catch((error)=> {
            console.log(error);
        })

    }

    static callback(){
	    alert();
    }

	constructor() {
	}
}

export default TagUtils;
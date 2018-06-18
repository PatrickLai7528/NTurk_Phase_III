/*
	系统标签工具类
 */

class TagUtils {
	static getSystemTags(){
	    let tags = [];
        this.$http({
            url: "http://localhost:8086/task/systemTags/",
            method: "GET",
            headers: {Authorization: "sdafasfdsfdas"}
        }).then((response)=>{
            alert(response);
        }).catch((error)=> {
            console.log(error);
        })
	}

	constructor() {
	}
}

export default TagUtils;
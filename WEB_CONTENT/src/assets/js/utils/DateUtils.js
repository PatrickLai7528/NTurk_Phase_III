/*
	日期處理的工具類
 */
class DateUtils {
	static simpleDateFormate(oldDate) {
		// date has the format: "yyyy-MM-dd hh:mm:ss"
		return oldDate === null ? "null" : oldDate.substring(0, 10);
	}

	static dateFormat(oldDate) {
        Date.prototype.format = function (fmt) {
            let o = {
                "M+": this.getMonth() + 1,                 //月份
                "d+": this.getDate(),                    //日
                "h+": this.getHours(),                   //小时
                "m+": this.getMinutes(),                 //分
                "s+": this.getSeconds(),                 //秒
                "q+": Math.floor((this.getMonth() + 3) / 3), //季度
                "S": this.getMilliseconds()             //毫秒
            };
            if (/(y+)/.test(fmt)) {
                fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
            }
            for (let k in o) {
                if (new RegExp("(" + k + ")").test(fmt)) {
                    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
                }
            }
            return fmt;
        };

        let tem = new Date(oldDate).getTime();
        let ans = new Date(tem).format("yyyy-MM-dd hh:mm:ss");
        return ans;
    }

    static monthFirstDay(currentYear, currentMonth, backtrace) {
	    let month = this.countMonth(currentMonth, backtrace);
		let year = currentMonth+backtrace<=0 ? currentYear-1 : currentYear;
		return year+"-"+month+"-01";
    }

    static monthLastDay(currentYear, currentMonth, backtrace) {
		let end = 0;
        let leapMonth = new Set([1, 3, 5, 7, 8, 10, 12]);
        let non_leap = new Set([4, 6, 9, 11]);
        let month = this.countMonth(currentMonth, backtrace);
        let year = currentMonth+backtrace<=0 ? currentYear-1 : currentYear;

        if(leapMonth.has(month)){
			end = 31;
		} else if(non_leap.has(month)){
			end = 30;
		} else if(month===2){
            if(year%400 === 0){
                end = 29;
            }
            else if(year%4===0 && year%100!==0){
                end = 29;
            }
            else{
                end = 28;
            }
		}

        return year+"-"+month+"-"+end;
    }

    static countMonth(month, backtrace){
        let result = (month + backtrace + 12) % 12;
        if(result === 0){
            result = 12;
        }
        return result;
    }
}

export default DateUtils;
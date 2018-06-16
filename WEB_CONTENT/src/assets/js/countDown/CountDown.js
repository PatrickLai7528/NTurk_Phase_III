// +----------------------------------------------------------------------
// | countdown
// +----------------------------------------------------------------------
// | HomePage : https://github.com/plasusu/countdown
// +----------------------------------------------------------------------
// | Author: plasusu <zhaobin0511@qq.com>
// +----------------------------------------------------------------------
'use strict';
const now = new Date().getTime();
const ONE_YEAR = 31536000;
const ONE_MONTH = 2628000;
const patterns = [
    {
        pattern: '{years}',
        secs: ONE_YEAR * 1000,
    }, {
        pattern: '{months}',
        secs: ONE_MONTH * 1000,
    }, {
        pattern: '{weeks}',
        secs: 7 * 24 * 60 * 60 * 1000,
    }, {
        pattern: '{days}',
        secs: 24 * 60 * 60 * 1000,
    }, {
        pattern: '{hours}',
        secs: 60 * 60 * 1000,
    }, {
        pattern: '{minutes}',
        secs: 60 * 1000,
    }, {
        pattern: '{seconds}',
        secs: 1000,
    }, {
        pattern: '{decimal}',
        secs: 100,
    },
];

const doNothing = () => {
    // do nothing
};

class Countdown {
    constructor(opt) {
        this.currentTime = opt.currentTime || now;
        this.timeEnd = opt.timeEnd || now;
        this.selector = opt.selector || '.J_countdown';
        this.msgPattern = opt.msgPattern || '{days}天{hours}时{minutes}分{seconds}秒{decimal}';
        this.showZero = opt.showZero || true;
        this.afterCount = opt.afterCount || doNothing;

        this.distance = this.timeEnd - this.currentTime;
        this.unit = 100;
        this.view = null;
        this.timer = null;
    }

    init() {
        // console.log("init");
        if (this.isOver()) {
            this.overTime();
            return;
        }
        this.updateView();
        this.runTimer();
    }

    isOver() {
        return this.currentTime >= this.timeEnd;
    }

    updateView() {
        let displayMsg = this.msgPattern;
        let distanceClone = this.distance;
        let val;

        patterns.forEach((pat, index) => {
            const patSecs = pat.secs;
            const patPattern = pat.pattern;

            if (displayMsg.indexOf(patPattern) !== -1) {
                val = Math.floor(distanceClone / patSecs);
                // {decimal} 本就应该小于10所以没必要加0，另外因为{decimal}是0.1s所以跳动会比较快，加0反而会出现闪烁的感觉
                val = (this.showZero && val > 0 && val <= 9 && patPattern !== '{decimal}') ? `0${val}` : val;
                distanceClone -= (val * patSecs);
                displayMsg = displayMsg.replace(patPattern, val);
            }
        });
        document.querySelector(this.selector).innerHTML = displayMsg;
    }

    overTime() {
        window.clearInterval(this.timer);
        this.afterCount();
    }

    runTimer() {
        const {unit} = this;
        this.timer = window.setInterval(() => {
            this.distance -= unit;
            this.updateView();
            if (this.distance <= 0) {
                this.overTime();
            }
        }, unit);
    }

    clearTimer() {
        clearInterval(this.timer);
    }
}

export default Countdown;


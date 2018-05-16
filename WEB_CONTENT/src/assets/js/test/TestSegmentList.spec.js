/*
    測試SegmentList類
    一個SegmentsList有多個Segment, 一個Segment有多個Polygon
 */
import SegmentList from "../SegmentList.js";

describe("Test Class SegmentList", function () {

    it("Test 1", function () {
        var segmentList = new SegmentList();
        segmentList.newOne();
        expect(segmentList._getCurrentSegmentIndex()).toBe(0);
        expect(segmentList._getSegmentList(0)).toEqual({
            polygon: [
                {x: 0, y: 0}
            ],
            tag: "",
            color: "#FFF"
        });
        segmentList.newOne();
        expect(segmentList._getCurrentSegmentIndex()).toBe(1);
        expect(segmentList._getSegmentList(0)).toEqual({
            polygon: [
                {x: 0, y: 0}
            ],
            tag: "",
            color: "#FFF"
        });
        expect(segmentList._getSegmentList(1)).toEqual({
            polygon: [
                {x: 0, y: 0}
            ],
            tag: "",
            color: "#FFF"
        });
    })

    it("Test 2", function () {
        var canvas = {
            offsetTop: 12
        };
        var canvasDiv = {
            child: [],
            appendChild: function (child) {
                this.child.push(child);
            }
        };
        var segmentList = new SegmentList();
        segmentList.addTag(canvas, canvasDiv)
        expect(canvasDiv.child.length).toBe(1);
        expect(canvasDiv.child[0].id).toBe('div0');
        expect(canvasDiv.child[0].innerHTML).toEqual("<el-tag style=\"background: #e5e9f2\">標記1 </el-tag>");
    })

});
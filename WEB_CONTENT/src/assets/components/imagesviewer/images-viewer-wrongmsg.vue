<template >
	<div class = "wrong-msg" >
		<el-card >
			<template slot = "header" >
				亲爱的用户，在您刚才的评判过程中，我们发现了错误的判决：
			</template >
			<div style = "text-align: center" >
				<div id = "canvasDiv2" >
	                <div v-html = "canvasHtml" >
	                    {{canvasHtml}}
	                </div >
	                <div v-html = "tagHtml" >{{tagHtml}}</div >
	            </div >
	            <p >这道题的图片和标注如上图所示</p >
	            <p >您认为:{{wrongAnswerPairs[0]}}</p >
	            <p >但实际上：{{wrongAnswerPairs[1]}}</p >
	            <p >您的答案对标注的质量非常关键，请您在下次的标注中更加<strong >用心</strong >和<strong >仔细</strong ></p >
	            <p >我们还会<strong >继续检查</strong >您的答案，如果下次再发现问题可能会对您采取<strong >惩罚措施</strong ></p >
	
	            <el-button type = "primary" @click = "read()" >确 定</el-button >
				</div >
		</el-card >
	</div >
</template >

<script >
export default {
    data() {
        return {
            wrongImg: "taskPic-2018061515075768226.jpg",            //返回的是wrongImg
            wrongAnnotation: {},      //这是那个wrongImg的annotation
            tagHtml: '',
            canvasHtml: '<canvas id="canvas"></canvas>',
        }
    },
	methods
}
</script >

<style scoped >

</style >
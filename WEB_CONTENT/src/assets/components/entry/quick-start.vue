<template>
    <div class="quick-start">
        <el-card class="quick-start-card">
            <div slot="header">第一步</div>
            <div class="quick-start-content">申請賬戶，填寫資料並提交</div>
        </el-card>
        <el-card class="quick-start-card">
            <div slot="header">第二步</div>
            <div class="quick-start-content">
                <span>
                眾包工人:去任務大廳領取任務，完成並提交<br>眾包發起者:去創建新的任務，填寫資料並提交
            </span></div>
        </el-card>
        <el-card class="quick-start-card">
            <div slot="header">第三步</div>
            <div class="quick-start-content">
                <span>
                    眾包工人:等候任務獎勵積分發放，期間您可以再去領其它任務進行<br>
                    眾包發起者:查看任務完成人數，完成進度和完成情況
                </span>
            </div>
        </el-card>
        <el-card class="quick-start-card">
            <div slot="header">第四步</div>
            <div class="quick-start-content">
                <span>
                    眾包工人:把積分兌換成金錢<br>
                    眾包發起者:查看任務分析和任務結果，確送支付積分
                </span>
            </div>
        </el-card>
    </div>

</template>

<script>
    export default {
        name: ""
    }
</script>

<style scoped>
    .quick-start {
        width: 100%;
        margin: 1em;
        font-family: Microsoft YaHei;
        text-align: left;
    }

    .quick-start-card {
        margin-bottom: 1em;
    }
</style>
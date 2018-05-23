<template>
    <div class="about">
        <div id="hexagon">
        </div>
        <el-row class="container">
            <el-col :span="12" class="about-left">
                <el-row>
                    <el-col :span="24">
                        <el-row>
                            <el-col :span="24">
                                <h3>我是誰?</h3>
                                <span class="description">
                                    我名Turk,姓N,叫NTurk<br>
                                    我是一個很出色的眾包標註平台<br>
                                    如果您需要為您的圖片集進行標註<br>
                                    我可以幫您<br>
                                </span>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="24">
                                <h3>我來自那裡?</h3>
                                <span class="description">我來自南京大學軟件學院<br>
                                由大二學生陳驍、吉宇哲、賴健明和<br>
                                連遠翔開發，剛剛出生還沒滿月</span><br>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="24">
                                <h3>我要到那裡去?</h3>
                                <span class="description">很不幸的<br>
                                我的父母要我把交到老師和助教<br>
                                父母說是要把我交去做身體檢查之類的<br>
                                希望檢查完後能夠成為<br>
                                一個出色的眾包標註平台</span>
                            </el-col>
                        </el-row>
                    </el-col>
                </el-row>
            </el-col>
            <el-col :span="12" class="about-right">
                <el-row>
                    <el-col :span="24">
                        <el-row>
                            <el-col :span="24">
                                <h3>我要做甚麼?</h3>
                                <span class="description">如果您的圖片集需要標註<br>
                                可以上傳給我<br>
                                我會找最優秀的工人來給您完成<br>
                                只需要很少的費用<br>
                                就夠獲得大量的完成品<br>
                                同時我也會分析一些統計數據和完成質量<br>
                                通過評分和獎勵系統<br>
                                監督和激勵工人去完成任務<br>
                                您可以<br>
                                    　　1.選擇任務類型
                                如整體標註、區域劃分、區域標註等<br>
                                　　2.指定經驗高的工人來幫你完成任務<br>
                                　　3.您可以設置不同的獎勵方式來吸引工人來完成你的任務</span>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="24">
                                <h3>我能給你甚麼?</h3>
                                <span class="description">如果您是眾包工人<br>
                                　　1.您可以獲得獎勵積分，積分可以兌換成金錢<br><br>
                                如果你的眾包發起者<br>
                                　　2.您可以發起任務，在短時間內完成的圖片集標註</span><br>
                            </el-col>
                        </el-row>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>

    </div>
</template>

<script>
    export default {
        name: ""
    }
</script>

<style scoped>
    .container {
        margin-bottom: 20px;
    }

    .about-right {
        text-align: left;
        padding-left: 20px;
        border-color: #A9A9A9;
        border-right-width: 0;
        border-left-width: 2.5px;
        border-top-width: 0;
        border-bottom-width: 0;
        border-style: solid;
        /*height: 120%;*/
    }

    .about-left {
        text-align: right;
        padding-right: 20px;
        height: 120%;
    }

    .about {
        font-family: Microsoft YaHei;
        margin-top: 3em;
        margin-left: 8em;
        margin-right: 8em;
        border-color: #A9A9A9;
        border-width: 1em;
        border-style: solid;
    }

    #hexagon {
        font-size: 30px;
        width: 100px;
        height: 55px;
        background: #A9A9A9;
        position: relative;
        margin: 50px auto;
    }

    #hexagon:before {
        content: "";
        width: 0;
        height: 0;
        position: absolute;
        top: -25px;
        left: 0;
        border-left: 50px solid transparent;
        border-right: 50px solid transparent;
        border-bottom: 25px solid #A9A9A9;
    }

    #hexagon:after {
        content: "";
        width: 0;
        height: 0;
        position: absolute;
        bottom: -25px;
        left: 0;
        border-left: 50px solid transparent;
        border-right: 50px solid transparent;
        border-top: 25px solid #A9A9A9;
    }

    .description {
        font-weight: lighter;
    }
</style>
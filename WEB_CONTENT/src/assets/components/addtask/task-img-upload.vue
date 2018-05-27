<template>
    <div class="taskImgUpload">
        <el-upload
                ref="upload"
                class="upload-demo"
                multiple
                :action=url
                :auto-upload="false"
                :headers="headers"
                :on-change="onChange"
                :on-remove="onRemove"
                :http-request="uploadToServer"
                list-type="picture-card">
            <i class="el-icon-upload" style="alignment: left"></i>
        </el-upload>
    </div>
</template>

<script>
    import moment from 'moment'
    import lrz from 'lrz'

	export default {
		data() {
			return {
			    imageNum: 0,
				imageNames: [],
                url: "http://localhost:8086/image",
			};
		},
		beforeDestroy() {
			this.$bus.off("uploadImageSet");
			this.$bus.off("getImageNames");
            this.$bus.off("getImageNum");
		},
		mounted() {
			let _this = this;
			this.$nextTick(_this.setUpBusEvent)
		},
		methods: {
			/* 往vue-bus注冊事件 */
			setUpBusEvent() {
				this.$bus.$on("uploadImageSet", () => {
						this.uploadImageSet();
					}
				);
				this.$bus.$on("getImageNames", (callback) => {
						this.getImageNames(callback);
					}
				);
                this.$bus.$on("getImageNum", (callback) => {
                        this.getImageNum(callback);
                    }
                );
			},
            onChange(file, fileList) {
			    this.imageNum = fileList.length;
            },
            onRemove(file, fileList) {
                this.imageNum = fileList.length;
            },
			// 提交任務數據一同提交
			uploadImageSet() {
				// console.log("in upload image set");
                this.$refs.upload.submit();
			},
			// 異步取得圖片的名稱
			getImageNames(callback) {
				// console.log("here is getImageNames " + this.imageNames);
                callback(this.imageNames);
			},
            // 異步取得圖片的名稱
            getImageNum(callback) {
                // console.log("here is getImageNames " + this.imageNames);
                callback(this.imageNum);
            },
            uploadToServer(request) {
			    let file = request.file;
                lrz(file, {width: 800, height: 800, quality: 0.7}).then((rst)=>{
                    let pic = rst.file;
                    let fileName = moment().format('YYYYMMDDHHmmssSSS') + Math.round(Math.random() * 1000).toString();
                    let keyName = `taskPic-${fileName}.jpg`;
                    let formData = new FormData();
                    let renamedPic = new File([pic], keyName, {type: 'jpg'});

                    formData.append('file', renamedPic);
                    formData.append('token', this.$store.getters.getToken);
                    formData.append('key', keyName);

                    this.$http({
                        url: this.url,
                        method: "POST",
                        headers: {Authorization: this.$store.getters.getToken},
                        data: formData
                    }).then((res)=>{
                        this.imageNames.push(keyName);
                    }).catch((error)=> {
                        console.log(error);
                    });
                }).catch((err)=>{
                    console.log(err);
                });
			}
		},
		computed: {
			headers() {   //将获得headers放在computed计算属性中试试,将token变成store.state.token
				return {Authorization: this.$store.getters.getToken};
			}
		}
	}</script>

<style scoped>
    .taskImgUpload {
        text-align: left;
    }
</style>
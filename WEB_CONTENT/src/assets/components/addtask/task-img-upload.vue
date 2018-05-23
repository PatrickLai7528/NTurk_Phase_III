<template>
    <div class="taskImgUpload">
        <el-upload
                ref="upload"
                class="upload-demo"
                action="http://localhost:8086/image"
                multiple
                :auto-upload="false"
                :headers="headers"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :on-change="onChange"
                list-type="picture-card">
            <i class="el-icon-upload" style="alignment: left"></i>
        </el-upload>
    </div>
</template>

<script>
	export default {
		data() {
			return {
				imageNames: []
			};
		},
		beforeDestroy() {
			this.$bus.off("uploadImageSet");
			this.$bus.off("getImageNames");
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
			},
			handleRemove(file, fileList) {
				this.handleSuccess("remove", file, fileList);   //在删除后也调用emit
				console.log(file, fileList);
			},
			handlePreview(file) {
				console.log(file);
			},
			onChange(file, fileList) {    //在上傳圖片之前,先把圖片的名稱push到數組裡
				// console.log(file);
				this.imageNames.push(file.name);
				// console.log(this.imageNames);
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
import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex);
/*
    use this.$store instead of import store.js
    use this.$store.dispatch('actionName') to set state.xxx
    use this.$store.getters.getXxxx instead of this.$store.state.xxxx
    WARNING: DON'T ADD () AFTER this.$store.getters.getXxxx
*/
var store = new Vuex.Store({
    state: {
        isLogin: false,
        userId: "",
        userType: "",
        token: null,
        annotationIds:[],
        imgNames:[]
    },
    mutations: {
        LOG_IN(state, data) {  //登入，保存状态
            sessionStorage.setItem("userId", data.userId);  //添加到sessionStorage
            sessionStorage.setItem("token", data.token);  //添加到sessionStorage
            sessionStorage.setItem("userType", data.userType);
            sessionStorage.setItem("isLogin", true);
            state.userId = data.userId;           //同步的改变store中的状态
            state.token = data.token;
            state.userType = data.userType;
            state.isLogin = true
        },
        SIGN_OUT(state) {   //退出，删除状态
            sessionStorage.removeItem("userId");  //移除sessionStorage
            sessionStorage.removeItem("token");
            sessionStorage.removeItem("userType");
            sessionStorage.removeItem("isLogin");
            sessionStorage.removeItem('annotationIds');
            sessionStorage.removeItem('imgNames');
            state.userId = '';           //同步的改变store中的状态
            state.token = null;
            state.userType = '';
            state.isLogin = false;
            state.imgNames = [];
            state.annotationIds = [];
        },
        changeAnnotationIds(state,newList){      //更改AnnotationIds的值
            let temp = JSON.stringify(newList);
            sessionStorage.setItem('annotationIds',temp);
            state.annotationIds = newList;
        },
        changeImgNames(state,newList){
            let temp = JSON.stringify(newList);
            console.log(temp);
            sessionStorage.setItem('imgNames',temp);
            state.imgNames = newList;
        }
    },
    actions: {
        logIn({commit}, data) {
            commit("LOG_IN", data);
        },
        doSignOut({commit}) {
            commit("SIGN_OUT");
        }
    },
    getters: {
        getUserId(state) {
            if (!state.isLogin || state.isLogin === null || state.isLogin === undefined) {
                state.isLogin = sessionStorage.getItem('isLogin');   //从sessionStorage中读取状态
                state.userId = sessionStorage.getItem('userId');
                state.token = sessionStorage.getItem('token');
                state.userType = sessionStorage.getItem('userType');
            }
            return state.userId;
        },
        getToken(state) {
            if (!state.isLogin || state.isLogin === null || state.isLogin === undefined) {
                state.isLogin = sessionStorage.getItem('isLogin');   //从sessionStorage中读取状态
                state.userId = sessionStorage.getItem('userId');
                state.token = sessionStorage.getItem('token');
                state.userType = sessionStorage.getItem('userType');
            }
            return state.token;
        },
        getUserType(state) {
            if (!state.isLogin || state.isLogin === null || state.isLogin === undefined) {
                state.isLogin = sessionStorage.getItem('isLogin');   //从sessionStorage中读取状态
                state.userId = sessionStorage.getItem('userId');
                state.token = sessionStorage.getItem('token');
                state.userType = sessionStorage.getItem('userType');
            }
            return state.userType;
        },
        getAnnotationIds(state){
            state.annotationIds = JSON.parse(sessionStorage.getItem('annoatationIds'));
            return state.annotationIds;
        },
        getImgNames(state){
            state.imgNames = JSON.parse(sessionStorage.getItem('imgNames'));
            return state.imgNames;
        }
    }
});

Vue.prototype.$store = store;
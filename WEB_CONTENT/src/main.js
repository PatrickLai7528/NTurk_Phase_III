import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router.js'
import axios from 'axios'
import vuex from 'vuex'
import VueBus from 'vue-bus';

Vue.use(VueBus);
Vue.use(ElementUI)
Vue.use(vuex);

Vue.prototype.$http = axios;
new Vue({
    el: '#app',
    router,
    axios,
    render: h => h(App)
})

// 任務大廳
import taskLobby from './assets/components/common/common-main.vue'
// 區域劃分
import imagesEditorSegment from './assets/components/imageseditor/images-editor-segment.vue'
// 區域標註
import imagesEditorFrame from './assets/components/imageseditor/images-editor-frame.vue'
// 整體標註
import imagesEditorGeneral from './assets/components/imageseditor/images-editor-general.vue'
// 註冊和登錄
import entry from './assets/components/entry/entry.vue'
// entry的組件
import logIn from './assets/components/entry/log-in.vue'
// entry的組件
import signUp from './assets/components/entry/sign-up.vue'
// 新增任務
import addTask from './assets/components/addtask/add-task.vue'
// 關於我們
import requesterLobby from './assets/components/common/common-requester-lobby.vue'
//requester的查看某个任务的lobby界面
import imagesViewGeneral from './assets/components/imagesviewer/images-view-general.vue'
//requester查看general标注界面
import imagesViewSegment from './assets/components/imagesviewer/images-view-segment.vue'
//requester查看segment标注界面
import imagesViewFrame from './assets/components/imagesviewer/images-view-frame.vue'
//requester查看frame标注界面
import requesterTaskLobby from './assets/components/common/common-requester-task-lobby.vue'
//requester查看所有他发起的task的信息的界面
import about from './assets/components/common/common-about.vue'
import userProfile from './assets/components/userprofile/user-profile.vue'
import quickStart from './assets/components/entry/quick-start.vue'
import admin from './assets/components/admin/admin.vue'
import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "./assets/store/store";

Vue.use(VueRouter);

const routes = [
    {
        path: '/',
        redirect: 'entry/login'
    }, {
        path: '/admin',
        component: admin,
        name: 'admin',
        meta: {
            requireAuth: true,  // 添加该字段，表示进入这个路由是需要登录的
        },
    },
    {
        path: '/tasklobby',
        component: taskLobby,
        name: 'tasklobby'
    },
    {
        path: '/addtask',
        component: addTask,
        name: 'addtask',
        meta: {
            requireAuth: true,  // 添加该字段，表示进入这个路由是需要登录的
        },
    },
    {
        path: '/about',
        component: about,
        name: 'about'
    },
    {
        path: '/profile',
        component: userProfile,
        name: 'profile',
        children:[
            {
                path: '',
                redirect:'requestertasklobby',
            },
            {
                path:'requestertasklobby',
                component:requesterTaskLobby,
                name:'requestertasklobby',
            },
            {
                path:'requesterlobby/:taskId',
                component:requesterLobby,
                name:'requesterlobby',
            }
        ],
        meta: {
            requireAuth: true,  // 添加该字段，表示进入这个路由是需要登录的
        },
    },
    {
        path:'/viewgeneral/:taskId/:contractId',
        component:imagesViewGeneral,
        name:'viewgeneral',
        meta:{
            requireAuth: true,
        }
    },
    {
        path:'/viewframe/:taskId/:contractId',
        component:imagesViewFrame,
        name:'viewframe',
        meta:{
            requireAuth: true,
        }
    },
    {
        path:'/viewsegment/:taskId/:contractId',
        component:imagesViewSegment,
        name:'viewsegment',
        meta:{
            requireAuth: true,
        }
    },
    // 進入時的頁面
    {
        path: '/entry',
        component: entry,
        name: 'entry',
        children: [
            {
                path: 'login',
                component: logIn,
                name: 'login'
            },
            {
                path: 'signup',
                component: signUp,
                name: 'signup'
            },
            {
                path: '',
                component: quickStart,
                name: 'quickstart'
            }
        ]
    },
    {
        name: 'segment',
        path: '/segment/:taskId',
        component: imagesEditorSegment,
        meta: {
            requireAuth: true,  // 添加该字段，表示进入这个路由是需要登录的
        },
    },
    {
        name: 'frame',
        path: '/frame/:taskId',
        component: imagesEditorFrame,
        meta: {
            requireAuth: true,  // 添加该字段，表示进入这个路由是需要登录的
        },
    },
    {
        name: 'general',
        path: '/general/:taskId',
        component: imagesEditorGeneral,
        meta: {
            requireAuth: true,  // 添加该字段，表示进入这个路由是需要登录的
        },
    }
];
const router = new VueRouter({
    routes
});

export default router
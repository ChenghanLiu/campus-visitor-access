import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

// 1) 现有页面
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import pay from '@/views/pay'
import register from '@/views/register'
import center from '@/views/center'

// 2) 占位页：不依赖额外文件，先把路由占住（不报错）
function ComingSoon(title, backPath = '/') {
	return {
		name: `${title}占位页`,
		render(h) {
			return h('div', { style: { padding: '24px' } }, [
				h('h2', { style: { margin: '0 0 12px' } }, `${title}（开发中）`),
				h(
					'p',
					{ style: { margin: '0 0 16px', color: '#666' } },
					'该模块页面尚未完成，但路由已预留。后续实现页面后，把这里的占位组件替换为真实 import 即可。'
				),
				h(
					'button',
					{
						style: {
							padding: '8px 12px',
							borderRadius: '6px',
							border: '1px solid #dcdfe6',
							background: '#fff',
							cursor: 'pointer'
						},
						on: {
							click: () => {
								window.location.hash = `#${backPath}`
							}
						}
					},
					'返回系统首页'
				)
			])
		}
	}
}

// 3) 路由表（保持你现有结构，只增加顶层模块路由）
export const routes = [
	{
		path: '/',
		name: '系统首页',
		component: Index,
		children: [
			{
				path: '/',
				name: '系统首页',
				component: Home,
				meta: { icon: '', title: 'center', affix: true }
			},
			{
				path: '/updatePassword',
				name: '修改密码',
				component: UpdatePassword,
				meta: { icon: '', title: 'updatePassword' }
			},

			// ✅ 已实现：设备管理
			{
				path: '/equipment',
				name: '设备管理',
				component: () => import('@/views/modules/equipment/list.vue')
			},


			{
				path: "/users",
				name: "用户管理",
				component: () => import("@/views/modules/user/list.vue")
			},

			{
				path: '/profile',
				name: '个人信息',
				component: ComingSoon('个人信息')
			},
			{
				path: "/labs",
				name: "实验室管理",
				component: () => import("@/views/modules/lab/list.vue")
			},
			{
				path: "/experiments",
				name: "实验项目管理",
				component: () => import("@/views/modules/experiment/list.vue")
			},

			{
				path: '/reservations',
				component: () => import('@/views/modules/reservation/list.vue')
			},

			{
				path: "/training",
				name: "安全培训管理",
				component: () => import("@/views/modules/training/list.vue")
			},

			{
				path: "/inspection",
				name: "安全检查管理",
				component: () => import("@/views/modules/inspection/list.vue")
			},

			{
				path: "/analytics",
				name: "统计分析",
				component: () => import("@/views/modules/stats/index.vue")
			}

		]
	},

	// 登录/注册保持不动
	{
		path: '/login',
		name: 'login',
		component: Login,
		meta: { icon: '', title: 'login' }
	},
	{
		path: '/register',
		name: 'register',
		component: register,
		meta: { icon: '', title: 'register' }
	},

	// 404
	{
		path: '*',
		component: NotFound
	}
]

// 4) 实例化 Router（保持 hash 模式）
const router = new VueRouter({
	mode: 'hash',
	routes
})

// 5) 修复重复跳转报错（保持你原来的写法）
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
	return originalPush.call(this, location).catch(err => err)
}

export default router

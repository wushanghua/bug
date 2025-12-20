import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", redirect: "/login" },

    // 管理后台路由
    {
      path: "/manager",
      meta: {
        title: "BUG后台管理系统",
        requireAuth: true, // 添加需要登录的标识
      },
      component: () => import("../views/Manager.vue"),
      children: [
        { path: "", redirect: { name: "adminHome" } }, // 使用命名路由重定向
        { path: "home", name:'adminHome', component: () => import("@/components/management/home.vue"),},
        { path: "userList", component: () => import("../components/management/userList.vue") },
        { path: "bug_list", component: () => import("../components/bug_list.vue") },
        { path: "bugList", component: () => import("../components/management/bugList.vue") },
        { path: "bug_detail/:id", component: () => import("../components/bug_detail.vue") },
        { path: "notice", component: () => import("../components/management/notice.vue") },
        { path: "userinfo", component: () => import("../components/userInfo.vue") },
      ],
    },

    // 前台路由
    {
      path: "/bugmana",
      meta: {
        title: "bug提交系统",
        requireAuth: true, // 添加需要登录的标识
      },
      component: () => import("../views/BugSubmitMana.vue"),
      children: [
        { path: "userhome", component: () => import("../components/userHome.vue") },
        { path: "bug_list", component: () => import("../components/bug_list.vue") },
        { path: "submit_bug", component: () => import("../components/SubmitBug.vue") },
       { path: "userinfo", component: () => import("../components/userInfo.vue") },
       { path: "bug_detail/:id", component: () => import("../components/bug_detail.vue") },
      ],
    },

    // 不需要登录的页面
    {
      path: "/login",
      meta: { title: "登录", requireAuth: false },
      component: () => import("@/views/login.vue"),
    },
    {
      path: "/register",
      meta: { title: "注册", requireAuth: false },
      component: () => import("@/views/Register.vue"),
    },
    {
      path: "/404",
      meta: { title: "404", requireAuth: false },
      component: () => import("../views/404.vue"),
    },
    { path: "/:pathMatch(.*)", redirect: "/404" },
  ],
});

// 路由守卫控制访问权限
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || "系统";

  // 检查是否需要登录
  if (to.meta.requireAuth) {
    // 检查是否已登录（从localStorage获取用户信息）
    const user = localStorage.getItem("xm-pro-user");
    if (user) {
      // 已登录，允许访问
      next();
    } else {
      // 未登录，重定向到登录页
      ElMessage.error("请先登录");
      next("/login");
    }
  } else {
    // 不需要登录的页面，直接访问
    next();
  }
});

export default router;

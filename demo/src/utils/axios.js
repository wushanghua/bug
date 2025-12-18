import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
    baseURL: "http://localhost:8080",
    //  baseURL: import.meta.env.VITE_API_BASE_URL, // 从环境变量获取 baseURL
    timeout: 30000 // 后台接口超时时间
});

// request 拦截器
// 可以在请求发送前对请求做一些处理
request.interceptors.request.use(
    config => {
        // 从localStorage获取token并添加到请求头
        const tokenStr = localStorage.getItem('xm-pro-user');
        if (tokenStr) {
            // token可能是JSON字符串，需要解析
            let token = tokenStr;
            try {
                const parsed = JSON.parse(tokenStr);
                // 如果解析后是字符串，使用解析后的值；如果是对象，尝试获取token字段或使用原值
                if (typeof parsed === 'string') {
                    token = parsed;
                } else if (typeof parsed === 'object' && parsed !== null) {
                    // 如果是对象，尝试获取token字段，否则使用原字符串
                    token = parsed.token || tokenStr;
                }
            } catch (e) {
                // 如果不是JSON，直接使用原字符串
                token = tokenStr;
            }
            // 确保token是字符串类型，并去除可能的引号
            if (typeof token === 'string') {
                token = token.trim();
                // 去除首尾可能的双引号
                if (token.startsWith('"') && token.endsWith('"')) {
                    token = token.substring(1, token.length - 1);
                }
            } else {
                // 如果不是字符串，转换为字符串
                token = String(token);
            }
            config.headers['Authorization'] = token;
        }
        
        // 如果是FormData，不设置Content-Type，让浏览器自动设置（包含boundary）
        if (!(config.data instanceof FormData)) {
            config.headers['Content-Type'] = 'application/json;charset=utf-8';
        }
        return config;
     },
    error => {
        return Promise.reject(error);
    }
);

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        return res;
    },
    error => {
        if (error.response) {
            // 服务器返回了响应，但状态码不在2xx范围内
            const status = error.response.status;
            if (status === 401) {
                ElMessage.error("未登录或登录已过期，请重新登录");
            } else if (status === 403) {
                ElMessage.error("没有权限执行此操作");
            } else if (status === 404) {
                ElMessage.error("未找到请求接口");
            } else if (status === 500) {
                ElMessage.error("系统异常，请查看后端控制台报错");
            } else {
                const msg = error.response.data?.msg || error.response.data?.message || `请求失败 (${status})`;
                ElMessage.error(msg);
            }
        } else if (error.request) {
            // 请求已发出，但没有收到响应
            ElMessage.error("网络错误，请检查网络连接");
        } else {
            // 其他错误
            console.error('请求错误：', error.message);
            ElMessage.error("请求失败：" + error.message);
        }
        return Promise.reject(error);
    }
);

export default request;
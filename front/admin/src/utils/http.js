import axios from "axios";

const http = axios.create({
    timeout: 1000 * 30,
    withCredentials: false,
    baseURL: "/", // ✅ 不改 baseURL
    headers: {
        "Content-Type": "application/json; charset=utf-8"
    }
});

http.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("Token");

        // ✅ 后端只认 Authorization: Bearer xxx
        if (token) {
            config.headers["Authorization"] = `Bearer ${token}`;
        }

        // ❌ 防止旧模板残留：不要再传 Token 这个头
        if (config.headers && config.headers["Token"]) {
            delete config.headers["Token"];
        }

        return config;
    },
    (error) => Promise.reject(error)
);

http.interceptors.response.use(
    (response) => response,
    (error) => Promise.reject(error)
);

export default http;

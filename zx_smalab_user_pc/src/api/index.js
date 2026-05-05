import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '',
  timeout: 30000
});

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.token = token;
  }
  return config;
});

request.interceptors.response.use(
  response => {
    const data = response.data;
    if (data.code === 777) {
      ElMessage.error('登录过期，请重新登录');
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      localStorage.removeItem('userType');
      window.location.href = '/login';
      return Promise.reject(data);
    }
    if (data.code && data.code !== 200) {
      console.warn(`[API] 请求失败: code=${data.code}, msg=${data.msg}`);
      return Promise.reject(data);
    }
    return data;
  },
  error => {
    if (error.message && error.message.indexOf('Network Error') !== -1) {
      ElMessage.error('网络连接失败，请检查网络');
    }
    return Promise.reject(error);
  }
);

export default request;

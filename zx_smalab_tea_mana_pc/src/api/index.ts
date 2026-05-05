import axios, { AxiosInstance, AxiosError, AxiosRequestConfig, InternalAxiosRequestConfig, AxiosResponse } from "axios";
import { showFullScreenLoading, tryHideFullScreenLoading } from "@/components/Loading/fullScreen";
import { LOGIN_URL } from "@/config";
import { ElMessage } from "element-plus";
import { ResultData } from "@/api/interface";
import { ResultEnum } from "@/enums/httpEnum";
import { AxiosCanceler } from "./helper/axiosCancel";
import { useUserStore } from "@/stores/modules/user";
import router from "@/routers";

export interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  loading?: boolean;
  cancel?: boolean;
}

const config = {
  // 默认地址请求地址，可在 .env.** 文件中修改
  baseURL: import.meta.env.VITE_API_URL as string,
  // 设置超时时间
  timeout: ResultEnum.TIMEOUT as number
  // 跨域时候允许携带凭证
  // withCredentials: true
};

const axiosCanceler = new AxiosCanceler();

class RequestHttp {
  service: AxiosInstance;
  public constructor(config: AxiosRequestConfig) {
    // instantiation
    this.service = axios.create(config);

    /**
     * @description 请求拦截器
     * 客户端发送请求 -> [请求拦截器] -> 服务器
     * token校验(JWT) : 接受服务器返回的 token,存储到 vuex/pinia/本地储存当中
     */
    this.service.interceptors.request.use(
      (config: CustomAxiosRequestConfig) => {
        // 检查是否需要拦截所有接口请求
        const mockApiError = import.meta.env.VITE_MOCK_API_ERROR === "true";
        if (mockApiError) {
          tryHideFullScreenLoading();
          ElMessage.error("后端未连接，接口请求已被拦截");
          return Promise.reject(new Error("API requests are intercepted (VITE_MOCK_API_ERROR=true)"));
        }

        const userStore = useUserStore();
        // 重复请求不需要取消，在 api 服务中通过指定的第三个参数: { cancel: false } 来控制
        config.cancel ?? (config.cancel = true);
        config.cancel && axiosCanceler.addPending(config);
        // 当前请求不需要显示 loading，在 api 服务中通过指定的第三个参数: { loading: false } 来控制
        config.loading ?? (config.loading = true);
        config.loading && showFullScreenLoading();
        if (config.headers && typeof config.headers.set === "function") {
          config.headers.set("token", userStore.token);
        }
        return config;
      },
      (error: AxiosError) => {
        return Promise.reject(error);
      }
    );

    /**
     * @description 响应拦截器
     *  服务器换返回信息 -> [拦截统一处理] -> 客户端JS获取到信息
     */
    this.service.interceptors.response.use(
      (response: AxiosResponse) => {
        const { data, config } = response;
        const userStore = useUserStore();
        axiosCanceler.removePending(config);
        tryHideFullScreenLoading();

        // 登录过期
        if (data.code == ResultEnum.TOKEN_ERROR) {
          userStore.setToken("");
          router.replace(LOGIN_URL);
          ElMessage.error("登录过期，请您重新登录！");
          return Promise.reject(data);
        }

        // 其他错误不再全局弹窗，由各页面自行处理
        if (data.code && data.code !== ResultEnum.SUCCESS) {
          console.warn(`[API] ${config.url} 请求失败: code=${data.code}, msg=${data.msg}`);
          return Promise.reject(data);
        }
        return data;
      },
      async (error: AxiosError) => {
        tryHideFullScreenLoading();
        // 仅在网络错误时提示，不弹 HTTP 状态码错误
        if (error.message.indexOf("Network Error") !== -1) {
          ElMessage.error("网络连接失败，请检查网络");
        } else if (error.message.indexOf("timeout") !== -1) {
          ElMessage.error("请求超时，请稍后重试");
        }
        if (!window.navigator.onLine) router.replace("/500");
        return Promise.reject(error);
      }
    );
  }

  /**
   * @description 常用请求方法封装
   */
  get<T>(url: string, params?: object, _object = {}): Promise<ResultData<T>> {
    return this.service.get(url, { params, ..._object });
  }
  post<T>(url: string, params?: object | string, _object = {}): Promise<ResultData<T>> {
    return this.service.post(url, params, _object);
  }
  put<T>(url: string, params?: object, _object = {}): Promise<ResultData<T>> {
    return this.service.put(url, params, _object);
  }
  delete<T>(url: string, params?: any, _object = {}): Promise<ResultData<T>> {
    return this.service.delete(url, { params, ..._object });
  }
  download(url: string, params?: object, _object = {}): Promise<BlobPart> {
    return this.service.post(url, params, { ..._object, responseType: "blob" });
  }
}

export default new RequestHttp(config);

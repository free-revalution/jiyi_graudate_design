import { Login } from "@/api/interface/index";
import authMenuList from "@/assets/json/authMenuList.json";
import authButtonList from "@/assets/json/authButtonList.json";
import http from "@/api";

export const loginApi = (data: Login.ReqLoginForm) => {
  return http.post(`/user_permiss/auth/login`, data);
};

export const getAuthMenuListApi = () => {
  return authMenuList;
};

export const getAuthButtonListApi = () => {
  return authButtonList;
};

export const logoutApi = () => {
  return http.post(`/user_permiss/auth/logout`);
};

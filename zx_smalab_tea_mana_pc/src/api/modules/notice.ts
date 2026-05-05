import http from "@/api";

export const getNoticeList = (courseId: number) => http.get(`/api/course/${courseId}/notice/list`);
export const createNotice = (courseId: number, params: any) => http.post(`/api/course/${courseId}/notice`, params);
export const updateNotice = (courseId: number, id: number, params: any) => http.put(`/api/course/${courseId}/notice/${id}`, params);
export const deleteNotice = (courseId: number, id: number) => http.delete(`/api/course/${courseId}/notice/${id}`);

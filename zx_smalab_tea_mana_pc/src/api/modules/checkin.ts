import http from "@/api";

export const getCheckInList = (courseId: number) => http.get(`/api/course/${courseId}/checkin/list`);
export const createCheckIn = (courseId: number, params: any) => http.post(`/api/course/${courseId}/checkin`, params);
export const getCheckInDetail = (courseId: number, id: number) => http.get(`/api/course/${courseId}/checkin/${id}`);
export const startCheckIn = (courseId: number, id: number) => http.post(`/api/course/${courseId}/checkin/${id}/start`);
export const endCheckIn = (courseId: number, id: number) => http.post(`/api/course/${courseId}/checkin/${id}/end`);
export const getCheckInRecords = (courseId: number, id: number) => http.get(`/api/course/${courseId}/checkin/${id}/records`);
export const getCheckInStatistics = (courseId: number, id: number) => http.get(`/api/course/${courseId}/checkin/${id}/statistics`);

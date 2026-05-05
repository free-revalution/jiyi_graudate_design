import http from "@/api";

export const getTrainingList = (courseId: number, params: any) => http.get(`/api/course/${courseId}/training/list`, params, { cancel: false });
export const createTraining = (courseId: number, params: any) => http.post(`/api/course/${courseId}/training`, params);
export const getTrainingDetail = (courseId: number, id: number) => http.get(`/api/course/${courseId}/training/${id}`);
export const updateTraining = (courseId: number, id: number, params: any) => http.put(`/api/course/${courseId}/training/${id}`, params);
export const deleteTraining = (courseId: number, id: number) => http.delete(`/api/course/${courseId}/training/${id}`);
export const publishTraining = (courseId: number, id: number) => http.post(`/api/course/${courseId}/training/${id}/publish`);
export const getTrainingNodes = (courseId: number, id: number) => http.get(`/api/course/${courseId}/training/${id}/nodes`);
export const saveTrainingNodes = (courseId: number, id: number, params: any) => http.post(`/api/course/${courseId}/training/${id}/nodes`, params);
export const getTrainingStudents = (courseId: number, id: number) => http.get(`/api/course/${courseId}/training/${id}/students`);
export const getTrainingStudentDetail = (courseId: number, id: number, userId: number) => http.get(`/api/course/${courseId}/training/${id}/student/${userId}`);

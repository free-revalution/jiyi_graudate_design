import http from "@/api";

export const getExerciseList = (courseId: number, params: any) => http.get(`/api/course/${courseId}/exercise/list`, params);
export const createExercise = (courseId: number, params: any) => http.post(`/api/course/${courseId}/exercise`, params);
export const getExerciseDetail = (courseId: number, id: number) => http.get(`/api/course/${courseId}/exercise/${id}`);
export const updateExercise = (courseId: number, id: number, params: any) => http.put(`/api/course/${courseId}/exercise/${id}`, params);
export const deleteExercise = (courseId: number, id: number) => http.delete(`/api/course/${courseId}/exercise/${id}`);
export const publishExercise = (courseId: number, id: number) => http.post(`/api/course/${courseId}/exercise/${id}/publish`);

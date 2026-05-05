import http from "@/api";

export const getHomeworkList = (courseId: number, params: any) => http.get(`/api/course/${courseId}/homework/list`, params);
export const createHomework = (courseId: number, params: any) => http.post(`/api/course/${courseId}/homework`, params);
export const getHomeworkDetail = (courseId: number, id: number) => http.get(`/api/course/${courseId}/homework/${id}`);
export const updateHomework = (courseId: number, id: number, params: any) => http.put(`/api/course/${courseId}/homework/${id}`, params);
export const deleteHomework = (courseId: number, id: number) => http.delete(`/api/course/${courseId}/homework/${id}`);
export const publishHomework = (courseId: number, id: number) => http.post(`/api/course/${courseId}/homework/${id}/publish`);
export const getHomeworkAnswers = (courseId: number, id: number) => http.get(`/api/course/${courseId}/homework/${id}/answers`);
export const gradeHomeworkScore = (courseId: number, id: number, userId: number, score: number) =>
    http.put(`/api/course/${courseId}/homework/${id}/answer/${userId}/score`, { score });

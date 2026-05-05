import http from "@/api";

export const getScoreWeight = (courseId: number) => http.get(`/api/course/${courseId}/score-weight`);
export const saveScoreWeight = (courseId: number, params: any) => http.put(`/api/course/${courseId}/score-weight`, params);

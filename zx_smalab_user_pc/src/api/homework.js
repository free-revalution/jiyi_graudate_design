import request from './index';

export const getMyHomework = (courseId) => request.get(`/api/course/${courseId}/homework/my`);
export const getHomeworkQuestions = (courseId, id) => request.get(`/api/course/${courseId}/homework/${id}/questions`);
export const submitHomework = (courseId, id, answers) => request.post(`/api/course/${courseId}/homework/${id}/submit`, { answers });

import request from './index';

export const getMyExercises = (courseId) => request.get(`/api/course/${courseId}/exercise/my`);
export const getExerciseQuestions = (courseId, id) => request.get(`/api/course/${courseId}/exercise/${id}/questions`);
export const submitExercise = (courseId, id, answers) => request.post(`/api/course/${courseId}/exercise/${id}/submit`, { answers });

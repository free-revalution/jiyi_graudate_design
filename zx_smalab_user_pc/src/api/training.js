import request from './index';

export const getMyTrainings = (courseId) => request.get(`/api/course/${courseId}/training/my`);
export const getTrainingDetail = (courseId, id) => request.get(`/api/course/${courseId}/training/${id}/detail`);

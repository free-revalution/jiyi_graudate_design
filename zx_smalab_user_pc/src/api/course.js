import request from './index';

export const getHotCourses = () => request.get('/api/home/hot-courses');
export const getHomeStats = () => request.get('/api/home/stats');
export const getCourseList = (params) => request.get('/api/courses', { params });
export const getCourseDetail = (id) => request.get(`/api/courses/${id}`);
export const enrollCourse = (id, classId) => request.post(`/api/my/courses/${id}/enroll`, { classId });
export const getCourseClasses = (courseId) => request.get(`/api/course/${courseId}/classes`);
export const getMaterialList = (courseId) => request.get(`/api/course/${courseId}/material/list`);
export const getTrainingList = (courseId) => request.get(`/api/course/${courseId}/training/list`, { params: { page: 1, limit: 100 } });
export const getTrainingNodes = (courseId, trainingId) => request.get(`/api/course/${courseId}/training/${trainingId}/nodes`);
export const getHomeworkList = (courseId) => request.get(`/api/course/${courseId}/homework/my`);
export const getHomeworkQuestions = (courseId, homeworkId) => request.get(`/api/course/${courseId}/homework/${homeworkId}/questions`);
export const submitHomework = (courseId, homeworkId, answers) => request.post(`/api/course/${courseId}/homework/${homeworkId}/submit`, { answers });

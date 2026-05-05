import request from './index';

export const getUserInfo = () => request.get('/api/user/info');
export const updateUserInfo = (data) => request.put('/api/user/info', data);
export const getMyCourses = () => request.get('/api/my/courses');
export const getMyCourseDetail = (courseId) => request.get(`/api/my/course/${courseId}`);
export const unenrollCourse = (courseId) => request.delete(`/api/my/courses/${courseId}/unenroll`);

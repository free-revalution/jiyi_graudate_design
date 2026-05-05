import http from "@/api";

// Course CRUD
export const getCourseList = (params: any) => http.get("/api/course/list", params);
export const createCourse = (params: any) => http.post("/api/course", params);
export const getCourseDetail = (id: number) => http.get(`/api/course/${id}`);
export const updateCourse = (id: number, params: any) => http.put(`/api/course/${id}`, params);
export const deleteCourse = (id: number) => http.delete(`/api/course/${id}`);
export const publishCourse = (id: number) => http.post(`/api/course/${id}/publish`);
export const closeCourse = (id: number) => http.post(`/api/course/${id}/close`);

// Course Terms
export const getCourseTerms = (courseId: number) => http.get(`/api/course/${courseId}/terms`);
export const createCourseTerm = (courseId: number, params: any) => http.post(`/api/course/${courseId}/terms`, params);
export const updateCourseTerm = (courseId: number, id: number, params: any) => http.put(`/api/course/${courseId}/terms/${id}`, params);
export const deleteCourseTerm = (courseId: number, id: number) => http.delete(`/api/course/${courseId}/terms/${id}`);

// Teachers
export const getTeachers = (courseId: number) => http.get(`/api/course/${courseId}/teachers`);
export const addTeacher = (courseId: number, params: any) => http.post(`/api/course/${courseId}/teachers`, params);
export const updateTeacher = (courseId: number, id: number, params: any) => http.put(`/api/course/${courseId}/teachers/${id}`, params);
export const deleteTeacher = (courseId: number, id: number) => http.delete(`/api/course/${courseId}/teachers/${id}`);

// Classes
export const getClasses = (courseId: number) => http.get(`/api/course/${courseId}/classes`);
export const createClass = (courseId: number, params: any) => http.post(`/api/course/${courseId}/classes`, params);

// Students
export const getStudents = (courseId: number, classId: number) => http.get(`/api/course/${courseId}/class/${classId}/students`);
export const addStudent = (courseId: number, classId: number, params: any) => http.post(`/api/course/${courseId}/class/${classId}/students`, params);
export const deleteStudent = (courseId: number, classId: number, id: number) => http.delete(`/api/course/${courseId}/class/${classId}/students/${id}`);

// Course Setting
export const getCourseBaseInfo = (courseId: number) => http.get(`/api/course/${courseId}`);
export const updateCourseBaseInfo = (courseId: number, params: any) => http.put(`/api/course/${courseId}`, params);

// Materials (videos/files)
export const getMaterialList = (courseId: number) => http.get(`/api/course/${courseId}/material/list`);
export const uploadMaterial = (courseId: number, formData: FormData) =>
  http.post(`/api/course/${courseId}/material`, formData, { headers: { "Content-Type": "multipart/form-data" } });
export const deleteMaterial = (courseId: number, id: number) => http.delete(`/api/course/${courseId}/material/${id}`);

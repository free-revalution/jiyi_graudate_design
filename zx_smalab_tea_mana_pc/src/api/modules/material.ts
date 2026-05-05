import http from "@/api";

export const getMaterialList = (courseId: number) => http.get(`/api/course/${courseId}/material/list`);
export const uploadMaterial = (courseId: number, formData: FormData) => http.post(`/api/course/${courseId}/material`, formData);
export const deleteMaterial = (courseId: number, id: number) => http.delete(`/api/course/${courseId}/material/${id}`);

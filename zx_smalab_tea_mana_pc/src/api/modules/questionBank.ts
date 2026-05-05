import http from "@/api";

export const getQuestionBankList = (params: any) => http.get("/api/question-bank/list", params);
export const createQuestionBank = (params: any) => http.post("/api/question-bank", params);
export const updateQuestionBank = (id: number, params: any) => http.put(`/api/question-bank/${id}`, params);
export const deleteQuestionBank = (id: number) => http.delete(`/api/question-bank/${id}`);
export const getQuestionBankItems = (id: number) => http.get(`/api/question-bank/${id}/items`);

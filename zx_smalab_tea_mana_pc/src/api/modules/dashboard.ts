import http from "@/api";

export default {
  getOverviewData() {
    return http.get("/api/dashboard/overview");
  },
  getUserTrendData(params: any) {
    return http.get("/api/dashboard/trend", params);
  },
  getPostTrendData(params: any) {
    return http.get("/api/dashboard/trend", params);
  },
  getCommentTrendData(params: any) {
    return http.get("/api/dashboard/trend", params);
  }
};

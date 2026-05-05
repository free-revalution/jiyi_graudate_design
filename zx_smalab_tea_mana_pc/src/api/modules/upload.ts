import { Upload } from "@/api/interface/index";
import http from "@/api";

export const uploadFile = (params: FormData) => {
  return http.post<Upload.ResFileUrl>(`/user_permiss/minio/upload-system-user-file`, params, {
    cancel: false,
    timeout: 300000
  });
};

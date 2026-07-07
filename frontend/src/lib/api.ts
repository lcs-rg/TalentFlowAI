import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

// Request interceptor: ensure token is always attached
api.interceptors.request.use((config) => {
  if (!config.headers["Authorization"]) {
    const token = localStorage.getItem("talentflow-token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
  }
  return config;
});

// Response interceptor: auto-refresh on 401
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        const { data } = await axios.post(
          `${API_BASE_URL}/api/v1/auth/refresh`,
          {},
          { withCredentials: true }
        );
        const newToken = data.data.accessToken;
        api.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;
        localStorage.setItem("talentflow-token", newToken);
        original.headers["Authorization"] = `Bearer ${newToken}`;
        return api(original);
      } catch {
        if (typeof window !== "undefined") {
          localStorage.removeItem("talentflow-token");
          localStorage.removeItem("talentflow-user");
          window.location.href = "/login";
        }
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);

"use client";

import { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { api } from "./api";

interface User {
  id: string;
  tenantId: string;
  email: string;
  name: string;
  role: string;
  avatarUrl?: string;
}

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string, tenantSlug: string) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
}

interface RegisterData {
  email: string;
  password: string;
  name: string;
  companyName: string;
  companySlug: string;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Try to get user from stored token (session rehydration)
    const stored = localStorage.getItem("talentflow-user");
    if (stored) {
      try { setUser(JSON.parse(stored)); } catch { /* ignore */ }
    }
    setLoading(false);
  }, []);

  const login = async (email: string, password: string, tenantSlug: string) => {
    const { data: res } = await api.post("/api/v1/auth/login", { email, password, tenantSlug });
    const { accessToken, refreshToken, user: u } = res.data;
    // Store refresh in httpOnly, access in memory (via interceptor)
    document.cookie = `refreshToken=${refreshToken}; path=/; max-age=604800; SameSite=Strict; Secure`;
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    localStorage.setItem("talentflow-user", JSON.stringify(u));
    setUser(u);
  };

  const register = async (data: RegisterData) => {
    const { data: res } = await api.post("/api/v1/auth/register", data);
    const { accessToken, refreshToken, user: u } = res.data;
    document.cookie = `refreshToken=${refreshToken}; path=/; max-age=604800; SameSite=Strict; Secure`;
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    localStorage.setItem("talentflow-user", JSON.stringify(u));
    setUser(u);
  };

  const logout = async () => {
    try { await api.post("/api/v1/auth/logout"); } catch { /* ignore */ }
    delete api.defaults.headers.common["Authorization"];
    document.cookie = "refreshToken=; path=/; max-age=0";
    localStorage.removeItem("talentflow-user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be inside AuthProvider");
  return ctx;
}

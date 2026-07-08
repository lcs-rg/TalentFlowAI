"use client";

import { useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { motion } from "motion/react";
import toast from "react-hot-toast";

export default function LoginPage() {
  const { login } = useAuth();
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [tenantSlug, setTenantSlug] = useState("");
  const [needsSlug, setNeedsSlug] = useState(false);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      await login(email, password, needsSlug ? tenantSlug : "");
      toast.success("Bem-vindo de volta!");
      router.push("/");
    } catch (err: any) {
      const msg = err.response?.data?.error?.message || "Credenciais inválidas.";
      if (msg.includes("MULTIPLE_TENANTS")) {
        setNeedsSlug(true);
        toast.error("Email encontrado em múltiplas empresas. Informe o slug.");
      } else {
        toast.error(msg);
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-bg">
      <motion.div
        initial={{ opacity: 0, y: 8 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="w-full max-w-sm mx-auto space-y-8 px-6 py-12"
      >
        <div className="text-center space-y-2">
          <div className="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-primary">
            <span className="text-sm font-semibold text-text-primary">TF</span>
          </div>
          <h1 className="text-lg font-semibold text-text-primary">TalentFlow</h1>
          <p className="text-sm text-text-secondary">Entre na sua conta</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {needsSlug && (
            <div className="space-y-1.5">
              <label className="block text-xs font-medium text-text-secondary">Slug da empresa</label>
              <input
                type="text" value={tenantSlug} onChange={(e) => setTenantSlug(e.target.value)}
                className="w-full h-9 rounded-lg bg-bg-elevated px-3 text-sm text-text-primary placeholder:text-text-disabled border border-border focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent"
                placeholder="minha-empresa" autoFocus required
              />
              <p className="text-[11px] text-status-warning">Seu email está em múltiplas empresas. Informe o slug.</p>
            </div>
          )}

          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-text-secondary">Email</label>
            <input
              type="email" value={email} onChange={(e) => setEmail(e.target.value)}
              className="w-full h-9 rounded-lg bg-bg-elevated px-3 text-sm text-text-primary placeholder:text-text-disabled border border-border focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent"
              placeholder="seu@email.com" autoFocus={!needsSlug} required
            />
          </div>

          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-text-secondary">Senha</label>
            <input
              type="password" value={password} onChange={(e) => setPassword(e.target.value)}
              className="w-full h-9 rounded-lg bg-bg-elevated px-3 text-sm text-text-primary placeholder:text-text-disabled border border-border focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent"
              placeholder="••••••••" required
            />
          </div>

          <button
            type="submit" disabled={loading}
            className="w-full h-9 rounded-lg bg-primary hover:bg-primary-hover text-text-primary text-sm font-medium transition-colors disabled:opacity-50"
          >
            {loading ? "Entrando..." : "Entrar"}
          </button>
        </form>

        <p className="text-center text-xs text-text-disabled">
          Não tem conta?{" "}
          <Link href="/register" className="text-accent hover:underline transition-colors">
            Criar conta gratuita
          </Link>
        </p>
        <p className="text-center text-xs text-text-disabled mt-2">
          É candidato?{" "}
          <Link href="/candidate/login" className="text-accent hover:underline transition-colors">
            Portal do Candidato
          </Link>
        </p>
      </motion.div>
    </div>
  );
}

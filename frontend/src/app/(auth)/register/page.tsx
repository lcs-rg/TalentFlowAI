"use client";

import { useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { motion } from "motion/react";
import toast from "react-hot-toast";

export default function RegisterPage() {
  const { register } = useAuth();
  const router = useRouter();
  const [form, setForm] = useState({ name: "", email: "", password: "", companyName: "", companySlug: "" });
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      await register(form);
      toast.success("Conta criada com sucesso!");
      router.push("/");
    } catch {
      toast.error("Erro ao criar conta.");
    } finally {
      setLoading(false);
    }
  }

  const i = (field: string) => ({
    value: (form as any)[field],
    onChange: (e: any) => setForm({ ...form, [field]: e.target.value }),
    className: "w-full h-9 rounded-lg bg-bg-elevated px-3 text-sm text-text-primary placeholder:text-text-disabled border border-border focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent",
    required: true,
  });

  return (
    <div className="flex min-h-screen items-center justify-center bg-bg">
      <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}
        className="w-full max-w-sm mx-auto space-y-6 px-6 py-12">
        <div className="text-center space-y-2">
          <div className="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-primary">
            <span className="text-sm font-semibold text-text-primary">TF</span>
          </div>
          <h1 className="text-lg font-semibold text-text-primary">Criar conta</h1>
          <p className="text-sm text-text-secondary">Comece a recrutar com inteligência</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3">
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-text-secondary">Seu nome</label>
            <input type="text" placeholder="João Silva" {...i("name")} />
          </div>
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-text-secondary">Email</label>
            <input type="email" placeholder="joao@email.com" {...i("email")} />
          </div>
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-text-secondary">Senha</label>
            <input type="password" placeholder="Mínimo 8 caracteres" {...i("password")} />
          </div>
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-text-secondary">Nome da empresa</label>
            <input type="text" placeholder="Minha Empresa" {...i("companyName")} />
          </div>
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-text-secondary">Slug (URL)</label>
            <input type="text" placeholder="minha-empresa" {...i("companySlug")} />
          </div>

          <button type="submit" disabled={loading}
            className="w-full h-9 rounded-lg bg-primary hover:bg-primary-hover text-text-primary text-sm font-medium transition-colors disabled:opacity-50">
            {loading ? "Criando..." : "Criar conta"}
          </button>
        </form>

        <p className="text-center text-xs text-text-disabled">
          Já tem conta?{" "}
          <Link href="/login" className="text-accent hover:underline transition-colors">Entrar</Link>
        </p>
      </motion.div>
    </div>
  );
}

"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { motion } from "motion/react";
import { api } from "@/lib/api";
import toast from "react-hot-toast";
import Link from "next/link";
import { ArrowLeft, Sparkles } from "lucide-react";

export default function NewJobPage() {
  const router = useRouter();
  const [form, setForm] = useState({ title: "", description: "", department: "", location: "", type: "FULL_TIME", salaryMin: "", salaryMax: "", currency: "BRL" });
  const [loading, setLoading] = useState(false);

  const f = (name: string, placeholder = "") => ({
    value: (form as any)[name],
    onChange: (e: any) => setForm({ ...form, [name]: e.target.value }),
    className: "w-full h-9 rounded-lg bg-bg-elevated px-3 text-sm text-text-primary placeholder:text-text-disabled border border-border focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent",
    placeholder,
    required: name === "title" || name === "description",
  });

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault(); setLoading(true);
    try {
      const payload = {
        ...form,
        salaryMin: form.salaryMin ? Number(form.salaryMin) : null,
        salaryMax: form.salaryMax ? Number(form.salaryMax) : null,
      };
      const { data } = await api.post("/api/v1/jobs", payload);
      toast.success("Vaga publicada!");
      router.push(`/jobs/${data.data.id}`);
    } catch (err: any) {
      const msg = err.response?.data?.error?.message || err.response?.data?.error?.errors?.[0]?.message || "Erro ao criar vaga";
      toast.error(msg);
    } finally { setLoading(false); }
  }

  return (
    <div className="max-w-2xl mx-auto p-4 lg:p-8 space-y-6">
      <div className="flex items-center gap-4">
        <Link href="/jobs" className="p-1.5 rounded-lg hover:bg-bg-elevated text-text-disabled hover:text-text-primary transition-colors">
          <ArrowLeft size={16} />
        </Link>
        <div>
          <h1 className="text-lg font-semibold text-text-primary">Nova vaga</h1>
          <p className="text-sm text-text-secondary">Preencha os detalhes da posição</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-5">
        <div className="space-y-1.5">
          <label className="text-xs font-medium text-text-secondary">Título da vaga</label>
          <input {...f("title", "Ex: Desenvolvedor Full Stack Sênior")} />
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-medium text-text-secondary">Descrição</label>
          <textarea {...f("description", "Descreva a vaga, requisitos e diferenciais...")}
            className="w-full min-h-[120px] rounded-lg bg-bg-elevated px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled border border-border focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent resize-y" />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-medium text-text-secondary">Departamento</label>
            <input {...f("department", "Ex: Tecnologia")} />
          </div>
          <div className="space-y-1.5">
            <label className="text-xs font-medium text-text-secondary">Localização</label>
            <input {...f("location", "Ex: Remoto, São Paulo")} />
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-medium text-text-secondary">Tipo</label>
          <select {...f("type")} className="w-full h-9 rounded-lg bg-bg-elevated px-3 text-sm text-text-primary border border-border focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent">
            <option value="FULL_TIME">Tempo integral</option>
            <option value="PART_TIME">Meio período</option>
            <option value="CONTRACT">Contrato</option>
            <option value="INTERNSHIP">Estágio</option>
            <option value="REMOTE">Remoto</option>
          </select>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-medium text-text-secondary">Salário mín.</label>
            <input type="number" {...f("salaryMin", "5000")} />
          </div>
          <div className="space-y-1.5">
            <label className="text-xs font-medium text-text-secondary">Salário máx.</label>
            <input type="number" {...f("salaryMax", "15000")} />
          </div>
          <div className="space-y-1.5">
            <label className="text-xs font-medium text-text-secondary">Moeda</label>
            <select {...f("currency")} className="w-full h-9 rounded-lg bg-bg-elevated px-3 text-sm text-text-primary border border-border focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent">
              <option value="BRL">BRL</option>
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
            </select>
          </div>
        </div>

        <div className="flex gap-3 pt-2">
          <button type="submit" disabled={loading}
            className="inline-flex items-center gap-2 h-9 px-5 rounded-lg bg-primary hover:bg-primary-hover text-text-primary text-sm font-medium transition-colors disabled:opacity-50">
            <Sparkles size={14} /> {loading ? "Publicando..." : "Publicar vaga"}
          </button>
          <Link href="/jobs">
            <button type="button" className="h-9 px-4 rounded-lg bg-bg-elevated hover:bg-bg-elevated-2 border border-border text-text-secondary text-sm transition-colors">
              Cancelar
            </button>
          </Link>
        </div>
      </form>
    </div>
  );
}

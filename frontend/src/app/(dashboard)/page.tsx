"use client";

import { useQuery } from "@tanstack/react-query";
import { motion } from "motion/react";
import { api } from "@/lib/api";
import { Briefcase, Users, Calendar, TrendingUp, Sparkles, Plus, ArrowRight } from "lucide-react";
import Link from "next/link";

interface DashboardData {
  activeJobs: number;
  totalCandidates: number;
  interviewsScheduled: number;
  hiresThisMonth: number;
  avgMatchScore: number;
  topCandidates: { id: string; name: string; email: string; score: number }[];
  pipelineHealth: Record<string, number>;
}

const stats = [
  { key: "activeJobs", label: "Vagas ativas", icon: Briefcase },
  { key: "totalCandidates", label: "Candidatos", icon: Users },
  { key: "interviewsScheduled", label: "Entrevistas", icon: Calendar },
  { key: "hiresThisMonth", label: "Contratações (mês)", icon: TrendingUp },
] as const;

export default function DashboardPage() {
  const { data, isLoading } = useQuery<DashboardData>({
    queryKey: ["dashboard"],
    queryFn: async () => {
      const { data: res } = await api.get("/api/v1/jobs/dashboard");
      return res.data;
    },
  });

  return (
    <div className="space-y-8 p-4 lg:p-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
        <div>
          <h1 className="text-lg font-semibold text-text-primary">Dashboard</h1>
          <p className="mt-1 text-sm text-text-secondary">Visão geral do seu processo seletivo</p>
        </div>
        <Link href="/jobs/new">
          <button className="inline-flex items-center gap-1.5 h-9 px-4 rounded-lg bg-primary hover:bg-primary-hover text-text-primary text-sm font-medium transition-colors">
            <Plus size={14} /> Nova vaga
          </button>
        </Link>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-5">
        {stats.map(({ key, label, icon: Icon }, i) => (
          <motion.div
            key={key}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.05, duration: 0.25 }}
          >
            <div className="rounded-xl bg-bg-elevated border border-border p-4 hover:bg-bg-elevated-2 transition-colors">
              <div className="flex items-start justify-between">
                <div className="rounded-lg bg-primary/10 p-1.5">
                  <Icon size={14} className="text-accent" />
                </div>
              </div>
              <div className="mt-3">
                {isLoading ? (
                  <div className="h-7 w-16 animate-pulse rounded bg-bg-elevated-2" />
                ) : (
                  <p className="text-xl font-semibold text-text-primary font-mono tabular-nums">
                    {data?.[key] ?? 0}
                  </p>
                )}
                <p className="mt-0.5 text-xs text-text-secondary">{label}</p>
              </div>
            </div>
          </motion.div>
        ))}

        {/* V2: AI Score card */}
        <motion.div
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.25, duration: 0.25 }}
        >
          <div className="rounded-xl bg-bg-elevated border border-accent/20 p-4 bg-accent/5">
            <div className="flex items-start justify-between">
              <div className="rounded-lg bg-accent/20 p-1.5">
                <Sparkles size={14} className="text-accent" />
              </div>
            </div>
            <div className="mt-3">
              {isLoading ? (
                <div className="h-7 w-16 animate-pulse rounded bg-bg-elevated-2" />
              ) : (
                <p className="text-xl font-semibold text-accent font-mono tabular-nums">
                  {data?.avgMatchScore != null ? Math.round(data.avgMatchScore * 100) : 0}%
                </p>
              )}
              <p className="mt-0.5 text-xs text-text-secondary">Score médio IA</p>
            </div>
          </div>
        </motion.div>
      </div>

      {/* AI Recommendations */}
      {data?.topCandidates && data.topCandidates.length > 0 && (
        <section>
          <h2 className="text-sm font-medium text-text-secondary mb-3 flex items-center gap-1.5">
            <Sparkles size={13} className="text-accent" /> Top candidatos
          </h2>
          <div className="space-y-1.5">
            {data.topCandidates.slice(0, 3).map((c, i) => (
              <Link key={c.id} href={`/candidates/${c.id}`}>
                <div className="rounded-lg bg-bg-elevated border border-border p-3 hover:bg-bg-elevated-2 transition-colors flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <div className="flex h-7 w-7 items-center justify-center rounded-full bg-primary/10 text-accent text-xs font-medium">
                      {c.name[0]}
                    </div>
                    <div>
                      <p className="text-sm text-text-primary">{c.name}</p>
                      <p className="text-xs text-text-disabled">{c.email}</p>
                    </div>
                  </div>
                  <span className="text-sm font-semibold text-accent font-mono">
                    {Math.round((c.score ?? 0) * 100)}%
                  </span>
                </div>
              </Link>
            ))}
          </div>
        </section>
      )}

      {/* Quick Actions */}
      <section>
        <h2 className="text-sm font-medium text-text-secondary mb-3">Ações rápidas</h2>
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
          {[
            { href: "/jobs", label: "Ver vagas", desc: "Gerencie suas vagas ativas" },
            { href: "/jobs/new", label: "Publicar vaga", desc: "Crie uma nova vaga" },
            { href: "/company", label: "Perfil da empresa", desc: "Atualize dados e branding" },
          ].map((item, i) => (
            <motion.div
              key={item.href}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 + i * 0.05, duration: 0.25 }}
            >
              <Link href={item.href}>
                <div className="rounded-xl bg-bg-elevated border border-border p-4 hover:bg-bg-elevated-2 transition-colors group h-full">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm font-medium text-text-primary">{item.label}</p>
                      <p className="mt-0.5 text-xs text-text-secondary">{item.desc}</p>
                    </div>
                    <ArrowRight size={14} className="text-text-disabled group-hover:text-accent transition-colors shrink-0" />
                  </div>
                </div>
              </Link>
            </motion.div>
          ))}
        </div>
      </section>
    </div>
  );
}

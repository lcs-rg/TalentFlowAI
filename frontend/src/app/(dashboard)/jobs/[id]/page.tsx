"use client";

import { useParams, useRouter } from "next/navigation";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { motion } from "motion/react";
import Link from "next/link";
import { api } from "@/lib/api";
import { Button, Card, Badge, Skeleton } from "@/components/ui/primitives";
import {
  ArrowLeft, MapPin, Clock, Briefcase, Plus, GripVertical, DollarSign,
  Sparkles, Trash2,
} from "lucide-react";
import { useState } from "react";
import toast from "react-hot-toast";

interface JobResponse {
  id: string; title: string; description: string; department: string;
  location: string; type: string; salaryMin: number; salaryMax: number;
  currency: string; status: string; candidateCount: number;
  createdAt: string; publishedAt: string;
}

interface StageResponse {
  id: string; name: string; orderIndex: number; type: string; color: string;
}

interface ApplicationResponse {
  id: string; jobId: string; candidateId: string; stageId: string;
  status: string; score: number; candidateName: string; candidateEmail: string;
  stageName: string;
}

const TYPE_LABELS: Record<string, string> = {
  FULL_TIME: "Full-time", PART_TIME: "Part-time", CONTRACT: "Contrato",
  INTERNSHIP: "Estágio", REMOTE: "Remoto",
};

export default function JobDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const queryClient = useQueryClient();
  const [showAddCandidate, setShowAddCandidate] = useState(false);
  const [newCandidate, setNewCandidate] = useState({ name: "", email: "", phone: "" });

  const { data: job, isLoading: jobLoading } = useQuery({
    queryKey: ["job", id],
    queryFn: async () => {
      const { data: res } = await api.get(`/api/v1/jobs/${id}`);
      return res.data as JobResponse;
    },
  });

  const { data: stages, isLoading: stagesLoading } = useQuery({
    queryKey: ["pipeline", id],
    queryFn: async () => {
      const { data: res } = await api.get(`/api/v1/jobs/${id}/pipeline`);
      return res.data as StageResponse[];
    },
  });

  const { data: applications, isLoading: appsLoading } = useQuery({
    queryKey: ["applications", id],
    queryFn: async () => {
      const { data: res } = await api.get(`/api/v1/jobs/${id}/applications?size=100`);
      return res.data as ApplicationResponse[];
    },
  });

  const moveMutation = useMutation({
    mutationFn: async ({ applicationId, stageId }: { applicationId: string; stageId: string }) => {
      await api.patch(`/api/v1/applications/${applicationId}/move`, { stageId });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["applications", id] });
    },
  });

  const addCandidateMutation = useMutation({
    mutationFn: async () => {
      await api.post(`/api/v1/jobs/${id}/applications`, newCandidate);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["applications", id] });
      queryClient.invalidateQueries({ queryKey: ["job", id] });
      setShowAddCandidate(false);
      setNewCandidate({ name: "", email: "", phone: "" });
      toast.success("Candidato adicionado!");
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.error?.message || "Erro ao adicionar");
    },
  });

  const aiScreeningMutation = useMutation({
    mutationFn: async () => {
      const { data: res } = await api.post(`/api/v1/ai/screening/${id}`);
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["applications", id] });
      toast.success("Triagem IA concluída!");
    },
    onError: () => {
      toast.error("Erro na triagem IA");
    },
  });

  const publishMutation = useMutation({
    mutationFn: async () => {
      await api.patch(`/api/v1/jobs/${id}/publish`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["job", id] });
      toast.success("Vaga publicada!");
    },
  });

  const loading = jobLoading || stagesLoading || appsLoading;

  if (loading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-5 w-32" />
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-4 w-96" />
      </div>
    );
  }

  if (!job) return null;

  const appsByStage: Record<string, ApplicationResponse[]> = {};
  stages?.forEach((s) => { appsByStage[s.id] = []; });
  applications?.forEach((a) => {
    const sid = a.stageId;
    if (sid && appsByStage[sid]) {
      appsByStage[sid].push(a);
    }
  });

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="space-y-2">
        <Link href="/jobs" className="inline-flex items-center gap-1.5 text-xs text-tertiary hover:text-secondary transition-colors">
          <ArrowLeft size={12} /> Vagas
        </Link>
        <div className="flex items-start justify-between">
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-lg font-semibold tracking-tight text-primary">{job.title}</h1>
              <Badge variant={job.status === "PUBLISHED" ? "success" : "default"}>
                {job.status === "DRAFT" ? "Rascunho" : job.status === "PUBLISHED" ? "Publicada" : "Fechada"}
              </Badge>
            </div>
            <div className="mt-1.5 flex items-center gap-3 text-xs text-tertiary">
              {job.department && <span className="flex items-center gap-1"><Briefcase size={11} /> {job.department}</span>}
              {job.location && <span className="flex items-center gap-1"><MapPin size={11} /> {job.location}</span>}
              <span className="flex items-center gap-1"><Clock size={11} /> {TYPE_LABELS[job.type]}</span>
              {job.salaryMin && <span className="flex items-center gap-1"><DollarSign size={11} /> {job.currency} {job.salaryMin?.toLocaleString()} - {job.salaryMax?.toLocaleString()}</span>}
            </div>
          </div>
          <div className="flex items-center gap-2">
            {job.status === "DRAFT" && (
              <Button size="sm" onClick={() => publishMutation.mutate()} loading={publishMutation.isPending}>
                Publicar
              </Button>
            )}
            <Button
              variant="ghost"
              size="sm"
              leftIcon={<Sparkles size={12} />}
              onClick={() => aiScreeningMutation.mutate()}
              loading={aiScreeningMutation.isPending}
            >
              Triagem IA
            </Button>
          </div>
        </div>
      </div>

      {/* Pipeline Kanban */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-sm font-medium text-secondary">
            Pipeline · {applications?.length ?? 0} candidatos
          </h2>
        </div>

        <div className="flex gap-3 overflow-x-auto pb-4">
          {stages?.map((stage) => (
            <div key={stage.id} className="flex-shrink-0 w-64">
              <div className="flex items-center justify-between mb-2 px-1">
                <div className="flex items-center gap-2">
                  <div className="w-2 h-2 rounded-full" style={{ backgroundColor: stage.color || "#6B7280" }} />
                  <span className="text-xs font-medium text-secondary">{stage.name}</span>
                </div>
                <span className="text-xs text-tertiary tabular-nums">
                  {appsByStage[stage.id]?.length ?? 0}
                </span>
              </div>

              <div className="space-y-1.5 min-h-[100px]">
                {appsByStage[stage.id]?.map((app) => (
                  <motion.div
                    key={app.id}
                    layout
                    transition={{ type: "spring", stiffness: 300, damping: 25 }}
                  >
                    <Card padding="sm" hover className="group cursor-grab active:cursor-grabbing">
                      <div className="flex items-start gap-2">
                        <GripVertical size={12} className="text-tertiary mt-0.5 shrink-0" />
                        <div className="min-w-0 flex-1">
                          <p className="text-[13px] font-medium text-primary truncate">{app.candidateName}</p>
                          <p className="text-[11px] text-tertiary truncate">{app.candidateEmail}</p>
                          {app.score != null && (
                            <div className="mt-1.5 flex items-center gap-1.5">
                              <div className="flex-1 h-1 rounded-full bg-tertiary">
                                <div
                                  className="h-full rounded-full bg-accent-500 transition-all"
                                  style={{ width: `${(app.score ?? 0) * 100}%` }}
                                />
                              </div>
                              <span className="text-[11px] font-medium text-accent-600 dark:text-accent-400 tabular-nums">
                                {Math.round((app.score ?? 0) * 100)}%
                              </span>
                            </div>
                          )}
                        </div>
                        <select
                          value={app.stageId ?? ""}
                          onChange={(e) => {
                            if (e.target.value) moveMutation.mutate({ applicationId: app.id, stageId: e.target.value });
                          }}
                          onClick={(e) => e.stopPropagation()}
                          className="opacity-0 group-hover:opacity-100 transition-opacity text-[10px] bg-tertiary border border-primary rounded px-1 py-0.5"
                        >
                          {stages.map((s) => (
                            <option key={s.id} value={s.id}>{s.name}</option>
                          ))}
                        </select>
                      </div>
                    </Card>
                  </motion.div>
                ))}

                <button
                  onClick={() => setShowAddCandidate(true)}
                  className="w-full rounded-lg border border-dashed border-primary p-2 text-xs text-tertiary hover:text-secondary hover:border-neutral-300 dark:hover:border-neutral-600 transition-all"
                >
                  <Plus size={12} className="inline mr-1" /> Adicionar
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Add Candidate Modal */}
      {showAddCandidate && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 backdrop-blur-sm" onClick={() => setShowAddCandidate(false)}>
          <motion.div
            initial={{ opacity: 0, scale: 0.97 }}
            animate={{ opacity: 1, scale: 1 }}
            className="w-full max-w-sm bg-secondary border border-primary rounded-xl p-5 shadow-overlay"
            onClick={(e) => e.stopPropagation()}
          >
            <h3 className="text-sm font-semibold text-primary mb-4">Adicionar candidato</h3>
            <div className="space-y-3">
              <input
                type="text"
                value={newCandidate.name}
                onChange={(e) => setNewCandidate({ ...newCandidate, name: e.target.value })}
                placeholder="Nome completo"
                className="w-full h-9 rounded-lg bg-tertiary px-3 text-sm text-primary placeholder:text-tertiary border border-primary focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500"
                autoFocus
              />
              <input
                type="email"
                value={newCandidate.email}
                onChange={(e) => setNewCandidate({ ...newCandidate, email: e.target.value })}
                placeholder="Email"
                className="w-full h-9 rounded-lg bg-tertiary px-3 text-sm text-primary placeholder:text-tertiary border border-primary focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500"
              />
              <input
                type="text"
                value={newCandidate.phone}
                onChange={(e) => setNewCandidate({ ...newCandidate, phone: e.target.value })}
                placeholder="Telefone (opcional)"
                className="w-full h-9 rounded-lg bg-tertiary px-3 text-sm text-primary placeholder:text-tertiary border border-primary focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500"
              />
              <div className="flex gap-2 justify-end pt-1">
                <Button variant="ghost" size="sm" onClick={() => setShowAddCandidate(false)}>Cancelar</Button>
                <Button size="sm" onClick={() => addCandidateMutation.mutate()} loading={addCandidateMutation.isPending}>Adicionar</Button>
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
}

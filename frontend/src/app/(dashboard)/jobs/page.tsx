"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { motion } from "motion/react";
import Link from "next/link";
import { api } from "@/lib/api";
import { Button, Card, Badge, Skeleton } from "@/components/ui/primitives";
import { Plus, Search, Briefcase, MapPin, Clock, ChevronRight } from "lucide-react";

interface JobResponse {
  id: string;
  title: string;
  description: string;
  department: string;
  location: string;
  type: string;
  status: string;
  candidateCount: number;
  createdAt: string;
  publishedAt: string;
}

const STATUS_STYLES: Record<string, { label: string; variant: "default" | "success" | "warning" | "accent" }> = {
  DRAFT: { label: "Rascunho", variant: "default" },
  PUBLISHED: { label: "Publicada", variant: "success" },
  CLOSED: { label: "Fechada", variant: "warning" },
  ARCHIVED: { label: "Arquivada", variant: "default" },
};

const TYPE_LABELS: Record<string, string> = {
  FULL_TIME: "Full-time",
  PART_TIME: "Part-time",
  CONTRACT: "Contrato",
  INTERNSHIP: "Estágio",
  REMOTE: "Remoto",
};

export default function JobsPage() {
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  const { data, isLoading } = useQuery({
    queryKey: ["jobs", search, statusFilter],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (search) params.set("search", search);
      if (statusFilter) params.set("status", statusFilter);
      const { data: res } = await api.get(`/api/v1/jobs?${params}`);
      return res.data as JobResponse[];
    },
  });

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-lg font-semibold tracking-tight text-primary">Vagas</h1>
          <p className="mt-1 text-sm text-tertiary">
            {data?.length ?? 0} vagas encontradas
          </p>
        </div>
        <Link href="/jobs/new">
          <Button leftIcon={<Plus size={14} />}>Nova vaga</Button>
        </Link>
      </div>

      {/* Filters */}
      <div className="flex gap-3">
        <div className="relative flex-1 max-w-sm">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-tertiary" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar vagas..."
            className="w-full h-9 rounded-lg bg-tertiary pl-9 pr-3 text-sm text-primary placeholder:text-tertiary border border-primary transition-all duration-150 hover:border-neutral-300 dark:hover:border-neutral-700 focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500"
          />
        </div>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="h-9 rounded-lg bg-tertiary px-3 text-sm text-primary border border-primary transition-all duration-150 hover:border-neutral-300 dark:hover:border-neutral-700 focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500"
        >
          <option value="">Todos status</option>
          <option value="DRAFT">Rascunho</option>
          <option value="PUBLISHED">Publicada</option>
          <option value="CLOSED">Fechada</option>
        </select>
      </div>

      {/* Job List */}
      {isLoading ? (
        <div className="space-y-2">
          {[1, 2, 3].map((i) => (
            <Card key={i} padding="md">
              <Skeleton className="h-5 w-48 mb-2" />
              <Skeleton className="h-3 w-72" />
            </Card>
          ))}
        </div>
      ) : data?.length === 0 ? (
        <Card padding="lg">
          <div className="flex flex-col items-center py-8 text-center">
            <div className="rounded-full bg-tertiary p-2.5 mb-3">
              <Briefcase size={18} className="text-tertiary" />
            </div>
            <p className="text-sm font-medium text-primary">Nenhuma vaga encontrada</p>
            <p className="mt-1 text-xs text-tertiary">
              Crie sua primeira vaga para começar.
            </p>
          </div>
        </Card>
      ) : data && data.length > 0 ? (
        <div className="space-y-2">
          {data.map((job, i) => (
            <motion.div
              key={job.id}
              initial={{ opacity: 0, y: 4 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.03, duration: 0.2 }}
            >
              <Link href={`/jobs/${job.id}`}>
                <Card hover padding="md" className="group">
                  <div className="flex items-start justify-between gap-4">
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2">
                        <h3 className="text-sm font-medium text-primary truncate group-hover:text-accent-500 transition-colors">
                          {job.title}
                        </h3>
                        <Badge variant={STATUS_STYLES[job.status]?.variant ?? "default"}>
                          {STATUS_STYLES[job.status]?.label ?? job.status}
                        </Badge>
                      </div>
                      <div className="mt-1.5 flex items-center gap-3 text-xs text-tertiary">
                        {job.department && (
                          <span className="flex items-center gap-1">
                            <Briefcase size={11} /> {job.department}
                          </span>
                        )}
                        {job.location && (
                          <span className="flex items-center gap-1">
                            <MapPin size={11} /> {job.location}
                          </span>
                        )}
                        <span className="flex items-center gap-1">
                          <Clock size={11} /> {TYPE_LABELS[job.type] ?? job.type}
                        </span>
                      </div>
                    </div>
                    <div className="flex items-center gap-3 shrink-0">
                      <div className="text-right">
                        <p className="text-sm font-semibold text-primary">{job.candidateCount}</p>
                        <p className="text-[11px] text-tertiary">candidatos</p>
                      </div>
                      <ChevronRight size={14} className="text-tertiary group-hover:text-accent-500 transition-colors" />
                    </div>
                  </div>
                </Card>
              </Link>
            </motion.div>
          ))}
        </div>
      ) : null}
    </div>
  );
}

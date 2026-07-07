"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { motion } from "motion/react";
import Link from "next/link";
import { api } from "@/lib/api";
import { Button, Card, Badge, Skeleton, EmptyState } from "@/components/ui/primitives";
import { Plus, Search, User, Mail, Phone, ChevronRight } from "lucide-react";

interface CandidateResponse {
  id: string;
  name: string;
  email: string;
  phone: string;
  resumeUrl: string;
  tags: string[];
  createdAt: string;
  applications: { id: string; jobId: string; status: string; stageId: string }[];
}

export default function CandidatesPage() {
  const [search, setSearch] = useState("");

  const { data, isLoading } = useQuery({
    queryKey: ["candidates", search],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (search) params.set("search", search);
      const { data: res } = await api.get(`/api/v1/candidates?${params}`);
      return res.data as CandidateResponse[];
    },
  });

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-lg font-semibold tracking-tight text-primary">Candidatos</h1>
          <p className="mt-1 text-sm text-tertiary">
            {data?.length ?? 0} candidatos encontrados
          </p>
        </div>
        <Link href="/candidates/new">
          <Button leftIcon={<Plus size={14} />}>Novo candidato</Button>
        </Link>
      </div>

      {/* Search */}
      <div className="flex gap-3">
        <div className="relative flex-1 max-w-sm">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-tertiary" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar candidatos..."
            className="w-full h-9 rounded-lg bg-tertiary pl-9 pr-3 text-sm text-primary placeholder:text-tertiary border border-primary transition-all duration-150 focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500"
          />
        </div>
      </div>

      {/* List */}
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
        <EmptyState
          icon={<User size={18} />}
          title="Nenhum candidato encontrado"
          description="Cadastre seu primeiro candidato para começar."
          action={
            <Link href="/candidates/new">
              <Button size="sm" leftIcon={<Plus size={12} />}>Novo candidato</Button>
            </Link>
          }
        />
      ) : data && data.length > 0 ? (
        <div className="space-y-2">
          {data.map((c, i) => (
            <motion.div
              key={c.id}
              initial={{ opacity: 0, y: 4 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.03, duration: 0.2 }}
            >
              <Link href={`/candidates/${c.id}`}>
                <Card hover padding="md" className="group">
                  <div className="flex items-start justify-between gap-4">
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary/10 text-accent text-xs font-medium shrink-0">
                          {c.name[0]?.toUpperCase()}
                        </div>
                        <div>
                          <h3 className="text-sm font-medium text-primary group-hover:text-accent-500 transition-colors">
                            {c.name}
                          </h3>
                          <div className="flex items-center gap-3 mt-0.5 text-xs text-tertiary">
                            <span className="flex items-center gap-1">
                              <Mail size={11} /> {c.email}
                            </span>
                            {c.phone && (
                              <span className="flex items-center gap-1">
                                <Phone size={11} /> {c.phone}
                              </span>
                            )}
                          </div>
                        </div>
                      </div>
                      {c.tags && c.tags.length > 0 && (
                        <div className="flex gap-1 mt-2 ml-10">
                          {c.tags.map((tag) => (
                            <Badge key={tag} variant="default">{tag}</Badge>
                          ))}
                        </div>
                      )}
                    </div>
                    <div className="flex items-center gap-3 shrink-0">
                      <div className="text-right">
                        <p className="text-sm font-semibold text-primary">{c.applications?.length ?? 0}</p>
                        <p className="text-[11px] text-tertiary">candidaturas</p>
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

"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { Button, Card, Input, Skeleton } from "@/components/ui/primitives";
import { Building2, Save } from "lucide-react";
import { useState, useEffect } from "react";
import toast from "react-hot-toast";

interface CompanyData {
  id: string;
  name: string;
  industry: string;
  size: string;
  website: string;
  logoUrl: string;
}

export default function CompanyPage() {
  const queryClient = useQueryClient();
  const [name, setName] = useState("");
  const [industry, setIndustry] = useState("");
  const [size, setSize] = useState("");
  const [website, setWebsite] = useState("");

  const { data: company, isLoading } = useQuery({
    queryKey: ["company"],
    queryFn: async () => {
      const { data: res } = await api.get("/api/v1/company");
      return res.data as CompanyData;
    },
  });

  useEffect(() => {
    if (company) {
      setName(company.name || "");
      setIndustry(company.industry || "");
      setSize(company.size || "");
      setWebsite(company.website || "");
    }
  }, [company]);

  const updateMutation = useMutation({
    mutationFn: async () => {
      await api.put("/api/v1/company", { name, industry, size, website });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["company"] });
      toast.success("Empresa atualizada!");
    },
    onError: () => toast.error("Erro ao atualizar"),
  });

  if (isLoading) {
    return (
      <div className="max-w-lg space-y-8">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-64 w-full" />
      </div>
    );
  }

  return (
    <div className="max-w-lg space-y-8">
      <div>
        <h1 className="text-lg font-semibold tracking-tight text-primary">Empresa</h1>
        <p className="mt-1 text-sm text-tertiary">Gerencie os dados da sua empresa</p>
      </div>

      <Card padding="lg">
        <div className="space-y-5">
          <Input
            label="Nome da empresa"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Nome da empresa"
          />
          <Input
            label="Segmento"
            value={industry}
            onChange={(e) => setIndustry(e.target.value)}
            placeholder="Ex: Tecnologia, Saúde, Finanças"
          />
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-secondary">Porte</label>
            <select
              value={size}
              onChange={(e) => setSize(e.target.value)}
              className="w-full h-9 rounded-lg bg-tertiary px-3 text-sm text-primary border border-primary focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500"
            >
              <option value="">Selecione...</option>
              <option value="STARTUP">Startup (1-10)</option>
              <option value="SMALL">Pequena (11-50)</option>
              <option value="MEDIUM">Média (51-200)</option>
              <option value="LARGE">Grande (201-1000)</option>
              <option value="ENTERPRISE">Enterprise (1000+)</option>
            </select>
          </div>
          <Input
            label="Site"
            value={website}
            onChange={(e) => setWebsite(e.target.value)}
            placeholder="https://suaempresa.com"
          />
          <div className="pt-2">
            <Button
              leftIcon={<Save size={14} />}
              onClick={() => updateMutation.mutate()}
              loading={updateMutation.isPending}
            >
              Salvar
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}

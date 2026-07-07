# Roadmap do Produto

## Importante

Este projeto será desenvolvido em etapas. Cada etapa representa uma versão do produto.

**NUNCA** implemente funcionalidades de etapas futuras sem autorização explícita.

**NUNCA** antecipe código "porque será útil depois".

**NUNCA** crie tabelas, APIs, componentes ou abstrações para funcionalidades que ainda não fazem parte da etapa atual.

Quando eu desejar iniciar uma nova etapa, avisarei explicitamente. Exemplo: `"Iniciar V2"` ou `"Começar Etapa 3"`.

Somente após essa autorização você poderá desenvolver funcionalidades daquela etapa. Até esse momento, considere que elas não existem.

---

## V1 — MVP

**Objetivo:** Construir um ATS completo, utilizável e comercializável. Esta versão deve ser capaz de substituir planilhas ou processos simples de recrutamento.

### Funcionalidades permitidas

**Empresa**
- Login
- Cadastro
- Empresas
- Usuários
- Equipes
- Dashboard
- CRUD de vagas
- Pipeline Kanban
- Cadastro de candidatos
- Upload de currículos
- Agenda de entrevistas
- Notas

**Candidato**
- Cadastro
- Login
- Perfil profissional
- Currículo
- Upload de PDF
- Aplicação para vagas
- Histórico de candidaturas

**IA**
- Parsing de currículo
- Compatibilidade vaga ↔ candidato
- Sugestões de melhoria do currículo

**Infraestrutura**
- Spring Boot
- Next.js
- Supabase
- PostgreSQL
- Storage
- Python AI Service

### Não implementar nesta versão

Chat IA, Agentes, RAG, Busca vetorial, Recomendações inteligentes, Analytics avançado, Integrações externas, WhatsApp, LinkedIn, Outlook, Gmail, Marketplace, Mobile.

---

## V2

**Objetivo:** Melhorar produtividade do recrutador.

**Funcionalidades:** Busca semântica, Embeddings, pgvector, Recomendações de candidatos, Recomendações de vagas, Pesquisa inteligente, Dashboard IA, Filtros inteligentes.

> Esta etapa NÃO deve ser iniciada sem autorização.

---

## V3

**Objetivo:** Transformar IA em um diferencial competitivo.

**Funcionalidades:** RAG, Agentes especializados, Geração de perguntas para entrevistas, Resumo automático de candidatos, Score avançado, Ranking inteligente, Automações, Sugestões para recrutadores.

> Esta etapa NÃO deve ser iniciada sem autorização.

---

## V4

**Objetivo:** Escalabilidade Enterprise.

**Funcionalidades:** API Pública, Webhooks, Integrações, Microsoft Teams, Slack, Google Workspace, Outlook, SSO, Multi-organização, Auditoria avançada, Billing, Analytics Enterprise.

> Esta etapa NÃO deve ser iniciada sem autorização.

---

## Regra obrigatória

Sempre considere que apenas **UMA** versão está ativa.

Se a versão ativa for a V1:
- Ignore completamente V2, V3 e V4
- Não proponha implementações futuras
- Não crie código para funcionalidades futuras
- Não crie tabelas pensando em funcionalidades futuras
- Não gere endpoints pensando em funcionalidades futuras
- Não faça otimizações prematuras

Caso alguma implementação da etapa atual possa ser influenciada por uma funcionalidade futura, apenas documente essa observação e aguarde autorização para implementá-la.

O objetivo é manter o projeto enxuto, organizado e evolutivo. Cada etapa deve estar completamente funcional antes da próxima começar.

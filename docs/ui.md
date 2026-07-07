# Padrões de Interface

## Organização de Componentes

Componentes de UI seguem a mesma organização por domínio definida no frontend, não uma pasta `components/` genérica:

```
modules/
  job/components/           JobCard, JobPipelineBoard, JobSummaryGlass
  candidate/components/     CandidateAvatar, ResumePreview
  application/components/   ApplicationStageBadge, ApplicationTimeline
  interview/components/     InterviewScheduler
shared/components/            Button, Input, Table, Modal, Badge, EmptyState
```

`shared/components/` contém apenas primitivos verdadeiramente reutilizáveis entre módulos (botão, input, tabela, badge). Qualquer componente com conhecimento de domínio (ex: `JobPipelineBoard`) vive no módulo correspondente, nunca em `shared/`.

## Padrão: Tabela Densa

Usada em listagens (Jobs, Candidates, Applications). Altura de linha compacta (36–40px), fonte `--text-body` para texto e `--text-data` (Plex Mono) para colunas numéricas/data.

Linha em hover usa `--bg-elevated-2`, nunca sombra. Ação em linha (editar, remover) aparece só no hover, mantendo a tabela visualmente limpa em repouso.

Cabeçalho de coluna usa `--text-caption`, sempre com opção de ordenação quando fizer sentido (nunca decorativo).

## Padrão: Kanban de Pipeline

Colunas representam estágios do pipeline do job. Cada card de candidato no kanban é deliberadamente minimalista: avatar, nome, badge de status (`--status-*`), sem indicador de score — a coluna em si já comunica o estágio, repetir a métrica de progresso ali seria redundante.

Drag-and-drop entre colunas dispara a mesma ação que `PATCH /applications/{id}/move`, com feedback otimista na UI e rollback visual em caso de erro.

## Padrão: Formulário

Inputs usam `--radius-sm`, borda `--border`, foco com `--accent`. Validação client-side (React Hook Form + Zod) mostra erro abaixo do campo, cor `--status-danger`, nunca só borda vermelha sem texto (acessibilidade).

## Padrão: Estado Vazio

Este é o lugar certo para a fonte Fraunces aparecer fora do card de assinatura — estado vazio é um convite à ação, não um erro. Título curto em Fraunces (`--text-display-md`), uma frase de apoio em Plex Sans, e a ação primária (ex: "Criar primeira vaga").

Nunca usar linguagem genérica tipo "Nenhum dado encontrado". Escrever no vocabulário do produto: "Nenhuma vaga publicada ainda" + botão "Publicar vaga".

## Voz e Microcopy

Voz ativa, no vocabulário de quem usa (recrutador), nunca termo técnico do sistema. Botão "Publicar vaga", nunca "Submit" ou "Enviar formulário". Toast de confirmação repete o mesmo verbo do botão: "Vaga publicada", não "Sucesso!".

Mensagens de erro nunca se desculpam nem são vagas: "Não foi possível mover o candidato — o estágio de destino não existe mais", nunca "Algo deu errado".

## Componente de Assinatura `<GlassProgressIndicator />`

Vive em `shared/components/`, mas é usado exclusivamente por `job/components/JobSummaryGlass` e `application/components/ApplicationHeader` — não deve ser importado livremente em qualquer tela, para preservar a raridade do elemento.

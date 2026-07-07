# Frontend

## Roteamento

Utilizar **Next.js App Router** (não Pages Router).

Priorizar **React Server Components** para telas de leitura/listagem, reservando Client Components apenas para interatividade real (formulários, filtros dinâmicos, mutations).

## Organização de Código

- Organizar o frontend **por domínio**, espelhando os módulos do backend (job, candidate, interview, application, etc.). Nunca por tipo genérico de arquivo.
- Cada módulo deve conter seus próprios componentes, hooks, chamadas de API e types.
- Rotas em `app/` devem ser organizadas com **route groups** (ex: `(auth)`, `(dashboard)`), mantendo a estrutura de página fina e delegando lógica para `modules/`.

## Gerenciamento de Estado

- Utilizar **TanStack Query** para todo estado que representa dados do servidor (listagens, detalhes, mutations), aproveitando cache, revalidação e tratamento de loading/error nativos.
- Utilizar **Zustand** apenas para estado de UI puramente local (modais, sidebars, wizards). Nunca para dados de servidor.
- Não utilizar Redux.

## UI e Estilização

- Utilizar **Tailwind CSS** como base de estilização.
- Utilizar **shadcn/ui** como biblioteca de componentes, customizando conforme identidade visual do produto.
- Manter os componentes shadcn/ui copiados localmente (padrão da própria lib), versionados no repositório, permitindo customização direta sem depender de pacote externo fechado.

## Autenticação no Client

- O token de autenticação deve ser mantido em **cookie httpOnly**, nunca em localStorage ou sessionStorage.
- Utilizar **middleware do Next.js** para proteger rotas autenticadas, redirecionando para login quando o token estiver ausente ou inválido.
- Nenhuma decisão de autorização (o que o usuário pode ou não fazer) deve ser tratada como fonte de verdade no client. O client pode ocultar elementos de UI por UX, mas o backend deve sempre revalidar a permissão.

## Formulários e Validação

- Utilizar **React Hook Form** para gerenciamento de formulários.
- Utilizar **Zod** para validação de schema no client.
- A validação no client serve apenas para UX/feedback imediato. Toda validação crítica deve ser revalidada no backend, sem exceção.

## Comunicação com API e Tratamento de Erro

- Criar um **client HTTP único** (wrapper sobre fetch) responsável por consumir o formato padrão de resposta do backend (`{data, meta}` para sucesso, objeto com `code` para erro).
- Mapear códigos de erro retornados pelo backend (`code`) para mensagens amigáveis no client, evitando tratamento de erro duplicado em cada chamada.
- Cada rota/módulo deve implementar **`loading.tsx`** e **`error.tsx`** próprios, evitando estados de carregamento genéricos ou telas em branco durante navegação.

## Ambiente e Segredos

- Nenhuma chave de serviço (Supabase service role, chaves de IA, etc.) deve ser exposta ao client. Operações que exigem essas chaves devem passar por **Server Actions** ou rotas de API no próprio Next.js atuando como proxy.
- Variáveis expostas ao client (`NEXT_PUBLIC_*`) devem ser tratadas como públicas por definição. Nunca colocar segredo nelas.

# Segurança

## Autenticação

Utilizar **JWT stateless** para access token e **refresh token opaco** (não-JWT) persistido no banco.

- O access token deve ter vida curta (ex: 15 minutos) e não deve ser consultado no banco a cada requisição.
- O refresh token deve ser armazenado no banco associado a `user_id` e `device_id`/`session_id`, permitindo:
  - Logout de um dispositivo específico (deletar o registro correspondente)
  - Logout global (deletar todos os registros do usuário)
  - Revogação imediata em caso de comprometimento de conta
- Ao usar um refresh token, gerar um novo par de tokens (**rotação**). O refresh token anterior deve ser invalidado no momento do uso.
- Se um refresh token já utilizado for reapresentado (**reuse detection**), tratar como possível comprometimento e revogar todas as sessões daquele usuário.
- Nunca armazenar o refresh token em localStorage no frontend. Utilizar **cookie httpOnly + secure + sameSite**.

## Autorização (RBAC)

Implementar RBAC com **permissions granulares** desde a V1, não apenas roles fixas.

Modelo: `role` agrupa um conjunto de `permissions` (ex: `job:create`, `job:publish`, `candidate:view`, `candidate:delete`, `interview:schedule`).

- Toda checagem de autorização deve ocorrer no backend. Nunca confiar em flags vindas do frontend.
- As permissions devem ser carregadas no momento da autenticação e incluídas como **claims no access token**, evitando consulta ao banco a cada requisição.
- Alterações de permissão de um usuário só devem ter efeito no próximo refresh token (ou forçar refresh imediato quando crítico).

## Multi Tenant

- Todo dado deve ser isolado por `company_id`.
- Toda query, sem exceção, deve filtrar por `company_id` no contexto da requisição autenticada. **Nunca** aceitar `company_id` vindo do frontend/payload.
- O `company_id` deve ser extraído do token autenticado, nunca de parâmetro de rota ou body.

## API Security

- Implementar **rate limiting** em duas camadas:
  - Infraestrutura (reverse proxy/API Gateway) para proteção genérica contra brute-force e abuso de tráfego
  - Aplicação (Spring Boot) para regras de negócio por tenant/plano (ex: limite de análises de IA por dia conforme plano contratado)
- Aplicar **Helmet** (ou equivalente) para headers de segurança HTTP.
- Definir **Content Security Policy (CSP)** restritiva.
- Configurar **CORS** explicitamente por ambiente. Nunca usar wildcard (`*`) em produção.
- Definir limite de tamanho de request para todos os endpoints, especialmente upload.
- CSRF não é aplicável às rotas de API (autenticação via Bearer token, não cookie de sessão). Validar se algum fluxo futuro passar a depender de cookie de sessão.
- Utilizar **Idempotency Keys** em operações críticas não-idempotentes (ex: criação de cobrança, disparo de convite).

## Banco de Dados

- Nunca construir queries via concatenação de string. Utilizar sempre ORM/prepared statements parametrizados.
- Aplicar privilégio mínimo na conexão do banco usada pela aplicação (sem permissões de DDL em runtime).
- Toda tabela sensível deve possuir auditoria (quem alterou, quando, o quê).
- A avaliação de Row Level Security (RLS) como camada adicional de isolamento multi-tenant fica para etapa futura.

## Upload

- Currículos e documentos devem ser armazenados fora da aplicação (**Supabase Storage**), nunca em disco local do servidor de aplicação.
- Validar o tipo de arquivo pelo conteúdo real (**magic bytes/MIME real**), nunca confiar apenas na extensão enviada pelo cliente.
- Definir limite máximo de tamanho de arquivo.
- Gerar nomes de arquivo aleatórios (**UUID**) no armazenamento, nunca usar o nome original do arquivo enviado.
- Nunca expor a URL real de storage publicamente. Utilizar **URLs assinadas com expiração**.

## Segurança de IA

- Tratar todo conteúdo extraído de currículos e todo input de usuário como **não-confiável** antes de incluir em prompts (mitigação de prompt injection).
- Nunca permitir que a IA execute ações de negócio diretamente a partir de instruções contidas em documentos processados.
- Garantir **isolamento de contexto entre tenants** em qualquer chamada ao AI Service. Nunca misturar dados de tenants diferentes na mesma requisição/contexto de IA.
- Sanitizar e limitar o contexto enviado ao modelo, evitando vazamento de dados sensíveis desnecessários no prompt.
- Preparar a arquitetura para, na V3 (RAG), aplicar controle de acesso também na camada de recuperação de contexto (retrieval), não apenas na geração.

## Infraestrutura

- Toda credencial e chave sensível deve ser armazenada como secret/variável de ambiente. Nunca commitada no repositório.
- Toda comunicação entre serviços (Frontend, Spring Boot, AI Service, Banco) deve utilizar **HTTPS/TLS**, inclusive internamente quando aplicável.
- Logs não devem conter dados sensíveis (senhas, tokens, currículos completos, PII desnecessária).
- Nenhuma resposta de erro para o cliente deve conter stack trace ou detalhes internos de implementação.
- Implementar monitoramento básico e health checks desde a V1.
- Definir rotina de backup do banco de dados desde o primeiro ambiente de produção.

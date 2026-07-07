# Observabilidade e CI/CD

## Logging

- Todo serviço (Next.js, Spring Boot, AI Service) deve emitir logs estruturados em **JSON**. Nunca texto livre.
- Todo log deve incluir, no mínimo: `timestamp`, `level`, `service`, `tenant_id` (quando aplicável), `request_id`.
- Nunca registrar em log: senhas, tokens, refresh tokens, conteúdo completo de currículo, ou qualquer PII além do estritamente necessário para debug.

## Correlação entre Serviços

- Gerar um `request_id` (ou `trace_id`) na borda (Next.js/API Gateway) e propagá-lo em todas as chamadas subsequentes entre Frontend → Spring Boot → AI Service.
- Utilizar **OpenTelemetry** como padrão de instrumentação de tracing em todos os serviços, evitando dependência de SDK proprietário de APM.

## Métricas e Dashboards

- Utilizar **Grafana Cloud** (free tier) como plataforma de observabilidade na V1, cobrindo logs, métricas e tracing.
- Cada serviço deve expor métricas básicas: taxa de erro, latência (p50/p95/p99), throughput de requisições.
- O AI Service deve expor métricas específicas: tempo de resposta por modelo, tokens consumidos, taxa de erro por provider.

## Health Checks

- Todo serviço deve expor endpoint de health check (`/health` ou `/actuator/health`) cobrindo status próprio e de dependências críticas (banco, AI Service, storage).
- O health check não deve expor detalhes internos sensíveis (versão de biblioteca, stack trace, configuração).

## Alertas

- Configurar alertas básicos desde a V1 para: taxa de erro acima do normal, latência elevada, health check falhando.
- Não implementar alertas granulares demais na V1 (evitar fadiga de alerta). Expandir conforme o produto ganha tráfego real.

## CI (Integração Contínua)

- Utilizar **GitHub Actions** como plataforma de CI/CD.
- Todo pull request deve rodar automaticamente: **lint**, **testes automatizados**, **build**.
- Aplicar análise estática de segurança (**SAST**) via **Semgrep** no pipeline, alinhado ao OWASP Top 10/ASVS.
- Aplicar scan de vulnerabilidades em imagens Docker (ex: **Trivy**) antes de qualquer deploy.
- Nenhum PR deve ser mesclado com pipeline falhando, sem exceção.

## CD (Entrega Contínua)

- Deploy automatizado para ambiente de staging a cada merge na branch principal.
- Deploy para produção deve ser um passo deliberado (aprovação manual ou tag de release). Não automático a partir de staging.
- Migrations de banco (Flyway) devem rodar como etapa do pipeline de deploy. Nunca manualmente em produção.
- Rollback de deploy deve ser um processo documentado e testado. Não improvisado quando um incidente ocorrer.

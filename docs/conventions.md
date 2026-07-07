# Convenções de Código

## Regras Transversais (todos os serviços)

- Aplicar **YAGNI**: não implementar abstração, configuração ou flexibilidade para um cenário que ainda não existe. Resolver o problema real de hoje, não o hipotético de amanhã.
- Nunca criar interface para uma classe que possui apenas uma implementação. Interface só se justifica havendo necessidade concreta: múltiplas implementações reais, desacoplamento entre módulos, ou integração externa.
- Organização de código sempre **por domínio** (módulo de negócio), nunca por camada técnica global. Nenhum novo código deve ser adicionado em uma pasta `controllers/`, `services/` ou `repositories/` na raiz do projeto — cada módulo é dono da sua própria estrutura interna.
- Toda nova funcionalidade deve ser implementada dentro do módulo correspondente ao seu domínio, mesmo que isso signifique criar um módulo novo.
- Nomes de variáveis, funções e classes devem refletir o domínio de negócio (ex: `publishJob`, não `updateStatus`), evitando nomes genéricos que escondem intenção.

## Backend (Java / Spring Boot)

- Seguir convenções padrão Java: `PascalCase` para classes, `camelCase` para métodos e variáveis, `UPPER_SNAKE_CASE` para constantes.
- Não sufixar classes com `Impl` quando houver apenas uma implementação (ex: `UserService`, nunca `UserServiceImpl` + `UserService` interface, salvo os casos já descritos na regra transversal de interfaces).
- Toda exceção de negócio deve herdar de uma das exceções já definidas: `BusinessException`, `ValidationException`, `ForbiddenException`, `NotFoundException`, `ConflictException`. Nunca lançar `RuntimeException` diretamente.
- DTOs de entrada devem ser sufixados com `Request`, DTOs de saída com `Response` (ex: `CreateJobRequest`, `JobResponse`).

## AI Service (Python)

- Seguir **PEP 8**. Usar `snake_case` para funções e variáveis, `PascalCase` para classes.
- Usar **type hints** em toda função pública.
- Usar **Pydantic** para todo schema de entrada/saída de API.

## Frontend (TypeScript / Next.js)

- Seguir `camelCase` para funções e variáveis, `PascalCase` para componentes e tipos.
- Componentes React devem ter o mesmo nome do arquivo que os contém.
- Tipos e interfaces TypeScript não devem ser prefixados com `I` (ex: `Job`, nunca `IJob`).
- Nenhum `any` implícito ou explícito sem justificativa comentada no código.

## Commits e Versionamento

Seguir **Conventional Commits**: `feat:`, `fix:`, `chore:`, `refactor:`, `docs:`, `test:`.

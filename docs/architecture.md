# Arquitetura Backend

## Estilo

O backend será um **Monólito Modular**.

## Organização

Organizar o código **por domínio**. Nunca por camada global.

Cada módulo deve conter apenas o código relacionado ao seu contexto.

## DDD

Utilizar princípios de DDD de forma **pragmática**.

- Não criar abstrações desnecessárias.
- Não criar interfaces quando existir apenas uma implementação.

## Responsabilidades

- Toda regra de negócio deve permanecer no **Core API** (Spring Boot).
- Toda IA deve permanecer no **AI Service** (Python).

## Comunicação

- Nunca retornar entidades diretamente. Sempre utilizar **DTOs**.
- Utilizar **eventos internos** para comunicação entre módulos.

## Padrão de Resposta da API

Toda API retorna o mesmo envelope, tanto em sucesso quanto em erro (`ApiResponse<T>`), com um campo `success: boolean` indicando o caso.

Sucesso:
```json
{
  "success": true,
  "data": {},
  "meta": {},
  "error": null,
  "timestamp": "..."
}
```

Erro:
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 404,
    "code": "CANDIDATE_NOT_FOUND",
    "message": "Candidate not found",
    "errors": []
  },
  "timestamp": "..."
}
```

O campo `error.code` é obrigatório em toda resposta de erro — é ele que o frontend usa para mapear mensagens amigáveis. `error.message` é auxiliar/debug, nunca fonte de verdade para lógica de UI.

A especificação completa e a lista de endpoints vivem em `api.md`, que é a fonte de verdade operacional deste princípio.

## Exceções

- Toda exceção deve possuir tipo específico.
- Toda resposta HTTP deve seguir um padrão único.

## Entidades

- As entidades podem conter comportamento relacionado ao próprio domínio.
- Nunca integrações externas.

## Funcionalidades

Toda nova funcionalidade deve ser implementada dentro do módulo correspondente.

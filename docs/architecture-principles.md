# Architecture Principles

## Prioridades

1. Segurança
2. Manutenibilidade
3. Escalabilidade
4. Performance
5. Experiência do usuário

## Conflitos

- Caso exista conflito entre performance e segurança, **priorize segurança**.
- Caso exista conflito entre rapidez de implementação e segurança, **priorize segurança**.
- Nunca implemente soluções inseguras apenas para acelerar o desenvolvimento.

## Segurança

- Todo código deve seguir **OWASP ASVS** e **OWASP Top 10** como referência.
- Sempre considerar que o sistema armazenará dados sensíveis de empresas e candidatos.
- Toda decisão arquitetural deve considerar ataques reais que um SaaS sofre na internet.

## Produção

O projeto deve ser desenvolvido pensando em ambiente de produção desde o primeiro commit.

## Zero Trust

- Nunca assumir que usuários ou clientes são confiáveis.
- Todo input deve ser tratado como potencialmente malicioso.
- Nunca confiar em dados enviados pelo frontend.

## Validação, Autenticação, Autorização

- Toda validação crítica deve acontecer no backend.
- Toda autenticação deve ser feita no backend.
- Toda autorização deve ser feita no backend.

## Comunicação

Toda comunicação deve utilizar HTTPS.

## Logs e Erros

- Nunca expor informações sensíveis em logs.
- Nunca retornar stack traces para clientes.
- Toda exceção deve possuir tratamento apropriado.

## Dependências

- Toda dependência adicionada deve possuir justificativa técnica.
- Sempre utilizar versões estáveis e atualizadas das bibliotecas.
- Sempre preferir bibliotecas consolidadas ao invés de implementações próprias.

## Regra Geral

Sempre pensar na segurança antes de escrever código.

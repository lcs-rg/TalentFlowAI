# AI Architecture

## Serviço Independente

A Inteligência Artificial deve ser implementada como um **serviço independente em Python**.

## Responsabilidades

- O **Spring Boot** continua sendo o responsável por todo o domínio e pelas regras de negócio.
- O **AI Service** é responsável apenas por processamento inteligente.

## Acesso a Dados

Nunca acessar o banco de dados diretamente a partir do AI Service. Toda comunicação deve ocorrer através da API do backend.

## Comunicação

- Utilizar **REST** na V1.
- Projetar pontos de extensão para filas no futuro, sem implementá-las prematuramente.

## Prompts

- Os prompts devem ser armazenados em **arquivos versionados**.
- Nunca embutir prompts diretamente no código.

## Provider

Criar uma abstração de `AIProvider` para evitar acoplamento com um fornecedor específico.

## Auditoria

Toda resposta da IA deve conter metadados suficientes para auditoria técnica:

- Modelo
- Versão do prompt
- Duração
- Uso de tokens (quando disponível)
- Identificador da requisição

## Limite de Atuação

A IA nunca executa ações de negócio automaticamente. Ela apenas analisa, resume, classifica e sugere.

# Design System (Tema Vinho Premium)

## Direção

Personalidade: **acolhedor refinado**. A densidade de informação é alta (ferramenta de trabalho diário), mas a temperatura de cor e a tipografia carregam o calor — nunca cinza neutro, nunca preto/branco puro.

## Cores

### Base

| Token | Hex | Uso |
|---|---|---|
| `--bg` | `#120C10` | Fundo da aplicação |
| `--bg-elevated` | `#22161B` | Cards, painéis |
| `--bg-elevated-2` | `#2C1D24` | Hover de card, modais, superfícies aninhadas |
| `--border` | `#3A2530` | Divisores, bordas de input |
| `--border-strong` | `#4A2E3B` | Bordas em foco/hover |

### Marca

| Token | Hex | Uso |
|---|---|---|
| `--primary` | `#7A284B` | Botão primário, ação principal, links |
| `--primary-hover` | `#93315C` | Hover/active de elementos primários |
| `--accent` | `#D58DAA` | Destaque, foco, badges de atenção, elemento de assinatura |

### Texto

| Token | Hex | Uso |
|---|---|---|
| `--text-primary` | `#F7F3F4` | Texto principal |
| `--text-secondary` | `#B79AA3` | Texto secundário, labels, metadados |
| `--text-disabled` | `#6B565D` | Texto desabilitado |

### Status (família "adega" — nunca usar vermelho/verde/azul genérico)

| Token | Hex | Significado |
|---|---|---|
| `--status-success` (dourado) | `#C9A227` | Aprovado, contratado, publicado |
| `--status-info` (ameixa) | `#8C7AA6` | Em análise, informativo |
| `--status-warning` (âmbar) | `#D98F4E` | Pendente, aguardando ação |
| `--status-danger` (tijolo) | `#B3413F` | Rejeitado, erro, ação destrutiva |

Nunca usar `--primary` (vinho) para indicar erro/perigo — a proximidade tonal com vermelho de erro gera ambiguidade. `--status-danger` (tijolo) é deliberadamente mais alaranjado para se distinguir do vinho a distância.

## Tipografia

| Papel | Fonte | Uso |
|---|---|---|
| Display | **Fraunces** (peso 480–600, itálico só para ênfase pontual) | H1 de páginas de destino, estados vazios, telas de onboarding. Uso raro e deliberado — nunca em tabela ou componente denso. |
| UI/Corpo | **IBM Plex Sans** | Toda interface: navegação, formulários, botões, headers de seção dentro do dashboard. |
| Dados | **IBM Plex Mono** (tabular figures) | Números, datas, contadores, IDs em tabelas — garante alinhamento vertical perfeito em telas densas. |

### Escala (otimizada para densidade)

| Token | Tamanho/Altura | Fonte |
|---|---|---|
| `--text-display-lg` | 40/44 | Fraunces |
| `--text-display-md` | 28/34 | Fraunces |
| `--text-heading` | 18/24, semibold | Plex Sans |
| `--text-body` | 13/18, regular | Plex Sans |
| `--text-caption` | 11/16, medium, +2% tracking | Plex Sans |
| `--text-data` | 12/16, tabular-nums | Plex Mono |

## Espaçamento

Unidade base 4px: `4 — 8 — 12 — 16 — 24 — 32 — 48`. Telas densas (tabelas, kanban) usam os incrementos menores (4/8/12) como padrão; espaçamento generoso (24/32/48) reservado para separar seções, não itens dentro de uma lista.

## Raio de Borda

| Token | Valor | Uso |
|---|---|---|
| `--radius-sm` | 6px | Inputs, botões, tags retangulares |
| `--radius-md` | 10px | Cards |
| `--radius-lg` | 14px | Modais, painéis flutuantes |
| `--radius-pill` | 999px | Exceção deliberada: badges de status e avatares apenas |

## Elevação

Em tema escuro, evitar sombra pesada. Usar borda sutil (`--border`) + leve gradiente interno de luz (highlight de 4–6% branco no topo do card) em vez de `box-shadow` tradicional. Reservar sombra apenas para modais e overlays.
